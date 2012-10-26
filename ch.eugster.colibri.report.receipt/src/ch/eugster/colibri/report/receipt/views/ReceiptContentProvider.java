package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.Receipt;

public class ReceiptContentProvider implements IStructuredContentProvider
{

	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		if (inputElement instanceof Object[])
		{
			return (Object[]) inputElement;
		}
		return new Receipt[0];
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

}
