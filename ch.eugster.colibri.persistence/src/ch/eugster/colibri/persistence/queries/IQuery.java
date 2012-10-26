package ch.eugster.colibri.persistence.queries;

import java.util.Collection;

import ch.eugster.colibri.persistence.model.Entity;

public interface IQuery<T extends Entity>
{
	// long count(Expression expression);

	// T delete(T entity);

	// T find(Expression expression);

	T find(Long id);

	// T merge(T entity);

	// void refresh(AbstractEntity entity);

	// Collection<T> select(Expression expression);

	Collection<T> selectAll(boolean deletedToo);
}
