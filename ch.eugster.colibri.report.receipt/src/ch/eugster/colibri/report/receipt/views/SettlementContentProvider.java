package ch.eugster.colibri.report.receipt.views;

import java.util.Collection;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementContentProvider implements IStructuredContentProvider
{

	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		if (inputElement instanceof Collection)
		{
			@SuppressWarnings("unchecked")
			final Collection<Object> objects = (Collection<Object>) inputElement;
			return objects.toArray(new Object[0]);
		}
		else if (inputElement instanceof Settlement[])
		{
			return (Settlement[]) inputElement;
		}
		return new Settlement[0];
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

}
