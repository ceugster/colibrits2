package ch.eugster.colibri.client;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.app.ApplicationInstanceListener;
import ch.eugster.colibri.client.app.ApplicationInstanceManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.client";

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private LogService logService;
	
	// The shared instance
	private static Activator plugin;

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

		ApplicationInstanceManager.setApplicationInstanceListener(new ApplicationInstanceListener()
		{
			@Override
			public void newInstanceCreated()
			{
				final LogService logService = Activator.this.logServiceTracker.getService();
				if (logService != null)
				{
					logService.log(LogService.LOG_ERROR, "Andere Instanz wurde gestartet...");
				}
			}
		});

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		logService = this.logServiceTracker.getService();
		log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
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
		log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
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
	
	public void log(int level, String message)
	{
		if (logService != null)
		{
			logService.log(level, message);
		}
	}
}
