package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;

public class PositionContentProvider implements IStructuredContentProvider
{

	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		if (inputElement instanceof Receipt)
		{
			return ((Receipt) inputElement).getPositions().toArray(new Position[0]);
		}
		return new Position[0];
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

}
