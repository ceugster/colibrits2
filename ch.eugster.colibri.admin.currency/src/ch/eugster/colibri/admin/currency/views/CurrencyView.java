package ch.eugster.colibri.admin.currency.views;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.currency.Activator;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.queries.CurrencyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class CurrencyView extends AbstractEntityView implements IDoubleClickListener
{
	public static final String ID = "ch.eugster.colibri.admin.currency.view"; //$NON-NLS-1$

	private TableViewer viewer;

	private IDialogSettings settings;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Table table = new Table(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));

		this.viewer = new TableViewer(table);
		this.viewer.setContentProvider(new CurrencyContentProvider());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });

		TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Code");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				Image image = null;
				if (cell.getElement() instanceof Currency)
				{
					Currency referenceCurrency = null;
					final PersistenceService persistenceService = (PersistenceService) CurrencyView.this.persistenceServiceTracker
							.getService();
					if (persistenceService != null)
					{
						final CommonSettings settings = (CommonSettings) persistenceService.getServerService().find(
								CommonSettings.class, Long.valueOf(1L));
						if (settings != null)
						{
							referenceCurrency = settings.getReferenceCurrency();
						}
					}
					final Currency currency = (Currency) cell.getElement();

					if ((referenceCurrency != null) && currency.equals(referenceCurrency))
					{
						image = Activator.getDefault().getImageRegistry().get("money_green_16.png"); //$NON-NLS-1$
					}
					else if (currency.getPaymentTypes().size() > 0)
					{
						image = Activator.getDefault().getImageRegistry().get("money_violet_16.png"); //$NON-NLS-1$
					}
					else
					{
						image = Activator.getDefault().getImageRegistry().get("money_yellow_16.png"); //$NON-NLS-1$
					}
					cell.setText(((Currency) cell.getElement()).getCode());
					cell.setImage(image);
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Bezeichnung");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Currency)
				{
					cell.setText(((Currency) cell.getElement()).getName());
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Region");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Currency)
				{
					cell.setText(((Currency) cell.getElement()).getRegion());
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Kurs");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Currency)
				{
					cell.setText(CurrencyQuery.formatQuotation(((Currency) cell.getElement()).getQuotation()));
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Rundungsfaktor");
		viewerColumn.getColumn().setToolTipText("Rundungsfaktor");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Currency)
				{
					cell.setText(CurrencyQuery.formatRoundFactor(((Currency) cell.getElement()).getRoundFactor()));
				}
			}
		});

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
				if (service != null)
				{
					final CommonSettings settings = (CommonSettings) service.getServerService().find(
							CommonSettings.class, Long.valueOf(1L));
					super.modifiedService(reference, service);
					final UIJob job = new UIJob("remove items")
					{
						@Override
						public IStatus runInUIThread(final IProgressMonitor monitor)
						{
							if (CurrencyView.this.viewer.getContentProvider() != null)
							{
								CurrencyView.this.viewer.setSorter(new CurrencySorter(settings));
								CurrencyView.this.viewer.setInput(service);
								CurrencyView.this.packColumns();
							}
							return Status.OK_STATUS;
						}

					};
					job.schedule();
				}
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				final UIJob job = new UIJob("remove items")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						CurrencyView.this.viewer.setInput(service);
						CurrencyView.this.packColumns();
						return null;
					}

				};
				job.schedule();
				super.modifiedService(reference, service);
			}

			@Override
			public void removedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				final UIJob job = new UIJob("remove items")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (CurrencyView.this.viewer.getContentProvider() != null)
						{
							CurrencyView.this.viewer.setInput(CurrencyView.this.viewer);
							CurrencyView.this.packColumns();
						}
						return null;
					}

				};
				job.schedule();
				super.removedService(reference, service);
			}
		};
		this.persistenceServiceTracker.open();

		EntityMediator.addListener(Currency.class, this);
		EntityMediator.addListener(CommonSettings.class, this);

	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Currency.class, this);
		EntityMediator.removeListener(CommonSettings.class, this);
		this.persistenceServiceTracker.close();
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
		else if (object instanceof Currency)
		{
			Activator.getDefault().editCurrency((Currency) object);
		}
		else
		{
			return;
		}
	}

	public Currency getPreviousCurrency(final AbstractEntity entity)
	{
		if (entity instanceof CommonSettings)
		{
			final CommonSettings settings = (CommonSettings) entity;
			return settings.getReferenceCurrency();
		}
		return null;
	}

	public TableViewer getViewer()
	{
		return this.viewer;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
		this.settings = Activator.getDefault().getDialogSettings().getSection(CurrencyView.ID);
		if (this.settings == null)
		{
			this.settings = Activator.getDefault().getDialogSettings().addNewSection(CurrencyView.ID);
		}

		this.settings.put("visible", true);
		this.settings.put("position", IPageLayout.LEFT);
	};

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		this.viewer.refresh();
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (entity instanceof Currency)
		{
			this.viewer.add(entity);
			this.packColumns();
		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		Currency previousCurrency = null;
		if (entity instanceof CommonSettings)
		{
			final CommonSettings settings = (CommonSettings) entity;
			previousCurrency = settings.getReferenceCurrency();
			if (previousCurrency == null)
			{
				this.viewer.refresh();
			}
			else
			{
				this.viewer.refresh(settings.getReferenceCurrency());
			}
		}
		else if (entity instanceof Currency)
		{
			this.viewer.refresh(entity);
		}
		else
		{
			this.viewer.refresh();
		}

		this.packColumns();
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
				for (final TableColumn tableColumn : viewer.getTable().getColumns())
				{
					tableColumn.pack();
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}
}