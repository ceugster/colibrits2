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
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.ChargeType;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Payment;
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
		if (key.getLabel().equals("Barzahlung"))
		{
			System.out.println(this.userPanel.getReceiptWrapper().getReceiptDifference());
		}
		this.userPanel.getPaymentWrapper().getPayment().setPaymentType(this.getPaymentType());
		if (this.key.getValue() == 0d)
		{
			final double value = this.userPanel.getValueDisplay().getAmount();
			if (value == 0D)
			{
				if (!this.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
				{
					if (this.getPaymentType().getCurrency().getId().equals(this.userPanel.getSalespoint().getCommonSettings().getReferenceCurrency().getId()))
					{
						double difference = this.userPanel.getReceiptWrapper().getReceiptDifference();
						this.userPanel.getPaymentWrapper().getPayment().setBack(difference < 0);
						this.userPanel.getPaymentWrapper().getPayment().setAmount(difference);
					}
					else
					{
						double difference = this.userPanel.getReceiptWrapper().getReceiptDifference();
						Payment payment = Payment.newInstance(this.userPanel.getReceiptWrapper().getReceipt());
						payment.setPaymentType(this.getPaymentType());
						payment.setBack(difference < 0);
						payment.setAmount(difference, Receipt.QuotationType.DEFAULT_CURRENCY, Receipt.QuotationType.FOREIGN_CURRENCY);
						this.userPanel.getPaymentWrapper().replacePayment(payment);
					}
				}
			}
			else
			{
				boolean setAmount = true;
				double range = this.userPanel.getSalespoint().getCommonSettings().getMaxPaymentRange();
				double max = this.userPanel.getSalespoint().getCommonSettings().getMaxPaymentAmount();
				if (Math.abs(value) > Math.abs(range))
				{
					if (Math.abs(value) > Math.abs(max))
					{
						MessageDialog.showInformation(Activator.getDefault().getFrame(), this.userPanel.getProfile(), "Eingabe zu hoch", "Der eingegebene Betrag ist höher als zugelassen.\nBitte korrigieren Sie den Betrag.", MessageDialog.TYPE_ERROR);
						setAmount = false;
						return;
					}
					else
					{
						if (MessageDialog.showQuestion(Activator.getDefault().getFrame(), this.userPanel.getProfile(), "Eingabe sehr hoch", "Der eingegebene Betrag ist sehr hoch.\nWollen Sie ihn trotzdem verwenden?", MessageDialog.TYPE_QUESTION, new int[] { MessageDialog.BUTTON_YES, MessageDialog.BUTTON_NO}, 0) != MessageDialog.BUTTON_YES)
						{
							setAmount = false;
						}
					}
				}
				if (setAmount)
				{
					this.userPanel.getPaymentWrapper().getPayment().setAmount(value);
				}
			}
		}
		else
		{
			this.userPanel.getPaymentWrapper().getPayment().setAmount(this.key.getValue());
			this.userPanel.getValueDisplay().getAmount();

		}
		if (this.getPaymentType().getPaymentTypeGroup().isChargable())
		{
			if (this.getPaymentType().getChargeType() != null && !this.getPaymentType().getChargeType().equals(ChargeType.NONE))
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
		double inputAmount = this.userPanel.getValueDisplay().testAmount();
		double receiptDiff = this.userPanel.getReceiptWrapper().getReceiptDifference();
		
		if (key.getLabel().equals("Barzahlung"))
		{
			System.out.println(this.userPanel.getReceiptWrapper().getReceiptDifference());
		}
		if (this.key.getValue() == 0d)
		{
			if (this.userPanel.getValueDisplay().testAmount() == 0d)
			{
				if (this.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
				{
					return this.userPanel.getValueDisplay().testAmount() > 0D;
				}
				else
				{
					return this.userPanel.getReceiptWrapper().getReceiptDifference() > 0D;
				}
			}
			else
			{
				return true;
			}
		}
		return true;
	}

	public void propertyChange(final PropertyChangeEvent event)
	{
		double inputAmount = this.userPanel.getValueDisplay().testAmount();
		double receiptDiff = this.userPanel.getReceiptWrapper().getReceiptDifference();
		if (key.getLabel().equals("Barzahlung"))
		{
			System.out.println();
		}
		if (event.getSource().equals(this.userPanel.getValueDisplay()))
		{
			if (this.key.getValue() == 0d)
			{
				if (this.getPaymentType().getPaymentTypeGroup().isChargable())
				{
					this.setEnabled(this.userPanel.getReceiptWrapper().getReceiptDifference() > 0D);
				}
				else
				{
					this.setEnabled(this.userPanel.getReceiptWrapper().getReceiptDifference() > 0D);
//					this.setEnabled(this.userPanel.getValueDisplay().testAmount() != 0d);
				}
			}
		}
	}
}
