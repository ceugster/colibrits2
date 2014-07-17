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

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderTaxCode;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;
import ch.eugster.colibri.provider.galileo.wgserve.GalileoProductGroup;
import ch.eugster.colibri.provider.galileo.wgserve.WgserveProxy;
import ch.eugster.colibri.provider.galileo.wgserve.old.WgserveOldProxy;
import ch.eugster.colibri.provider.galileo.wgserve.sql.WgserveSqlProxy;
import ch.eugster.colibri.provider.service.AbstractProviderService;
import ch.eugster.colibri.provider.service.ProviderConfigurator;

public class GalileoConfiguratorComponent extends AbstractProviderService implements ProviderConfigurator
{
	private WgserveProxy wgserveProxy;

	protected int countInserted = 0;

	protected int countUpdated = 0;

	protected int countNotMapped = 0;

	protected boolean inserted;

	protected boolean updated;

	protected boolean notMapped;

	private Collection<String> codesWithError;

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

	public Map<String, IProperty> getDefaultProperties()
	{
		return GalileoConfiguration.GalileoProperty.asMap();
	}
	
	public boolean isConnect()
	{
		this.loadProperties(persistenceService.getServerService(), Activator.getDefault().getConfiguration().getProviderId(), GalileoConfiguration.GalileoProperty.asMap());
		IProperty property = properties.get(GalileoProperty.CONNECT.key());
		return Integer.valueOf(property.value()).intValue() > 0;
	}

	private IStatus importExternalProductGroups(final IProgressMonitor monitor, IStatus status)
	{
		if (status.getSeverity() != IStatus.OK)
		{
			return status;
		}
		try
		{
			IProperty property = properties.get(GalileoProperty.DATABASE_PATH.key());
			String database = property.value();
			status = this.wgserveProxy.openDatabase(database);
			if (status.getSeverity() == IStatus.OK)
			{
				final String[] codes = this.wgserveProxy.selectAllCodes();
				monitor.beginTask("Warengruppen importieren", codes.length);
				status = this.update(codes, monitor, status);
				monitor.done();
			}
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Anfordern der Warengruppen aus Galileo ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			this.wgserveProxy.closeDatabase();
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
			status = start(status);
			if (status.getSeverity() == IStatus.OK && this.isConnect())
			{
				status = this.importExternalProductGroups(monitor, status);
				status = stop(status);
			}
		}
		return status;
	}

	private IStatus start(IStatus status)
	{
		if (status.getSeverity() != IStatus.OK)
		{
			return status;
		}

		try
		{
			if (this.isConnect())
			{
				IProperty property = properties.get(GalileoProperty.CONNECT.key());
				int connect = Integer.valueOf(property.value()).intValue();
				if (connect == 1 || connect == 2)
				{
					if (connect == 1)
					{
						this.wgserveProxy = new WgserveOldProxy();
					}
					else if (connect == 2)
					{
						this.wgserveProxy = new WgserveSqlProxy();
					}
				}
			}
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					Topic.SCHEDULED_PROVIDER_UPDATE.topic(), e);
			e.printStackTrace();
		}
		return status;
	}

	private IStatus stop(final IStatus status)
	{
		if (this.wgserveProxy != null)
		{
			this.wgserveProxy.dispose();
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
			IProperty property = properties.get(GalileoProperty.DATABASE_PATH.key());
			String database = property.value();
			status = this.wgserveProxy.openDatabase(database);
			if (status.isOK())
			{
				final String[] codes = this.wgserveProxy.selectChangedCodes();
				monitor.beginTask("Warengruppen synchronisieren", codes.length);
				status = this.update(codes, monitor, status);
				monitor.done();
			}
		}
		catch (final Exception e)
		{
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Beim Anfordern der Warengruppen aus Galileo ist ein Fehler aufgetreten.", e);
		}
		finally
		{
			this.wgserveProxy.closeDatabase();
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
			status = start(status);
			if (status.getSeverity() == IStatus.OK && isConnect())
			{
				status = this.synchronizeExternalProductGroups(monitor, status);
				status = stop(status);
			}
		}
		return status;
	}

	protected void activate(final ComponentContext componentContext)
	{
		super.activate(componentContext);
		this.loadProperties(persistenceService.getServerService(), Activator.getDefault().getConfiguration().getProviderId(), GalileoConfiguration.GalileoProperty.asMap());
		log(LogService.LOG_DEBUG, "Service " + this.context.getProperties().get("component.name") + " aktiviert.");
	}

	protected void confirmChanges(final String code)
	{
		this.wgserveProxy.confirmChanges(code);
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		if (logService != null)
		{
			this.logService.log(LogService.LOG_DEBUG, "Service " + componentContext.getProperties().get("component.name")
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
					if (this.wgserveProxy.getWg(code))
					{
						final GalileoProductGroup group = this.wgserveProxy.createGalileoProductGroup(code);
						ExternalProductGroup externalProductGroup = this.getExternalProductGroup(group);

						if (this.persistenceService != null)
						{
							try
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
							catch (Exception e)
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
	
	public String getProviderId()
	{
		return Activator.getDefault().getConfiguration().getProviderId();
	}

	@Override
	public IStatus setTaxCodes(IProgressMonitor monitor) 
	{
		IStatus status = null;
		if (persistenceService != null)
		{
			int count = 0;
			int newCodes = 0;
			TaxTypeQuery typeQuery = (TaxTypeQuery) persistenceService.getServerService().getQuery(TaxType.class);
			TaxType taxType = typeQuery.selectByCode("U");
			Collection<Tax> taxes = taxType.getTaxes();
			for (Tax tax : taxes)
			{
				TaxCodeMapping mapping = tax.getTaxCodeMapping(Activator.getDefault().getConfiguration().getProviderId());
				if (mapping == null)
				{
					String code = GalileoTaxCode.getCode(tax.getTaxRate().getCode());
					mapping = TaxCodeMapping.newInstance(tax);
					mapping.setCode(code);
					mapping.setProvider(Activator.getDefault().getConfiguration().getProviderId());
					mapping.setTax(tax);
					tax.addTaxCodeMapping(mapping);
					try
					{
						persistenceService.getServerService().merge(mapping.getTax());
						newCodes++;
					}
					catch (Exception e)
					{
						status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), "Einige Zuordnungen konnten nicht durchgeführt werden, versuchen Sie es nach einem Neustart des Programms noch einmal.", e);
					}
				}
				count++;
			}
			if (status == null)
			{
				status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "Die Zuordnung ist abgeschlossen. Es wurden " + count + " Steuersätze geprüft. Es " + (newCodes == 0 ? "mussten keine neuen Codes hinzugefügt werden." : (newCodes == 1 ? "wurde ein Code zugeordnet." : "wurden " + newCodes + " zugeordnet.")));
			}
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
