/*
 * Created on 11.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.receipts;

import javax.swing.DefaultListSelectionModel;

import ch.eugster.colibri.persistence.model.Receipt;

public class CurrentReceiptListSelectionModel extends DefaultListSelectionModel
{
	public static final long serialVersionUID = 0l;

	private CurrentReceiptListModel model;

	public CurrentReceiptListSelectionModel(final CurrentReceiptListModel model)
	{
		this.model = model;
	}

	public CurrentReceiptListModel getListModel()
	{
		return model;
	}

	public Receipt getSelectedReceipt()
	{
		return model.getReceipt(getMinSelectionIndex());
	}
}
