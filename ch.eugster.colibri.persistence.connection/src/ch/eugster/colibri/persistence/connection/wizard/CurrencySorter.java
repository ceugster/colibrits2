/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

public class CurrencySorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final String[] cur1 = (String[]) element1;
		final String[] cur2 = (String[]) element2;

		if ((cur1 != null) && (cur2 != null))
		{
			return cur1[4].compareTo(cur2[4]);
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
