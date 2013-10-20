package ch.eugster.colibri.scheduler;

import java.util.ArrayList;
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

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.rules.ServerDatabaseRule;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.ConnectionService.ConnectionType;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.transfer.services.TransferAgent;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.provider.service.ProviderService;
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
		UpdateSchedulerJob(String name)
		{
			super(name);
		}

		protected IStatus run(final IProgressMonitor monitor)
		{
			IStatus status = Status.OK_STATUS;
			if (monitor.isCanceled())
			{
				return Status.CANCEL_STATUS;
			}
//			try
//			{
				List<ConnectionService> services = new ArrayList<ConnectionService>();
				if (persistenceService.getServerService() != null && persistenceService.getServerService().isConnected())
				{
					services.add(persistenceService.getServerService());
				}
				if (persistenceService.getCacheService() != null && persistenceService.getCacheService().isConnected())
				{
					services.add(persistenceService.getCacheService());
				}
				ProviderUpdater[] providerUpdaters = UpdateSchedulerComponent.this.providerUpdaters.toArray(new ProviderUpdater[0]);
				Arrays.sort(providerUpdaters);
	
				monitor.beginTask("Aktualisiere...", providerUpdaters.length * services.size() + (transferAgent == null ? 0 : 1));
				try
				{
					if (transferAgent != null)
					{
						status = transferAgent.transfer(new SubProgressMonitor(monitor, getSchedulerCount()), getSchedulerCount());
						sendReceiptTransferEvent(status);
						if (status.getSeverity() == IStatus.CANCEL)
						{
							monitor.done();
							return status;
						}
						monitor.worked(1);
					}
	
					IStatus transferStatus = status;
					IStatus providerUpdateStatus = Status.OK_STATUS;
					for (ProviderUpdater providerUpdater : providerUpdaters)
					{
						boolean checked = false;
						for (ConnectionService service : services)
						{
							if (providerUpdater.doUpdatePositions(service.getConnectionType()))
							{
								Collection<Position> positions = providerUpdater.getPositions(service, getSchedulerCount(), service.getConnectionType());
								if (positions.size() > 0)
								{
									checked = true;
									IStatus providerStatus = providerUpdater.updatePositions(service, positions, new SubProgressMonitor(monitor, positions.size()));
									if (providerStatus.getSeverity() == IStatus.ERROR)
									{
										providerUpdateStatus = providerStatus;
									}
									if (providerStatus.getSeverity() == IStatus.CANCEL)
									{
										return status;
									}
								}
							}
							if (providerUpdater.doUpdatePayments(service.getConnectionType()))
							{
								Collection<Payment> payments = providerUpdater.getPayments(service, getSchedulerCount(), service.getConnectionType());
								if (payments.size() > 0)
								{
									checked = true;
									IStatus providerStatus = providerUpdater.updatePayments(service, payments, new SubProgressMonitor(monitor, payments.size()));
									if (providerStatus.getSeverity() == IStatus.ERROR)
									{
										providerUpdateStatus = providerStatus;
									}
									if (providerStatus.getSeverity() == IStatus.CANCEL)
									{
										return status;
									}
								}
							}
							if (!checked)
							{
								if (providerUpdater.doCheckFailover())
								{
									IStatus providerStatus = providerUpdater.checkConnection();
									if (providerStatus.getSeverity() == IStatus.ERROR)
									{
										providerUpdateStatus = providerStatus;
									}
								}
							}
							this.sendProviderUpdateEvent(providerUpdateStatus, transferStatus, providerUpdater);
							monitor.worked(1);
						}
					}
				}
				finally
				{
					monitor.done();
				}
//			}
//			finally
//			{
//				UpdateSchedulerComponent.this.updateScheduler.schedule(getRepeatDelay());
//			}
			return Status.OK_STATUS;
		}
		
		private void sendProviderUpdateEvent(IStatus status, IStatus transferStatus, ProviderUpdater providerUpdater)
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
				properties.put("transferStatus", transferStatus);
				properties.put("providerId", providerUpdater.getProviderId());
				properties.put("message", "Die Verbindung zu " + providerUpdater.getName() + " konnte nicht hergestellt werden. Die Daten müssen manuell erfasst werden.");
				if (status.getException() != null)
				{
					properties.put(EventConstants.EXCEPTION, status.getException());
					String msg = status.getException().getMessage();
					properties.put(EventConstants.EXCEPTION_MESSAGE, msg == null ? "" : msg);
				}
				properties.put("status", status);
				ProviderService.Topic topic = null;
				for (ProviderService.Topic t : ProviderService.Topic.values())
				{
					if (status.getMessage().equals(t.topic()))
					{
						topic = t;
						break;
					}
				}
				if (topic == null)
				{
					topic = ProviderService.Topic.ARTICLE_UPDATE;
				}
				properties.put("topic", topic);
				final Event event = new Event(topic.topic(), properties);
				UpdateSchedulerComponent.this.eventAdmin.sendEvent(event);
			}
		}
		
		private void sendReceiptTransferEvent(IStatus status)
		{
			if (eventAdmin != null)
			{
				final Dictionary<String, Object> properties = new Hashtable<String, Object>();
				properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundleContext()
						.getBundle());
				properties.put(
						EventConstants.BUNDLE_ID,
						Long.valueOf(Activator.getDefault().getBundleContext().getBundle()
								.getBundleId()));
				properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundleContext().getBundle().getSymbolicName());
				properties.put(EventConstants.SERVICE, context.getServiceReference());
				properties.put(EventConstants.SERVICE_ID, context.getProperties().get("component.id"));
				properties.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
				properties.put(EventConstants.SERVICE_PID, context.getProperties().get("component.name"));
				properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
				properties.put("status", status); final Event event = new Event("ch/eugster/colibri/persistence/server/database",
						properties);
				eventAdmin.sendEvent(event);
			}
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
