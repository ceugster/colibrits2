package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.jdom.Element;

public class ConnectionSorter extends ViewerSorter
{

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2)
	{
		final Element element1 = (Element) e1;
		final Element element2 = (Element) e2;

		return element1.getText().compareTo(element2.getText());
	}

}
