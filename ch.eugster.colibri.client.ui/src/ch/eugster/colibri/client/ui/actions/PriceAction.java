/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.MainTabbedPane;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;

public final class PriceAction extends UserPanelProfileAction
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Preis";

	public static final String ACTION_COMMAND = "price.action";

	private double maxRange = 0d;

	private double maxAmount = 0d;

	public PriceAction(final UserPanel userPanel, final Profile profile)
	{
		super(PriceAction.TEXT, PriceAction.ACTION_COMMAND, userPanel, profile);
		maxRange = Math.abs(userPanel.getMainTabbedPane().getSetting().getMaxPriceRange());
		maxAmount = Math.abs(userPanel.getMainTabbedPane().getSetting().getMaxPriceAmount());
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final double price = userPanel.getValueDisplay().testAmount();

		int result = ch.eugster.colibri.client.ui.dialogs.MessageDialog.BUTTON_YES;
		if (maxRange > 0 && maxRange <= Math.abs(price))
		{
			final String title = "Eingabeprüfung";
			final String message = "Der eingegebene Preis ist sehr hoch. Soll er trotzdem akzeptiert werden?";
			final int messageType = ch.eugster.colibri.client.ui.dialogs.MessageDialog.TYPE_QUESTION;
			result = ch.eugster.colibri.client.ui.dialogs.MessageDialog.showSimpleDialog(Activator.getDefault()
					.getFrame(), profile, title, message, messageType);
		}
		if (result == ch.eugster.colibri.client.ui.dialogs.MessageDialog.BUTTON_YES)
		{
			userPanel.getPositionWrapper().getPosition().setPrice(userPanel.getValueDisplay().getAmount());
		}
		userPanel.getPositionListPanel().getModel().actionPerformed(event);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource().equals(userPanel.getValueDisplay()))
		{
			if (event.getPropertyName().equals("value"))
			{
				if (!userPanel.getCurrentState().equals(UserPanel.State.LOCKED))
				{
					setEnabled(shouldEnable());
				}
			}
		}
	}

	@Override
	protected boolean getState(final StateChangeEvent event)
	{
		return shouldEnable();
	}

	private boolean shouldEnable()
	{
		final double price = Math.abs(userPanel.getValueDisplay().testAmount());
		return (price > 0) && ((maxAmount == 0d) || (maxAmount >= price));
	}
}
