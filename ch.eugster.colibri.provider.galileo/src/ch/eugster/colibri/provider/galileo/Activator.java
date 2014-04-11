package ch.eugster.colibri.provider.galileo;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	private ServiceTracker<LogService, LogService> logServiceTracker;

	// The shared instance
	private static Activator plugin;

	private GalileoConfiguration configuration;

	/**
	 * The constructor
	 */
	public Activator()
	{
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

		logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		logServiceTracker.open();

		final LogService logService = (LogService) logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Plugin " + context.getBundle().getSymbolicName() + " gestartet.");
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
		final LogService logService = (LogService) logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Plugin " + context.getBundle().getSymbolicName() + " gestoppt.");
		}

		logServiceTracker.close();

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

	public GalileoConfiguration getConfiguration()
	{
		if (this.configuration == null)
		{
			this.configuration = new GalileoConfiguration();
		}
		return this.configuration;
	}
}
