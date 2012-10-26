package ch.eugster.colibri.admin.ui.editors;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public abstract class TableViewerSorter extends ViewerSorter implements SelectionListener
{
	protected Viewer viewer;
	
	protected boolean ascending = false;
	protected int columnIndex = 0;
	
	public TableViewerSorter(Viewer viewer)
	{
		this.viewer = viewer;
	}
	
	public TableViewerSorter(Viewer viewer, int columnIndex)
	{
		this(viewer);
		this.columnIndex = columnIndex;
	}
	
	public void setColumnIndex(int columnIndex)
	{
		if (this.columnIndex == columnIndex)
			this.ascending = !this.ascending;
		else
			this.columnIndex = columnIndex;
	}
	
	public int getColumnIndex()
	{
		return this.columnIndex;
	}
	
	public void widgetSelected(SelectionEvent event)
	{
		event.getSource();
		
	}
	
	public void widgetDefaultSelected(SelectionEvent event)
	{
		event.getSource();
	}
}
