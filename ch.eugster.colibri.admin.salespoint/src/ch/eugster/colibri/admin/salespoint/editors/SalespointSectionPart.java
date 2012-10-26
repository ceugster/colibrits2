package ch.eugster.colibri.admin.salespoint.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.eugster.colibri.admin.ui.editors.AbstractEntitySectionPart;
import ch.eugster.colibri.persistence.model.Salespoint;

public class SalespointSectionPart extends AbstractEntitySectionPart<Salespoint>
{
	private Text name;

	private Text host;

	private Text location;

	private Text mapping;

	public SalespointSectionPart(final Composite parent, final FormToolkit toolkit, final int style)
	{
		super(parent, toolkit, style);
	}

	public void getValues(final Salespoint salespoint)
	{
		salespoint.setName(name.getText());
		salespoint.setHost(host.getText());
		salespoint.setLocation(location.getText());
		salespoint.setMapping(mapping.getText());
	}

	@Override
	public void initialize(final IManagedForm form)
	{
		super.initialize(form);

		getSection().setClient(fillSalespointSection(getSection()));
		getSection().setText("Kasse");
		getSection().addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				SalespointSectionPart.this.getManagedForm().getForm().reflow(true);
			}
		});
	}

	@Override
	public void setFocus()
	{
		name.setFocus();
	}

	public void setValues(final Salespoint salespoint)
	{
		name.setText(salespoint.getName());
		host.setText(salespoint.getHost());
		location.setText(salespoint.getLocation());
		mapping.setText(salespoint.getMapping());
	}

	private Control fillSalespointSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = getManagedForm().getToolkit().createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		Label label = getManagedForm().getToolkit().createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		name = getManagedForm().getToolkit().createText(composite, "");
		name.setLayoutData(layoutData);
		name.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				SalespointSectionPart.this.markDirty();
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = getManagedForm().getToolkit().createLabel(composite, "Host", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		host = getManagedForm().getToolkit().createText(composite, "");
		host.setLayoutData(layoutData);
		host.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				SalespointSectionPart.this.markDirty();
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = getManagedForm().getToolkit().createLabel(composite, "Standort", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		location = getManagedForm().getToolkit().createText(composite, "");
		location.setLayoutData(layoutData);
		location.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				SalespointSectionPart.this.markDirty();
			}
		});

		label = getManagedForm().getToolkit().createLabel(composite, "ExportId", SWT.NONE);
		label.setLayoutData(new TableWrapData());

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		mapping = getManagedForm().getToolkit().createText(composite, "", SWT.SINGLE);
		mapping.setLayoutData(layoutData);
		mapping.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				SalespointSectionPart.this.markDirty();
			}
		});

		getManagedForm().getToolkit().paintBordersFor(composite);

		return composite;
	}

}
