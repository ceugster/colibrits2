package ch.eugster.colibri.persistence.events;

import java.util.ArrayList;
import java.util.HashMap;

import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public class EntityMediator
{

	private static HashMap<Class<? extends AbstractEntity>, ArrayList<EntityListener>> entityListeners = new HashMap<Class<? extends AbstractEntity>, ArrayList<EntityListener>>();

	private static EntityMediator instance;

	private boolean active = true;

	@PostLoad
	public void postLoad(final AbstractEntity entity)
	{
		if (this.active)
		{
			final ArrayList<EntityListener> entityListeners = EntityMediator.entityListeners.get(entity.getClass());
			if (entityListeners != null)
			{
				if (!entityListeners.isEmpty())
				{
					final EntityListener[] listeners = entityListeners.toArray(new EntityListener[0]);
					for (final EntityListener listener : listeners)
					{
						listener.postLoad(entity);
					}
				}
			}
		}
	}

	@PostPersist
	public void postPersist(final AbstractEntity entity)
	{
		if (this.active)
		{
			final ArrayList<EntityListener> entityListeners = EntityMediator.entityListeners.get(entity.getClass());
			if (entityListeners != null)
			{
				if (!entityListeners.isEmpty())
				{
					final EntityListener[] listeners = entityListeners.toArray(new EntityListener[0]);
					for (final EntityListener listener : listeners)
					{
						listener.postPersist(entity);
					}
				}
			}
		}
	}

	@PostRemove
	public void postRemove(final AbstractEntity entity)
	{
		if (this.active)
		{
			final ArrayList<EntityListener> entityListeners = EntityMediator.entityListeners.get(entity.getClass());
			if (entityListeners != null)
			{
				if (!entityListeners.isEmpty())
				{
					final EntityListener[] listeners = entityListeners.toArray(new EntityListener[0]);
					for (final EntityListener listener : listeners)
					{
						listener.postRemove(entity);
					}
				}
			}
		}
	}

	@PostUpdate
	public void postUpdate(final AbstractEntity entity)
	{
		if (this.active)
		{
			final ArrayList<EntityListener> entityListeners = EntityMediator.entityListeners.get(entity.getClass());
			if (entityListeners != null)
			{
				if (!entityListeners.isEmpty())
				{
					final EntityListener[] listeners = entityListeners.toArray(new EntityListener[0]);
					for (final EntityListener listener : listeners)
					{
						if (entity.isDeleted())
						{
							listener.postDelete(entity);
						}
						else
						{
							listener.postUpdate(entity);
						}
					}
				}
			}
		}
	}

	@PrePersist
	public void prePersist(final AbstractEntity entity)
	{
		if (this.active)
		{
			final ArrayList<EntityListener> entityListeners = EntityMediator.entityListeners.get(entity.getClass());
			if (entityListeners != null)
			{
				if (!entityListeners.isEmpty())
				{
					final EntityListener[] listeners = entityListeners.toArray(new EntityListener[0]);
					for (final EntityListener listener : listeners)
					{
						listener.prePersist(entity);
					}
				}
			}
		}
	}

	@PreRemove
	public void preRemove(final AbstractEntity entity)
	{
		if (this.active)
		{
			final ArrayList<EntityListener> entityListeners = EntityMediator.entityListeners.get(entity.getClass());
			if (entityListeners != null)
			{
				if (!entityListeners.isEmpty())
				{
					final EntityListener[] listeners = entityListeners.toArray(new EntityListener[0]);
					for (final EntityListener listener : listeners)
					{
						listener.preRemove(entity);
					}
				}
			}
		}
	}

	@PreUpdate
	public void preUpdate(final AbstractEntity entity)
	{
		if (this.active)
		{
			final ArrayList<EntityListener> entityListeners = EntityMediator.entityListeners.get(entity.getClass());
			if (entityListeners != null)
			{
				if (!entityListeners.isEmpty())
				{
					final EntityListener[] listeners = entityListeners.toArray(new EntityListener[0]);
					for (final EntityListener listener : listeners)
					{
						if (entity.isDeleted())
						{
							listener.preDelete(entity);
						}
						else
						{
							listener.preUpdate(entity);
						}
					}
				}
			}
		}
	}

	public static void addListener(final Class<? extends AbstractEntity> clazz, final EntityListener listener)
	{
		ArrayList<EntityListener> listeners = EntityMediator.entityListeners.get(clazz);
		if (listeners == null)
		{
			listeners = new ArrayList<EntityListener>();
		}

		listeners.add(listener);
		EntityMediator.entityListeners.put(clazz, listeners);
	}

	public static EntityMediator getInstance()
	{
		if (EntityMediator.instance == null)
		{
			EntityMediator.instance = new EntityMediator();
		}
		return EntityMediator.instance;
	}

	public static void removeListener(final Class<? extends AbstractEntity> clazz, final EntityListener listener)
	{
		final ArrayList<EntityListener> listeners = EntityMediator.entityListeners.get(clazz);
		if (listeners != null)
		{
			listeners.remove(listener);
		}
		EntityMediator.entityListeners.put(clazz, listeners);
	}

	public static void setActive(final boolean active)
	{
		EntityMediator.instance.active = active;
	}
}
