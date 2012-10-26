package ch.eugster.colibri.report.salespoint.views;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.report.salespoint.Activator;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class SalespointView extends ViewPart
{
	public static final String ID = "ch.eugster.colibri.report.salespoint.view";

	private TableViewer viewer;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private IDialogSettings settings;

	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);

		settings = Activator.getDefault().getDialogSettings().getSection("report.salespoint");
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection("report.salespoint");
		}
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		final Table table = new Table(composite, SWT.BORDER | SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		table.setLayoutData(new GridData(GridData.FILL_BOTH));
		table.setHeaderVisible(true);

		this.viewer = new TableViewer(table);
		this.viewer.setContentProvider(new SalespointContentProvider());
		this.viewer.setSorter(new SalespointViewerSorter());
		this.viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.viewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				Collection<String> ids = new ArrayList<String>();
				StructuredSelection ssel = (StructuredSelection) viewer.getSelection();
				Object[] objects = ssel.toArray();
				for (Object object : objects)
				{
					if (object instanceof Salespoint)
					{
						ids.add(((Salespoint) object).getId().toString());
					}
				}
				settings.put("selected.salespoint.ids", ids.toArray(new String[0]));
			}
		});

		TableViewerColumn viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
		viewerColumn.getColumn().setText("Bezeichnung");
		viewerColumn.setLabelProvider(new CellLabelProvider()
		{
			@Override
			public void update(final ViewerCell cell)
			{
				if (cell.getElement() instanceof Salespoint)
				{
					String hostname = null;
					final PersistenceService persistenceService = (PersistenceService) SalespointView.this.persistenceServiceTracker
							.getService();
					if (persistenceService != null)
					{
						final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService()
								.getQuery(CommonSettings.class);
						final CommonSettings commonSettings = query.findDefault();
						if (commonSettings != null)
						{
							hostname = commonSettings.getHostnameResolver().getHostname();
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
					}
				}
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
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
			}
		});

		viewerColumn = new TableViewerColumn(this.viewer, SWT.NONE);
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

		final Group selectorGroup = new Group(composite, SWT.None);
		selectorGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectorGroup.setLayout(new GridLayout(3, true));

		final Button selectAllButton = new Button(selectorGroup, SWT.PUSH);
		selectAllButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectAllButton.setText("alle");
		selectAllButton.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				final Collection<Object> objects = new ArrayList<Object>();
				final TableItem[] items = SalespointView.this.viewer.getTable().getItems();
				for (final TableItem item : items)
				{
					objects.add(item.getData());
				}
				final StructuredSelection ssel = new StructuredSelection(objects.toArray(new Object[0]));
				SalespointView.this.viewer.setSelection(ssel);
			}

		});

		final Button selectNoneButton = new Button(selectorGroup, SWT.PUSH);
		selectNoneButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectNoneButton.setText("keine");
		selectNoneButton.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				final StructuredSelection ssel = new StructuredSelection(new Object[0]);
				SalespointView.this.viewer.setSelection(ssel);
			}

		});

		final Button reverseSelectionButton = new Button(selectorGroup, SWT.PUSH);
		reverseSelectionButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		reverseSelectionButton.setText("umkehren");
		reverseSelectionButton.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				StructuredSelection ssel = (StructuredSelection) SalespointView.this.viewer.getSelection();
				final Object[] sels = ssel.toArray();

				final Collection<Object> objects = new ArrayList<Object>();
				final TableItem[] items = SalespointView.this.viewer.getTable().getItems();
				for (final TableItem item : items)
				{
					boolean found = false;
					for (final Object sel : sels)
					{
						if (sel.equals(item.getData()))
						{
							found = true;
						}
					}
					if (!found)
					{
						objects.add(item.getData());
					}
				}

				ssel = new StructuredSelection(objects.toArray(new Object[0]));
				SalespointView.this.viewer.setSelection(ssel);
			}

		});

		this.getSite().setSelectionProvider(this.viewer);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				final UIJob job = new UIJob("Lade Kassen...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						final SalespointQuery query = (SalespointQuery) service.getServerService().getQuery(
								Salespoint.class);
						final Salespoint[] salespoints = query.selectAll(true).toArray(new Salespoint[0]);
						Salespoint[] selectedSalespoints = SalespointView.this.getSelectedSalespoints(salespoints);
						SalespointView.this.viewer.setInput(salespoints);
						SalespointView.this.viewer.setSelection(new StructuredSelection(selectedSalespoints));
						SalespointView.this.packColumns();
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
				final UIJob job = new UIJob("Lade Kassen...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						SalespointView.this.viewer.setInput(service);
						SalespointView.this.packColumns();
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
				final UIJob job = new UIJob("Lade Kassen...")
				{

					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						if (SalespointView.this.viewer.getContentProvider() != null)
						{
							SalespointView.this.viewer.setInput(new Salespoint[0]);
							SalespointView.this.packColumns();
						}
						return Status.OK_STATUS;
					}

				};
				job.setUser(true);
				job.schedule();
			}
		};
		this.persistenceServiceTracker.open();

		getSite().setSelectionProvider(this.viewer);
	}

	private Salespoint[] getSelectedSalespoints(Salespoint[] salespoints)
	{
		Collection<Salespoint> selected = new ArrayList<Salespoint>();
		String[] salespointIds = settings.getArray("selected.salespoint.ids");
		if (salespointIds != null)
		{
			for (String salespointId : salespointIds)
			{
				try
				{
					Long id = Long.valueOf(salespointId);
					for (Salespoint salespoint : salespoints)
					{
						if (salespoint.getId().equals(id))
						{
							selected.add(salespoint);
						}
					}
				}
				catch (NumberFormatException e)
				{
					// do nothing...
				}
			}
		}
		return selected.toArray(new Salespoint[0]);
	}

	@Override
	public void dispose()
	{
		if (this.persistenceServiceTracker != null)
		{
			this.persistenceServiceTracker.close();
		}
		super.dispose();
	}

	@SuppressWarnings("rawtypes")
	public String getSalespointNames()
	{
		if (((StructuredSelection) this.viewer.getSelection()).size() == ((Salespoint[]) this.viewer.getInput()).length)
		{
			return "Alle";
		}
		else
		{
			StringBuilder builder = new StringBuilder();
			final StructuredSelection ssel = (StructuredSelection) this.viewer.getSelection();
			final Iterator iterator = ssel.iterator();
			while (iterator.hasNext())
			{
				final Object object = iterator.next();
				if (object instanceof Salespoint)
				{
					if (builder.length() > 0)
					{
						builder = builder.append(", ");
					}
					builder = builder.append(((Salespoint) object).getName());
				}
			}
			return builder.toString();
		}
	}

	public Salespoint[] getSelectedSalespoints()
	{
		final StructuredSelection ssel = (StructuredSelection) this.viewer.getSelection();
		Object[] selection = ssel.toArray();
		Salespoint[] salespoints = new Salespoint[selection.length];
		for (int i = 0; i < selection.length; i++)
		{
			salespoints[i] = (Salespoint) selection[i];
		}
		return salespoints;
	}

	public void packColumns()
	{
		final TableColumn[] columns = this.viewer.getTable().getColumns();
		for (final TableColumn column : columns)
		{
			column.pack();
		}
	}

	@Override
	public void setFocus()
	{
		this.viewer.getTable().setFocus();
	}
}
