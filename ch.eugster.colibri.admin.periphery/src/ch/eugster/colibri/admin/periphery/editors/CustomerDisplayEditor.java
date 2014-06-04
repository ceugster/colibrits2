/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.editors;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
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
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.osgi.framework.ServiceReference;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Salespoint;

public class CustomerDisplayEditor extends AbstractEntityEditor<CustomerDisplaySettings> implements
		PropertyChangeListener
{
	public static final String ID = "ch.eugster.colibri.admin.periphery.customer.display.editor";

	private Text port;

	private Text converter;

	private Spinner rows;

	private Spinner cols;

	private Text test;

	private IDialogSettings settings;

	public CustomerDisplayEditor()
	{
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(CustomerDisplaySettings.class, this);
		super.dispose();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") final Class adapter)
	{
		return null;
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		this.settings = Activator.getDefault().getDialogSettings().getSection(CustomerDisplayEditor.ID);
		if (this.settings == null)
		{
			this.settings = Activator.getDefault().getDialogSettings().addNewSection(CustomerDisplayEditor.ID);
		}
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof CustomerDisplaySettings)
		{
			final CustomerDisplaySettings display = (CustomerDisplaySettings) entity;
			if (display.equals(this.getEditorInput().getAdapter(CustomerDisplaySettings.class)))
			{
				this.dispose();
			}
		}
	}

	@Override
	public void propertyChange(final PropertyChangeEvent evt)
	{
		this.setDirty(true);
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
		// this.codePages.getCCombo().setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createPeripherySection(scrolledForm);
		this.createTestSection(scrolledForm);
		EntityMediator.addListener(Salespoint.class, this);
	}

	@Override
	protected String getName()
	{
		final CustomerDisplayEditorInput input = (CustomerDisplayEditorInput) this.getEditorInput();
		final ServiceReference<CustomerDisplayService> reference = input.getServiceReference();
		if (reference != null)
		{
			final String deviceName = (String) reference.getProperty("custom.device");
			if (deviceName != null)
			{
				return deviceName;
			}
		}
		return "???";
	}

	@Override
	protected String getText()
	{
		return "Kundendisplay";
	}

	@Override
	protected void loadValues()
	{
		CustomerDisplayEditorInput input = (CustomerDisplayEditorInput) this.getEditorInput();
		final CustomerDisplaySettings periphery = (CustomerDisplaySettings) input
				.getAdapter(CustomerDisplaySettings.class);
		final ServiceReference<CustomerDisplayService> reference = input.getServiceReference();

		String portName = periphery.getPort();
		if ((portName == null) || portName.isEmpty())
		{
			final Object prop = reference.getProperty("custom.port");
			if (prop instanceof String)
			{
				portName = (String) prop;
			}
		}
		this.port.setText(portName);

		String conversions = periphery.getConverter();
		if ((conversions == null) || conversions.isEmpty())
		{
			final Object prop = reference.getProperty("custom.convert");
			if (prop instanceof String)
			{
				conversions = (String) prop;
			}
			else if (prop instanceof String[])
			{
				final StringBuilder result = new StringBuilder();
				final String[] lines = (String[]) prop;
				for (final String line : lines)
				{
					result.append(line + "\n");
				}
				conversions = result.toString();
			}
		}
		this.converter.setText(conversions);

		Integer cols = periphery.getCols();
		if ((cols == null) || cols.equals(Integer.valueOf(0)))
		{
			final Object prop = reference.getProperty("custom.cols");
			if (prop instanceof Integer)
			{
				cols = (Integer) prop;
			}
		}
		this.cols.setSelection(cols == null ? 0 : cols.intValue());

		Integer rows = periphery.getRows();
		if ((rows == null) || rows.equals(Integer.valueOf(0)))
		{
			final Object prop = reference.getProperty("custom.rows");
			if (prop instanceof Integer)
			{
				rows = (Integer) prop;
			}
		}
		this.rows.setSelection(rows == null ? 0 : rows.intValue());

//		Integer delay = periphery.getDelay();
//		if ((delay == null) || delay.equals(Integer.valueOf(0)))
//		{
//			final Object prop = reference.getProperty("custom.delay");
//			if (prop instanceof Integer)
//			{
//				delay = (Integer) prop;
//			}
//		}
//		this.delay.setSelection(delay == null ? 0 : delay.intValue());

		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final CustomerDisplayEditorInput input = (CustomerDisplayEditorInput) this.getEditorInput();
		final CustomerDisplaySettings periphery = (CustomerDisplaySettings) input
				.getAdapter(CustomerDisplaySettings.class);
		periphery.setPort(this.port.getText());
		periphery.setConverter(this.converter.getText());
		periphery.setCols(Integer.valueOf(this.cols.getSelection()));
		periphery.setRows(Integer.valueOf(this.rows.getSelection()));
//		periphery.setDelay(Integer.valueOf(this.delay.getSelection()));
		periphery.setComponentName((String) input.getServiceReference().getProperty("component.name"));
		periphery.setName((String) input.getServiceReference().getProperty("custom.device"));
	}

	@Override
	protected void updateControls()
	{
		super.updateControls();
	}

	@Override
	protected boolean validate()
	{
		final Message msg = this.getEmptyNameMessage();

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<CustomerDisplaySettings> input)
	{
		return input.getAdapter(CustomerDisplaySettings.class) instanceof CustomerDisplaySettings;
	}

	private Section createPeripherySection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Eigenschaften");
		section.setClient(this.fillPeripherySection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				CustomerDisplayEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createTestSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Prüfen");
		section.setClient(this.fillTestSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				CustomerDisplayEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillPeripherySection(final Section parent)
	{
		final TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(tableWrapData);
		composite.setLayout(new GridLayout(3, false));

		Label label = this.formToolkit.createLabel(composite, "Gerätename", SWT.NONE);
		label.setLayoutData(new GridData());

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		this.port = this.formToolkit.createText(composite, "");
		this.port.setLayoutData(gridData);
		this.port.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				CustomerDisplayEditor.this.setDirty(true);
			}
		});

		label = this.formToolkit.createLabel(composite, "Spalten", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 32;
		gridData.horizontalSpan = 2;

		this.cols = new Spinner(composite, SWT.RIGHT);
		this.cols.setLayoutData(gridData);
		this.cols.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		this.cols.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				CustomerDisplayEditor.this.setDirty(true);
			}
		});
		this.cols.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				CustomerDisplayEditor.this.setDirty(true);
			}
		});
		this.cols.setIncrement(1);
		this.cols.setPageIncrement(10);
		this.formToolkit.adapt(this.cols);

		label = this.formToolkit.createLabel(composite, "Zeilen", SWT.NONE);
		label.setLayoutData(new GridData());

		gridData = new GridData();
		gridData.widthHint = 32;
		gridData.horizontalSpan = 2;

		this.rows = new Spinner(composite, SWT.RIGHT);
		this.rows.setLayoutData(gridData);
		this.rows.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		this.rows.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				CustomerDisplayEditor.this.setDirty(true);
			}
		});
		this.rows.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				CustomerDisplayEditor.this.setDirty(true);
			}
		});
		this.rows.setIncrement(1);
		this.rows.setPageIncrement(10);
		this.formToolkit.adapt(this.rows);

//		label = this.formToolkit.createLabel(composite, "Verzögerung", SWT.NONE);
//		label.setLayoutData(new GridData());
//
//		gridData = new GridData();
//		gridData.widthHint = 32;
//
//		this.delay = new Spinner(composite, SWT.NONE);
//		this.delay.setLayoutData(gridData);
//		this.delay.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
//		this.delay.addSelectionListener(new SelectionListener()
//		{
//			@Override
//			public void widgetDefaultSelected(final SelectionEvent e)
//			{
//				this.widgetSelected(e);
//			}
//
//			@Override
//			public void widgetSelected(final SelectionEvent e)
//			{
//				CustomerDisplayEditor.this.setDirty(true);
//			}
//		});
//		this.delay.addModifyListener(new ModifyListener()
//		{
//			@Override
//			public void modifyText(final ModifyEvent e)
//			{
//				CustomerDisplayEditor.this.setDirty(true);
//			}
//		});
//		this.delay.setIncrement(1);
//		this.delay.setPageIncrement(10);
//		this.formToolkit.adapt(this.delay);
//
//		label = this.formToolkit.createLabel(composite, "in Sekunden", SWT.NONE);
//		label.setLayoutData(new GridData());

		label = this.formToolkit.createLabel(composite, "Konverter");
		label.setLayoutData(new GridData());

		gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;
		gridData.heightHint = 100;

		this.converter = this.formToolkit.createText(composite, "", SWT.MULTI | SWT.V_SCROLL);
		this.converter.setLayoutData(gridData);
		this.converter.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				CustomerDisplayEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillTestSection(final Section parent)
	{
		final TableWrapData tableWrapData = new TableWrapData();
		tableWrapData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(tableWrapData);
		composite.setLayout(new GridLayout(3, false));

		final Label label = this.formToolkit.createLabel(composite, "Test");
		label.setLayoutData(new GridData());

		final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.heightHint = 26;

		String testText = settings.get("customer.display.editor.test.value");
		if (testText == null) 
		{
			testText = "Ä Ö Ü ä ö ü";
		}
		test = this.formToolkit.createText(composite, testText, SWT.MULTI | SWT.WRAP);
		test.setLayoutData(gridData);
		test.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e) 
			{
				settings.put("customer.display.editor.test.value", test.getText());
			}
		});

		final Button button = this.formToolkit.createButton(composite, "Test", SWT.PUSH);
		button.setLayoutData(new GridData());
		button.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				CustomerDisplayEditorInput input = (CustomerDisplayEditorInput) CustomerDisplayEditor.this
						.getEditorInput();
				final ServiceReference<CustomerDisplayService> reference = input.getServiceReference();
				if (reference != null)
				{
					final CustomerDisplayService service = Activator.getDefault().getBundle().getBundleContext().getService(reference);
					final CustomerDisplayService display = (CustomerDisplayService) service;
					Properties props = new Properties();
					props.setProperty("port", CustomerDisplayEditor.this.port.getText());
					props.setProperty("converter", CustomerDisplayEditor.this.converter.getText());
					props.setProperty("cols", CustomerDisplayEditor.this.cols.getText());
					props.setProperty("rows", CustomerDisplayEditor.this.rows.getText());
					IStatus status = display.testDisplay(props, CustomerDisplayEditor.this.test.getText());
					if (status.getSeverity() == IStatus.ERROR)
					{
						MessageDialog.openInformation(getSite().getShell(), "Anzeige", status.getMessage());
					}
				}
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Message getEmptyNameMessage()
	{
		final Message msg = null;
		return msg;
	}
}
