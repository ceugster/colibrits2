/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TaxRateEditor extends AbstractEntityEditor<TaxRate>
{
	public static final String ID = "ch.eugster.colibri.admin.editors.taxRateEditor";

	private Text code;

	private Text name;

	public TaxRateEditor()
	{
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(TaxRate.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		final TaxRate taxRate = (TaxRate) entity;
		if (taxRate.equals(this.getEditorInput().getAdapter(TaxRate.class)))
		{
			this.dispose();
		}
	}

	@Override
	public void propertyChanged(final Object source, final int propId)
	{
		if (propId == IEditorPart.PROP_DIRTY)
		{
			this.setDirty(true);
		}
	}

	@Override
	public void setFocus()
	{
		this.code.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createSection(scrolledForm);
		EntityMediator.addListener(TaxRate.class, this);
	}

	// @Override
	// protected Message getMessage(ErrorCode errorCode)
	// {
	// Message msg = null;
	// // TODO
	// if (errorCode.equals(ErrorCode.CONNECTION_FAILOR))
	// msg = getUniqueCodeMessage();
	// return msg;
	// }

	@Override
	protected String getName()
	{
		final TaxRate taxRate = (TaxRate) this.getEditorInput().getAdapter(TaxRate.class);
		if (taxRate.getId() == null)
		{
			return "Neu";
		}
		return taxRate.getName();
	}

	@Override
	protected String getText()
	{
		return "Mehrwertsteuergruppe";
	}

	@Override
	protected void loadValues()
	{
		final TaxRate taxRate = (TaxRate) this.getEditorInput().getAdapter(TaxRate.class);
		this.code.setText(taxRate.getCode());
		this.name.setText(taxRate.getName());
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final TaxRate taxRate = (TaxRate) this.getEditorInput().getAdapter(TaxRate.class);
		taxRate.setCode(this.code.getText());
		taxRate.setName(this.name.getText());
	}

	@Override
	protected boolean validate()
	{
		Message msg = this.getEmptyCodeMessage();

		if (msg == null)
		{
			msg = this.getUniqueCodeMessage();
		}

		if (msg == null)
		{
			msg = this.getEmptyNameMessage();
		}

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<TaxRate> input)
	{
		return input.getAdapter(TaxRate.class) instanceof TaxRate;
	}

	private Section createSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Angaben zur Mehrwertsteuergruppe");
		section.setClient(this.fillSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				TaxRateEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		Label label = this.formToolkit.createLabel(composite, "Code", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.code = this.formToolkit.createText(composite, "");
		this.code.setLayoutData(layoutData);
		this.code.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				TaxRateEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.name = this.formToolkit.createText(composite, "");
		this.name.setLayoutData(layoutData);
		this.name.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				TaxRateEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyCodeMessage()
	{
		Message msg = null;

		if (this.name.getText().isEmpty())
		{
			msg = new Message(this.name, "Fehler");
			msg.setMessage("Die Mehrwertsteuergruppe muss einen Code haben.");
		}
		return msg;
	}

	private Message getEmptyNameMessage()
	{
		Message msg = null;

		if (this.name.getText().isEmpty())
		{
			msg = new Message(this.name, "Fehler");
			msg.setMessage("Die Mehrwertsteuergruppe sollte eine Bezeichnung haben.");
		}
		return msg;
	}

	private Message getUniqueCodeMessage()
	{
		Message msg = null;

		final TaxRate taxRate = (TaxRate) this.getEditorInput().getAdapter(TaxRate.class);

		final String code = this.code.getText();

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final TaxRateQuery query = (TaxRateQuery) persistenceService.getServerService().getQuery(TaxRate.class);
			if (!query.isCodeUnique(code, taxRate.getId()))
			{
				msg = new Message(this.code, "Fehler");
				msg.setMessage("Der gewählte Code wird bereits verwendet.");
				return msg;
			}
		}

		return msg;
	}
}
