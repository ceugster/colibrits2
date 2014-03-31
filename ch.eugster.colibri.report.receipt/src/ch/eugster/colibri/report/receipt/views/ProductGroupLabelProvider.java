package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import ch.eugster.colibri.persistence.model.ProductGroup;

public class ProductGroupLabelProvider extends LabelProvider implements IBaseLabelProvider
{
	@Override
	public String getText(final Object element)
	{
		if (element instanceof ProductGroup)
		{
			return ((ProductGroup) element).getName();
		}
		return super.getText(element);
	}

}
