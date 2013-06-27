/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
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
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.provider.service.ProviderConfigurator;

public class TaxEditor extends AbstractEntityEditor<Tax>
{
	public static final String ID = "ch.eugster.colibri.admin.editors.taxEditor";

	private Text text;

	private Text account;

	private ComboViewer currentTaxes;

	private final Map<String, TaxCodeMapping> taxCodeMappings = new HashMap<String, TaxCodeMapping>();

	private final Map<String, Text> mappingCodes = new HashMap<String, Text>();

	private final Map<String, Text> mappingAccounts = new HashMap<String, Text>();

	private final ServiceTracker<ProviderConfigurator, ProviderConfigurator> providerConfiguratorTracker;

	public TaxEditor()
	{
		this.providerConfiguratorTracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(TaxActivator.getDefault().getBundle().getBundleContext(),
				ProviderConfigurator.class, null);
		this.providerConfiguratorTracker.open();
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Tax.class, this);

		this.providerConfiguratorTracker.close();

		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof Tax)
		{
			final Tax tax = (Tax) entity;
			if (tax.equals(this.getEditorInput().getAdapter(Tax.class)))
			{
				this.dispose();
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
		this.account.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createMainSection(scrolledForm);

		final Tax tax = (Tax) this.getEditorInput().getAdapter(Tax.class);

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker
				.getService();
		if (providerConfigurator != null)
		{
			if (providerConfigurator.canMap(tax))
			{
				this.createMappingSection(scrolledForm);
			}
		}
		EntityMediator.addListener(Tax.class, this);
	}

	@Override
	protected String getName()
	{
		final Tax tax = (Tax) this.getEditorInput().getAdapter(Tax.class);
		return tax.format();
	}

	@Override
	protected String getText()
	{
		final Tax tax = (Tax) this.getEditorInput().getAdapter(Tax.class);
		return "Mehrwertsteuer: " + tax.format();
	}

	// @Override
	// protected Message getMessage(final ErrorCode errorCode)
	// {
	// Message msg = null;
	//
	// if (this.currentTaxes.getSelection().isEmpty())
	// {
	// msg = new Message(this.currentTaxes.getControl(), "Fehler");
	// msg.setMessage("Es muss ein gültiger Mehrwertsteuersatz ausgewählt sein.");
	// }
	// return msg;
	// }

	@Override
	protected void loadValues()
	{
		final Tax tax = (Tax) this.getEditorInput().getAdapter(Tax.class);
		this.text.setText(tax.getText());
		this.account.setText(tax.getAccount());
		this.currentTaxes.setSelection(new StructuredSelection(tax.getCurrentTax()));
		final Collection<TaxCodeMapping> taxCodeMappings = tax.getTaxCodeMappings();
		for (final TaxCodeMapping taxCodeMapping : taxCodeMappings)
		{
			this.taxCodeMappings.put(taxCodeMapping.getProvider(), taxCodeMapping);
			Text code = this.mappingCodes.get(taxCodeMapping.getProvider());
			if (code != null)
			{
				code.setText(taxCodeMapping.getCode());
			}
			Text account = this.mappingAccounts.get(taxCodeMapping.getProvider());
			if (account != null)
			{
				account.setText(taxCodeMapping.getAccount());
			}
		}
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final Tax tax = (Tax) this.getEditorInput().getAdapter(Tax.class);
		tax.setText(this.text.getText());
		tax.setAccount(this.account.getText());

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker
				.getService();
		if (providerConfigurator != null)
		{
			TaxCodeMapping mapping = this.taxCodeMappings.get(providerConfigurator.getProviderId());
			if (mapping == null)
			{
				if (this.mappingCodes.get(providerConfigurator.getProviderId()) != null)
				{
					if (!this.mappingCodes.get(providerConfigurator.getProviderId()).getText().isEmpty())
					{
						final TaxCodeMapping taxCodeMapping = TaxCodeMapping.newInstance((Tax) this.getEditorInput()
								.getAdapter(Tax.class));
						taxCodeMapping.setProvider(providerConfigurator.getProviderId());
						taxCodeMapping.setCode(this.mappingCodes.get(providerConfigurator.getProviderId()).getText());
						taxCodeMapping.setAccount(this.mappingAccounts.get(providerConfigurator.getProviderId())
								.getText());
						((Tax) this.getEditorInput().getAdapter(Tax.class)).addTaxCodeMapping(taxCodeMapping);
					}
				}
			}
			else
			{
				if (this.mappingCodes.get(mapping.getProvider()).getText().isEmpty())
				{
					mapping.setDeleted(true);
					mapping.setCode(null);
					mapping.setAccount(null);
				}
				else
				{
					mapping.setDeleted(false);
					mapping.setCode(this.mappingCodes.get(mapping.getProvider()).getText());
					mapping.setAccount(this.mappingAccounts.get(mapping.getProvider()).getText());
				}
			}
		}
	}

	@Override
	protected boolean validate()
	{
		if (this.currentTaxes.getSelection().isEmpty())
		{
			return false;
		}

		return true;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Tax> input)
	{
		return input.getAdapter(Tax.class) instanceof Tax;
	}

	private Section createMainSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Angaben zur Mehrwertsteuer");
		section.setClient(this.fillMainSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				TaxEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createMappingSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker
				.getService();
		if (providerConfigurator != null)
		{
			section.setText("Mapping " + providerConfigurator.getName());
			section.setClient(this.fillMappingSection(section));
			section.addExpansionListener(new ExpansionAdapter()
			{
				@Override
				public void expansionStateChanged(final ExpansionEvent e)
				{
					TaxEditor.this.scrolledForm.reflow(true);
				}
			});
		}
		return section;
	}

	private Control fillMainSection(final Section parent)
	{
		final Tax tax = (Tax) this.getEditorInput().getAdapter(Tax.class);

		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, false));

		Label label = this.formToolkit.createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(new GridData());

		this.text = this.formToolkit.createText(composite, "");
		this.text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.text.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				TaxEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Konto", SWT.NONE);
		label.setLayoutData(new GridData());

		this.account = this.formToolkit.createText(composite, "");
		this.account.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.account.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				TaxEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Aktueller Steuersatz", SWT.NONE);
		label.setLayoutData(new GridData());

		final CCombo combo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.FLAT);
		combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		combo.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				TaxEditor.this.setDirty(true);
			}
		});

		final CurrentTax[] currentTaxes = tax.getCurrentTaxes().toArray(new CurrentTax[0]);

		this.currentTaxes = new ComboViewer(combo);
		this.currentTaxes.setContentProvider(new CurrentTaxContentProvider());
		this.currentTaxes.setLabelProvider(new CurrentTaxLabelProvider());
		this.currentTaxes.setSorter(new CurrentTaxSorter());
		this.currentTaxes.setInput(currentTaxes);

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillMappingSection(final Section parent)
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

		Text code = this.formToolkit.createText(composite, "");
		code.setLayoutData(layoutData);
		code.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				TaxEditor.this.setDirty(true);
			}
		});

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Konto", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		Text account = this.formToolkit.createText(composite, "");
		account.setLayoutData(layoutData);
		account.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				TaxEditor.this.setDirty(true);
			}
		});

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker
				.getService();
		if (providerConfigurator != null)
		{
			this.mappingCodes.put(providerConfigurator.getProviderId(), code);
			this.mappingAccounts.put(providerConfigurator.getProviderId(), account);
		}
		this.formToolkit.paintBordersFor(composite);

		return composite;
	}
}
