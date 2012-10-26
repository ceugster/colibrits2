/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
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
import org.eclipse.ui.progress.UIJob;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderIdService;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class ProductEditor extends AbstractEntityEditor<ProductGroup>
{
	public static final String ID = "ch.eugster.colibri.admin.product.editor";

	private Text code;

	private Text name;

	private Text mappingId;

	private FormattedText quantityProposal;

	private FormattedText priceProposal;

	private ComboViewer options;

	private ComboViewer paymentTypes;

	private Text account;

	private ComboViewer defaultTax;

	private final HashMap<String, ComboViewer> mappingViewers = new HashMap<String, ComboViewer>();

	private final ServiceTracker<ProviderIdService, ProviderIdService> providerTracker;

	private boolean checkModifyEvent = false;
	
	protected void updateControls()
	{
		this.checkModifyEvent = false;
		super.updateControls();
		ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);
		for(ComboViewer viewer : mappingViewers.values())
		{
			String providerId = (String) viewer.getData("providerId");
			viewer.setInput(getExternalProductGroupViewerInput(providerId).toArray(new ExternalProductGroup[0]));
			Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(providerId);
			for (ProductGroupMapping mapping : mappings)
			{
				if (!mapping.isDeleted())
				{
					viewer.setSelection(new StructuredSelection(new ExternalProductGroup[] { mapping.getExternalProductGroup() }));
				}
			}
		}
		this.checkModifyEvent = true;
	}
	
	public ProductEditor()
	{
		EntityMediator.addListener(ProductGroup.class, this);
		EntityMediator.addListener(ExternalProductGroup.class, this);
		EntityMediator.addListener(ProductGroupMapping.class, this);

		this.providerTracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderIdService.class, null);
		this.providerTracker.open();
	}

	@Override
	public void dispose()
	{
		this.providerTracker.close();

		EntityMediator.removeListener(ProductGroupMapping.class, this);
		EntityMediator.removeListener(ProductGroup.class, this);
		EntityMediator.removeListener(ExternalProductGroup.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity.equals(this.getEditorInput().getAdapter(ProductGroup.class)))
		{
			UIJob job = new UIJob("Der Warengruppeneditor wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					dispose();
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof ExternalProductGroup)
		{
			UIJob job = new UIJob("Der Warengruppeneditor wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					ExternalProductGroup externalProductGroup = (ExternalProductGroup) entity;
					ComboViewer mappingViewer = mappingViewers.get(externalProductGroup.getProvider());
					if (mappingViewer != null)
					{
						mappingViewer.remove(externalProductGroup);
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof ProductGroupMapping)
		{
			UIJob job = new UIJob("Der Warengruppeneditor wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					ProductGroupMapping mapping = (ProductGroupMapping) entity;
					ComboViewer mappingViewer = mappingViewers.get(mapping.getExternalProductGroup().getProvider());
					if (mappingViewer != null)
					{
						if (!mappingViewer.getSelection().isEmpty())
						{
							StructuredSelection ssel = (StructuredSelection) mappingViewer.getSelection();
							ExternalProductGroup externalProductGroup = (ExternalProductGroup) ssel.getFirstElement();
							if (externalProductGroup.getId().equals(mapping.getExternalProductGroup().getId()))
							{
								mappingViewer.setSelection(new StructuredSelection());
							}
						}
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}
	
	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (entity instanceof ExternalProductGroup)
		{
			UIJob job = new UIJob("Der Warengruppeneditor wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					ExternalProductGroup externalProductGroup = (ExternalProductGroup) entity;
					ComboViewer mappingViewer = mappingViewers.get(externalProductGroup.getProvider());
					if (mappingViewer != null)
					{
						mappingViewer.add(externalProductGroup);
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof ProductGroupMapping)
		{
			UIJob job = new UIJob("Der Warengruppeneditor wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					ProductGroupMapping productGroupMapping = (ProductGroupMapping) entity;
					ComboViewer mappingViewer = mappingViewers.get(productGroupMapping.getExternalProductGroup().getProvider());
					if (mappingViewer != null)
					{
						if (!productGroupMapping.getProductGroup().getProductGroupMappings().contains(productGroupMapping))
						{
							productGroupMapping.getProductGroup().addProductGroupMapping(productGroupMapping);
						}
						mappingViewer.refresh(productGroupMapping.getProductGroup());
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (entity instanceof ProductGroup)
		{
			final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);
			if (productGroup.getId().equals(entity.getId()))
			{
				UIJob job = new UIJob("Der Warengruppeneditor wird aktualisiert...")
				{
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor)
					{
						ProductGroup pg = (ProductGroup) entity;
						if (!pg.getProductGroupType().equals(productGroup.getProductGroupType()))
						{
							productGroup.setProductGroupType(pg.getProductGroupType());
						}
						return Status.OK_STATUS;
					}
				};
				job.setUser(true);
				job.schedule();
			}
		}
		else if (entity instanceof ExternalProductGroup)
		{
			UIJob job = new UIJob("Der Warengruppeneditor wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					ExternalProductGroup externalProductGroup = (ExternalProductGroup) entity;
					ComboViewer mappingViewer = mappingViewers.get(externalProductGroup.getProvider());
					if (mappingViewer != null)
					{
						mappingViewer.refresh(externalProductGroup);
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
		}
		else if (entity instanceof ProductGroupMapping)
		{
			UIJob job = new UIJob("Der Warengruppeneditor wird aktualisiert...")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor)
				{
					ProductGroupMapping mapping = (ProductGroupMapping) entity;
					ComboViewer mappingViewer = mappingViewers.get(mapping.getExternalProductGroup().getProvider());
					if (mappingViewer != null)
					{
						mappingViewer.refresh(mapping.getExternalProductGroup());
					}
					return Status.OK_STATUS;
				}
			};
			job.setUser(true);
			job.schedule();
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
		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);

		this.createDescriptionSection(scrolledForm);
		if (!productGroup.getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL))
		{
			this.createDefaultSection(scrolledForm);
		}
		this.createFinanceSection(scrolledForm);

		if (productGroup.getProductGroupType().equals(ProductGroupType.SALES_RELATED))
		{
			final ProviderIdService providerService = (ProviderIdService) this.providerTracker.getService();
			if (providerService != null)
			{
				this.createMappingSection(scrolledForm, providerService);
			}
		}
	}

	@Override
	protected String getName()
	{
		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);
		if (productGroup.getId() == null)
		{
			return "Neu";
		}
		return productGroup.getName();
	}

	@Override
	protected String getText()
	{
		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);
		return productGroup.getProductGroupType().toString();
	}

	@Override
	protected void loadValues()
	{
		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);

		this.code.setText(productGroup.getCode());
		this.name.setText(productGroup.getName());
		this.mappingId.setText(productGroup.getMappingId());
		this.account.setText(productGroup.getAccount());

		if (productGroup.getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL))
		{
			if (productGroup.getPaymentType() != null)
			{
				this.paymentTypes.setSelection(new StructuredSelection(productGroup.getPaymentType()));
			}
		}
		else
		{
			this.quantityProposal.setValue(productGroup.getQuantityProposal());
			this.priceProposal.setValue(productGroup.getPriceProposal());
			if (this.options != null)
			{
				Option[] options = null;
				Option proposalOption = productGroup.getProposalOption();
				if (proposalOption != null && proposalOption.equals(Option.PAYED_INVOICE))
				{
					options = new Option[] { Option.PAYED_INVOICE };
				}
				else
				{
					options = productGroup.getProductGroupType().getOptions();
				}
				if (options.length > 0)
				{
					this.options.setInput(options);
				}

				Position.Option option = productGroup.getProposalOption();
				if (option == null)
				{
					option = productGroup.getProductGroupType().getOptions()[0];
				}
				this.options.setSelection(new StructuredSelection(option));
				if (productGroup.getProposalOption().equals(Option.PAYED_INVOICE))
				{
					this.options.getCCombo().setEnabled(false);
				}
			}

			if ((productGroup.getDefaultTax() != null) && (this.defaultTax != null))
			{
				final Tax[] defaultTaxes = new Tax[] { productGroup.getDefaultTax() };
				this.defaultTax.setSelection(new StructuredSelection(defaultTaxes));
			}
		}

		final Collection<ProductGroupMapping> productGroupMappings = productGroup.getProductGroupMappings();
		for (final ProductGroupMapping productGroupMapping : productGroupMappings)
		{
			if (productGroupMapping.getProductGroup().getId().equals(productGroup.getId())
					&& !productGroupMapping.isDeleted())
			{
				final ComboViewer viewer = this.mappingViewers.get(productGroupMapping.getExternalProductGroup()
						.getProvider());
				if (viewer != null)
				{
					viewer.setSelection(new StructuredSelection(productGroupMapping.getExternalProductGroup()));
				}
			}
		}

		this.setDirty(false);
		this.checkModifyEvent = true;
	}

	@Override
	protected void saveValues()
	{
		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);

		productGroup.setCode(this.code.getText());
		productGroup.setName(this.name.getText());
		productGroup.setMappingId(this.mappingId.getText());
		productGroup.setAccount(this.account.getText());

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			if (productGroup.getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL))
			{
				final StructuredSelection ssel = (StructuredSelection) this.paymentTypes.getSelection();
				productGroup.setPaymentType((PaymentType) ssel.getFirstElement());

				final TaxQuery query = (TaxQuery) persistenceService.getServerService().getQuery(Tax.class);
				final TaxType taxType = (TaxType) persistenceService.getServerService().find(TaxType.class,
						Long.valueOf(1L));
				final TaxRate taxRate = (TaxRate) persistenceService.getServerService().find(TaxRate.class,
						Long.valueOf(1L));
				final Collection<Tax> taxes = query.selectByTaxTypeAndTaxRate(taxType, taxRate);
				if (!taxes.isEmpty())
				{
					productGroup.setDefaultTax(taxes.iterator().next());
				}

				productGroup.setPriceProposal(0D);
				productGroup.setQuantityProposal(1);
			}
			else
			{
				final PaymentType paymentType = (PaymentType) persistenceService.getServerService().find(
						PaymentType.class, Long.valueOf(1L));
				productGroup.setPaymentType(paymentType);

				String value = this.quantityProposal.getControl().getText();
				productGroup.setQuantityProposal(Integer.parseInt(value));

				value = this.priceProposal.getControl().getText();
				productGroup.setPriceProposal(Double.parseDouble(value));

				if (this.options != null)
				{
					final StructuredSelection ssel = (StructuredSelection) this.options.getSelection();
					productGroup.setProposalOption((Option) ssel.getFirstElement());
				}

				if (this.defaultTax != null)
				{
					final StructuredSelection ssel = (StructuredSelection) this.defaultTax.getSelection();
					productGroup.setDefaultTax((Tax) ssel.getFirstElement());
				}
			}
		}

		final Set<String> providers = this.mappingViewers.keySet();
		for (final String provider : providers)
		{
			final ComboViewer viewer = this.mappingViewers.get(provider);
			if (viewer != null)
			{
				StructuredSelection ssel = (StructuredSelection) viewer.getSelection();
				if (ssel.isEmpty())
				{
					String code = viewer.getCCombo().getText();
					if (!code.isEmpty())
					{
						Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(provider);
						for (ProductGroupMapping mapping : mappings)
						{
							mapping.setDeleted(true);
						}
						ExternalProductGroup externalProductGroup = ExternalProductGroup.newInstance(provider);
						externalProductGroup.setCode(code);
						ProductGroupMapping mapping = ProductGroupMapping.newInstance(productGroup, externalProductGroup);
						mapping.setProvider(provider);
						externalProductGroup.setProductGroupMapping(mapping);
						productGroup.addProductGroupMapping(mapping);
					}
				}
				else
				{
					ExternalProductGroup externalProductGroup = (ExternalProductGroup) ssel.getFirstElement();
					if (externalProductGroup.getId() == null)
					{
						Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(provider);
						for (ProductGroupMapping mapping : mappings)
						{
							mapping.setDeleted(true);
						}
					}
					else
					{
						Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(provider);
						boolean found = false;
						for (ProductGroupMapping mapping : mappings)
						{
							if (mapping.getExternalProductGroup().getId().equals(externalProductGroup.getId()))
							{
								if (mapping.isDeleted())
								{
									mapping.setDeleted(false);
									found = true;
								}
							}
							else
							{
								mapping.setDeleted(true);
							}
						}
						if (!found)
						{
							ProductGroupMapping mapping = ProductGroupMapping.newInstance(productGroup,
										externalProductGroup);
							mapping.setProvider(provider);
							externalProductGroup.setProductGroupMapping(mapping);
							productGroup.addProductGroupMapping(mapping);
						}
					}
				}
			}
		}
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

		if (msg == null)
		{
			msg = this.getUniqueNameMessage();
		}

		if (msg == null)
		{
			msg = this.getEmptyTaxSelectionMessage();
		}

		if (msg == null)
		{
			msg = this.getEmptyCurrencySelectionMessage();
		}

		if (msg == null)
		{
			msg = this.getValidExternalProductGroupMessage();
		}
		
		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<ProductGroup> input)
	{
		return input.getAdapter(ProductGroup.class) instanceof ProductGroup;
	}

	private Section createDefaultSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Vorschlagsdaten");
		section.setClient(this.fillDefaultSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProductEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createDescriptionSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Beschreibung");
		section.setClient(this.fillDescriptionSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProductEditor.this.scrolledForm.reflow(true);
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

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Finanzen");
		section.setClient(this.fillFinanceSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProductEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createMappingSection(final ScrolledForm scrolledForm,
			final ProviderIdService providerService)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText(providerService.getProviderLabel());
		section.setClient(this.fillMappingSection(section, providerService));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProductEditor.this.scrolledForm.reflow(true);
			}
		});
		return section;
	}

	private Control fillDefaultSection(final Section parent)
	{
		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);

		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, false));

		Label label = this.formToolkit.createLabel(composite, "Vorschlag Menge", SWT.NONE);
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
				ProductEditor.this.setDirty(true);
			}
		});
		this.quantityProposal = new FormattedText(text);
		this.quantityProposal.setFormatter(new NumberFormatter("#####0"));

		label = this.formToolkit.createLabel(composite, "Vorschlag Preis", SWT.NONE);
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
				ProductEditor.this.setDirty(true);
			}
		});
		this.priceProposal = new FormattedText(text);
		this.priceProposal.setFormatter(new NumberFormatter("########0.00"));

		final Option[] options = productGroup.getProductGroupType().getOptions();
		if (options.length > 0)
		{
			label = this.formToolkit.createLabel(composite, "Vorschlag Option", SWT.NONE);
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
					ProductEditor.this.setDirty(true);
				}
			});
			this.formToolkit.adapt(combo);

			this.options = new ComboViewer(combo);
			this.options.setContentProvider(new OptionContentProvider());
			this.options.setLabelProvider(new OptionLabelProvider());
		}

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final TaxTypeQuery query = (TaxTypeQuery) persistenceService.getServerService().getQuery(TaxType.class);
			final Tax[] taxes = query.selectTaxes(productGroup.getProductGroupType()).toArray(new Tax[0]);
			if (taxes.length > 0)
			{
				label = this.formToolkit.createLabel(composite, "Vorschlag Mehrwertsteuer", SWT.NONE);
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
						ProductEditor.this.setDirty(true);
					}
				});

				this.defaultTax = new ComboViewer(combo);
				this.defaultTax.setContentProvider(new TaxContentProvider());
				this.defaultTax.setLabelProvider(new TaxLabelProvider());
				this.defaultTax.setSorter(new TaxSorter());
				this.defaultTax.setInput(taxes);
			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillDescriptionSection(final Section parent)
	{
		final GridLayout layout = new GridLayout(2, false);

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		Label label = this.formToolkit.createLabel(composite, "Beschriftung", SWT.NONE);
		label.setLayoutData(new GridData());

		this.code = this.formToolkit.createText(composite, "");
		this.code.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.code.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				if (ProductEditor.this.name.getText().isEmpty()
						|| ProductEditor.this.name.getText().equals(
								ProductEditor.this.code.getText().substring(0,
										ProductEditor.this.code.getText().length() - 1)))
				{
					ProductEditor.this.name.setText(ProductEditor.this.code.getText());
				}
				ProductEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(new GridData());

		this.name = this.formToolkit.createText(composite, "");
		this.name.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.name.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ProductEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Export Id", SWT.NONE);
		label.setLayoutData(new GridData());

		GridData gridData = new GridData();
		gridData.widthHint = 64;

		this.mappingId = this.formToolkit.createText(composite, "");
		this.mappingId.setLayoutData(gridData);
		this.mappingId.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ProductEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillFinanceSection(final Section parent)
	{
		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);

		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, false));

		if (productGroup.getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL))
		{
			final Label label = this.formToolkit.createLabel(composite, "Währung", SWT.NONE);
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
					ProductEditor.this.setDirty(true);
				}
			});
			this.formToolkit.adapt(combo);

			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker
					.getService();
			if (persistenceService != null)
			{
				final ProductGroupQuery query = (ProductGroupQuery) persistenceService.getServerService().getQuery(
						ProductGroup.class);
				final Collection<ProductGroup> pgs = query.selectByProductGroupType(productGroup.getProductGroupType());
				final PaymentTypeQuery paymentTypeQuery = (PaymentTypeQuery) persistenceService.getServerService()
						.getQuery(PaymentType.class);
				final Collection<PaymentType> paymentTypes = paymentTypeQuery.selectByChange(true);
				for (final ProductGroup pg : pgs)
				{
					if (pg.getPaymentType() != null)
					{
						if ((productGroup.getId() == null) || !productGroup.getId().equals(pg.getId()))
						{
							paymentTypes.remove(pg.getPaymentType());
						}
					}
				}
				this.paymentTypes = new ComboViewer(combo);
				this.paymentTypes.setContentProvider(new PaymentTypeContentProvider());
				this.paymentTypes.setLabelProvider(new PaymentTypeLabelProvider());
				this.paymentTypes.setSorter(new PaymentTypeSorter());
				this.paymentTypes.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter() });
				this.paymentTypes.setInput(paymentTypes.toArray(new PaymentType[0]));
			}
		}

		final Label label = this.formToolkit.createLabel(composite, "Konto", SWT.NONE);
		label.setLayoutData(new GridData());

		this.account = this.formToolkit.createText(composite, "");
		this.account.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.account.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				ProductEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillMappingSection(final Section parent, final ProviderIdService providerService)
	{
		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, false));

		final Label label = this.formToolkit.createLabel(composite, "Externe Warengruppe", SWT.NONE);
		label.setLayoutData(new GridData());

		final CCombo combo = new CCombo(composite, SWT.FLAT);
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
				ProductEditor.this.setDirty(true);
			}
		});
		ControlDecoration decoration = new ControlDecoration(combo, SWT.TOP | SWT.LEFT);
		decoration.setShowOnlyOnFocus(false);
		combo.setData("decoration", decoration);
		combo.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				if (checkModifyEvent)
				{
					ComboViewer viewer = (ComboViewer) combo.getData("viewer");
					ControlDecoration decoration = (ControlDecoration) combo.getData("decoration");
					if (viewer != null)
					{
						String code = combo.getText();
						ExternalProductGroup[] externalProductGroups = (ExternalProductGroup[]) viewer.getInput();
						if (externalProductGroups != null)
						{
							for (ExternalProductGroup externalProductGroup : externalProductGroups)
							{
								if (externalProductGroup.getCode().equals(code)) 
								{
									viewer.setSelection(new StructuredSelection(new ExternalProductGroup[] { externalProductGroup }));
									return;
								}
								String providerId = (String) viewer.getData("providerId");
								Collection<ExternalProductGroup> mappedProductGroups = selectMappedExternalProductGroups(providerId);
								Image image = null;
								String text = "";
								for (ExternalProductGroup mappedProductGroup : mappedProductGroups)
								{
									if (mappedProductGroup.getCode().equals(code)) 
									{
										image = FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage();
										text = "Die gewählte externe Warengruppe wird bereits verwendet.";
									}
								}
								decoration.setImage(image);
								decoration.setDescriptionText(text);
							}
						}
						setDirty(true);
					}
				}
			}
		});
		
		final ComboViewer viewer = new ComboViewer(combo);
		viewer.setContentProvider(new ExternalProductGroupContentProvider());
		viewer.setLabelProvider(new ExternalProductGroupLabelProvider());
		viewer.setFilters(new ViewerFilter[] { new DeletedEntityViewerFilter(), new ViewerFilter() 
		{
			@Override
			public boolean select(Viewer viewer, Object parentElement,
					Object element) 
			{
				if (element instanceof ExternalProductGroup)
				{
					ProductGroup productGroup = (ProductGroup) getEditorInput().getAdapter(ProductGroup.class);
					ProductGroupMapping mapping = ((ExternalProductGroup) element).getProductGroupMapping();
					return mapping == null || mapping.getProductGroup().getId().equals(productGroup.getId());
				}
				return true;
			}
		}});
		viewer.setSorter(new ExternalProductGroupSorter());
		viewer.setData("providerId", providerService.getProviderId());
		combo.setData("viewer", viewer);
		
		this.mappingViewers.put(providerService.getProviderId(), viewer);

		viewer.setInput(getExternalProductGroupViewerInput(providerService.getProviderId()).toArray(new ExternalProductGroup[0]));

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Collection<ExternalProductGroup> getExternalProductGroupViewerInput(String providerId)
	{
		Collection<ExternalProductGroup> externalProductGroups = new ArrayList<ExternalProductGroup>();
		externalProductGroups.add(ExternalProductGroup.newInstance(providerId));
		externalProductGroups.addAll(externalProductGroups = selectUnmappedExternalProductGroups(providerId));

		ProductEditorInput input = (ProductEditorInput) this.getEditorInput();
		ProductGroup productGroup = input.getEntity();
		Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(providerId);
		for (ProductGroupMapping mapping : mappings)
		{
			if (!mapping.isDeleted())
			{
				if (!externalProductGroups.contains(mapping.getExternalProductGroup()))
				{
					externalProductGroups.add(mapping.getExternalProductGroup());
				}
			}
		}
		return externalProductGroups;
	}
	
	private Message getEmptyCodeMessage()
	{
		Message msg = null;

		if (this.code.getText().isEmpty())
		{
			msg = new Message(this.name, "Fehler");
			msg.setMessage("Die Warengruppe muss einen Code haben.");
		}
		return msg;
	}

	private Message getEmptyCurrencySelectionMessage()
	{
		Message msg = null;

		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);
		if (productGroup.getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL))
		{
			if (this.paymentTypes.getSelection().isEmpty())
			{
				msg = new Message(this.paymentTypes.getControl(), "Fehler");
				msg.setMessage("Bei Geldeinlagen und -entnahmen muss eine Währung angegeben werden.");
			}
		}
		return msg;
	}

	private Message getEmptyNameMessage()
	{
		Message msg = null;

		if (this.name.getText().isEmpty())
		{
			msg = new Message(this.name, "Fehler");
			msg.setMessage("Die Warengruppe muss eine Bezeichnung haben.");
		}
		return msg;
	}

	private Message getEmptyTaxSelectionMessage()
	{
		Message msg = null;
		if (this.defaultTax != null)
		{
			if (this.defaultTax.getSelection().isEmpty())
			{
				msg = new Message(this.defaultTax.getControl(), "Fehler");
				msg.setMessage("Der Warengruppe muss eine Mehrwertsteuer zugeordnet werden.");
			}
		}
		return msg;
	}

	private Message getValidExternalProductGroupMessage()
	{
		Message msg = null;
		if (mappingViewers != null)
		{
			Collection<ComboViewer> viewers = mappingViewers.values();
			for (ComboViewer viewer : viewers)
			{
				String providerId = (String) viewer.getData("providerId");
				StructuredSelection ssel = (StructuredSelection) viewer.getSelection();
				if (ssel.isEmpty())
				{
					CCombo combo = viewer.getCCombo();
					String text = combo.getText();
					Collection<ExternalProductGroup> mappedProductGroups = this.selectMappedExternalProductGroups(providerId);
					for (ExternalProductGroup mappedProductGroup : mappedProductGroups)
					{
						if (mappedProductGroup.getCode().equals(text)) 
						{
							ControlDecoration decoration = (ControlDecoration) viewer.getCCombo().getData("decoration");
							decoration.setImage(FieldDecorationRegistry.getDefault().getFieldDecoration(FieldDecorationRegistry.DEC_ERROR).getImage());
							decoration.setDescriptionText("Die gewählte externe Warengruppe wird bereits verwendet.");
							msg = new Message(viewer.getCCombo(), "Fehler");
							msg.setMessage("Die gewählte externe Warengruppe " + viewer.getCCombo().getText() + " wird bereits verwendet.");
							return msg;
						}
					}
				}
			}
		}
		return msg;
	}

	private Message getUniqueCodeMessage()
	{
		Message msg = null;

		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);

		final String code = this.code.getText();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ProductGroupQuery queryService = (ProductGroupQuery) persistenceService.getServerService().getQuery(
					ProductGroup.class);
			if (!queryService.isCodeUnique(code, productGroup.getId()))
			{
				msg = new Message(this.name, "Fehler");
				msg.setMessage("De gewählte Code wird bereits verwendet.");
				return msg;
			}
		}
		return msg;
	}

	private Message getUniqueNameMessage()
	{
		Message msg = null;

		final ProductGroup productGroup = (ProductGroup) this.getEditorInput().getAdapter(ProductGroup.class);

		final String name = this.name.getText();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ProductGroupQuery queryService = (ProductGroupQuery) persistenceService.getServerService().getQuery(
					ProductGroup.class);
			if (queryService != null)
			{
				if (!queryService.isNameUnique(name, productGroup.getId()))
				{
					msg = new Message(this.name, "Fehler");
					msg.setMessage("Die gewählte Bezeichnung wird bereits verwendet.");
					return msg;
				}
			}
		}
		return msg;
	}
	
	private Collection<ExternalProductGroup> selectMappedExternalProductGroups(String providerId)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker
		.getService();
		if (persistenceService != null)
		{
			final ExternalProductGroupQuery query = (ExternalProductGroupQuery) persistenceService
				.getServerService().getQuery(ExternalProductGroup.class);
			return query.selectMapped(providerId);
		}
		return new ArrayList<ExternalProductGroup>();
	}

	private Collection<ExternalProductGroup> selectUnmappedExternalProductGroups(String providerId)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker
		.getService();
		if (persistenceService != null)
		{
			final ExternalProductGroupQuery query = (ExternalProductGroupQuery) persistenceService
				.getServerService().getQuery(ExternalProductGroup.class);
			return query.selectUnmapped(providerId);
		}
		return new ArrayList<ExternalProductGroup>();
	}

}
