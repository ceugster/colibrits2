package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.jdom.Element;

public class ConnectionLabelProvider extends LabelProvider implements IBaseLabelProvider
{
	@Override
	public String getText(final Object element)
	{
		if (element instanceof Element)
		{
			final Element connection = (Element) element;
			return connection.getText();
		}
		return "";
	}
}
