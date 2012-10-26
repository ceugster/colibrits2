/*
 * Created on 21.05.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.swt.dialogs;

import ch.eugster.colibri.persistence.model.PaymentType;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PaymentTypeSelectionDialogInput
{

	private PaymentType paymentType;

	public PaymentTypeSelectionDialogInput(final PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}

	public PaymentType getPaymentType()
	{
		return paymentType;
	}

	public void setPaymentType(final PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}
}
