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
import ch.eugster.colibri.provider.galileo.galserve.FindArticleServerCom4j;
import ch.eugster.colibri.provider.galileo.galserve.IFindArticleServer;
import ch.eugster.colibri.provider.galileo.galserve.IUpdateProviderServer;
import ch.eugster.colibri.provider.galileo.galserve.UpdateProviderServerCom4j;
import ch.eugster.colibri.provider.galileo.kundenserver.CustomerServer;
import ch.eugster.colibri.provider.galileo.service.GalileoConfiguratorComponent.GalileoTaxCode;
import ch.eugster.colibri.provider.service.ProviderInterface;

public class GalileoInterfaceComponent implements ProviderInterface
{
	private LogService logService;

	private EventAdmin eventAdmin;

	private ComponentContext context;

	private GalileoConfiguration configuration;

//	private IArticleServer articleServer;

	private IFindArticleServer findArticleServer;

	private IUpdateProviderServer updateProviderServer;

	private CustomerServer customerServer;

	private boolean showFailoverMessage = true;
	
	private final Collection<BarcodeVerifier> barcodeVerifiers = new ArrayList<BarcodeVerifier>();

	public GalileoInterfaceComponent()
	{
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
	
	public boolean isConnect()
	{
		return this.getFindArticleServer().isConnect();
	}

	@Override
	public IStatus findAndRead(final Barcode barcode, final Position position)
	{
		IStatus status = Status.OK_STATUS;
		log(LogService.LOG_INFO, "Verbindung checken.");
		if (getFindArticleServer().isConnect())
		{
			log(LogService.LOG_INFO, "Suche in Warenbewirtschaftung nach \"" + barcode.getCode() + "\".");
			status = this.getFindArticleServer().findAndRead(barcode, position);
			if ((status.getSeverity() == IStatus.OK) || (status.getSeverity() == IStatus.ERROR))
			{
				log(LogService.LOG_INFO, "Suche nach \"" + barcode.getCode() + "\" abgeschlossen.");
				this.sendEvent(this.getEvent(status, true));
			}
		}
		return status;
	}
	
	private void sendEvent(Event event)
	{
		if (this.eventAdmin != null)
		{
			this.eventAdmin.sendEvent(event);
		}
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
		IStatus status = Status.OK_STATUS;
		if (this.getCustomerServer().isConnect())
		{
			log(LogService.LOG_INFO, "Starte Kundensuche...");
			try
			{
				status = this.getCustomerServer().selectCustomer(position, productGroup);
			}
			finally
			{
				log(LogService.LOG_INFO, position.getReceipt().getCustomer() == null ? "Kundensuche abgebrochen." : "Kunden ausgewählt: " + position.getReceipt().getCustomerCode() + " - " + position.getReceipt().getCustomer().getFullname() + ".");
				this.sendEvent(this.getEvent(status, true));
			}
		}
		return status;
	}

	@Override
	public IStatus updateProvider(final Position position)
	{
		IStatus status = Status.OK_STATUS;
		if (getUpdateProviderServer().isConnect())
		{
			log(LogService.LOG_INFO, "Aktualisiere Warenbewirtschaftung...");
			/*
			 * First check if position state is valid (e.g. if
			 * Position.searchValue() is a barcode Position.Product must exist
			 */
			status = this.checkPosition(position);
			if (status.getSeverity() == IStatus.OK)
			{
				status = this.getUpdateProviderServer().updateProvider(position);
			}
			log(LogService.LOG_INFO, (status.getSeverity() == IStatus.OK ? "Warenbewirtschaftung erfolgreich aktualisiert." : "Aktualisierung fehlgeschlagen."));
			if ((status.getSeverity() == IStatus.OK) || (status.getSeverity() == IStatus.ERROR))
			{
				this.sendEvent(this.getEvent(status, false));
			}
		}
		return status;
	}

	private void log(int severity, String msg)
	{
		if (this.logService != null)
		{
			this.logService.log(severity, msg);
		}
	}
	
	@Override
	public IStatus checkConnection(Map<String, IProperty> properties)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "Die Verbindung zu " + configuration.getName() + " konnte hergestellt werden.");
		if (getFindArticleServer().isConnect())
		{
			IProperty property = properties.get(GalileoConfiguration.Property.DATABASE_PATH.key());
			status = this.getFindArticleServer().checkConnection(property.value());
			if (status.getSeverity() == IStatus.OK)
			{
				if (getUpdateProviderServer().isConnect())
				{
					status = this.getUpdateProviderServer().checkConnection(property.value());
				}
			}
		}
		return status;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;
		this.configuration = new GalileoConfiguration();
		log(LogService.LOG_INFO, "Service " + this.context.getProperties().get("component.name") + " aktiviert.");
	}

	protected void deactivate(final ComponentContext componentContext)
	{
//		this.stopArticleServer();
		this.stopFindArticleServer();
		this.stopUpdateProviderServer();
		this.stopCustomerServer();

		log(LogService.LOG_INFO, "Service " + this.context.getProperties().get("component.name")
					+ " deaktiviert.");
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
		 * Prüfen, ob Position.searchValue ein Barcode. Falls ja, muss ein
		 * Product vorhanden sein. Falls das nicht ist, muss es nachträglich
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
						log(LogService.LOG_INFO, "Prüfe Position " + position.getSearchValue() + "...");
					}
					status = this.findAndRead(barcode, position);
					if (this.logService != null)
					{
						log(LogService.LOG_INFO, "Position " + (status.getSeverity() == IStatus.OK ? "OK." :  "FEHLER."));
					}
					break;
				}
			}
		}
		return status;
	}

//	private IArticleServer getArticleServer()
//	{
//		this.startArticleServer();
//		return this.articleServer;
//	}

	private IFindArticleServer getFindArticleServer()
	{
		this.startFindArticleServer();
		return this.findArticleServer;
	}

	private IUpdateProviderServer getUpdateProviderServer()
	{
		this.startUpdateProviderServer();
		return this.updateProviderServer;
	}

	private CustomerServer getCustomerServer()
	{
		this.startCustomerServer();
		return this.customerServer;
	}

	private Event getEvent(final IStatus status, boolean force)
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
		}
		properties.put("message", "Die Verbindung zu " + configuration.getName() + " konnte nicht hergestellt werden. Die Daten müssen manuell erfasst werden.");
		properties.put("status", status);
		properties.put("force", Boolean.valueOf(force));
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

	private void startUpdateProviderServer()
	{
		if (this.updateProviderServer == null)
		{
			this.updateProviderServer = new UpdateProviderServerCom4j();
			this.updateProviderServer.start();
		}
	}

//	private void startArticleServer()
//	{
//		if (this.articleServer == null)
//		{
//			this.articleServer = new ArticleServerCom4j();
//		}
//	}

	private void startFindArticleServer()
	{
		if (this.findArticleServer == null)
		{
			this.findArticleServer = new FindArticleServerCom4j();
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

//	private void stopArticleServer()
//	{
//		if (this.articleServer != null)
//		{
//			this.articleServer.stop();
//			this.articleServer = null;
//		}
//	}

	private void stopFindArticleServer()
	{
		if (this.findArticleServer != null)
		{
			this.findArticleServer.stop();
			this.findArticleServer = null;
		}
	}

	private void stopUpdateProviderServer()
	{
		if (this.updateProviderServer != null)
		{
			this.updateProviderServer.stop();
			this.updateProviderServer = null;
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
					return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Mehrwertsteuer für " + taxCode.name() + " in " + this.getName() + " nicht gemappt.");
				}
			}
		}
		IStatus status = new Status(IStatus.OK, Activator.PLUGIN_ID, "");
		return status;
	}
}
