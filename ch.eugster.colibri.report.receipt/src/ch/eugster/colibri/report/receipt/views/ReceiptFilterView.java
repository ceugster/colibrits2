package ch.eugster.colibri.report.receipt.views;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.PropertyResourceBundle;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.queries.SettlementQuery;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.report.destination.views.DestinationView;
import ch.eugster.colibri.report.engine.ReportService;
import ch.eugster.colibri.report.receipt.Activator;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class ReceiptFilterView extends ViewPart implements ISelectionProvider, ISelectionChangedListener
{
	public static final String ID = "ch.eugster.colibri.report.receipt.filterview";

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private IDialogSettings settings;

	private DateTime date;

	private Text number;

	private ComboViewer salespointViewer;

	private ComboViewer settlementViewer;

	private ComboViewer userViewer;

	private ComboViewer stateViewer;
	
	private ComboViewer productGroupViewer;
	
	private ComboViewer paymentTypeViewer;
	
	private FormattedText amount;

	private Button loadReceiptList;
	
	private Button printReceiptList;

	private int receiptCount = 0;
	
	private final ListenerList listeners = new ListenerList();

	@Override
	public void createPartControl(final Composite parent)
	{
		parent.setLayout(new GridLayout());

		final Composite composite = new Composite(parent, SWT.None);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());

		Group group = new Group(composite, SWT.NONE);
		group.setText("Selektion");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(3, false));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;

		Label label = new Label(group, SWT.WRAP);
		label.setLayoutData(gridData);
		label.setText("Selektieren Sie nach Datum und Kasse...");

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Datum");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		this.date = new DateTime(group, SWT.DATE | SWT.MEDIUM | SWT.BORDER);
		this.date.setLayoutData(gridData);
		this.date.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				DateTime dateTime = (DateTime) e.getSource();
				Calendar calendar = getStartDate(dateTime);
				ReceiptFilterView.this.settings.put("date.selection", calendar.getTimeInMillis());
				updateSettlementViewer();
//				StructuredSelection ssel = selectReceipts();
//				SelectionChangedEvent event = new SelectionChangedEvent(ReceiptFilterView.this, ssel);
//				ReceiptFilterView.this.fireSelectionChanged(event);
			}
		});

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Kasse");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		Combo combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(gridData);

		this.salespointViewer = new ComboViewer(combo);
		this.salespointViewer.setContentProvider(new SalespointContentProvider());
		this.salespointViewer.setLabelProvider(new SalespointLabelProvider());
		this.salespointViewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.salespointViewer.setSorter(new SalespointSorter());
		this.salespointViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				updateSettlementViewer();
			}
		});
		this.salespointViewer.addSelectionChangedListener(this);

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;

		label = new Label(group, SWT.SEPARATOR | SWT.HORIZONTAL);
		label.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 3;

		label = new Label(group, SWT.NONE);
		label.setLayoutData(gridData);
		label.setText("oder nach Belegnummer...");

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Belegnummer");

		this.number = new Text(group, SWT.BORDER);
		this.number.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.number.addVerifyListener(new VerifyListener()
		{
			@Override
			public void verifyText(VerifyEvent e)
			{
				e.doit = e.text.isEmpty() || Character.isDigit(e.character);
			}
		});
		this.number.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				final String value = ((Text) e.getSource()).getText();
				ReceiptFilterView.this.settings.put("number.selection", value);

				settlementViewer.getControl().setEnabled(number.getText().isEmpty());
				stateViewer.getControl().setEnabled(number.getText().isEmpty());
				userViewer.getControl().setEnabled(number.getText().isEmpty());
			}
		});

		Button clear = new Button(group, SWT.PUSH);
		clear.setLayoutData(new GridData());
		clear.setImage(Activator.getDefault().getImageRegistry().get("clear"));
		clear.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(SelectionEvent event)
			{
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				number.setText("");
				settings.put("number.selection", "");
			}
		});

		group = new Group(composite, SWT.NONE);
		group.setText("Belegfilter");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(2, false));

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Abschluss");

		combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.settlementViewer = new ComboViewer(combo);
		this.settlementViewer.setContentProvider(new SettlementContentProvider());
		this.settlementViewer.setLabelProvider(new SettlementLabelProvider());
		this.settlementViewer.setSorter(new SettlementSorter());
		this.settlementViewer.addSelectionChangedListener(this);

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Belegstatus");

		combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.stateViewer = new ComboViewer(combo);
		this.stateViewer.setContentProvider(new ArrayContentProvider());
		this.stateViewer.setLabelProvider(new LabelProvider());
		this.stateViewer.addSelectionChangedListener(this);

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Benutzer");

		combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		this.userViewer = new ComboViewer(combo);
		this.userViewer.setContentProvider(new UserContentProvider());
		this.userViewer.setLabelProvider(new UserLabelProvider());
		this.userViewer.setSorter(new UserSorter());
		this.userViewer.addSelectionChangedListener(this);

		group = new Group(composite, SWT.NONE);
		group.setText("Positionenfilter");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(3, false));

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Warengruppe");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		
		combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(gridData);

		this.productGroupViewer = new ComboViewer(combo);
		this.productGroupViewer.setContentProvider(new ProductGroupContentProvider());
		this.productGroupViewer.setLabelProvider(new ProductGroupLabelProvider());
		this.productGroupViewer.setSorter(new ProductGroupSorter());
		this.productGroupViewer.addSelectionChangedListener(this);

		group = new Group(composite, SWT.NONE);
		group.setText("Zahlungsfilter");
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setLayout(new GridLayout(3, false));

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Zahlungsart");

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		
		combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(gridData);

		this.paymentTypeViewer = new ComboViewer(combo);
		this.paymentTypeViewer.setContentProvider(new PaymentTypeContentProvider());
		this.paymentTypeViewer.setLabelProvider(new PaymentTypeLabelProvider());
		this.paymentTypeViewer.setSorter(new PaymentTypeSorter());
		this.paymentTypeViewer.addSelectionChangedListener(this);

		label = new Label(group, SWT.None);
		label.setLayoutData(new GridData());
		label.setText("Betrag");

		final Text text = new Text(group, SWT.BORDER | SWT.SINGLE);
		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		text.addFocusListener(new FocusAdapter() 
		{
			@Override
			public void focusGained(FocusEvent e) 
			{
				text.selectAll();
				
			}
		});
		
		this.amount = new FormattedText(text);
		this.amount.setFormatter(new NumberFormatter("#,###,##0.00", "#,###,##0.00"));
		this.amount.getControl().addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				Double amount = Double.valueOf(0D);
				Object value = ReceiptFilterView.this.amount.getValue();
				try
				{
					amount = Double.valueOf(value == null ? Double.valueOf(0D).toString() : value.toString());
				}
				catch (NumberFormatException nfe)
				{
					
				}
				IStructuredSelection ssel = new StructuredSelection(new Double[] { amount });
				SelectionChangedEvent event = new SelectionChangedEvent(ReceiptFilterView.this, ssel);
				ReceiptFilterView.this.fireSelectionChanged(event);
			}
		});
		
		clear = new Button(group, SWT.PUSH);
		clear.setLayoutData(new GridData());
		clear.setImage(Activator.getDefault().getImageRegistry().get("clear"));
		clear.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(SelectionEvent event)
			{
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				Double value = Double.valueOf(0D);
				amount.setValue(value);
				settings.put("amount.selection", value);
			}
		});

		GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		
		Composite filler = new Composite(composite, SWT.None);
		filler.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		filler.setLayout(gridLayout);
		
//		gridLayout = new GridLayout(2, false);
//		gridLayout.marginHeight = 0;
//		gridLayout.marginWidth = 0;
//		
//		filler = new Composite(composite, SWT.BORDER);
//		filler.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//		filler.setLayout(gridLayout);
		
//		Composite filler2 = new Composite(filler, SWT.BORDER);
//		filler2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		loadReceiptList = new Button(filler, SWT.PUSH);
		loadReceiptList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		loadReceiptList.setText("Belegliste laden");
		loadReceiptList.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(SelectionEvent event)
			{
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				loadReceiptList();
			}
		});

//		filler2 = new Composite(filler, SWT.BORDER);
//		filler2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		printReceiptList = new Button(filler, SWT.PUSH);
		printReceiptList.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		printReceiptList.setText("Belegliste drucken");
		printReceiptList.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(SelectionEvent event)
			{
				widgetSelected(event);
			}

			@Override
			public void widgetSelected(SelectionEvent event)
			{
				printReceiptList();
			}
		});

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService persistenceService = (PersistenceService) super.addingService(reference);
				ReceiptFilterView.this.initializeSelection(persistenceService);
				return persistenceService;
			}

			@Override
			public void removedService(final ServiceReference<PersistenceService> reference, final PersistenceService service)
			{
				ReceiptFilterView.this.initializeSelection(null);
			}
		};
		this.persistenceServiceTracker.open();

		this.getSite().setSelectionProvider(this);

		this.setFocus();
	}
	
	private void loadReceiptList()
	{
		StructuredSelection ssel = selectReceipts();
		SelectionChangedEvent event = new SelectionChangedEvent(ReceiptFilterView.this, ssel);
		ReceiptFilterView.this.fireSelectionChanged(event);
	}
	
	private void printReceiptList()
	{
		final DestinationObject destination = getDestination();
		if (destination == null)
		{
			MessageDialog.openInformation(null, "Keine Destination ausgewählt", "Sie haben keine Destination selektiert.");
			return;
		}
		if (destination.destination.equals(ReportService.Destination.EXPORT) && destination.format == null)
		{
			MessageDialog.openInformation(null, "Kein Ausgabeformat ausgewählt", "Sie haben kein Ausgabeformat selektiert.");
			return;
		}

		final JRDataSource dataSource = createDataSource();
		if (dataSource == null)
		{
			MessageDialog.openInformation(null, "Keine Daten vorhanden", "Für die gewählte Selektion sind keine Daten vorhanden (aus der Vorgängerversion übernommene Daten sind für diese Auswertung nicht abrufbar).");
			return;
		}

		UIJob job = new UIJob("Auswertung wird aufbereitet...")
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor)
			{
				try
				{
//					start.setEnabled(false);
					monitor.beginTask("Auswertung wird aufbereitet. Bitte warten Sie...", IProgressMonitor.UNKNOWN);
					final InputStream report = getReport();
					printReport(new SubProgressMonitor(monitor, 1), report, dataSource, getParameters(), destination.destination, destination.format);
				}
				catch (IOException e1)
				{
				}
				finally
				{
					monitor.done();
//					start.setEnabled(true);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	private DestinationObject getDestination()
	{
		DestinationObject destination = new DestinationObject();
		IViewReference[] references = this.getSite().getWorkbenchWindow().getActivePage().getViewReferences();
		for (IViewReference reference : references)
		{
			if (reference.getId().equals(DestinationView.ID))
			{
				DestinationView view = (DestinationView) reference.getView(true);
				if (view == null)
				{
					MessageDialog.openInformation(null, "Keine Destination ausgewählt", "Sie haben keine Destination selektiert.");
					return null;
				}
				else
				{
					if (view.getSelectedDestination() != null)
					{
						destination.destination = view.getSelectedDestination();
					}
					if (view.getSelectedFormat() != null)
					{
						destination.format = view.getSelectedFormat();
					}
				}
			}
		}
		return destination;
	}
	
	private JRDataSource createDataSource()
	{
		JRDataSource dataSource = null;
		Long receiptNumber = null;
		if (number.getText().length() > 0)
		{
			try
			{
				receiptNumber = Long.valueOf(number.getText());
			}
			catch (NumberFormatException e)
			{
			}
		}
		if (receiptNumber == null)
		{
			IStructuredSelection ssel = (IStructuredSelection) salespointViewer.getSelection();
			if (ssel.isEmpty())
			{
				MessageDialog.openInformation(null, "Keine Kasse ausgewählt", "Sie haben keine Kasse selektiert.");
				return null;
			}
			else
			{
				Salespoint salespoint = (Salespoint) ssel.getFirstElement();
				Calendar[] dateRange = new Calendar[2];
				dateRange[0] = getStartDate(date);
				dateRange[1] = getEndDate(dateRange[0]);
				dataSource = createDataSource(salespoint, dateRange);
			}
		}
		else
		{
			dataSource = createDataSource(receiptNumber);
		}
		return dataSource;
	}
	
	private Map<String, Object> getParameters()
	{
		NumberFormat nf = DecimalFormat.getIntegerInstance();
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		final String header = "Header";
		parameters.put("header", header);
		parameters.put("printTime",
				SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()));
		parameters.put("salespoint", getSalespoint().getName());
		parameters.put("date", SimpleDateFormat.getDateInstance().format(getStartDate(date).getTime()));
		parameters.put("receiptCount", nf.format(receiptCount));
		URL entry = Activator.getDefault().getBundle().getEntry("/reports/" + "ReceiptList" + ".properties");
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
	
	private JRDataSource createDataSource(Long receiptNumber)
	{
		receiptCount = 0;
		JRDataSource dataSource = null;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = (PersistenceService) tracker.getService();
			if (service != null)
			{
				Collection<Entry> entries = new ArrayList<Entry>();
				ReceiptQuery query = (ReceiptQuery) service.getServerService().getQuery(Receipt.class);
				Collection<Receipt> receipts = query.selectByNumber(receiptNumber);
				if (receipts.size() > 0)
				{
					for (Receipt receipt : receipts)
					{
						receiptCount++;
						Collection<Position> positions = receipt.getPositions();
						{
							for (Position position : positions)
							{
								entries.add(new Entry(position));
							}
						}
						Collection<Payment> payments = receipt.getPayments();
						{
							for (Payment payment : payments)
							{
								entries.add(new Entry(payment));
							}
						}
					}
				}
				if (!entries.isEmpty())
				{
					dataSource = new JRMapArrayDataSource(entries.toArray(new Entry[0]));
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return dataSource;
	}

	private JRDataSource createDataSource(Salespoint salespoint, Calendar[] dateRange)
	{
		receiptCount = 0;
		JRDataSource dataSource = null;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = (PersistenceService) tracker.getService();
			if (service != null)
			{
				IStructuredSelection ssel = (IStructuredSelection) this.settlementViewer.getSelection();
				Settlement settlement = (Settlement) ssel.getFirstElement();
				ssel = (IStructuredSelection) this.stateViewer.getSelection();
				ReceiptStateSelector state = (ReceiptStateSelector) ssel.getFirstElement();
				ssel = (IStructuredSelection) this.userViewer.getSelection();
				User user = (User) ssel.getFirstElement();
				Collection<Entry> entries = new ArrayList<Entry>();
				ReceiptQuery query = (ReceiptQuery) service.getServerService().getQuery(Receipt.class);
				Collection<Receipt> receipts = query.selectBySalespointAndDate(salespoint, dateRange[0], dateRange[1]);
				if (receipts.size() > 0)
				{
					for (Receipt receipt : receipts)
					{
						if (settlement.getId() == null || receipt.getSettlement().getId().equals(settlement.getId()))
						{
							if (state.matches(receipt.getState()))
							{
								if (user.getId() == null || (receipt.getUser() != null && receipt.getUser().getId().equals(user.getId())))
								{
									receiptCount++;
									Collection<Position> positions = receipt.getPositions();
									{
										for (Position position : positions)
										{
											entries.add(new Entry(position));
										}
									}
									Collection<Payment> payments = receipt.getPayments();
									{
										for (Payment payment : payments)
										{
											entries.add(new Entry(payment));
										}
									}
								}
							}
						}
					}
				}
				if (!entries.isEmpty())
				{
					dataSource = new JRMapArrayDataSource(entries.toArray(new Entry[0]));
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return dataSource;
	}

	private InputStream getReport() throws IOException
	{
		URL url = Activator.getDefault().getBundle().getEntry("reports/" + "ReceiptList" + ".jrxml");
		return url.openStream();
	}
	
	private void printReport(IProgressMonitor monitor, InputStream report, JRDataSource dataSource, Map<String, Object> parameters, ReportService.Destination destination, ReportService.Format format)
	{
		File exportFile = null;
		if (destination.equals(ReportService.Destination.EXPORT))
		{
			FileDialog dialog = new FileDialog(this.getSite().getShell());
			dialog.setFileName("*" + format.extension());
			dialog.setFilterExtensions(new String[] { "*" + format.extension() });
			dialog.setFilterIndex(0);
			dialog.setFilterNames(new String[] { format.label() });
			dialog.setText("Datei");
			String path = dialog.open();
			if (path == null)
			{
				return;
			}
			exportFile = new File(path);
		}

		ServiceTracker<ReportService, ReportService> tracker = new ServiceTracker<ReportService, ReportService>(Activator.getDefault().getBundle().getBundleContext(), ReportService.class, null);
		try
		{
			tracker.open();
			final ReportService service = tracker.getService();
			if (service != null)
			{
				monitor.beginTask("Vorschau", 1);
				switch (destination)
				{
					case EXPORT:
					{
						service.export(report, dataSource, parameters, format, exportFile);
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
						MessageDialog.openInformation(null, "Destination nicht verfügbar", "Das ausgewählte Ausgabeziel ist nicht verfügbar.");
						return;
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
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		super.dispose();
	}

	private Calendar getStartDate(DateTime dateTime)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.set(Calendar.YEAR, dateTime.getYear());
		calendar.set(Calendar.MONTH, dateTime.getMonth());
		calendar.set(Calendar.DATE, dateTime.getDay());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar;
	}

	private Calendar getEndDate(Calendar startDate)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		calendar.setTimeInMillis(startDate.getTimeInMillis());
		calendar.add(Calendar.DATE, 1);
		calendar.add(Calendar.SECOND, -1);
		return calendar;
	}

	private StructuredSelection selectReceipts()
	{
		PersistenceService service = (PersistenceService) persistenceServiceTracker.getService();
		if (service == null)
		{
			return new StructuredSelection();
		}
		else
		{
			ReceiptQuery query = (ReceiptQuery) service.getServerService().getQuery(Receipt.class);
			Collection<Receipt> receipts = null;

			if (!number.getText().isEmpty())
			{
				Long value = Long.valueOf(this.number.getText());
				receipts = query.selectByNumber(value);
			}
			else
			{
				Calendar startDate = getStartDate(date);
				Calendar endDate = getEndDate(startDate);

				StructuredSelection ssel = (StructuredSelection) salespointViewer.getSelection();
				Salespoint salespoint = (Salespoint) ssel.getFirstElement();
				{
					receipts = query.selectBySalespointAndDate(salespoint, startDate,
							endDate);
				}
			}
			return new StructuredSelection(receipts.toArray(new Receipt[0]));
		}
	}

	private Settlement[] selectSettlements()
	{
		PersistenceService service = (PersistenceService) persistenceServiceTracker.getService();
		if (service == null)
		{
			return new Settlement[0];
		}
		else
		{
			if (!number.getText().isEmpty())
			{
				return new Settlement[0];
			}
			else
			{
				Calendar[] dateRange = new Calendar[2];
				dateRange[0] = getStartDate(date);
				dateRange[1] = getEndDate(dateRange[0]);

				StructuredSelection salespoints = (StructuredSelection) salespointViewer.getSelection();
				Salespoint salespoint = (Salespoint) salespoints.getFirstElement();
				if (salespoint == null)
				{
					return new Settlement[0];
				}
				else
				{
					Collection<Settlement> settlements = new ArrayList<Settlement>();
					settlements.add(Settlement.newInstance(salespoint));
					SettlementQuery query = (SettlementQuery) service.getServerService().getQuery(Settlement.class);
					settlements
							.addAll(query.selectBySalespointAndDateRange(new Salespoint[] { salespoint }, dateRange));
					return settlements.toArray(new Settlement[0]);
				}
			}
		}
	}

	public Date getDate()
	{
		final Calendar calendar = getStartDate(this.date);
		return calendar.getTime();
	}

	public String getReceiptNumber()
	{
		if (this.number.getText().isEmpty())
		{
			return null;
		}
		else
		{
			return this.number.getText();
		}
	}

	public Salespoint getSalespoint()
	{
		final StructuredSelection ssel = (StructuredSelection) this.salespointViewer.getSelection();
		if (ssel.isEmpty())
		{
			return null;
		}
		else
		{
			if (ssel.getFirstElement() instanceof Salespoint)
			{
				return (Salespoint) ssel.getFirstElement();
			}
			else
			{
				return null;
			}
		}
	}

	public Settlement getSettlement()
	{
		final StructuredSelection ssel = (StructuredSelection) this.settlementViewer.getSelection();
		if (ssel.isEmpty())
		{
			return null;
		}
		else
		{
			if (ssel.getFirstElement() instanceof Settlement)
			{
				return (Settlement) ssel.getFirstElement();
			}
			else
			{
				return null;
			}
		}
	}

	public Receipt.State getState()
	{
		final StructuredSelection ssel = (StructuredSelection) this.stateViewer.getSelection();
		if (ssel.getFirstElement() instanceof Receipt.State)
		{
			return (Receipt.State) ssel.getFirstElement();
		}
		else
		{
			return null;
		}
	}

	public User getUser()
	{
		final StructuredSelection ssel = (StructuredSelection) this.userViewer.getSelection();
		if (ssel.isEmpty())
		{
			return null;
		}
		else
		{
			if (ssel.getFirstElement() instanceof User)
			{
				return (User) ssel.getFirstElement();
			}
			else
			{
				return null;
			}
		}
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);
		this.settings = Activator.getDefault().getDialogSettings().getSection("receipt.filter.view");
		if (this.settings == null)
		{
			this.settings = Activator.getDefault().getDialogSettings().addNewSection("receipt.filter.view");
		}
		try
		{
			this.settings.getLong("date.selection");
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("date.selection", GregorianCalendar.getInstance().getTimeInMillis());
		}
		try
		{
			this.settings.getLong("salespoint.selection");
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("salespoint.selection", 0);
		}
		if (this.settings.get("number.selection") == null)
		{
			this.settings.put("number.selection", GregorianCalendar.getInstance().getTimeInMillis());
		}
		try
		{
			this.settings.getLong("settlement.filter");
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("settlement.filter", 0);
		}
		try
		{
			this.settings.getLong("user.filter");
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("user.filter", 0);
		}
		try
		{
			this.settings.getLong("state.filter");
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("state.filter", 0);
		}
		try
		{
			this.settings.getLong("product.group.filter");
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("product.group.filter", 0);
		}
		try
		{
			this.settings.getLong("payment.type.filter");
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("payment.type.filter", 0);
		}
		try
		{
			this.settings.getLong("amount.filter");
		}
		catch (final NumberFormatException e)
		{
			this.settings.put("amount.filter", 0D);
		}
	}

	private void setDate(Calendar calendar)
	{
		if (!this.date.isDisposed())
		{
			this.date.setYear(calendar.get(Calendar.YEAR));
			this.date.setMonth(calendar.get(Calendar.MONTH));
			this.date.setDay(calendar.get(Calendar.DATE));
			this.date.setHours(calendar.get(Calendar.HOUR_OF_DAY));
			this.date.setMinutes(calendar.get(Calendar.MINUTE));
			this.date.setSeconds(calendar.get(Calendar.SECOND));
		}
	}

	@Override
	public void setFocus()
	{
		this.date.setFocus();
	}

	private void initializeSelection(final PersistenceService persistenceService)
	{
		final long timeInMillis = this.settings.getLong("date.selection");
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(timeInMillis);
		this.setDate(calendar);

		if (persistenceService == null)
		{
			if (salespointViewer.getControl() != null && !this.salespointViewer.getControl().isDisposed())
			{
				this.salespointViewer.setInput(null);
				this.salespointViewer.setSelection(new StructuredSelection(new Salespoint[0]));
				this.settlementViewer.setInput(null);
				this.settlementViewer.setSelection(new StructuredSelection(new Settlement[0]));
				this.userViewer.setInput(null);
				this.userViewer.setSelection(new StructuredSelection(new User[0]));
				this.productGroupViewer.setInput(null);
				this.productGroupViewer.setSelection(new StructuredSelection(new PaymentType[0]));
				this.paymentTypeViewer.setInput(null);
				this.paymentTypeViewer.setSelection(new StructuredSelection(new PaymentType[0]));
			}
		}
		else
		{
			Long id = Long.valueOf(this.settings.getLong("salespoint.selection"));
			final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getServerService().getQuery(
					Salespoint.class);
			final Salespoint[] salespoints = salespointQuery.selectAll(true).toArray(new Salespoint[0]);
			this.salespointViewer.setInput(salespoints);
			if (salespoints.length > 0)
			{
				for (final Salespoint salespoint : salespoints)
				{
					if (salespoint.getId().equals(id))
					{
						this.salespointViewer.setSelection(new StructuredSelection(new Salespoint[] { salespoint }));
						break;
					}
				}
				if (this.salespointViewer.getSelection().isEmpty())
				{
					this.salespointViewer.setSelection(new StructuredSelection(salespoints[0]));
					settings.put("salespoint.selection", salespoints[0].getId());
				}
			}

			this.number.setText(this.settings.get("number.selection"));

			if (!this.salespointViewer.getSelection().isEmpty())
			{
				StructuredSelection ssel = (StructuredSelection) this.salespointViewer.getSelection();
				Salespoint salespoint = (Salespoint) ssel.getFirstElement();
				Collection<Settlement> settlements = new ArrayList<Settlement>();
				Settlement all = Settlement.newInstance(salespoint);
				settlements.add(all);
				SettlementQuery settlementQuery = (SettlementQuery) persistenceService.getServerService().getQuery(
						Settlement.class);
				Calendar startDate = getStartDate(date);
				Calendar endDate = getEndDate(startDate);
				settlements.addAll(settlementQuery.selectBySalespointsAndSettled(new Salespoint[] { salespoint },
						startDate.getTimeInMillis(), endDate.getTimeInMillis()));
				this.settlementViewer.setInput(settlements);
				id = Long.valueOf(settings.getLong("settlement.filter"));
				if (id.longValue() > 0L)
				{
					for (Settlement settlement : settlements)
					{
						if (settlement.getId() != null && settlement.getId().equals(id))
						{
							settlementViewer.setSelection(new StructuredSelection(new Settlement[] { settlement }));
							break;
						}
					}
				}
				if (settlementViewer.getSelection().isEmpty())
				{
					settlementViewer.setSelection(new StructuredSelection(new Settlement[] { all }));
				}
			}

			this.stateViewer.setInput(ReceiptStateSelector.values());
			StructuredSelection ssel = new StructuredSelection(
					new ReceiptStateSelector[] { ReceiptStateSelector.values()[this.settings.getInt("state.filter")] });
			this.stateViewer.setSelection(ssel);

			id = Long.valueOf(this.settings.getLong("user.filter"));
			final UserQuery userQuery = (UserQuery) persistenceService.getServerService().getQuery(User.class);
			Collection<User> allUsers = new ArrayList<User>();
			User all = User.newInstance();
			allUsers.add(all);
			allUsers.addAll(userQuery.selectAll(false));
			User[] users = allUsers.toArray(new User[0]);
			this.userViewer.setInput(users);
			if (users.length > 0)
			{
				for (final User user : users)
				{
					if (user.getId() == null)
					{
						if (id.longValue() == 0L)
						{
							this.userViewer.setSelection(new StructuredSelection(new User[] { user }));
						}
					}
					else
					{
						if (user.getId().equals(id))
						{
							this.userViewer.setSelection(new StructuredSelection(new User[] { user }));
							break;
						}
					}
				}
				if (this.userViewer.getSelection().isEmpty())
				{
					this.userViewer.setSelection(new StructuredSelection(users[0]));
				}
			}

			id = Long.valueOf(this.settings.getLong("product.group.filter"));
			final ProductGroupQuery productGroupQuery = (ProductGroupQuery) persistenceService.getServerService().getQuery(ProductGroup.class);
			Collection<ProductGroup> allProductGroups = new ArrayList<ProductGroup>();
			ProductGroup emptyProductGroup = ProductGroup.newInstance(ProductGroupType.SALES_RELATED, null);
			allProductGroups.add(emptyProductGroup);
			allProductGroups.addAll(productGroupQuery.selectAll(false));
			ProductGroup[] productGroups = allProductGroups.toArray(new ProductGroup[0]);
			this.productGroupViewer.setInput(productGroups);
			if (productGroups.length > 0)
			{
				for (final ProductGroup productGroup : productGroups)
				{
					if (productGroup.getId() == null)
					{
						if (id.longValue() == 0L)
						{
							this.productGroupViewer.setSelection(new StructuredSelection(new ProductGroup[] { productGroup }));
							break;
						}
					}
					else
					{
						if (productGroup.getId().equals(id))
						{
							this.productGroupViewer.setSelection(new StructuredSelection(new ProductGroup[] { productGroup }));
							break;
						}
					}
				}
				if (this.productGroupViewer.getSelection().isEmpty())
				{
					this.productGroupViewer.setSelection(new StructuredSelection(productGroups[0]));
				}
			}
			
			id = Long.valueOf(this.settings.getLong("payment.type.filter"));
			final PaymentTypeQuery paymentTypeQuery = (PaymentTypeQuery) persistenceService.getServerService().getQuery(PaymentType.class);
			Collection<PaymentType> allPaymentTypes = new ArrayList<PaymentType>();
			PaymentType emptyPaymentType = PaymentType.newInstance(PaymentTypeGroup.CASH);
			allPaymentTypes.add(emptyPaymentType);
			allPaymentTypes.addAll(paymentTypeQuery.selectAll(false));
			PaymentType[] paymentTypes = allPaymentTypes.toArray(new PaymentType[0]);
			this.paymentTypeViewer.setInput(paymentTypes);
			if (paymentTypes.length > 0)
			{
				for (final PaymentType paymentType : paymentTypes)
				{
					if (paymentType.getId() == null)
					{
						if (id.longValue() == 0L)
						{
							this.paymentTypeViewer.setSelection(new StructuredSelection(new PaymentType[] { paymentType }));
							break;
						}
					}
					else
					{
						if (paymentType.getId().equals(id))
						{
							this.paymentTypeViewer.setSelection(new StructuredSelection(new PaymentType[] { paymentType }));
							break;
						}
					}
				}
				if (this.paymentTypeViewer.getSelection().isEmpty())
				{
					this.paymentTypeViewer.setSelection(new StructuredSelection(paymentTypes[0]));
				}
			}
			
			double defaultAmount = this.settings.getDouble("amount.filter");
			this.amount.setValue(Double.valueOf(defaultAmount));
		}
	}

	public enum ReceiptStateSelector
	{
		ALL, REVERSED, SAVED;

		public boolean matches(Receipt.State state)
		{
			switch (this)
			{
			case ALL:
			{
				return true;
			}
			case REVERSED:
			{
				return state.equals(Receipt.State.REVERSED);
			}
			case SAVED:
			{
				return state.equals(Receipt.State.SAVED);
			}
			default:
			{
				return false;
			}
			}
		}
		
		public Receipt.State getState()
		{
			switch (this)
			{
				case ALL:
				{
					return null;
				}
				case REVERSED:
				{
					return Receipt.State.REVERSED;
				}
				case SAVED:
				{
					return Receipt.State.SAVED;
				}
				default:
				{
					throw new RuntimeException("Invalid receipt state selector");
				}
			}
		}

		@Override
		public String toString()
		{
			switch (this)
			{
				case ALL:
				{
					return "";
				}
				case REVERSED:
				{
					return Receipt.State.REVERSED.toString();
				}
				case SAVED:
				{
					return Receipt.State.SAVED.toString();
				}
				default:
				{
					throw new RuntimeException("Invalid receipt state selector");
				}
			}
		}
	}

	private void fireSelectionChanged(SelectionChangedEvent event)
	{
		Object[] objects = this.listeners.getListeners();
		for (Object object : objects)
		{
			if (object instanceof ISelectionChangedListener)
			{
				((ISelectionChangedListener) object).selectionChanged(event);
			}
		}
	}

	private void updateSettlementViewer()
	{
		settlementViewer.setInput(selectSettlements());
		settlementViewer.setSelection(new StructuredSelection(new Settlement[] { (Settlement) settlementViewer
				.getElementAt(0) }));
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public ISelection getSelection()
	{
		return selectReceipts();
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection)
	{
	}

	@Override
	public void selectionChanged(SelectionChangedEvent event)
	{
		if (event.getSource().equals(settlementViewer))
		{
			StructuredSelection ssel = (StructuredSelection) event.getSelection();
			if (ssel.getFirstElement() instanceof Settlement)
			{
				final Settlement settlement = (Settlement) ssel.getFirstElement();
				ReceiptFilterView.this.settings.put("settlement.filter", settlement.getId() == null ? 0l : settlement
						.getId().longValue());
				event = new SelectionChangedEvent(this, ssel);
				fireSelectionChanged(event);
			}
		}
		else if (event.getSource().equals(stateViewer))
		{
			StructuredSelection ssel = (StructuredSelection) event.getSelection();
			if (ssel.getFirstElement() instanceof ReceiptStateSelector)
			{
				final ReceiptStateSelector selector = (ReceiptStateSelector) ssel.getFirstElement();
				ReceiptFilterView.this.settings.put("state.filter", selector.ordinal());
				event = new SelectionChangedEvent(this, ssel);
				fireSelectionChanged(event);
			}
		}
		else if (event.getSource().equals(userViewer))
		{
			StructuredSelection ssel = (StructuredSelection) event.getSelection();
			if (ssel.getFirstElement() instanceof User)
			{
				final User user = (User) ssel.getFirstElement();
				ReceiptFilterView.this.settings
						.put("user.filter", user.getId() == null ? 0l : user.getId().longValue());
				event = new SelectionChangedEvent(this, ssel);
				fireSelectionChanged(event);
			}
		}
		else if (event.getSource().equals(productGroupViewer))
		{
			StructuredSelection ssel = (StructuredSelection) event.getSelection();
			if (ssel.getFirstElement() instanceof ProductGroup)
			{
				final ProductGroup productGroup = (ProductGroup) ssel.getFirstElement();
				ReceiptFilterView.this.settings
						.put("product.group.filter", productGroup.getId() == null ? 0l : productGroup.getId().longValue());
				event = new SelectionChangedEvent(this, ssel);
				fireSelectionChanged(event);
			}
		}
		else if (event.getSource().equals(paymentTypeViewer))
		{
			StructuredSelection ssel = (StructuredSelection) event.getSelection();
			if (ssel.getFirstElement() instanceof PaymentType)
			{
				final PaymentType paymentType = (PaymentType) ssel.getFirstElement();
				ReceiptFilterView.this.settings
						.put("payment.type.filter", paymentType.getId() == null ? 0l : paymentType.getId().longValue());
				event = new SelectionChangedEvent(this, ssel);
				fireSelectionChanged(event);
			}
		}
		else if (event.getSource().equals(salespointViewer))
		{
			if (this.number.getText().isEmpty())
			{
				StructuredSelection ssel = (StructuredSelection) event.getSelection();
				if (ssel.getFirstElement() instanceof PaymentType)
				{
					final PaymentType paymentType = (PaymentType) ssel.getFirstElement();
					Long id = paymentType.getId();
					ReceiptFilterView.this.settings.put("paymentType.selection", id == null ? Long.valueOf(0L) : id);
					ssel = selectReceipts();
					event = new SelectionChangedEvent(this, ssel);
					fireSelectionChanged(event);
				}
			}
		}
		else if (event.getSelection() instanceof IStructuredSelection)
		{
			StructuredSelection ssel = (StructuredSelection) event.getSelection();
			if (ssel.getFirstElement() instanceof Double)
			{
				final Double amount = (Double) ssel.getFirstElement();
				ReceiptFilterView.this.settings
						.put("amount.filter", amount == null ? 0D : amount.doubleValue());
				event = new SelectionChangedEvent(this, ssel);
				fireSelectionChanged(event);
			}
		}
	}

	private class DestinationObject
	{
		public ReportService.Destination destination = ReportService.Destination.PREVIEW;
		public ReportService.Format format = ReportService.Format.PDF;
	}
}
