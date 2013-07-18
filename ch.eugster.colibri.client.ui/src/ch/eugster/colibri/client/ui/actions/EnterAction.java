/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.PaymentWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.numeric.ValueDisplay;
import ch.eugster.colibri.persistence.model.Profile;

public class EnterAction extends UserPanelProfileAction
{
	private static final long serialVersionUID = 0l;

	public static final String DEFAULT_TEXT = "Zahlungen";

	public static final String ACTION_COMMAND = "enter.action";

	public EnterAction(final UserPanel userPanel, final Profile profile)
	{
		super(EnterAction.DEFAULT_TEXT, EnterAction.ACTION_COMMAND, userPanel, profile);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand().equals(EnterAction.ACTION_COMMAND))
		{
			performEnterAction(event);
		}
	}

	private void performEnterAction(final ActionEvent event)
	{
		final UserPanel.State state = userPanel.getCurrentState();

		if (state.equals(UserPanel.State.POSITION_INPUT))
		{
			if (userPanel.getPositionWrapper().isPositionComplete())
			{
				userPanel.getPositionListPanel().getModel().actionPerformed(event);
			}
			else if (userPanel.getValueDisplay().getText().isEmpty())
			{
				userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.PAYMENT_INPUT));
			}
		}
		else if (state.equals(UserPanel.State.PAYMENT_INPUT))
		{
			final PaymentWrapper wrapper = userPanel.getPaymentWrapper();
			final ValueDisplay display = userPanel.getValueDisplay();

			if (!wrapper.isPaymentComplete() && display.getText().equals(""))
			{
				userPanel.fireStateChange(new StateChangeEvent(state, UserPanel.State.POSITION_INPUT));
			}
		}
	}

}
