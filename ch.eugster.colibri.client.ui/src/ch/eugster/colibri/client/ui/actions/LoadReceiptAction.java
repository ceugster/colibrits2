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
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListModel;
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListSelectionModel;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;

public class LoadReceiptAction extends UserPanelProfileAction implements ListSelectionListener, TableModelListener
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Laden";

	public static final String ACTION_COMMAND = "load.current.receipt.action";

	private CurrentReceiptListModel tableModel;

	private CurrentReceiptListSelectionModel selectionModel;

	public LoadReceiptAction(final UserPanel userPanel, final Profile profile, final CurrentReceiptListModel tableModel,
			final CurrentReceiptListSelectionModel selectionModel)
	{
		super(LoadReceiptAction.TEXT, LoadReceiptAction.ACTION_COMMAND, userPanel, profile);
		this.tableModel = tableModel;
		this.selectionModel = selectionModel;
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		Receipt receipt = userPanel.getReceiptWrapper().replaceReceipt(selectionModel.getSelectedReceipt());
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
