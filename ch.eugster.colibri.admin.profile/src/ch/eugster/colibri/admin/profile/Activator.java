package ch.eugster.colibri.admin.profile;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.editors.configurable.ConfigurableEditor;
import ch.eugster.colibri.admin.profile.editors.configurable.ConfigurableEditorInput;
import ch.eugster.colibri.admin.profile.editors.profile.ProfileEditor;
import ch.eugster.colibri.admin.profile.editors.profile.ProfileEditorInput;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorInput;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Tab;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.admin.profile";

	private static Activator plugin;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public void editConfigurable(final Configurable configurable)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new ConfigurableEditorInput(configurable), ConfigurableEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editProfile(final Profile profile)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new ProfileEditorInput(profile), ProfileEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editTab(final Tab tab)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new TabEditorInput(tab), TabEditor.ID, true);
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
		imageRegistry.put("PROFILE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/profile_16.gif")));
		imageRegistry.put("CONFIGURABLE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/configurable_16.gif")));
		imageRegistry.put("TAB", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/tab_16.gif")));

		imageRegistry.put("display.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/display.png")));
		imageRegistry.put("numeric.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/numeric.png")));
		imageRegistry.put("article.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/article.png")));
		imageRegistry.put("function.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/function.png")));
		imageRegistry.put("login.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/login.png")));
		imageRegistry.put("wait.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/wait.gif")));
		imageRegistry.put("nowait.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/nowait.gif")));
		imageRegistry.put("sunshine.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/sunshine.png")));

		imageRegistry.put("metal-error.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/metal-error.gif")));
		imageRegistry.put("metal-inform.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/metal-inform.gif")));
		imageRegistry.put("metal-question.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/metal-question.gif")));
		imageRegistry.put("metal-warn.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/metal-warn.gif")));

		imageRegistry.put("pin-blue.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/pin-blue.gif")));
		imageRegistry.put("pin-yellow.gif", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/pin-yellow.gif")));
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
