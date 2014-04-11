package ch.eugster.colibri.print.settlement;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.print.settlement";

	private static Activator activator;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private BundleContext context;

	public BundleContext getContext()
	{
		return this.context;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(final BundleContext bundleContext) throws Exception
	{
		Activator.activator = this;
		this.context = bundleContext;

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(bundleContext, LogService.class, null);
		this.logServiceTracker.open();
		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(final BundleContext bundleContext) throws Exception
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
		}
		this.logServiceTracker.close();

		Activator.activator = null;
	}

	public static Activator getDefault()
	{
		return Activator.activator;
	}

}
