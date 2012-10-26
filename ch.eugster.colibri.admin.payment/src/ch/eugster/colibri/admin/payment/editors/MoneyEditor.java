/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.payment.editors;

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
import ch.eugster.colibri.persistence.model.Money;

public class MoneyEditor extends AbstractEntityEditor<Money>
{
	public static final String ID = "ch.eugster.colibri.money.editor";

	private FormattedText value;

	public MoneyEditor()
	{
		EntityMediator.addListener(Money.class, this);
	}

	@Override
	public void updateControls()
	{
		this.setPartName(getName());
		this.setContentDescription(getText());
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Money.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof Money)
		{
			final Money money = (Money) entity;
			final Money m = (Money) ((MoneyEditorInput) getEditorInput()).getAdapter(Money.class);
			if (money.equals(m))
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
			setDirty(true);
		}
	}

	@Override
	public void setFocus()
	{
		value.getControl().setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		createDescriptionSection(scrolledForm);
	}

	@Override
	protected String getName()
	{
		final MoneyEditorInput input = (MoneyEditorInput) getEditorInput();
		final Money money = (Money) input.getAdapter(Money.class);
		if (money.getId() == null)
		{
			return "Neu";
		}
		return money.getPaymentType().getCurrency().getCode() + " " + money.getValue();
	}

	// @Override
	// protected Message getMessage(ErrorCode errorCode)
	// {
	// return null;
	// }

	@Override
	protected String getText()
	{
		final MoneyEditorInput input = (MoneyEditorInput) getEditorInput();
		final Money money = (Money) input.getAdapter(Money.class);
		return "Kleingeld/Geldschein: " + money.getPaymentType().getCurrency().getCode();
	}

	@Override
	protected void loadValues()
	{
		final Money money = (Money) ((MoneyEditorInput) getEditorInput()).getAdapter(Money.class);
		value.setValue(money.getValue());
		setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final Money money = (Money) ((MoneyEditorInput) getEditorInput()).getAdapter(Money.class);
		final String value = this.value.getControl().getText();
		money.setValue(Double.parseDouble(value));
	}

	@Override
	protected boolean validate()
	{
		final Message msg = null;
		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Money> input)
	{
		return input.getAdapter(Money.class) instanceof Money;
	}

	private Section createDescriptionSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Wert");
		section.setClient(fillDescriptionSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				MoneyEditor.this.scrolledForm.reflow(true);
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

		final Composite composite = formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		final Label label = formToolkit.createLabel(composite, "Wert", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		final Text text = formToolkit.createText(composite, "");
		text.setLayoutData(layoutData);
		text.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				MoneyEditor.this.setDirty(true);
			}
		});
		value = new FormattedText(text);
		value.setFormatter(new NumberFormatter("#####0.00####"));

		formToolkit.paintBordersFor(composite);

		return composite;
	}
}
