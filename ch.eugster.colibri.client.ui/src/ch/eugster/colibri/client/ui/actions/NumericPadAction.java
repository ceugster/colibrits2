/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.actions.ProfileAction;

public abstract class NumericPadAction extends ProfileAction
{
	private static final long serialVersionUID = 0l;

	public NumericPadAction(final String text, final String actionCommand, final Profile profile)
	{
		super(text, actionCommand, profile);
	}
}
