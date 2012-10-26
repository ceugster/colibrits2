package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.User;

public class UserContentProvider implements IStructuredContentProvider
{

	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub

	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		if (inputElement instanceof User[])
		{
			return (User[]) inputElement;
		}
		return new User[0];
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}
}
