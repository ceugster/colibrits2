/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.payment.editors;

import java.util.Collection;
import java.util.Locale;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.nebula.widgets.formattedtext.PercentFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
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
import ch.eugster.colibri.persistence.model.ChargeType;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.CurrencyQuery;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class PaymentEditor extends AbstractEntityEditor<PaymentType>
{
	public static final String ID = "ch.eugster.colibri.admin.payment.editor";

	private Text code;

	private Text name;

	private ComboViewer currencies;

	private Text account;

	private Text mappingId;

	private ComboViewer productGroupViewer;
	
	private FormattedText percentualCharge;
	
	private FormattedText fixCharge;
	
	private Button openCashdrawer;

	private Button change;

	private Button[] chargeTypes;

	public PaymentEditor()
	{
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(PaymentType.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof PaymentType)
		{
			final PaymentType paymentType = (PaymentType) entity;
			final PaymentType pt = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);
			if (paymentType.equals(pt))
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
		this.createPaymentSection(scrolledForm);
		final PaymentType paymentType = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);
		if (paymentType.getPaymentTypeGroup().isChargable())
		{
			this.createCreditCardSection(scrolledForm);
		}
		EntityMediator.addListener(PaymentType.class, this);
	}

	@Override
	protected String getName()
	{
		final PaymentType paymentType = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);
		if (paymentType.getId() == null)
		{
			return "Neu";
		}
		return paymentType.getCode();
	}

	// @Override
	// protected Message getMessage(final ErrorCode errorCode)
	// {
	// return getEmptyCurrencySelectionMessage();
	// }

	@Override
	protected String getText()
	{
		final PaymentType paymentType = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);
		return "Zahlungsart: " + paymentType.getPaymentTypeGroup().toString();
	}

	@Override
	protected void loadValues()
	{
		final PaymentType paymentType = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);
		this.code.setText(paymentType.getCode());
		this.name.setText(paymentType.getName());

		Currency currency = null;
		if (paymentType.getCurrency() == null)
		{
			final java.util.Currency cur = java.util.Currency.getInstance(Locale.getDefault());
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
			if (persistenceService != null)
			{
				final CurrencyQuery currencyQuery = (CurrencyQuery) persistenceService.getServerService().getQuery(Currency.class);
				currency = currencyQuery.selectByCode(cur.getCurrencyCode());
				if (paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.CASH))
				{
					final PaymentTypeQuery paymentTypeQuery = (PaymentTypeQuery) persistenceService.getServerService().getQuery(PaymentType.class);
					if (paymentTypeQuery.selectByPaymentTypeGroupAndCurrency(paymentType.getPaymentTypeGroup(), currency).size() > 0)
					{
						final Currency[] currencies = (Currency[]) this.currencies.getInput();
						if (currencies.length > 0)
						{
							currency = currencies[0];
						}
						else
						{
							currency = null;
						}
					}
				}
			}
		}
		else
		{
			currency = paymentType.getCurrency();
		}

		this.currencies.setSelection(new StructuredSelection(new Currency[] { currency }));

		this.account.setText(paymentType.getAccount());
		this.mappingId.setText(paymentType.getMappingId());

		this.openCashdrawer.setSelection(paymentType.isOpenCashdrawer());
		if (this.change != null)
		{
			this.change.setSelection(paymentType.isChange());
		}

		if (paymentType.getPaymentTypeGroup().isChargable())
		{
			PersistenceService service = (PersistenceService) this.persistenceServiceTracker.getService();
			if (service != null)
			{
				ProductGroupQuery query = (ProductGroupQuery) service.getServerService().getQuery(ProductGroup.class);
				Collection<ProductGroup> productGroups = query.selectByProductGroupTypeWithoutPayedInvoice(ProductGroupType.NON_SALES_RELATED);
				productGroupViewer.setInput(productGroups.toArray(new ProductGroup[0]));
				if (paymentType.getProductGroup() != null)
				{
					productGroupViewer.setSelection(new StructuredSelection(new ProductGroup[] { paymentType.getProductGroup() }));
				}
			}
			ChargeType chargeType = paymentType.getChargeType() == null ? ChargeType.NONE : paymentType.getChargeType();
			chargeTypes[chargeType.ordinal()].setSelection(true);
			this.percentualCharge.setValue(paymentType.getPercentualCharge());
			this.fixCharge.setValue(paymentType.getFixCharge());
			this.percentualCharge.getControl().setEnabled(chargeType.equals(ChargeType.PERCENTUAL));
		}
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final PaymentType paymentType = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);
		paymentType.setCode(this.code.getText());
		paymentType.setName(this.name.getText());

		StructuredSelection ssel = (StructuredSelection) this.currencies.getSelection();
		final Currency currency = (Currency) ssel.getFirstElement();
		paymentType.setCurrency(currency);

		paymentType.setAccount(this.account.getText());
		paymentType.setMappingId(this.mappingId.getText());

		paymentType.setOpenCashdrawer(this.openCashdrawer.getSelection());
		if (this.change != null)
		{
			paymentType.setChange(this.change.getSelection());
		}

		if (paymentType.getPaymentTypeGroup().isChargable())
		{
			ssel = (StructuredSelection) productGroupViewer.getSelection();
			ProductGroup productGroup = (ProductGroup) ssel.getFirstElement();
			paymentType.setProductGroup(productGroup);
			for (Button chargeType : chargeTypes)
			{
				if (chargeType.getSelection())
				{
					paymentType.setChargeType((ChargeType)chargeType.getData("charge.type"));
				}
			}
			try
			{
				String value = this.percentualCharge.getControl().getText();
				value = value.replace("%", "");
				double charge = Double.valueOf(value).doubleValue() / 100;
				paymentType.setPercentualCharge(charge);

				value = this.fixCharge.getControl().getText();
				charge = Double.valueOf(value).doubleValue();
				paymentType.setFixCharge(charge);
			}
			catch(NumberFormatException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	protected boolean validate()
	{
		Message msg = this.getEmptyCurrencySelectionMessage();

		final PaymentType paymentType = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);
		if (msg == null && paymentType.getPaymentTypeGroup().isChargable())
		{
			msg = this.getEmptyProductGroupSelectionMessage();
		}

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<PaymentType> input)
	{
		return input.getAdapter(PaymentType.class) instanceof PaymentType;
	}

	private Section createPaymentSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Angaben zur Zahlungsart");
		section.setClient(this.fillPaymentSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				PaymentEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createCreditCardSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Optionen Kreditkarte");
		section.setClient(this.fillCreditCardSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				PaymentEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillPaymentSection(final Section parent)
	{
		final PaymentType paymentType = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);

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

		Label label = this.formToolkit.createLabel(composite, "Beschriftung", SWT.NONE);
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
				if (PaymentEditor.this.name.getText().isEmpty()
						|| PaymentEditor.this.name.getText().equals(
								PaymentEditor.this.code.getText().substring(0,
										PaymentEditor.this.code.getText().length() - 1)))
				{
					PaymentEditor.this.name.setText(PaymentEditor.this.code.getText());
				}
				PaymentEditor.this.setDirty(true);
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
				PaymentEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Währung", SWT.NONE);
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
				PaymentEditor.this.setDirty(true);
			}
		});

		final Currency[] currencies = this.selectCurrencies();
		this.currencies = new ComboViewer(combo);
		this.currencies.setContentProvider(new CurrencyContentProvider());
		this.currencies.setLabelProvider(new CurrencyLabelProvider());
		this.currencies.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
		this.currencies.setSorter(new CurrencySorter());
		this.currencies.setInput(currencies);
		this.currencies.getCCombo().setEnabled(paymentType.getId() == null);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Konto", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.account = this.formToolkit.createText(composite, "");
		this.account.setLayoutData(layoutData);
		this.account.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				PaymentEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		this.openCashdrawer = this.formToolkit.createButton(composite, "Kassenschublade öffnen", SWT.CHECK);
		this.openCashdrawer.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				PaymentEditor.this.setDirty(true);
			}

			public void widgetSelected(final SelectionEvent event)
			{
				PaymentEditor.this.setDirty(true);
			}
		});

		if ((paymentType.getPaymentTypeGroup() != null) && paymentType.getPaymentTypeGroup().isAsChangeAvailable())
		{
			layoutData = new TableWrapData();
			layoutData.grabHorizontal = false;

			label = this.formToolkit.createLabel(composite, "", SWT.NONE);
			label.setLayoutData(layoutData);

			layoutData = new TableWrapData();
			layoutData.grabHorizontal = false;

			this.change = this.formToolkit.createButton(composite, "Kann als Wechselgeld verwendet werden (Rückgeld)", SWT.CHECK);
			this.change.addSelectionListener(new SelectionListener()
			{
				public void widgetDefaultSelected(final SelectionEvent event)
				{
					PaymentEditor.this.setDirty(true);
				}

				public void widgetSelected(final SelectionEvent event)
				{
					PaymentEditor.this.setDirty(true);
				}
			});
		}

		label = this.formToolkit.createLabel(composite, "ExportId", SWT.NONE);
		label.setLayoutData(new TableWrapData());

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.mappingId = this.formToolkit.createText(composite, "", SWT.SINGLE);
		this.mappingId.setLayoutData(layoutData);
		this.mappingId.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				PaymentEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillCreditCardSection(final Section parent)
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

		Label label = this.formToolkit.createLabel(composite, "Belastungskonto", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		CCombo combo = new CCombo(composite, SWT.READ_ONLY | SWT.FLAT);
		combo.setLayoutData(layoutData);
		combo.setCursor(combo.getDisplay().getSystemCursor(SWT.CURSOR_ARROW));
		
		productGroupViewer = new ComboViewer(combo);
		productGroupViewer.setContentProvider(new ArrayContentProvider());
		productGroupViewer.setLabelProvider(new LabelProvider() 
		{
			@Override
			public String getText(Object element) 
			{
				if (element instanceof ProductGroup)
				{
					ProductGroup productGroup = (ProductGroup) element;
					return (productGroup.getCode().isEmpty() ? "" : productGroup.getCode() + " - ") + productGroup.getName();
				}
				return "";
			}
		});
		
		chargeTypes = new Button[ChargeType.values().length];
		for (ChargeType chargeType : ChargeType.values())
		{
			layoutData = new TableWrapData();
			layoutData.grabHorizontal = false;

			label = this.formToolkit.createLabel(composite, chargeType.equals(ChargeType.values()[0]) ? "Belastungsart" : "", SWT.NONE);
			label.setLayoutData(layoutData);

			layoutData = new TableWrapData();
			layoutData.align = TableWrapData.FILL;
			layoutData.grabHorizontal = true;

			chargeTypes[chargeType.ordinal()] = this.formToolkit.createButton(composite, chargeType.label(), SWT.RADIO);
			chargeTypes[chargeType.ordinal()].setData("charge.type", chargeType);
			chargeTypes[chargeType.ordinal()].addListener(SWT.Selection, new Listener() 
			{
				@Override
				public void handleEvent(Event event) 
				{
					ChargeType chargeType = (ChargeType) event.widget.getData("charge.type");
					percentualCharge.getControl().setEnabled(chargeType.equals(ChargeType.PERCENTUAL));
					PaymentEditor.this.setDirty(true);
				}
			});
		}

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "prozentuelle Belastung", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		Text text = this.formToolkit.createText(composite, "");
		text.setLayoutData(layoutData);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				PaymentEditor.this.setDirty(true);
			}
		});
		percentualCharge = new FormattedText(text);
		percentualCharge.setFormatter(new PercentFormatter("#0.###", "#0.###"));

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Fixe (oder Mindest-) Belastung", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		text = this.formToolkit.createText(composite, "");
		text.setLayoutData(layoutData);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				PaymentEditor.this.setDirty(true);
			}
		});
		fixCharge = new FormattedText(text);
		fixCharge.setFormatter(new NumberFormatter("##0.00", "##0.00"));
		
		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyCurrencySelectionMessage()
	{
		Message msg = null;

		if (this.currencies.getSelection().isEmpty())
		{
			msg = new Message(this.currencies.getControl(), "Fehler");
			msg.setMessage("Der Zahlungsart muss eine Währung zugeordnet werden.");
		}

		return msg;
	}

	private Message getEmptyProductGroupSelectionMessage()
	{
		Message msg = null;

		if (!this.chargeTypes[0].getSelection())
		{
			if (this.productGroupViewer.getSelection().isEmpty())
			{
				msg = new Message(this.productGroupViewer.getControl(), "Fehler");
				msg.setMessage("Der Kreditkarte muss ein Konto zugeordnet werden.");
			}
		}

		return msg;
	}

	private Currency[] selectCurrencies()
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final CurrencyQuery query = (CurrencyQuery) persistenceService.getServerService().getQuery(Currency.class);
			final Collection<Currency> currencies = query.selectAll(true);
			final PaymentType thisPaymentType = (PaymentType) this.getEditorInput().getAdapter(PaymentType.class);
			if (thisPaymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.CASH))
			{
				final PaymentTypeQuery paymentTypeQuery = (PaymentTypeQuery) persistenceService.getServerService().getQuery(PaymentType.class);
				final Collection<PaymentType> paymentTypes = paymentTypeQuery.selectByGroup(thisPaymentType.getPaymentTypeGroup());
				for (final PaymentType paymentType : paymentTypes)
				{
					
					if (thisPaymentType.getCurrency() != null)
					{
						if (!paymentType.getCurrency().getId().equals(thisPaymentType.getCurrency().getId()))
						{
							if (currencies.contains(paymentType.getCurrency()))
							{
								currencies.remove(paymentType.getCurrency());
							}
						}
					}
					else
					{
						if (currencies.contains(paymentType.getCurrency()))
						{
							currencies.remove(paymentType.getCurrency());
						}
					}
				}
			}
			return currencies.toArray(new Currency[0]);
		}
		return new Currency[0];
	}
}
