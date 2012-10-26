package ch.eugster.colibri.admin.ui.editors;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class AbstractEntitySectionPart<T extends AbstractEntity> extends SectionPart implements IAbstractEntityValueConsumer<T>
{
	public AbstractEntitySectionPart(final Composite parent, final FormToolkit toolkit, final int style)
	{
		super(parent, toolkit, style);
	}
}
