package ch.eugster.colibri.admin.salespoint;

import java.util.Collection;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.salespoint.editors.SalespointEditor;
import ch.eugster.colibri.admin.salespoint.editors.SalespointEditorInput;
import ch.eugster.colibri.admin.salespoint.editors.StockEditor;
import ch.eugster.colibri.admin.salespoint.editors.StockEditorInput;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	// The plug-in ID
	public static final String PLUGIN_ID = "ch.eugster.colibri.admin.salespoint";

	// The shared instance
	private static Activator plugin;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public void editSalespoint(final Salespoint salespoint)
	{
		try
		{
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new SalespointEditorInput(salespoint), SalespointEditor.ID, true);
		}
		catch (final PartInitException e)
		{
			e.printStackTrace();
		}
	}

	public void editStock(final Stock stock)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final PaymentTypeQuery query = (PaymentTypeQuery) persistenceService.getServerService().getQuery(PaymentType.class);
			final Collection<PaymentType> paymentTypes = query.selectByGroup(PaymentTypeGroup.CASH);
			final Salespoint salespoint = stock.getSalespoint();
			final Collection<Stock> stocks = salespoint.getStocks();
			for (final Stock stk : stocks)
			{
				if (!stk.isDeleted())
				{
					if (!stk.getPaymentType().equals(stock.getPaymentType()))
					{
						paymentTypes.remove(stk.getPaymentType());
					}
				}
			}
			if (paymentTypes.size() > 0)
			{
				try
				{
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.openEditor(new StockEditorInput(stock), StockEditor.ID, true);
				}
				catch (final PartInitException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put("salespoint_this.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/salespoint_this.png")));
		imageRegistry.put("salespoint_in_use.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/salespoint_in_use.png")));
		imageRegistry.put("salespoint.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/salespoint.png")));
		imageRegistry.put("money_green_16.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_green_16.png")));
		imageRegistry.put("money_violet_16.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/money_violet_16.png")));
		imageRegistry.put("kasse.png", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/kasse.png")));
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

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(context, PersistenceService.class, null);
		this.persistenceServiceTracker.open();

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

		this.persistenceServiceTracker.close();

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
