package ch.eugster.colibri.report.receipt.views;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Receipt;

public class PaymentTypeViewerFilter extends ViewerFilter
{
	private PaymentType filteredPaymentType;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		boolean show = true;
		if (filteredPaymentType != null)
		{
			if (filteredPaymentType.getId() != null)
			{
				Receipt receipt = null;
				if (element instanceof Receipt)
				{
					receipt = (Receipt) element;
					boolean found = false;
					Collection<Payment> payments = receipt.getPayments();
					for (Payment payment : payments)
					{
						if (payment.getPaymentType().getId().equals(filteredPaymentType.getId()))
						{
							found = true;
						}
					}
					show = found;
				}
				else if (element instanceof Payment)
				{
					Payment payment = (Payment) element;
					return payment.getPaymentType().getId().equals(filteredPaymentType.getId());
				}
			}
		}
		return show;
	}

	public void setPaymentType(PaymentType paymentType)
	{
		filteredPaymentType = paymentType;
	}

}
