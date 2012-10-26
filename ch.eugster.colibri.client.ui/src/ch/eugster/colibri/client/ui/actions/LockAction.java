/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;

public class LockAction extends ConfigurableAction
{
	public static final long serialVersionUID = 0l;

	public LockAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (userPanel.getCurrentState().equals(UserPanel.State.LOCKED))
		{
			if (userPanel.getUser().getPosLogin().equals(userPanel.getValueDisplay().getPosLogin()))
			{
				userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), userPanel.getOldState()));
			}
			else
			{
				final String title = "Ungültiges Passwort";
				final String message = "Das eingegebene Passwort ist ungültig.";
				final int messageType = ch.eugster.colibri.client.ui.dialogs.MessageDialog.TYPE_INFORMATION;
				MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), userPanel.getProfile(), title, message, messageType);
			}
		}
		else
		{
			userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.LOCKED));
		}
	}
}
