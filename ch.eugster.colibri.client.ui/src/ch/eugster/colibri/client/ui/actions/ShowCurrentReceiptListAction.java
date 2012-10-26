/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;

public class ShowCurrentReceiptListAction extends ConfigurableAction
{
	private static final long serialVersionUID = 0l;

	public ShowCurrentReceiptListAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.RECEIPTS_LIST));
	}

}
