/*
 * Created on 11.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.parking;

import javax.swing.DefaultListSelectionModel;

import ch.eugster.colibri.persistence.model.Receipt;

public class ParkedReceiptListSelectionModel extends DefaultListSelectionModel
{
	public static final long serialVersionUID = 0l;

	private ParkedReceiptListModel model;

	public ParkedReceiptListSelectionModel(final ParkedReceiptListModel model)
	{
		this.model = model;
	}

	public ParkedReceiptListModel getListModel()
	{
		return model;
	}

	public Receipt getSelectedReceipt()
	{
		return model.getReceipt(getMinSelectionIndex());
	}
}
