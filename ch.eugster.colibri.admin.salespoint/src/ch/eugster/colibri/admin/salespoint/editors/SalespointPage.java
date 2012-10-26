package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.swt.layout.FormData;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityFormPage;
import ch.eugster.colibri.admin.ui.editors.IAbstractEntityValueConsumer;
import ch.eugster.colibri.persistence.model.Salespoint;

public class SalespointPage extends AbstractEntityFormPage<Salespoint>
{
	public SalespointPage(final FormEditor editor, final String id, final String title)
	{
		super(editor, id, title);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void getValues(final Salespoint salespoint)
	{
		final IFormPart[] parts = getManagedForm().getParts();
		for (final IFormPart part : parts)
		{
			if (part instanceof IAbstractEntityValueConsumer<?>)
			{
				((IAbstractEntityValueConsumer<Salespoint>) part).getValues(salespoint);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setValues(final Salespoint salespoint)
	{
		final IFormPart[] parts = getManagedForm().getParts();
		for (final IFormPart part : parts)
		{
			if (part instanceof IAbstractEntityValueConsumer<?>)
			{
				((IAbstractEntityValueConsumer<Salespoint>) part).setValues(salespoint);
			}
		}
	}

	@Override
	protected void createFormContent(final IManagedForm managedForm)
	{
		final ColumnLayout layout = new ColumnLayout();
		managedForm.getForm().getBody().setLayout(layout);
		managedForm.getForm().getBody().setLayoutData(new FormData(200, 100));

		final FormToolkit toolkit = managedForm.getToolkit();
		final int style = ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE;

		final SalespointSectionPart part = new SalespointSectionPart(managedForm.getForm().getBody(), toolkit, style);
		managedForm.addPart(part);
	}

}
