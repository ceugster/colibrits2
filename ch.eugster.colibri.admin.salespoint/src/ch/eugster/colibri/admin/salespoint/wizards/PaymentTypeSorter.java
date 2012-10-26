/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.wizards;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.PaymentType;

public class PaymentTypeSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final PaymentType pt1 = (PaymentType) element1;
		final PaymentType pt2 = (PaymentType) element2;

		if ((pt1 != null) && (pt2 != null))
		{
			return pt1.getCurrency().format().compareTo(pt2.getCurrency().format());
		}
		else if (pt1 == null)
		{
			return -1;
		}
		else if (pt2 == null)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
