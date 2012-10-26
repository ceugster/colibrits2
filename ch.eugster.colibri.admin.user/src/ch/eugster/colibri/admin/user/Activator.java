package ch.eugster.colibri.admin.user;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.user.editors.RoleEditor;
import ch.eugster.colibri.admin.user.editors.RoleEditorInput;
import ch.eugster.colibri.admin.user.editors.UserEditor;
import ch.eugster.colibri.admin.user.editors.UserEditorInput;
import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.model.User;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	public static final String STAR_RED = "star.red";

	public static final String STAR_GREEN = "star.green";

	public static final String STAR_ORANGE = "star.orange";

	public static final String USER_RED = "user.red";

	public static final String USER_BLUE = "user.blue";

	public static final String USER_GREEN = "user.green";

	public static final String LOGIN = "login";

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.admin.user";

	private ServiceTracker<LogService, LogService> logServiceTracker;

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public void editRole(final Role role)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new RoleEditorInput(role), RoleEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editUser(final User user)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new UserEditorInput(user), UserEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put(Activator.STAR_GREEN, ImageDescriptor.createFromURL(this.getBundle().getEntry("icons/star_green_16x16.png")));
		imageRegistry.put(Activator.STAR_RED, ImageDescriptor.createFromURL(this.getBundle().getEntry("icons/star_red_16x16.png")));
		imageRegistry.put(Activator.STAR_ORANGE, ImageDescriptor.createFromURL(this.getBundle().getEntry("icons/star_orange_16x16.png")));
		imageRegistry.put(Activator.USER_BLUE, ImageDescriptor.createFromURL(this.getBundle().getEntry("icons/user_blue_16.png")));
		imageRegistry.put(Activator.USER_GREEN, ImageDescriptor.createFromURL(this.getBundle().getEntry("icons/user_green_16.png")));
		imageRegistry.put(Activator.USER_RED, ImageDescriptor.createFromURL(this.getBundle().getEntry("icons/user_red_16.png")));
		imageRegistry.put(Activator.USER_RED, ImageDescriptor.createFromURL(this.getBundle().getEntry("icons/user_red_16.png")));
		imageRegistry.put(Activator.LOGIN, ImageDescriptor.createFromURL(this.getBundle().getEntry("icons/login_48.png")));
	}

	// public void addUser(User user)
	// {
	// try
	// {
	// PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new
	// UserEditorInput(user),
	// UserEditor.ID, true);
	// }
	// catch (PartInitException e)
	// {
	// e.printStackTrace();
	// }
	// }

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
			log.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
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
			log.log(LogService.LOG_INFO, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
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

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 * 
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(final String path)
	{
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, path);
	}
}
