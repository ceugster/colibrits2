package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Salespoint;

public class SalespointSorter extends ViewerSorter
{

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2)
	{
		if ((e1 instanceof Salespoint) && (e2 instanceof Salespoint))
		{
			final Salespoint s1 = (Salespoint) e1;
			final Salespoint s2 = (Salespoint) e2;

			return s1.getName().compareTo(s2.getName());
		}
		return 0;
	}

}
