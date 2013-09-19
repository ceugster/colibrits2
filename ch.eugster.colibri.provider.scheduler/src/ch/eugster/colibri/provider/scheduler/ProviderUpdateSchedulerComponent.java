package ch.eugster.colibri.provider.scheduler;

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
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.rules.ServerDatabaseRule;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.IProperty.Section;
import ch.eugster.colibri.provider.scheduler.service.ProviderUpdateScheduler;
import ch.eugster.colibri.provider.service.ProviderInterface;
import ch.eugster.colibri.provider.service.ProviderUpdater;

public class ProviderUpdateSchedulerComponent implements ProviderUpdateScheduler
{
	private ProviderUpdateJob providerUpdateJob;

	private PersistenceService persistenceService;

	private List<ProviderUpdater> providerUpdaters = new Vector<ProviderUpdater>();

	private EventAdmin eventAdmin;

	private Salespoint salespoint;
	
	private ComponentContext context;

	public void setEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	public void addProviderUpdater(final ProviderUpdater providerUpdater)
	{
		this.providerUpdaters.add(providerUpdater);
	}

	public void removeProviderUpdater(ProviderUpdater providerUpdater)
	{
		this.providerUpdaters.remove(providerUpdater);
	}
	
	public void unsetEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	public void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;
	
		Map<String, IProperty> schedulerProperties = ProviderUpdateScheduler.SchedulerProperty.asMap();
		ProviderPropertyQuery providerPropertyQuery = (ProviderPropertyQuery) persistenceService.getServerService().getQuery(ProviderProperty.class);
		Collection<ProviderProperty> providerProperties = providerPropertyQuery.selectByProvider(this.context.getBundleContext().getBundle().getSymbolicName());
		for (ProviderProperty providerProperty : providerProperties)
		{
			IProperty property = schedulerProperties.get(providerProperty.getKey());
			property.setPersistedProperty(providerProperty);
		}
		this.providerUpdateJob = new ProviderUpdateJob("Updating...", schedulerProperties);
		this.providerUpdateJob.setRule(ServerDatabaseRule.getRule());
		this.providerUpdateJob.setPriority(Job.LONG);
		this.providerUpdateJob.schedule(this.providerUpdateJob.getDelay());
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		if (this.providerUpdateJob != null)
		{
			this.providerUpdateJob.stop();
			this.providerUpdateJob.cancel();
			this.context = null;
		}
	}

	private class ProviderUpdateJob extends Job
	{
		private boolean running = true;
		
		private Map<String, IProperty> properties;
		
		ProviderUpdateJob(String name, Map<String, IProperty> properties)
		{
			super(name);
			this.properties = properties;
		}
		
		protected IStatus run(final IProgressMonitor monitor)
		{
			IStatus status = Status.OK_STATUS;
			try
			{
				if (salespoint == null)
				{
					SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getServerService().getQuery(Salespoint.class);
					salespoint = salespointQuery.getCurrentSalespoint();
				}
				final PositionQuery query = (PositionQuery) ProviderUpdateSchedulerComponent.this.persistenceService
						.getServerService().getQuery(Position.class);
				IProperty property = properties.get(ProviderUpdateScheduler.SchedulerProperty.SCHEDULER_COUNT.key());
				int count = Integer.valueOf(property.value()).intValue();
				final Position[] positions = query.selectProviderUpdates(salespoint, count).toArray(new Position[0]);
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
							ProviderUpdater[] updaters = providerUpdaters.toArray(new ProviderUpdater[0]);
							for (ProviderUpdater updater : updaters)
							{
								if (updater.getProviderId().equals(position.getProvider()))
								{
									final IStatus state = updater.updateProvider(position);
									if ((state.getSeverity() == IStatus.OK) || (state.getSeverity() == IStatus.ERROR))
									{
										status = state;
									}
									if (status.getSeverity() == IStatus.OK)
									{
										position = (Position) ProviderUpdateSchedulerComponent.this.persistenceService
												.getServerService().merge(position);
									}
									break;
								}
							}
						}
					}

					if (ProviderUpdateSchedulerComponent.this.eventAdmin instanceof EventAdmin)
					{
						final Dictionary<String, Object> properties = new Hashtable<String, Object>();
						properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundleContext().getBundle());
						properties.put(EventConstants.BUNDLE_ID,
								Long.valueOf(Activator.getDefault().getBundleContext().getBundle().getBundleId()));
						properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundleContext().getBundle().getSymbolicName());
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
							String msg = status.getException().getMessage();
							properties.put(EventConstants.EXCEPTION_MESSAGE, msg == null ? "" : msg);
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
			IProperty property = properties.get(ProviderUpdateScheduler.SchedulerProperty.SCHEDULER_PERIOD.key());
			return Long.valueOf(property.value()).longValue() * 1000;
		}

		public long getDelay()
		{
			IProperty property = properties.get(ProviderUpdateScheduler.SchedulerProperty.SCHEDULER_DELAY.key());
			return Long.valueOf(property.value()).longValue() * 1000;
		}
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
