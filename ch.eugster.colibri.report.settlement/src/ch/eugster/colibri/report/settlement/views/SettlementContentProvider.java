package ch.eugster.colibri.report.settlement.views;

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
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
	{
	}

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof Settlement[])
		{
			return (Object[]) inputElement;
		}
		else if (inputElement instanceof Collection)
		{
			return ((Collection<?>) inputElement).toArray(new Object[0]);
		}
		return new Object[0];
	}

}
