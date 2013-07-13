/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.report.settlement.views;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.PropertyResourceBundle;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SettlementInternal;
import ch.eugster.colibri.persistence.model.SettlementPayedInvoice;
import ch.eugster.colibri.persistence.model.SettlementPayment;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.SettlementReceipt;
import ch.eugster.colibri.persistence.model.SettlementRestitutedPosition;
import ch.eugster.colibri.persistence.model.SettlementTax;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.ServerService;
import ch.eugster.colibri.report.settlement.Activator;
import ch.eugster.colibri.report.settlement.model.SettlementEntry;

public class SettlementDateRangeComposite extends AbstractSettlementCompositeChild implements ISettlementCompositeChild
{
	private static final String reportName = "SettlementStatistics";

	private final ListenerList listeners = new ListenerList();

	private Button printReversedReceipts;

	private Button printInternals;

	private Button printRestitutedPositions;

	private Button printPayedInvoices;

	private IDialogSettings settings;

	/**
	 * @param parent
	 * @param style
	 */
	public SettlementDateRangeComposite(Composite parent, int style)
	{
		super(parent, style);
	}

	@Override
	protected void setInput()
	{
		// do nothing
	}

	@Override
	protected void init()
	{
		settings = Activator.getDefault().getDialogSettings().getSection(SettlementView.class.getName());
		if (settings == null)
		{
			settings = Activator.getDefault().getDialogSettings().addNewSection(SettlementView.class.getName());
		}

		this.setLayout(new GridLayout());

		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		group.setText("Optionen"); //$NON-NLS-1$

		printInternals = new Button(group, SWT.CHECK);
		printInternals.setText("Einlagen/Entnahmen auflisten");
		printInternals.setLayoutData(new GridData());
		printInternals.setSelection(settings.getBoolean("print.detailed.internals"));
		printInternals.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				settings.put("print.detailed.internals", ((Button) e.widget).getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		printInternals.setSelection(settings.getBoolean("print.detailed.internals"));

		printRestitutedPositions = new Button(group, SWT.CHECK);
		printRestitutedPositions.setText("Rücknahmen auflisten");
		printRestitutedPositions.setLayoutData(new GridData());
		printRestitutedPositions.setSelection(settings.getBoolean("print.restituted.positions"));
		printRestitutedPositions.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				settings.put("print.restituted.positions", ((Button) e.widget).getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		printRestitutedPositions.setSelection(settings.getBoolean("print.restituted.positions"));

		printPayedInvoices = new Button(group, SWT.CHECK);
		printPayedInvoices.setText("Bezahlte Rechnungen auflisten");
		printPayedInvoices.setLayoutData(new GridData());
		printPayedInvoices.setSelection(settings.getBoolean("print.payed.invoices"));
		printPayedInvoices.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				settings.put("print.payed.invoices", ((Button) e.widget).getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		printPayedInvoices.setSelection(settings.getBoolean("print.payed.invoices"));

		printReversedReceipts = new Button(group, SWT.CHECK);
		printReversedReceipts.setText("Stornierte Belege auflisten");
		printReversedReceipts.setLayoutData(new GridData());
		printReversedReceipts.setSelection(settings.getBoolean("print.reversed.receipts"));
		printReversedReceipts.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				settings.put("print.reversed.receipts", ((Button) e.widget).getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		printReversedReceipts.setSelection(settings.getBoolean("print.reversed.receipts"));

	}

	@Override
	public JRDataSource createDataSource()
	{
		JRDataSource dataSource = null;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		PersistenceService service = (PersistenceService) tracker.getService();
		if (service != null)
		{
			Collection<SettlementEntry> entries = new ArrayList<SettlementEntry>();

			entries.addAll(createPositionSection(service.getServerService()).values());
			entries.addAll(createPaymentSection(service.getServerService()).values());
			entries.addAll(createTaxSection(service.getServerService()).values());

			if (this.printInternals.getSelection())
			{
				entries.addAll(createInternalSection(service.getServerService()).values());
			}
			if (this.printRestitutedPositions.getSelection())
			{
				entries.addAll(createRestitutedPositionSection(service.getServerService()).values());
			}
			if (this.printPayedInvoices.getSelection())
			{
				entries.addAll(createPayedInvoiceSection(service.getServerService()).values());
			}
			if (this.printReversedReceipts.getSelection())
			{
				entries.addAll(createReversedReceiptsSection(service.getServerService()).values());
			}

			SettlementEntry[] allEntries = entries.toArray(new SettlementEntry[0]);
			Arrays.sort(allEntries);

			for (SettlementEntry entry : allEntries)
			{
				System.out.println(entry.get("section") + ", " + entry.get("group") + ", " + entry.get("code") + ", "
						+ entry.get("text") + ", " + entry.get("quantity") + ", " + entry.get("amount1") + ", "
						+ entry.get("amount2"));
			}
			if (allEntries.length > 0)
			{
				dataSource = new JRMapArrayDataSource(allEntries);
			}
		}
		tracker.close();

		return dataSource;
	}

	protected Map<Long, SettlementEntry> createPositionSection(ServerService service)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		Collection<SettlementPosition> positions = query.selectPositions(this.selectedSalespoints,
				this.selectedDateRange);
		return createPositionSection(new HashMap<Long, SettlementEntry>(), positions);
	}

	protected Map<Long, SettlementEntry> createPaymentSection(ServerService service)
	{
		final PaymentQuery query = (PaymentQuery) service.getQuery(Payment.class);
		Collection<SettlementPayment> payments = query.selectPayments(this.selectedSalespoints, this.selectedDateRange);
		return createPaymentSection(new HashMap<Long, SettlementEntry>(), payments, this.selectedSalespoints[0]
				.getCommonSettings().getReferenceCurrency());
	}

	protected Map<Long, SettlementEntry> createTaxSection(ServerService service)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		Collection<SettlementTax> taxes = query.selectTaxes(this.selectedSalespoints, this.selectedDateRange);
		return createTaxSection(new HashMap<Long, SettlementEntry>(), taxes);
	}

	protected Map<Long, SettlementEntry> createInternalSection(ServerService service)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		Collection<SettlementInternal> internals = query.selectInternals(this.selectedSalespoints,
				this.selectedDateRange);
		return createInternalSection(new HashMap<Long, SettlementEntry>(), internals);
	}

	protected Map<Long, SettlementEntry> createRestitutedPositionSection(ServerService service)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		Collection<SettlementRestitutedPosition> internals = query.selectRestitutedPositions(this.selectedSalespoints,
				this.selectedDateRange);
		return createRestitutedPositionSection(new HashMap<Long, SettlementEntry>(), internals);
	}

	protected Map<Long, SettlementEntry> createPayedInvoiceSection(ServerService service)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		Collection<SettlementPayedInvoice> payedInvoices = query.selectPayedInvoices(this.selectedSalespoints,
				this.selectedDateRange);
		return createPayedInvoiceSection(new HashMap<Long, SettlementEntry>(), payedInvoices);
	}

	protected Map<Long, SettlementEntry> createReversedReceiptsSection(ServerService service)
	{
		final ReceiptQuery query = (ReceiptQuery) service.getQuery(Receipt.class);
		Collection<SettlementReceipt> receipts = query.selectReversed(this.selectedSalespoints, this.selectedDateRange);
		return createReceiptSection(new HashMap<Long, SettlementEntry>(), receipts);
	}

	public boolean isWithReversedReceipts()
	{
		return printReversedReceipts.getSelection();
	}

	public boolean isWithDetailedInternals()
	{
		return printInternals.getSelection();
	}

	public boolean isWithRestitutedPositions()
	{
		return printRestitutedPositions.getSelection();
	}

	public boolean isWithPayedInvoices()
	{
		return printPayedInvoices.getSelection();
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.listeners.add(listener);
	}

	@Override
	public ISelection getSelection()
	{
		if (selectedSalespoints == null)
		{
			if (selectedDateRange == null || selectedDateRange.length != 2)
			{
				return new StructuredSelection();
			}
			return new StructuredSelection(selectedDateRange);
		}

		Object[] objects = new Object[selectedSalespoints.length + selectedDateRange.length];
		for (int i = 0; i > selectedSalespoints.length; i++)
		{
			objects[i] = selectedSalespoints[i];
		}
		for (int i = 0; i < selectedDateRange.length; i++)
		{
			objects[selectedSalespoints.length + i] = selectedDateRange[i];
		}
		return new StructuredSelection(objects);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection)
	{
		if (selection instanceof StructuredSelection)
		{
			Collection<Salespoint> salespoints = new ArrayList<Salespoint>();
			Collection<Calendar> dates = new ArrayList<Calendar>();
			StructuredSelection ssel = (StructuredSelection) selection;
			Object[] objects = ssel.toArray();
			for (Object object : objects)
			{
				if (object instanceof Salespoint)
				{
					salespoints.add((Salespoint) object);
				}
				else if (object instanceof Calendar)
				{
					dates.add((Calendar) object);
				}
			}
			selectedSalespoints = salespoints.toArray(new Salespoint[0]);
			selectedDateRange = dates.size() == 2 ? dates.toArray(new Calendar[0]) : null;
		}
	}

	@Override
	public boolean validateSelection()
	{
		return selectedSalespoints != null && selectedSalespoints.length > 0 && selectedDateRange != null
				&& selectedDateRange.length == 2;
	}

	@Override
	public String getReportName()
	{
		return reportName;
	}

	@Override
	public InputStream getReport() throws IOException
	{
		URL url = Activator.getDefault().getBundle().getEntry("reports/" + reportName + ".jrxml");
		return url.openStream();
	}

	private long countReceipts()
	{
		long count = 0L;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		PersistenceService service = (PersistenceService) tracker.getService();
		if (service != null)
		{
			ReceiptQuery query = (ReceiptQuery) service.getServerService().getQuery(Receipt.class);
			count = query.countBySalespointsAndDateRange(this.selectedSalespoints, this.selectedDateRange);
		}
		tracker.close();
		return count;
	}

	@Override
	public Hashtable<String, Object> getParameters()
	{
		NumberFormat nf = DecimalFormat.getIntegerInstance();
		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		final String header = "Header";
		parameters.put("header", header);
		parameters.put("printTime",
				SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()));
		parameters.put("salespoints", getSalespointList());
		parameters.put("dateRange", getDateRangeList());
		parameters.put("receiptCount", nf.format(countReceipts()));
		parameters.put("taxInclusive", this.selectedSalespoints[0].getCommonSettings().isTaxInclusive() ? "inkl. MwSt."
				: "exkl. Mwst.");
		URL entry = Activator.getDefault().getBundle().getEntry("/reports/" + getReportName() + ".properties");
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

	private String getSalespointList()
	{
		StringBuilder salespoints = new StringBuilder();
		if (selectedSalespoints != null && selectedSalespoints.length > 0)
		{
			for (Salespoint selectedSalespoint : selectedSalespoints)
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
		if (selectedDateRange != null && selectedDateRange.length == 2)
		{
			if (selectedDateRange[0].get(Calendar.YEAR) == selectedDateRange[1].get(Calendar.YEAR))
			{
				if (selectedDateRange[0].get(Calendar.MONTH) == selectedDateRange[1].get(Calendar.MONTH))
				{
					if (selectedDateRange[0].get(Calendar.DATE) == selectedDateRange[1].get(Calendar.DATE))
					{
						return SimpleDateFormat.getDateInstance().format(selectedDateRange[0].getTime());
					}
				}
			}

			StringBuilder builder = new StringBuilder();
			builder = builder.append(SimpleDateFormat.getDateInstance().format(selectedDateRange[0].getTime()));
			builder = builder.append(" bis ");
			builder = builder.append(SimpleDateFormat.getDateInstance().format(selectedDateRange[1].getTime()));
			return builder.toString();
		}
		return "";
	}

}
