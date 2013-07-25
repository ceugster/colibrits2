package ch.eugster.colibri.admin.layout.printer.menu;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.layout.printer.Activator;
import ch.eugster.colibri.admin.layout.printer.editors.PrintoutEditor;
import ch.eugster.colibri.admin.layout.printer.editors.PrintoutEditorInput;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PrintoutQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.print.service.PrintService;

public class EditPrintoutMenu extends ContributionItem
{

	public EditPrintoutMenu()
	{
	}

	public EditPrintoutMenu(final String id)
	{
		super(id);
	}

	@Override
	public void fill(final Menu menu, final int index)
	{
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection() instanceof StructuredSelection)
		{
			final StructuredSelection ssel = (StructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getActivePage().getSelection();
			if (ssel.getFirstElement() instanceof ServiceReference)
			{
				@SuppressWarnings("unchecked")
				final ServiceReference<ReceiptPrinterService> reference = (ServiceReference<ReceiptPrinterService>) ssel.getFirstElement();
				this.selectReceiptPrinterService(menu, index, reference);
			}
			else if (ssel.getFirstElement() instanceof Salespoint)
			{
				final Salespoint salespoint = (Salespoint) ssel.getFirstElement();
				if (salespoint.getReceiptPrinterSettings() != null)
				{
					this.selectPrintServices(menu, index, salespoint);
				}
			}
		}
	}

	private void addMenuItem(final Menu menu, final int index, final PrintService printService,
			final Salespoint salespoint)
	{
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText((printService).getMenuLabel());
		menuItem.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(
						Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
				tracker.open();
				final PersistenceService service = tracker.getService();
				if (service instanceof PersistenceService)
				{
					final ReceiptPrinterSettings receiptPrinterSettings = salespoint.getReceiptPrinterSettings()
							.getReceiptPrinterSettings();
					final PrintoutQuery query = (PrintoutQuery) service.getServerService().getQuery(Printout.class);
					Printout printout = query.findByPrintoutTypeAndSalespoint(printService.getLayoutTypeId(),
							salespoint);
					if (printout == null)
					{
						printout = Printout.newInstance(printService.getLayoutTypeId(), salespoint);
						Printout parent = query.findTemplate(printService.getLayoutTypeId(), receiptPrinterSettings);
						if (parent == null)
						{
							parent = Printout.newInstance(printService.getLayoutTypeId(), receiptPrinterSettings);
						}
						printout.setParent(parent);
						parent.addPrintout(printout);
					}
					try
					{
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.openEditor(new PrintoutEditorInput(printService, printout), PrintoutEditor.ID, true);
					}
					catch (final PartInitException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
	}

	private void addMenuItem(final Menu menu, final ReceiptPrinterService receiptPrinterService,
			final PrintService printService)
	{
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText((printService).getMenuLabel());
		menuItem.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(
						Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
				tracker.open();
				final PersistenceService service = tracker.getService();
				if (service instanceof PersistenceService)
				{
					final PrintoutQuery query = (PrintoutQuery) service.getServerService().getQuery(Printout.class);
					Printout printout = query.findTemplate(printService.getLayoutTypeId(), receiptPrinterService.getReceiptPrinterSettings());
					if (printout == null)
					{
						printout = Printout.newInstance(printService.getLayoutType(receiptPrinterService).getId(),
								receiptPrinterService.getReceiptPrinterSettings());
					}
					try
					{
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.openEditor(new PrintoutEditorInput(printService, printout), PrintoutEditor.ID, true);
					}
					catch (final PartInitException ex)
					{
						ex.printStackTrace();
					}
				}
				tracker.close();
			}
		});
	}

	private void selectPrintServices(final Menu menu, final int index, final ReceiptPrinterService receiptPrinterService)
	{
		// final ReceiptPrinterSettings receiptPrinterSettings =
		// receiptPrinterService.getReceiptPrinterSettings();
		// if (receiptPrinterSettings != null)
		// {
		final ServiceTracker<PrintService, PrintService> tracker = new ServiceTracker<PrintService, PrintService>(Activator.getDefault().getBundle().getBundleContext(),
				PrintService.class, null);
		tracker.open();

		final ServiceReference<PrintService>[] references = tracker.getServiceReferences();
		if (references instanceof ServiceReference[])
		{
			for (final ServiceReference<PrintService> reference : references)
			{
				final PrintService printService = (PrintService) tracker.getService(reference);
				if (printService instanceof PrintService)
				{
					this.addMenuItem(menu, receiptPrinterService, printService);
				}
			}
		}
		tracker.close();
		// }
	}

	private void selectPrintServices(final Menu menu, final int index, final Salespoint salespoint)
	{
		final ServiceTracker<PrintService, PrintService> tracker = new ServiceTracker<PrintService, PrintService>(Activator.getDefault().getBundle().getBundleContext(),
				PrintService.class, null);
		tracker.open();

		final ServiceReference<PrintService>[] references = tracker.getServiceReferences();
		if (references instanceof ServiceReference[])
		{
			for (final ServiceReference<PrintService> reference : references)
			{
				final PrintService printService = (PrintService) tracker.getService(reference);
				if (printService instanceof PrintService)
				{
					this.addMenuItem(menu, index, printService, salespoint);
				}
			}
		}
		tracker.close();
	}

	private void selectReceiptPrinterService(final Menu menu, final int index, final ServiceReference<ReceiptPrinterService> reference)
	{
		final ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptServiceTracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle()
				.getBundleContext(), ReceiptPrinterService.class, null);
		receiptServiceTracker.open();
		final ReceiptPrinterService receiptPrinterService = (ReceiptPrinterService) receiptServiceTracker
				.getService(reference);
		if (receiptPrinterService != null)
		{
			this.selectPrintServices(menu, index, receiptPrinterService);
		}
		receiptServiceTracker.close();
	}
}
