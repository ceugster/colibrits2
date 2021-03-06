package ch.eugster.colibri.admin.periphery.views;

import java.text.NumberFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.admin.periphery.editors.DisplayEditor;
import ch.eugster.colibri.admin.periphery.editors.DisplayEditorInput;
import ch.eugster.colibri.admin.periphery.editors.PrintoutEditor;
import ch.eugster.colibri.admin.periphery.editors.PrintoutEditorInput;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.display.service.DisplayService;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.print.service.PrintService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class PeripheryView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.periphery.view";

	public static final NumberFormat nf = NumberFormat.getCurrencyInstance();

	private TreeViewer viewer;

	private ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptPrinterServiceTracker;

	private ServiceTracker<CustomerDisplayService, CustomerDisplayService> customerDisplayServiceTracker;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{

		final Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setHeaderVisible(false);

		this.viewer = new TreeViewer(tree);
		this.viewer.setContentProvider(new PeripheryContentProvider());
		this.viewer.setLabelProvider(new PeripheryLabelProvider(this));
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.setInput(this.viewer);

		this.viewer.addDoubleClickListener(this);
		this.createContextMenu();

		this.getSite().setSelectionProvider(this.viewer);

		this.receiptPrinterServiceTracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle().getBundleContext(),
				ReceiptPrinterService.class, null)
		{
			@Override
			public ReceiptPrinterService addingService(final ServiceReference<ReceiptPrinterService> reference)
			{
				final UIJob job = new UIJob("")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						ISelection sel = PeripheryView.this.viewer.getSelection();
						PeripheryView.this.viewer.setInput(PeripheryView.this.viewer);
						PeripheryView.this.viewer.setSelection(sel);
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
				return super.addingService(reference);
			}

			@Override
			public void modifiedService(final ServiceReference<ReceiptPrinterService> reference, final ReceiptPrinterService service)
			{
				final UIJob job = new UIJob("")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						ISelection sel = PeripheryView.this.viewer.getSelection();
						PeripheryView.this.viewer.setInput(PeripheryView.this.viewer);
						PeripheryView.this.viewer.setSelection(sel);
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
				super.modifiedService(reference, service);
			}

			@Override
			public void removedService(final ServiceReference<ReceiptPrinterService> reference, final ReceiptPrinterService service)
			{
				final UIJob job = new UIJob("")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (PeripheryView.this.viewer.getControl() != null && !PeripheryView.this.viewer.getControl().isDisposed())
						{
							PeripheryView.this.viewer.setInput(PeripheryView.this.viewer);
						}
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
				super.removedService(reference, service);
			}
		};
		this.receiptPrinterServiceTracker.open();

		this.customerDisplayServiceTracker = new ServiceTracker<CustomerDisplayService, CustomerDisplayService>(Activator.getDefault().getBundle().getBundleContext(),
				CustomerDisplayService.class, null)
		{
			@Override
			public CustomerDisplayService addingService(final ServiceReference<CustomerDisplayService> reference)
			{
				final UIJob job = new UIJob("")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						ISelection sel = PeripheryView.this.viewer.getSelection();
						PeripheryView.this.viewer.setInput(PeripheryView.this.viewer);
						PeripheryView.this.viewer.setSelection(sel);
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
				return super.addingService(reference);
			}

			@Override
			public void modifiedService(final ServiceReference<CustomerDisplayService> reference, final CustomerDisplayService service)
			{
				final UIJob job = new UIJob("")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						ISelection sel = PeripheryView.this.viewer.getSelection();
						PeripheryView.this.viewer.setInput(PeripheryView.this.viewer);
						PeripheryView.this.viewer.setSelection(sel);
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
				super.modifiedService(reference, service);
			}

			@Override
			public void removedService(final ServiceReference<CustomerDisplayService> reference, final CustomerDisplayService service)
			{
				final UIJob job = new UIJob("")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (PeripheryView.this.viewer.getControl() != null && !PeripheryView.this.viewer.getControl().isDisposed())
						{
							PeripheryView.this.viewer.setInput(PeripheryView.this.viewer);
						}
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
				super.removedService(reference, service);
			}
		};
		this.customerDisplayServiceTracker.open();

		EntityMediator.addListener(CustomerDisplaySettings.class, this);
		EntityMediator.addListener(ReceiptPrinterSettings.class, this);
	}

	@Override
	public void dispose()
	{
		receiptPrinterServiceTracker.close();
		customerDisplayServiceTracker.close();
		EntityMediator.removeListener(CustomerDisplaySettings.class, this);
		EntityMediator.removeListener(ReceiptPrinterSettings.class, this);
		super.dispose();
	}

	public void doubleClick(final DoubleClickEvent event)
	{
		final StructuredSelection ssel = (StructuredSelection) event.getSelection();
		if (ssel.getFirstElement() instanceof PeripheryGroup)
		{
			PeripheryGroup peripheryGroup = (PeripheryGroup) ssel.getFirstElement();
			if (this.viewer.getExpandedState(peripheryGroup))
			{
				this.viewer.collapseToLevel(peripheryGroup, TreeViewer.ALL_LEVELS);
			}
			else
			{
				this.viewer.expandToLevel(ssel.getFirstElement(), 1);
			}
		}
		else if (ssel.getFirstElement() instanceof ServiceReference)
		{
			final ServiceReference<?> ref = (ServiceReference<?>) ssel.getFirstElement();
			final Integer group = (Integer) ref.getProperty("custom.group");
			if (group instanceof Integer)
			{
				final int peripheryGroup = (group).intValue();
				final String componentName = (String) ref.getProperty("component.name");
				if (peripheryGroup == 0)
				{
					@SuppressWarnings("unchecked")
					ServiceReference<ReceiptPrinterService> reference = (ServiceReference<ReceiptPrinterService>) ref;
					Activator.getDefault().editReceiptPrinterPeriphery(componentName, reference);
				}
				else if (peripheryGroup == 1)
				{
					@SuppressWarnings("unchecked")
					ServiceReference<CustomerDisplayService> reference = (ServiceReference<CustomerDisplayService>) ref;
					Activator.getDefault().editCustomerDisplayPeriphery(componentName, reference);
				}
			}
		}
		else if (ssel.getFirstElement() instanceof Display)
		{
			Display display = (Display) ssel.getFirstElement();
			display = refresh(display);
			try
			{
				DisplayService displayService = getDisplayService(display);
				if (displayService != null)
				{
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new DisplayEditorInput(displayService, display), DisplayEditor.ID, true);
				}
			}
			catch (final PartInitException ex)
			{
				ex.printStackTrace();
			}
		}
		else if (ssel.getFirstElement() instanceof Printout)
		{
			Printout printout = (Printout) ssel.getFirstElement();
			printout = refresh(printout);
			try
			{
				PrintService service = getPrintService(printout);
				if (service != null)
				{
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.openEditor(new PrintoutEditorInput(service, printout), PrintoutEditor.ID, true);
				}
			}
			catch (final PartInitException ex)
			{
				ex.printStackTrace();
			}
		}
//		else if (ssel.getFirstElement() instanceof ReceiptPrinterSettings)
//		{
//			ReceiptPrinterSettings receiptPrinterSettings = (ReceiptPrinterSettings) ssel.getFirstElement();
//			final ServiceTracker<PrintService, PrintService> printServiceTracker = new ServiceTracker<PrintService, PrintService>(Activator.getDefault().getBundle().getBundleContext(),
//					PrintService.class, null);
//			printServiceTracker.open();
//			try
//			{
//				PrintService[] printServices = printServiceTracker.getServices(new PrintService[0]);
//				final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
//				tracker.open();
//				final PersistenceService service = tracker.getService();
//				if (service instanceof PersistenceService)
//				{
//					for (PrintService printService : printServices)
//					{
//						final PrintoutQuery query = (PrintoutQuery) service.getServerService().getQuery(Printout.class);
//						Printout printout = query.findTemplate(printService.getLayoutTypeId(), receiptPrinterSettings);
//						if (printout == null)
//						{
//							printout = Printout.newInstance(printService.getLayoutTypeId(), receiptPrinterSettings);
//						}
//						try
//						{
//							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
//									.openEditor(new PrintoutEditorInput(printService, printout), PrintoutEditor.ID, true);
//						}
//						catch (final PartInitException ex)
//						{
//							ex.printStackTrace();
//						}
//					}
//				}
//			}
//			finally
//			{
//				printServiceTracker.close();
//			}
//		}
	}

	private Display refresh(Display display)
	{
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = tracker.getService();
			return (Display) service.getServerService().refresh(display);
		}
		finally
		{
			tracker.close();
		}
	}

	private Printout refresh(Printout printout)
	{
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = tracker.getService();
			return (Printout) service.getServerService().refresh(printout);
		}
		finally
		{
			tracker.close();
		}
	}

	public DisplayService getDisplayService(Display display)
	{
		ServiceTracker<DisplayService, DisplayService> tracker = new ServiceTracker<DisplayService, DisplayService>(Activator.getDefault().getBundle().getBundleContext(), DisplayService.class, null);
		tracker.open();
		try
		{
			DisplayService[] services = tracker.getServices(new DisplayService[0]);
			for (DisplayService service : services)
			{
				String componentName = display.getCustomerDisplaySettings().getComponentName();
				String layoutTypeId = service.getLayoutType(componentName).getId();
				if (layoutTypeId.equals(display.getDisplayType()))
				{
					return service;
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return null;
	}
	
	public PrintService getPrintService(Printout printout)
	{
		ServiceTracker<PrintService, PrintService> tracker = new ServiceTracker<PrintService, PrintService>(Activator.getDefault().getBundle().getBundleContext(), PrintService.class, null);
		tracker.open();
		try
		{
			PrintService[] services = tracker.getServices(new PrintService[0]);
			for (PrintService service : services)
			{
				if (service.getLayoutTypeId().equals(printout.getPrintoutType()))
				{
					return service;
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return null;
	}
	
	public TreeViewer getViewer()
	{
		return this.viewer;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		this.viewer.getControl().setFocus();
	}

	private void createContextMenu()
	{
		final MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		final Menu menu = menuManager.createContextMenu(this.viewer.getControl());
		this.viewer.getControl().setMenu(menu);

		this.getSite().registerContextMenu(menuManager, this.viewer);
	}
}