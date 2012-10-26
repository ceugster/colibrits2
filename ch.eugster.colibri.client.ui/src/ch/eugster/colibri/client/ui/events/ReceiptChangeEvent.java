/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;

import ch.eugster.colibri.persistence.model.Receipt;

public class ReceiptChangeEvent
{
	private Receipt oldReceipt;

	private Receipt newReceipt;

	public ReceiptChangeEvent(final Receipt oldReceipt, final Receipt newReceipt)
	{
		this.oldReceipt = oldReceipt;
		this.newReceipt = newReceipt;
	}

	public Receipt getNewReceipt()
	{
		return newReceipt;
	}

	public Receipt getOldReceipt()
	{
		return oldReceipt;
	}
}
