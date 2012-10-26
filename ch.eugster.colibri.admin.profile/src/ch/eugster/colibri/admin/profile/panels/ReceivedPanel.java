/*
 * Created on 18.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.panels;

import ch.eugster.colibri.persistence.model.Profile;

public class ReceivedPanel extends DisplayPanel
{
	public static final long serialVersionUID = 0l;

	private static final String TEXT = "Erhalten";

	public ReceivedPanel(final Profile profile)
	{
		super(profile);
	}

	@Override
	public void setTestData()
	{
		textLabel.setText("Erhalten");
		foreignCurrencyLabel.setText("EUR");
		foreignCurrencyAmountLabel.setText("1'250.00");
		defaultCurrencyLabel.setText("CHF");
		defaultCurrencyAmountLabel.setText("2'000.00");
	}

	@Override
	protected String getText()
	{
		return ReceivedPanel.TEXT;
	}
}
