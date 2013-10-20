package ch.eugster.colibri.provider.galileo.service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
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
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderTaxCode;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.wgserve.ClassFactory;
import ch.eugster.colibri.provider.galileo.wgserve.Iwgserve;
import ch.eugster.colibri.provider.service.ProviderConfigurator;

public class GalileoConfiguratorComponent implements ProviderConfigurator
{
	private String database;

	private boolean connect;
	
	private boolean open;

	private Iwgserve wgserve;

	protected int countInserted = 0;

	protected int countUpdated = 0;

	protected int countNotMapped = 0;

	protected boolean inserted;

	protected boolean updated;

	protected boolean notMapped;

	protected IStatus status;

	private LogService logService;

	private PersistenceService persistenceService;

	private Collection<String> codesWithError;

	private Map<String, IProperty> properties;
	
	public GalileoConfiguratorComponent()
	{
	}

	@Override
	public boolean canMap(final CurrentTax currentTax)
	{
		return Activator.getDefault().getConfiguration().canMap(currentTax);
	}

	@Override
	public boolean canMap(final Tax tax)
	{
		return Activator.getDefault().getConfiguration().canMap(tax);
	}

	@Override
	public String getImageName()
	{
		return Activator.getDefault().getConfiguration().getImageName();
	}

	@Override
	public String getName()
	{
		return Activator.getDefault().getConfiguration().getName();
	}

	@Override
	public Map<String, IProperty> getProperties()
	{
		if (this.properties == null)
		{
			updateProperties();
		}
		return this.properties;
	}
	
	public Map<String, IProperty> getDefaultProperties()
	{
		return GalileoConfiguration.GalileoProperty.asMap();
	}

	@Override
	public String getProviderId()
	{
		return Activator.getDefault().getConfiguration().getProviderId();
	}
	
	public boolean isConnect()
	{
		this.updateProperties();
		return connect;
	}

	private IStatus importExternalProductGroups(final IProgressMonitor monitor, IStatus status)
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
				return new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
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
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Anfordern der Warengruppen aus Galileo ist ein Fehler aufgetreten.", e);
		}

		return status;
	}

	@Override
	public IStatus importProductGroups(final IProgressMonitor monitor)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
				"Die Warengruppen wurden erfolgreich importiert.");
		if (this.persistenceService == null)
		{
			return new Status(
					IStatus.WARNING,
					Activator.getDefault().getBundle().getSymbolicName(),
					"Es besteht keine Verbindung zur Datenbank. Bitte versuchen Sie diesen Vorgang, wenn die Verbindung zur Datenbank wiederhergestellt ist.");
		}
		else
		{
			updateProperties();
			if (this.status.getSeverity() == IStatus.OK && this.isConnect())
			{
				status = this.importExternalProductGroups(monitor, status);
			}
		}
		return status;
	}

	private void updateProperties()
	{
		final ServiceTracker<PersistenceService, PersistenceService> serviceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		serviceTracker.open();
		try
		{
			Map<String, IProperty> properties = GalileoConfiguration.GalileoProperty.asMap();
			final PersistenceService persistenceService = (PersistenceService) serviceTracker.getService();
			if (persistenceService != null)
			{
				final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getCacheService()
						.getQuery(ProviderProperty.class);
				Map<String, ProviderProperty>  providerProperties = query.selectByProviderAsMap(Activator.getDefault().getConfiguration().getProviderId());
				for (final ProviderProperty providerProperty : providerProperties.values())
				{
					IProperty property = properties.get(providerProperty.getKey());
					property.setPersistedProperty(providerProperty);
				}
				final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getCacheService().getQuery(
						Salespoint.class);
				Salespoint salespoint = salespointQuery.getCurrentSalespoint();
				if (salespoint != null)
				{
					providerProperties = query.selectByProviderAndSalespointAsMap(Activator.getDefault().getConfiguration().getProviderId(), salespoint);
					for (final ProviderProperty providerProperty : providerProperties.values())
					{
						IProperty property = properties.get(providerProperty.getKey());
						property.setPersistedProperty(providerProperty);
					}
				}
				this.database = properties.get(GalileoProperty.DATABASE_PATH.key()).value();
				this.connect = Boolean.valueOf(properties.get(GalileoProperty.CONNECT.key()).value()).booleanValue();
			}
		}
		finally
		{
			serviceTracker.close();
		}
	}
	
	private IStatus start(IStatus status)
	{
		if (status.getSeverity() != IStatus.OK)
		{
			return status;
		}

		try
		{
			this.wgserve = ClassFactory.createwgserve();
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					" Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " konnte nicht hergestellt werden.", e);
			e.printStackTrace();
		}
		return status;
	}

	private IStatus stop(final IStatus status)
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
				return new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
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
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Anfordern der Warengruppen aus Galileo ist ein Fehler aufgetreten.", e);
		}

		return status;
	}

	@Override
	public IStatus synchronizeProductGroups(final IProgressMonitor monitor)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
				"Die Warengruppen wurden erfolgreich synchronisiert.");
		if (this.persistenceService == null)
		{
			return new Status(
					IStatus.WARNING,
					Activator.getDefault().getBundle().getSymbolicName(),
					"Es besteht keine Verbindung zur Datenbank. Bitte versuchen Sie diesen Vorgang, wenn die Verbindung zur Datenbank wiederhergestellt ist.");
		}
		else
		{
			updateProperties();
			if (status.getSeverity() == IStatus.OK && this.isConnect())
			{
				status = this.synchronizeExternalProductGroups(monitor, status);
			}
		}
		return status;
	}

	protected void activate(final ComponentContext componentContext)
	{
		if (logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name") + " aktiviert.");
		}
		this.status = this.start(Status.OK_STATUS);
		System.out.println(this.status);
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
		this.stop(status);
	}

	protected ExternalProductGroup getExternalProductGroup(final GalileoProductGroup group)
	{
		ExternalProductGroup externalProductGroup = null;

		if (this.persistenceService != null)
		{
			final ExternalProductGroupQuery query = (ExternalProductGroupQuery) this.persistenceService
					.getServerService().getQuery(ExternalProductGroup.class);
			externalProductGroup = query.selectByProviderAndCode(Activator.getDefault().getConfiguration().getProviderId(), group.getCode());
			if (externalProductGroup == null)
			{
				this.notMapped = true;
				this.inserted = true;
				externalProductGroup = ExternalProductGroup.newInstance(Activator.getDefault().getConfiguration().getProviderId());
				final ProductGroup productGroup = this.getProductGroup(group);
				if (productGroup != null)
				{
					final ProductGroupMapping productGroupMapping = ProductGroupMapping.newInstance(productGroup,
							externalProductGroup);
					externalProductGroup.setProductGroupMapping(productGroupMapping);
					Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(Activator.getDefault().getConfiguration().getProviderId());
					for (ProductGroupMapping mapping : mappings)
					{
						if (!mapping.isDeleted())
						{
							mapping.setDeleted(true);
						}
					}
					productGroup.addProductGroupMapping(productGroupMapping);
					this.notMapped = false;
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
			status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(),
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
					status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), msg.toString());
				}
				else
				{
					StringBuilder msg = new StringBuilder(
							"Einige Warengruppen müssen von Ihnen manuell zugeordnet werden:");
					msg = msg.append("\n");
					msg = msg.append("\nEingefügte Warengruppen: " + formatter.format(this.countInserted));
					msg = msg.append("\nAktualisierte Warengruppen: " + formatter.format(this.countUpdated));
					msg = msg.append("\nManuell zuzuordnen: " + formatter.format(this.countNotMapped) + " !");
					status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), msg.toString());
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
				status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), msg.toString());
			}
		}
		return status;
	}

	private void closeDatabase()
	{
		if (this.open)
		{
			this.open = !((Boolean) this.wgserve.do_close()).booleanValue();
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
				Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(Activator.getDefault().getConfiguration().getProviderId());
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
		if (persistenceService != null)
		{
			int count = 0;
			int newCodes = 0;
			TaxTypeQuery typeQuery = (TaxTypeQuery) persistenceService.getServerService().getQuery(TaxType.class);
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
					persistenceService.getServerService().merge(mapping.getTax());
					newCodes++;
				}
				count++;
			}
			status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "Die Zuordnung ist abgeschlossen. Es wurden " + count + " Steuersätze geprüft. Es " + (newCodes == 0 ? "mussten keine neuen Codes hinzugefügt werden." : (newCodes == 1 ? "wurde ein Code zugeordnet." : "wurden " + newCodes + " zugeordnet.")));
		}
		else
		{
			status = new Status(IStatus.CANCEL, Activator.getDefault().getBundle().getSymbolicName(), "Die Zuordnung konnte nicht durchgeführt werden, da keine Verbindung zur Datenbank hergestellt werden konnte.");
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
