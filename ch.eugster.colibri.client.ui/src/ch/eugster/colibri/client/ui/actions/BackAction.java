/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.actions.ProfileAction;

public class BackAction extends ProfileAction
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Zurück";

	public static final String ACTION_COMMAND = "back.action";

	public BackAction(final Profile profile)
	{
		super(BackAction.TEXT, BackAction.ACTION_COMMAND, profile);
	}
}
