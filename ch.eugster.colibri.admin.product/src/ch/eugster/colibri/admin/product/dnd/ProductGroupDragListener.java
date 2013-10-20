package ch.eugster.colibri.admin.product.dnd;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import ch.eugster.colibri.persistence.model.ProductGroup;

public class ProductGroupDragListener implements DragSourceListener 
{
	private TreeViewer viewer;
	
	public ProductGroupDragListener(TreeViewer viewer)
	{
		this.viewer = viewer;
	}

	@Override
	public void dragStart(DragSourceEvent event) 
	{
		if (event.getSource() instanceof DragSource)
		{
			DragSource source = (DragSource) event.getSource();
			if (source.getControl().equals(viewer.getTree()))
			{
				dragSetData(event);
			}
		}
	}

	@Override
	public void dragSetData(DragSourceEvent event) 
	{
		Collection<ProductGroup> groups = new ArrayList<ProductGroup>();
		if (event.getSource() instanceof DragSource)
		{
			DragSource source = (DragSource) event.getSource();
			if (source.getControl().equals(viewer.getTree()))
			{
				StructuredSelection ssel = (StructuredSelection) viewer.getSelection();
				Object[] elements = (Object[]) ssel.toArray();
				for (Object element : elements)
				{
					if (element instanceof ProductGroup)
					{
						groups.add((ProductGroup) element);
					}
				}
			}
		}
		ProductGroupTransfer.getTransfer().setData(DND.DROP_MOVE, groups.toArray(new ProductGroup[0]));
	}

	@Override
	public void dragFinished(DragSourceEvent event) 
	{
	}

}
