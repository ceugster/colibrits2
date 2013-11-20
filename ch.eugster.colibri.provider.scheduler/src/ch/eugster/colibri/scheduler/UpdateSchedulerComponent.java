package ch.eugster.colibri.scheduler;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
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
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.rules.ServerDatabaseRule;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.transfer.services.TransferAgent;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.provider.service.ProviderUpdater;
import ch.eugster.colibri.scheduler.service.UpdateScheduler;

public class UpdateSchedulerComponent implements UpdateScheduler
{
	private PersistenceService persistenceService;

	private List<ProviderUpdater> providerUpdaters = new Vector<ProviderUpdater>();

	private TransferAgent transferAgent;

	private EventAdmin eventAdmin;
	
	private LogService logService;
	
	private ComponentContext context;
	
	private UpdateSchedulerJob updateScheduler;

	private Map<String, IProperty> schedulerProperties;
	
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
		this.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " aktiviert.");
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
		this.log(LogService.LOG_INFO, "Service " + this.getClass().getName() + " deaktiviert.");
	}

	private class UpdateSchedulerJob extends Job
	{
		private boolean showFailoverMessage = true;
		
		UpdateSchedulerJob(String name)
		{
			super(name);
		}

		protected IStatus run(final IProgressMonitor monitor)
		{
			IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_TRANSFER.topic());
			try
			{
				monitor.beginTask("Aktualisiere...", 2);
				ProviderUpdater[] providerUpdaters = UpdateSchedulerComponent.this.providerUpdaters.toArray(new ProviderUpdater[0]);
				Arrays.sort(providerUpdaters);
				if (persistenceService.getCacheService() != null)
				{
					IStatus providerStatus = update(new SubProgressMonitor(monitor, providerUpdaters.length), providerUpdaters, persistenceService);
					sendUpdateEvent(Topic.SCHEDULED_PROVIDER_UPDATE, providerStatus);
					monitor.worked(1);
					IStatus transferStatus = transfer(new SubProgressMonitor(monitor, getSchedulerCount()));
					sendUpdateEvent(Topic.SCHEDULED_TRANSFER, transferStatus);
					monitor.worked(1);
					if (transferStatus.getSeverity() == IStatus.ERROR)
					{
						status = transferStatus;
					}
					if (providerStatus.getSeverity() == IStatus.ERROR)
					{
						status = providerStatus;
					}
				}
			}
			finally
			{
				sendUpdateEvent(Topic.SCHEDULED, status);
				monitor.done();
			}
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED.topic());
		}
		
		private void sendUpdateEvent(Topic topic, IStatus status)
		{
			if (UpdateSchedulerComponent.this.eventAdmin != null)
			{
				final Dictionary<String, Object> properties = new Hashtable<String, Object>();
				properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundleContext().getBundle());
				properties.put(EventConstants.BUNDLE_ID,
						Long.valueOf(Activator.getDefault().getBundleContext().getBundle().getBundleId()));
				properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundleContext().getBundle().getSymbolicName());
				properties.put(EventConstants.SERVICE,
						UpdateSchedulerComponent.this.context.getServiceReference());
				properties.put(EventConstants.SERVICE_ID, UpdateSchedulerComponent.this.context
						.getProperties().get("component.id"));
				properties.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
				properties.put(EventConstants.SERVICE_PID, UpdateSchedulerComponent.this.context
						.getProperties().get("component.name"));
				properties
						.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
				if (status.getException() != null)
				{
					properties.put(EventConstants.EXCEPTION, status.getException());
					String msg = status.getException().getMessage();
					properties.put(EventConstants.EXCEPTION_MESSAGE, msg == null ? "" : msg);

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
				properties.put("topic", topic);
				properties.put("status", status);
				final Event event = new Event(topic.topic(), properties);
				UpdateSchedulerComponent.this.eventAdmin.sendEvent(event);
			}
		}
		
		private IStatus transfer(IProgressMonitor monitor)
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
			return status;
		}

		private IStatus update(IProgressMonitor monitor, ProviderUpdater[] providerUpdaters, PersistenceService persistenceService)
		{
			try
			{
				monitor.beginTask("Update Positions...", providerUpdaters.length * 2);
				for (ProviderUpdater providerUpdater : providerUpdaters)
				{
					if (providerUpdater.doUpdatePositions())
					{
						IStatus status = updatePositions(providerUpdater, persistenceService);
						if (status.getSeverity() == IStatus.ERROR || status.getSeverity() == IStatus.CANCEL)
						{
							return status;
						}
					}
					monitor.worked(1);

					if (providerUpdater.doUpdatePayments())
					{
						IStatus status = updatePayments(providerUpdater, persistenceService);
						monitor.worked(1);
						if (status.getSeverity() == IStatus.ERROR || status.getSeverity() == IStatus.CANCEL)
						{
							return status;
						}
					}
					monitor.worked(1);
				}
			}
			finally
			{
				monitor.done();
			}
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		}
		
		private IStatus updatePositions(ProviderUpdater providerUpdater, PersistenceService persistenceService)
		{
			if (providerUpdater.doUpdatePositions())
			{
				Collection<Position> positions = providerUpdater.getPositions(persistenceService.getCacheService(), getSchedulerCount());
				return providerUpdater.updatePositions(persistenceService, positions);
			}
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		}
		
		private IStatus updatePayments(ProviderUpdater providerUpdater, PersistenceService persistenceService)
		{
			if (providerUpdater.doUpdatePayments())
			{
				Collection<Payment> payments = providerUpdater.getPayments(persistenceService.getCacheService(), getSchedulerCount());
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
