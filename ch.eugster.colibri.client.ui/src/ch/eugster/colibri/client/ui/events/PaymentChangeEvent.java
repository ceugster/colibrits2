/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;

import ch.eugster.colibri.persistence.model.Payment;

public class PaymentChangeEvent
{
	private Payment oldPayment;

	private Payment newPayment;

	public PaymentChangeEvent(final Payment oldPayment, final Payment newPayment)
	{
		this.oldPayment = oldPayment;
		this.newPayment = newPayment;
	}

	public Payment getNewPayment()
	{
		return newPayment;
	}

	public Payment getOldPayment()
	{
		return oldPayment;
	}
}
