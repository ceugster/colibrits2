package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.LabelProvider;

public class DriverLabelProvider extends LabelProvider
{
	@Override
	public String getText(final Object element)
	{
		return ((SupportedDriver) element).getPlatform();
	}

}
