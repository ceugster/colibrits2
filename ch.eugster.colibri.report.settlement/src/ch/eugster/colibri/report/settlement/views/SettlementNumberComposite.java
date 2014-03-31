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
import java.util.List;
import java.util.Map;
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
import org.eclipse.jface.viewers.IStructuredSelection;
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
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementInternal;
import ch.eugster.colibri.persistence.model.SettlementPayedInvoice;
import ch.eugster.colibri.persistence.model.SettlementPayment;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.SettlementReceipt;
import ch.eugster.colibri.persistence.model.SettlementTax;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
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

	private Button updateSettlement;
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

//		updateSettlement = new Button(this, SWT.PUSH);
//		updateSettlement.setLayoutData(new GridData());
//		updateSettlement.setText("Aktualisieren");
//		updateSettlement.addSelectionListener(new SelectionListener() 
//		{
//			@Override
//			public void widgetSelected(SelectionEvent e) 
//			{
//				updateSettlement(settlementViewer.getSelection());
//			}
//
//			@Override
//			public void widgetDefaultSelected(SelectionEvent e) 
//			{
//				widgetSelected(e);
//			}
//		});
		
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
		entries.addAll(createInternalSection(new HashMap<Long, SettlementEntry>(), settlement.getInternals(), false).values());
		entries.addAll(createReceiptSection(new HashMap<Long, SettlementEntry>(), settlement.getReversedReceipts())
				.values());
		entries.addAll(createDetailSection(new HashMap<Long, SettlementEntry>(), settlement.getDetails()).values());
		entries.addAll(createMoneySection(new HashMap<Long, SettlementEntry>(), settlement.getMoneys()).values());

		SettlementEntry[] allEntries = entries.toArray(new SettlementEntry[0]);
		Arrays.sort(allEntries);

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
			if (parentView.getSelectedSalespoints() == null || parentView.getSelectedSalespoints().length == 0)
			{
				settlementViewer.setInput(new Object[0]);
				settlementViewer.setSelection(new StructuredSelection());
				return;
			}
			if (parentView.getSelectedDateRange() == null || parentView.getSelectedDateRange().length != 2)
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
				Collection<Settlement> s = query.selectBySalespointsAndSettled(parentView.getSelectedSalespoints(),
						parentView.getSelectedDateRange()[0].getTimeInMillis(), parentView.getSelectedDateRange()[1].getTimeInMillis());
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

	private void updateSettlement(ISelection selection) 
	{
		if (selection.isEmpty())
		{
			return;
		}
		if (selection instanceof IStructuredSelection)
		{
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.getFirstElement() instanceof Settlement)
			{
				Settlement settlement = (Settlement) ssel.getFirstElement();
				settlement.setReceiptCount(0L);
				MessageDialog dialog = new MessageDialog(this.getShell(), "Abschluss aktualisieren", null, "Achtung, Sie sind dabei, den Tagesabschluss vom " + SimpleDateFormat.getInstance().getDateTimeInstance().format(settlement.getSettled().getTime()) + " zu verändern. Diese Änderung kann nicht mehr rückganging gemacht werden. Wollen Sie die Aktualisierung trotzdem durchführen?", MessageDialog.WARNING, new String[] { "Ja", "Nein" }, 1);
				if (dialog.open() == 0)
				{
					ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
							PersistenceService.class, null);
					try
					{
						tracker.open();
						PersistenceService service = (PersistenceService) tracker.getService();
						if (service != null)
						{
							ReceiptQuery query = (ReceiptQuery) service.getServerService().getQuery(Receipt.class);
							List<Receipt> receipts = query.selectBySettlement(settlement, new Receipt.State[] {Receipt.State.SAVED, Receipt.State.REVERSED });
							Map<Long, SettlementPosition> settlementPositions = loadSettlementPositions(settlement);
							Map<Long, SettlementPayedInvoice> settlementPayedInvoices = loadSettlementPayedInvoices(settlement);
							Map<Long, SettlementReceipt> settlementReceipts = loadSettlementReceipts(settlement);
							Map<Long, SettlementInternal> settlementInternals = loadSettlementInternals(settlement);
//							Map<Long, SettlementTax> settlementTax = loadSettlementTaxes(settlement);
							Map<Long, SettlementPaymentValue> settlementPayments = loadSettlementPayments(settlement);
							for (Receipt receipt : receipts)
							{
								if (receipt.getState().equals(Receipt.State.REVERSED))
								{
									SettlementReceipt settlementReceipt = settlementReceipts.remove(receipt.getId());
									if (settlementReceipt == null)
									{
										settlementReceipt = SettlementReceipt.newInstance(settlement, receipt);
										settlement.getReversedReceipts().add(settlementReceipt);
									}
									else
									{
										settlementReceipt.setReceipt(receipt);
									}
									settlementReceipt.setDeleted(receipt.isDeleted());
								}
								long receiptCount = settlement.getReceiptCount();
								settlement.setReceiptCount(receiptCount + 1);
								List<Position> positions = receipt.getPositions();
								for (Position position : positions)
								{
									if (!position.isDeleted())
									{
										if (position.getOption().equals(Option.PAYED_INVOICE))
										{
											SettlementPayedInvoice settlementPayedInvoice = settlementPayedInvoices.remove(position.getId());
											if (settlementPayedInvoice == null)
											{
												settlementPayedInvoice = SettlementPayedInvoice.newInstance(settlement, position);
											}
											else
											{
												settlementPayedInvoice.setPosition(position);
												
											}
										}
										if (position.getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL))
										{
											SettlementInternal settlementInternal = settlementInternals.remove(position.getId());
											if (settlementInternal == null)
											{
												settlementInternal = SettlementInternal.newInstance(settlement, position);
											}
											else
											{
												settlementInternal.setPosition(position);
												
											}
										}
										if (!receipt.isInternal())
										{
											SettlementPosition settlementPosition = settlementPositions.get(position.getProductGroup().getId());
											if (settlementPosition == null)
											{
												settlementPosition = SettlementPosition.newInstance(settlement, position.getProductGroup(), position.getReceipt().getSettlement().getSalespoint().getPaymentType().getCurrency());
												settlementPosition.setProductGroupType(position.getProductGroup().getProductGroupType());
												settlement.getPositions().add(settlementPosition);
											}
											settlementPosition.setDeleted(false);
											int qty = settlementPosition.getQuantity();
											settlementPosition.setQuantity(qty + position.getQuantity());
											double amount = settlementPosition.getDefaultCurrencyAmount();
											settlementPosition.setDefaultCurrencyAmount(amount + position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
											amount = settlementPosition.getTaxAmount();
											settlementPosition.setTaxAmount(amount + position.getTaxAmount(Receipt.QuotationType.DEFAULT_CURRENCY));
										}
									}
								}
								List<Payment> payments = receipt.getPayments();
								for (Payment payment : payments)
								{
									if (!payment.isDeleted() && !receipt.isInternal())
									{
										SettlementPaymentValue paymentValue = settlementPayments.get(payment.getPaymentType().getId());
										if (paymentValue == null)
										{
											paymentValue = new SettlementPaymentValue(createSettlementPayment(payment, settlement));
											settlementPayments.put(payment.getPaymentType().getId(), paymentValue);
										}
										else
										{
											SettlementPayment settlementPayment = paymentValue.getRight(payment.getAmount());
											if (settlementPayment == null)
											{
												settlementPayment = createSettlementPayment(payment, settlement);
												paymentValue.setSettlementPayment(settlementPayment);
											}
											else
											{
												updateSettlementPayment(payment, settlementPayment);
											}
										}
									}
								}
							}
							if (settlementReceipts.size() > 0)
							{
								for (SettlementReceipt settlementReceipt : settlementReceipts.values())
								{
									settlementReceipt.setDeleted(true);
								}
							}
							if (settlementPayedInvoices.size() > 0)
							{
								for (SettlementPayedInvoice settlementPayedInvoice : settlementPayedInvoices.values())
								{
									settlementPayedInvoice.setDeleted(true);
								}
							}
							if (settlementInternals.size() > 0)
							{
								for (SettlementInternal settlementInternal : settlementInternals.values())
								{
									settlementInternal.setDeleted(true);
								}
							}
							if (settlementPositions.size() > 0)
							{
								for (SettlementPosition settlementPosition : settlementPositions.values())
								{
									settlementPosition.setDeleted(true);
								}
							}
						}
					}
					finally
					{
						tracker.close();
					}
					tracker.close();
				}
			}
		}
	}
	
	private Map<Long, SettlementReceipt> loadSettlementReceipts(Settlement settlement)
	{
		Map<Long, SettlementReceipt> receipts = new HashMap<Long, SettlementReceipt>();
		List<SettlementReceipt> settlementReceipts = settlement.getReversedReceipts();
		for (SettlementReceipt settlementReceipt : settlementReceipts)
		{
			receipts.put(settlementReceipt.getReceiptId(), settlementReceipt);
		}
		return receipts;
	}
	
	private Map<Long, SettlementPosition> loadSettlementPositions(Settlement settlement)
	{
		Map<Long, SettlementPosition> positions = new HashMap<Long, SettlementPosition>();
		List<SettlementPosition> settlementPositions = settlement.getPositions();
		for (SettlementPosition settlementPosition : settlementPositions)
		{
			settlementPosition.setDeleted(true);
			settlementPosition.setQuantity(0);
			settlementPosition.setTaxAmount(0d);
			settlementPosition.setDefaultCurrencyAmount(0d);
			positions.put(settlementPosition.getProductGroup().getId(), settlementPosition);
		}
		return positions;
	}

	private Map<Long, SettlementPaymentValue> loadSettlementPayments(Settlement settlement)
	{
		Map<Long, SettlementPaymentValue> payments = new HashMap<Long, SettlementPaymentValue>();
		List<SettlementPayment> settlementPayments = settlement.getPayments();
		for (SettlementPayment settlementPayment : settlementPayments)
		{
			settlementPayment.setDeleted(true);
			SettlementPaymentValue value = payments.get(settlementPayment.getPaymentType());
			if (value == null)
			{
				value = new SettlementPaymentValue(settlementPayment);
			}
			else
			{
				value.setSettlementPayment(settlementPayment);
			}
		}
		return payments;
	}
	
	private class SettlementPaymentValue
	{
		private SettlementPayment payment;
		
		private SettlementPayment back;
	
		public SettlementPaymentValue(SettlementPayment settlementPayment)
		{
			setSettlementPayment(settlementPayment);
		}

		private SettlementPayment clear(SettlementPayment settlementPayment)
		{
			settlementPayment.setDefaultCurrencyAmount(0d);
			settlementPayment.setForeignCurrencyAmount(0d);
			settlementPayment.setQuantity(0);
			return settlementPayment;
		}
		public void setSettlementPayment(SettlementPayment settlementPayment)
		{
			if (settlementPayment.getDefaultCurrencyAmount() < 0)
			{
				this.back = clear(settlementPayment);
			}
			else
			{
				this.payment = clear(settlementPayment);
			}
		}
		
		public SettlementPayment getBack()
		{
			return this.back;
		}
		
		public SettlementPayment getPayment()
		{
			return this.payment;
		}
		
		public SettlementPayment getRight(double amount)
		{
			if (amount < 0)
			{
				return this.back;
			}
			else
			{
				return this.payment;
			}
		}
	}

	private SettlementPayment createSettlementPayment(Payment payment, Settlement settlement)
	{
		SettlementPayment settlementPayment= SettlementPayment.newInstance(settlement, payment.getPaymentType());
		return updateSettlementPayment(payment, settlementPayment);
	}
	
	private SettlementPayment updateSettlementPayment(Payment payment, SettlementPayment settlementPayment)
	{
		settlementPayment.setDeleted(false);
		settlementPayment.setQuantity(settlementPayment.getQuantity() + 1);
		settlementPayment.setDefaultCurrencyAmount(payment.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY));
		settlementPayment.setForeignCurrencyAmount(payment.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY));
		return settlementPayment;
	}

	private Map<Long, SettlementPayedInvoice> loadSettlementPayedInvoices(Settlement settlement)
	{
		Map<Long, SettlementPayedInvoice> payedInvoices = new HashMap<Long, SettlementPayedInvoice>();
		List<SettlementPayedInvoice> settlementPayedInvoices = settlement.getPayedInvoices();
		for (SettlementPayedInvoice settlementPayedInvoice : settlementPayedInvoices)
		{
			payedInvoices.put(settlementPayedInvoice.getPositionId(), settlementPayedInvoice);
		}
		return payedInvoices;
	}

	private Map<Long, SettlementInternal> loadSettlementInternals(Settlement settlement)
	{
		Map<Long, SettlementInternal> internals = new HashMap<Long, SettlementInternal>();
		List<SettlementInternal> settlementInternals = settlement.getInternals();
		for (SettlementInternal settlementInternal : settlementInternals)
		{
			internals.put(settlementInternal.getPositionId(), settlementInternal);
		}
		return internals;
	}

//	private Map<Long, SettlementTax> loadSettlementTaxes(Settlement settlement)
//	{
//		Map<Long, SettlementTax> positions = new HashMap<Long, SettlementTax>();
//		List<SettlementTax> settlementPositions = settlement.getPositions();
//		for (SettlementTax settlementPosition : settlementPositions)
//		{
//			settlementPosition.setQuantity(0);
//			settlementPosition.setTaxAmount(0d);
//			settlementPosition.setDefaultCurrencyAmount(0d);
//			positions.put(settlementPosition.getProductGroup().getId(), settlementPosition);
//		}
//		return positions;
//	}
}
