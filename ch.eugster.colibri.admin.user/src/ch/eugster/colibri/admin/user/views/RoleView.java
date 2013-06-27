package ch.eugster.colibri.admin.user.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.admin.user.Activator;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class RoleView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.role.view";

	private TableViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Table table = new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);

		this.viewer = new TableViewer(table);
		this.viewer.setContentProvider(new RoleContentProvider());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });

		final TableViewerColumn tableViewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		tableViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof Role)
				{
					final Role role = (Role) object;
					cell.setText(role.getName());
					if (role.getId().equals(Long.valueOf(1l)))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get(Activator.STAR_RED));
					}
					else
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get(Activator.STAR_ORANGE));
					}
				}
			}
		});
		final TableColumn tableColumn = tableViewerColumn.getColumn();
		tableColumn.setResizable(true);
		tableColumn.setText("Bezeichnung");

		this.viewer.addDoubleClickListener(this);

		this.createContextMenu();

		this.getSite().setSelectionProvider(this.viewer);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				final UIJob job = new UIJob("Lade Rollen...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						RoleView.this.viewer.setInput(service);
						RoleView.this.packColumns();
						return Status.OK_STATUS;
					}

				};
				// job.setUser(true);
				job.schedule();
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.modifiedService(reference, service);
				final UIJob job = new UIJob("Lade Rollen...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						RoleView.this.viewer.setInput(service);
						RoleView.this.packColumns();
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
			}

			@Override
			public void removedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.removedService(reference, service);
				final UIJob job = new UIJob("Lade Rollen...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (RoleView.this.viewer.getContentProvider() != null)
						{
							RoleView.this.viewer.setInput(RoleView.this.viewer);
							RoleView.this.packColumns();
						}
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
			}
		};
		this.persistenceServiceTracker.open();
		EntityMediator.addListener(Role.class, this);
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Role.class, this);
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof Role)
		{
			Activator.getDefault().editRole((Role) object);
		}
		else
		{
			return;
		}
	}

	public TableViewer getViewer()
	{
		return this.viewer;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
	};

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		this.viewer.refresh(entity);
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		this.viewer.add(entity);
		// viewer.refresh();
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		this.viewer.refresh(entity);
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

	private void packColumns()
	{
		UIJob job = new UIJob("update columns width")
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				final TableColumn[] columns = viewer.getTable().getColumns();
				for (final TableColumn column : columns)
				{
					column.pack();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}