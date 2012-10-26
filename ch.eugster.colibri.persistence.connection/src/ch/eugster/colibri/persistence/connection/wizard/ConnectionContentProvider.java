package ch.eugster.colibri.persistence.connection.wizard;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.jdom.Document;
import org.jdom.Element;

public class ConnectionContentProvider implements IContentProvider, IStructuredContentProvider
{

	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getElements(final Object inputElement)
	{
		if (inputElement instanceof Document)
		{
			final Document document = (Document) inputElement;
			final Element root = document.getRootElement();

			@SuppressWarnings("unchecked")
			final List<Element> connections = root.getChildren("connection");
			final Element[] elements = new Element[connections.size() + 1];
			elements[0] = root.getChild("current").getChild("connection");

			final Iterator<Element> iterator = connections.iterator();
			for (int i = 1; iterator.hasNext(); i++)
			{
				elements[i] = iterator.next();
			}
			return elements;
		}
		return new Element[0];
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}
}
