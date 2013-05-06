package ch.eugster.colibri.report.receipt.views;

import java.math.BigDecimal;
import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Receipt;

public class AmountViewerFilter extends ViewerFilter
{
	private Double filteredAmount;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		boolean show = true;
		if (filteredAmount != null)
		{
			if (filteredAmount.doubleValue() != 0D)
			{
				if (element instanceof Receipt)
				{
					Receipt receipt = (Receipt) element;
					boolean found = false;
					Collection<Payment> payments = receipt.getPayments();
					for (Payment payment : payments)
					{
						double amount1 = new BigDecimal(Double.valueOf(payment.getAmount()).toString()).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
						double amount2 = new BigDecimal(Double.valueOf(payment.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY)).toString()).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
						if (filteredAmount.doubleValue() == amount1 || filteredAmount == amount2)
						{
							found = true;
							break;
						}
					}
					show = found;
				}
				else if (element instanceof Payment)
				{
					Payment payment = (Payment) element;
					double amount1 = new BigDecimal(Double.valueOf(payment.getAmount()).toString()).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
					double amount2 = new BigDecimal(Double.valueOf(payment.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY)).toString()).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
					return filteredAmount.doubleValue() == amount1 || filteredAmount == amount2;
				}
			}
		}
		return show;
	}
	
	public void setAmount(Double amount)
	{
		filteredAmount = amount;
	}

}
