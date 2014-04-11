package ch.eugster.colibri.report;

import java.util.Hashtable;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.report.engine.ReportService;
import ch.eugster.colibri.report.internal.engine.ReportServiceComponent;

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
			logService.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
		}
		context.registerService(ReportService.class.getName(), new ReportServiceComponent(),
				new Hashtable<String, Object>());
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
			logService.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
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
		imageRegistry.put("first", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/first.gif")));
		imageRegistry.put("firstd", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/firstd.gif")));
		imageRegistry.put("last", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/last.gif")));
		imageRegistry.put("lastd", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/lastd.gif")));
		imageRegistry.put("next", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/next.gif")));
		imageRegistry.put("nextd", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/nextd.gif")));
		imageRegistry.put("previous", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/previous.gif")));
		imageRegistry.put("previousd", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/previousd.gif")));
		imageRegistry.put("print", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/print.gif")));
		imageRegistry.put("printd", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/printd.gif")));
		imageRegistry.put("reload", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/reload.gif")));
		imageRegistry.put("reloadd", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/reloadd.gif")));
		imageRegistry.put("save", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/save.gif")));
		imageRegistry.put("saved", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/saved.gif")));
		imageRegistry.put("zoomactual", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomactual.gif")));
		imageRegistry.put("zoomactuald", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomactuald.gif")));
		imageRegistry.put("zoomfitpage", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomfitpage.gif")));
		imageRegistry.put("zoomfitpaged",
				ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomfitpaged.gif")));
		imageRegistry.put("zoomfitwidth",
				ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomfitwidth.gif")));
		imageRegistry.put("zoomfitwidthd",
				ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomfitwidthd.gif")));
		imageRegistry.put("zoomminus", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomminus.gif")));
		imageRegistry.put("zoomminusd", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomminusd.gif")));
		imageRegistry.put("zoomplus", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomplus.gif")));
		imageRegistry.put("zoomplusd", ImageDescriptor.createFromURL(getBundle().getEntry("/icons/zoomplusd.gif")));
	}

}
