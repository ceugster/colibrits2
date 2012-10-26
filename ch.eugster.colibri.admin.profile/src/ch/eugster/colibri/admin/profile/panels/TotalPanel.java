/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.panels;

import ch.eugster.colibri.persistence.model.Profile;

public class TotalPanel extends DisplayPanel
{
	public static final long serialVersionUID = 0l;

	private static final String TEXT = "Total";

	public TotalPanel(final Profile profile)
	{
		super(profile);
		textLabel.setText(TotalPanel.TEXT);
	}

	@Override
	public void setTestData()
	{
		textLabel.setText("Total");
		foreignCurrencyLabel.setText("EUR");
		foreignCurrencyAmountLabel.setText("1'234.00");
		defaultCurrencyLabel.setText("CHF");
		defaultCurrencyAmountLabel.setText("1'974.40");
	}

	@Override
	protected String getText()
	{
		return TotalPanel.TEXT;
	}

}
