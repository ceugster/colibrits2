/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.CurrentTax;

public class CurrentTaxSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final CurrentTax ct1 = (CurrentTax) element1;
		final CurrentTax ct2 = (CurrentTax) element2;

		if ((ct1 != null) && (ct2 != null))
		{
			return ct2.getValidFrom().compareTo(ct1.getValidFrom());
		}
		else if (ct1 == null)
		{
			return -1;
		}
		else if (ct2 == null)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
