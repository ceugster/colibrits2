package ch.eugster.colibri.scheduler;

import java.util.Arrays;
import java.util.Collection;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.rules.ServerDatabaseRule;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.transfer.services.TransferAgent;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.provider.service.ProviderUpdater;
import ch.eugster.colibri.scheduler.service.UpdateScheduler;

public class UpdateSchedulerComponent implements UpdateScheduler, EventHandler
{
	private boolean locked = false;

	private boolean running = false;
	
	private PersistenceService persistenceService;

	private List<ProviderUpdater> providerUpdaters = new Vector<ProviderUpdater>();

	private TransferAgent transferAgent;

	private EventAdmin eventAdmin;
	
	private LogService logService;
	
	private ComponentContext context;
	
	private UpdateSchedulerJob updateScheduler;

	private Map<String, IProperty> schedulerProperties;
	
	private boolean showFailoverMessage = true;
	
	private Dictionary<String, Object> properties;
	
	private void setRunning(boolean _running)
	{
		this.running = _running;
	}
	
	public void setEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	protected void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void setLogService(LogService logService)
	{
		this.logService = logService;
	}
	
	protected void unsetLogService(LogService logService)
	{
		this.logService = null;
	}
	
	protected void setTransferAgent(final TransferAgent transferAgent)
	{
		this.transferAgent = transferAgent;
	}

	protected void unsetTransferAgent(final TransferAgent transferAgent)
	{
		this.transferAgent = null;
	}

	protected void addProviderUpdater(final ProviderUpdater providerUpdater)
	{
		this.providerUpdaters.add(providerUpdater);
	}

	protected void removeProviderUpdater(ProviderUpdater providerUpdater)
	{
		this.providerUpdaters.remove(providerUpdater);
	}
	
	protected void unsetEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	protected void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;
	
		String topics[] = new String[] {"ch/eugster/colibri/persistence/replication/completed", Topic.LOCK.topic(), Topic.UNLOCK.topic() };
		Hashtable<String, Object> serviceProperties = new Hashtable<String, Object>();
		serviceProperties.put(EventConstants.EVENT_TOPIC, topics);
		Activator.getDefault().getBundleContext().registerService(EventHandler.class, this, serviceProperties);
	}
	
	public void handleEvent(Event event)
	{
		if (event.getTopic().equals("ch/eugster/colibri/persistence/replication/completed"))
		{
			schedulerProperties = UpdateScheduler.SchedulerProperty.asMap();
			ProviderPropertyQuery providerPropertyQuery = (ProviderPropertyQuery) persistenceService.getCacheService().getQuery(ProviderProperty.class);
			Collection<ProviderProperty> providerProperties = providerPropertyQuery.selectByProvider(this.context.getBundleContext().getBundle().getSymbolicName());
			for (ProviderProperty providerProperty : providerProperties)
			{
				IProperty property = schedulerProperties.get(providerProperty.getKey());
				if (property != null)
				{
					property.setPersistedProperty(providerProperty);
				}
			}
			this.updateScheduler = new UpdateSchedulerJob("Updating...");
			this.updateScheduler.setRule(ServerDatabaseRule.getRule());
			this.updateScheduler.setPriority(Job.LONG);
			this.updateScheduler.addJobChangeListener(new JobChangeAdapter()
			{
				@Override
				public void done(IJobChangeEvent event) 
				{
					if (event.getResult().getSeverity() != IStatus.CANCEL)
					{
						UpdateSchedulerComponent.this.updateScheduler.schedule(getRepeatDelay());
					}
				}
			});
			this.updateScheduler.schedule(getDelay());
			this.log(LogService.LOG_DEBUG, "Service " + this.getClass().getName() + " aktiviert.");
		}
		else if (event.getTopic().equals(Topic.LOCK.topic()))
		{
			while (this.running)
			{
				try 
				{
					Thread.sleep(200L);
				} 
				catch (InterruptedException e) 
				{
				}
			}
			this.locked = true;
		}
		else if (event.getTopic().equals(Topic.UNLOCK.topic()))
		{
			this.locked = false;
		}
	}

	private void log(int level, String message)
	{
		if (logService != null)
		{
			logService.log(level, message);
		}
	}
	
	protected void deactivate(final ComponentContext componentContext)
	{
		if (this.updateScheduler != null)
		{
			this.updateScheduler.cancel();
			this.updateScheduler = null;
		}
		this.context = null;
		this.log(LogService.LOG_DEBUG, "Service " + this.getClass().getName() + " deaktiviert.");
	}
	
	private long countTransfers()
	{
		final ReceiptQuery receiptQuery = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
		long count = receiptQuery.countRemainingToTransfer();
//		final SettlementQuery settlementQuery = (SettlementQuery) persistenceService.getCacheService().getQuery(Settlement.class);
//		count += settlementQuery.countTransferables();
		return count;
	}

	private long countProviderUpdates()
	{
		long count = 0L;
		for (ProviderUpdater providerUpdater : providerUpdaters)
		{
			if (providerUpdater.isActive())
			{
				count += providerUpdater.countPositions(UpdateSchedulerComponent.this.persistenceService);
				count += providerUpdater.countPayments(UpdateSchedulerComponent.this.persistenceService);
			}
		}
		return count;
	}
	
	private void sendUpdateEvent(IStatus status, String providerId)
	{
		if (UpdateSchedulerComponent.this.eventAdmin != null)
		{
			long count = countProviderUpdates();
			Dictionary<String, Object> properties = createBasicProperties(Topic.SCHEDULED_PROVIDER_UPDATE, status);
			properties.put("topic", Topic.SCHEDULED_PROVIDER_UPDATE);
			properties.put("status", status);
			properties.put("count", count);
			properties.put("provider", providerId);
			properties.put("failover", Boolean.valueOf(status.getException() != null));
			final Event event = new Event(Topic.SCHEDULED_PROVIDER_UPDATE.topic(), properties);
			UpdateSchedulerComponent.this.eventAdmin.sendEvent(event);
		}
	}
	
	private void sendTransferEvent(IStatus status)
	{
		if (UpdateSchedulerComponent.this.eventAdmin != null)
		{
			long count = countTransfers();
			Dictionary<String, Object> properties = createBasicProperties(Topic.SCHEDULED_TRANSFER, status);
			properties.put("topic", Topic.SCHEDULED_TRANSFER);
			properties.put("status", status);
			properties.put("count", count);
			properties.put("provider", "transfer");
			properties.put("failover", Boolean.valueOf(status.getException() != null));
			final Event event = new Event(Topic.SCHEDULED_TRANSFER.topic(), properties);
			UpdateSchedulerComponent.this.eventAdmin.sendEvent(event);
		}
	}
	
	private Dictionary<String, Object> createBasicProperties(Topic topic, IStatus status)
	{
		this.properties = new Hashtable<String, Object>();
		this.properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundleContext().getBundle().getSymbolicName());
		this.properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
		if (status.getSeverity() == IStatus.WARNING)
		{
			if (status.getException() != null)
			{
				String msg = status.getException().getMessage();
				this.properties.put(EventConstants.MESSAGE, msg == null ? "" : msg);
			}
		}
		else
		{
			if (status.getException() != null)
			{
				this.properties.put(EventConstants.EXCEPTION, status.getException());
				String msg = status.getException().getMessage();
				this.properties.put(EventConstants.EXCEPTION_MESSAGE, msg == null ? "" : msg);

				if (this.showFailoverMessage)
				{
					this.properties.put("show.failover.message", Boolean.valueOf(this.showFailoverMessage));
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
		}
		return properties;
	}
	
	private class UpdateSchedulerJob extends Job
	{
		UpdateSchedulerJob(String name)
		{
			super(name);
		}

		protected IStatus run(final IProgressMonitor monitor)
		{
			if (!UpdateSchedulerComponent.this.locked)
			{
				try
				{
					UpdateSchedulerComponent.this.setRunning(true);
					monitor.beginTask("Aktualisiere...", 2);
					ProviderUpdater[] providerUpdaters = UpdateSchedulerComponent.this.providerUpdaters.toArray(new ProviderUpdater[0]);
					Arrays.sort(providerUpdaters);
					if (persistenceService.getCacheService() != null)
					{
						update(new SubProgressMonitor(monitor, providerUpdaters.length), providerUpdaters);
						monitor.worked(1);
						transfer(new SubProgressMonitor(monitor, getSchedulerCount()));
						monitor.worked(1);
					}
				}
				finally
				{
					UpdateSchedulerComponent.this.setRunning(false);
					monitor.done();
				}
			}
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), "Aktualisierung durchgeführt");
		}
		
		private void transfer(IProgressMonitor monitor)
		{
			monitor.beginTask("Transferiere...", 1);
			IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_TRANSFER.topic());
			if (transferAgent != null)
			{
				try
				{
					status = transferAgent.transfer(getSchedulerCount());
					monitor.worked(1);
				}
				finally
				{
					monitor.done();
				}
			}
			sendTransferEvent(status);
		}

		private void update(IProgressMonitor monitor, ProviderUpdater[] providerUpdaters)
		{
			try
			{
				monitor.beginTask("Update Positions...", providerUpdaters.length * 2);
				for (ProviderUpdater providerUpdater : providerUpdaters)
				{
					if (providerUpdater.isActive())
					{
						if (providerUpdater.doUpdatePositions())
						{
							IStatus status = updatePositions(providerUpdater, persistenceService);
							sendUpdateEvent(status, providerUpdater.getProviderId());
						}
						monitor.worked(1);

						if (providerUpdater.doUpdatePayments())
						{
							IStatus status = updatePayments(providerUpdater, persistenceService);
							sendUpdateEvent(status, providerUpdater.getProviderId());
						}
						monitor.worked(1);
					}
					else
					{
						monitor.worked(2);
					}
				}
			}
			finally
			{
				monitor.done();
			}
		}
		
		private IStatus updatePositions(ProviderUpdater providerUpdater, PersistenceService persistenceService)
		{
			if (providerUpdater.doUpdatePositions())
			{
				List<Position> positions = providerUpdater.getPositions(persistenceService, getSchedulerCount());
				return providerUpdater.updatePositions(persistenceService, positions);
			}
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		}
		
		private IStatus updatePayments(ProviderUpdater providerUpdater, PersistenceService persistenceService)
		{
			if (providerUpdater.doUpdatePayments())
			{
				List<Payment> payments = providerUpdater.getPayments(persistenceService, getSchedulerCount());
				return providerUpdater.updatePayments(persistenceService, payments);
			}
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		}

	}
	
	public long getRepeatDelay()
	{
		IProperty property = schedulerProperties.get(UpdateScheduler.SchedulerProperty.SCHEDULER_PERIOD.key());
		return Long.valueOf(property.value()).longValue() * 1000;
	}

	public long getDelay()
	{
		IProperty property = schedulerProperties.get(UpdateScheduler.SchedulerProperty.SCHEDULER_DELAY.key());
		return Long.valueOf(property.value()).longValue() * 1000;
	}

	public int getSchedulerCount()
	{
		IProperty property = schedulerProperties.get(UpdateScheduler.SchedulerProperty.SCHEDULER_COUNT.key());
		return Integer.valueOf(property.value()).intValue();
	}

	@Override
	public String getName() 
	{
		return "Aktualisierungsplanung";
	}

	public Section[] getSections()
	{
		return SchedulerSection.values();
	}

	@Override
	public String getProviderId() 
	{
		return Activator.getDefault().getBundleContext().getBundle().getSymbolicName();
	}
}
