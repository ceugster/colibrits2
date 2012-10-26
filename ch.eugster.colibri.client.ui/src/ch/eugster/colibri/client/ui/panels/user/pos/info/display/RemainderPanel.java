/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.display;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Position.AmountType;

public class RemainderPanel extends DisplayPanel
{
	public static final long serialVersionUID = 0l;

	private static final String BACK = "Zurück";

	private static final String REMAINDER = "Offen";

	private double positionDefaultCurrencyAmount;

	public RemainderPanel(final UserPanel userPanel, final Profile profile)
	{
		super(userPanel, profile);
	}

	@Override
	public void setData(final Receipt receipt)
	{
		positionDefaultCurrencyAmount = receipt.getPositionDefaultCurrencyAmount(AmountType.NETTO);
		double backAmount = receipt.getPaymentDefaultCurrencyBackAmount();

		if (backAmount == positionDefaultCurrencyAmount)
		{
			defaultCurrencyAmount = 0d;
		}
		else if (backAmount == 0d)
		{
			defaultCurrencyAmount = positionDefaultCurrencyAmount - receipt.getPaymentDefaultCurrencyAmount();
		}
		else
		{
			defaultCurrencyAmount = backAmount;
		}

		final double positionForeignCurrencyAmount = receipt.getPositionDefaultForeignCurrencyAmount(AmountType.NETTO);
		backAmount = receipt.getPaymentDefaultForeignCurrencyBackAmount();

		if (backAmount == positionForeignCurrencyAmount)
		{
			foreignCurrencyAmount = 0d;
		}
		else if (backAmount == 0d)
		{
			foreignCurrencyAmount = positionForeignCurrencyAmount - receipt.getPaymentDefaultForeignCurrencyAmount();
		}
		else
		{
			foreignCurrencyAmount = backAmount;
		}

		textLabel.setText(getText());

		if (positionDefaultCurrencyAmount >= 0)
		{
			defaultCurrencyAmount = Math.abs(defaultCurrencyAmount);
			foreignCurrencyAmount = Math.abs(foreignCurrencyAmount);
		}

	}

	@Override
	protected String getText()
	{
		if (positionDefaultCurrencyAmount < 0d)
		{
			if (getDefaultCurrencyAmount() < 0d)
			{
				return RemainderPanel.REMAINDER;
			}
			else
			{
				return RemainderPanel.BACK;
			}
		}
		else
		{
			if (getDefaultCurrencyAmount() < 0)
			{
				return RemainderPanel.BACK;
			}
			else
			{
				return RemainderPanel.REMAINDER;
			}
		}
	}
}
