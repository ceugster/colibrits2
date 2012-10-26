package ch.eugster.colibri.admin.ui.editors;

import org.eclipse.ui.forms.AbstractFormPart;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class AbstractEntityFormPart<T extends AbstractEntity> extends AbstractFormPart implements IAbstractEntityValueConsumer<T>
{
}
