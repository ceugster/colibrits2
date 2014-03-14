/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.actions.ProfileAction;

public class BackFromSettleAction extends ProfileAction
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Zurück";

	public static final String ACTION_COMMAND = "back.action";

	public BackFromSettleAction(final Profile profile)
	{
		super(BackFromSettleAction.TEXT, BackFromSettleAction.ACTION_COMMAND, profile);
	}

	@Override
	public void actionPerformed(ActionEvent event) 
	{
		super.actionPerformed(event);
	}
}
