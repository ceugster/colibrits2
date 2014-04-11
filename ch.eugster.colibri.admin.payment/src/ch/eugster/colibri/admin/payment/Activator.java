package ch.eugster.colibri.admin.payment;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.payment.editors.MoneyEditor;
import ch.eugster.colibri.admin.payment.editors.MoneyEditorInput;
import ch.eugster.colibri.admin.payment.editors.PaymentEditor;
import ch.eugster.colibri.admin.payment.editors.PaymentEditorInput;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.admin.payment";

	// The shared instance
	private static Activator plugin;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public void editMoney(final Money money)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(new MoneyEditorInput(money), MoneyEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editPaymentType(final PaymentType paymentType)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new PaymentEditorInput(paymentType), PaymentEditor.ID, true);
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
		imageRegistry.put("AE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/american_express_16.png")));
		imageRegistry.put("CREDITCARD", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/creditcard_16.png")));
		imageRegistry.put("DINERS", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/diners_16.gif")));
		imageRegistry.put("EC", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/ec_16.png")));
		imageRegistry.put("GUTSCHEIN", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/gutschein_16.gif")));
		imageRegistry.put("MAESTRO", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/maestro.gif")));
		imageRegistry.put("MASTERCARD", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/mastercard_16.gif")));
		imageRegistry.put("MONEY_BLUE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_blue_16.png")));
		imageRegistry.put("MONEY_GREEN", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_green_16.png")));
		imageRegistry.put("MONEY_RED", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_red_16.png")));
		imageRegistry.put("MONEY_VIOLET", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_violet_16.png")));
		imageRegistry.put("MONEY_YELLOW", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_yellow_16.png")));
		imageRegistry.put("POSTCARD", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/postcard_16.gif")));
		imageRegistry.put("SBVV", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/sbvv_16.png")));
		imageRegistry.put("SBVV_GROSS", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/sbvv_16.gif")));
		imageRegistry.put("VISA", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/visa_16.png")));
		imageRegistry.put("DEBITCARD", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/debitcard.gif")));
	}

	@Override
	protected void loadDialogSettings()
	{
		super.loadDialogSettings();
	}

	@Override
	protected void saveDialogSettings()
	{
		super.saveDialogSettings();
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
