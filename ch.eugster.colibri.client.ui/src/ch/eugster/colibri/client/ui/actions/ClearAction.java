/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import ch.eugster.colibri.persistence.model.Profile;

public class ClearAction extends NumericPadAction
{
	public static final long serialVersionUID = 0l;

	public static final String TEXT = "Leeren";

	public static final String ACTION_COMMAND = "clear.action";

	public ClearAction(final Profile profile)
	{
		super(ClearAction.TEXT, ClearAction.ACTION_COMMAND, profile);
	}
}
