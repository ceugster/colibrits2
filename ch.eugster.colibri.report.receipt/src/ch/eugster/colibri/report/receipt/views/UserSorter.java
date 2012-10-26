package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.User;

public class UserSorter extends ViewerSorter
{

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2)
	{
		if ((e1 instanceof User) && (e2 instanceof User))
		{
			final User u1 = (User) e1;
			final User u2 = (User) e2;

			return u1.getUsername().compareTo(u2.getUsername());
		}
		return super.compare(viewer, e1, e2);
	}

}
