/*
 * Created on 13.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;

import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.key.FunctionType;

public class KeyDragSourceListener implements DragSourceListener
{
	private TabEditor tabEditor;

	public KeyDragSourceListener(final TabEditor tabEditor)
	{
		super();
		this.tabEditor = tabEditor;
	}

	public void dragFinished(final DragSourceEvent event)
	{
	}

	public void dragSetData(final DragSourceEvent event)
	{
		Object object = null;

		final StructuredSelection ssel = (StructuredSelection) tabEditor.getKeyViewer().getSelection();
		if (ssel.getFirstElement() instanceof ProductGroup)
		{
			object = ssel.getFirstElement();
		}
		else if (ssel.getFirstElement() instanceof PaymentType)
		{
			object = ssel.getFirstElement();
		}
		else if (ssel.getFirstElement() instanceof TaxRate)
		{
			object = ssel.getFirstElement();
		}
		else if (ssel.getFirstElement() instanceof Position.Option)
		{
			object = ssel.getFirstElement();
		}
		else if (ssel.getFirstElement() instanceof FunctionType)
		{
			object = ssel.getFirstElement();
		}

		event.data = object;
		KeyTransfer.getInstance().setTabEditor(tabEditor);
		KeyTransfer.getInstance().setSource(object);
	}

	public void dragStart(final DragSourceEvent event)
	{
		Object object = null;
		final StructuredSelection ssel = (StructuredSelection) tabEditor.getKeyViewer().getSelection();
		if (ssel.getFirstElement() instanceof ProductGroup)
		{
			object = ssel.getFirstElement();
		}
		else if (ssel.getFirstElement() instanceof PaymentType)
		{
			object = ssel.getFirstElement();
		}
		else if (ssel.getFirstElement() instanceof TaxRate)
		{
			object = ssel.getFirstElement();
		}
		else if (ssel.getFirstElement() instanceof Position.Option)
		{
			object = ssel.getFirstElement();
		}
		else if (ssel.getFirstElement() instanceof FunctionType)
		{
			object = ssel.getFirstElement();
		}
		else
		{
			event.doit = false;
			return;
		}

		event.data = object;
		KeyTransfer.getInstance().setTabEditor(tabEditor);
		KeyTransfer.getInstance().setSource(object);
	}
}
