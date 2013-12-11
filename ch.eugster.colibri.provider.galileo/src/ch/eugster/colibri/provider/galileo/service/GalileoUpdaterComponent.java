package ch.eugster.colibri.provider.galileo.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.barcode.code.Barcode;
import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoSection;
import ch.eugster.colibri.provider.galileo.galserve.IUpdateProviderServer;
import ch.eugster.colibri.provider.galileo.galserve.UpdateProviderServerCom4j;
import ch.eugster.colibri.provider.service.AbstractProviderUpdater;
import ch.eugster.colibri.provider.service.ProviderQuery;

public class GalileoUpdaterComponent extends AbstractProviderUpdater
{
	private LogService logService;

	private ProviderQuery providerQuery;

	private IUpdateProviderServer updateProviderServer;

	private final Collection<BarcodeVerifier> barcodeVerifiers = new ArrayList<BarcodeVerifier>();

	public GalileoUpdaterComponent()
	{
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
	public IStatus updatePositions(PersistenceService persistenceService,
			Collection<Position> positions)
	{
		IStatus status = super.updatePositions(persistenceService, positions);
		this.providerQuery.setStatus(status);
		return status;
	}
	
	@Override
	public IStatus updatePayments(PersistenceService persistenceService,
			Collection<Payment> payments)
	{
		IStatus status = super.updatePayments(persistenceService, payments);
		this.providerQuery.setStatus(status);
		return status;
	}
	
	@Override
	public IStatus updateProvider(final Position position)
	{
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
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
		this.startUpdateProviderServer();
		log(LogService.LOG_INFO, "Service " + this.context.getProperties().get("component.name") + " aktiviert.");
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		this.stopUpdateProviderServer();
		log(LogService.LOG_INFO, "Service " + this.context.getProperties().get("component.name")
					+ " deaktiviert.");
		this.context = null;
	}

	protected void setBarcodeVerifier(final BarcodeVerifier barcodeVerifier)
	{
		this.barcodeVerifiers.add(barcodeVerifier);
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
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
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
		return this.updateProviderServer;
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
	public boolean doUpdatePositions() 
	{
		return true;
	}

	@Override
	public boolean doUpdatePayments() 
	{
		return false;
	}
	
}
