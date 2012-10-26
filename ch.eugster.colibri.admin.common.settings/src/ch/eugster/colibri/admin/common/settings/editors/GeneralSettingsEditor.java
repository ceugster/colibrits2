/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.common.settings.editors;

import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
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
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.CommonSettings.HostnameResolver;

public class GeneralSettingsEditor extends AbstractEntityEditor<CommonSettings>
{
	public static final String ID = "ch.eugster.colibri.admin.common.settings.editor";

	private Text address;

	private Text taxNumber;

	private Button[] hostnameResolvers;

	private FormattedText maxPriceRange;

	private FormattedText maxPriceAmount;

	private FormattedText maxPaymentRange;

	private FormattedText maxPaymentAmount;

	private FormattedText maxQuantityRange;

	private FormattedText maxQuantityAmount;

	private Text receiptNumberFormat;

	private Button taxInclusive;

	private Spinner transferDelay;
	
	private Spinner transferRepeatDelay;
	
	private Spinner transferReceiptCount;
	
	private Button allowTestSettlement;

	private Button maximizedClientWindow;

	private ScrolledForm scrolledForm;

	public GeneralSettingsEditor()
	{
		EntityMediator.addListener(CommonSettings.class, this);
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(CommonSettings.class, this);
		super.dispose();
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
		this.hostnameResolvers[0].setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.scrolledForm = scrolledForm;

		this.createAddressSection(scrolledForm);
		this.createRegistrationSection(scrolledForm);
		this.createReceiptNumberFormatSection(scrolledForm);
		this.createConstraintsSection(scrolledForm);
		this.createTaxSection(scrolledForm);
		this.createTransferSection(scrolledForm);
		this.createSettlementSection(scrolledForm);
		this.createSettingSection(scrolledForm);
	}

	@Override
	protected String getName()
	{
		return "Einstellungen";
	}

	@Override
	protected String getText()
	{
		return "Allgemeine Einstellungen";
	}

	@Override
	protected void loadValues()
	{
		final CommonSettings commonSettings = (CommonSettings) ((GeneralSettingsEditorInput) this.getEditorInput())
				.getAdapter(CommonSettings.class);

		this.address.setText(commonSettings.getAddress());
		this.taxNumber.setText(commonSettings.getTaxNumber());

		final HostnameResolver resolver = commonSettings.getHostnameResolver();
		for (final Button hostnameResolver : this.hostnameResolvers)
		{
			hostnameResolver.setSelection(hostnameResolver.getData("resolver").equals(resolver));
		}

		this.maxPriceRange.setValue(Double.valueOf(commonSettings.getMaxPriceRange()));
		this.maxPriceAmount.setValue(Double.valueOf(commonSettings.getMaxPriceAmount()));

		this.maxPaymentRange.setValue(Double.valueOf(commonSettings.getMaxPaymentRange()));
		this.maxPaymentAmount.setValue(Double.valueOf(commonSettings.getMaxPaymentAmount()));

		this.maxQuantityRange.setValue(Integer.valueOf(commonSettings.getMaxQuantityRange()));
		this.maxQuantityAmount.setValue(commonSettings.getMaxQuantityAmount());

		this.receiptNumberFormat.setText(commonSettings.getReceiptNumberFormat());

		this.taxInclusive.setSelection(commonSettings.isTaxInclusive());

		this.transferDelay.setSelection(commonSettings.getTransferDelay());
		this.transferRepeatDelay.setSelection(commonSettings.getTransferRepeatDelay());
		this.transferReceiptCount.setSelection(commonSettings.getTransferReceiptCount());
		
		this.allowTestSettlement.setSelection(commonSettings.isAllowTestSettlement());
		
		this.maximizedClientWindow.setSelection(commonSettings.isMaximizedClientWindow());
		
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final CommonSettings commonSettings = (CommonSettings) ((GeneralSettingsEditorInput) this.getEditorInput())
				.getAdapter(CommonSettings.class);

		commonSettings.setAddress(address.getText());
		commonSettings.setTaxNumber(taxNumber.getText());

		for (final Button hostnameResolver : this.hostnameResolvers)
		{
			if (hostnameResolver.getSelection())
			{
				commonSettings.setHostnameResolver((HostnameResolver) hostnameResolver.getData("resolver"));
			}
		}

		commonSettings.setMaxPriceRange(((Double) this.maxPriceRange.getValue()).doubleValue());
		commonSettings.setMaxPriceAmount(((Double) this.maxPriceAmount.getValue()).doubleValue());
		commonSettings.setMaxPaymentRange(((Double) this.maxPaymentRange.getValue()).doubleValue());
		commonSettings.setMaxPaymentAmount(((Double) this.maxPaymentAmount.getValue()).doubleValue());
		commonSettings.setMaxQuantityRange(((Integer) this.maxQuantityRange.getValue()).intValue());
		commonSettings.setMaxQuantityAmount(((Integer) this.maxQuantityAmount.getValue()).intValue());

		commonSettings.setReceiptNumberFormat(this.receiptNumberFormat.getText());

		commonSettings.setTaxInclusive(this.taxInclusive.getSelection());

		commonSettings.setTransferDelay(this.transferDelay.getSelection());
		commonSettings.setTransferRepeatDelay(this.transferRepeatDelay.getSelection());
		commonSettings.setTransferReceiptCount(this.transferReceiptCount.getSelection());
		
		commonSettings.setAllowTestSettlement(this.allowTestSettlement.getSelection());

		commonSettings.setMaximizedClientWindow(this.maximizedClientWindow.getSelection());
	}

	@Override
	protected void updateControls()
	{
		super.updateControls();
	}

	@Override
	protected boolean validate()
	{
		final Message msg = null;
		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<CommonSettings> input)
	{
		return input.getAdapter(CommonSettings.class) instanceof CommonSettings;
	}

	private Section createConstraintsSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Validierung Eingaben");
		section.setClient(this.fillConstraintsSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				GeneralSettingsEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createReceiptNumberFormatSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Format Belegnummern");
		section.setClient(this.fillReceiptNumberFormatSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				GeneralSettingsEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createSettingSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Weitere Einstellungen");
		section.setClient(this.fillSettingsSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				GeneralSettingsEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createAddressSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Adresse");
		section.setClient(this.fillAddressSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				GeneralSettingsEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createRegistrationSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Kassenregistrierung");
		section.setClient(this.fillRegistrationSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				GeneralSettingsEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createTaxSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Berechnung Mehrwertsteuer");
		section.setClient(this.fillTaxSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				GeneralSettingsEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createTransferSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Belegtransfer");
		section.setClient(this.fillTransferSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				GeneralSettingsEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createSettlementSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Abschluss");
		section.setClient(this.fillSettlementSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				GeneralSettingsEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillConstraintsSection(final Section parent)
	{
		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, false));

		Label label = this.formToolkit.createLabel(composite, "Preis bestätigen ab", SWT.NONE);
		label.setLayoutData(new GridData());

		GridData gridData = new GridData();
		gridData.widthHint = 64;

		Text text = this.formToolkit.createText(composite, "", SWT.RIGHT);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(final ModifyEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

		});

		this.maxPriceRange = new FormattedText(text);
		this.maxPriceRange.setFormatter(new NumberFormatter("########0.00", "###,###,##0.00"));

		label = this.formToolkit.createLabel(composite, "Maximale Preiseingabe", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 64;

		text = this.formToolkit.createText(composite, "", SWT.RIGHT);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(final ModifyEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

		});

		this.maxPriceAmount = new FormattedText(text);
		this.maxPriceAmount.setFormatter(new NumberFormatter("########0.00", "###,###,##0.00"));

		label = this.formToolkit.createLabel(composite, "Zahlungsbetrag bestätigen ab", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 64;

		text = this.formToolkit.createText(composite, "", SWT.RIGHT);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(final ModifyEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

		});

		this.maxPaymentRange = new FormattedText(text);
		this.maxPaymentRange.setFormatter(new NumberFormatter("########0.00", "###,###,##0.00"));

		label = this.formToolkit.createLabel(composite, "Maximaler Zahlungsbetrag", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 64;

		text = this.formToolkit.createText(composite, "", SWT.RIGHT);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(final ModifyEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

		});

		this.maxPaymentAmount = new FormattedText(text);
		this.maxPaymentAmount.setFormatter(new NumberFormatter("########0.00", "###,###,##0.00"));

		label = this.formToolkit.createLabel(composite, "Menge bestätigen ab", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 48;

		text = this.formToolkit.createText(composite, "", SWT.RIGHT);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(final ModifyEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

		});

		this.maxQuantityRange = new FormattedText(text);
		this.maxQuantityRange.setFormatter(new NumberFormatter("########0", "###,###,##0"));

		label = this.formToolkit.createLabel(composite, "Maximale Mengeneingabe", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 48;

		text = this.formToolkit.createText(composite, "", SWT.RIGHT);
		text.setLayoutData(gridData);
		text.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(final ModifyEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

		});

		this.maxQuantityAmount = new FormattedText(text);
		this.maxQuantityAmount.setFormatter(new NumberFormatter("########0", "###,###,##0"));

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillReceiptNumberFormatSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(2, false));

		final Label label = this.formToolkit.createLabel(composite, "Format Belegnummer");
		label.setLayoutData(new GridData());

		this.receiptNumberFormat = this.formToolkit.createText(composite, "");
		this.receiptNumberFormat.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.receiptNumberFormat.addModifyListener(new ModifyListener()
		{

			@Override
			public void modifyText(final ModifyEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillAddressSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(2, false));

		Label label = formToolkit.createLabel(composite, "Adresse");
		label.setLayoutData(new GridData());

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 100;

		address = formToolkit.createText(composite, "", SWT.MULTI | SWT.WRAP);
		address.setLayoutData(gridData);
		address.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				setDirty(true);
			}
		});

		label = formToolkit.createLabel(composite, "Steuernummer");
		label.setLayoutData(new GridData());

		taxNumber = formToolkit.createText(composite, "", SWT.MULTI | SWT.WRAP);
		taxNumber.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		taxNumber.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillSettingsSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout());

		maximizedClientWindow = formToolkit.createButton(composite, "Kassenfenster maximieren", SWT.CHECK);
		maximizedClientWindow.setLayoutData(new GridData());
		maximizedClientWindow.addSelectionListener(new SelectionListener() 
		{
			@Override
			public void widgetSelected(SelectionEvent e) 
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) 
			{
				widgetSelected(e);
			}
		});
		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillRegistrationSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout());

		final HostnameResolver[] resolvers = CommonSettings.HostnameResolver.values();
		this.hostnameResolvers = new Button[resolvers.length];
		for (int i = 0; i < resolvers.length; i++)
		{
			this.hostnameResolvers[i] = this.formToolkit.createButton(composite, resolvers[i].getLabel(), SWT.RADIO);
			this.hostnameResolvers[i].setData("resolver", resolvers[i]);
			this.hostnameResolvers[i].addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetDefaultSelected(final SelectionEvent e)
				{
					this.widgetSelected(e);
				}

				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					for (final Button hostnameResolver : GeneralSettingsEditor.this.hostnameResolvers)
					{
						if (!e.getSource().equals(hostnameResolver))
						{
							hostnameResolver.setSelection(false);
						}
					}
					GeneralSettingsEditor.this.setDirty(true);
				}
			});
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillTaxSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(3, false));

		this.taxInclusive = this.formToolkit.createButton(composite,
				"Mehrwertsteuerberechnungsbasis inkl. Mehrwertsteuer", SWT.CHECK);
		this.taxInclusive.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillTransferSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(2, false));

		Label label = this.formToolkit.createLabel(composite, "Startverzögerung (in Millisekunden)");
		label.setLayoutData(new GridData());
		
		GridData gridData = new GridData();
		gridData.widthHint = 64;
		
		this.transferDelay = new Spinner(composite, SWT.None);
		this.transferDelay.setLayoutData(gridData);
		this.transferDelay.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		this.transferDelay.setDigits(0);
		this.transferDelay.setIncrement(1000);
		this.transferDelay.setPageIncrement(10000);
		this.transferDelay.setMaximum(Integer.MAX_VALUE);
		this.transferDelay.setMinimum(0);
		this.transferDelay.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				
			}});
		this.transferDelay.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		formToolkit.adapt(transferDelay);
		
		label = this.formToolkit.createLabel(composite, "Wiederholungsverzögerung (in Millisekunden)");
		label.setLayoutData(new GridData());
		
		gridData = new GridData();
		gridData.widthHint = 64;
		
		this.transferRepeatDelay = new Spinner(composite, SWT.None);
		this.transferRepeatDelay.setLayoutData(gridData);
		this.transferRepeatDelay.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		this.transferRepeatDelay.setDigits(0);
		this.transferRepeatDelay.setIncrement(1000);
		this.transferRepeatDelay.setPageIncrement(10000);
		this.transferRepeatDelay.setMaximum(Integer.MAX_VALUE);
		this.transferRepeatDelay.setMinimum(0);
		this.transferRepeatDelay.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}
		});
		this.transferRepeatDelay.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		formToolkit.adapt(transferRepeatDelay);
		
		label = this.formToolkit.createLabel(composite, "Anzahl Belege pro Übertragungslauf");
		label.setLayoutData(new GridData());
		
		gridData = new GridData();
		gridData.widthHint = 32;
		
		this.transferReceiptCount = new Spinner(composite, SWT.None);
		this.transferReceiptCount.setLayoutData(gridData);
		this.transferReceiptCount.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		this.transferReceiptCount.setDigits(0);
		this.transferReceiptCount.setIncrement(1);
		this.transferReceiptCount.setPageIncrement(10);
		this.transferReceiptCount.setMaximum(Integer.MAX_VALUE);
		this.transferReceiptCount.setMinimum(0);
		this.transferReceiptCount.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				
			}});
		this.transferReceiptCount.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		formToolkit.adapt(transferReceiptCount);
		
		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillSettlementSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(3, false));

		this.allowTestSettlement = this.formToolkit.createButton(composite,
				"Provisorischen Abschluss erlauben", SWT.CHECK);
		this.allowTestSettlement.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				GeneralSettingsEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

}
