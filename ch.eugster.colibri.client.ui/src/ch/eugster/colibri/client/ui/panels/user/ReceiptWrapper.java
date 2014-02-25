/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.ReceiptChangeEvent;
import ch.eugster.colibri.client.ui.events.ReceiptChangeListener;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.ProviderState;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Receipt.QuotationType;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.rules.LocalDatabaseRule;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.voucher.VoucherService;

public class ReceiptWrapper implements DisposeListener, PropertyChangeListener
{
	public static final String KEY_RECEIPT = "receipt";

	public static final String KEY_PROPERTY_NUMBER = "number";

	public static final String KEY_PROPERTY_TRANSACTION = "transaction";

	public static final String KEY_PROPERTY_BOOKKEEPING_TRANSACTION = "bookkeepingTransaction";

	public static final String KEY_PROPERTY_STATE = "state";

	public static final String KEY_PROPERTY_SETTLEMENT = "settlement";

	public static final String KEY_PROPERTY_SALESPOINT = "salespoint";

	public static final String KEY_PROPERTY_CURRENCY = "currency";

	public static final String KEY_PROPERTY_USER = "user";

	public static final String KEY_PROPERTY_POSITIONS = "positions";

	public static final String KEY_PROPERTY_PAYMENTS = "payments";

	private final Collection<ReceiptChangeListener> receiptChangeListeners = new ArrayList<ReceiptChangeListener>();

	private final ServiceTracker<LogService, LogService> logServiceTracker;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

//	private final ServiceTracker<ProviderInterface, ProviderInterface> providerInterfaceTracker;

	private final ServiceTracker<BarcodeVerifier, BarcodeVerifier> barcodeVerifierTracker;

	private ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptPrinterTracker;

	private final ServiceTracker<EventAdmin, EventAdmin> eventServiceTracker;

	private final ServiceTracker<VoucherService, VoucherService> voucherServiceTracker;

	private final UserPanel userPanel;

	private Receipt receipt;

	public ReceiptWrapper(UserPanel userPanel)
	{
		this.userPanel = userPanel;

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		this.logServiceTracker.open();

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

//		this.providerInterfaceTracker = new ServiceTracker<ProviderInterface, ProviderInterface>(Activator.getDefault().getBundle().getBundleContext(),
//				ProviderInterface.class, null);
//		this.providerInterfaceTracker.open();

		this.barcodeVerifierTracker = new ServiceTracker<BarcodeVerifier, BarcodeVerifier>(Activator.getDefault().getBundle().getBundleContext(),
				BarcodeVerifier.class, null);
		this.barcodeVerifierTracker.open();

		this.eventServiceTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventServiceTracker.open();

		this.voucherServiceTracker = new ServiceTracker<VoucherService, VoucherService>(Activator.getDefault().getBundle().getBundleContext(),
				VoucherService.class, null);
		this.voucherServiceTracker.open();
	}

	public void addReceiptChangeListener(final ReceiptChangeListener listener)
	{
		if (listener != null)
		{
			if (!this.receiptChangeListeners.contains(listener))
			{
				this.receiptChangeListeners.add(listener);
			}
		}
	}

	public void clearPayments()
	{
		this.receipt.clearPayments();
		this.fireReceiptChangeEvent(new ReceiptChangeEvent(this.receipt, this.receipt));
	}

	@Override
	public void dispose()
	{
		this.receiptPrinterTracker.close();
		this.barcodeVerifierTracker.close();
//		this.providerInterfaceTracker.close();
		this.persistenceServiceTracker.close();
		this.eventServiceTracker.close();
		this.voucherServiceTracker.close();
		this.receiptChangeListeners.clear();
	}

	public Receipt getReceipt()
	{
		return this.receipt;
	}

	public double getReceiptDifference()
	{
		final double positionAmount = this.receipt.getPositionReferenceCurrencyAmount(AmountType.NETTO);
		final double paymentAmount = this.receipt.getPaymentReferenceCurrencyAmount();
		final double value = BigDecimal.valueOf(positionAmount - paymentAmount).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
		return value;
	}

	public boolean isReceiptBalanced()
	{
		final double positionAmount = this.receipt.getPositionReferenceCurrencyAmount(AmountType.NETTO);
		final double paymentAmount = this.receipt.getPaymentReferenceCurrencyAmount();
		final double value = BigDecimal.valueOf(positionAmount - paymentAmount).setScale(2, RoundingMode.HALF_UP)
				.doubleValue();
		return value == 0d;
	}

	public boolean isReceiptEmpty()
	{
		if (this.receipt == null)
		{
			return true;
		}
		if (this.receipt.getPositions().size() > 0)
		{
			return false;
		}
		if (this.receipt.getPayments().size() > 0)
		{
			return false;
		}
		return true;
	}

	public void parkReceipt()
	{
		this.receipt.setState(Receipt.State.PARKED);
		this.receipt.setNumber(this.userPanel.getSalespoint().getNextParkedReceiptNumber());
		updateVoucherProvider();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			try
			{
				persistenceService.getCacheService().merge(this.receipt);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				MessageDialog.showInformation(Activator.getDefault().getFrame(), userPanel.getProfile(), "Fehler", "Der Beleg konnte nicht geparkt werden.", MessageDialog.TYPE_ERROR);
			}
		}
	}
	
	private void updateVoucherProvider()
	{
		VoucherService service = voucherServiceTracker.getService();
		if (service != null)
		{
			Collection<Position> positions = receipt.getPositions();
			for (Position position : positions)
			{
				if (position.getProvider() != null && position.getProvider().equals(service.getProviderId()))
				{
					if (!position.isDeleted() && position.getProviderState().equals(ProviderState.RESERVED))
					{
						VoucherService.Result result = service.cancelReservedAmount(position.getProvider(), position.getAmount(QuotationType.DEFAULT_CURRENCY, AmountType.NETTO));
						position.setProviderState(result.isOK() ? ProviderState.OPEN : position.getProviderState());
					}
					position.setDeleted(true);
				}
			}
			Collection<Payment> payments = receipt.getPayments();
			for (Payment payment : payments)
			{
				if (payment.getProviderId() != null && payment.getProviderId().equals(service.getProviderId()))
				{
					if (!payment.isDeleted() && payment.getProviderState().equals(ProviderState.RESERVED))
					{
						VoucherService.Result result = service.cancelReservedAmount(payment.getProviderId(), payment.getAmount(QuotationType.DEFAULT_CURRENCY));
						payment.setProviderState(result.isOK() ? ProviderState.OPEN : payment.getProviderState());
					}
					payment.setDeleted(true);
				}
			}
		}
	}
	
	Receipt prepareReceipt()
	{
		if (this.receipt != null)
		{
			this.receipt.removePropertyChangeListener("customer", this);
		}
		final Receipt newReceipt = Receipt.newInstance(this.userPanel.getSalespoint().getSettlement(),
				this.userPanel.getUser());
		newReceipt.addPropertyChangeListener("customer", this);
		this.fireReceiptChangeEvent(new ReceiptChangeEvent(this.receipt, newReceipt));
		this.receipt = newReceipt;
		return this.receipt;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt)
	{
		this.fireReceiptChangeEvent(new ReceiptChangeEvent(null, this.receipt));
	}

	public void removeReceiptChangeListener(final ReceiptChangeListener listener)
	{
		if (listener != null)
		{
			if (this.receiptChangeListeners.contains(listener))
			{
				this.receiptChangeListeners.remove(listener);
			}
		}
	}

	public Receipt replaceReceipt(final Receipt receipt)
	{
		this.receipt.removePropertyChangeListener("customer", this);
		this.fireReceiptChangeEvent(new ReceiptChangeEvent(this.receipt, receipt));
		this.receipt = receipt;
		this.receipt.addPropertyChangeListener("customer", this);
		return this.receipt;
	}

	public void setDiscount(final double discount)
	{
		Collection<Position> positions = this.receipt.getPositions();
		for (final Position position : positions)
		{
			if (position.getQuantity() > 0 && position.getProductGroup().getProductGroupType().equals(ProductGroupType.SALES_RELATED))
			{
				position.setDiscount(discount);
			}
		}
//		this.userPanel.getPositionWrapper().preparePosition(this.receipt);
		this.fireReceiptChangeEvent(new ReceiptChangeEvent(null, this.receipt));
//		final EventAdmin eventAdmin = (EventAdmin) this.eventServiceTracker.getService();
//		if (eventAdmin != null)
//		{
//			eventAdmin.sendEvent(this.getEvent(Topic.POSITION_ADDED.topic(), positions[positions.length - 1]));
//		}
	}
	
	public void storeReceipt()
	{
		this.storeReceipt(false);
	}

	public void storeReceipt(final boolean deleted)
	{
		Job job = new Job("Storing Receipt...")
		{
			@Override
			public IStatus run(IProgressMonitor monitor) 
			{
				ReceiptWrapper.this.receipt.setDeleted(deleted);
				ReceiptWrapper.this.receipt.setState(Receipt.State.SAVED);
				ReceiptWrapper.this.receipt.setNumber(ReceiptWrapper.this.userPanel.getSalespoint().getNextReceiptNumber());

				final PersistenceService persistenceService = (PersistenceService) ReceiptWrapper.this.persistenceServiceTracker.getService();
				if (persistenceService != null)
				{
////					Long receiptId = ReceiptWrapper.this.receipt.getId();
					try
					{
						ReceiptWrapper.this.receipt = (Receipt) persistenceService.getCacheService().merge(ReceiptWrapper.this.receipt);
						if (!ReceiptWrapper.this.receipt.isDeleted())
						{
							ReceiptWrapper.this.sendEvent(ReceiptWrapper.this.receipt);
						}
						userPanel.setSalespoint((Salespoint) persistenceService.getCacheService().merge(userPanel.getSalespoint()));
						ReceiptWrapper.this.prepareReceipt();
//						ReceiptWrapper.this.receipt = ReceiptWrapper.this.prepareReceipt();
						ReceiptWrapper.this.userPanel.getPositionWrapper().preparePosition(ReceiptWrapper.this.userPanel.getReceiptWrapper().receipt);
						ReceiptWrapper.this.userPanel.getPaymentWrapper().preparePayment(ReceiptWrapper.this.userPanel.getReceiptWrapper().receipt);
						ReceiptWrapper.this.userPanel.fireStateChange(new StateChangeEvent(ReceiptWrapper.this.userPanel.getCurrentState(),
								UserPanel.State.POSITION_INPUT));
					} 
					catch (Exception e) 
					{
						e.printStackTrace();
						ReceiptWrapper.this.sendEvent(ReceiptWrapper.this.receipt, e);
						MessageDialog.showInformation(Activator.getDefault().getFrame(), userPanel.getProfile(), "Fehler", "Der Beleg konnte nicht gespeichert werden.", MessageDialog.TYPE_ERROR);
					}
				}
				return Status.OK_STATUS;
			}
		};
		job.setRule(LocalDatabaseRule.getRule());
		job.setPriority(Job.INTERACTIVE);
		job.schedule();
	}

	public void fireReceiptChangeEvent(final ReceiptChangeEvent event)
	{
		final ReceiptChangeListener[] listeners = this.receiptChangeListeners.toArray(new ReceiptChangeListener[0]);
		for (final ReceiptChangeListener listener : listeners)
		{
			listener.receiptChange(event);
		}
	}

	private Event getEvent(final String topics, final Receipt receipt, Exception e)
	{
		boolean openCashdrawer = false;
		for (Payment payment : receipt.getPayments())
		{
			if (payment.getPaymentType().isOpenCashdrawer())
			{
				openCashdrawer = true;
				break;
			}
		}
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.SERVICE, this.eventServiceTracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID,
				this.eventServiceTracker.getServiceReference().getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		IStatus status = null;
		if (e != null)
		{
			properties.put(EventConstants.EXCEPTION, e);
			properties.put(EventConstants.EXCEPTION_CLASS, e.getClass());
			properties.put(EventConstants.EXCEPTION, e.getMessage());
			status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.STORE_RECEIPT.topic(), e);
		}
		else
		{
			status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.STORE_RECEIPT.topic());
		}
		properties.put(IPrintable.class.getName(), receipt);
		properties.put("status", status);
		properties.put("open.drawer", Boolean.valueOf(openCashdrawer));
		properties.put("copies", receipt.hasRestitution() ? Integer.valueOf(userPanel.getRestitutionPrintCount()) : Integer.valueOf(1));
		return new Event(topics, properties);
	}

	private void sendEvent(final Receipt receipt)
	{
		sendEvent(receipt, null);
	}

	private void sendEvent(final Receipt receipt, Exception e)
	{
		final EventAdmin eventAdmin = (EventAdmin) this.eventServiceTracker.getService();
		if (eventAdmin != null)
		{
			eventAdmin.sendEvent(this.getEvent(Topic.STORE_RECEIPT.topic(), receipt, e));
		}
	}

}
