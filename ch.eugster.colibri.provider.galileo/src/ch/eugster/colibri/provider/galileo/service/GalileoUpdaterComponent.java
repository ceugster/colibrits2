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
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.service.ConnectionService.ConnectionType;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoSection;
import ch.eugster.colibri.provider.galileo.galserve.IUpdateProviderServer;
import ch.eugster.colibri.provider.galileo.galserve.UpdateProviderServerCom4j;
import ch.eugster.colibri.provider.service.AbstractProviderUpdater;
import ch.eugster.colibri.provider.service.ProviderQuery;
import ch.eugster.colibri.provider.service.ProviderService;

public class GalileoUpdaterComponent extends AbstractProviderUpdater
{
	private LogService logService;

	private EventAdmin eventAdmin;

	private ProviderQuery providerQuery;

	private IUpdateProviderServer updateProviderServer;

	private boolean showFailoverMessage = true;
	
	private final Collection<BarcodeVerifier> barcodeVerifiers = new ArrayList<BarcodeVerifier>();

	public GalileoUpdaterComponent()
	{
	}

	private void sendEvent(Event event)
	{
		if (this.eventAdmin != null)
		{
			this.eventAdmin.sendEvent(event);
		}
	}

	@Override
	public String getName()
	{
		return Activator.getDefault().getConfiguration().getName();
	}

	@Override
	public Map<String, IProperty> getProperties()
	{
		return GalileoConfiguration.GalileoProperty.asMap();
	}

	@Override
	public String getProviderId()
	{
		return Activator.getDefault().getConfiguration().getProviderId();
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
	public IStatus checkConnection()
	{
		return checkConnection(this.getProperties());
	}

	@Override
	public IStatus checkConnection(Map<String, IProperty> properties)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), "Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " wurde erfolgreich hergestellt.");
		if (getUpdateProviderServer().isConnect())
		{
			IProperty property = properties.get(GalileoConfiguration.GalileoProperty.DATABASE_PATH.key());
			status = this.getUpdateProviderServer().checkConnection(property.value());
		}
		return status;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;
		log(LogService.LOG_INFO, "Service " + this.context.getProperties().get("component.name") + " aktiviert.");
	}

	protected void deactivate(final ComponentContext componentContext)
	{
//		this.stopArticleServer();
//		this.stopFindArticleServer();
		this.stopUpdateProviderServer();
//		this.stopCustomerServer();

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

	protected void setProviderQuery(ProviderQuery providerQuery)
	{
		this.providerQuery = providerQuery;
	}
	
	protected void unsetProviderQuery(ProviderQuery providerQuery)
	{
		this.providerQuery = null;
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
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), ProviderService.Topic.ARTICLE_UPDATE.topic());
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
					status = providerQuery.findAndRead(barcode, position);
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

	private IUpdateProviderServer getUpdateProviderServer()
	{
		this.startUpdateProviderServer();
		return this.updateProviderServer;
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
		properties.put(EventConstants.BUNDLE_ID, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		if (status.getException() != null)
		{
			properties.put(EventConstants.EXCEPTION, status.getException());
			properties.put(EventConstants.EXCEPTION_MESSAGE, status.getException().getMessage() == null ? "" : status.getException().getMessage());
		}
		properties.put("message", "Die Verbindung zu " + Activator.getDefault().getConfiguration().getName() + " konnte nicht hergestellt werden. Die Daten müssen manuell erfasst werden.");
		properties.put("status", status);
		properties.put("force", Boolean.valueOf(force));
		for (ProviderService.Topic t : ProviderService.Topic.values())
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

	private void stopUpdateProviderServer()
	{
		if (this.updateProviderServer != null)
		{
			this.updateProviderServer.stop();
			this.updateProviderServer = null;
		}
	}

	@Override
	public IStatus updateProvider(Payment payment) 
	{
		return Status.OK_STATUS;
	}

	@Override
	public Map<String, IProperty> getDefaultProperties() 
	{
		return GalileoConfiguration.GalileoProperty.asMap();
	}

	@Override
	public boolean canCheckConnection() 
	{
		return true;
	}

	@Override
	public boolean doCheckFailover() 
	{
		return true;
	}

	@Override
	public boolean isSalespointSpecificPossible() 
	{
		return true;
	}

	@Override
	public Section[] getSections() 
	{
		return GalileoSection.values();
	}

	@Override
	public Integer getRanking() 
	{
		return Integer.valueOf(1000);
	}

	@Override
	public boolean doUpdatePositions(ConnectionType connectionType) 
	{
		switch(connectionType)
		{
		case LOCAL:
		{
			return true;
		}
		case SERVER:
		{
			return true;
		}
		default:
		{
			return false;
		}
		}
	}

	@Override
	public boolean doUpdatePayments(ConnectionType connectionType) 
	{
		switch(connectionType)
		{
		case LOCAL:
		{
			return true;
		}
		case SERVER:
		{
			return true;
		}
		default:
		{
			return false;
		}
		}
	}
	
}
