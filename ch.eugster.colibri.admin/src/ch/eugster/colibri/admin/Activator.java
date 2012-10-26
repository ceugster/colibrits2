package ch.eugster.colibri.admin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.app.ApplicationInstanceListener;
import ch.eugster.colibri.admin.app.ApplicationInstanceManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.admin";

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

		ApplicationInstanceManager.setApplicationInstanceListener(new ApplicationInstanceListener()
		{
			@Override
			public void newInstanceCreated()
			{
				final LogService logService = Activator.this.logServiceTracker.getService();
				if (logService != null)
				{
					logService.log(LogService.LOG_ERROR, "Andere Instanz wurde gestartet...");
				}
			}
		});

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();
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
		this.logServiceTracker.close();
		Activator.plugin = null;
		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put("EXIT", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/exit_16x16.png")));
		imageRegistry.put("PREFS",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/settings_16x16.png")));
		imageRegistry.put("OK", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/ok_16.png")));
		imageRegistry.put("EXCLAMATION",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/exclamation_16.png")));
		imageRegistry.put("ERROR", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/error_16.png")));
		imageRegistry.put("reset", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/undo_edit.png")));
		imageRegistry.put("quit", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/exit_16x16.png")));
		imageRegistry.put("login", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/login48.png")));
		imageRegistry.put("wait", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/wait_animated.gif")));
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
