package ch.eugster.colibri.report.export.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementViewerContentProvider implements
		IStructuredContentProvider {

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
		if (Settlement[].class.isInstance(inputElement))
		{
			return (Object[]) inputElement;
		}
		return new Settlement[0];
	}

}
