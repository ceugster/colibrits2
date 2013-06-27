package ch.eugster.colibri.admin.periphery.views;

import java.text.NumberFormat;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
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
import org.osgi.framework.ServiceReference;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class PeripheryView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.periphery.view";

	public static final NumberFormat nf = NumberFormat.getCurrencyInstance();

	private TreeViewer viewer;

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
		this.viewer.setLabelProvider(new PeripheryLabelProvider());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.setInput(this.viewer);

		this.viewer.addDoubleClickListener(this);
		this.createContextMenu();

		this.getSite().setSelectionProvider(this.viewer);
		EntityMediator.addListener(CustomerDisplaySettings.class, this);
		EntityMediator.addListener(ReceiptPrinterSettings.class, this);
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(CustomerDisplaySettings.class, this);
		EntityMediator.removeListener(ReceiptPrinterSettings.class, this);
		super.dispose();
	}

	public void doubleClick(final DoubleClickEvent event)
	{
		final StructuredSelection ssel = (StructuredSelection) event.getSelection();
		if (ssel.getFirstElement() instanceof ServiceReference)
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
		else if (ssel.getFirstElement() instanceof PeripheryGroup)
		{
			this.viewer.expandToLevel(ssel.getFirstElement(), 1);
		}
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