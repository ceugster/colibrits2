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

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.ChargeType;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class PaymentTypeAction extends ConfigurableAction implements PropertyChangeListener, DisposeListener
{
	public static final long serialVersionUID = 0l;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public PaymentTypeAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		userPanel.getValueDisplay().addPropertyChangeListener(this);
		userPanel.addDisposeListener(this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		this.userPanel.getPaymentWrapper().getPayment().setPaymentType(this.getPaymentType());
		if (this.key.getValue() == 0d)
		{
			final double value = this.userPanel.getValueDisplay().getAmount();
			if (value == 0D)
			{
				if (!this.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
				{
					double difference = this.userPanel.getReceiptWrapper().getReceiptDifference();
					this.userPanel.getPaymentWrapper().getPayment().setAmount(difference);
				}
			}
			else
			{
				this.userPanel.getPaymentWrapper().getPayment().setAmount(value);
			}
		}
		else
		{
			this.userPanel.getPaymentWrapper().getPayment().setAmount(this.key.getValue());
		}
		if (this.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.CREDIT))
		{
			if (!this.getPaymentType().getChargeType().equals(ChargeType.NONE))
			{
				if (this.getPaymentType().getProductGroup() != null)
				{
					double amount = this.userPanel.getReceiptWrapper().getReceipt().getPositionAmount(Receipt.QuotationType.REFERENCE_CURRENCY, Position.AmountType.NETTO);
					double charge = this.getPaymentType().getChargeType().calculateCharge(this.getPaymentType(), amount);

					Position position = Position.newInstance(this.getUserPanel().getPaymentWrapper().getPayment().getReceipt());
					position.setProductGroup(this.getPaymentType().getProductGroup());
					position.setPrice(charge);
					position.setQuantity(1);

					amount = this.userPanel.getPaymentWrapper().getPayment().getAmount() + charge;
					this.userPanel.getReceiptWrapper().getReceipt().addPosition(position);
					this.userPanel.getPositionListPanel().getModel().actionPerformed(event);
					this.userPanel.getPaymentWrapper().getPayment().setAmount(amount);
				}
			}
		}
		
		this.userPanel.getPaymentPanel().getModel().actionPerformed(event);
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	public PaymentType getPaymentType()
	{
		PaymentType paymentType = null;
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			paymentType = (PaymentType) persistenceService.getCacheService().find(PaymentType.class, this.key.getParentId());
		}
		return paymentType;
	}

	@Override
	public boolean getState(final StateChangeEvent event)
	{
		if (this.key.getValue() == 0d)
		{
			if (this.userPanel.getValueDisplay().testAmount() == 0d)
			{
				return this.userPanel.getReceiptWrapper().getReceiptDifference() != 0D;
			}
			else
			{
				return false;
			}
		}
		return true;
	}

	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource().equals(this.userPanel.getValueDisplay()))
		{
			if (this.key.getValue() == 0d)
			{
				this.setEnabled(this.userPanel.getValueDisplay().testAmount() != 0d);
			}
		}
	}
}
