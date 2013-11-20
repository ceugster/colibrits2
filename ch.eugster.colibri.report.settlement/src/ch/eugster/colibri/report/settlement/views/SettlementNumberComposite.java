/*
 * Created on 17.06.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.report.settlement.views;

import java.awt.Toolkit;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.PropertyResourceBundle;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.data.JRMapArrayDataSource;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.progress.UIJob;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.queries.SettlementQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.report.settlement.Activator;
import ch.eugster.colibri.report.settlement.model.SettlementEntry;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class SettlementNumberComposite extends AbstractSettlementCompositeChild implements ISettlementCompositeChild,
		EventHandler
{
	private Button printOnReceiptPrinter;

	private ComboViewer settlementViewer;

	/**
	 * @param parent
	 * @param style
	 */
	public SettlementNumberComposite(Composite parent, SettlementView parentView, int style)
	{
		super(parent, parentView, style);
	}

	@Override
	protected void init()
	{
		this.setLayout(new GridLayout(2, false));

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		Group group = new Group(this, SWT.SHADOW_ETCHED_IN);
		group.setLayout(new GridLayout());
		group.setLayoutData(gridData);
		group.setText("Auswahl Abschlussnummer"); //$NON-NLS-1$

		Combo combo = new Combo(group, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		settlementViewer = new ComboViewer(combo);
		settlementViewer.setContentProvider(new SettlementContentProvider());
		settlementViewer.setLabelProvider(new SettlementLabelProvider());
		settlementViewer.setFilters(new ViewerFilter[] { new SettlementNotSettledFilter(),
				new DeletedEntityViewerFilter() });
		settlementViewer.addSelectionChangedListener(new ISelectionChangedListener()
		{
			@Override
			public void selectionChanged(SelectionChangedEvent event)
			{
				if (printOnReceiptPrinter != null)
				{
					StructuredSelection ssel = (StructuredSelection) event.getSelection();
					printOnReceiptPrinter.setEnabled(!ssel.isEmpty());
				}
			}
		});

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		Label label = new Label(this, SWT.WRAP);
		label.setLayoutData(gridData);
		label.setText("Wählen Sie den gewünschten Abschluss aus der Liste aus.");

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalSpan = 2;

		Composite filler = new Composite(this, SWT.NONE);
		filler.setLayoutData(gridData);

		label = new Label(this, SWT.WRAP);
		label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		printOnReceiptPrinter = new Button(this, SWT.PUSH);
		printOnReceiptPrinter.setImage(Activator.getDefault().getImageRegistry().get("print"));
		printOnReceiptPrinter.setToolTipText("Auf Belegdrucker drucken");
		printOnReceiptPrinter.setLayoutData(new GridData());
		printOnReceiptPrinter.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				StructuredSelection ssel = (StructuredSelection) settlementViewer.getSelection();
				final Settlement settlement = (Settlement) ssel.getFirstElement();
				if (settlement.getId() != null)
				{
					try
					{
						IRunnableWithProgress op = new IRunnableWithProgress()
						{
							@Override
							public void run(IProgressMonitor monitor) throws InvocationTargetException,
									InterruptedException
							{
								final ServiceTracker<EventAdmin, EventAdmin> tracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle()
										.getBundleContext(), EventAdmin.class, null);
								try
								{
									monitor.beginTask("Der Abschluss wird auf dem Belegdrucker ausgedruckt...",
											IProgressMonitor.UNKNOWN);
									tracker.open();
									final EventAdmin eventAdmin = (EventAdmin) tracker.getService();
									if (eventAdmin != null)
									{
										eventAdmin.sendEvent(getEvent(tracker,
												Topic.PRINT_SETTLEMENT.topic(), settlement));
									}
								}
								finally
								{
									tracker.close();
									monitor.done();
								}
							}
						};
						ProgressMonitorDialog dialog = new ProgressMonitorDialog(SettlementNumberComposite.this
								.getShell());
						dialog.run(true, true, op);
					}
					catch (InvocationTargetException ex)
					{
					}
					catch (InterruptedException ex)
					{
					}
				}
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put("event.topics", Topic.PRINT_ERROR.topic());
		Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class.getName(), this, properties);
	}

	@Override
	public void dispose()
	{
		super.dispose();
	}

	private Event getEvent(final ServiceTracker<EventAdmin, EventAdmin> tracker, final String topics, final Settlement settlement)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
		properties.put(EventConstants.SERVICE, tracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID, tracker.getServiceReference().getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put(IPrintable.class.getName(), settlement);
		properties.put("force", true);
		properties.put("status", Status.OK_STATUS);
		return new Event(topics, properties);
	}

	public Settlement getSelectedSettlement()
	{
		if (this.settlementViewer.getSelection().isEmpty())
		{
			return null;
		}
		else
		{
			StructuredSelection ssel = (StructuredSelection) settlementViewer.getSelection();
			return (Settlement) ssel.getFirstElement();
		}
	}

	@Override
	public JRDataSource createDataSource()
	{
		Settlement settlement = this.getSelectedSettlement();
		if (settlement == null)
		{
			return null;
		}
		Collection<SettlementEntry> entries = new ArrayList<SettlementEntry>();
		entries.addAll(createPositionSection(new HashMap<Long, SettlementEntry>(), settlement.getPositions()).values());
		entries.addAll(createPaymentSection(new HashMap<Long, SettlementEntry>(), settlement.getPayments(),
				settlement.getSalespoint().getCommonSettings().getReferenceCurrency()).values());
		entries.addAll(createTaxSection(new HashMap<Long, SettlementEntry>(), settlement.getTaxes()).values());
		entries.addAll(createRestitutedPositionSection(new HashMap<Long, SettlementEntry>(),
				settlement.getRestitutedPositions()).values());
		entries.addAll(createPayedInvoiceSection(new HashMap<Long, SettlementEntry>(), settlement.getPayedInvoices())
				.values());
		entries.addAll(createInternalSection(new HashMap<Long, SettlementEntry>(), settlement.getInternals()).values());
		entries.addAll(createReceiptSection(new HashMap<Long, SettlementEntry>(), settlement.getReversedReceipts())
				.values());
		entries.addAll(createDetailSection(new HashMap<Long, SettlementEntry>(), settlement.getDetails()).values());
		entries.addAll(createMoneySection(new HashMap<Long, SettlementEntry>(), settlement.getMoneys()).values());

		SettlementEntry[] allEntries = entries.toArray(new SettlementEntry[0]);
		Arrays.sort(allEntries);
		for (SettlementEntry entry : allEntries)
		{
			System.out.println(entry.get("section") + ", " + entry.get("group") + ", " + entry.get("code") + ", "
					+ entry.get("text") + ", " + entry.get("quantity") + ", " + entry.get("amount1") + ", "
					+ entry.get("amount2"));
		}

		return allEntries.length == 0 ? null : new JRMapArrayDataSource(allEntries);
	}

	public boolean validSelection()
	{
		return !settlementViewer.getSelection().isEmpty();
	}

	@Override
	public void setInput()
	{
		if (settlementViewer.getContentProvider() != null)
		{
			if (getSelectedSalespoints() == null || getSelectedSalespoints().length == 0)
			{
				settlementViewer.setInput(new Object[0]);
				settlementViewer.setSelection(new StructuredSelection());
				return;
			}
			if (getSelectedDateRange() == null || getSelectedDateRange().length != 2)
			{
				settlementViewer.setInput(new Object[0]);
				settlementViewer.setSelection(new StructuredSelection());
				return;
			}
			ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
					PersistenceService.class, null);
			tracker.open();
			PersistenceService service = (PersistenceService) tracker.getService();
			if (service == null)
			{
				settlementViewer.setInput(new Object[0]);
				settlementViewer.setSelection(new StructuredSelection());
			}
			else
			{
				SettlementQuery query = (SettlementQuery) service.getServerService().getQuery(Settlement.class);
				Collection<Settlement> s = query.selectBySalespointsAndSettled(getSelectedSalespoints(),
						getSelectedDateRange()[0].getTimeInMillis(), getSelectedDateRange()[1].getTimeInMillis());
				Settlement[] settlements = s.toArray(new Settlement[0]);
				settlementViewer.setInput(settlements);
				if (settlements.length > 0)
				{
					settlementViewer.setSelection(new StructuredSelection(new Settlement[] { settlements[0] }));
				}
				else
				{
					settlementViewer.setSelection(new StructuredSelection());
				}
			}
			tracker.close();
		}
	}

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.settlementViewer.addSelectionChangedListener(listener);
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener)
	{
		this.settlementViewer.removeSelectionChangedListener(listener);
	}

	@Override
	public boolean validateSelection()
	{
		boolean valid = !this.settlementViewer.getSelection().isEmpty();
		return valid;
	}

	@Override
	public String getReportName()
	{
		return "SingleSettlementReport";
	}

	@Override
	public InputStream getReport() throws IOException
	{
		URL url = Activator.getDefault().getBundle().getEntry("reports/" + getReportName() + ".jrxml");
		return url.openStream();
	}

	@Override
	public Hashtable<String, Object> getParameters()
	{
		String address = this.getSelectedSettlement().getSalespoint().getCommonSettings().getAddress();
		String taxNumber = this.getSelectedSettlement().getSalespoint().getCommonSettings().getTaxNumber();
		String taxInclusive = this.getSelectedSettlement().getSalespoint().getCommonSettings().isTaxInclusive() ? "Betrag inkl. Mwst"
				: "Betrag exkl. Mwst";

		Hashtable<String, Object> parameters = new Hashtable<String, Object>();
		parameters.put("header", address);
		parameters.put("taxNumber", taxNumber);
		parameters.put("taxInclusive", taxInclusive);

		NumberFormat formatter = DecimalFormat.getInstance();
		parameters.put("receiptCount", formatter.format(this.getSelectedSettlement().getReceiptCount()));
		parameters.put("printTime",
				SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()));
		parameters.put("salespoints", this.getSelectedSettlement().getSalespoint().getName());
		Calendar calendar = GregorianCalendar.getInstance();
		calendar = this.getSelectedSettlement().getSettled();
		parameters.put("settlementDate", SimpleDateFormat.getDateTimeInstance().format(calendar.getTime()));
		formatter.setGroupingUsed(false);
		parameters.put("settlementNumber", formatter.format(calendar.getTimeInMillis()));
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

	@Override
	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(Topic.PRINT_ERROR.topic()))
		{
			UIJob job = new UIJob("Message")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					IStatus status = (IStatus) event.getProperty("status");
					Toolkit.getDefaultToolkit().beep();
					MessageDialog dialog = new MessageDialog(getShell(), "Belegdrucker", null, status.getMessage(),
							MessageDialog.WARNING, new String[] { "OK" }, 0);
//					dialog.setBlockOnOpen(true);
					dialog.open();
					return status;
				}
			};
			job.schedule();
		}
	}

	@Override
	public ISelection getSelection() 
	{
		return this.settlementViewer.getSelection();
	}

	@Override
	public void setSelection(ISelection selection) 
	{
		this.settlementViewer.setSelection(selection);
	}
}
