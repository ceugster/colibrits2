/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Position;

public class OptionSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final Position.Option o1 = (Position.Option) element1;
		final Position.Option o2 = (Position.Option) element2;

		if ((o1 != null) && (o2 != null))
		{
			return o1.toString().compareTo(o2.toString());
		}
		else if (o1 == null)
		{
			return -1;
		}
		else if (o2 == null)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
