package ch.eugster.colibri.report.salespoint.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.Salespoint;

public class SalespointViewerSorter extends ViewerSorter
{

	@Override
	public int compare(final Viewer viewer, final Object element1, final Object element2)
	{
		final Salespoint s1 = (Salespoint) element1;
		final Salespoint s2 = (Salespoint) element2;

		return s1.getName().compareTo(s2.getName());
	}

}
