/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.ui.actions;

import ch.eugster.colibri.persistence.model.Profile;

public abstract class ProfileAction extends BasicAction
{
	public static final long serialVersionUID = 0l;

	protected Profile profile;

	public ProfileAction(final String text, final String actionCommand, final Profile profile)
	{
		super(text, actionCommand);
		this.profile = profile;
	}

}
