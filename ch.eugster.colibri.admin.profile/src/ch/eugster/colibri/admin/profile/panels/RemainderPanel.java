/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.panels;

import ch.eugster.colibri.persistence.model.Profile;

public class RemainderPanel extends DisplayPanel
{
	public static final long serialVersionUID = 0l;

	private static final String BACK = "Rückgeld";

	private static final String REMAINDER = "Offen";

	private double positionDefaultCurrencyAmount;

	public RemainderPanel(final Profile profile)
	{
		super(profile);
	}

	@Override
	public void setTestData()
	{
		textLabel.setText(BACK);
		foreignCurrencyLabel.setText("EUR");
		foreignCurrencyAmountLabel.setText("16.00");
		defaultCurrencyLabel.setText("CHF");
		defaultCurrencyAmountLabel.setText("25.60");
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
