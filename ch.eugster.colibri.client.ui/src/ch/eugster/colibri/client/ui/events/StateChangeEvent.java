/*
 * Created on 2009 3 6
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.UserPanel.State;

public class StateChangeEvent
{
	private UserPanel.State oldState;
	private UserPanel.State newState;
	
	public StateChangeEvent(UserPanel.State oldState, UserPanel.State newState)
	{
		this.oldState = oldState;
		this.newState = newState;
	}
	
	public State getOldState()
	{
		return this.oldState;
	}
	
	public State getNewState()
	{
		return this.newState;
	}
	
}
