package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.swt.layout.FormData;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

import ch.eugster.colibri.admin.ui.editors.AbstractEntitySharedHeaderFormEditor;
import ch.eugster.colibri.persistence.model.Salespoint;

public class SalespointMultiPageEditor extends AbstractEntitySharedHeaderFormEditor<Salespoint>
{
	public static final String ID = "ch.eugster.colibri.admin.salespoint.editor";

	@Override
	public void propertyChanged(final Object source, final int propId)
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void addPages()
	{
		try
		{
			this.addPage(new SalespointPage(this, "salespoint.id", "TEST"));
			this.addPage(new SalespointPage(this, "salespoint.provider", "TEST"));
		}
		catch (final PartInitException e)
		{

		}
	}

	@Override
	protected void createHeaderContents(final IManagedForm headerForm)
	{
		final Salespoint salespoint = (Salespoint) getEditorInput().getAdapter(Salespoint.class);

		final ColumnLayout layout = new ColumnLayout();
		headerForm.getForm().getBody().setLayout(layout);
		headerForm.getForm().getBody().setLayoutData(new FormData());

		final FormToolkit toolkit = headerForm.getToolkit();
		final Form form = toolkit.createForm(headerForm.getForm().getBody());
		form.setText(salespoint.getId() == null ? "Neue Kasse" : "Kasse " + salespoint.getName().trim());
		toolkit.decorateFormHeading(form);
	}
}
