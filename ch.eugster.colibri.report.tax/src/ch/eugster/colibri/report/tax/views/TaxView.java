package ch.eugster.colibri.report.tax.views;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

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
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
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

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.report.daterange.views.DateView;
import ch.eugster.colibri.report.destination.views.DestinationView;
import ch.eugster.colibri.report.engine.ReportService;
import ch.eugster.colibri.report.salespoint.views.SalespointView;
import ch.eugster.colibri.report.tax.Activator;
import ch.eugster.colibri.report.tax.PositionEntry;

public class TaxView extends ViewPart implements IViewPart, ISelectionListener, ISelectionChangedListener
{
	public static final String ID = "ch.eugster.colibri.report.tax.view";

	private IDialogSettings settings;

	private Button start;

	private ReportService.Destination selectedDestination;

	private ReportService.Format selectedFormat;
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);

		settings = Activator.getDefault().getDialogSettings().getSection(TaxView.class.getName());
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection(TaxView.class.getName());
		}
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());
		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
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
				final JRDataSource dataSource = createDataSource();
				if (dataSource == null)
				{
					MessageDialog.openInformation(null, "Keine Daten vorhanden", "Für die gewählte Selektion sind keine Daten vorhanden.");
					return;
				}
				selectedDestination = getSelectedDestination();
				selectedFormat = getSelectedFormat();
				final Hashtable<String, Object> parameters = getParameters();
				final URL url = Activator.getDefault().getBundle().getEntry("reports/" + "TaxAccounting" + ".jrxml");
				UIJob job = new UIJob("Auswertung wird aufbereitet...")
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						try
						{
//								start.setEnabled(false);
							monitor.beginTask("Auswertung wird aufbereitet. Bitte warten Sie...", IProgressMonitor.UNKNOWN);
							final InputStream report = url.openStream();
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
				job.schedule();
			}
		});
		
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(SalespointView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DateView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DestinationView.ID, this);

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

	public JRDataSource createDataSource()
	{
		JRDataSource dataSource = null;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = (PersistenceService) tracker.getService();
			if (service != null)
			{
				List<PositionEntry> entries = new ArrayList<PositionEntry>();
				Calendar[] dateRange = getSelectedDateRange();
				PositionQuery query = (PositionQuery) service.getServerService().getQuery(Position.class);
				List<Position> positions = query.selectBySalespointsAndDateRange(getSelectedSalespoints(), dateRange);
				for (Position position : positions)
				{
					entries.add(new PositionEntry(position));
				}
				if (entries.size() > 0)
				{
					dataSource = new JRMapArrayDataSource(entries.toArray(new PositionEntry[0]));
				}
			}
		}
		catch (Exception e){}
		finally
		{
			tracker.close();
		}

		return dataSource;
	}

	@Override
	public void setFocus()
	{
		this.start.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		updateStartButton();
	}
	
	private boolean validateSelection()
	{
		return (getSelectedSalespoints() != null && getSelectedSalespoints().length > 0) && getSelectedDateRange() != null
				&& getSelectedDateRange().length == 2;	
	}

	private void updateStartButton()
	{
		this.start.setEnabled(this.validateSelection());
	}

	private String getSalespointList()
	{
		StringBuilder salespoints = new StringBuilder();
		if (getSelectedSalespoints() != null && getSelectedSalespoints().length > 0)
		{
			for (Salespoint selectedSalespoint : this.getSelectedSalespoints())
			{
				if (salespoints.length() > 0)
				{
					salespoints.append(", ");
				}
				salespoints.append(selectedSalespoint.getName());
			}
		}
		return salespoints.toString();
	}

	private String getDateRangeList()
	{
		if (getSelectedDateRange() != null && getSelectedDateRange().length == 2)
		{
			if (getSelectedDateRange()[0].get(Calendar.YEAR) == this.getSelectedDateRange()[1].get(Calendar.YEAR))
			{
				if (getSelectedDateRange()[0].get(Calendar.MONTH) == getSelectedDateRange()[1].get(Calendar.MONTH))
				{
					if (getSelectedDateRange()[0].get(Calendar.DATE) == getSelectedDateRange()[1].get(Calendar.DATE))
					{
						return SimpleDateFormat.getDateInstance().format(getSelectedDateRange()[0].getTime());
					}
				}
			}

			StringBuilder builder = new StringBuilder();
			builder = builder.append(SimpleDateFormat.getDateInstance().format(getSelectedDateRange()[0].getTime()));
			builder = builder.append(" bis ");
			builder = builder.append(SimpleDateFormat.getDateInstance().format(getSelectedDateRange()[1].getTime()));
			return builder.toString();
		}
		return "";
	}

	private Hashtable<String, Object> getParameters()
	{
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		final String header = "Header";
		parameters.put("header", header);
		parameters.put("printTime",
				SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance(Locale.getDefault()).getTime()));
		parameters.put("salespoints", getSalespointList());
		parameters.put("dateRange", getDateRangeList());
		URL entry = Activator.getDefault().getBundle().getEntry("/reports/TaxAccounting.properties");
		try
		{
			InputStream stream = entry.openStream();
			parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, new PropertyResourceBundle(stream));
		}
		catch (Exception e)
		{
		}
		return parameters;
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