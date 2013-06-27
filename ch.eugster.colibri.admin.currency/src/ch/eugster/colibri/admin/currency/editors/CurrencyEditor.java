/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.currency.editors;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
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
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.queries.CurrencyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CurrencyEditor extends AbstractEntityEditor<Currency>
{
	public static final String ID = "ch.eugster.colibri.admin.currency.editor"; //$NON-NLS-1$

	private Text code;

	private Text name;

	private Text region;

	private FormattedText quotation;

	private FormattedText roundFactor;

	public CurrencyEditor()
	{
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Currency.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof Currency)
		{
			final Currency currency = (Currency) entity;
			final Currency c = (Currency) ((CurrencyEditorInput) this.getEditorInput()).getAdapter(Currency.class);
			if (currency.equals(c))
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().closeEditor(this, false);
			}
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
		this.createDescriptionSection(scrolledForm);
		this.createFinanceSection(scrolledForm);
		EntityMediator.addListener(Currency.class, this);
	}

	@Override
	protected String getName()
	{
		final Currency currency = (Currency) ((CurrencyEditorInput) this.getEditorInput()).getAdapter(Currency.class);
		if (currency.getId() == null)
		{
			return "Neu";
		}
		return currency.getCode();
	}

	// @Override
	// protected Message getMessage(final ErrorCode errorCode)
	// {
	// Message msg = null;
	//
	// msg = getEmptyCodeMessage();
	//
	// if (msg == null)
	// {
	// msg = getUniqueCodeMessage();
	// }
	//
	// return msg;
	// }

	@Override
	protected String getText()
	{
		return "Währung";
	}

	@Override
	protected void loadValues()
	{
		final Currency currency = (Currency) ((CurrencyEditorInput) this.getEditorInput()).getAdapter(Currency.class);
		this.code.setText(currency.getCode());
		this.name.setText(currency.getName());
		this.region.setText(currency.getRegion());
		this.quotation.setValue(currency.getQuotation());
		this.roundFactor.setValue(currency.getRoundFactor());
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final Currency currency = (Currency) ((CurrencyEditorInput) this.getEditorInput()).getAdapter(Currency.class);
		currency.setCode(this.code.getText());
		currency.setName(this.name.getText());
		currency.setRegion(this.region.getText());
		String value = this.quotation.getControl().getText();
		currency.setQuotation(Double.parseDouble(value));
		value = this.roundFactor.getControl().getText();
		currency.setRoundFactor(Double.parseDouble(value));
	}

	@Override
	protected void updateControls()
	{
	}

	@Override
	protected boolean validate()
	{
		final Message msg = this.getEmptyCodeMessage();

		if (msg == null)
		{
			this.getUniqueCodeMessage();
		}

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Currency> input)
	{
		return input.getAdapter(Currency.class) instanceof Currency;
	}

	private Section createDescriptionSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Beschreibung");
		section.setClient(this.fillDescriptionSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				CurrencyEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createFinanceSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Werte");
		section.setClient(this.fillFinanceSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				CurrencyEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillDescriptionSection(final Section parent)
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

		this.code = this.formToolkit.createText(composite, ""); //$NON-NLS-1$
		this.code.setLayoutData(layoutData);
		this.code.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				CurrencyEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.name = this.formToolkit.createText(composite, ""); //$NON-NLS-1$
		this.name.setLayoutData(layoutData);
		this.name.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				CurrencyEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Region", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.region = this.formToolkit.createText(composite, ""); //$NON-NLS-1$
		this.region.setLayoutData(layoutData);
		this.region.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				CurrencyEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillFinanceSection(final Section parent)
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

		Label label = this.formToolkit.createLabel(composite, "Kurs", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		Text text = this.formToolkit.createText(composite, ""); //$NON-NLS-1$
		text.setLayoutData(layoutData);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				CurrencyEditor.this.setDirty(true);
			}
		});
		this.quotation = new FormattedText(text);
		this.quotation.setFormatter(new NumberFormatter("#####0.0#####"));

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Rundungsfaktor", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		text = this.formToolkit.createText(composite, ""); //$NON-NLS-1$
		text.setLayoutData(layoutData);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				CurrencyEditor.this.setDirty(true);
			}
		});
		this.roundFactor = new FormattedText(text);
		this.roundFactor.setFormatter(new NumberFormatter("#####0.0#####"));

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyCodeMessage()
	{
		Message msg = null;

		if (this.code.getText().isEmpty())
		{
			msg = new Message(this.code, "Fehler");
			msg.setMessage("Die Währung benötigt einen eindeutigen Code.");
		}

		return msg;
	}

	private Message getUniqueCodeMessage()
	{
		Message msg = null;

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final Currency currency = (Currency) ((CurrencyEditorInput) this.getEditorInput()).getAdapter(Currency.class);
			final String code = this.code.getText();
			final CurrencyQuery query = (CurrencyQuery) persistenceService.getServerService().getQuery(Currency.class);
			if (query.isCodeUnique(code, currency.getId()))
			{
				msg = new Message(this.name, "Fehler");
				msg.setMessage("Der gewählte Code wird bereits verwendet.");
				return msg;
			}
		}

		return msg;
	}
}
