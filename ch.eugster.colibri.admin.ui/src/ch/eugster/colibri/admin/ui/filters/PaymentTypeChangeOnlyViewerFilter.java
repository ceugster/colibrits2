package ch.eugster.colibri.admin.ui.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.PaymentType;

public class PaymentTypeChangeOnlyViewerFilter extends ViewerFilter
{

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element)
	{
		if (element instanceof PaymentType)
		{
			final PaymentType paymentType = (PaymentType) element;
			return paymentType.isChange();
		}
		return true;
	}

}
