package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.Salespoint;

public class SalespointContentProvider implements IStructuredContentProvider
{

	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		if (inputElement instanceof Salespoint[])
		{
			return (Salespoint[]) inputElement;
		}
		return new Salespoint[0];
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

}
