package ch.eugster.colibri.persistence.events;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public interface EntityListener
{
	public void postDelete(AbstractEntity entity);

	public void postLoad(AbstractEntity entity);

	public void postPersist(AbstractEntity entity);

	public void postRemove(AbstractEntity entity);

	public void postUpdate(AbstractEntity entity);

	public void preDelete(AbstractEntity entity);

	public void prePersist(AbstractEntity entity);

	public void preRemove(AbstractEntity entity);

	public void preUpdate(AbstractEntity entity);
}
