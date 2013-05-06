package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.PaymentType;

public class PaymentTypeContentProvider implements IContentProvider,
		IStructuredContentProvider {

	@Override
	public Object[] getElements(Object inputElement)
	{
		if (inputElement instanceof PaymentType[])
		{
			return (PaymentType[]) inputElement;
		}
		return new PaymentType[0];
	}

	@Override
	public void dispose() 
	{
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
