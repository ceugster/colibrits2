/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Profile;

public class ProfileSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final Profile p1 = (Profile) element1;
		final Profile p2 = (Profile) element2;

		if ((p1 != null) && (p2 != null))
		{
			return p1.getName().compareTo(p2.getName());
		}
		else if (p1 == null)
		{
			return -1;
		}
		else if (p2 == null)
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
}
