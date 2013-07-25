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
		double value = this.key.getValue() == 0D ? 1D : this.key.getValue();
		userPanel.getPositionWrapper().getPosition().setDiscount(value);
	}

	@Override
	public boolean getState(final StateChangeEvent event)
	{
		if (super.getState(event)) 
		{
			if (event.getNewState().equals(UserPanel.State.POSITION_INPUT))
			{
				return true;
			}
		}
		return false;
	}

	@Override
	public void dispose()
	{
	}
}
