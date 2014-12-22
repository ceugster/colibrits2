/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.display;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;

public class TotalPanel extends DisplayPanel
{
	public static final long serialVersionUID = 0l;

	private static final String TEXT = "Total";

	public TotalPanel(final UserPanel userPanel, final Profile profile)
	{
		super(userPanel, profile);
		textLabel.setText(TotalPanel.TEXT);
	}

	@Override
	public void setData(final Receipt receipt)
	{
//		if (receipt.getPositionAmount(Receipt.QuotationType.REFERENCE_CURRENCY, Position.AmountType.NETTO) != 0D)
//		{
			defaultCurrencyAmount = receipt.getPositionDefaultCurrencyAmount(AmountType.NETTO);
			foreignCurrencyAmount = receipt.getPositionDefaultForeignCurrencyAmount(AmountType.NETTO);
//		}
	}

	@Override
	protected String getText()
	{
		return TotalPanel.TEXT;
	}
}
