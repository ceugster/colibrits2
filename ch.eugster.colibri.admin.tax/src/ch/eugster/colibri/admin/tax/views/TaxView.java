package ch.eugster.colibri.admin.tax.views;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ColumnWeightData;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.ui.views.AbstractEntityView;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.CurrentTaxCodeMapping;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderConfigurator;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

@SuppressWarnings("rawtypes")
public class TaxView extends AbstractEntityView implements IDoubleClickListener, SelectionListener,
		IModeSelectionProvider
{
	public static final String ID = "ch.eugster.colibri.admin.tax.view";

	private TreeViewer viewer;

	private ServiceTracker<ProviderConfigurator, ProviderConfigurator> providerConfiguratorTracker;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private Button[] modes;

	private Mode currentMode;

	private final Collection<IModeSelectionListener> modeSelectionListeners = new ArrayList<IModeSelectionListener>();

	private IDialogSettings settings;

	public TaxView()
	{
		IDialogSettings dialogSettings = TaxActivator.getDefault().getDialogSettings();
		settings = dialogSettings.getSection(TaxView.ID);
		if (settings == null)
		{
			settings = dialogSettings.addNewSection(TaxView.ID);
			settings.put("mode", 0);
		}
	}

	@Override
	public void addModeSelectionListener(final IModeSelectionListener listener)
	{
		this.modeSelectionListeners.add(listener);
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Group group = new Group(parent, SWT.NONE);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, true));

		this.modes = new Button[Mode.values().length];

		this.modes[Mode.TYPE.ordinal()] = new Button(group, SWT.RADIO);
		this.modes[Mode.TYPE.ordinal()].setText("Nach Arten");
		this.modes[Mode.TYPE.ordinal()].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.modes[Mode.TYPE.ordinal()].addSelectionListener(this);

		this.modes[Mode.GROUP.ordinal()] = new Button(group, SWT.RADIO);
		this.modes[Mode.GROUP.ordinal()].setText("Nach Gruppen");
		this.modes[Mode.GROUP.ordinal()].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.modes[Mode.GROUP.ordinal()].addSelectionListener(this);

		final TableLayout layout = new TableLayout();
		final Tree tree = new Tree(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		tree.setLayout(layout);
		tree.setHeaderVisible(true);

		this.viewer = new TreeViewer(tree);
		this.viewer.setContentProvider(new TaxContentProvider(this.viewer));
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.addDoubleClickListener(this);
		this.viewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		this.viewer.addTreeListener(new ITreeViewerListener()
		{
			@Override
			public void treeCollapsed(final TreeExpansionEvent event)
			{

			}

			@Override
			public void treeExpanded(final TreeExpansionEvent event)
			{
				TaxView.this.packColumns();
			}
		});

		TreeViewerColumn treeViewerColumn = new TreeViewerColumn(this.viewer, SWT.NONE);
		treeViewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				final Object object = cell.getElement();
				if (object instanceof TaxType)
				{
					cell.setText(((TaxType) object).getName());
				}
				else if (object instanceof TaxRate)
				{
					cell.setText(((TaxRate) object).getName());
				}
				else if (object instanceof Tax)
				{
					if (TaxView.this.currentMode.equals(Mode.TYPE))
					{
						cell.setText(((Tax) object).getTaxRate().getName());
					}
					else if (TaxView.this.currentMode.equals(Mode.GROUP))
					{
						cell.setText(((Tax) object).getTaxType().getName());
					}
				}
				else if (object instanceof CurrentTax)
				{
					CurrentTax currentTax = (CurrentTax) object;
					boolean current = currentTax.getId().equals(currentTax.getTax().getCurrentTax().getId());
					ImageRegistry registry = TaxActivator.getDefault().getImageRegistry();
					cell.setImage(current ? registry.get("dot.green") : registry.get("dot.transparent"));
					cell.setText(CurrentTax.format((CurrentTax) object));
				}
			}
		});
		TreeColumn treeColumn = treeViewerColumn.getColumn();
		treeColumn.setResizable(true);
		treeColumn.setText("Bezeichnung");
		layout.addColumnData(new ColumnWeightData(20, true));

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
					final Object object = cell.getElement();
					if (object instanceof Tax)
					{
						final TaxCodeMapping mapping = ((Tax) object).getTaxCodeMapping(providerConfigurator
								.getProviderId());
						if (mapping == null)
						{
							cell.setText("");
						}
						else if (mapping.isDeleted())
						{
							cell.setText("");
						}
						else
						{
							cell.setText(mapping.getCode());
						}
					}
					else if (object instanceof CurrentTax)
					{
						final ProviderConfigurator provider = (ProviderConfigurator) TaxView.this.providerConfiguratorTracker
								.getService();
						final CurrentTaxCodeMapping mapping = ((CurrentTax) object).getCurrentTaxCodeMapping(provider
								.getProviderId());
						if (mapping == null)
						{
							cell.setText("");
						}
						else if (mapping.isDeleted())
						{
							cell.setText("");
						}
						else
						{
							cell.setText(mapping.getCode());
						}
					}
				}
			});
			treeColumn = treeViewerColumn.getColumn();
			treeColumn.setResizable(true);
			treeColumn.setText(providerConfigurator.getName());
		}

		this.viewer.addDoubleClickListener(this);

		this.createContextMenu();

		this.getSite().setSelectionProvider(this.viewer);

		int mode = settings.getInt("mode");
		this.currentMode = Mode.values()[mode < 0 ? 0 : (mode > this.modes.length - 1 ? this.modes.length - 1 : mode)];
		this.modes[currentMode.ordinal()].setSelection(true);
		Event event = new Event();
		event.doit = true;
		event.widget = this.modes[currentMode.ordinal()];
		this.widgetSelected(new SelectionEvent(event));

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(TaxActivator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				final UIJob job = new UIJob("Lade Mehrwertsteuer...")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						TaxView.this.viewer.setInput(currentMode);
						TaxView.this.packColumns();
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
				return service;
			}

			@Override
			public void modifiedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				super.modifiedService(reference, service);
				final UIJob job = new UIJob("Lade Mehrwertsteuer...")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						TaxView.this.viewer.setInput(currentMode);
						TaxView.this.packColumns();
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
				final UIJob job = new UIJob("Lade Mehrwertsteuer...")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (TaxView.this.viewer.getContentProvider() != null)
						{
							TaxView.this.viewer.setInput(TaxView.this.viewer);
							TaxView.this.packColumns();
						}
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
			}
		};
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		this.providerConfiguratorTracker.close();

		this.modeSelectionListeners.clear();

		EntityMediator.removeListener(CurrentTaxCodeMapping.class, this);
		EntityMediator.removeListener(CurrentTax.class, this);
		EntityMediator.removeListener(TaxCodeMapping.class, this);
		EntityMediator.removeListener(Tax.class, this);
		EntityMediator.removeListener(TaxType.class, this);
		EntityMediator.removeListener(TaxRate.class, this);
		super.dispose();
	}

	@Override
	public void doubleClick(final DoubleClickEvent event)
	{
		final ISelection selection = event.getSelection();
		final Object object = ((IStructuredSelection) selection).getFirstElement();
		if (object instanceof TaxRate)
		{
			TaxActivator.getDefault().editTaxRate((TaxRate) object);
		}
		else if (object instanceof TaxType)
		{
			TaxActivator.getDefault().editTaxType((TaxType) object);
		}
		else if (object instanceof Tax)
		{
			TaxActivator.getDefault().editTax(this, (Tax) object);
		}
		else if (object instanceof CurrentTax)
		{
			TaxActivator.getDefault().editCurrentTax((CurrentTax) object);
		}
		else
		{
			return;
		}
	};

	public Mode getCurrentMode()
	{
		return this.currentMode;
	}

	public TreeViewer getViewer()
	{
		return this.viewer;
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
		EntityMediator.addListener(TaxRate.class, this);
		EntityMediator.addListener(TaxType.class, this);
		EntityMediator.addListener(Tax.class, this);
		EntityMediator.addListener(TaxCodeMapping.class, this);
		EntityMediator.addListener(CurrentTax.class, this);
		EntityMediator.addListener(CurrentTaxCodeMapping.class, this);

		this.providerConfiguratorTracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(TaxActivator.getDefault().getBundle().getBundleContext(),
				ProviderConfigurator.class, null);
		this.providerConfiguratorTracker.open();

	}

	@Override
	public void notifyModeSelectionListeners(final Mode mode)
	{
		for (final IModeSelectionListener listener : this.modeSelectionListeners)
		{
			listener.modeSelected(mode);
		}
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		final Object refresh = this.getParent(entity);
		if (refresh != null)
		{
			UIJob job = new UIJob("Update view...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (viewer != null && !viewer.getControl().isDisposed())
					{
						if (refresh.equals(viewer))
						{
							viewer.refresh();
						}
						else
						{
							viewer.refresh(refresh);
						}
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		final Object parent = this.getParent(entity);
		if (parent != null)
		{
			UIJob job = new UIJob("Update view...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (viewer != null && !viewer.getControl().isDisposed())
					{
						viewer.add(parent, entity);
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		final Object parent = this.getParent(entity);
		if (parent != null)
		{
			UIJob job = new UIJob("Update view...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					if (viewer != null && !viewer.getControl().isDisposed())
					{
						viewer.refresh(parent);
					}
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	@Override
	public void removeModeSelectionListener(final IModeSelectionListener listener)
	{
		this.modeSelectionListeners.remove(listener);

	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus()
	{
		this.packColumns();
		this.viewer.getControl().setFocus();
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent event)
	{
		this.widgetSelected(event);
	}

	@Override
	public void widgetSelected(final SelectionEvent event)
	{
		if (event.widget.equals(this.modes[Mode.GROUP.ordinal()]))
		{
			this.currentMode = Mode.GROUP;
		}
		else if (event.widget.equals(this.modes[Mode.TYPE.ordinal()]))
		{
			this.currentMode = Mode.TYPE;
		}

		this.notifyModeSelectionListeners(this.currentMode);
		this.viewer.setInput(this.currentMode);
		this.viewer.refresh();
	}

	private void createContextMenu()
	{
		final MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);

		final Menu menu = menuManager.createContextMenu(this.viewer.getControl());
		this.viewer.getControl().setMenu(menu);

		this.getSite().registerContextMenu(menuManager, this.viewer);
	}

	private Object getParent(final AbstractEntity entity)
	{
		if (entity instanceof TaxType)
		{
			return viewer;
		}
		else if (entity instanceof TaxRate)
		{
			return viewer;
		}
		else if (entity instanceof Tax)
		{
			if (this.viewer.getInput() instanceof Mode)
			{
				final Mode mode = (Mode) this.viewer.getInput();
				if (mode.equals(Mode.GROUP))
				{
					return ((Tax) entity).getTaxRate();
				}
				else if (mode.equals(Mode.TYPE))
				{
					return ((Tax) entity).getTaxType();
				}
				else
				{
					return null;
				}
			}
			else
			{
				return null;
			}
		}
		else if (entity instanceof TaxCodeMapping)
		{
			return ((TaxCodeMapping) entity).getTax();
		}
		else if (entity instanceof CurrentTax)
		{
			return ((CurrentTax) entity).getTax();
		}
		else if (entity instanceof CurrentTaxCodeMapping)
		{
			return ((CurrentTaxCodeMapping) entity).getCurrentTax();
		}
		else
		{
			return null;
		}
	}

	private void packColumns()
	{
		UIJob job = new UIJob("update columns width")
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				int used = 0;
				final Rectangle size = viewer.getTree().getClientArea();
				for (int i = 1; i < viewer.getTree().getColumnCount(); i++)
				{
					viewer.getTree().getColumn(i).pack();
					used = +viewer.getTree().getColumn(i).getWidth();
				}
				viewer.getTree().getColumn(0).setWidth(size.width - used);
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	public enum Mode
	{
		TYPE, GROUP;
	}
}