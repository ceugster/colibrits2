/*
 * Created on 11.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.payment;

import javax.swing.DefaultListSelectionModel;

public class PaymentListSelectionModel extends DefaultListSelectionModel
{
	public static final long serialVersionUID = 0l;
	
	private PaymentListModel model;
	
	public PaymentListSelectionModel(PaymentListModel model)
	{
		this.model = model;
	}
	
	public PaymentListModel getListModel()
	{
		return this.model;
	}
}
