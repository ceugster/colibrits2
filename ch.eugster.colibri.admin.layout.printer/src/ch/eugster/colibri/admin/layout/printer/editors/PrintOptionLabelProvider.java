package ch.eugster.colibri.admin.layout.printer.editors;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;

public class PrintOptionLabelProvider extends LabelProvider implements IBaseLabelProvider
{

	@Override
	public String getText(final Object element)
	{
		if (element instanceof PrintOption)
		{
			return ((PrintOption) element).label();
		}
		return "";
	}

}
