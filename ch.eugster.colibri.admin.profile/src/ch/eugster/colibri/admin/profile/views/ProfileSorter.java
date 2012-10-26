/*
 * Created on 19.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Tab;

public class ProfileSorter extends ViewerSorter
{
	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		if ((element1 instanceof Configurable) && (element2 instanceof Configurable))
		{
			final Configurable c1 = (Configurable) element1;
			final Configurable c2 = (Configurable) element2;

			return c1.getType().compareTo(c2.getType());
		}
		else if ((element1 instanceof Tab) && (element2 instanceof Tab))
		{
			final Tab tab1 = (Tab) element1;
			final Tab tab2 = (Tab) element2;
			return tab1.getPos() - tab2.getPos();
		}
		return 0;
	}
}
