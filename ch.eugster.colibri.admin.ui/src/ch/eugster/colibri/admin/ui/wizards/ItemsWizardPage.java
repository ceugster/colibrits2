package ch.eugster.colibri.admin.ui.wizards;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class ItemsWizardPage<T extends AbstractEntity> extends WizardPage implements ISelectionChangedListener, IDoubleClickListener,
		EntityListener
{

	private ArrayList<ISelectionChangedListener> selectionChangedListeners = new ArrayList<ISelectionChangedListener>();

	private ArrayList<IDoubleClickListener> doubleClickListeners = new ArrayList<IDoubleClickListener>();

	public ItemsWizardPage(final String name, final String title, final ImageDescriptor image)
	{
		super(name, title, image);
	}

	public void addDoubleClickListener(final IDoubleClickListener listener)
	{
		if (!this.doubleClickListeners.contains(listener))
		{
			this.doubleClickListeners.add(listener);
		}
	}

	public void addSelectionChangedListener(final ISelectionChangedListener listener)
	{
		if (!this.selectionChangedListeners.contains(listener))
		{
			this.selectionChangedListeners.add(listener);
		}
	}

	public abstract boolean canDelete();

	public void fireDoubleClickEvent(final DoubleClickEvent event)
	{
		final Iterator<IDoubleClickListener> iterator = this.doubleClickListeners.iterator();
		while (iterator.hasNext())
		{
			iterator.next().doubleClick(event);
		}
	}

	public void fireSelectionChangedEvent(final SelectionChangedEvent event)
	{
		final Iterator<ISelectionChangedListener> iterator = this.selectionChangedListeners.iterator();
		while (iterator.hasNext())
		{
			iterator.next().selectionChanged(event);
		}
	}

	public abstract T[] getInput();

	public abstract T getNewEntity();

	public abstract ISelection getSelection();

	public abstract boolean isEmptySelection();

	@Override
	public void postDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void postLoad(final AbstractEntity entity)
	{
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
	}

	@Override
	public void postRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
	}

	@Override
	public void preDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void prePersist(final AbstractEntity entity)
	{
	}

	@Override
	public void preRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void preUpdate(final AbstractEntity entity)
	{
	}

	public void removeDoubleClickListener(final IDoubleClickListener listener)
	{
		if (this.doubleClickListeners.contains(listener))
		{
			this.doubleClickListeners.remove(listener);
		}
	}

	public void removeSelectionChangedListener(final ISelectionChangedListener listener)
	{
		if (this.selectionChangedListeners.contains(listener))
		{
			this.selectionChangedListeners.remove(listener);
		}
	}

	public abstract void setSelection(ISelection selection);

	protected Composite doCreateControl(final Composite parent)
	{
		return parent;
	}

}
