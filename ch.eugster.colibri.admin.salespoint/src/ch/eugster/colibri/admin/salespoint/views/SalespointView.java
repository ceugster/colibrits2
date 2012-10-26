package ch.eugster.colibri.admin.salespoint.views;

import java.text.NumberFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.AbstractTreeViewer;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.salespoint.Activator;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class SalespointView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.salespoint.view";

	public static final NumberFormat nf = NumberFormat.getCurrencyInstance();

	private TreeViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		final Tree tree = new Tree(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLayoutData(new GridData(GridData.FILL_BOTH));
		tree.setHeaderVisible(true);

		this.viewer = new TreeViewer(tree);
		this.viewer.setContentProvider(new SalespointContentProvider());
		this.viewer.setSorter(new SalespointViewerSorter());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.addDoubleClickListener(this);

		TreeViewerColumn viewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Bezeichnung");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				Image image = null;
				if (cell.getElement() instanceof Salespoint)
				{
					String hostname = null;
					final PersistenceService persistenceService = (PersistenceService) SalespointView.this.persistenceServiceTracker
							.getService();
					if (persistenceService != null)
					{
						final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService()
								.getQuery(CommonSettings.class);
						final CommonSettings settings = query.findDefault();
						if (settings != null)
						{
							hostname = settings.getHostnameResolver().getHostname();
						}
					}
					final Salespoint salespoint = (Salespoint) cell.getElement();
					cell.setText(salespoint.getName());
					if (salespoint.getHost() == null)
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("salespoint.png"));
					}
					else if (salespoint.getHost().equals(hostname))
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("salespoint_this.png"));
					}
					else
					{
						cell.setImage(Activator.getDefault().getImageRegistry().get("salespoint_in_use.png"));
					}
				}
				else if (cell.getElement() instanceof Stock)
				{
					final Stock stock = (Stock) cell.getElement();
					final Currency currency = stock.getPaymentType().getCurrency();
					SalespointView.nf.setCurrency(currency.getCurrency());
					SalespointView.nf.setMaximumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
					SalespointView.nf.setMinimumFractionDigits(currency.getCurrency().getDefaultFractionDigits());
					final StringBuffer sb = new StringBuffer(SalespointView.nf.format(stock.getAmount()));
					cell.setText(sb.toString());
					if (stock.getPaymentType().equals(stock.getSalespoint().getPaymentType()))
					{
						image = Activator.getDefault().getImageRegistry().get("money_green_16.png");
					}
					else
					{
						image = Activator.getDefault().getImageRegistry().get("money_violet_16.png");
					}

					cell.setImage(image);
				}
			}
		});

		viewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Standort");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Salespoint)
				{
					final Salespoint salespoint = (Salespoint) cell.getElement();
					if (salespoint.getLocation() != null)
					{
						cell.setText(salespoint.getLocation());
					}
				}
				else if (cell.getElement() instanceof Stock)
				{
					final Stock stock = (Stock) cell.getElement();
					cell.setText(stock.isVariable() ? " (variabel)" : " (fix)");
				}
			}
		});

		viewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Profil");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Salespoint)
				{
					final Salespoint salespoint = (Salespoint) cell.getElement();
					if (salespoint.getProfile() != null)
					{
						cell.setText(salespoint.getProfile().getName());
					}
				}
			}
		});

		viewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Registrierter Host");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Salespoint)
				{
					final Salespoint salespoint = (Salespoint) cell.getElement();
					if (salespoint.getHost() != null)
					{
						cell.setText(salespoint.getHost());
					}
				}
			}
		});

		this.createContextMenu(this.viewer);
		this.viewer.addDoubleClickListener(this);

		this.getSite().setSelectionProvider(this.viewer);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				final UIJob uiJob = new UIJob("set viewer input")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						SalespointView.this.viewer.setInput(service);
						SalespointView.this.packColumns();
						return Status.OK_STATUS;
					}
				};
				uiJob.schedule();
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.modifiedService(reference, service);
				final UIJob job = new UIJob("set service input")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						SalespointView.this.viewer.setInput(service);
						SalespointView.this.packColumns();
						return null;
					}
				};
				job.schedule();
			}

			@Override
			public void removedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.removedService(reference, service);
				final UIJob job = new UIJob("set service input")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (SalespointView.this.viewer.getContentProvider() != null)
						{
							SalespointView.this.viewer.setInput(SalespointView.this.viewer);
							SalespointView.this.packColumns();
						}
						return Status.OK_STATUS;
					}
				};
				job.schedule();
			}
		};
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		EntityMediator.removeListener(Profile.class, this);
		EntityMediator.removeListener(Salespoint.class, this);
		EntityMediator.removeListener(Stock.class, this);
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (!(object instanceof AbstractEntity))
		{
			return;
		}
		else if (object instanceof Salespoint)
		{
			Activator.getDefault().editSalespoint((Salespoint) object);
		}
		else if (object instanceof Stock)
		{
			Activator.getDefault().editStock((Stock) object);
		}
		else
		{
			return;
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

		EntityMediator.addListener(Profile.class, this);
		EntityMediator.addListener(Salespoint.class, this);
		EntityMediator.addListener(Stock.class, this);
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (!this.viewer.getTree().isDisposed())
		{
			this.viewer.refresh();
		}
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (!this.viewer.getTree().isDisposed())
		{
			Object parent = null;
			if (entity instanceof Salespoint)
			{
				parent = this;
			}
			else if (entity instanceof Stock)
			{
				parent = ((Stock) entity).getSalespoint();
			}
			else
			{
				return;
			}

			this.viewer.add(parent, entity);
			this.viewer.expandToLevel(entity, AbstractTreeViewer.ALL_LEVELS);
			this.packColumns();
		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (!this.viewer.getTree().isDisposed())
		{
			if (entity instanceof Profile)
			{
				final Profile profile = (Profile) entity;
				for (final Salespoint salespoint : profile.getSalespoints())
				{
					this.viewer.refresh(salespoint);
				}
			}
			else
			{
				final UIJob uiJob = new UIJob("refresh viewer")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						SalespointView.this.viewer.refresh(entity);
						packColumns();
						return Status.OK_STATUS;
					}
				};
				uiJob.schedule();
			}
		}
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		if (!this.viewer.getTree().isDisposed())
		{
			this.viewer.getControl().setFocus();
		}
	}

	private void packColumns()
	{
		UIJob job = new UIJob("update columns width")
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				for (final TreeColumn treeColumn : viewer.getTree().getColumns())
				{
					treeColumn.pack();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}