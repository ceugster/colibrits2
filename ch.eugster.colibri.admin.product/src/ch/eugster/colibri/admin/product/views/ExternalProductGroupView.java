package ch.eugster.colibri.admin.product.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderIdService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class ExternalProductGroupView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.external.product.group.view";

	private TreeViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderIdService, ProviderIdService> providerTracker;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		final Tree tree = new Tree(parent, SWT.FULL_SELECTION | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setHeaderVisible(true);

		this.viewer = new TreeViewer(tree);
		this.viewer.setContentProvider(new ExternalProductGroupContentProvider());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.addDoubleClickListener(this);

		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.LEFT);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof ExternalProductGroup)
				{
					final ExternalProductGroup externalProductGroup = (ExternalProductGroup) object;
					cell.setText(externalProductGroup.getCode());
				}
				else if (object instanceof ProductGroupMapping)
				{
					final ProductGroupMapping mapping = (ProductGroupMapping) object;
					cell.setText(mapping.getProductGroup().getCode());
				}
			}
		});
		TreeColumn treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Code");

		treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.LEFT);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof ExternalProductGroup)
				{
					final ExternalProductGroup externalProductGroup = (ExternalProductGroup) object;
					cell.setText(externalProductGroup.getText());
				}
				else if (object instanceof ProductGroupMapping)
				{
					final ProductGroupMapping mapping = (ProductGroupMapping) object;
					cell.setText(mapping.getProductGroup().getName());
				}
			}
		});
		treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Bezeichnung");

		treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.LEFT);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof ExternalProductGroup)
				{
					final ExternalProductGroup externalProductGroup = (ExternalProductGroup) object;
					cell.setText(externalProductGroup.getAccount());
				}
				else if (object instanceof ProductGroupMapping)
				{
					final ProductGroupMapping mapping = (ProductGroupMapping) object;
					cell.setText(mapping.getProductGroup().getAccount());
				}
			}
		});
		treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Konto");

		treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.LEFT);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object element = cell.getElement();
				if (element instanceof ExternalProductGroup)
				{
					final ExternalProductGroup externalProductGroup = (ExternalProductGroup) element;
					Object[] services = providerTracker.getServices();
					for (Object object : services)
					{
						if (object instanceof ProviderIdService)
						{
							ProviderIdService service = (ProviderIdService) object;
							if (service.getProviderId().equals(externalProductGroup.getProvider()))
							{
								cell.setText(service.getProviderLabel());
							}
						}
					}
				}
			}
		});
		treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Warenbewirtschaftung");

		this.createContextMenu();

		this.getSite().setSelectionProvider(this.viewer);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				ExternalProductGroupView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						ExternalProductGroupView.this.viewer.setInput(service);
						ExternalProductGroupView.this.packColumns();
					}
				});
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.modifiedService(reference, service);
				ExternalProductGroupView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						ExternalProductGroupView.this.viewer.setInput(service);
						ExternalProductGroupView.this.packColumns();
					}
				});
			}

			@Override
			public void removedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.removedService(reference, service);
				ExternalProductGroupView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						if (ExternalProductGroupView.this.viewer.getContentProvider() != null)
						{
							ExternalProductGroupView.this.viewer.setInput(null);
							ExternalProductGroupView.this.packColumns();
						}
					}
				});
			}
		};
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.providerTracker.close();
		this.persistenceServiceTracker.close();

		EntityMediator.removeListener(ExternalProductGroup.class, this);
		EntityMediator.removeListener(ProductGroupMapping.class, this);
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof ExternalProductGroup)
		{
			Activator.getDefault().editExternalProductGroup((ExternalProductGroup) object);
		}
	}

	public Object getParent(final AbstractEntity entity)
	{
		if (entity instanceof ProductGroup)
		{
			return ((ProductGroup) entity).getProductGroupType();
		}
		else
		{
			return null;
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
		EntityMediator.addListener(ExternalProductGroup.class, this);
		EntityMediator.addListener(ProductGroupMapping.class, this);

		this.providerTracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderIdService.class, null);
		this.providerTracker.open();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof ProductGroupMapping)
		{
			UIJob job = new UIJob("Die Sicht der externen Warengruppen wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					viewer.refresh();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else
		{
			UIJob job = new UIJob("Die Sicht der externen Warengruppen wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					viewer.refresh();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (entity instanceof ExternalProductGroup)
		{
			UIJob job = new UIJob("Die Sicht der externen Warengruppen wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					Object service = persistenceServiceTracker.getService();
					if (service != null)
					{
						viewer.add(service, entity);
						packColumns();
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof ProductGroupMapping)
		{
			UIJob job = new UIJob("Die Sicht der externen Warengruppen wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					ProductGroupMapping mapping = (ProductGroupMapping) entity;
					if (mapping.getExternalProductGroup().getProductGroupMapping() == null)
					{
						mapping.getExternalProductGroup().setProductGroupMapping(mapping);
					}
					viewer.add(mapping.getExternalProductGroup(), mapping);
					packColumns();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (entity instanceof ExternalProductGroup)
		{
			UIJob job = new UIJob("Die Sicht der externen Warengruppen wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					viewer.refresh(entity);
					packColumns();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof ProductGroupMapping)
		{
			UIJob job = new UIJob("Die Sicht der externen Warengruppen wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					ProductGroupMapping mapping = (ProductGroupMapping) entity;
					viewer.refresh(mapping.getExternalProductGroup());
					packColumns();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
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
				final TreeColumn[] treeColumns = viewer.getTree().getColumns();
				for (final TreeColumn treeColumn : treeColumns)
				{
					treeColumn.pack();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}