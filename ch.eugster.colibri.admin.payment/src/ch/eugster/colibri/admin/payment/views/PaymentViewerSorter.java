/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.payment.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;

public class PaymentViewerSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		if ((element1 instanceof PaymentType) && (element2 instanceof PaymentType))
		{
			final PaymentType pt1 = (PaymentType) element1;
			final PaymentType pt2 = (PaymentType) element2;

			return pt1.getCode().compareTo(pt2.getCode());
		}
		else if ((element1 instanceof Money) && (element2 instanceof Money))
		{
			final Money m1 = (Money) element1;
			final Money m2 = (Money) element2;

			if (m1.getValue() > m2.getValue())
			{
				return 1;
			}
			else if (m1.getValue() < m2.getValue())
			{
				return -1;
			}
			else
			{
				return 0;
			}
		}
		else
		{
			return 0;
		}
	}
}
