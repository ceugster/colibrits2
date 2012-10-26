package ch.eugster.colibri.report;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.report";

	private ServiceTracker<LogService, LogService> logServiceTracker;

	// The shared instance
	private static Activator plugin;

	public LogService getLogService()
	{
		return this.logServiceTracker.getService();
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

		final LogService logService = this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
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
		final LogService logService = this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
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

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put("reset", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/undo_edit.png")));
		imageRegistry.put("quit", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/exit_16x16.png")));
		imageRegistry.put("login", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/login48.png")));
		imageRegistry.put("wait", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/wait_animated.gif")));
	}

}
