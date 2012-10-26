package ch.eugster.colibri.barcode.bookcenter;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

public class Activator implements BundleActivator
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.barcode.bookcenter";

	private ServiceTracker<LogService, LogService> logServiceTracker;

	@Override
	public void start(final BundleContext context) throws Exception
	{
		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
		}

		this.logServiceTracker.close();
	}

	public static String getDescription()
	{
		return "Barcode des Schweizerischen Buchzentrums (7-stellig)";
	}

	public static String getName()
	{
		return "BZ";
	}
}
