/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import java.text.NumberFormat;
import java.util.Collection;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
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
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class StockEditor extends AbstractEntityEditor<Stock>
{
	public static final String ID = "ch.eugster.colibri.stock.editor";

	private NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

	private ComboViewer paymentTypes;

	private FormattedText amount;

	private Button variable;

	public StockEditor()
	{
		EntityMediator.addListener(Stock.class, this);
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Stock.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof Stock)
		{
			final Stock stock = (Stock) entity;
			final Stock m = (Stock) ((StockEditorInput) this.getEditorInput()).getAdapter(Stock.class);
			if (stock.equals(m))
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
		this.paymentTypes.getControl().setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createDescriptionSection(scrolledForm);
	}

	// @Override
	// protected Message getMessage(final ErrorCode errorCode)
	// {
	// return null;
	// }

	@Override
	protected String getName()
	{
		final StockEditorInput input = (StockEditorInput) this.getEditorInput();
		final Stock stock = (Stock) input.getAdapter(Stock.class);
		if (stock.getId() == null)
		{
			return "Neu";
		}

		final java.util.Currency cur = java.util.Currency.getInstance(stock.getPaymentType().getCurrency().getCode());
		this.numberFormat.setCurrency(cur);
		return stock.getPaymentType().getCurrency().getCode() + " " + this.numberFormat.format(stock.getAmount());
	}

	@Override
	protected String getText()
	{
		final StockEditorInput input = (StockEditorInput) this.getEditorInput();
		final Stock stock = (Stock) input.getAdapter(Stock.class);
		return "Kassenstock: " + stock.getSalespoint().getName();
	}

	@Override
	protected void loadValues()
	{
		final Stock stock = (Stock) ((StockEditorInput) this.getEditorInput()).getAdapter(Stock.class);
		PaymentType type = null;
		if (stock.getPaymentType() == null)
		{
			final PaymentType[] paymentTypes = (PaymentType[]) this.paymentTypes.getInput();
			if (paymentTypes.length > 0)
			{
				type = paymentTypes[0];
			}
		}
		else
		{
			type = stock.getPaymentType();
		}
		this.paymentTypes.setSelection(new StructuredSelection(type));
		this.amount.setValue(new Double(stock.getAmount()));
		this.variable.setSelection(stock.isVariable());

		this.setDirty(stock.getId() == null);
	}

	@Override
	protected void saveValues()
	{
		final Stock stock = (Stock) ((StockEditorInput) this.getEditorInput()).getAdapter(Stock.class);
		final StructuredSelection ssel = (StructuredSelection) this.paymentTypes.getSelection();
		stock.setPaymentType((PaymentType) ssel.getFirstElement());
		final String value = this.amount.getControl().getText();
		stock.setAmount(Double.parseDouble(value));
	}

	@Override
	protected boolean validate()
	{
		final Message msg = null;
		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Stock> input)
	{
		return input.getAdapter(Stock.class) instanceof Stock;
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
		section.setText("Wert");
		section.setClient(this.fillDescriptionSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				StockEditor.this.scrolledForm.reflow(true);
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

		Label label = this.formToolkit.createLabel(composite, "Währung", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		final CCombo combo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.FLAT);
		combo.setLayoutData(layoutData);
		combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		combo.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				StockEditor.this.setDirty(true);
			}
		});

		PaymentType[] paymentTypes = new PaymentType[0];
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final PaymentTypeQuery query = (PaymentTypeQuery) persistenceService.getServerService().getQuery(PaymentType.class);
			final Collection<PaymentType> pts = query.selectByGroup(PaymentTypeGroup.CASH);
			final StockEditorInput input = (StockEditorInput) this.getEditorInput();
			final Stock stock = (Stock) input.getAdapter(Stock.class);
			final Salespoint salespoint = stock.getSalespoint();
			final Collection<Stock> stocks = salespoint.getStocks();
			for (final Stock stk : stocks)
			{
				if (!stk.equals(stock))
				{
					if (!stk.isDeleted())
					{
						pts.remove(stk.getPaymentType());
					}
				}
			}
			paymentTypes = pts.toArray(new PaymentType[0]);
		}

		this.paymentTypes = new ComboViewer(combo);
		this.paymentTypes.setContentProvider(new PaymentTypeContentProvider());
		this.paymentTypes.setLabelProvider(new PaymentTypeLabelProvider());
		this.paymentTypes.setSorter(new PaymentTypeSorter());
		this.paymentTypes.setInput(paymentTypes);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Betrag", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		final Text text = this.formToolkit.createText(composite, "");
		text.setLayoutData(layoutData);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				StockEditor.this.setDirty(true);
			}
		});
		this.amount = new FormattedText(text);
		this.amount.setFormatter(new NumberFormatter("#####0.00####"));

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Variabel", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.variable = this.formToolkit.createButton(composite, "", SWT.CHECK);
		this.variable.setLayoutData(layoutData);
		this.variable.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				StockEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}
}
