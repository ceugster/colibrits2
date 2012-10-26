/*
 * Created on 16.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.login;

import ch.eugster.colibri.persistence.model.User;

public class LoginEvent
{
	private User user;

	public LoginEvent(final User user)
	{
		this.user = user;
	}

	public User getUser()
	{
		return user;
	}
}
