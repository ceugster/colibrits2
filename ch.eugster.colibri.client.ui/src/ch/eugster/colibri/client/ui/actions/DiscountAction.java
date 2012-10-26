/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.beans.PropertyChangeEvent;

import javax.swing.Action;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;

public final class DiscountAction extends UserPanelProfileAction
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Rabatt (%)";

	public static final String ACTION_COMMAND = "discount.action";

	public DiscountAction(final UserPanel userPanel, final Profile profile)
	{
		super(DiscountAction.TEXT, DiscountAction.ACTION_COMMAND, userPanel, profile);
		putValue(Action.ACTION_COMMAND_KEY, DiscountAction.ACTION_COMMAND);
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
		final double discount = Math.abs(userPanel.getValueDisplay().testDiscount());
		return (discount >= 0d) && (discount <= 100d);
	}
}
