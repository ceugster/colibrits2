package ch.eugster.colibri.persistence.transfer.impl.services;

import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.rules.ServerDatabaseRule;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.transfer.impl.Activator;
import ch.eugster.colibri.persistence.transfer.services.TransferAgent;
import ch.eugster.colibri.persistence.transfer.services.TransferScheduler;

public class TransferSchedulerImpl implements TransferScheduler
{
	private TransferJob transferJob;

	private ComponentContext context;

	private PersistenceService persistenceService;

	private TransferAgent transferAgent;

	private EventAdmin eventAdmin;

	public void setEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	public void setTransferAgent(final TransferAgent transferAgent)
	{
		this.transferAgent = transferAgent;
	}

	public void unsetEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	public void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	public void unsetTransferAgent(final TransferAgent transferAgent)
	{
		this.transferAgent = null;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;

		CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getCacheService().getQuery(CommonSettings.class);
		CommonSettings settings = query.findDefault();
		if (settings != null)
		{
			transferJob = new TransferJob("Belegtransfer...", settings);
	//		transferJob.setSystem(true);
			transferJob.setRule(ServerDatabaseRule.getRule());
			transferJob.setPriority(Job.LONG);
			transferJob.schedule(Integer.valueOf(settings.getTransferDelay()).longValue());
		}
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		if (transferJob != null)
		{
			transferJob.stop();
			transferJob.cancel();
		}
		this.context = null;
	}

	private class TransferJob extends Job
	{
		private boolean running = true;

		private CommonSettings settings;
		
		public TransferJob(String name, CommonSettings settings)
		{
			super(name);
			this.settings = settings;
		}
		
		public long getRepeatDelay()
		{
			return settings == null ? 60000 : settings.getTransferRepeatDelay();
		}
		
		@Override
		protected IStatus run(final IProgressMonitor monitor)
		{
			IStatus status = Status.OK_STATUS;
			try
			{
				final ReceiptQuery query = (ReceiptQuery) TransferSchedulerImpl.this.persistenceService
						.getCacheService().getQuery(Receipt.class);
				final Receipt[] receipts = query.selectTransferables(settings.getTransferReceiptCount()).toArray(new Receipt[0]);
				for (final Receipt receipt : receipts)
				{
					if (monitor.isCanceled())
					{
						status = Status.CANCEL_STATUS;
						break;
					}
					else
					{
						status = TransferSchedulerImpl.this.transferAgent.transfer(receipt);
						if (TransferSchedulerImpl.this.eventAdmin instanceof EventAdmin)
						{
							final Dictionary<String, Object> properties = new Hashtable<String, Object>();
							properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundleContext()
									.getBundle());
							properties.put(
									EventConstants.BUNDLE_ID,
									Long.valueOf(Activator.getDefault().getBundleContext().getBundle()
											.getBundleId()));
							properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
							properties.put(EventConstants.SERVICE,
									TransferSchedulerImpl.this.context.getServiceReference());
							properties.put(EventConstants.SERVICE_ID, TransferSchedulerImpl.this.context
									.getProperties().get("component.id"));
							properties.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
							properties.put(EventConstants.SERVICE_PID, TransferSchedulerImpl.this.context
									.getProperties().get("component.name"));
							properties.put(EventConstants.TIMESTAMP,
									Long.valueOf(Calendar.getInstance().getTimeInMillis()));
							properties.put("status", status);
							final Event event = new Event("ch/eugster/colibri/persistence/server/database",
									properties);
							TransferSchedulerImpl.this.eventAdmin.sendEvent(event);
						}
					}
				}
			}
			finally
			{
				schedule(getRepeatDelay());
			}
			return Status.OK_STATUS;
		}
		
		
		
		public boolean shouldSchedule()
		{
			return this.running;
		}
		
		public void stop()
		{
			this.running = false;
		}
	}
}
