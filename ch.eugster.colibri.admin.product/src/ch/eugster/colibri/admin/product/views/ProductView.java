package ch.eugster.colibri.admin.product.views;

import java.text.NumberFormat;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeViewerListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TreeExpansionEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
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
import ch.eugster.colibri.admin.product.dnd.ProductGroupDragListener;
import ch.eugster.colibri.admin.product.dnd.ProductGroupTransfer;
import ch.eugster.colibri.admin.product.dnd.ProductGroupViewerDropAdapter;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderConfigurator;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class ProductView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.product.view";

	private static final NumberFormat PERCENT_FORMATTER = NumberFormat.getPercentInstance();
	
	private TreeViewer viewer;

	private CommonSettings settings;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderConfigurator, ProviderConfigurator> providerConfiguratorTracker;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService().getQuery(
					CommonSettings.class);
			this.settings = query.findDefault();
		}

		final Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLayout(new TableLayout());
		tree.setHeaderVisible(true);

		this.viewer = new TreeViewer(tree);
		this.viewer.setContentProvider(new ProductContentProvider());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.addDoubleClickListener(this);
		this.viewer.addTreeListener(new ITreeViewerListener()
		{
			@Override
			public void treeCollapsed(final TreeExpansionEvent event)
			{

			}

			@Override
			public void treeExpanded(final TreeExpansionEvent event)
			{
				ProductView.this.packColumns();
			}
		});

		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof ProductGroupGroup)
				{
					final ProductGroupGroup group = (ProductGroupGroup) object;
					if (group.equals(ProductGroupGroup.SALES))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("PLUS"));
					}
					else if (group.equals(ProductGroupGroup.EXPENSES))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("MINUS"));
					}
					else if (group.equals(ProductGroupGroup.INTERNAL))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("BANK"));
					}
					cell.setText(group.toString());
				}
				else if (object instanceof ProductGroupType)
				{
					final ProductGroupType type = (ProductGroupType) object;
					if (type.equals(ProductGroupType.SALES_RELATED))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("BOOKS"));
					}
					else if (type.equals(ProductGroupType.NON_SALES_RELATED))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("ELSE"));
					}
					else if (type.equals(ProductGroupType.EXPENSES_MATERIAL))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("MONEY_RED"));
					}
					else if (type.equals(ProductGroupType.EXPENSES_INVESTMENT))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("MONEY_VIOLET"));
					}
					else if (type.equals(ProductGroupType.ALLOCATION))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("MONEY_YELLOW"));
					}
					else if (type.equals(ProductGroupType.WITHDRAWAL))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("MONEY_BLUE"));
					}
					cell.setText(type.toCode());
				}
				else if (object instanceof ProductGroup)
				{
					final ProductGroup productGroup = (ProductGroup) object;
					if (ProductView.this.settings != null)
					{
						if ((ProductView.this.settings.getDefaultProductGroup() != null)
								&& ProductView.this.settings.getDefaultProductGroup().getId()
										.equals(productGroup.getId()))
						{
							cell.setImage(Activator.getDefault().getImageRegistry().get("BOOK_OPEN_RED"));
						}
						else if ((ProductView.this.settings.getPayedInvoice() != null)
								&& ProductView.this.settings.getPayedInvoice().getId()
										.equals(productGroup.getId()))
						{
							cell.setImage(Activator.getDefault().getImageRegistry().get("INVOICE"));
						}
						else
						{
							final ProductGroupType type = productGroup.getProductGroupType();
							if (type.equals(ProductGroupType.SALES_RELATED))
							{
								cell.setImage(Activator.getDefault().getImageRegistry().get("BOOK_CLOSED"));
							}
							else if (type.equals(ProductGroupType.NON_SALES_RELATED))
							{
								if (productGroup.getCode().toLowerCase().contains("sbvv")
										|| productGroup.getName().toLowerCase().contains("sbvv"))
								{
									cell.setImage(Activator.getDefault().getImageRegistry().get("SBVV"));
								}
								else
								{
									cell.setImage(Activator.getDefault().getImageRegistry().get("BON"));
								}
							}
							else if (type.equals(ProductGroupType.EXPENSES_MATERIAL))
							{
								cell.setImage(Activator.getDefault().getImageRegistry().get("MONEY_RED"));
							}
							else if (type.equals(ProductGroupType.EXPENSES_INVESTMENT))
							{
								cell.setImage(Activator.getDefault().getImageRegistry().get("MONEY_VIOLET"));
							}
							else if (type.equals(ProductGroupType.ALLOCATION))
							{
								cell.setImage(Activator.getDefault().getImageRegistry().get("MONEY_YELLOW"));
							}
							else if (type.equals(ProductGroupType.WITHDRAWAL))
							{
								cell.setImage(Activator.getDefault().getImageRegistry().get("MONEY_BLUE"));
							}
						}
						cell.setText(productGroup.getName());
					}
				}
			}
		});
		TreeColumn treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Bezeichnung");

		treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof ProductGroup)
				{
					cell.setText(((ProductGroup) object).getCode());
				}
			}
		});
		treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Beschriftung");

		PERCENT_FORMATTER.setMinimumFractionDigits(0);
		PERCENT_FORMATTER.setMaximumFractionDigits(3);
		
		treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof ProductGroup)
				{
					ProductGroup productGroup = (ProductGroup) object;
					Tax tax = productGroup.getDefaultTax();
					if (tax == null)
					{
						cell.setText("?");
					}
					else
					{
						cell.setText(PERCENT_FORMATTER.format(tax.getCurrentTax().getPercentage()));
					}
				}
			}
		});
		treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Mwst");

		treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof ProductGroup)
				{
					cell.setText(((ProductGroup) object).getAccount());
				}
			}
		});
		treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Konto");

		treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof ProductGroup)
				{
					ProductGroup productGroup = (ProductGroup) object;
					if (productGroup.isPayedInvoice())
					{
						cell.setText("Bez. Rechnungen");
					}
					else if (productGroup.isDefault())
					{
						cell.setText("Default");
					}
				}
			}
		});
		treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Spezialkonto");

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker
				.getService();
		if (providerConfigurator != null)
		{
			treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
			treeViewerColumn.setLabelProvider(new CellLabelProvider()
			{
				@Override
				public void update(final ViewerCell cell)
				{
					final Object element = cell.getElement();
					if (element instanceof ProductGroup)
					{
						final ProductGroup productGroup = (ProductGroup) element;
						String text = "";
						Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(providerConfigurator.getProviderId());
						for (ProductGroupMapping mapping : mappings)
						{
							if (!mapping.isDeleted())
							{
								text = mapping.getExternalProductGroup().getCode();
							}
						}
						cell.setText(text);
					}
				}
			});
			treeColumn = treeViewerColumn.getColumn();
			treeColumn.setResizable(true);
			treeColumn.setText(providerConfigurator.getName());
		}
		
		int ops = DND.DROP_MOVE;
		Transfer[] transfers = new Transfer[] { ProductGroupTransfer.getTransfer() };
		viewer.addDragSupport(ops, transfers, new ProductGroupDragListener(viewer));
		viewer.addDropSupport(ops, transfers, new ProductGroupViewerDropAdapter(viewer));

		this.createContextMenu();

		this.getSite().setSelectionProvider(this.viewer);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				ProductView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						ProductView.this.viewer.setInput(service);
						ProductView.this.packColumns();
					}
				});
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.modifiedService(reference, service);
				ProductView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						ProductView.this.viewer.setInput(service);
						ProductView.this.packColumns();
					}
				});
			}

			@Override
			public void removedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.removedService(reference, service);
				ProductView.this.getSite().getShell().getDisplay().asyncExec(new Runnable()
				{
					@Override
					public void run()
					{
						if (ProductView.this.viewer.getContentProvider() != null)
						{
							ProductView.this.viewer.setInput(ProductView.this.viewer);
							ProductView.this.packColumns();
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
		this.providerConfiguratorTracker.close();
		this.persistenceServiceTracker.close();

		EntityMediator.removeListener(ProductGroup.class, this);
		EntityMediator.removeListener(ProductGroupMapping.class, this);
		EntityMediator.removeListener(CommonSettings.class, this);
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof ProductGroupGroup)
		{
			this.viewer.setExpandedState(object, true);
		}
		else if (object instanceof ProductGroupType)
		{
			this.viewer.setExpandedState(object, true);
		}
		else if (object instanceof ProductGroup)
		{
			Activator.getDefault().editProductGroup((ProductGroup) object);
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
		EntityMediator.addListener(ProductGroup.class, this);
		EntityMediator.addListener(ProductGroupMapping.class, this);
		EntityMediator.addListener(CommonSettings.class, this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.providerConfiguratorTracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderConfigurator.class, null);
		this.providerConfiguratorTracker.open();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		UIJob job = new UIJob("Die Warengruppensicht wird aktualisiert...")
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

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (entity instanceof ProductGroup)
		{
			UIJob job = new UIJob("Die Warengruppensicht wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					final ProductGroup productGroup = (ProductGroup) entity;
					final ProductGroupType type = productGroup.getProductGroupType();
					if (!type.getChildren().contains(productGroup))
					{
						type.getChildren().add(productGroup);
					}
					ProductView.this.viewer.refresh(type);
					ProductView.this.packColumns();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof ProductGroupMapping)
		{
			UIJob job = new UIJob("Die Warengruppensicht wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					final ProductGroupMapping mapping = (ProductGroupMapping) entity;
					final ProductGroup productGroup = mapping.getProductGroup();
					if (!productGroup.getProductGroupMappings().contains(mapping))
					{
						productGroup.addProductGroupMapping(mapping);
					}
					ProductView.this.viewer.refresh(productGroup);
					ProductView.this.packColumns();
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
		if (entity instanceof ProductGroup)
		{
			UIJob job = new UIJob("Die Warengruppensicht wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					viewer.refresh();
					packColumns();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof ProductGroupMapping)
		{
			UIJob job = new UIJob("Die Warengruppensicht wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					final ProductGroupMapping mapping = (ProductGroupMapping) entity;
					viewer.refresh(mapping.getProductGroup());
					packColumns();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof CommonSettings)
		{
			UIJob job = new UIJob("Die Warengruppensicht wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					viewer.refresh(ProductGroupType.SALES_RELATED);
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