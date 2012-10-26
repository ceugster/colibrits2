/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.currency.wizards;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Currency;

public class CurrencySorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final Currency cur1 = (Currency) element1;
		final Currency cur2 = (Currency) element2;

		if ((cur1 != null) && (cur2 != null))
		{
			return cur1.format().compareTo(cur2.format());
		}
		else if (cur1 == null)
		{
			return -1;
		}
		else if (cur2 == null)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
