package ch.eugster.colibri.report.export;

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
	public static final String PLUGIN_ID = "ch.eugster.colibri.report.settlement";

	// The shared instance
	private static Activator plugin;

	private ServiceTracker<LogService, LogService> logServiceTracker;
	
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
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		plugin = this;

		logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		logServiceTracker.open();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(BundleContext context) throws Exception
	{
		logServiceTracker.close();
		
		plugin = null;
		super.stop(context);
	}

	public void log(int level, String message)
	{
		LogService service = logServiceTracker.getService();
		if (service != null)
		{
			service.log(level, message);
		}
	}
	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return plugin;
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put("print", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/print.gif")));
	}

}
