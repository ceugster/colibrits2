/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.ui.Activator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractEntityEditorInput<T extends AbstractEntity> implements IEditorInput
{
	protected T entity;

	@SuppressWarnings("unchecked")
	public AbstractEntityEditorInput(T entity)
	{
		if (entity.getId() == null)
		{
			this.entity = entity;
		}
		else
		{
			ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
					PersistenceService.class, null);
			tracker.open();
			PersistenceService persistenceService = (PersistenceService) tracker.getService();
			if (persistenceService != null)
			{
				this.entity = (T) persistenceService.getServerService().refresh(entity);
			}
			tracker.close();
		}
	}

	@Override
	public boolean equals(final Object other)
	{
		if (other instanceof AbstractEntityEditorInput)
		{
			final AbstractEntityEditorInput<?> input = (AbstractEntityEditorInput<?>) other;
			final AbstractEntity otherEntity = (AbstractEntity) input.getAdapter(input.getEntity().getClass());
			if ((otherEntity.getId() != null) && (this.entity.getId() != null))
			{
				return otherEntity.getId().equals(this.entity.getId());
			}
		}
		return false;
	}

	@Override
	public boolean exists()
	{
		return false;
	}

	public T getEntity()
	{
		return this.entity;
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	public void setParent(AbstractEntity parent)
	{

	}

	public AbstractEntity getParent()
	{
		return null;
	}

	@Override
	public IPersistableElement getPersistable()
	{
		return null;
	}

	public abstract boolean hasParent();

	public void setEntity(final T entity)
	{
		this.entity = entity;
	}
}
