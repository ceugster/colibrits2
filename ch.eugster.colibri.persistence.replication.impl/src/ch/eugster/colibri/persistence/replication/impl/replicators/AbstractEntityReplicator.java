/*
 * Created on 24.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractEntityReplicator<T extends AbstractEntity> implements EntityReplicator<T>
{
	protected PersistenceService persistenceService;

	public AbstractEntityReplicator(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	public abstract void replicate(IProgressMonitor monitor);

	protected abstract T replicate(T source);

	protected T replicate(final T source, final T target)
	{
		target.setId(source.getId());
		target.setDeleted(source.isDeleted());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getVersion());
		return target;
	}
	
}
