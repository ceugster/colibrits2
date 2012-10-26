package ch.eugster.colibri.admin.ui.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;

import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class AbstractEntitySharedHeaderFormEditor<T extends AbstractEntity> extends SharedHeaderFormEditor implements IPropertyListener,
		EntityListener
{
	@Override
	public void doSave(final IProgressMonitor monitor)
	{
		// this.saveValues();
		// AbstractEntityEditorInput<T> input = (AbstractEntityEditorInput<T>)
		// this.getEditorInput();
		// T entity = input.getEntity();
		// input.setEntity(this.getEntityHelper().store(entity));
		// if (input.hasParent())
		// this.getEntityHelper().refresh(input.getParent());
		// this.editorDirtyStateChanged();
		// this.updateControls();
	}

	@Override
	public void doSaveAs()
	{
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
	{
		setInput(input);
		setSite(site);
		setPartName("");
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		// TODO Auto-generated method stub
		return false;
	}

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

}
