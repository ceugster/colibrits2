/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.actions.ProfileAction;

public abstract class UserPanelProfileAction extends ProfileAction implements StateChangeListener, PropertyChangeListener
{
	public static final long serialVersionUID = 0l;

	protected UserPanel userPanel;

	public UserPanelProfileAction(final String text, final String actionCommand, final UserPanel userPanel, final Profile profile)
	{
		super(text, actionCommand, profile);
		this.userPanel = userPanel;
		this.userPanel.addStateChangeListener(this);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{

	}

	@Override
	public final void stateChange(final StateChangeEvent event)
	{
		if (event.getNewState().equals(UserPanel.State.LOCKED))
		{
			setEnabled(false);
		}
		else
		{
			setEnabled(getState(event));
		}

		firePropertyChange("state", event.getOldState(), event.getNewState());
	}

	protected boolean getState(final StateChangeEvent event)
	{
		return true;
	}
}
