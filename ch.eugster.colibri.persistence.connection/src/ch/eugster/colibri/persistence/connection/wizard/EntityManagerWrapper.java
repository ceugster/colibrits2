package ch.eugster.colibri.persistence.connection.wizard;

import javax.persistence.EntityManager;

public class EntityManagerWrapper
{
	private EntityManager entityManager;

	public EntityManager getEntityManager()
	{
		return this.entityManager;
	}

	public void setEntityManager(final EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

}
