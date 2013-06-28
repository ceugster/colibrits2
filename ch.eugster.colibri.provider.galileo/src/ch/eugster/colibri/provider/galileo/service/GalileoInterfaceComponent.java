package ch.eugster.colibri.provider.galileo.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.queries.TaxCodeMappingQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.galserve.ArticleServerCom4j;
import ch.eugster.colibri.provider.galileo.galserve.IArticleServer;
import ch.eugster.colibri.provider.galileo.kundenserver.CustomerServer;
import ch.eugster.colibri.provider.galileo.service.GalileoConfiguratorComponent.GalileoTaxCode;
import ch.eugster.colibri.provider.service.ProviderInterface;

public class GalileoInterfaceComponent implements ProviderInterface
{
	private LogService logService;

	private EventAdmin eventAdmin;

	private ComponentContext context;

	private GalileoConfiguration configuration;

	private IArticleServer articleServer;

	private CustomerServer customerServer;

	private boolean showFailoverMessage = true;
	
	private final Collection<BarcodeVerifier> barcodeVerifiers = new ArrayList<BarcodeVerifier>();

	public GalileoInterfaceComponent()
	{
		System.out.println();
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
	public IStatus findAndRead(final Barcode barcode, final Position position)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Suche in Warenbewirtschaftung nach \"" + barcode.getCode() + "\".");
		}
		final IStatus status = this.getArticleServer().findAndRead(barcode, position);
		if ((status.getSeverity() == IStatus.OK) || (status.getSeverity() == IStatus.ERROR))
		{
			if (this.logService != null)
			{
				this.logService.log(LogService.LOG_INFO, "Suche nach \"" + barcode.getCode() + "\" abgeschlossen.");
			}
			if (this.eventAdmin != null)
			{
				this.eventAdmin.sendEvent(this.getEvent(status));
			}
		}
		return status;
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
		return GalileoConfiguration.Property.asMap();
	}

	@Override
	public String getProviderId()
	{
		return this.configuration.getProviderId();
	}

	@Override
	public IStatus selectCustomer(final Position position, ProductGroup productGroup)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Starte Kundensuche...");
		}
		IStatus status = Status.OK_STATUS;
		try
		{
			status = this.getCustomerServer().selectCustomer(position, productGroup);
		}
		finally
		{
			if (this.logService != null)
			{
				this.logService.log(LogService.LOG_INFO, position.getReceipt().getCustomer() == null ? "Kundensuche abgebrochen." : "Kunden ausgew�hlt: " + position.getReceipt().getCustomerCode() + " - " + position.getReceipt().getCustomer().getFullname() + ".");
			}
			if (this.eventAdmin != null)
			{
				this.eventAdmin.sendEvent(this.getEvent(status));
			}
		}
		return status;
	}

	@Override
	public IStatus updateProvider(final Position position)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Aktualisiere Warenbewirtschaftung...");
		}
		/*
		 * First check if position state is valid (e.g. if
		 * Position.searchValue() is a barcode Position.Product must exist
		 */
		IStatus status = this.checkPosition(position);
		if (status.getSeverity() == IStatus.OK)
		{
			status = this.getArticleServer().updateProvider(position);
		}
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, (status.getSeverity() == IStatus.OK ? "Warenbewirtschaftung erfolgreich aktualisiert." : "Aktualisierung fehlgeschlagen."));
		}
		if ((status.getSeverity() == IStatus.OK) || (status.getSeverity() == IStatus.ERROR))
		{
			if (this.eventAdmin != null)
			{
				this.eventAdmin.sendEvent(this.getEvent(status));
			}
		}
		return status;
	}

	@Override
	public IStatus checkConnection(Map<String, IProperty> properties)
	{
		IProperty property = properties.get(GalileoConfiguration.Property.DATABASE_PATH.key());
		return this.getArticleServer().checkConnection(property.value());
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;

		this.configuration = new GalileoConfiguration();

		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.context.getProperties().get("component.name") + " aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		this.stopArticleServer();
		this.stopCustomerServer();

		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + this.context.getProperties().get("component.name")
					+ " deaktiviert.");
		}

		this.context = null;
	}

	protected void setBarcodeVerifier(final BarcodeVerifier barcodeVerifier)
	{
		this.barcodeVerifiers.add(barcodeVerifier);
	}

	protected void setEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void unsetBarcodeVerifier(final BarcodeVerifier barcodeVerifier)
	{
		this.barcodeVerifiers.remove(barcodeVerifier);
	}

	protected void unsetEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	private IStatus checkPosition(final Position position)
	{
		/*
		 * Pr�fen, ob Position.searchValue ein Barcode. Falls ja, muss ein
		 * Product vorhanden sein. Falls das nicht ist, muss es nachtr�glich
		 * erstellt werden.
		 */
		IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID, ProviderInterface.Topic.ARTICLE_UPDATE.topic());
		if (position.getProduct() == null)
		{
			final BarcodeVerifier[] verifiers = this.barcodeVerifiers.toArray(new BarcodeVerifier[0]);
			for (final BarcodeVerifier verifier : verifiers)
			{
				final Barcode barcode = verifier.verify(position.getSearchValue());
				if (barcode instanceof Barcode)
				{
					if (this.logService != null)
					{
						this.logService.log(LogService.LOG_INFO, "Pr�fe Position " + position.getSearchValue() + "...");
					}
					status = this.findAndRead(barcode, position);
					if (this.logService != null)
					{
						this.logService.log(LogService.LOG_INFO, "Position " + (status.getSeverity() == IStatus.OK ? "OK." :  "FEHLER."));
					}
					break;
				}
			}
		}
		return status;
	}

	private IArticleServer getArticleServer()
	{
		this.startArticleServer();
		return this.articleServer;
	}

	private CustomerServer getCustomerServer()
	{
		this.startCustomerServer();
		return this.customerServer;
	}

	private Event getEvent(final IStatus status)
	{
		final String topic = status.getMessage();
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();

		if (status.getException() != null)
		{
			if (this.showFailoverMessage)
			{
				properties.put("show.failover.message", Boolean.valueOf(this.showFailoverMessage));
				this.showFailoverMessage = false;
			}
		}
		else
		{
			if (!this.showFailoverMessage)
			{
				this.showFailoverMessage = true;
			}
		}

		properties.put(EventConstants.EVENT_TOPIC, topic);
		properties.put(EventConstants.BUNDLE_ID, Activator.PLUGIN_ID);
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		if (status.getException() != null)
		{
			properties.put(EventConstants.EXCEPTION, status.getException());
			properties.put(EventConstants.EXCEPTION_MESSAGE, status.getException().getMessage() == null ? "" : status.getException().getMessage());
			properties.put("message", "Die Verbindung zur Warenbewirtschaftung konnte nicht hergestellt werden. Die Daten m�ssen manuell erfasst werden.");
		}
		properties.put("status", status);
		for (ProviderInterface.Topic t : ProviderInterface.Topic.values())
		{
			if (t.topic().equals(status.getMessage()))
			{
				properties.put("topic", t);
				break;
			}
		}
		final Event event = new Event(topic, properties);
		return event;
	}

	private void startArticleServer()
	{
		if (this.articleServer == null)
		{
			this.articleServer = new ArticleServerCom4j();
		}
	}

	private void startCustomerServer()
	{
		if (this.customerServer == null)
		{
			this.customerServer = new CustomerServer();
			this.customerServer.start();
		}
	}

	private void stopArticleServer()
	{
		if (this.articleServer != null)
		{
			this.articleServer.stop();
			this.articleServer = null;
		}
	}

	private void stopCustomerServer()
	{
		if (this.customerServer != null)
		{
			this.customerServer.stop();
			this.customerServer = null;
		}
	}

	@Override
	public IStatus checkTaxCodes(PersistenceService service)
	{
		if (service != null)
		{
			TaxCodeMappingQuery query = (TaxCodeMappingQuery) service.getCacheService().getQuery(TaxCodeMapping.class);
			GalileoTaxCode[] taxCodes = GalileoConfiguratorComponent.GalileoTaxCode.values();
			for (GalileoTaxCode taxCode : taxCodes)
			{
				String providerId = this.getProviderId();
				String code = GalileoTaxCode.getCode(taxCode.name());
				TaxCodeMapping mapping = query.selectTaxCodeMappingByProviderAndCode(providerId, code);
				if (mapping == null || mapping.isDeleted())
				{
					return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Mehrwertsteuer f�r " + taxCode.name() + " in " + this.getName() + " nicht gemappt.");
				}
			}
		}
		IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID, "");
		return status;
	}
}
