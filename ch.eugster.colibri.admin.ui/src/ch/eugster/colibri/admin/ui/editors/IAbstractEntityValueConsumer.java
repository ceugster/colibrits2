package ch.eugster.colibri.admin.ui.editors;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public interface IAbstractEntityValueConsumer<T extends AbstractEntity>
{
	void getValues(T entity);

	void setValues(T entity);
}
