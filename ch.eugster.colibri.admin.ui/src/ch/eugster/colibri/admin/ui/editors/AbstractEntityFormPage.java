package ch.eugster.colibri.admin.ui.editors;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class AbstractEntityFormPage<T extends AbstractEntity> extends FormPage
{
	public AbstractEntityFormPage(final FormEditor editor, final String id, final String title)
	{
		super(editor, id, title);
	}

	public abstract void getValues(T entity);

	public abstract void setValues(T entity);
}
