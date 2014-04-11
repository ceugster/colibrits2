package ch.eugster.colibri.admin.periphery;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.periphery.editors.CustomerDisplayEditor;
import ch.eugster.colibri.admin.periphery.editors.CustomerDisplayEditorInput;
import ch.eugster.colibri.admin.periphery.editors.ReceiptPrinterEditor;
import ch.eugster.colibri.admin.periphery.editors.ReceiptPrinterEditorInput;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.queries.CustomerDisplaySettingsQuery;
import ch.eugster.colibri.persistence.queries.ReceiptPrinterSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.admin.periphery";

	public static final String IMAGE_PRINTER = "image.printer";

	public static final String IMAGE_DISPLAY = "image.display";

	public static final String IMAGE_PROPERTIES = "image.props";

	public static final String IMAGE_PROPERTIES_GROUP = "image.props";

	public static final String PROPERTY_WIZARD = "property.wizard";

	// The shared instance
	private static Activator plugin;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public void editCustomerDisplayPeriphery(final String componentName, final ServiceReference<CustomerDisplayService> reference)
	{
		final ServiceReference<PersistenceService> serverReference = Activator.getDefault().getBundle().getBundleContext()
				.getServiceReference(PersistenceService.class);
		if (serverReference != null)
		{
			final PersistenceService persistenceService = (PersistenceService) Activator.getDefault().getBundle()
					.getBundleContext().getService(serverReference);
			if (persistenceService != null)
			{
				final CustomerDisplaySettingsQuery query = (CustomerDisplaySettingsQuery) persistenceService
						.getServerService().getQuery(CustomerDisplaySettings.class);
				CustomerDisplaySettings periphery = query.findByComponentName(componentName);
				if (periphery == null)
				{
					final ServiceReference<CustomerDisplayService> customerDisplayReference = Activator.getDefault().getBundle().getBundleContext()
					.getServiceReference(CustomerDisplayService.class);
					if (customerDisplayReference != null)
					{
						final CustomerDisplayService customerDisplayService = (CustomerDisplayService) Activator.getDefault().getBundle()
						.getBundleContext().getService(customerDisplayReference);
						if (customerDisplayService != null)
						{
							periphery = customerDisplayService.createCustomerDisplaySettings();
							try
							{
								periphery = (CustomerDisplaySettings) persistenceService.getServerService().merge(periphery);
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
								IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
								ErrorDialog.openError(this.getWorkbench().getActiveWorkbenchWindow().getShell(), "Fehler", periphery.getName() + " konnte nicht gespeichert werden.", status);
							}
						}
					}
				}
				try
				{
					PlatformUI
							.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.openEditor(new CustomerDisplayEditorInput(periphery, reference), CustomerDisplayEditor.ID,
									true);
				}
				catch (final PartInitException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	public void editReceiptPrinterPeriphery(final String componentName, final ServiceReference<ReceiptPrinterService> reference)
	{
		final ServiceReference<PersistenceService> serverReference = Activator.getDefault().getBundle().getBundleContext()
				.getServiceReference(PersistenceService.class);
		if (serverReference != null)
		{
			final PersistenceService persistenceService = (PersistenceService) Activator.getDefault().getBundle()
					.getBundleContext().getService(serverReference);
			if (persistenceService != null)
			{
				final ReceiptPrinterSettingsQuery query = (ReceiptPrinterSettingsQuery) persistenceService
						.getServerService().getQuery(ReceiptPrinterSettings.class);
				ReceiptPrinterSettings periphery = query.findByComponentName(componentName);
				if (periphery == null)
				{
					final ReceiptPrinterService receiptPrinterService = (ReceiptPrinterService) Activator.getDefault().getBundle()
					.getBundleContext().getService(reference);
					if (receiptPrinterService != null)
					{
						periphery = receiptPrinterService.createReceiptPrinterSettings();
						try
						{
							periphery = (ReceiptPrinterSettings) persistenceService.getServerService().merge(periphery);
						} 
						catch (Exception e) 
						{
							e.printStackTrace();
							IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
							ErrorDialog.openError(this.getWorkbench().getActiveWorkbenchWindow().getShell(), "Fehler", periphery.getName() + " konnte nicht gespeichert werden.", status);
						}
					}
				}
				try
				{
					PlatformUI
							.getWorkbench()
							.getActiveWorkbenchWindow()
							.getActivePage()
							.openEditor(new ReceiptPrinterEditorInput(periphery, reference), ReceiptPrinterEditor.ID,
									true);
				}
				catch (final PartInitException e)
				{
					e.printStackTrace();
				}
			}
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
			log.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestartet");
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
			log.log(LogService.LOG_DEBUG, "Plugin " + Activator.PLUGIN_ID + " gestoppt");
		}

		this.logServiceTracker.close();

		Activator.plugin = null;
		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put(Activator.IMAGE_PRINTER,
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/printer.png")));
		imageRegistry.put(Activator.IMAGE_DISPLAY,
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/display.png")));
		imageRegistry.put(Activator.PROPERTY_WIZARD,
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/props_48x48.png")));
		imageRegistry.put(Activator.IMAGE_PROPERTIES_GROUP,
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/props_16x16.png")));
		imageRegistry.put(Activator.IMAGE_PROPERTIES,
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/props_16x16.png")));
		imageRegistry.put("layout",
				ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/layout_16.png")));
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
