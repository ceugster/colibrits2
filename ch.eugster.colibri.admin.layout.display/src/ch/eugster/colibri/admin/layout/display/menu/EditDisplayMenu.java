package ch.eugster.colibri.admin.layout.display.menu;

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

import ch.eugster.colibri.admin.layout.display.Activator;
import ch.eugster.colibri.admin.layout.display.editors.DisplayEditor;
import ch.eugster.colibri.admin.layout.display.editors.DisplayEditorInput;
import ch.eugster.colibri.display.service.DisplayService;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.DisplayQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class EditDisplayMenu extends ContributionItem
{

	public EditDisplayMenu()
	{
	}

	public EditDisplayMenu(final String id)
	{
		super(id);
	}

	@Override
	public void fill(final Menu menu, final int index)
	{
		if (PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getSelection() instanceof StructuredSelection)
		{
			final StructuredSelection ssel = (StructuredSelection) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.getSelection();
			if (ssel.getFirstElement() instanceof ServiceReference)
			{
				@SuppressWarnings("unchecked")
				final ServiceReference<CustomerDisplayService> reference = (ServiceReference<CustomerDisplayService>) ssel.getFirstElement();
				this.selectCustomerDisplayService(menu, index, reference);
			}
			else if (ssel.getFirstElement() instanceof Salespoint)
			{
				final Salespoint salespoint = (Salespoint) ssel.getFirstElement();
				if (salespoint.getCustomerDisplaySettings() != null)
				{
					this.selectDisplayServices(menu, index, salespoint);
				}
			}
		}
	}

	private void addMenuItem(final Menu menu, final CustomerDisplaySettings customerDisplaySettings, final DisplayService displayService)
	{
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText((displayService).getMenuLabel());
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
				final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
				tracker.open();
				final PersistenceService service = tracker.getService();
				if (service instanceof PersistenceService)
				{
					final DisplayQuery query = (DisplayQuery) service.getServerService().getQuery(Display.class);
					Display display = query.findTemplate(displayService.getContext().getProperties().get("component.name").toString(), customerDisplaySettings);
					if (display == null)
					{
						final String displayName = displayService.getContext().getProperties().get("component.name").toString();
						display = Display.newInstance(displayName, customerDisplaySettings);
					}
					try
					{
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.openEditor(new DisplayEditorInput(displayService, display), DisplayEditor.ID, true);
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

	private void addMenuItem(final Menu menu, final int index, final DisplayService displayService, final Salespoint salespoint)
	{
		final MenuItem menuItem = new MenuItem(menu, SWT.PUSH);
		menuItem.setText((displayService).getMenuLabel());
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
				final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
				tracker.open();
				final PersistenceService service = tracker.getService();
				if (service instanceof PersistenceService)
				{
					final CustomerDisplaySettings customerDisplaySettings = salespoint.getCustomerDisplaySettings().getCustomerDisplaySettings();
					final DisplayQuery query = (DisplayQuery) service.getServerService().getQuery(Display.class);
					Display display = query.findByDisplayTypeAndSalespoint(displayService.getLayoutType().getId(), salespoint);
					if (display == null)
					{
						display = Display.newInstance(displayService.getLayoutType().getId(), salespoint);
						Display parent = query.findTemplate(displayService.getLayoutType().getId(), customerDisplaySettings);
						if (parent == null)
						{
							parent = Display.newInstance(displayService.getContext().getProperties().get("component.name").toString(), customerDisplaySettings);
						}
						display.setParent(parent);
						parent.addPrintout(display);
					}
					try
					{
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
								.openEditor(new DisplayEditorInput(displayService, display), DisplayEditor.ID, true);
					}
					catch (final PartInitException ex)
					{
						ex.printStackTrace();
					}
				}
			}
		});
	}

	private void selectCustomerDisplayService(final Menu menu, final int index, final ServiceReference<CustomerDisplayService> reference)
	{
		final ServiceTracker<CustomerDisplayService, CustomerDisplayService> customerDisplayTracker = new ServiceTracker<CustomerDisplayService, CustomerDisplayService>(Activator.getDefault().getBundle().getBundleContext(),
				CustomerDisplayService.class, null);
		customerDisplayTracker.open();
		final CustomerDisplayService customerDisplayService = (CustomerDisplayService) customerDisplayTracker.getService(reference);
		if (customerDisplayService != null)
		{
			this.selectDisplayServices(menu, index, customerDisplayService);
		}
		customerDisplayTracker.close();
	}

	private void selectDisplayServices(final Menu menu, final int index, final CustomerDisplayService customerDisplayService)
	{
		final CustomerDisplaySettings customerDisplaySettings = customerDisplayService.getCustomerDisplaySettings();
		if (customerDisplaySettings != null)
		{
			final ServiceTracker<DisplayService, DisplayService> tracker = new ServiceTracker<DisplayService, DisplayService>(Activator.getDefault().getBundle().getBundleContext(),
					DisplayService.class, null);
			tracker.open();

			final ServiceReference<DisplayService>[] references = tracker.getServiceReferences();
			if (references instanceof ServiceReference[])
			{
				for (final ServiceReference<DisplayService> reference : references)
				{
					final DisplayService displayService = (DisplayService) tracker.getService(reference);
					if (displayService instanceof DisplayService)
					{
						this.addMenuItem(menu, customerDisplaySettings, displayService);
					}
				}
			}
			tracker.close();
		}
	}

	private void selectDisplayServices(final Menu menu, final int index, final Salespoint salespoint)
	{
		final ServiceTracker<DisplayService, DisplayService> tracker = new ServiceTracker<DisplayService, DisplayService>(Activator.getDefault().getBundle().getBundleContext(), DisplayService.class,
				null);
		tracker.open();

		final ServiceReference<DisplayService>[] references = tracker.getServiceReferences();
		if (references instanceof ServiceReference[])
		{
			for (final ServiceReference<DisplayService> reference : references)
			{
				final DisplayService displayService = tracker.getService(reference);
				if (displayService instanceof DisplayService)
				{
					this.addMenuItem(menu, index, displayService, salespoint);
				}
			}
		}
		tracker.close();
	}
}
