package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.PaymentType;

public class PaymentTypeSorter extends ViewerSorter
{

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2)
	{
		if ((e1 instanceof PaymentType) && (e2 instanceof PaymentType))
		{
			final PaymentType pt1 = (PaymentType) e1;
			final PaymentType pt2 = (PaymentType) e2;

			return pt1.getName().compareTo(pt2.getName());
		}
		return super.compare(viewer, e1, e2);
	}

}
