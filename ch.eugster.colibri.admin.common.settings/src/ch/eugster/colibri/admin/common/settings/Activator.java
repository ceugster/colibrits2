package ch.eugster.colibri.admin.common.settings;

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
	public static final String PLUGIN_ID = "ch.eugster.colibri.ui.common.settings";

	public static final String IMAGE_LAYOUT = "image.layout";

	public static final String IMAGE_PRINTER = "image.printer";

	public static final String IMAGE_STOCK = "image.stock";

	public static final String IMAGE_REGISTER = "image.register";

	public static final String IMAGE_BULP = "image.bulp";

	public static final String IMAGE_GEARWHEEL = "image.gearwheel";

	private ServiceTracker<LogService, LogService> logServiceTracker;

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	@Override
	public void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put(Activator.IMAGE_LAYOUT, ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/layout_16.png")));
		imageRegistry.put(Activator.IMAGE_PRINTER, ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/printer.png")));
		imageRegistry.put(Activator.IMAGE_STOCK, ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/books.gif")));
		imageRegistry.put(Activator.IMAGE_REGISTER, ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/register_16.png")));
		imageRegistry.put(Activator.IMAGE_BULP, ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/gluehbirne_16.png")));
		imageRegistry.put(Activator.IMAGE_GEARWHEEL, ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/gearwheel_16.png")));
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

		final LogService log = (LogService) this.logServiceTracker.getService();
		if (log != null)
		{
			log.log(LogService.LOG_INFO, Activator.PLUGIN_ID + ".plugin gestartet");
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
		final LogService log = (LogService) this.logServiceTracker.getService();
		if (log != null)
		{
			log.log(LogService.LOG_INFO, Activator.PLUGIN_ID + ".plugin gestoppt");
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
}
