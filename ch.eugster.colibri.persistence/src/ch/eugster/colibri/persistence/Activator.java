package ch.eugster.colibri.persistence;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator extends Plugin
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.persistence";

	private static Activator activator;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	public void log(final IStatus status)
	{
		this.log(status.getMessage());
	}

	public void log(final String message)
	{
		if (this.logServiceTracker != null)
		{
			final LogService logService = this.logServiceTracker.getService();
			if (logService != null)
			{
				logService.log(LogService.LOG_INFO, message);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(final BundleContext context) throws Exception
	{
		Activator.activator = this;

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		this.log("Plugin " + Activator.PLUGIN_ID + " gestartet.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(final BundleContext context) throws Exception
	{
		this.log("Plugin " + Activator.PLUGIN_ID + " gestoppt.");
		this.logServiceTracker.close();
		Activator.activator = null;
	}

	public static Activator getDefault()
	{
		return Activator.activator;
	}

}
