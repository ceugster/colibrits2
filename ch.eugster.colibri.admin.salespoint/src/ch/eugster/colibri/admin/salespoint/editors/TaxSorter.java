/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Tax;

public class TaxSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final Tax tax1 = (Tax) element1;
		final Tax tax2 = (Tax) element2;

		if ((tax1 != null) && (tax2 != null))
		{
			return tax1.format().compareTo(tax2.format());
		}
		else if (tax1 == null)
		{
			return -1;
		}
		else if (tax2 == null)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
