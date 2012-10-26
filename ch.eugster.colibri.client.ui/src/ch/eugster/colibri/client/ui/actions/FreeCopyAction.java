/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;

public final class FreeCopyAction extends ConfigurableAction implements DisposeListener
{
	public static final long serialVersionUID = 0l;

	public FreeCopyAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		userPanel.getPositionWrapper().setFreeCopy(true);
	}

	@Override
	public boolean getState(final StateChangeEvent event)
	{
		if (event.getNewState().equals(UserPanel.State.POSITION_INPUT))
		{
			return true;
		}
		return false;
	}

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		
	}
}
