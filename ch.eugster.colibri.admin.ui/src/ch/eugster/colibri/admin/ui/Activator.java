package ch.eugster.colibri.admin.ui;

import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.events.EventTopic;
import ch.eugster.colibri.persistence.service.PersistenceService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements EventHandler
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.ui.admin"; //$NON-NLS-1$

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

	private UIJob job;
	
	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public PersistenceService getPersistenceService()
	{
		return (PersistenceService) this.persistenceServiceTracker.getService();
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(EventTopic.SERVER.topic()))
		{
			if (event.getProperty("status") instanceof IStatus)
			{
				final IStatus status = (IStatus) event.getProperty("status");
				if (status.getSeverity() == IStatus.ERROR)
				{
					job = new UIJob("database error")
					{
						@Override
						public IStatus runInUIThread(final IProgressMonitor monitor)
						{
							Shell shell = null;
							if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() == null)
							{
								Display display = null;
								display = Display.getCurrent();
								if (display == null)
								{
									display = Display.getDefault();
								}
								if (display.getActiveShell() == null)
								{
									shell = new Shell(display);
								}
								else
								{
									shell = display.getActiveShell();
								}
							}
							else
							{
								shell = Activator.this.getWorkbench().getActiveWorkbenchWindow().getShell();
							}

							final ErrorDialog dialog = new ErrorDialog(shell, "Datenbankfehler", status.getMessage(),
									status, 0);
							dialog.open();
							return Status.OK_STATUS;
						}
					};
					job.schedule();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception
	{
		super.start(context);
		Activator.plugin = this;

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(context, PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		final String[] topics = new String[] { EventTopic.SERVER.topic() };
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = context.registerService(EventHandler.class, eventHandler,
				properties);

		final LogService log = (LogService) this.logServiceTracker.getService();
		if (log != null)
		{
			log.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception
	{
		if (this.job != null)
		{
			this.job.cancel();
		}
		this.eventHandlerServiceRegistration.unregister();

		this.persistenceServiceTracker.close();

		final LogService log = (LogService) this.logServiceTracker.getService();
		if (log != null)
		{
			log.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
		}

		this.logServiceTracker.close();

		Activator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return Activator.plugin;
	}
}
