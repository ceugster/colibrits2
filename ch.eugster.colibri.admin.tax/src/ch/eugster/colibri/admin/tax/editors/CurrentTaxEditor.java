/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
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
import ch.eugster.colibri.persistence.model.CurrentTaxCodeMapping;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderConfigurator;

public class CurrentTaxEditor extends AbstractEntityEditor<CurrentTax>
{
	public static final String ID = "ch.eugster.colibri.admin.editors.currentTaxEditor";

	private final NumberFormat pf;

	private final NumberFormat nf;

	private DateTime validFrom;

	private Text percentage;

	private final Map<String, CurrentTaxCodeMapping> currentTaxCodeMappings = new HashMap<String, CurrentTaxCodeMapping>();

	private final Map<String, Text> mappingCodes = new HashMap<String, Text>();

	private final Map<String, Text> mappingAccounts = new HashMap<String, Text>();

	private ServiceTracker<ProviderConfigurator, ProviderConfigurator> providerConfiguratorTracker;

	public CurrentTaxEditor()
	{
		pf = NumberFormat.getPercentInstance();
		pf.setMaximumFractionDigits(3);
		pf.setMinimumFractionDigits(0);
		
		nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(0);

		this.providerConfiguratorTracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(TaxActivator.getDefault().getBundle().getBundleContext(),
				ProviderConfigurator.class, null);
		this.providerConfiguratorTracker.open();
	}

	@Override
	public void dispose()
	{
		this.providerConfiguratorTracker.close();

		EntityMediator.removeListener(CurrentTax.class, this);

		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof CurrentTax)
		{
			final CurrentTax currentTax = (CurrentTax) entity;
			if (currentTax.equals(this.getEditorInput().getAdapter(CurrentTax.class)))
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
		this.validFrom.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createSection(scrolledForm);

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker.getService();
		if (providerConfigurator != null)
		{
			final CurrentTax currentTax = (CurrentTax) this.getEditorInput().getAdapter(CurrentTax.class);

			if (providerConfigurator.canMap(currentTax))
			{
				this.createMappingSection(providerConfigurator.getProviderId(), scrolledForm);
			}
		}
		EntityMediator.addListener(CurrentTax.class, this);
	}

	// @Override
	// protected Message getMessage(final ErrorCode errorCode)
	// {
	// Message msg = null;
	//
	// if (validFrom.getDate() == null)
	// {
	// msg = new Message(validFrom, "Fehler");
	// msg.setMessage("Es muss ein Gültigkeitsdatum angegeben werden.");
	// }
	// return msg;
	// }

	@Override
	protected String getName()
	{
		final CurrentTax currentTax = (CurrentTax) this.getEditorInput().getAdapter(CurrentTax.class);
		return currentTax.format();
	}

	@Override
	protected String getText()
	{
		final CurrentTax currentTax = (CurrentTax) this.getEditorInput().getAdapter(CurrentTax.class);
		final Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
		calendar.setTimeInMillis(currentTax.getValidFrom().longValue());
		return "Aktuelle Mwst: " + currentTax.getTax().getCode() + " " + DateFormat.getDateInstance().format(calendar.getTime());
	}

	@Override
	protected void loadValues()
	{
		final CurrentTax currentTax = (CurrentTax) this.getEditorInput().getAdapter(CurrentTax.class);
		final Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
		calendar.setTimeInMillis(currentTax.getValidFrom().longValue());
		this.validFrom.setYear(calendar.get(Calendar.YEAR));
		this.validFrom.setMonth(calendar.get(Calendar.MONTH));
		this.validFrom.setDay(calendar.get(Calendar.DATE));
		
		this.percentage.setText(pf.format(currentTax.getPercentage()));

		final Collection<CurrentTaxCodeMapping> currentTaxCodeMappings = currentTax.getCurrentTaxCodeMappings();
		for (final CurrentTaxCodeMapping currentTaxCodeMapping : currentTaxCodeMappings)
		{
			this.currentTaxCodeMappings.put(currentTaxCodeMapping.getProvider(), currentTaxCodeMapping);
			this.mappingCodes.get(currentTaxCodeMapping.getProvider()).setText(currentTaxCodeMapping.getCode());
			this.mappingAccounts.get(currentTaxCodeMapping.getProvider()).setText(currentTaxCodeMapping.getAccount());
		}
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final CurrentTax currentTax = (CurrentTax) this.getEditorInput().getAdapter(CurrentTax.class);
		final Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.DATE, this.validFrom.getDay());
		calendar.set(Calendar.MONTH, this.validFrom.getMonth());
		calendar.set(Calendar.YEAR, this.validFrom.getYear());
		currentTax.setValidFrom(Long.valueOf(calendar.getTimeInMillis()));
		
		String value = this.percentage.getText();
		value = value.replace("%", "");
		value = value.replace(" ", "");
		double taxPercents = Double.parseDouble(value);
		if (this.percentage.isFocusControl())
		{
			taxPercents = taxPercents / 100;
		}
		currentTax.setPercentage(taxPercents);

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker.getService();
		if (providerConfigurator != null)
		{
			if (this.currentTaxCodeMappings.get(providerConfigurator.getProviderId()) == null)
			{
				if (this.mappingCodes.get(providerConfigurator.getProviderId()) != null)
				{
					if (!this.mappingCodes.get(providerConfigurator.getProviderId()).getText().isEmpty())
					{
						final CurrentTaxCodeMapping currentTaxCodeMapping = CurrentTaxCodeMapping.newInstance((CurrentTax) this.getEditorInput()
								.getAdapter(CurrentTax.class));
						currentTaxCodeMapping.setProvider(providerConfigurator.getProviderId());
						currentTaxCodeMapping.setCode(this.mappingCodes.get(currentTaxCodeMapping).getText());
						currentTaxCodeMapping.setAccount(this.mappingAccounts.get(currentTaxCodeMapping).getText());
						((CurrentTax) this.getEditorInput().getAdapter(CurrentTax.class)).addCurrentTaxCodeMapping(currentTaxCodeMapping);
					}
				}
			}
			else
			{
				for (final CurrentTaxCodeMapping mapping : currentTax.getCurrentTaxCodeMappings())
				{
					final CurrentTaxCodeMapping m = this.currentTaxCodeMappings.get(mapping.getProvider());
					if (m != null)
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
		}
	}

	@Override
	protected boolean validate()
	{
		return true;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<CurrentTax> input)
	{
		return input.getAdapter(CurrentTax.class) instanceof CurrentTax;
	}

	private Section createMappingSection(final String provider, final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText(provider.toString() + "-Mapping");
		section.setClient(this.fillMappingSection(provider, section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				CurrentTaxEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
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
		section.setText("Angaben zum Mehrwertsteuersatz");
		section.setClient(this.fillSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				CurrentTaxEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillMappingSection(final String provider, final Section parent)
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

		Text text = this.formToolkit.createText(composite, "");
		text.setLayoutData(layoutData);
		text.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				CurrentTaxEditor.this.setDirty(true);
			}
		});
		this.mappingCodes.put(provider, text);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Konto", SWT.NONE);
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
				CurrentTaxEditor.this.setDirty(true);
			}
		});
		this.mappingAccounts.put(provider, text);

		this.formToolkit.paintBordersFor(composite);

		return composite;
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

		Label label = this.formToolkit.createLabel(composite, "Gültig ab", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();

		this.validFrom = new DateTime(composite, SWT.DATE | SWT.MEDIUM);
		this.validFrom.setLayoutData(layoutData);
		this.validFrom.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		this.validFrom.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent event)
			{
				this.widgetSelected(event);
			}

			@Override
			public void widgetSelected(final SelectionEvent event)
			{
				CurrentTaxEditor.this.setDirty(true);
			}
		});
		this.formToolkit.adapt(this.validFrom);

		final CurrentTax currentTax = (CurrentTax) this.getEditorInput().getAdapter(CurrentTax.class);
//		TaxType[] types = new TaxType[0];

		if (currentTax.getId() != null)
		{
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
			if (persistenceService != null)
			{
				final PositionQuery query = (PositionQuery) persistenceService.getServerService().getQuery(Position.class);
				final long count = query.countByCurrentTax(currentTax);
				this.validFrom.setEnabled(count == 0l);

//				final TaxTypeQuery taxTypeQuery = (TaxTypeQuery) persistenceService.getServerService().getQuery(TaxType.class);
//				types = taxTypeQuery.selectAll(false).toArray(new TaxType[0]);
			}
		}
		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		label = this.formToolkit.createLabel(composite, "Prozentsatz", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		percentage = this.formToolkit.createText(composite, "");
		percentage.setLayoutData(layoutData);
		percentage.addFocusListener(new FocusListener() 
		{
			@Override
			public void focusGained(FocusEvent e) 
			{
				Text text = (Text) e.getSource();
				if (!text.getText().isEmpty())
				{
					String s = text.getText().replaceAll("%", "");
					double value = 0d;
					try
					{
						value = Double.parseDouble(s);
						text.setText(nf.format(new Double(value).doubleValue()));
					}
					catch (NumberFormatException nfe)
					{
					nfe.printStackTrace();	
					}
				}
			}

			@Override
			public void focusLost(FocusEvent e) 
			{
				Text text = (Text) e.getSource();
				if (!text.getText().isEmpty())
				{
					try
					{
						double value = Double.parseDouble(text.getText());
						if (value != 0d)
						{
							value = value / 100;
						}
						text.setText(pf.format(value));
					}
					catch (NumberFormatException nfe)
					{
						
					}
				}
			}});
		percentage.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent event)
			{
				CurrentTaxEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}
}
