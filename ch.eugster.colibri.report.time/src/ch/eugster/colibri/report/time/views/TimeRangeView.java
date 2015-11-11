package ch.eugster.colibri.report.time.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PropertyResourceBundle;
import java.util.UUID;

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
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.DayTimeRow;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.report.daterange.views.DateView;
import ch.eugster.colibri.report.destination.views.DestinationView;
import ch.eugster.colibri.report.engine.ReportService;
import ch.eugster.colibri.report.salespoint.views.SalespointView;
import ch.eugster.colibri.report.time.Activator;

public class TimeRangeView extends ViewPart implements IViewPart, ISelectionListener, ISelectionChangedListener
{
	public static final String ID = "ch.eugster.colibri.report.timerange.view";

	private static final String[] keys = { "", "sunday", "monday", "tuesday", "wednesday", "thursday", "friday", "saturday" };

	private static final NumberFormat hourFormatter = new DecimalFormat("00");
	
	private IDialogSettings settings;

	private ReportService.Destination selectedDestination;

	private ReportService.Format selectedFormat;
	
	private Salespoint[] selectedSalespoints;
	
	private Calendar[] selectedDateRange;
	
	private Button start;
	
	private Spinner startTime;
	
	private Spinner endTime;
	
	private Button[] weekdays;
	
	private Button selectNonSalesToo;
	
	private Button showReceiptCount;
	
	private Button showReceiptStats;
	
	@Override
	public void init(IViewSite site) throws PartInitException
	{
		super.init(site);

		settings = Activator.getDefault().getDialogSettings().getSection(this.getClass().getName());
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection(this.getClass().getName());
		}
		setSetting("start.hour", 9);
		setSetting("end.hour", 18);
		boolean isSet = false;
		for (String key : keys)
		{
			if (key != null)
			{
				if (!isSet)
					isSet = this.settings.getBoolean(key);
				else
					break;
			}
		}
		if (!isSet)
		{
			for (String key : keys)
			{
				if (key != null)
				{
					this.settings.put(key, true);
				}
			}
		}

		ISelection sel = getSite().getWorkbenchWindow().getSelectionService().getSelection(SalespointView.ID);
		if (sel instanceof IStructuredSelection)
		{
			IStructuredSelection ssel = (IStructuredSelection) sel;
			Object[] objects = ssel.toArray();
			selectedSalespoints = new Salespoint[objects.length];
			for (int i = 0; i < objects.length; i++)
			{
				selectedSalespoints[i] = (Salespoint) objects[i];
			}
		}
		sel = getSite().getWorkbenchWindow().getSelectionService().getSelection(DateView.ID);
		if (sel instanceof IStructuredSelection)
		{
			IStructuredSelection ssel = (IStructuredSelection) sel;
			Object[] objects = ssel.toArray();
			selectedDateRange = new Calendar[objects.length];
			for (int i = 0; i < objects.length; i++)
			{
				selectedDateRange[i] = (Calendar) objects[i];
			}
		}
		sel = getSite().getWorkbenchWindow().getSelectionService().getSelection(DestinationView.ID);
		if (sel instanceof IStructuredSelection)
		{
			IStructuredSelection ssel = (IStructuredSelection) sel;
			selectedDestination = (ReportService.Destination) ssel.getFirstElement();
		}

		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(SalespointView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DateView.ID, this);
		getSite().getWorkbenchWindow().getSelectionService().addPostSelectionListener(DestinationView.ID, this);
	}
	
	private void setSetting(String key, int value)
	{
		try
		{
			settings.getInt(key);
		}
		catch (NumberFormatException e)
		{
			settings.put(key, value);
		}
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());
		
		Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());

		Group group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));
		group.setText("Zeitraum");
		
		Label label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Startstunde");
		
		GridData gridData = new GridData();
		gridData.widthHint = 64;
		
		startTime = new Spinner(group, SWT.BORDER);
		startTime.setLayoutData(gridData);
		startTime.setValues(settings.getInt("start.hour"), 0, 23, 0, 1, 6);
		startTime.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				updateStartButton();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Endstunde");
		
		gridData = new GridData();
		gridData.widthHint = 64;
		
		endTime = new Spinner(group, SWT.BORDER);
		endTime.setLayoutData(gridData);
		endTime.setValues(settings.getInt("end.hour"), 1, 24, 0, 1, 6);
		endTime.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				updateStartButton();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});

		group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout());
		group.setText("Wochentage");
		
		Composite dayComposite = new Composite(group, SWT.None);
		dayComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dayComposite.setLayout(new GridLayout(keys.length - 1, true));
		
		weekdays = new Button[keys.length];
		for (int i = 1; i < keys.length; i++)
		{
			weekdays[i] = new Button(dayComposite, SWT.CHECK);
			weekdays[i].setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			weekdays[i].setText(DateFormatSymbols.getInstance().getWeekdays()[i]);
			weekdays[i].setSelection(settings.getBoolean(keys[i]));
			weekdays[i].setData("weekday", i);
			weekdays[i].addSelectionListener(new SelectionListener() 
			{
				@Override
				public void widgetSelected(SelectionEvent e) 
				{
					Button dayButton = (Button) e.getSource();
					Integer weekday = (Integer) dayButton.getData("weekday");
					TimeRangeView.this.settings.put(keys[weekday.intValue()], weekdays[weekday.intValue()].getSelection());
					updateStartButton();
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e) 
				{
					widgetSelected(e);
				}
			});
			weekdays[i].setSelection(this.settings.getBoolean(keys[i]));
		}

		Composite dayButtonComposite = new Composite(group, SWT.None);
		dayButtonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dayButtonComposite.setLayout(new GridLayout(3, true));
		
		Button selectAll = new Button(dayButtonComposite, SWT.PUSH);
		selectAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectAll.setText("Alle auswählen");
		selectAll.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				for (Button weekday : weekdays)
				{
					if (weekday != null)
					{
						weekday.setSelection(true);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		Button selectNone = new Button(dayButtonComposite, SWT.PUSH);
		selectNone.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		selectNone.setText("Keinen auswählen");
		selectNone.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				for (Button weekday : weekdays)
				{
					if (weekday != null)
					{
						weekday.setSelection(false);
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		Button reverse = new Button(dayButtonComposite, SWT.PUSH);
		reverse.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		reverse.setText("Auswahl umkehren");
		reverse.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				for (Button weekday : weekdays)
				{
					if (weekday != null)
					{
						weekday.setSelection(!weekday.getSelection());
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		
		gridData = new GridData(GridData.FILL_BOTH);
		
		group = new Group(composite, SWT.SHADOW_ETCHED_IN);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));
		group.setLayout(new GridLayout());
		group.setText("Optionen");

		this.selectNonSalesToo = new Button(group, SWT.CHECK);
		this.selectNonSalesToo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.selectNonSalesToo.setText("Nicht umssatzrelevante Warengruppen berücksichtigen");
		this.selectNonSalesToo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				Button button = (Button) e.getSource();
				TimeRangeView.this.settings.put("non.sales.too", button.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		this.selectNonSalesToo.setSelection(this.settings.getBoolean("non.sales.too"));
		
		this.showReceiptCount = new Button(group, SWT.CHECK);
		this.showReceiptCount.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.showReceiptCount.setText("Anzahl Belege anzeigen");
		this.showReceiptCount.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				Button button = (Button) e.getSource();
				TimeRangeView.this.settings.put("show.receipt.count", button.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		this.showReceiptCount.setSelection(this.settings.getBoolean("show.receipt.count"));
		
		this.showReceiptStats = new Button(group, SWT.CHECK);
		this.showReceiptStats.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.showReceiptStats.setText("Durchschnittlichen Belegbetrag anzeigen");
		this.showReceiptStats.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				Button button = (Button) e.getSource();
				TimeRangeView.this.settings.put("show.receipt.stat", button.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		this.showReceiptStats.setSelection(this.settings.getBoolean("show.receipt.stat"));
		
		label = new Label(composite, SWT.None);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText("Die Tagesstatistik liefert Informationen über die Umsätze im Tagesverlauf einer oder mehrerer Kassen innerhalb des gewählten Zeitraums.\n\n So führen Sie eine Tagesstatistik durch:\n\n- Wählen Sie die Kassen, deren Belege in der Statistik berücksichtigt werden sollen\n- Wählen Sie Anfangs- und Enddatum\n- Wählen Sie den Zeitraum aus\n- Wählen Sie den oder die gewünschten Wochentage aus\n- Wählen Sie die gewünschten Optionen");

		Composite buttonComposite = new Composite(parent, SWT.NONE);
		buttonComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		buttonComposite.setLayout(new GridLayout(2, false));

		label = new Label(buttonComposite, SWT.NONE);
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
				try
				{
					if (!validSelection())
					{
						MessageDialog.openInformation(TimeRangeView.this.getViewSite().getShell(), "Ungültiger Zeitraum", "Die Endstunde muss grösser sein als die Anfangsstunde.");
					}
					final JRDataSource dataSource = createDataSource();
					if (dataSource == null)
					{
						MessageDialog.openInformation(TimeRangeView.this.getViewSite().getShell(), "Keine Daten vorhanden", "Für die gewählten Selektionen sind keine Daten verfügbar.");
					}
					else
					{
						final Hashtable<String, Object> parameters = getParameters();
						final InputStream report = getReport();
						if (report == null)
						{
							MessageDialog.openWarning(TimeRangeView.this.getViewSite().getShell(), "Ungültige Berichtsvorlage", "Die verwendete Berichtsvorlage ist ungültig. Bitte wenden Sie sich an den Hersteller der Kasse, damit dieser das Problem beheben kann.");
						}
						else
						{
							start.setEnabled(false);
							printReport(report, dataSource, parameters);
						}
					}
				}
				finally
				{
					start.setEnabled(true);
				}
			}
		});
		
		ISelectionService service = this.getSite().getWorkbenchWindow().getSelectionService();
		service.addPostSelectionListener(DateView.ID, this);
		service.addPostSelectionListener(SalespointView.ID, this);
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	@Override
	public void setFocus()
	{
		this.startTime.setFocus();
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection)
	{
		if (part instanceof DestinationView)
		{
			DestinationView view = (DestinationView) part;
			this.selectedDestination = view.getSelectedDestination();
			if (selectedDestination.equals(ReportService.Destination.EXPORT))
			{
				selectedFormat = view.getSelectedFormat();
			}
		}
		if (part instanceof SalespointView)
		{
			SalespointView view = (SalespointView) part;
			selectedSalespoints = view.getSelectedSalespoints();
		}
		if (part instanceof DateView)
		{
			DateView view = (DateView) part;
			selectedDateRange = view.getDates();
		}
		updateStartButton();
	}

	private void updateStartButton()
	{
		boolean enabled = false;
		for (Button weekday : weekdays)
		{
			if (weekday != null && weekday.getSelection())
			{
				enabled = true;
				break;
			}
		}
		enabled = enabled && validSelection();
		enabled = enabled && this.selectedDestination != null;
		enabled = enabled && this.selectedSalespoints != null && this.selectedSalespoints.length > 0;
		enabled = enabled && this.selectedDateRange != null;
		this.start.setEnabled(enabled);
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		updateStartButton();
	}

	protected IStatus printReport(final InputStream report, final JRDataSource dataSource, final Map<String, Object> parameters)
	{
		
		if (this.selectedDestination == null)
		{
			return new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Sie haben kein Ausgabeziel ausgewählt.");
		}
		File exportFile = null;
		if (selectedDestination.equals(ReportService.Destination.EXPORT))
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
				switch (selectedDestination)
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
						UIJob job = new UIJob("preview")
						{
							@Override
							public IStatus runInUIThread(IProgressMonitor monitor) 
							{
								service.view(new SubProgressMonitor(monitor, 1), report, dataSource, parameters);
								return Status.OK_STATUS;
							}
						};
						job.setSystem(true);
						job.schedule();
						break;
					}
					default:
					{
						return new Status(IStatus.CANCEL, Activator.PLUGIN_ID,
								"Das ausgewählte Ausgabeziel ist nicht verfügbar.");
					}
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			tracker.close();
		}
		return Status.OK_STATUS;
	}
	
	private int[] getSelectedWeekdays()
	{
		List<Integer> selectedDays = new ArrayList<Integer>();
		for (int i = 1; i < weekdays.length; i++)
		{
			if (weekdays[i] != null && weekdays[i].getSelection())
			{
				selectedDays.add(Integer.valueOf((int)weekdays[i].getData("weekday")));
			}
		}
		int[] days = new int[selectedDays.size()];
		for (int i = 0; i < selectedDays.size(); i++)
		{
			days[i] = selectedDays.listIterator(i).next();
		}
		return days;
	}

	private int[] getSelectedHours()
	{
		int[] hourRange = new int[2];
		hourRange[0] = this.startTime.getSelection();
		hourRange[1] = this.endTime.getSelection() - 1;
		return hourRange;
	}

	protected Collection<DayTimeRow> selectItems()
	{
		Collection<DayTimeRow> result = null;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		try
		{
			tracker.open();
			PersistenceService service = (PersistenceService) tracker.getService();
			if (service != null)
			{
				final PositionQuery query = (PositionQuery) service.getServerService().getQuery(Position.class);
				result = query.selectDayHourStatisticsRange(this.selectedSalespoints, this.selectedDateRange, this.getSelectedWeekdays(), this.getSelectedHours(), this.selectNonSalesToo.getSelection(), showReceiptCount.getSelection());
			}
		}
		finally
		{
			tracker.close();
		}
		return result;
	}

	public JRDataSource createDataSource()
	{
		Collection<DayTimeRow> items = selectItems();
		return items.isEmpty() ? null : new JRMapArrayDataSource(items.toArray(new DayTimeRow[0]));
	}

	public boolean validSelection()
	{
		return startTime.getSelection() < (endTime.getSelection());
	}

	private Element createTextField(Namespace ns, String pattern, int x, int width, int y, int height, String cdata, String textAlignment)
	{
		Element textField = new Element("textField", ns);
		textField.setAttribute(new Attribute("pattern", pattern));
		textField.setAttribute(new Attribute("isBlankWhenNull", "true"));
		Element reportElement = new Element("reportElement", ns);
		reportElement.setAttribute(new Attribute("uuid", UUID.randomUUID().toString()));
		reportElement.setAttribute(new Attribute("positionType", "Float"));
		reportElement.setAttribute(new Attribute("x", Integer.valueOf(x).toString()));
		reportElement.setAttribute(new Attribute("y", Integer.valueOf(y).toString()));
		reportElement.setAttribute(new Attribute("width", Integer.valueOf(width).toString()));
		reportElement.setAttribute(new Attribute("height", Integer.valueOf(height).toString()));
		Element textElement = new Element("textElement", ns);
		textElement.setAttribute(new Attribute("textAlignment", textAlignment));
		textElement.setAttribute(new Attribute("verticalAlignment", "Middle"));
		Element font = new Element("font", ns);
		font.setAttribute(new Attribute("fontName", "Arial"));
		font.setAttribute(new Attribute("size", "8"));
		Element textFieldExpression = new Element("textFieldExpression", ns);
		textFieldExpression.setContent(new CDATA(cdata));
		textElement.addContent(font);
		textField.addContent(reportElement);
		textField.addContent(textElement);
		textField.addContent(textFieldExpression);
		return textField;
	}

	private Element createLabel(Namespace ns, int x, int width, int y, int height, int fontSize, String value)
	{
		Element label = new Element("staticText", ns);
		Element reportElement = new Element("reportElement", ns);
		reportElement.setAttribute(new Attribute("positionType", "Float"));
		reportElement.setAttribute(new Attribute("x", new Integer(x).toString()));
		reportElement.setAttribute(new Attribute("y", new Integer(y).toString()));
		reportElement.setAttribute(new Attribute("width", new Integer(width).toString()));
		reportElement.setAttribute(new Attribute("height", new Integer(height).toString()));
		reportElement.setAttribute(new Attribute("uuid", "c43dc746-19ac-42e1-b880-c76b17638d9c"));
		Element textElement = new Element("textElement", ns);
		Element font = new Element("font", ns);
		font.setAttribute(new Attribute("size", new Integer(fontSize).toString()));
		textElement.addContent(font);
		Element text = new Element("text", ns);
		CDATA cdata = new CDATA(value);
		text.addContent(cdata);
		label.addContent(reportElement);
		label.addContent(textElement);
		label.addContent(text);
		return label;
	}

	private int getHours()
	{
		return endTime.getSelection() - startTime.getSelection();
	}
	
	public InputStream getReport()
	{
		URL url = Activator.getDefault().getBundle().getEntry("reports/DayHourStatistics.jrxml");
		try {
			InputStream is = url.openStream();
			SAXBuilder builder = new SAXBuilder();
			Document document = builder.build(is);
			is.close();

			Namespace ns = Namespace.getNamespace("http://jasperreports.sourceforge.net/jasperreports");
			// The pageheader section
			Element band = document.getRootElement().getChild("pageHeader", ns).getChild("band", ns);
			int count = getHours();
			int width = (708 - 80) / count;
			for (int i = 0; i < count; i++)
			{
				int x = 80 + (i * width);
				int y = 4;
				int no = startTime.getSelection() + i;
				band.addContent(createTextField(ns, "##,##0.00", x, width, y, 12, "$P{amount@" + no + "}", "Right"));
			}

			// The detail section
			
			band = document.getRootElement().getChild("detail", ns).getChild("band", ns);
			int y = 4;
			if (this.showReceiptCount.getSelection())
			{
				y += 12;
				int height = band.getAttribute("height").getIntValue();
				band.setAttribute(new Attribute("height", new Integer(height + 12).toString()));
				band.addContent(createLabel(ns, 20, 60, 16, 12, 8, "Belege"));
				band.addContent(createTextField(ns, "##,##0", 696, 48, y, 12, "$F{count}", "Right"));
			}
			if (this.showReceiptStats.getSelection())
			{
				y += 12;
				int height = band.getAttribute("height").getIntValue();
				band.setAttribute(new Attribute("height", new Integer(height + 12).toString()));
				band.addContent(createLabel(ns, 20, 60, 28, 12, 8, "Betrag/Beleg"));
				band.addContent(createTextField(ns, "##,##0.00", 696, 48, y, 12, "$V{stats}", "Right"));
			}
			for (int i = 0; i < count; i++)
			{
				int x = 80 + (i * width);
				y = 4;
				int no = startTime.getSelection() + i;
				band.addContent(createTextField(ns, "##,##0.00", x, width, y, 12, "$F{amount@" + no + "}", "Right"));
				if (this.showReceiptCount.getSelection())
				{
					y += 12;
					band.addContent(createTextField(ns, "##,##0", x, width, y, 12, "$F{count@" + no + "}", "Right"));
				}
				if (this.showReceiptStats.getSelection())
				{
					y +=12;
					band.addContent(createTextField(ns, "##,##0.00", x, width, y, 12, "$V{stats@" + no + "}", "Right"));
				}
			}

			band = document.getRootElement().getChild("summary", ns).getChild("band", ns);
			int height = band.getAttribute("height").getIntValue();
			y = 8;
			if (this.showReceiptCount.getSelection())
			{
				y += 12;
				band.setAttribute(new Attribute("height", new Integer(height + y).toString()));
				band.addContent(createLabel(ns, 20, 60, y, 12, 8, "Belege"));
				band.addContent(createTextField(ns, "##,##0", 696, 48, y, 12, "$F{count}", "Right"));
			}
			if (this.showReceiptStats.getSelection())
			{
				y += 12;
				band.setAttribute(new Attribute("height", new Integer(height + y).toString()));
				band.addContent(createLabel(ns, 20, 60, y, 12, 8, "Betrag/Beleg"));
				band.addContent(createTextField(ns, "##,##0.00", 696, 48, y, 12, "$V{stats}", "Right"));
			}
			for (int i = 0; i < count; i++)
			{
				int x = 80 + i * width;
				y = 8;
				int no = startTime.getSelection() + i;
				band.addContent(createTextField(ns, "##,##0.00", x, width, y, 12, "$V{amount@" + no + "}", "Right"));
				if (this.showReceiptCount.getSelection())
				{
					y +=12;
					band.addContent(createTextField(ns, "##,##0", x, width, y, 12, "$V{count@" + no + "}", "Right"));
				}
				if (this.showReceiptStats.getSelection())
				{
					y +=12;
					band.addContent(createTextField(ns, "##,##0.00", x, width, y, 12, "$V{stats@" + no + "}", "Right"));
				}
			}
			List<Element> lines = band.getChildren("line", ns);
			Element endLine = lines.get(lines.size() - 1);
			Element reportElement = endLine.getChild("reportElement", ns);
			reportElement.setAttribute("y", new Integer(band.getAttribute("height").getIntValue() - 2).toString());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			XMLOutputter outputter = new XMLOutputter();
			outputter.output(document, out);
			is = new ByteArrayInputStream(out.toByteArray());
			return is;
			
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (JDOMException e) 
		{
			e.printStackTrace();
		}
		return null;
	}

	public Hashtable<String, Object> getParameters()
	{
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("header", this.selectedSalespoints[0].getCommonSettings().getAddress());
		parameters.put("printTime",
				SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance(Locale.getDefault()).getTime()));
		StringBuilder salespointNames = new StringBuilder();
		for (Salespoint salespoint : selectedSalespoints)
		{
			if (salespoint != null)
			{
				salespointNames.append(salespoint.getName() + " ");
			}
		}
		parameters.put("salespoints", salespointNames.toString());

		parameters.put("name", "Kasse");
		
		int count = getHours();
		for (int i = 0; i < count; i++)
		{
			parameters.put("amount@" + new Integer(startTime.getSelection() + i).toString(), hourFormatter.format(startTime.getSelection() + i) + "-" + hourFormatter.format(startTime.getSelection() + i + 1));
		}
		parameters.put("amount", "Total");
		
		StringBuilder days = new StringBuilder("Gewählte Wochentage: ");
		for (int i = 0; i < weekdays.length; i++)
		{
			if (weekdays[i] != null && weekdays[i].getSelection())
			{
				days = days.append(weekdays[i].getText());
				if (i < weekdays.length - 1)
				{
					days = days.append(", ");
				}
			}
		}
		if (this.selectNonSalesToo.getSelection())
		{
			days.append("nicht umsatzrelevante Warengruppen berücksichtigt");
		}
		parameters.put("weekday", days.toString());

		StringBuilder range = new StringBuilder("Gewählter Zeitraum: ");
		range = range.append(SimpleDateFormat.getDateInstance().format(selectedDateRange[0].getTime()));
		range = range.append(SimpleDateFormat.getDateInstance().format(selectedDateRange[1].getTime()));
		parameters.put("dateRange", range.toString());
		
		URL entry = Activator.getDefault().getBundle().getEntry("/reports/DayHourStatistics.properties");
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
}