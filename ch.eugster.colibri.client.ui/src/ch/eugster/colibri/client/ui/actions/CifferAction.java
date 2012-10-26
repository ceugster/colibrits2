/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;

public final class CifferAction extends NumericPadAction
{
	public static final long serialVersionUID = 0l;

	public CifferAction(final String text, final UserPanel userPanel)
	{
		super(text, text, userPanel.getProfile());
	}
}
