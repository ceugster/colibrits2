package ch.eugster.colibri.provider;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.provider";

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private static Activator plugin;

	@Override
	public void start(final BundleContext context) throws Exception
	{
		Activator.plugin = this;

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		final LogService writer = (LogService) this.logServiceTracker.getService();
		if (writer != null)
		{
			writer.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception
	{
		final LogService writer = (LogService) this.logServiceTracker.getService();
		if (writer != null)
		{
			writer.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
		}

		this.logServiceTracker.close();

		Activator.plugin = null;
	}

	public static Activator getDefault()
	{
		return Activator.plugin;
	}
}