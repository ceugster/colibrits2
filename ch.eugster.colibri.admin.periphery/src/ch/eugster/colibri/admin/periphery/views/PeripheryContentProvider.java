/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.views;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.queries.PrintoutQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.print.service.PrintService;

public class PeripheryContentProvider implements ITreeContentProvider
{
	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptPrinterServiceTracker;

	private ServiceTracker<PrintService, PrintService> printServiceTracker;

	public PeripheryContentProvider()
	{
		this.receiptPrinterServiceTracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle().getBundleContext(),
				ReceiptPrinterService.class, null);
		this.receiptPrinterServiceTracker.open();
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
		this.printServiceTracker = new ServiceTracker<PrintService, PrintService>(Activator.getDefault().getBundle().getBundleContext(), PrintService.class, null);
		this.printServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.printServiceTracker.close();
		this.persistenceServiceTracker.close();
		this.receiptPrinterServiceTracker.close();
	}

	@Override
	public Object[] getChildren(final Object parent)
	{
		if (parent instanceof PeripheryGroup)
		{
			final PeripheryGroup peripheryGroup = (PeripheryGroup) parent;
			try
			{
				final ServiceReference<?>[] references = Activator.getDefault().getBundle().getBundleContext()
						.getServiceReferences(peripheryGroup.getServiceName(), null);
				if (references instanceof ServiceReference[])
				{
					return references;
				}
			}
			catch (final InvalidSyntaxException e)
			{
				e.printStackTrace();
			}
			return new ServiceReference[0];
		}
		else if (parent instanceof ServiceReference)
		{
			final Collection<Printout> printouts = new ArrayList<Printout>();
			@SuppressWarnings("unchecked")
			final ServiceReference<ReceiptPrinterService> receiptPrinterServiceReference = (ServiceReference<ReceiptPrinterService>) parent;
			final ReceiptPrinterService receiptPrinterService = (ReceiptPrinterService) this.receiptPrinterServiceTracker
					.getService(receiptPrinterServiceReference);
			if (receiptPrinterService != null)
			{
				final ServiceReference<PrintService>[] references = this.printServiceTracker.getServiceReferences();
				for (final ServiceReference<PrintService> reference : references)
				{
					final PrintService printService = (PrintService) this.printServiceTracker.getService(reference);
					if (printService != null)
					{
						final PersistenceService service = (PersistenceService) this.persistenceServiceTracker.getService();
						if (service != null)
						{
							final PrintoutQuery query = (PrintoutQuery) service.getServerService().getQuery(Printout.class);
							Printout printout = query.findTemplate(printService.getLayoutType(receiptPrinterService).getId(),
									receiptPrinterService.getReceiptPrinterSettings());
							if (printout == null)
							{
								printout = Printout.newInstance(printService.getLayoutType(receiptPrinterService).getId(),
										receiptPrinterService.getReceiptPrinterSettings());
							}
							printouts.add(printout);
						}
					}
				}
			}
			return new Printout[0];
		}
		return PeripheryGroup.values();
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object element)
	{
		if (element instanceof ServiceReference)
		{
			final ServiceReference<?> reference = (ServiceReference<?>) element;
			final Integer group = (Integer) reference.getProperty("component.group");
			if (group instanceof Integer)
			{
				return PeripheryGroup.values()[group.intValue()];
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		if (parent instanceof PeripheryGroup)
		{
			final PeripheryGroup peripheryGroup = (PeripheryGroup) parent;
			try
			{
				final ServiceReference<?>[] references = Activator.getDefault().getBundle().getBundleContext()
						.getServiceReferences(peripheryGroup.getServiceName(), null);
				if (references instanceof ServiceReference[])
				{
					return references.length > 0;
				}
			}
			catch (final InvalidSyntaxException e)
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}
}
