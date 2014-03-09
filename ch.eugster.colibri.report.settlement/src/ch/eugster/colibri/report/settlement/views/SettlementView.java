package ch.eugster.colibri.report.settlement.views;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.report.daterange.views.DateView;
import ch.eugster.colibri.report.destination.views.DestinationView;
import ch.eugster.colibri.report.engine.ReportService;
import ch.eugster.colibri.report.salespoint.views.SalespointView;
import ch.eugster.colibri.report.settlement.Activator;

public class SettlementView extends ViewPart implements IViewPart, ISelectionListener, ISelectionChangedListener
{
	public static final String ID = "ch.eugster.colibri.report.settlement.view";

	private IDialogSettings settings;

	private TabFolder tabFolder;

	private Button start;

	private ISettlementCompositeChild selectedChild;

//	private Salespoint[] selectedSalespoints;
//	
//	private Calendar[] selectedDateRange;
	
	private ReportService.Destination selectedDestination;

	private ReportService.Format selectedFormat;
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);

		settings = Activator.getDefault().getDialogSettings().getSection(this.getClass().getName());
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection(this.getClass().getName());
		}
		try
		{
			settings.getInt("selected.tab");
		}
		catch (NumberFormatException e)
		{
			settings.put("selected.tab", 0);
		}
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		tabFolder = new TabFolder(parent, SWT.TOP);
		tabFolder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Datumsbereich");
		SettlementDateRangeComposite dateRangeComposite = new SettlementDateRangeComposite(tabFolder, this, SWT.NONE);
		item.setControl(dateRangeComposite);
		tabFolder.setSelection(item);

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Einzelner Abschluss");
		SettlementNumberComposite numberComposite = new SettlementNumberComposite(tabFolder, this, SWT.NONE);
		numberComposite.addSelectionChangedListener(this);
		item.setControl(numberComposite);

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Differenzliste");
		SettlementDifferenceComposite differenceComposite = new SettlementDifferenceComposite(tabFolder, this, SWT.NONE);
		differenceComposite.addSelectionChangedListener(this);
		item.setControl(differenceComposite);

		item = new TabItem(tabFolder, SWT.NONE);
		item.setText("Rabattliste");
		SettlementDiscountComposite discountComposite = new SettlementDiscountComposite(tabFolder, this, SWT.NONE);
		discountComposite.addSelectionChangedListener(this);
		item.setControl(discountComposite);

		// item = new TabItem(tabFolder, SWT.NONE);
		// item.setText("Abschlüsse über Periode");
		// SettlementRangeComposite rangeComposite = new
		// SettlementRangeComposite(tabFolder, SWT.NONE);
		// item.setControl(rangeComposite);

		tabFolder.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				TabItem tabItem = (TabItem) e.item;
				if (tabItem.getControl() instanceof ISettlementCompositeChild)
				{
					selectedChild = (ISettlementCompositeChild) tabItem.getControl();

					TabFolder folder = (TabFolder) e.widget;
					TabItem[] items = folder.getItems();
					for (TabItem item : items)
					{
						if (item.getControl() == selectedChild)
						{
							selectedChild.addSelectionChangedListener(SettlementView.this);
							start.setEnabled(selectedChild.validateSelection());
							settings.put("selected.tab", folder.getSelectionIndex());
						}
						else
						{
							if (item.getControl() instanceof ISettlementCompositeChild)
							{
								((ISettlementCompositeChild) item.getControl())
										.removeSelectionChangedListener(SettlementView.this);
							}
						}
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

		if (tabFolder.getItemCount() > settings.getInt("selected.tab"))
		{
			item = tabFolder.getItem(settings.getInt("selected.tab"));
			selectedChild = (ISettlementCompositeChild) item.getControl();
			tabFolder.setSelection(item);
		}

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setLayout(new GridLayout(2, false));

		Label label = new Label(buttonComposite, SWT.NONE);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		start = new Button(buttonComposite, SWT.PUSH);
		start.setText("Auswertung starten");
		start.addSelectionListener(new SelectionListener()
		{

			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				if (selectedChild != null)
				{
					final JRDataSource dataSource = selectedChild.createDataSource();
					if (dataSource == null)
					{
						MessageDialog.openInformation(null, "Keine Daten vorhanden", "Für die gewählte Selektion sind keine Daten vorhanden (aus der Vorgängerversion übernommene Daten sind für diese Auswertung nicht abrufbar).");
						return;
					}
//					selectedSalespoints = getSelectedSalespoints();
//					selectedDateRange = getSelectedDateRange();
					selectedDestination = getSelectedDestination();
					selectedFormat = getSelectedFormat();
					final Hashtable<String, Object> parameters = selectedChild.getParameters();
					UIJob job = new UIJob("Auswertung wird aufbereitet...")
					{
						@Override
						public IStatus runInUIThread(IProgressMonitor monitor)
						{
							try
							{
//								start.setEnabled(false);
								monitor.beginTask("Auswertung wird aufbereitet. Bitte warten Sie...", IProgressMonitor.UNKNOWN);
								final InputStream report = selectedChild.getReport();
								printReport(new SubProgressMonitor(monitor, 1), report, dataSource, parameters);
							}
							catch (IOException e1)
							{
							}
							finally
							{
								monitor.done();
//								start.setEnabled(true);
							}
							return Status.OK_STATUS;
						}
					};
//					job.setUser(true);
					job.schedule();
				}
			}
		});
		
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(SalespointView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DateView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DestinationView.ID, this);
		selectedChild.addSelectionChangedListener(SettlementView.this);

		updateStartButton();
	}

	@Override
	public void dispose()
	{
		ISelectionService service = getSite().getWorkbenchWindow().getSelectionService();
		service.removePostSelectionListener(SalespointView.ID, this);
		service.removePostSelectionListener(DateView.ID, this);
		service.removePostSelectionListener(DestinationView.ID, this);
		super.dispose();
	}
	
	public Salespoint[] getSelectedSalespoints()
	{
		IViewReference[] references = this.getSite().getPage().getViewReferences();
		for (IViewReference reference : references)
		{
			IViewPart part = reference.getView(true);
			if (part instanceof SalespointView)
			{
				SalespointView view = (SalespointView) part;
				return view.getSelectedSalespoints();
			}
		}
		return null;
	}
	
	public Calendar[] getSelectedDateRange()
	{
		IViewReference[] references = this.getSite().getPage().getViewReferences();
		for (IViewReference reference : references)
		{
			IViewPart part = reference.getView(true);
			if (part instanceof DateView)
			{
				DateView view = (DateView) part;
				return view.getDates();
			}
		}
		return null;
	}
	
	public ReportService.Destination getSelectedDestination()
	{
		IViewReference[] references = this.getSite().getPage().getViewReferences();
		for (IViewReference reference : references)
		{
			IViewPart part = reference.getView(true);
			if (part instanceof DestinationView)
			{
				DestinationView view = (DestinationView) part;
				return view.getSelectedDestination();
			}
		}
		return null;
	}

	public ReportService.Format getSelectedFormat()
	{
		IViewReference[] references = this.getSite().getPage().getViewReferences();
		for (IViewReference reference : references)
		{
			IViewPart part = reference.getView(true);
			if (part instanceof DestinationView)
			{
				DestinationView view = (DestinationView) part;
				return view.getSelectedFormat();
			}
		}
		return null;
	}

	@Override
	public void setFocus()
	{
		this.start.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		updateChildren();
		updateStartButton();
	}
	
	private void updateChildren()
	{
		Control[] controls = tabFolder.getChildren();
		for (Control control : controls)
		{
			if (control instanceof ISettlementCompositeChild)
			{
				ISettlementCompositeChild child = (ISettlementCompositeChild) control;
				child.setInput();
			}
		}
	}
	
	private boolean validateSelection()
	{
		return (getSelectedSalespoints() != null && getSelectedSalespoints().length > 0) && getSelectedDateRange() != null
				&& getSelectedDateRange().length == 2;	
	}

	private void updateStartButton()
	{
		if (this.tabFolder.getSelection().length > 0)
		{
			TabItem tabItem = this.tabFolder.getSelection()[0];
			if (tabItem.getControl() instanceof ISettlementCompositeChild)
			{
				ISettlementCompositeChild child = (ISettlementCompositeChild) tabItem.getControl();
				this.start.setEnabled(child.validateSelection() && this.validateSelection());
			}
		}
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		this.updateStartButton();
	}

	protected IStatus printReport(IProgressMonitor monitor, final InputStream report, final JRDataSource dataSource, final Map<String, Object> parameters)
	{
		if (this.selectedDestination == null)
		{
			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Sie haben kein Ausgabeziel ausgewählt.");
		}
		File exportFile = null;
		if (this.selectedDestination.equals(ReportService.Destination.EXPORT))
		{
			if (this.selectedFormat == null)
			{
				return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Sie haben kein Dateiformat ausgewählt.");
			}
			else
			{
				FileDialog dialog = new FileDialog(this.getSite().getShell());
				dialog.setFileName("*" + selectedFormat.extension());
				dialog.setFilterExtensions(new String[] { "*" + selectedFormat.extension() });
				dialog.setFilterIndex(0);
				dialog.setFilterNames(new String[] { selectedFormat.label() });
				dialog.setText("Datei");
				String path = dialog.open();
				if (path == null)
				{
					return new Status(IStatus.OK, Activator.PLUGIN_ID, "");
				}
				exportFile = new File(path);
			}
		}

		ServiceTracker<ReportService, ReportService> tracker = new ServiceTracker<ReportService, ReportService>(Activator.getDefault().getBundle().getBundleContext(), ReportService.class, null);
		try
		{
			tracker.open();
			final ReportService service = tracker.getService();
			if (service != null)
			{
				monitor.beginTask("Vorschau", 1);
				switch (this.selectedDestination)
				{
					case EXPORT:
					{
						service.export(report, dataSource, parameters, selectedFormat, exportFile);
						break;
					}
					case PRINTER:
					{
						service.print(report, dataSource, parameters, false);
						break;
					}
					case RECEIPT_PRINTER:
					{
					}
					case PREVIEW:
					{
						service.view(monitor, report, dataSource, parameters);
						monitor.worked(1);
						monitor.done();
						break;
					}
					default:
					{
						return new Status(IStatus.CANCEL, Activator.PLUGIN_ID,
								"Das ausgewählte Ausgabeziel ist nicht verfügbar.");
					}
				}
				monitor.beginTask("Vorschau", 1);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			tracker.close();
			monitor.done();
		}
		return Status.OK_STATUS;
	}
}