package ch.eugster.colibri.persistence.queries;

import javax.persistence.EntityManager;

import ch.eugster.colibri.persistence.model.Sequence;

public class SequenceQuery
{
	private EntityManager entityManager;

	public SequenceQuery(final EntityManager entityManager)
	{
		this.entityManager = entityManager;
	}

	public void findAndUpdate(final String key, final long value)
	{
		if (this.entityManager != null)
		{
			Sequence sequence = this.entityManager.find(Sequence.class, key);
			if (sequence == null)
			{
				sequence = new Sequence();
				sequence.setKey(key);
			}
			if (sequence.getValue() < value)
			{
				this.entityManager.getTransaction().begin();
				sequence.setValue(value + 1l);
				this.entityManager.merge(sequence);
				this.entityManager.getTransaction().commit();
			}
		}
	}

	public Sequence findSequenceByKey(final String key)
	{
		if (this.entityManager != null)
		{
			return this.entityManager.find(Sequence.class, key);
		}
		return null;
	}

	public Sequence merge(final Sequence sequence)
	{
		if (this.entityManager != null)
		{
			return this.entityManager.merge(sequence);
		}
		return sequence;
	}
}
