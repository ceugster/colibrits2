package ch.eugster.colibri.admin.ui.filters;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.PaymentType;

public class UsedCurrenciesOnlyViewerFilter extends ViewerFilter
{

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element)
	{
		if (element instanceof Currency)
		{
			final Currency currency = (Currency) element;
			Collection<PaymentType> paymentTypes = currency.getPaymentTypes();
			for (PaymentType paymentType : paymentTypes)
			{
				if (paymentType.isOpenCashdrawer())
				{
					return true;
				}
			}
			return false;
		}
		return true;
	}

}
