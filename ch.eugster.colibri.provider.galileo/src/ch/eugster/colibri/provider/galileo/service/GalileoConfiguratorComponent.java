package ch.eugster.colibri.provider.galileo.service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderTaxCode;
import ch.eugster.colibri.provider.configuration.SchedulerProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.Property;
import ch.eugster.colibri.provider.galileo.wgserve.ClassFactory;
import ch.eugster.colibri.provider.galileo.wgserve.Iwgserve;
import ch.eugster.colibri.provider.service.ProviderConfigurator;

public class GalileoConfiguratorComponent implements ProviderConfigurator
{
	private String database;

	private boolean open;

	private Iwgserve wgserve;

	protected int countInserted = 0;

	protected int countUpdated = 0;

	protected int countNotMapped = 0;

	protected boolean inserted;

	protected boolean updated;

	protected boolean notMapped;

	// protected IStatus status;

	private LogService logService;

	private PersistenceService persistenceService;

	private GalileoConfiguration configuration;

	private Collection<String> codesWithError;

	public GalileoConfiguratorComponent()
	{
		this.configuration = new GalileoConfiguration();
	}

	@Override
	public boolean canMap(final CurrentTax currentTax)
	{
		return this.configuration.canMap(currentTax);
	}

	@Override
	public boolean canMap(final Tax tax)
	{
		return this.configuration.canMap(tax);
	}

	@Override
	public String getImageName()
	{
		return this.configuration.getImageName();
	}

	@Override
	public String getName()
	{
		return this.configuration.getName();
	}

	@Override
	public Map<String, IProperty> getProperties()
	{
		final Map<String, IProperty> properties = new HashMap<String, IProperty>();
		for (final IProperty property : Property.values())
		{
			properties.put(property.key(), property);
		}
		for (final IProperty property : SchedulerProperty.values())
		{
			properties.put(property.key(), property);
		}
		return properties;
	}

	@Override
	public String getProviderId()
	{
		return this.configuration.getProviderId();
	}

	private IStatus importExternalProductGroups(final IProgressMonitor monitor, IStatus status)
	{
		if (status.getSeverity() != IStatus.OK)
		{
			return status;
		}
		try
		{
			final boolean open = ((Boolean) this.wgserve.do_open(this.database)).booleanValue();
			if (!open)
			{
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Die Verbindung zur Warenbewirtschaftung kann nicht hergestellt werden.");
			}

			final String[] codes = this.selectAllCodes();
			monitor.beginTask("Warengruppen importieren", codes.length);
			status = this.update(codes, monitor, status);
			monitor.done();

			this.closeDatabase();
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Beim Anfordern der Warengruppen aus Galileo ist ein Fehler aufgetreten.", e);
		}

		return status;
	}

	@Override
	public IStatus importProductGroups(final IProgressMonitor monitor)
	{
		if (this.persistenceService == null)
		{
			return new Status(
					IStatus.WARNING,
					Activator.PLUGIN_ID,
					"Es besteht keine Verbindung zur Datenbank. Bitte versuchen Sie diesen Vorgang, wenn die Verbindung zur Datenbank wiederhergestellt ist.");
		}
		else
		{
			IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Die Warengruppen wurden erfolgreich importiert.");
			status = GalileoConfiguratorComponent.this.start(status);
			status = GalileoConfiguratorComponent.this.importExternalProductGroups(monitor, status);
			status = GalileoConfiguratorComponent.this.stop(status);
			return status;
		}
	}

	public IStatus start(IStatus status)
	{
		if (status.getSeverity() != IStatus.OK)
		{
			return status;
		}

		this.configuration = new GalileoConfiguration();

		final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle()
				.getBundleContext(), PersistenceService.class, null);
		persistenceServiceTracker.open();

		final PersistenceService persistenceService = (PersistenceService) persistenceServiceTracker.getService();
		if (persistenceService == null)
		{
			return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Die Verbindung zur Datenbank ist nicht hergestellt.");
		}

		final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getServerService().getQuery(
				ProviderProperty.class);
		final Map<String, ProviderProperty> properties = query.selectByProviderAsMap(
				this.configuration.getProviderId(), this.configuration.getDefaultPropertiesAsMap());

		this.database = properties.get(Property.DATABASE_PATH.key()).getValue();

		try
		{
			this.wgserve = ClassFactory.createwgserve();
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					" Die Verbindung zur Warenbewirtschaftung konnte nicht hergestellt werden.", e);
			e.printStackTrace();
		}
		return status;
	}

	public IStatus stop(final IStatus status)
	{
		if (wgserve != null)
		{
			this.wgserve.dispose();
		}
		return status;
	}

	private IStatus synchronizeExternalProductGroups(final IProgressMonitor monitor, IStatus status)
	{
		if (status.getSeverity() != IStatus.OK)
		{
			return status;
		}

		try
		{
			final boolean open = this.openDatabase(this.database);
			if (!open)
			{
				return new Status(IStatus.ERROR, Activator.PLUGIN_ID,
						"Die Verbindung zur Warenbewirtschaftung kann nicht hergestellt werden.");
			}

			final String[] codes = this.selectChangedCodes();
			monitor.beginTask("Warengruppen synchronisieren", codes.length);
			status = this.update(codes, monitor, status);
			monitor.done();

			this.closeDatabase();
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					"Beim Anfordern der Warengruppen aus Galileo ist ein Fehler aufgetreten.", e);
		}

		return status;
	}

	@Override
	public IStatus synchronizeProductGroups(final IProgressMonitor monitor)
	{
		if (this.persistenceService == null)
		{
			return new Status(
					IStatus.WARNING,
					"ch.eugster.colibri.provider.galileo.jcom",
					"Es besteht keine Verbindung zur Datenbank. Bitte versuchen Sie diesen Vorgang, nachdem die Verbindung zur Datenbank wiederhergestellt ist. ");
		}
		else
		{
			IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Die Warengruppen wurden erfolgreich synchronisiert.");
			status = GalileoConfiguratorComponent.this.start(status);
			status = GalileoConfiguratorComponent.this.synchronizeExternalProductGroups(monitor, status);
			status = GalileoConfiguratorComponent.this.stop(status);
			return status;
		}
	}

	protected void activate(final ComponentContext componentContext)
	{
		if (logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name") + " aktiviert.");
		}
	}

	protected void confirmChanges(final String code)
	{
		this.wgserve.do_setbestaetigt(code);
	}

	protected GalileoProductGroup createGalileoProductGroup(final String code)
	{
		final GalileoProductGroup group = new GalileoProductGroup();
		group.setCode(code);
		group.setText((String) this.wgserve.wgtext());
		group.setAccount((String) this.wgserve.konto());
		group.setBox1((String) this.wgserve.boX1());
		group.setBox2((String) this.wgserve.boX2());
		group.setDescBox1((String) this.wgserve.descboX1());
		group.setDescBox2((String) this.wgserve.descboX2());
		return group;
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		if (logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name")
					+ " deaktiviert.");
		}
	}

	protected ExternalProductGroup getExternalProductGroup(final GalileoProductGroup group)
	{
		ExternalProductGroup externalProductGroup = null;

		if (this.persistenceService != null)
		{
			final ExternalProductGroupQuery query = (ExternalProductGroupQuery) this.persistenceService
					.getServerService().getQuery(ExternalProductGroup.class);
			externalProductGroup = query.selectByProviderAndCode(this.configuration.getProviderId(), group.getCode());
			if (externalProductGroup == null)
			{
				this.notMapped = true;
				this.inserted = true;
				externalProductGroup = ExternalProductGroup.newInstance(this.configuration.getProviderId());
				final ProductGroup productGroup = this.getProductGroup(group);
				if (productGroup != null)
				{
					final ProductGroupMapping productGroupMapping = ProductGroupMapping.newInstance(productGroup,
							externalProductGroup);
					externalProductGroup.setProductGroupMapping(productGroupMapping);
					Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(this.configuration.getProviderId());
					for (ProductGroupMapping mapping : mappings)
					{
						if (!mapping.isDeleted())
						{
							productGroup.addProductGroupMapping(productGroupMapping);
							this.notMapped = false;
						}
					}
				}
			}
			else
			{
				this.updated = true;
				if (externalProductGroup.isDeleted())
				{
					externalProductGroup.setDeleted(false);
				}
				if (externalProductGroup.getProductGroupMapping() != null)
				{
					if (externalProductGroup.getProductGroupMapping().isDeleted())
					{
						externalProductGroup.getProductGroupMapping().setDeleted(false);
					}
					if (externalProductGroup.getProductGroupMapping().getProductGroup().isDeleted())
					{
						externalProductGroup.getProductGroupMapping().getProductGroup().setDeleted(false);
					}
				}
			}
			externalProductGroup.setCode(group.getCode());
			externalProductGroup.setText(group.getText());
			externalProductGroup.setAccount(group.getAccount());
		}
		return externalProductGroup;
	}

	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	protected void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	protected IStatus update(final String[] codes, final IProgressMonitor monitor, IStatus status)
	{
		this.countInserted = 0;
		this.countUpdated = 0;
		this.countNotMapped = 0;
		this.codesWithError = new ArrayList<String>();

		if (codes.length == 0)
		{
			status = new Status(IStatus.OK, Activator.PLUGIN_ID,
					"Es ist keine Aktualisierung der Warengruppen notwendig (Es wurden keine zu aktualisierenden Warengruppen gefunden).");
		}
		else
		{
			for (final String code : codes)
			{
				this.inserted = false;
				this.updated = false;
				this.notMapped = false;

				if (code.length() > 0)
				{
					if (wgserve.do_getwg(code).equals(Boolean.TRUE))
					{
						final GalileoProductGroup group = this.createGalileoProductGroup(code);
						ExternalProductGroup externalProductGroup = this.getExternalProductGroup(group);

						if (this.persistenceService != null)
						{
							externalProductGroup = (ExternalProductGroup) GalileoConfiguratorComponent.this.persistenceService.getServerService().merge(
									externalProductGroup);

							if (GalileoConfiguratorComponent.this.inserted)
							{
								GalileoConfiguratorComponent.this.countInserted++;
							}
							if (GalileoConfiguratorComponent.this.updated)
							{
								GalileoConfiguratorComponent.this.countUpdated++;
							}
							if (GalileoConfiguratorComponent.this.notMapped)
							{
								GalileoConfiguratorComponent.this.countNotMapped++;
							}
							if (GalileoConfiguratorComponent.this.inserted || GalileoConfiguratorComponent.this.updated  || GalileoConfiguratorComponent.this.notMapped)
							{
								GalileoConfiguratorComponent.this.confirmChanges(code);
								GalileoConfiguratorComponent.this.inserted = false;
								GalileoConfiguratorComponent.this.updated= false;
								GalileoConfiguratorComponent.this.notMapped = false;
							}
							else
							{
								GalileoConfiguratorComponent.this.codesWithError.add(code);
							}
						}
					}
				}
				monitor.worked(1);
			}
			final NumberFormat formatter = NumberFormat.getIntegerInstance();

			if (this.codesWithError.size() == 0)
			{
				if (this.countNotMapped == 0)
				{
					StringBuilder msg = new StringBuilder(
							"Alle Warengruppen wurden erfolgreich aktualisiert und zugeordnet:");
					msg = msg.append("\n");
					msg = msg.append("\nEingefügte Warengruppen: " + formatter.format(this.countInserted));
					msg = msg.append("\nAktualisierte Warengruppen: " + formatter.format(this.countUpdated));
					status = new Status(IStatus.OK, Activator.PLUGIN_ID, msg.toString());
				}
				else
				{
					StringBuilder msg = new StringBuilder(
							"Einige Warengruppen müssen von Ihnen manuell zugeordnet werden:");
					msg = msg.append("\n");
					msg = msg.append("\nEingefügte Warengruppen: " + formatter.format(this.countInserted));
					msg = msg.append("\nAktualisierte Warengruppen: " + formatter.format(this.countUpdated));
					msg = msg.append("\nManuell zuzuordnen: " + formatter.format(this.countNotMapped) + " !");
					status = new Status(IStatus.OK, Activator.PLUGIN_ID, msg.toString());
				}
			}
			else
			{
				StringBuilder msg = new StringBuilder("Einige Warengruppen konnten nicht aktualisiert werden:");
				msg = msg.append("\n");
				msg = msg.append("\nEingefügte Warengruppen: " + formatter.format(this.countInserted));
				msg = msg.append("\nAktualisierte Warengruppen: " + formatter.format(this.countUpdated));
				msg = msg.append("\nManuell zuzuordnen: " + formatter.format(this.countNotMapped));
				msg = msg.append("\nFolgende Warengruppen konnten nicht aktualisiert werden:\n");
				final Iterator<String> iterator = this.codesWithError.iterator();
				while (iterator.hasNext())
				{
					msg = msg.append(iterator.next());
					if (iterator.hasNext())
					{
						msg = msg.append(", ");
					}
				}
				status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg.toString());
			}
		}
		return status;
	}

	private void closeDatabase()
	{
		if (this.open)
		{
			this.open = ((Boolean) this.wgserve.do_close()).booleanValue();
		}
	}

	private ProductGroup getProductGroup(final GalileoProductGroup group)
	{
		ProductGroup productGroup = null;
		if (this.persistenceService != null)
		{
			final CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) this.persistenceService
					.getServerService().getQuery(CommonSettings.class);
			final CommonSettings commonSettings = commonSettingsQuery.findDefault();

			final ProductGroupQuery query = (ProductGroupQuery) this.persistenceService.getServerService().getQuery(
					ProductGroup.class);
			productGroup = query.findByCode(group.getCode());
			if (productGroup == null)
			{
				productGroup = ProductGroup.newInstance(ProductGroupType.SALES_RELATED, commonSettings);
				final Tax tax = (Tax) this.persistenceService.getServerService().find(Tax.class, Long.valueOf(4l));
				productGroup.setAccount(group.getAccount());
				productGroup.setCode((group.getText() == null) || group.getText().isEmpty() ? group.getCode() : group
						.getText());
				productGroup.setDefaultTax(tax);
				productGroup.setMappingId(group.getCode());
				productGroup.setName((group.getText() == null) || group.getText().isEmpty() ? group.getCode() : group
						.getText());
				productGroup.setPriceProposal(0D);
				productGroup.setProposalOption(Position.Option.ARTICLE);
				productGroup.setQuantityProposal(1);
			}
			else
			{
				Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(this.configuration.getProviderId());
				for (ProductGroupMapping mapping : mappings)
				{
					if (!mapping.isDeleted())
					{
						productGroup = null;
					}
				}
			}
		}
		return productGroup;
	}

	private boolean openDatabase(final String database)
	{
		if (!this.open)
		{
			this.open = ((Boolean) this.wgserve.do_open(this.database)).booleanValue();
		}
		return this.open;
	}

	private String[] readCodes()
	{
		final String codeList = (String) this.wgserve.wglist();
		if (codeList.length() > 0)
		{
			return codeList.split("[|]");
		}
		else
		{
			return new String[0];
		}
	}

	private String[] selectAllCodes()
	{
		if (((Boolean) this.wgserve.do_getwglist()).booleanValue())
		{
			return this.readCodes();
		}
		return new String[0];
	}

	private String[] selectChangedCodes()
	{
		if (((Boolean) this.wgserve.do_getchangedwglist()).booleanValue())
		{
			return this.readCodes();
		}
		return new String[0];
	}

	public class GalileoProductGroup
	{
		public static final String CODE = "code";

		public static final String PROPERTY_TEXT = "WGTEXT";

		public static final String PROPERTY_ACCOUNT = "KONTO";

		public static final String PROPERTY_BOX_1 = "BOX1";

		public static final String PROPERTY_BOX_2 = "BOX2";

		public static final String PROPERTY_DESC_BOX_1 = "DESCBOX1";

		public static final String PROPERTY_DESC_BOX_2 = "DESCBOX2";

		private String code;

		private String text;

		private String account;

		private String box1;

		private String box2;

		private String descBox1;

		private String descBox2;

		public String getAccount()
		{
			return this.account;
		}

		public String getBox1()
		{
			return this.box1;
		}

		public String getBox2()
		{
			return this.box2;
		}

		public String getCode()
		{
			return this.code;
		}

		public String getDescBox1()
		{
			return this.descBox1;
		}

		public String getDescBox2()
		{
			return this.descBox2;
		}

		public String getText()
		{
			return this.text;
		}

		public void setAccount(final String account)
		{
			this.account = account;
		}

		public void setBox1(final String box1)
		{
			this.box1 = box1;
		}

		public void setBox2(final String box2)
		{
			this.box2 = box2;
		}

		public void setCode(final String code)
		{
			this.code = code;
		}

		public void setDescBox1(final String descBox1)
		{
			this.descBox1 = descBox1;
		}

		public void setDescBox2(final String descBox2)
		{
			this.descBox2 = descBox2;
		}

		public void setText(final String text)
		{
			this.text = text;
		}
	}

	@Override
	public IStatus setTaxCodes(IProgressMonitor monitor) 
	{
		IStatus status = Status.OK_STATUS;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		PersistenceService service = tracker.getService();
		if (service != null)
		{
			int count = 0;
			int newCodes = 0;
			TaxTypeQuery typeQuery = (TaxTypeQuery) service.getServerService().getQuery(TaxType.class);
			TaxType taxType = typeQuery.selectByCode("U");
			Collection<Tax> taxes = taxType.getTaxes();
			for (Tax tax : taxes)
			{
				TaxCodeMapping mapping = tax.getTaxCodeMapping(this.getProviderId());
				if (mapping == null)
				{
					String code = GalileoTaxCode.getCode(tax.getTaxRate().getCode());
					mapping = TaxCodeMapping.newInstance(tax);
					mapping.setCode(code);
					mapping.setProvider(this.getProviderId());
					mapping.setTax(tax);
					tax.addTaxCodeMapping(mapping);
					service.getServerService().merge(mapping.getTax());
					newCodes++;
				}
				count++;
			}
			status = new Status(IStatus.OK, Activator.PLUGIN_ID, "Die Zuordnung ist abgeschlossen. Es wurden " + count + " Steuersätze geprüft. Es " + (newCodes == 0 ? "mussten keine neuen Codes hinzugefügt werden." : (newCodes == 1 ? "wurde ein Code zugeordnet." : "wurden " + newCodes + " zugeordnet.")));
		}
		else
		{
			status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Die Zuordnung konnte nicht durchgeführt werden, da keine Verbindung zur Datenbank hergestellt werden konnte.");
		}
		return status;
	}

	public static ProviderTaxCode[] getProviderTaxCodes() 
	{
		return GalileoTaxCode.values();
	}

	public enum GalileoTaxCode implements ProviderTaxCode
	{
		F, R, N;
		
		public static String getCode(String rate)
		{
			if (rate.equals("F"))
			{
				return "0";
			}
			else if (rate.equals("R"))
			{
				return "1";
			}
			else if (rate.equals("N"))
			{
				return "2";
			}
			else
			{
				return "";
			}
		}
	}
}
