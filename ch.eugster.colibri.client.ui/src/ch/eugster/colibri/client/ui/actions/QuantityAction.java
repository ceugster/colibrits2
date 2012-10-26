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

public final class QuantityAction extends UserPanelProfileAction
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Menge";

	public static final String ACTION_COMMAND = "quantity.action";

	private int maxRange = 0;

	private int maxQuantity = 0;

	public QuantityAction(final UserPanel userPanel, final Profile profile)
	{
		super(QuantityAction.TEXT, QuantityAction.ACTION_COMMAND, userPanel, profile);
		maxRange = Math.abs(MainTabbedPane.getTabbedPane().getSetting().getMaxQuantityRange());
		maxQuantity = Math.abs(MainTabbedPane.getTabbedPane().getSetting().getMaxQuantityAmount());
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final int quantity = userPanel.getValueDisplay().testQuantity();

		int result = ch.eugster.colibri.client.ui.dialogs.MessageDialog.BUTTON_YES;
		if ((maxRange > 0) && (maxRange < Math.abs(quantity)))
		{
			final String title = "Eingabeüberprüfung";
			final String message = "Die eingegebene Menge ist sehr hoch. Soll sie trotzdem akzeptiert werden?";
			final int messageType = ch.eugster.colibri.client.ui.dialogs.MessageDialog.TYPE_QUESTION;
			result = ch.eugster.colibri.client.ui.dialogs.MessageDialog.showSimpleDialog(Activator.getDefault()
					.getFrame(), profile, title, message, messageType);
		}
		if (result == ch.eugster.colibri.client.ui.dialogs.MessageDialog.BUTTON_YES)
		{
			userPanel.getPositionWrapper().getPosition().setQuantity(userPanel.getValueDisplay().getQuantity());
		}
		userPanel.getPositionListPanel().getModel().actionPerformed(event);
	}

	@Override
	public boolean getState(final StateChangeEvent event)
	{
		return shouldEnable();
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

	private boolean shouldEnable()
	{
		final int quantity = Math.abs(userPanel.getValueDisplay().testQuantity());
		return (quantity > 0) && ((maxQuantity == 0) || (quantity <= maxQuantity));
	}
}
