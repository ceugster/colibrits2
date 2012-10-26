package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import ch.eugster.colibri.persistence.model.Salespoint;

public class SalespointLabelProvider extends LabelProvider implements IBaseLabelProvider
{

	@Override
	public String getText(final Object element)
	{
		if (element instanceof Salespoint)
		{
			return ((Salespoint) element).getName();
		}
		return super.getText(element);
	}

}
