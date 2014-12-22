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
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementDetail;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.queries.SettlementQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.report.settlement.Activator;
import ch.eugster.colibri.report.settlement.model.DifferenceEntry;

public class SettlementDifferenceComposite extends AbstractSettlementCompositeChild implements ISettlementCompositeChild
{
	private static final String reportName = "SettlementDiffs";

	/**
	 * @param parent
	 * @param style
	 */
	public SettlementDifferenceComposite(Composite parent, SettlementView parentView, int style)
	{
		super(parent, parentView, style);
	}

	@Override
	public void setInput()
	{
		// do nothing
	}

	@Override
	protected void init()
	{
		this.setLayout(new GridLayout());
		Label label = new Label(this, SWT.WRAP);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		label.setText("Die Differenzliste weist Differenzen der Abschlüsse über die gewählte Periode aus. Diese Auswertung eignet sich nur für Perioden, für die gespeicherte Abschlüsse vorhanden sind.");
	}

	@Override
	public JRDataSource createDataSource()
	{
		JRDataSource dataSource = null;
		
		Salespoint[] salespoints = parentView.getSelectedSalespoints();
		if (salespoints.length == 0)
		{
			MessageDialog.openConfirm(this.getShell(), "Keine Kasse ausgewählt", "Sie haben keine Kasse ausgewählt.");
			return null;
		}
		Calendar[] dateRange = parentView.getSelectedDateRange();
		if (dateRange.length != 2)
		{
			MessageDialog.openConfirm(this.getShell(), "Ungültiger Datumsbereich", "Der Datumsbereich ist ungültig.");
			return null;
		}
		
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();
		PersistenceService service = (PersistenceService) tracker.getService();
		if (service != null)
		{
			List<DifferenceEntry> entries = new ArrayList<DifferenceEntry>();
			SettlementQuery query = (SettlementQuery) service.getServerService().getQuery(Settlement.class);
			List<Settlement> settlements = query.selectBySalespointAndDateRange(salespoints, dateRange);
			for (Settlement settlement : settlements)
			{
				List<SettlementDetail> details = settlement.getDetails();
				entries.addAll(createDetailSection(details));
			}

			DifferenceEntry[] allEntries = entries.toArray(new DifferenceEntry[0]);
			Arrays.sort(allEntries);

			if (allEntries.length > 0)
			{
				dataSource = new JRMapArrayDataSource(allEntries);
			}
		}
		tracker.close();

		return dataSource;
	}

	protected List<DifferenceEntry> createDetailSection(List<SettlementDetail> details)
	{
		List<DifferenceEntry> entries = new ArrayList<DifferenceEntry>();
		for (SettlementDetail detail : details)
		{
			double difference = detail.getCredit() - detail.getDebit();
			if (detail.getPart().equals(SettlementDetail.Part.DIFFERENCE) && difference != 0D)
			{
				DifferenceEntry entry = new DifferenceEntry();
				entry.setSalespoint(detail.getStock().getSalespoint().getName());
				entry.setCode(detail.getPaymentType().getCurrency().getCode());
				entry.setSettlement(SimpleDateFormat.getDateTimeInstance().format(detail.getSettlement().getSettled().getTime()) + (difference > 0 ? " Zuviel in Kasse" : " Zuwenig in Kasse"));
				entry.setAmount(Double.valueOf(difference));
				entry.setDate(detail.getSettlement().getSettled().getTime());
				entries.add(entry);
			}
		}
		return entries;
	}

	@Override
	public boolean validateSelection()
	{
		return true;
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
			count = query.countBySalespointsAndDateRange(this.parentView.getSelectedSalespoints(), this.parentView.getSelectedDateRange());
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
				SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance(Locale.getDefault()).getTime()));
		parameters.put("salespoints", getSalespointList());
		parameters.put("dateRange", getDateRangeList());
		parameters.put("receiptCount", nf.format(countReceipts()));
		parameters.put("taxInclusive", this.parentView.getSelectedSalespoints()[0].getCommonSettings().isTaxInclusive() ? "inkl. MwSt."
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
		if (parentView.getSelectedSalespoints() != null && parentView.getSelectedSalespoints().length > 0)
		{
			for (Salespoint selectedSalespoint : this.parentView.getSelectedSalespoints())
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
		if (parentView.getSelectedDateRange() != null && parentView.getSelectedDateRange().length == 2)
		{
			if (parentView.getSelectedDateRange()[0].get(Calendar.YEAR) == this.parentView.getSelectedDateRange()[1].get(Calendar.YEAR))
			{
				if (parentView.getSelectedDateRange()[0].get(Calendar.MONTH) == parentView.getSelectedDateRange()[1].get(Calendar.MONTH))
				{
					if (parentView.getSelectedDateRange()[0].get(Calendar.DATE) == parentView.getSelectedDateRange()[1].get(Calendar.DATE))
					{
						return SimpleDateFormat.getDateInstance().format(parentView.getSelectedDateRange()[0].getTime());
					}
				}
			}

			StringBuilder builder = new StringBuilder();
			builder = builder.append(SimpleDateFormat.getDateInstance().format(parentView.getSelectedDateRange()[0].getTime()));
			builder = builder.append(" bis ");
			builder = builder.append(SimpleDateFormat.getDateInstance().format(parentView.getSelectedDateRange()[1].getTime()));
			return builder.toString();
		}
		return "";
	}

	@Override
	public ISelection getSelection() 
	{
		return new StructuredSelection();
	}

	@Override
	public void setSelection(ISelection selection) 
	{
	}
}
