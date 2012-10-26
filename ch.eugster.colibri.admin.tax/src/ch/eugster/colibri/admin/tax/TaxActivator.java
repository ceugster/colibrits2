package ch.eugster.colibri.admin.tax;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.tax.editors.CurrentTaxEditor;
import ch.eugster.colibri.admin.tax.editors.CurrentTaxEditorInput;
import ch.eugster.colibri.admin.tax.editors.TaxEditor;
import ch.eugster.colibri.admin.tax.editors.TaxEditorInput;
import ch.eugster.colibri.admin.tax.editors.TaxRateEditor;
import ch.eugster.colibri.admin.tax.editors.TaxRateEditorInput;
import ch.eugster.colibri.admin.tax.editors.TaxTypeEditor;
import ch.eugster.colibri.admin.tax.editors.TaxTypeEditorInput;
import ch.eugster.colibri.admin.tax.views.TaxView;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;

/**
 * The activator class controls the plug-in life cycle
 */
public class TaxActivator extends AbstractUIPlugin
{
	public static final String PATTERN_PERCENTAGE = "##0.0##";

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.admin.tax";

	// The shared instance
	private static TaxActivator plugin;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	/**
	 * The constructor
	 */
	public TaxActivator()
	{
	}

	public void editCurrentTax(final CurrentTax currentTax)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new CurrentTaxEditorInput(currentTax), CurrentTaxEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editTax(final TaxView view, final Tax tax)
	{
		try
		{
			final TaxEditorInput input = new TaxEditorInput(tax, view.getCurrentMode());
			view.addModeSelectionListener(input);
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, TaxEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editTaxRate(final TaxRate taxRate)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new TaxRateEditorInput(taxRate), TaxRateEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editTaxType(final TaxType taxType)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new TaxTypeEditorInput(taxType), TaxTypeEditor.ID, true);
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
		TaxActivator.plugin = this;

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		final LogService log = (LogService) this.logServiceTracker.getService();
		if (log != null)
		{
			log.log(LogService.LOG_INFO, "Plugin " + TaxActivator.PLUGIN_ID + " gestartet.");
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
			log.log(LogService.LOG_INFO, "Plugin " + TaxActivator.PLUGIN_ID + " gestoppt.");
		}

		this.logServiceTracker.close();

		TaxActivator.plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static TaxActivator getDefault()
	{
		return TaxActivator.plugin;
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
		return AbstractUIPlugin.imageDescriptorFromPlugin(TaxActivator.PLUGIN_ID, path);
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry
				.put("dot.green", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/dot-green.png")));
		imageRegistry.put("dot.transparent",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/dot-transparent.png")));
	}

}
