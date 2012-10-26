/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.client.ui.events.PaymentChangeEvent;
import ch.eugster.colibri.client.ui.events.PaymentChangeListener;
import ch.eugster.colibri.client.ui.events.ReceiptChangeMediator;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Receipt;

public class PaymentWrapper implements PropertyChangeListener
{
	public static final String KEY_PAYMENT = "payment";

	public static final String KEY_PROPERTY_ID = "id";

	public static final String KEY_PROPERTY_TIMESTAMP = "timestamp";

	public static final String KEY_PROPERTY_VERSION = "version";

	public static final String KEY_PROPERTY_DELETED = "deleted";

	public static final String KEY_PROPERTY_AMOUNT = "amount";

	public static final String KEY_PROPERTY_ROUND_FACTOR = "roundFactor";

	public static final String KEY_PROPERTY_QUOTATION = "quotation";

	public static final String KEY_PROPERTY_SETTLEMENT = "settlement";

	public static final String KEY_PROPERTY_RECEIPT = "receipt";

	public static final String KEY_PROPERTY_PAYMENT_TYPE = "paymentType";

	public static final String KEY_PROPERTY_CURRENCY = "currency";

	public static final String KEY_PROPERTY_SALESPOINT = "salespoint";

	private Collection<PaymentChangeListener> paymentChangeListeners = new ArrayList<PaymentChangeListener>();

	private String[] propertyNames = new String[] { ReceiptWrapper.KEY_PROPERTY_PAYMENTS };

	private Payment payment;

	public PaymentWrapper(final UserPanel userPanel)
	{
		new ReceiptChangeMediator(userPanel, this, propertyNames);
	}

	public void addPaymentChangeListener(final PaymentChangeListener listener)
	{
		if (listener != null)
		{
			if (!paymentChangeListeners.contains(listener))
			{
				paymentChangeListeners.add(listener);
			}
		}
	}

	public Payment getPayment()
	{
		return payment;
	}

	public boolean isPaymentComplete()
	{
		if (payment.getPaymentType() == null)
		{
			return false;
		}
		if (payment.getAmount() == 0d)
		{
			return false;
		}
		return true;
	}

	public Payment preparePayment(final Receipt receipt)
	{
		final Payment newPayment = Payment.newInstance(receipt);
		firePaymentChangeEvent(new PaymentChangeEvent(payment, newPayment));
		payment = newPayment;
		return payment;
	}

	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource() instanceof Receipt)
		{
			if (event.getPropertyName().equals(ReceiptWrapper.KEY_PROPERTY_PAYMENTS))
			{
				preparePayment((Receipt) event.getSource());
			}
		}
	}

	public void removePaymentChangeListener(final PaymentChangeListener listener)
	{
		if (listener != null)
		{
			if (paymentChangeListeners.contains(listener))
			{
				paymentChangeListeners.remove(listener);
			}
		}
	}

	public Payment replacePayment(final Payment payment)
	{
		firePaymentChangeEvent(new PaymentChangeEvent(this.payment, payment));
		this.payment = payment;
		return this.payment;
	}

	private void firePaymentChangeEvent(final PaymentChangeEvent event)
	{
		final PaymentChangeListener[] listeners = paymentChangeListeners.toArray(new PaymentChangeListener[0]);
		for (final PaymentChangeListener listener : listeners)
		{
			listener.paymentChange(event);
		}
	}
}
