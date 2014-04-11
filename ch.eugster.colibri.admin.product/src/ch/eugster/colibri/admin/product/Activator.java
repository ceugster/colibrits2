package ch.eugster.colibri.admin.product;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.product.editors.ExternalProductGroupEditor;
import ch.eugster.colibri.admin.product.editors.ExternalProductGroupEditorInput;
import ch.eugster.colibri.admin.product.editors.ProductEditor;
import ch.eugster.colibri.admin.product.editors.ProductEditorInput;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroup;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.admin.product";

	// The shared instance
	private static Activator plugin;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public void editProductGroup(final ProductGroup productGroup)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new ProductEditorInput(productGroup), ProductEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editExternalProductGroup(final ExternalProductGroup productGroup)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new ExternalProductGroupEditorInput(productGroup), ExternalProductGroupEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
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
			log.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestartet.");
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
			log.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestoppt.");
		}

		this.logServiceTracker.close();

		Activator.plugin = null;
		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put("BOOKS", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/books.gif")));
		imageRegistry.put("BOOK_OPEN_BLUE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/book_open_blue_16.png")));
		imageRegistry.put("BOOK_OPEN_RED", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/book_open_red_16.png")));
		imageRegistry.put("BOOK_OPEN_GREEN", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/book_open_green_16.png")));
		imageRegistry.put("BOOK_CLOSED", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/book_closed_16.png")));
		imageRegistry.put("PLUS", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/plus_16.png")));
		imageRegistry.put("MINUS", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/minus_16.png")));
		imageRegistry.put("BANK", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/bank_16.png")));
		imageRegistry.put("MONEY_GREEN", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_green_16.png")));
		imageRegistry.put("MONEY_BLUE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_blue_16.png")));
		imageRegistry.put("MONEY_YELLOW", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_yellow_16.png")));
		imageRegistry.put("MONEY_RED", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_red_16.png")));
		imageRegistry.put("MONEY_VIOLET", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_violet_16.png")));
		imageRegistry.put("ELSE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/else_16.gif")));
		imageRegistry.put("SBVV", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/sbvv_16.png")));
		imageRegistry.put("BON", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/bon_16.gif")));
		imageRegistry.put("INVOICE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/invoice_16.gif")));
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
