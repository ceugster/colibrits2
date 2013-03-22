package ch.eugster.colibri.provider.scheduler;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.rules.LocalDatabaseRule;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.SchedulerProperty;
import ch.eugster.colibri.provider.scheduler.service.ProviderUpdateScheduler;
import ch.eugster.colibri.provider.service.ProviderInterface;

public class ProviderUpdateSchedulerComponent implements ProviderUpdateScheduler
{
	private ProviderUpdateJob providerUpdateJob;

	private PersistenceService persistenceService;

	private ProviderInterface providerInterface;

	private EventAdmin eventAdmin;

	private ComponentContext context;

	public void setEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	public void setProviderInterface(final ProviderInterface providerInterface)
	{
		this.providerInterface = providerInterface;
	}

	public void unsetEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	public void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	public void unsetProviderInterface(final ProviderInterface providerInterface)
	{
		this.providerInterface = null;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;
		
		ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getCacheService().getQuery(ProviderProperty.class);
		Map<String, String> defaults = new HashMap<String, String>();
		for (SchedulerProperty property : SchedulerProperty.values())
		{
			defaults.put(property.key(), property.value());
		}
		Map<String, ProviderProperty> properties = query.selectByProviderAsMap(providerInterface.getProviderId(), defaults);
		this.providerUpdateJob = new ProviderUpdateJob(this.providerInterface.getName() + " wird aktualisiert...", properties);
//		this.providerUpdateJob.setSystem(true);
		this.providerUpdateJob.setRule(LocalDatabaseRule.getRule());
		this.providerUpdateJob.setPriority(Job.LONG);
		this.providerUpdateJob.schedule(this.providerUpdateJob.getDelay());
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		this.providerUpdateJob.stop();
		this.providerUpdateJob.cancel();
		this.context = null;
	}

	private class ProviderUpdateJob extends Job
	{
		private boolean running = true;
		
		private Map<String, ProviderProperty> properties;
		
		ProviderUpdateJob(String name, Map<String, ProviderProperty> properties)
		{
			super(name);
			this.properties = properties;
		}
		
		protected IStatus run(final IProgressMonitor monitor)
		{
			IStatus status = Status.OK_STATUS;
			try
			{
				final PositionQuery query = (PositionQuery) ProviderUpdateSchedulerComponent.this.persistenceService
						.getCacheService().getQuery(Position.class);
				ProviderProperty property = properties.get(SchedulerProperty.SCHEDULER_RECEIPT_COUNT.key());
				int count = Integer.valueOf(property.getValue()).intValue();
				final Position[] positions = query.selectProviderUpdates(count).toArray(new Position[0]);
				if (positions.length > 0)
				{
					for (Position position : positions)
					{
						if (monitor.isCanceled())
						{
							status = Status.CANCEL_STATUS;
							break;
						}
						else
						{
							
							status = Status.OK_STATUS;
							final IStatus state = ProviderUpdateSchedulerComponent.this.providerInterface
									.updateProvider(position);
							if ((state.getSeverity() == IStatus.OK) || (state.getSeverity() == IStatus.ERROR))
							{
								status = state;
							}
							if (status.getSeverity() == IStatus.OK)
							{
//								position = (Position) ProviderUpdateSchedulerComponent.this.persistenceService
//										.getCacheService().refresh(position);
								
								position = (Position) ProviderUpdateSchedulerComponent.this.persistenceService
										.getCacheService().merge(position);
							}
						}
					}

					if (ProviderUpdateSchedulerComponent.this.eventAdmin instanceof EventAdmin)
					{
						final Dictionary<String, Object> properties = new Hashtable<String, Object>();
						properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundleContext().getBundle());
						properties.put(EventConstants.BUNDLE_ID,
								Long.valueOf(Activator.getDefault().getBundleContext().getBundle().getBundleId()));
						properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
						properties.put(EventConstants.SERVICE,
								ProviderUpdateSchedulerComponent.this.context.getServiceReference());
						properties.put(EventConstants.SERVICE_ID, ProviderUpdateSchedulerComponent.this.context
								.getProperties().get("component.id"));
						properties.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
						properties.put(EventConstants.SERVICE_PID, ProviderUpdateSchedulerComponent.this.context
								.getProperties().get("component.name"));
						properties
								.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
						if (status.getException() != null)
						{
							properties.put(EventConstants.EXCEPTION, status.getException());
							properties.put(EventConstants.EXCEPTION_MESSAGE, status.getException().getMessage());
						}
						properties.put("status", status);
						ProviderInterface.Topic topic = null;
						for (ProviderInterface.Topic t : ProviderInterface.Topic.values())
						{
							if (status.getMessage().equals(t.topic()))
							{
								topic = t;
								break;
							}
						}
						if (topic == null)
						{
							topic = ProviderInterface.Topic.ARTICLE_UPDATE;
						}
						properties.put("topic", topic);
						final Event event = new Event(topic.topic(), properties);
						ProviderUpdateSchedulerComponent.this.eventAdmin.sendEvent(event);
					}
				}
			}
			finally
			{
				this.schedule(getRepeatDelay());
			}
			return Status.OK_STATUS;
		}
		
		public void stop()
		{
			this.running = false;
		}

		public boolean shouldSchedule()
		{
			return this.running;
		}
		
		public long getRepeatDelay()
		{
			ProviderProperty property = properties.get(SchedulerProperty.SCHEDULER_PERIOD.key());
			return Integer.valueOf(property.getValue()).longValue();
		}

		public long getDelay()
		{
			ProviderProperty property = properties.get(SchedulerProperty.SCHEDULER_DELAY.key());
			return Integer.valueOf(property.getValue()).longValue();
		}
	}
}
