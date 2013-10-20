/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.PaymentChangeMediator;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class StoreReceiptShorthandAction extends ConfigurableAction implements PropertyChangeListener
{
	private static final long serialVersionUID = 0l;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private PaymentType paymentType;

	public StoreReceiptShorthandAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		new PositionChangeMediator(userPanel, this, new String[] { "position" });
		new PaymentChangeMediator(userPanel, this, new String[] { "payment" });

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			this.paymentType = (PaymentType) persistenceService.getCacheService().find(PaymentType.class,
					Long.valueOf(key.getParentId()));
		}
		this.persistenceServiceTracker.close();
		userPanel.getNumericPadPanel().addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand().equals(this.getActionCommand()))
		{
			final double difference = this.userPanel.getReceiptWrapper().getReceiptDifference();
			if (difference != 0d)
			{
				final Payment payment = this.userPanel.getPaymentWrapper().preparePayment(
						this.userPanel.getReceiptWrapper().getReceipt());
				payment.setPaymentType(this.paymentType);
				payment.setAmount(difference, Receipt.QuotationType.REFERENCE_CURRENCY,
						Receipt.QuotationType.FOREIGN_CURRENCY);
				payment.setBack(difference < 0d);
				this.userPanel.getReceiptWrapper().getReceipt().addPayment(payment);
			}
			if (this.userPanel.getReceiptWrapper().isReceiptBalanced())
			{
				this.userPanel.getReceiptWrapper().storeReceipt();
			}
		}
		else if (event.getActionCommand().equals(DeleteAction.ACTION_COMMAND))
		{
			this.setEnabled(shouldEnable());
		}
	}

	@Override
	public boolean getState(final StateChangeEvent event)
	{
		boolean enabled = super.getState(event);
		return enabled ? this.shouldEnable() : false;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		this.setEnabled(this.shouldEnable());
	}

	private boolean shouldEnable()
	{
		if (this.paymentType != null)
		{
			if (this.paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
			{
				final Receipt receipt = this.userPanel.getReceiptWrapper().getReceipt();
				final Collection<Payment> payments = receipt.getPayments();
				if (payments.isEmpty())
				{
					return false;
				}
				final double paymentAmount = receipt.getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
				final double positionAmount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
						Position.AmountType.NETTO);
				if ((paymentAmount <= positionAmount) || !receipt.hasVoucherPayment())
				{
					return false;
				}
			}

			final int positionSize = this.userPanel.getReceiptWrapper().getReceipt().getPositions().size();
			final int paymentSize = this.userPanel.getReceiptWrapper().getReceipt().getPayments().size();
			return (positionSize > 0) || (paymentSize > 0);
		}
		else
		{
			return false;
		}
	}
}
