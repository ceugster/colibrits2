package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;

import ch.eugster.colibri.persistence.model.User;

public class UserLabelProvider extends LabelProvider implements IBaseLabelProvider
{
	@Override
	public String getText(final Object element)
	{
		if (element instanceof User)
		{
			return ((User) element).getUsername();
		}
		return super.getText(element);
	}

}
