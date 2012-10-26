/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import ch.eugster.colibri.persistence.model.Profile;

public class DeleteAction extends NumericPadAction
{
	public static final long serialVersionUID = 0l;

	public static final String TEXT = "Löschen";

	public static final String ACTION_COMMAND = "delete.action";

	public DeleteAction(final Profile profile)
	{
		super(DeleteAction.TEXT, DeleteAction.ACTION_COMMAND, profile);
	}
}
