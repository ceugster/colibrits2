/*
 * Created on 30.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.PaymentType;

public class DrawerSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		if ((element1 instanceof PaymentType) && (element2 instanceof PaymentType))
		{
			final PaymentType pt1 = (PaymentType) element1;
			final PaymentType pt2 = (PaymentType) element2;

			return pt1.getName().compareTo(pt2.getName());
		}
		return 0;
	}
}
