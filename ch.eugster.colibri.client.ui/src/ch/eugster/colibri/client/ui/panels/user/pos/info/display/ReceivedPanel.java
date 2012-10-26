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

public class ReceivedPanel extends DisplayPanel
{
	public static final long serialVersionUID = 0l;

	private static final String TEXT = "Erhalten";

	public ReceivedPanel(final UserPanel userPanel, final Profile profile)
	{
		super(userPanel, profile);
	}

	@Override
	public void setData(final Receipt receipt)
	{
		if (receipt.getPaymentDefaultCurrencyBackAmount() == receipt.getPositionDefaultCurrencyAmount(AmountType.NETTO))
		{
			defaultCurrencyAmount = receipt.getPaymentDefaultCurrencyBackAmount();
			foreignCurrencyAmount = receipt.getPaymentDefaultForeignCurrencyBackAmount();
		}
		else
		{
			defaultCurrencyAmount = receipt.getPaymentDefaultCurrencyAmount() - receipt.getPaymentDefaultCurrencyBackAmount();
			foreignCurrencyAmount = receipt.getPaymentDefaultForeignCurrencyAmount() - receipt.getPaymentDefaultForeignCurrencyBackAmount();
		}
	}

	@Override
	protected String getText()
	{
		return ReceivedPanel.TEXT;
	}

}
