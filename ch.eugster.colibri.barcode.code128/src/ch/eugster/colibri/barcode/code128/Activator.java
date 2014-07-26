package ch.eugster.colibri.barcode.code128;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.code128.code.Code128.Code128Type;

public class Activator implements BundleActivator
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.barcode.code128";

	public static Activator plugin;

	public BundleContext context;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	@Override
	public void start(final BundleContext context) throws Exception
	{
		Activator.plugin = this;
		this.context = context;
		this.logServiceTracker = new ServiceTracker<LogService,LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
		}
	}

	@Override
	public void stop(final BundleContext context) throws Exception
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
		}

		this.logServiceTracker.close();

		Activator.plugin = null;
	}

	public static Activator getDefault()
	{
		return Activator.plugin;
	}

	public static String getDescription()
	{
		StringBuilder builder = new StringBuilder("Code128 Barcode Typen");
		for (Code128Type code128Type : Code128Type.values())
		{
			builder = builder.append(" " + code128Type.type() + " (" + code128Type.length() + " Zeichen)");
		}
		return builder.toString();
	}

}
