/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Receipt;

public class ReceiptParkingAction extends ConfigurableAction
{
	public static final long serialVersionUID = 0l;

	public ReceiptParkingAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (userPanel.getReceiptWrapper().isReceiptEmpty())
		{
			userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.PARKED_RECEIPTS_LIST));
		}
		else
		{
			userPanel.getReceiptWrapper().parkReceipt();
			Receipt receipt = userPanel.getReceiptWrapper().prepareReceipt();
			userPanel.getPositionWrapper().preparePosition(receipt);
			userPanel.getPaymentWrapper().preparePayment(receipt);
			userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.PAYMENT_INPUT));
			userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.POSITION_INPUT));
		}
	}
}
