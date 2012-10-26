package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import ch.eugster.colibri.persistence.model.Salespoint;

public class HostEditingSupport extends EditingSupport
{
	private CellEditor hostEditor;

	public HostEditingSupport(final TableViewer viewer)
	{
		super(viewer);
		this.hostEditor = new TextCellEditor(viewer.getTable());
	}

	@Override
	protected boolean canEdit(final Object element)
	{
		return true;
	}

	@Override
	protected CellEditor getCellEditor(final Object element)
	{
		return this.hostEditor;
	}

	@Override
	protected Object getValue(final Object element)
	{
		if (element instanceof Salespoint)
		{
			return ((Salespoint) element).getHost();
		}
		return null;
	}

	@Override
	protected void setValue(final Object element, final Object value)
	{
		if (element instanceof Salespoint)
		{
			((Salespoint) element).setHost(value.toString());
		}
		this.getViewer().update(element, null);
	}

}
