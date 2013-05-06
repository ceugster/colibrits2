/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.editors;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.widgets.formattedtext.FormattedText;
import org.eclipse.nebula.widgets.formattedtext.NumberFormatter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.salespoint.Activator;
import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.CommonSettings.HostnameResolver;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.CustomerDisplaySettingsQuery;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.ProfileQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.ReceiptPrinterSettingsQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.service.ProviderConfigurator;

public class SalespointEditor extends AbstractEntityEditor<Salespoint>
{
	public static final String ID = "ch.eugster.colibri.admin.salespoint.editor";

	private Text name;

	private Text host;

	private Hyperlink setHost;

	private Text location;

	private ComboViewer profiles;

	private ComboViewer paymentTypes;

	private Text mappingId;

	private FormattedText quantity;

	private FormattedText price;

	private Button forceSettlement;

	private ComboViewer taxes;

	private ComboViewer receiptPrinterViewer;

	private Text receiptPrinterPort;

	private Text receiptPrinterConverter;

	private Spinner receiptPrinterCols;

	private Spinner receiptPrinterLinesBeforeCut;

	private ComboViewer customerDisplayViewer;

	private Text customerDisplayPort;

	private Text customerDisplayConverter;

	private Spinner customerDisplayCols;

	private Spinner customerDisplayRows;

//	private Spinner customerDisplayDelay;
	
	private Button useIndividualExport;
	
	private Button export;
	
	private Text exportPath;

	private Button exportSelector;
	
	private Button useSalespointSpecificProviderProperties;

	private IProperty[] providerProperties;

	private Map<String, ProviderProperty> salespointProviderProperties;

	private Map<String, ProviderProperty> defaultProviderProperties;

	private final Map<String, Control> providerPropertyControls = new HashMap<String, Control>();

	private ScrolledForm scrolledForm;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private final ServiceTracker<ProviderConfigurator, ProviderConfigurator> providerConfiguratorTracker;

	private final ServiceTracker<CustomerDisplayService, CustomerDisplayService> customerDisplayServiceTracker;

	private final ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptPrinterServiceTracker;

	public SalespointEditor()
	{
		final BundleContext context = Activator.getDefault().getBundle().getBundleContext();

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(context, PersistenceService.class, null);
		this.persistenceServiceTracker.open();
		this.providerConfiguratorTracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(context, ProviderConfigurator.class, null);
		this.providerConfiguratorTracker.open();
		this.receiptPrinterServiceTracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(context, ReceiptPrinterService.class, null);
		this.receiptPrinterServiceTracker.open();
		this.customerDisplayServiceTracker = new ServiceTracker<CustomerDisplayService, CustomerDisplayService>(context, CustomerDisplayService.class, null);
		this.customerDisplayServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Salespoint.class, this);
		EntityMediator.removeListener(ReceiptPrinterSettings.class, this);
		EntityMediator.removeListener(CustomerDisplaySettings.class, this);
		EntityMediator.removeListener(Profile.class, this);
		
		this.receiptPrinterServiceTracker.close();
		this.customerDisplayServiceTracker.close();
		this.providerConfiguratorTracker.close();
		this.persistenceServiceTracker.close();

		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof Salespoint)
		{
			final Salespoint salespoint = (Salespoint) entity;
			if (salespoint.equals(this.getEditorInput().getAdapter(Salespoint.class)))
			{
				this.dispose();
			}
		}
		else if (entity instanceof Profile)
		{
			this.profiles.remove(entity);
		}
		if (entity instanceof Tax)
		{
			this.taxes.remove(entity);
		}
		else if (entity instanceof ReceiptPrinterSettings)
		{
			this.receiptPrinterViewer.remove(entity);
		}
		else if (entity instanceof CustomerDisplaySettings)
		{
			this.customerDisplayViewer.remove(entity);
		}
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (entity instanceof Profile)
		{
			this.profiles.refresh(entity);
		}
		if (entity instanceof Tax)
		{
			this.taxes.refresh(entity);
		}
		else if (entity instanceof ReceiptPrinterSettings)
		{
			this.receiptPrinterViewer.refresh(entity);
		}
		else if (entity instanceof CustomerDisplaySettings)
		{
			this.customerDisplayViewer.refresh(entity);
		}
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
		if (entity instanceof Profile)
		{
			this.profiles.add(entity);
		}
		if (entity instanceof Tax)
		{
			this.taxes.add(entity);
		}
		else if (entity instanceof CustomerDisplaySettings)
		{
			this.customerDisplayViewer.add(entity);
		}
		else if (entity instanceof ReceiptPrinterSettings)
		{
			this.receiptPrinterViewer.add(entity);
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
		this.name.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.scrolledForm = scrolledForm;

		this.createSalespointSection(scrolledForm);
		this.createProviderSection(scrolledForm);
		this.createProfileAndCurrencySection(scrolledForm);
		this.createProposalSection(scrolledForm);
		
		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (Bundle bundle : bundles)
		{
			if (bundle.getSymbolicName().equals("ch.eugster.colibri.export"))
			{
				this.createExportSection(scrolledForm);
				break;
			}
		}
		
		if ((this.receiptPrinterServiceTracker.getServices() != null)
				&& (this.receiptPrinterServiceTracker.getServices().length > 0))
		{
			this.createReceiptPrinterPeripherySection(scrolledForm);
		}
		if ((this.customerDisplayServiceTracker.getServices() != null)
				&& (this.customerDisplayServiceTracker.getServices().length > 0))
		{
			this.createCustomerDisplayPeripherySection(scrolledForm);
		}

		EntityMediator.addListener(Profile.class, this);
		EntityMediator.addListener(Salespoint.class, this);
		EntityMediator.addListener(ReceiptPrinterSettings.class, this);
		EntityMediator.addListener(CustomerDisplaySettings.class, this);
	}

	// @Override
	// protected Message getMessage(final ErrorCode errorCode)
	// {
	// Message msg = null;
	//
	// if (errorCode.equals("XX"))
	// {
	// msg = getUniqueNameMessage();
	// }
	// return msg;
	// }

	@Override
	protected String getName()
	{
		final Salespoint salespoint = (Salespoint) ((SalespointEditorInput) this.getEditorInput())
				.getAdapter(Salespoint.class);
		if (salespoint.getId() == null)
		{
			return "Neu";
		}
		return salespoint.getName();
	}

	@Override
	protected String getText()
	{
		return "Kasse";
	}

	@Override
	protected void loadValues()
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();

		final Salespoint salespoint = (Salespoint) ((SalespointEditorInput) this.getEditorInput())
				.getAdapter(Salespoint.class);

		if (salespoint.getCommonSettings() == null)
		{
			salespoint.setCommonSettings((CommonSettings) persistenceService.getServerService().find(
					CommonSettings.class, Long.valueOf(1L)));
		}

		if (salespoint.getPaymentType() == null)
		{
			final PaymentTypeQuery query = (PaymentTypeQuery) persistenceService.getServerService().getQuery(
					PaymentType.class);
			final Collection<PaymentType> paymentTypes = query.selectByPaymentTypeGroupAndCurrency(
					PaymentTypeGroup.CASH, salespoint.getCommonSettings().getReferenceCurrency());
			if (!paymentTypes.isEmpty())
			{
				salespoint.setPaymentType(paymentTypes.iterator().next());
			}
		}
		this.paymentTypes.setSelection(new StructuredSelection(salespoint.getPaymentType()));

		this.name.setText(salespoint.getName());
		this.host.setText(salespoint.getHost());
		this.location.setText(salespoint.getLocation());
		this.mappingId.setText(salespoint.valueOf(salespoint.getMapping()));
		this.forceSettlement.setSelection(salespoint.isForceSettlement());
		
		this.loadProviderValues(salespoint);

		if (salespoint.getProfile() == null)
		{
			this.profiles.setSelection(new StructuredSelection());
		}
		else
		{
			this.profiles.setSelection(new StructuredSelection(salespoint.getProfile()));
		}

		this.quantity.setValue(new Integer(salespoint.getProposalQuantity()));
		this.price.setValue(new Double(salespoint.getProposalPrice()));

		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (Bundle bundle : bundles)
		{
			if (bundle.getSymbolicName().equals("ch.eugster.colibri.export"))
			{
				if (this.useIndividualExport != null)
				{
					this.useIndividualExport.setSelection(salespoint.isUseIndividualExport());
				}
				if (this.export != null)
				{
					this.export.setSelection(salespoint.isExport());
					this.export.setEnabled(this.useIndividualExport.getSelection());
				}
				if (this.exportPath != null)
				{
					this.exportPath.setText(salespoint.getExportPath());
					this.exportPath.setEnabled(this.useIndividualExport.getSelection());
				}
				if (this.exportSelector != null)
				{
					this.exportSelector.setEnabled(this.useIndividualExport.getSelection());
				}
				break;
			}
		}
		
		if (salespoint.getProposalTax() != null)
		{
			this.taxes.setSelection(new StructuredSelection(salespoint.getProposalTax()));
		}

		if ((this.receiptPrinterServiceTracker.getServices() != null)
				&& (this.receiptPrinterServiceTracker.getServices().length > 0))
		{
			final SalespointReceiptPrinterSettings receiptPrinter = salespoint.getReceiptPrinterSettings();
			if (receiptPrinter != null && !receiptPrinter.isDeleted())
			{
				this.receiptPrinterViewer.setSelection(new StructuredSelection(receiptPrinter
						.getReceiptPrinterSettings()));
				this.receiptPrinterCols.setSelection(receiptPrinter.getCols());
				this.receiptPrinterConverter.setText(receiptPrinter.getConverter() == null ? "" : receiptPrinter.getConverter());
				this.receiptPrinterLinesBeforeCut.setSelection(receiptPrinter.getLinesBeforeCut());
				this.receiptPrinterPort.setText(receiptPrinter.getPort() == null ? "" : receiptPrinter.getPort());
			}
		}

		if ((this.customerDisplayServiceTracker.getServices() != null)
				&& (this.customerDisplayServiceTracker.getServices().length > 0))
		{
			final SalespointCustomerDisplaySettings customerDisplaySettings = salespoint.getCustomerDisplaySettings();
			if (customerDisplaySettings != null && !customerDisplaySettings.isDeleted())
			{
				this.customerDisplayViewer.setSelection(new StructuredSelection(customerDisplaySettings
						.getCustomerDisplaySettings()));
				this.customerDisplayCols.setSelection(customerDisplaySettings.getCols());
				this.customerDisplayConverter.setText(customerDisplaySettings.getConverter() == null ? "" : customerDisplaySettings.getConverter());
//				this.customerDisplayDelay.setSelection(customerDisplaySettings.getDelay());
				this.customerDisplayPort.setText(customerDisplaySettings.getPort() == null ? "" : customerDisplaySettings.getPort());
				this.customerDisplayRows.setSelection(customerDisplaySettings.getRows());
			}
		}

		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final Salespoint salespoint = (Salespoint) ((SalespointEditorInput) this.getEditorInput())
				.getAdapter(Salespoint.class);
		salespoint.setName(this.name.getText());
		salespoint.setHost(this.host.getText());
		salespoint.setLocation(this.location.getText());
		salespoint.setMapping(this.mappingId.getText());
		salespoint.setForceSettlement(forceSettlement.getSelection());

		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (Bundle bundle : bundles)
		{
			if (bundle.getSymbolicName().equals("ch.eugster.colibri.export"))
			{
				if (useIndividualExport != null)
				{
					if (useIndividualExport.getSelection())
					{
						salespoint.setUseIndividualExport(useIndividualExport.getSelection());
						if (this.export != null)
						{
							salespoint.setExport(this.export.getSelection());
						}
						if (this.exportPath != null)
						{
							salespoint.setExportPath(this.exportPath.getText());
						}
					}
				}
				break;
			}
		}

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker
				.getService();
		if (providerConfigurator != null)
		{
			final boolean useLocalProviderProperties = this.useSalespointSpecificProviderProperties.getSelection();
			salespoint.setLocalProviderProperties(useLocalProviderProperties);
			if (useLocalProviderProperties)
			{
				this.saveSalespointSpecificProviderProperties(providerConfigurator);
			}
		}

		StructuredSelection ssel = (StructuredSelection) this.profiles.getSelection();
		final Profile newProfile = (Profile) ssel.getFirstElement();
		final Profile oldProfile = salespoint.getProfile();
		if (oldProfile != null)
		{
			oldProfile.removeSalespoint(salespoint);
		}
		if (oldProfile == null || !newProfile.getId().equals(oldProfile.getId()))
		{
			salespoint.setProfile(newProfile);
			newProfile.addSalespoint(salespoint);
		}

		ssel = (StructuredSelection) this.paymentTypes.getSelection();
		final PaymentType paymentType = (PaymentType) ssel.getFirstElement();
		salespoint.setPaymentType(paymentType);
		boolean exists = false;
		final Collection<Stock> stocks = salespoint.getStocks();
		for (final Stock stock : stocks)
		{
			if (stock.getPaymentType().equals(salespoint.getPaymentType()))
			{
				exists = true;
			}
		}
		if (!exists)
		{
			final Stock stock = Stock.newInstance(salespoint, paymentType);
			salespoint.addStock(stock);
		}

		String value = this.quantity.getControl().getText();
		salespoint.setProposalQuantity(Integer.parseInt(value));

		value = this.price.getControl().getText();
		salespoint.setProposalPrice(Double.parseDouble(value));

		ssel = (StructuredSelection) this.taxes.getSelection();
		if (ssel.isEmpty() || (((Tax) ssel.getFirstElement()).getId() == null))
		{
			salespoint.setProposalTax(null);
		}
		else
		{
			salespoint.setProposalTax((Tax) ssel.getFirstElement());
		}

		if ((this.receiptPrinterServiceTracker.getServices() != null)
				&& (this.receiptPrinterServiceTracker.getServices().length > 0))
		{
			ssel = (StructuredSelection) this.receiptPrinterViewer.getSelection();
			if (ssel.isEmpty())
			{
				final SalespointReceiptPrinterSettings selectedPrinter = salespoint.getReceiptPrinterSettings();
				if (selectedPrinter instanceof SalespointReceiptPrinterSettings)
				{
					selectedPrinter.setDeleted(true);
				}
			}
			else
			{
				final ReceiptPrinterSettings selectedPrinter = (ReceiptPrinterSettings) ssel.getFirstElement();
				SalespointReceiptPrinterSettings currentPrinterSettings = salespoint.getReceiptPrinterSettings();
				if (currentPrinterSettings == null)
				{
					currentPrinterSettings = SalespointReceiptPrinterSettings.newInstance(selectedPrinter, salespoint);
					salespoint.setReceiptPrinterSettings(currentPrinterSettings);
					
				}
				currentPrinterSettings.setDeleted(selectedPrinter.getId() == null ? true : false);
				currentPrinterSettings.setCols(this.receiptPrinterCols.getSelection());
				currentPrinterSettings.setConverter(this.receiptPrinterConverter.getText().isEmpty() ? null : this.receiptPrinterConverter.getText());
				currentPrinterSettings.setLinesBeforeCut(this.receiptPrinterLinesBeforeCut.getSelection());
				currentPrinterSettings.setPort(this.receiptPrinterPort.getText().isEmpty() ? null : receiptPrinterPort.getText());
			}
		}

		if ((this.customerDisplayServiceTracker.getServices() != null)
				&& (this.customerDisplayServiceTracker.getServices().length > 0))
		{
			ssel = (StructuredSelection) this.customerDisplayViewer.getSelection();
			if (ssel.isEmpty())
			{
				final SalespointCustomerDisplaySettings selectedDisplay = salespoint.getCustomerDisplaySettings();
				if (selectedDisplay instanceof SalespointCustomerDisplaySettings)
				{
					selectedDisplay.setDeleted(true);
				}
			}
			else
			{
				final CustomerDisplaySettings selectedDisplay = (CustomerDisplaySettings) ssel.getFirstElement();
				SalespointCustomerDisplaySettings currentDisplay = salespoint.getCustomerDisplaySettings();
				if (currentDisplay == null)
				{
					currentDisplay = SalespointCustomerDisplaySettings.newInstance(selectedDisplay, salespoint);
					salespoint.setCustomerDisplaySettings(currentDisplay);
				}
				currentDisplay.setCols(this.customerDisplayCols.getSelection());
				currentDisplay.setConverter(this.customerDisplayConverter.getText().isEmpty() ? null : this.customerDisplayConverter.getText());
//				currentDisplay.setDelay(this.customerDisplayDelay.getSelection());
				currentDisplay.setPort(this.customerDisplayPort.getText().isEmpty() ? null : this.customerDisplayPort.getText());
				currentDisplay.setRows(this.customerDisplayRows.getSelection());
			}
		}
	}

	@Override
	protected void updateControls()
	{
		super.updateControls();

		final Salespoint salespoint = (Salespoint) ((SalespointEditorInput) this.getEditorInput())
				.getAdapter(Salespoint.class);
		this.paymentTypes.getControl().setEnabled(salespoint.getId() == null);

	}

	@Override
	protected boolean validate()
	{
		Message msg = this.getEmptyNameMessage();

		if (msg == null)
		{
			msg = this.getUniqueNameMessage();
		}

		if (msg == null)
		{
			msg = this.getNoProfileSelectedMessage();
		}

		final Salespoint salespoint = (Salespoint) ((SalespointEditorInput) this.getEditorInput())
				.getAdapter(Salespoint.class);
		if (salespoint.getId() == null)
		{
			if (msg == null)
			{
				msg = this.getNoPaymentTypeSelectedMessage();
			}
		}

		if (msg == null)
		{
			msg = this.validateExportPath();
		}

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	private Message validateExportPath()
	{
		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (Bundle bundle : bundles)
		{
			if (bundle.getSymbolicName().equals("ch.eugster.colibri.export"))
			{
				if (this.export.getSelection() && !new File(this.exportPath.getText()).exists())
				{
					return new Message(this.exportPath, "Ungültiger Export-Pfad", "Der eingegebene Export-Pfad ist ungültig. Wählen Sie ein vorhandenes Verzeichnis als Export-Pfad aus.");
				}
			}
		}
		return null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Salespoint> input)
	{
		return input.getAdapter(Salespoint.class) instanceof Salespoint;
	}

	private Section createCustomerDisplayPeripherySection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Kundendisplay");
		section.setClient(this.fillCustomerDisplayPeripherySection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				SalespointEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createProfileAndCurrencySection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Profil");
		section.setClient(this.fillProfileAndCurrencySection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				SalespointEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createProposalSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Vorschläge");
		section.setClient(this.fillProposalSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				SalespointEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createExportSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Export");
		section.setClient(this.fillExportSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				SalespointEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createProviderSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Warenbewirtschaftung");
		section.setClient(this.fillProviderSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				SalespointEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createReceiptPrinterPeripherySection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Belegdrucker");
		section.setClient(this.fillReceiptPrinterPeripherySection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				SalespointEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createSalespointSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Angaben zur Kasse");
		section.setClient(this.fillSalespointSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				SalespointEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillCustomerDisplayPeripherySection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(3, false));

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final Collection<CustomerDisplaySettings> peripheries = new ArrayList<CustomerDisplaySettings>();
			peripheries.add(CustomerDisplaySettings.newInstance());

			final CustomerDisplaySettingsQuery query = (CustomerDisplaySettingsQuery) persistenceService
					.getServerService().getQuery(CustomerDisplaySettings.class);
			final ServiceTracker<CustomerDisplayService, CustomerDisplayService> tracker = new ServiceTracker<CustomerDisplayService, CustomerDisplayService>(Activator.getDefault().getBundle().getBundleContext(),
					CustomerDisplayService.class, null);
			tracker.open();
			final ServiceReference<CustomerDisplayService>[] references = tracker.getServiceReferences();
			if (references != null)
			{
				for (final ServiceReference<CustomerDisplayService> reference : references)
				{
					final String componentName = (String) reference.getProperty("component.name");
					CustomerDisplaySettings periphery = query.findByComponentName(componentName);
					if (periphery == null)
					{
						periphery = CustomerDisplaySettings.newInstance();
						periphery.setComponentName(componentName);
						periphery.setName((String)reference.getProperty("custom.device"));
						final Integer cols = (Integer) reference.getProperty("custom.cols");
						periphery.setCols(cols == null ? 0 : cols.intValue());
						final Integer rows = (Integer) reference.getProperty("custom.rows");
						periphery.setRows(rows == null ? 0 : rows.intValue());
//						final Integer delay = (Integer) reference.getProperty("custom.delay");
//						periphery.setDelay(delay == null ? 0 : delay.intValue());
						periphery.setConverter(getCustomerDisplayConverter(tracker, reference));
						periphery.setPort((String) reference.getProperty("custom.port"));
						periphery = (CustomerDisplaySettings) persistenceService.getServerService().merge(periphery);
					}
					peripheries.add(periphery);
				}

				Label label = this.formToolkit.createLabel(composite, "Gerät", SWT.NONE);
				label.setLayoutData(new GridData());

				GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
				gridData.horizontalSpan = 2;
				
				final CCombo combo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.FLAT);
				combo.setLayoutData(gridData);
				combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				this.formToolkit.adapt(combo);

				this.customerDisplayViewer = new ComboViewer(combo);
				this.customerDisplayViewer.setContentProvider(new PeripheryContentProvider());
				this.customerDisplayViewer.setLabelProvider(new PeripheryLabelProvider());
				this.customerDisplayViewer.addSelectionChangedListener(new ISelectionChangedListener()
				{
					@Override
					public void selectionChanged(final SelectionChangedEvent event)
					{
						final StructuredSelection ssel = (StructuredSelection) event.getSelection();
						if (!ssel.isEmpty())
						{
							final CustomerDisplaySettings periphery = (CustomerDisplaySettings) ssel.getFirstElement();
							final Salespoint salespoint = ((SalespointEditorInput) SalespointEditor.this.getEditorInput())
									.getEntity();
							SalespointCustomerDisplaySettings salespointPeriphery = salespoint.getCustomerDisplaySettings();
							if (salespointPeriphery == null)
							{
								salespointPeriphery = periphery.getSalespointPeriphery(salespoint);
							}
							if (salespointPeriphery == null)
							{
								SalespointEditor.this.customerDisplayPort.setText(periphery.getPort() == null ? ""
										: periphery.getPort());
								SalespointEditor.this.customerDisplayConverter.setText(periphery.getConverter() == null ? ""
										: periphery.getConverter());
								SalespointEditor.this.customerDisplayCols.setSelection(periphery.getCols());
								SalespointEditor.this.customerDisplayRows.setSelection(periphery.getRows());
//								SalespointEditor.this.customerDisplayDelay.setSelection(periphery.getDelay());
							}
							else
							{
								SalespointEditor.this.customerDisplayPort.setText(salespointPeriphery.getPort() == null ? ""
										: (periphery.getPort() == null ? "" : periphery.getPort()));
								SalespointEditor.this.customerDisplayConverter
										.setText(salespointPeriphery.getConverter() == null ? "" : periphery.getConverter());
								SalespointEditor.this.customerDisplayCols.setSelection(salespointPeriphery.getCols());
							}
							SalespointEditor.this.setDirty(true);
						}
					}
				});
				this.customerDisplayViewer.setInput(peripheries.toArray(new CustomerDisplaySettings[0]));

				label = this.formToolkit.createLabel(composite, "Anschluss");
				label.setLayoutData(new GridData());

				gridData = new GridData(GridData.FILL_HORIZONTAL);
				gridData.horizontalSpan = 2;
				
				this.customerDisplayPort = this.formToolkit.createText(composite, "");
				this.customerDisplayPort.setLayoutData(gridData);
				this.customerDisplayPort.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});

				label = this.formToolkit.createLabel(composite, "Konverter");
				label.setLayoutData(new GridData());

				gridData = new GridData(GridData.FILL_HORIZONTAL);
				gridData.horizontalSpan = 2;
				gridData.heightHint = 96;

				this.customerDisplayConverter = this.formToolkit.createText(composite, "", SWT.MULTI | SWT.WRAP
						| SWT.V_SCROLL);
				this.customerDisplayConverter.setLayoutData(gridData);
				this.customerDisplayConverter.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});

				label = this.formToolkit.createLabel(composite, "Anzahl Spalten");
				label.setLayoutData(new GridData());

				gridData = new GridData();
				gridData.widthHint = 64;
				gridData.horizontalSpan = 2;

				this.customerDisplayCols = new Spinner(composite, SWT.NONE);
				this.customerDisplayCols.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				this.customerDisplayCols.setLayoutData(gridData);
				this.customerDisplayCols.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});
				this.customerDisplayCols.addSelectionListener(new SelectionListener()
				{

					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						this.widgetSelected(e);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});

				label = this.formToolkit.createLabel(composite, "Anzahl Zeilen");
				label.setLayoutData(new GridData());

				gridData = new GridData(GridData.FILL_HORIZONTAL);
				gridData.widthHint = 64;
				gridData.horizontalSpan = 2;

				this.customerDisplayRows = new Spinner(composite, SWT.NONE);
				this.customerDisplayRows.setLayoutData(gridData);
				this.customerDisplayRows.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				this.customerDisplayRows.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});
				this.customerDisplayRows.addSelectionListener(new SelectionListener()
				{

					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						this.widgetSelected(e);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});

//				label = this.formToolkit.createLabel(composite, "Verzögerung");
//				label.setLayoutData(new GridData());
//
//				this.customerDisplayDelay = new Spinner(composite, SWT.NONE);
//				this.customerDisplayDelay.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
//				this.customerDisplayDelay.addModifyListener(new ModifyListener()
//				{
//					@Override
//					public void modifyText(final ModifyEvent e)
//					{
//						SalespointEditor.this.setDirty(true);
//					}
//				});
//				this.customerDisplayDelay.addSelectionListener(new SelectionListener()
//				{
//
//					@Override
//					public void widgetDefaultSelected(final SelectionEvent e)
//					{
//						this.widgetSelected(e);
//					}
//
//					@Override
//					public void widgetSelected(final SelectionEvent e)
//					{
//						SalespointEditor.this.setDirty(true);
//					}
//				});
//
//				label = this.formToolkit.createLabel(composite, "Sekunden");
//				label.setLayoutData(new GridData());

			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private final Control fillProfileAndCurrencySection(final Section parent)
	{
		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, false));

		Label label = this.formToolkit.createLabel(composite, "Profil", SWT.NONE);
		label.setLayoutData(new GridData());

		CCombo combo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.FLAT);
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
				SalespointEditor.this.setDirty(true);
			}
		});
		this.formToolkit.adapt(combo);

		this.profiles = new ComboViewer(combo);
		this.profiles.setContentProvider(new ProfileContentProvider());
		this.profiles.setLabelProvider(new ProfileLabelProvider());
		this.profiles.setSorter(new ProfileSorter());

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ProfileQuery queryService = (ProfileQuery) persistenceService.getServerService().getQuery(
					Profile.class);
			if (queryService != null)
			{
				final Profile[] profiles = queryService.selectAll(false).toArray(new Profile[0]);
				this.profiles.setInput(profiles);
			}
		}

		label = this.formToolkit.createLabel(composite, "Währung", SWT.NONE);
		label.setLayoutData(new GridData());

		combo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.FLAT);
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
				SalespointEditor.this.setDirty(true);
			}
		});
		this.formToolkit.adapt(combo);

		this.paymentTypes = new ComboViewer(combo);
		this.paymentTypes.setContentProvider(new PaymentTypeContentProvider());
		this.paymentTypes.setLabelProvider(new PaymentTypeLabelProvider());
		this.paymentTypes.setSorter(new PaymentTypeSorter());

		if (persistenceService != null)
		{
			final PaymentTypeQuery queryService = (PaymentTypeQuery) persistenceService.getServerService().getQuery(
					PaymentType.class);
			if (queryService != null)
			{
				final PaymentType[] paymentTypes = queryService.selectByGroup(PaymentTypeGroup.CASH).toArray(
						new PaymentType[0]);
				this.paymentTypes.setInput(paymentTypes);
			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private final Control fillExportSection(final Section parent)
	{
		final Salespoint salespoint = (Salespoint) ((SalespointEditorInput) this.getEditorInput())
				.getAdapter(Salespoint.class);

		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(3, false));

		GridData gridData = new GridData();
		gridData.horizontalSpan = 3;

		this.useIndividualExport = this.formToolkit.createButton(composite,
				"Kassenspezifische Einstellungen verwenden", SWT.CHECK);
		this.useIndividualExport.setLayoutData(gridData);
		this.useIndividualExport.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				export.setEnabled(useIndividualExport.getSelection());
				export.setSelection(salespoint.isExport(useIndividualExport.getSelection()));
				exportPath.setEnabled(useIndividualExport.getSelection());
				exportPath.setText(salespoint.getExportPath(useIndividualExport.getSelection()));
				exportSelector.setEnabled(useIndividualExport.getSelection());
				SalespointEditor.this.setDirty(true);
			}

		});

		gridData = new GridData();
		gridData.horizontalSpan = 3;

		this.export = this.formToolkit.createButton(composite,
				"Belege exportieren", SWT.CHECK);
		this.export.setLayoutData(gridData);
		this.export.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				if (export.getSelection())
				{
					File file = new File(exportPath.getText());
					if (!file.exists())
					{
						setExportPath(salespoint);
					}
				}
				SalespointEditor.this.setDirty(true);
			}
		});

		Label label = this.formToolkit.createLabel(composite, "Export-Pfad", SWT.None);
		label.setLayoutData(new GridData());
		
		exportPath = this.formToolkit.createText(composite, "");
		exportPath.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		this.exportSelector = this.formToolkit.createButton(composite,
				"...", SWT.PUSH);
		this.exportSelector.setLayoutData(new GridData());
		this.exportSelector.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				setExportPath(salespoint);
			}

		});


		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private void setExportPath(Salespoint salespoint)
	{
		Shell shell = SalespointEditor.this.getSite().getShell();
		DirectoryDialog dialog = new DirectoryDialog(shell);
		dialog.setMessage("Zielverzeichnis für den Beleg-Export");
		dialog.setFilterPath(this.exportPath.getText());
		dialog.setText("Beleg-Export");
		String path = dialog.open();
		File file = new File(path == null ? exportPath.getText() : path);
		if (!file.exists() && export.getSelection())
		{
			String title = "Ungültiges Verzeichnis";
			String msg = "Das gewählte Verzeichnis existiert nicht. Bitte wählen Sie ein gültiges Verzeichnis.";
			MessageDialog messageDialog = new MessageDialog(shell, title, null, msg, MessageDialog.WARNING, new String[] { "OK" }, 0);
			messageDialog.open();
			export.setSelection(false);
		}
		else
		{
			exportPath.setText(path);
			SalespointEditor.this.setDirty(true);

			HostnameResolver resolver = salespoint.getCommonSettings().getHostnameResolver();
			String hostname = resolver.getHostname();
			if (salespoint.getHost() == null || !salespoint.getHost().equals(hostname))
			{
				String title = "Pfadangabe";
				String msg = "Bitte beachten Sie, dass das Programm den gewählten Pfad nicht überprüfen kann, da er sich auf einem anderen Host befindet. Stellen Sie sicher, dass der Pfad auf der Kasse " + salespoint.getName() + " lokal verfügbar ist.";
				MessageDialog messageDialog = new MessageDialog(shell, title, null, msg, MessageDialog.WARNING, new String[] { "OK" }, 0);
				messageDialog.open();
			}
		}
	}

	private final Control fillProposalSection(final Section parent)
	{
		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(new GridLayout(2, false));

		Label label = this.formToolkit.createLabel(composite, "Menge", SWT.NONE);
		label.setLayoutData(new GridData());

		GridData gridData = new GridData();
		gridData.widthHint = 36;

		final Text quantity = this.formToolkit.createText(composite, "");
		quantity.setLayoutData(gridData);
		this.quantity = new FormattedText(quantity);
		this.quantity.setFormatter(new NumberFormatter("#,##0", Locale.getDefault()));

		label = this.formToolkit.createLabel(composite, "Preis", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 36;

		final Text price = this.formToolkit.createText(composite, "");
		price.setLayoutData(gridData);
		this.price = new FormattedText(price);
		this.price.setFormatter(new NumberFormatter("#,##0.00", Locale.getDefault()));

		// label = this.formToolkit.createLabel(composite, "Option", SWT.NONE);
		// label.setLayoutData(new GridData());
		//
		// CCombo combo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN |
		// SWT.FLAT);
		// combo.setLayoutData(new GridData());
		// combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		// combo.addSelectionListener(new SelectionListener()
		// {
		// public void widgetDefaultSelected(final SelectionEvent e)
		// {
		// this.widgetSelected(e);
		// }
		//
		// public void widgetSelected(final SelectionEvent e)
		// {
		// SalespointEditor.this.setDirty(true);
		// }
		// });
		// this.formToolkit.adapt(combo);
		//
		// this.options = new ComboViewer(combo);
		// this.options.setContentProvider(new OptionContentProvider());
		// this.options.setLabelProvider(new OptionLabelProvider());
		// this.options.setSorter(new OptionSorter());
		// this.options.setInput(Position.Option.values());

		label = this.formToolkit.createLabel(composite, "Mehrwertsteuer", SWT.NONE);
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
				SalespointEditor.this.setDirty(true);
			}
		});
		this.formToolkit.adapt(combo);

		this.taxes = new ComboViewer(combo);
		this.taxes.setContentProvider(new TaxContentProvider());
		this.taxes.setLabelProvider(new TaxLabelProvider());
		this.taxes.setSorter(new TaxSorter());

		final Collection<Tax> taxes = new ArrayList<Tax>();
		taxes.add(Tax.newInstance(TaxRate.newInstance(), TaxType.newInstance()));

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final TaxQuery queryService = (TaxQuery) persistenceService.getServerService().getQuery(Tax.class);
			if (queryService != null)
			{
				final Collection<Tax> ts = queryService.selectAll(false);
				for (final Tax t : ts)
				{
					taxes.add(t);
				}
				this.taxes.setInput(taxes.toArray(new Tax[0]));
			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private final Control fillProviderSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(4, false));

		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker
				.getService();
		if (providerConfigurator == null)
		{
			this.formToolkit.createLabel(composite, "Keine Warenbewirtschaftung eingerichtet.");
		}
		else
		{
			if (providerConfigurator != null)
			{
				GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
				layoutData.horizontalSpan = 4;

				this.useSalespointSpecificProviderProperties = this.formToolkit.createButton(composite,
						"Kassenspezifische Einstellungen verwenden", SWT.CHECK);
				this.useSalespointSpecificProviderProperties.setLayoutData(layoutData);
				this.useSalespointSpecificProviderProperties.addSelectionListener(new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						this.widgetSelected(e);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						final Salespoint salespoint = (Salespoint) SalespointEditor.this.getEditorInput().getAdapter(
								Salespoint.class);
						final boolean selected = ((Button) e.getSource()).getSelection();
						final Map<String, Control> controlsMap = SalespointEditor.this.providerPropertyControls;
						final Collection<Control> controls = controlsMap.values();
						for (final Control control : controls)
						{
							control.setEnabled(selected);
							if (selected)
							{
								SalespointEditor.this.loadSalespointSpecificProviderPropertyValues(salespoint);
							}
							else
							{
								SalespointEditor.this.loadDefaultProviderPropertyValues();
							}
						}
					}

				});

				final Salespoint salespoint = (Salespoint) this.getEditorInput().getAdapter(Salespoint.class);
				final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker
						.getService();
				if (persistenceService != null)
				{
					final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getServerService()
							.getQuery(ProviderProperty.class);
					this.defaultProviderProperties = query.selectByProviderAsMap(providerConfigurator.getProviderId());
					this.salespointProviderProperties = query.selectByProviderAndSalespointAsMap(
							providerConfigurator.getProviderId(), salespoint);

					this.providerProperties = providerConfigurator.getProperties().values().toArray(new IProperty[0]);
					if ((this.providerProperties != null) && (this.providerProperties.length > 0))
					{
						for (final IProperty property : this.providerProperties)
						{
							layoutData = new GridData();
							layoutData.widthHint = 16;

							Label label = this.formToolkit.createLabel(composite, "   ");
							label.setLayoutData(layoutData);

							if (property.control().equals(Text.class.getName()))
							{
								label = this.formToolkit.createLabel(composite, property.label());
								label.setLayoutData(new GridData());

								layoutData = new GridData(GridData.FILL_HORIZONTAL);
								layoutData.horizontalSpan = 2;

								final Text text = this.formToolkit.createText(composite, "");
								text.setData("key", property.key());
								text.setLayoutData(layoutData);
								text.addModifyListener(new ModifyListener()
								{
									@Override
									public void modifyText(final ModifyEvent event)
									{
										final Text text = (Text) event.getSource();
										text.setData("value", text.getText());
										SalespointEditor.this.setDirty(true);
									}
								});
								this.providerPropertyControls.put(property.key(), text);

							}
							else if (property.control().equals(FileDialog.class.getName()))
							{
								label = this.formToolkit.createLabel(composite, property.label());
								label.setLayoutData(new GridData());

								final Text text = this.formToolkit.createText(composite, "");
								text.setData("key", property.key());
								text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
								text.addModifyListener(new ModifyListener()
								{
									@Override
									public void modifyText(final ModifyEvent event)
									{
										final Text text = (Text) event.getSource();
										text.setData("value", text.getText());
										SalespointEditor.this.setDirty(true);
									}
								});
								this.providerPropertyControls.put(property.key(), text);

								final Button button = this.formToolkit.createButton(composite, "...", SWT.PUSH);
								button.setLayoutData(new GridData());
								button.setData("key", property.key());
								button.setData("filter", property.filter());
								button.setData("name", property.label());
								button.addSelectionListener(new SelectionListener()
								{
									@Override
									public void widgetDefaultSelected(final SelectionEvent event)
									{
										this.widgetSelected(event);
									}

									@Override
									public void widgetSelected(final SelectionEvent event)
									{
										final FileDialog dialog = new FileDialog(SalespointEditor.this.getSite()
												.getShell());
										final Button button = (Button) event.getSource();
										final String key = (String) button.getData("key");
										final Text text = (Text) SalespointEditor.this.providerPropertyControls
												.get(key);
										dialog.setFileName(text.getText());
										final String[] filter = (String[]) button.getData("filter");
										dialog.setFilterExtensions(filter);
										dialog.setFilterIndex(0);
										final String name = (String) button.getData("name");
										dialog.setText(name);
										dialog.setFilterPath(null);
										final String value = dialog.open();
										if ((value != null) && (value.length() > 0))
										{
											text.setData("value", value);
											SalespointEditor.this.setDirty(true);
										}
									}
								});
								this.providerPropertyControls.put(property.key() + ".dialog", button);
							}
							else if (property.control().equals(Button.class.getName()))
							{
								label = this.formToolkit.createLabel(composite, "");
								label.setLayoutData(new GridData());

								layoutData = new GridData(GridData.FILL_HORIZONTAL);
								layoutData.horizontalSpan = 2;

								final Button button = this.formToolkit.createButton(composite, property.label(),
										SWT.CHECK);
								button.setLayoutData(layoutData);
								button.setData("key", property.key());
								button.addSelectionListener(new SelectionListener()
								{
									@Override
									public void widgetDefaultSelected(final SelectionEvent event)
									{
										this.widgetSelected(event);
									}

									@Override
									public void widgetSelected(final SelectionEvent event)
									{
										final Button button = (Button) event.getSource();
										button.setData("value", Boolean.toString(button.getSelection()));
										SalespointEditor.this.setDirty(true);
									}
								});
								this.providerPropertyControls.put(property.key(), button);
							}
						}
					}
				}
			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private String getCustomerDisplayConverter(ServiceTracker<CustomerDisplayService, CustomerDisplayService> tracker, ServiceReference<CustomerDisplayService> reference)
	{
		CustomerDisplayService service = (CustomerDisplayService) tracker.getService(reference);
		return service.convertToString(reference.getProperty("custom.convert"));
	}
	
	private Control fillReceiptPrinterPeripherySection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(3, false));

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final Collection<ReceiptPrinterSettings> peripheries = new ArrayList<ReceiptPrinterSettings>();
			peripheries.add(ReceiptPrinterSettings.newInstance());

			final ReceiptPrinterSettingsQuery query = (ReceiptPrinterSettingsQuery) persistenceService
					.getServerService().getQuery(ReceiptPrinterSettings.class);
			final ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> tracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle().getBundleContext(),
					ReceiptPrinterService.class, null);
			tracker.open();
			final ServiceReference<ReceiptPrinterService>[] references = tracker.getServiceReferences();
			if (references != null)
			{
				for (final ServiceReference<ReceiptPrinterService> reference : references)
				{
					final String componentName = (String) reference.getProperty("component.name");
					ReceiptPrinterSettings periphery = query.findByComponentName(componentName);
					if (periphery == null)
					{
						final Integer cols = (Integer) reference.getProperty("custom.cols");
						final Integer cuts = (Integer) reference.getProperty("custom.lines.before.cut");
						periphery = ReceiptPrinterSettings.newInstance();
						periphery.setComponentName(componentName);
						periphery.setName((String) reference.getProperty("custom.device"));
						periphery.setLinesBeforeCut(cuts == null ? 0 : cuts.intValue());
						periphery.setCols(cols == null ? 0 : cols.intValue());
						periphery.setConverter((String) getConverter(reference.getProperty("custom.convert")));
						periphery.setPort((String) reference.getProperty("custom.port"));
						periphery = (ReceiptPrinterSettings) persistenceService.getServerService().merge(periphery);
					}
					peripheries.add(periphery);
				}

				Label label = this.formToolkit.createLabel(composite, "Gerät", SWT.NONE);
				label.setLayoutData(new GridData());

				GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
				gridData.horizontalSpan = 2;
				
				final CCombo combo = new CCombo(composite, SWT.READ_ONLY | SWT.DROP_DOWN | SWT.FLAT);
				combo.setLayoutData(gridData);
				combo.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				this.formToolkit.adapt(combo);

				this.receiptPrinterViewer = new ComboViewer(combo);
				this.receiptPrinterViewer.setContentProvider(new PeripheryContentProvider());
				this.receiptPrinterViewer.setLabelProvider(new PeripheryLabelProvider());
				this.receiptPrinterViewer.addSelectionChangedListener(new ISelectionChangedListener()
				{
					@Override
					public void selectionChanged(final SelectionChangedEvent event)
					{
						final StructuredSelection ssel = (StructuredSelection) event.getSelection();
						final ReceiptPrinterSettings receiptPrinter = (ReceiptPrinterSettings) ssel.getFirstElement();
						final Salespoint salespoint = ((SalespointEditorInput) SalespointEditor.this.getEditorInput())
								.getEntity();
						SalespointReceiptPrinterSettings salespointReceiptPrinter = salespoint.getReceiptPrinterSettings();
						if (salespointReceiptPrinter == null)
						{
							salespointReceiptPrinter = receiptPrinter.getSalespointReceiptPrinter(salespoint);
						}
						if (salespointReceiptPrinter == null)
						{
							SalespointEditor.this.receiptPrinterPort.setText(receiptPrinter.getPort() == null ? ""
									: receiptPrinter.getPort() == null ? "" : receiptPrinter.getPort());
							SalespointEditor.this.receiptPrinterConverter.setText(receiptPrinter.getConverter() == null ? ""
									: receiptPrinter.getConverter() == null ? "" : receiptPrinter.getConverter());
							SalespointEditor.this.receiptPrinterCols.setSelection(receiptPrinter.getCols());
							SalespointEditor.this.receiptPrinterLinesBeforeCut.setSelection(receiptPrinter.getLinesBeforeCut());
						}
						else
						{
							SalespointEditor.this.receiptPrinterPort.setText(salespointReceiptPrinter.getPort() == null ? ""
									: receiptPrinter.getPort() == null ? "" : receiptPrinter.getPort());
							SalespointEditor.this.receiptPrinterConverter.setText(salespointReceiptPrinter
									.getConverter() == null ? "" : receiptPrinter.getConverter());
							SalespointEditor.this.receiptPrinterCols.setSelection(salespointReceiptPrinter.getCols());
							SalespointEditor.this.receiptPrinterLinesBeforeCut.setSelection(salespointReceiptPrinter
									.getLinesBeforeCut());
						}
						SalespointEditor.this.setDirty(true);
					}
				});
				this.receiptPrinterViewer.setInput(peripheries.toArray(new ReceiptPrinterSettings[0]));

				label = this.formToolkit.createLabel(composite, "Anschluss");
				label.setLayoutData(new GridData());

				gridData = new GridData(GridData.FILL_HORIZONTAL);
				gridData.horizontalSpan = 2;
				
				this.receiptPrinterPort = this.formToolkit.createText(composite, "");
				this.receiptPrinterPort.setLayoutData(gridData);
				this.receiptPrinterPort.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});

				label = this.formToolkit.createLabel(composite, "Konverter");
				label.setLayoutData(new GridData());

				gridData = new GridData(GridData.FILL_HORIZONTAL);
				gridData.horizontalSpan = 2;
				gridData.heightHint = 96;

				this.receiptPrinterConverter = this.formToolkit.createText(composite, "", SWT.MULTI | SWT.WRAP
						| SWT.V_SCROLL);
				this.receiptPrinterConverter.setLayoutData(gridData);
				this.receiptPrinterConverter.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});

				label = this.formToolkit.createLabel(composite, "Anzahl Spalten");
				label.setLayoutData(new GridData());

				gridData = new GridData();
				gridData.widthHint = 64;
				gridData.horizontalSpan = 2;
				
				this.receiptPrinterCols = new Spinner(composite, SWT.NONE);
				this.receiptPrinterCols.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				this.receiptPrinterCols.setLayoutData(gridData);
				this.receiptPrinterCols.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});
				this.receiptPrinterCols.addSelectionListener(new SelectionListener()
				{

					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						this.widgetSelected(e);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});

				label = this.formToolkit.createLabel(composite, "Vor Belegschnitt");
				label.setLayoutData(new GridData());

				this.receiptPrinterLinesBeforeCut = new Spinner(composite, SWT.NONE);
				this.receiptPrinterLinesBeforeCut.setLayoutData(new GridData());
				this.receiptPrinterLinesBeforeCut.setDigits(0);
				this.receiptPrinterLinesBeforeCut.setIncrement(1);
				this.receiptPrinterLinesBeforeCut.setPageIncrement(10);
				this.receiptPrinterLinesBeforeCut.setMinimum(0);
				this.receiptPrinterLinesBeforeCut.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				this.receiptPrinterLinesBeforeCut.addSelectionListener(new SelectionListener()
				{
					@Override
					public void widgetDefaultSelected(final SelectionEvent e)
					{
						this.widgetSelected(e);
					}

					@Override
					public void widgetSelected(final SelectionEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});
				this.receiptPrinterLinesBeforeCut.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						SalespointEditor.this.setDirty(true);
					}
				});
				this.formToolkit.adapt(this.receiptPrinterLinesBeforeCut);

				label = this.formToolkit.createLabel(composite, "Zeilen nachschieben");
				label.setLayoutData(new GridData());

			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private final Control fillSalespointSection(final Section parent)
	{
		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final GridLayout layout = new GridLayout(3, false);

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		Label label = this.formToolkit.createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(new GridData());

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		this.name = this.formToolkit.createText(composite, "");
		this.name.setLayoutData(gridData);
		this.name.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				SalespointEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Host", SWT.NONE);
		label.setLayoutData(new GridData());

		this.host = this.formToolkit.createText(composite, "");
		this.host.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		this.host.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				SalespointEditor.this.setDirty(true);
			}
		});

		this.setHost = new Hyperlink(composite, SWT.NONE);
		this.setHost.setText("Auf diese Station setzen");
		this.setHost.setToolTipText("Die Kasse auf die aktuelle Station, an der Sie sich befinden, setzen");
		this.setHost.setLayoutData(new GridData());
		this.setHost.setUnderlined(true);
		this.setHost.setForeground(this.setHost.getDisplay().getSystemColor(SWT.COLOR_BLUE));
		this.setHost.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseUp(final MouseEvent event)
			{

				final PersistenceService persistenceService = (PersistenceService) SalespointEditor.this.persistenceServiceTracker
						.getService();
				if (persistenceService != null)
				{
					final CommonSettingsQuery settingsQuery = (CommonSettingsQuery) persistenceService
							.getServerService().getQuery(CommonSettings.class);
					final CommonSettings settings = settingsQuery.findDefault();
					if (settings != null)
					{
						final String host = settings.getHostnameResolver().getHostname();
						final Salespoint editedSalespoint = (Salespoint) SalespointEditor.this.getEditorInput()
								.getAdapter(Salespoint.class);
						final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getServerService()
								.getQuery(Salespoint.class);
						Salespoint other = salespointQuery.getCurrentSalespoint();
						if (other == null)
						{
							SalespointEditor.this.host.setText(host);
						}
						else
						{
							if ((editedSalespoint.getId() == null) || !other.getId().equals(editedSalespoint.getId()))
							{
								final Shell shell = SalespointEditor.this.getSite().getShell();
								final String title = "Registration nicht möglich";
								final String msg = "Die Kasse "
										+ other.getName()
										+ " ist für diesen Arbeitsplatz registriert. Bevor Sie diese Kasse für diesen Arbeitsplatz registriert werden kann, muss die bestehende Registration aufgehoben werden.";
								final int type = MessageDialog.WARNING;
								final String[] buttons = new String[] { "OK" };
								final MessageDialog dialog = new MessageDialog(shell, title, null, msg, type, buttons,
										0);
								dialog.open();
							}
						}
					}
				}
			}
		});

		label = this.formToolkit.createLabel(composite, "Standort", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		this.location = this.formToolkit.createText(composite, "");
		this.location.setLayoutData(gridData);
		this.location.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				SalespointEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "ExportId", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		this.mappingId = this.formToolkit.createText(composite, "", SWT.SINGLE);
		this.mappingId.setLayoutData(gridData);
		this.mappingId.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				SalespointEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		this.forceSettlement = this.formToolkit.createButton(composite, "Tagesabschluss erzwingen", SWT.CHECK);
		this.forceSettlement.setLayoutData(gridData);
		this.forceSettlement.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				SalespointEditor.this.setDirty(true);
			}

			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				SalespointEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyNameMessage()
	{
		Message msg = null;

		if (this.name.getText().isEmpty())
		{
			msg = new Message(this.name, "Fehler");
			msg.setMessage("Die Kasse muss eine Bezeichnung haben.");
		}
		return msg;
	}

	private final Map<String, ProviderProperty> getExistingSalespointSpecificProviderProperties(
			final Salespoint salespoint)
	{
		final Map<String, ProviderProperty> properties = new HashMap<String, ProviderProperty>();
		final ProviderProperty[] providerProperties = salespoint.getProviderProperties().values()
				.toArray(new ProviderProperty[0]);
		for (final ProviderProperty providerProperty : providerProperties)
		{
			properties.put(providerProperty.getKey(), providerProperty);
		}
		return properties;
	}

	private Message getNoPaymentTypeSelectedMessage()
	{
		Message msg = null;

		if (this.paymentTypes.getSelection().isEmpty())
		{
			msg = new Message(this.paymentTypes.getControl(), "Fehler");
			msg.setMessage("Der Kasse muss eine Währung zugeordnet werden.");
		}

		return msg;
	}

	private Message getNoProfileSelectedMessage()
	{
		Message msg = null;

		if (this.profiles.getSelection().isEmpty())
		{
			msg = new Message(this.profiles.getControl(), "Fehler");
			msg.setMessage("Der Kasse muss ein Profil zugeordnet werden.");
		}

		return msg;
	}

	private Message getUniqueNameMessage()
	{
		Message msg = null;

		final Salespoint salespoint = (Salespoint) ((SalespointEditorInput) this.getEditorInput())
				.getAdapter(Salespoint.class);

		final String name = this.name.getText();

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final SalespointQuery query = (SalespointQuery) persistenceService.getServerService().getQuery(
					Salespoint.class);
			if (!query.isNameUnique(name, salespoint.getId()))
			{
				msg = new Message(this.name, "Fehler");
				msg.setMessage("Die gewählte Kassenbezeichnung wird bereits verwendet.");
				return msg;
			}
		}

		return msg;
	}

	private void loadDefaultProviderPropertyValues()
	{
		for (final IProperty p : this.providerProperties)
		{
			final ProviderProperty defaultProperty = this.defaultProviderProperties.get(p.key());
			if (defaultProperty != null)
			{
				final Control control = this.providerPropertyControls.get(p.key());
				if (p.control().equals(Text.class.getName()))
				{
					final Text text = (Text) control;
					text.setText(defaultProperty.getValue());
					text.setEnabled(false);
				}
				if (p.control().equals(FileDialog.class.getName()))
				{
					final Text text = (Text) control;
					text.setText(defaultProperty.getValue());
					text.setEnabled(false);
					final Button button = (Button) this.providerPropertyControls.get(p.key() + ".dialog");
					button.setEnabled(false);
				}
				else if (p.control().equals(Button.class.getName()))
				{
					final Button button = (Button) control;
					button.setSelection(Boolean.valueOf(defaultProperty.getValue()));
					button.setEnabled(false);
				}
			}
		}
	}

	private void loadProviderValues(final Salespoint salespoint)
	{
		final ProviderConfigurator providerConfigurator = (ProviderConfigurator) this.providerConfiguratorTracker
				.getService();
		if (providerConfigurator != null)
		{
			this.useSalespointSpecificProviderProperties.setSelection(salespoint.isLocalProviderProperties());

			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker
					.getService();
			if (persistenceService != null)
			{
				final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getServerService()
						.getQuery(ProviderProperty.class);
				this.defaultProviderProperties = query.selectByProviderAsMap(providerConfigurator.getProviderId());
				this.salespointProviderProperties = this.getExistingSalespointSpecificProviderProperties(salespoint);

				if (this.useSalespointSpecificProviderProperties.getSelection())
				{
					this.loadSalespointSpecificProviderPropertyValues(salespoint);
				}
				else
				{
					this.loadDefaultProviderPropertyValues();
				}
			}
		}
	}

	private void loadSalespointSpecificProviderPropertyValues(final Salespoint salespoint)
	{
		for (final IProperty p : this.providerProperties)
		{
			final ProviderProperty property = this.salespointProviderProperties.get(p.key());
			final ProviderProperty defaultProperty = this.defaultProviderProperties.get(p.key());
			if (property != null)
			{
				if (!property.isDeleted())
				{
					property.setDeleted(false);
				}
			}

			final String value = property == null ? (defaultProperty == null ? p.value() : defaultProperty.getValue())
					: property.getValue();
			final Control control = this.providerPropertyControls.get(p.key());
			if (p.control().equals(Text.class.getName()))
			{
				final Text text = (Text) control;
				text.setText(value);
				text.setEnabled(true);
			}
			if (p.control().equals(FileDialog.class.getName()))
			{
				final Text text = (Text) control;
				text.setText(value);
				text.setEnabled(true);
				final Button button = (Button) this.providerPropertyControls.get(p.key() + ".dialog");
				button.setEnabled(true);
			}
			else if (p.control().equals(Button.class.getName()))
			{
				final Button button = (Button) control;
				button.setSelection(Boolean.valueOf(value));
				button.setEnabled(true);
			}
		}
	}

	private void saveSalespointSpecificProviderProperties(final ProviderConfigurator providerConfigurator)
	{
		final Salespoint salespoint = (Salespoint) ((SalespointEditorInput) this.getEditorInput())
				.getAdapter(Salespoint.class);

		for (final IProperty p : this.providerProperties)
		{
			ProviderProperty property = this.salespointProviderProperties.get(p.key());
			if (property == null)
			{
				String value = null;
				final Control control = this.providerPropertyControls.get(p.key());
				if (control instanceof Text)
				{
					value = ((Text) control).getText();
				}
				else if (control instanceof Button)
				{
					value = (Boolean.toString(((Button) control).getSelection()));
				}
				property = ProviderProperty.newInstance(providerConfigurator.getProviderId(), salespoint);
				property.setKey(p.key());
				property.setProvider(providerConfigurator.getProviderId());
				property.setValue(value);
				salespoint.putProviderProperty(property);
			}
			else
			{
				final Control control = this.providerPropertyControls.get(p.key());
				if (control instanceof Text)
				{
					property.setValue(((Text) control).getText());
				}
				else if (control instanceof Button)
				{
					property.setValue(Boolean.toString(((Button) control).getSelection()));
				}
			}
		}
	}

	private String getConverter(Object property)
	{
		if (property instanceof String[])
		{
			StringBuilder builder = new StringBuilder();
			String[] converters = (String[]) property;
			for (String converter : converters)
			{
				builder.append(converter + "\n");
			}
			return builder.toString();
		}
		else
		{
			return property.toString();
		}
	}
}
