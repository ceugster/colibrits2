/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;

public class LogoutAction extends ConfigurableAction
{
	private static final long serialVersionUID = 1L;

	public LogoutAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		userPanel.getMainTabbedPane().removeUserPanel(userPanel);
	}

	protected boolean getState(final StateChangeEvent event)
	{
		boolean state = super.getState(event); 
		return event.getNewState().equals(UserPanel.State.MUST_SETTLE) ? true : state;
	}
}

