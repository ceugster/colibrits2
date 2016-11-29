/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.buttons;

import ch.eugster.colibri.persistence.model.Profile;

public class DialogButton extends ProfileButton
{
	public static final long serialVersionUID = 0l;

	public DialogButton(final String text, final Profile profile, boolean isFailOver)
	{
		super(profile, isFailOver);
		setText(text);
		update(isFailOver);
	}
}
