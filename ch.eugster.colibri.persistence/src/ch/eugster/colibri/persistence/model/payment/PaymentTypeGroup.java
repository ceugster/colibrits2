/*
 * Created on 14.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model.payment;

import java.util.ArrayList;
import java.util.Collection;

import ch.eugster.colibri.persistence.model.PaymentType;

public enum PaymentTypeGroup implements IPaymentTypeGroup
{
	CASH, VOUCHER, CREDIT, DEBIT;

	private static final String CASH_NAME = "Bargeld";

	private static final String VOUCHER_NAME = "Gutscheine";

	private static final String CREDIT_NAME = "Kreditkarten";

	private static final String DEBIT_NAME = "Debitkarten";

	private Collection<PaymentType> paymentTypes = new ArrayList<PaymentType>();
	
	public String getImageName()
	{
		if (this.equals(CASH))
		{
			return "money.png";
		}
		else if (this.equals(VOUCHER))
		{
			return "edit-content.png";
		}
		else if (this.equals(CREDIT))
		{
			return "creditcard_16.png";
		}
		else if (this.equals(DEBIT))
		{
			return "creditcard_16.png";
		}
		else
		{
			throw new RuntimeException("Invalid PaymentTypeGroup");
		}
	}

	public boolean isChargable()
	{
		if (this.equals(CREDIT))
		{
			return true;
		}
		else if (this.equals(DEBIT))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public boolean isAsChangeAvailable()
	{
		if (this.equals(CASH))
		{
			return true;
		}
		else if (this.equals(VOUCHER))
		{
			return true;
		}
		else if (this.equals(CREDIT))
		{
			return false;
		}
		else if (this.equals(DEBIT))
		{
			return false;
		}
		else
		{
			throw new RuntimeException("Invalid PaymentTypeGroup");
		}
	}

	@Override
	public String toString()
	{
		if (this.equals(CASH))
		{
			return PaymentTypeGroup.CASH_NAME;
		}
		else if (this.equals(VOUCHER))
		{
			return PaymentTypeGroup.VOUCHER_NAME;
		}
		else if (this.equals(CREDIT))
		{
			return PaymentTypeGroup.CREDIT_NAME;
		}
		else if (this.equals(DEBIT))
		{
			return PaymentTypeGroup.DEBIT_NAME;
		}
		else
		{
			throw new RuntimeException("Invalid PaymentTypeGroup");
		}
	}
	
	public void setPaymentTypes(Collection<PaymentType> paymentTypes)
	{
		this.paymentTypes = paymentTypes;
	}
	
	public void addPaymentType(PaymentType paymentType)
	{
		paymentTypes.add(paymentType);
	}
	
	public Collection<PaymentType> getPaymentTypes()
	{
		return paymentTypes;
	}
}
