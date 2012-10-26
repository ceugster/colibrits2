/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.pos.db.Salespoint;

public class SalespointSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final Salespoint salespoint1 = (Salespoint) element1;
		final Salespoint salespoint2 = (Salespoint) element2;

		if ((salespoint1 != null) && (salespoint2 != null))
		{
			if ((salespoint1.name != null) && (salespoint2.name != null))
			{
				return salespoint1.name.compareTo(salespoint2.name);
			}
			else if (salespoint1.name == null)
			{
				return -1;
			}
			else if (salespoint2.name == null)
			{
				return 1;
			}
			else
			{
				return 0;
			}
		}
		else if (salespoint1 == null)
		{
			return -1;
		}
		else if (salespoint2 == null)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
