package ch.eugster.colibri.provider.voucher.webservice.internal;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator
{
	private ServiceTracker<LogService, LogService> logServiceTracker;

	// The shared instance
	private static Activator plugin;

	private BundleContext context;
	
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
		this.context = context;
		Activator.plugin = this;

		logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		logServiceTracker.open();

		final LogService logService = (LogService) logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, "Plugin " + context.getBundle().getSymbolicName() + " gestartet.");
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
			logService.log(LogService.LOG_INFO, "Plugin " + context.getBundle().getSymbolicName() + " gestoppt.");
		}
		logServiceTracker.close();
		Activator.plugin = null;
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

	public BundleContext getContext()
	{
		return context;
	}
}
