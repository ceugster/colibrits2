/*

 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.parking.ParkedReceiptListModel;
import ch.eugster.colibri.client.ui.panels.user.parking.ParkedReceiptListSelectionModel;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;

public class LoadParkedReceiptAction extends UserPanelProfileAction implements ListSelectionListener, TableModelListener
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Holen";

	public static final String ACTION_COMMAND = "load.parked.receipt.action";

	private ParkedReceiptListModel tableModel;

	private ParkedReceiptListSelectionModel selectionModel;

	public LoadParkedReceiptAction(final UserPanel userPanel, final Profile profile, final ParkedReceiptListModel tableModel,
			final ParkedReceiptListSelectionModel selectionModel)
	{
		super(LoadParkedReceiptAction.TEXT, LoadParkedReceiptAction.ACTION_COMMAND, userPanel, profile);
		this.tableModel = tableModel;
		this.selectionModel = selectionModel;
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		Receipt receipt = selectionModel.getSelectedReceipt();
		receipt = userPanel.getReceiptWrapper().replaceReceipt(receipt);
		userPanel.getPositionWrapper().preparePosition(receipt);
		userPanel.getPaymentWrapper().preparePayment(receipt);
		userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.POSITION_INPUT));
	}

	@Override
	public void tableChanged(final TableModelEvent event)
	{
		setEnabled(shouldEnable());
	}

	@Override
	public void valueChanged(final ListSelectionEvent event)
	{
		setEnabled(shouldEnable());
	}

	private boolean shouldEnable()
	{
		if (tableModel.getRowCount() == 0)
		{
			return false;
		}
		else
		{
			return selectionModel.getMinSelectionIndex() > -1;
		}
	}
}
