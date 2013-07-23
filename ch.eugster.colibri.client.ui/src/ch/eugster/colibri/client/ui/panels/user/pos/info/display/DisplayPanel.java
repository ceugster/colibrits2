/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.display;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.eugster.colibri.client.ui.events.PaymentChangeMediator;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.events.ReceiptChangeMediator;
import ch.eugster.colibri.client.ui.panels.user.PaymentWrapper;
import ch.eugster.colibri.client.ui.panels.user.PositionWrapper;
import ch.eugster.colibri.client.ui.panels.user.ReceiptWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;

public abstract class DisplayPanel extends ch.eugster.colibri.ui.panels.DisplayPanel implements PropertyChangeListener
{
	public static final long serialVersionUID = 0l;

	protected Currency defaultCurrency;

	protected Currency foreignCurrency;

	protected String[] positionProperties = new String[] { ReceiptWrapper.KEY_PROPERTY_POSITIONS,
			PositionWrapper.KEY_POSITION };

	protected String[] paymentProperties = new String[] { ReceiptWrapper.KEY_PROPERTY_PAYMENTS,
			PaymentWrapper.KEY_PAYMENT, PaymentWrapper.KEY_PROPERTY_PAYMENT_TYPE };

	protected String[] receiptProperties = new String[] { ReceiptWrapper.KEY_PROPERTY_POSITIONS,
			ReceiptWrapper.KEY_PROPERTY_PAYMENTS };

	protected UserPanel userPanel;

	protected ReceiptChangeMediator receiptChangeMediator;

	protected PositionChangeMediator positionChangeMediator;

	protected PaymentChangeMediator paymentChangeMediator;

	public DisplayPanel(final UserPanel userPanel, final Profile profile)
	{
		super(profile);
		this.init(userPanel);
	}

	public void display(final Receipt receipt)
	{
		setData(receipt);

		defaultCurrency = receipt.getDefaultCurrency();
		foreignCurrency = receipt.getForeignCurrency();

		if ((foreignCurrency == null) || defaultCurrency.getId().equals(foreignCurrency.getId()))
		{
			foreignCurrencyLabel.setText("");
			foreignCurrencyAmountLabel.setText("");
		}
		else
		{
			final java.util.Currency fc = java.util.Currency.getInstance(foreignCurrency.getCode());
			foreignCurrencyFormat.setMaximumFractionDigits(fc.getDefaultFractionDigits());
			foreignCurrencyFormat.setMinimumFractionDigits(fc.getDefaultFractionDigits());
			foreignCurrencyLabel.setText(foreignCurrency.getCode());
			foreignCurrencyAmountLabel.setText(foreignCurrencyFormat.format(foreignCurrencyAmount));
		}

		final java.util.Currency dc = java.util.Currency.getInstance(defaultCurrency.getCode());
		if (defaultCurrencyFormat.getMaximumFractionDigits() != dc.getDefaultFractionDigits())
		{
			defaultCurrencyFormat.setMaximumFractionDigits(dc.getDefaultFractionDigits());
		}
		if (defaultCurrencyFormat.getMinimumFractionDigits() != dc.getDefaultFractionDigits())
		{
			defaultCurrencyFormat.setMinimumFractionDigits(dc.getDefaultFractionDigits());
		}
		if (!defaultCurrencyLabel.getText().equals(defaultCurrency.getCode()))
		{
			defaultCurrencyLabel.setText(defaultCurrency.getCode());
		}
		defaultCurrencyAmountLabel.setText(defaultCurrencyFormat.format(defaultCurrencyAmount));

		textLabel.validate();
		foreignCurrencyPanel.validate();
		defaultCurrencyPanel.validate();
		amountPanel.validate();
	}

	@Override
	public void finalize()
	{
		EntityMediator.removeListener(Profile.class, this);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource().equals(positionChangeMediator))
		{
			display(((Position) event.getNewValue()).getReceipt());
		}
		else if (event.getSource().equals(paymentChangeMediator))
		{
			display(((Payment) event.getNewValue()).getReceipt());
		}
		else if (event.getSource().equals(receiptChangeMediator))
		{
			if (event.getPropertyName().equals("receipt") || event.getPropertyName().equals("positions")
					|| event.getPropertyName().equals("payments"))
			{
				if (event.getSource() instanceof Receipt)
				{
					display((Receipt) event.getSource());
				}
				else if (event.getNewValue() instanceof Receipt)
				{
					final Receipt receipt = (Receipt) event.getNewValue();
					if ((receipt.getPositions().size() > 0) || (receipt.getPayments().size() > 0))
					{
						display(receipt);
					}
				}
			}
		}
	}

	public abstract void setData(Receipt receipt);

	protected void init(final UserPanel userPanel)
	{
		this.userPanel = userPanel;

		positionChangeMediator = new PositionChangeMediator(this.userPanel, this, positionProperties);
		paymentChangeMediator = new PaymentChangeMediator(this.userPanel, this, paymentProperties);
		receiptChangeMediator = new ReceiptChangeMediator(this.userPanel, this, receiptProperties);

		EntityMediator.addListener(Profile.class, this);
	}

}
