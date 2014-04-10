package ch.eugster.colibri.admin.provider.editors;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.EditorPart;

import ch.eugster.colibri.admin.provider.Activator;
import ch.eugster.colibri.admin.ui.editors.Resetable;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IDirtyable;
import ch.eugster.colibri.provider.configuration.IProperty;

public class ProviderPropertiesEditor extends EditorPart implements IPropertyListener, Resetable, IDirtyable
{
	public static final String ID = "ch.eugster.colibri.admin.provider.property.editor";

	private final Map<String, Control> controls = new HashMap<String, Control>();

	private boolean dirty;

	private FormToolkit formToolkit;

	private ScrolledForm scrolledForm;
	
	@Override
	public void createPartControl(final Composite parent)
	{
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();

		this.formToolkit = new FormToolkit(parent.getDisplay());

		final ColumnLayout columnLayout = new ColumnLayout();
		columnLayout.maxNumColumns = 2;
		columnLayout.minNumColumns = 1;

		this.scrolledForm = this.formToolkit.createScrolledForm(parent);
		this.scrolledForm.getBody().setLayout(columnLayout);
		this.scrolledForm.setText(input.getName());

		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		this.createSections(this.scrolledForm);

		this.setDirty(false);
	}

	@Override
	public void dispose()
	{
	}

	private boolean checkValues()
	{
		return true;
	}

	private boolean setValue(IProperty property, String value, ProviderProperty providerProperty)
	{
		boolean update = false;
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		if (property.isDefaultValue(value))
		{
			if (providerProperty != null)
			{
				if (!providerProperty.isDeleted())
				{
					providerProperty.setDeleted(true);
					update = true;
				}
			}
		}
		else
		{
			if (providerProperty == null)
			{
				providerProperty = ProviderProperty.newInstance(input.getProviderId());
				providerProperty.setKey(property.key());
				property.setPersistedProperty(providerProperty);
				update = true;
			}
			if (providerProperty.isDeleted())
			{
				providerProperty.setDeleted(false);
				update = true;
			}
			if (providerProperty.getValue(property.value()) == null || !providerProperty.getValue(property.value()).equals(value))
			{
				providerProperty.setValue(value, property.defaultValue());
				update = true;
			}
		}
		return update;
	}
	
	@Override
	public void doSave(final IProgressMonitor monitor)
	{
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		final PersistenceService persistenceService = input.getPersistenceService();
		if (persistenceService != null)
		{
			if (this.checkValues())
			{
				String value = null;
				for (final IProperty property : input.getProperties().values())
				{
					boolean update = false;
					if (property.control().equals(FileDialog.class.getName()))
					{
						final Text text = (Text) this.controls.get(property.key());
						value = text.getText();
					}
					else if (property.control().equals(Text.class.getName()))
					{
						final Text text = (Text) this.controls.get(property.key());
						value = text.getText();
					}
					else if (property.control().equals(Spinner.class.getName()))
					{
						final Spinner spinner = (Spinner) this.controls.get(property.key());
						value = Integer.toString(spinner.getSelection());
					}
					else if (property.control().equals(Button.class.getName()))
					{
						final Composite composite = (Composite) this.controls.get(property.key());
						value = Integer.toString(((Integer)composite.getData("value")).intValue());
					}
					if (setValue(property, value, property.getPersistedProperty()))
					{
						update = true;
					}
					if (update)
					{
						if (persistenceService != null)
						{
							try
							{
								property.setPersistedProperty((ProviderProperty) persistenceService.getServerService().merge(property.getPersistedProperty()));
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
								IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
								ErrorDialog.openError((Shell) this.getEditorSite().getShell(), "Fehler", "Die Eigenschaft " + property.toString() + " konnte nicht gespeichert werden.", status);
							}
						}
					}
				}
			}
		}
		this.setDirty(false);
	}

	@Override
	public void doSaveAs()
	{
		this.doSave(null);
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
	{
		this.setInput(input);
		this.setSite(site);
		this.setPartName(input.getName());
	}

	@Override
	public boolean isDirty()
	{
		return this.dirty;
	}

	@Override
	public boolean isSaveAsAllowed()
	{
		return false;
	}

	@Override
	public void propertyChanged(final Object source, final int propId)
	{
	}

	@Override
	public void reset(final boolean ask)
	{
		if (this.isDirty())
		{
			boolean reset = true;
			if (ask)
			{
				reset = MessageDialog.openQuestion(this.getEditorSite().getShell(), "Verwerfen",
						"Sollen die Änderungen verworfen werden?");
			}
			if (reset)
			{
				this.loadValues();
				this.setDirty(false);
			}
		}
	}

	@Override
	public void setFocus()
	{
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		Control control = this.controls.get(input.getProperties().values().toArray(new IProperty[0])[0]);
		if (control != null)
		{
			control.setFocus();
		}
	}

	protected void createSections(final ScrolledForm scrolledForm)
	{
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		IProperty.Section[] sections = input.getSections();
		for (IProperty.Section section : sections)
		{
			this.createSection(scrolledForm, section);
		}
	}

	private Section createSection(final ScrolledForm scrolledForm, IProperty.Section propertySection)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText(propertySection.title());
		section.setClient(this.fillSection(section, propertySection));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProviderPropertiesEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillSection(final Section parent, IProperty.Section section)
	{
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(section.columns(), false));

		for (final IProperty property : input.getProperties().values())
		{
			Control control = property.createControl(composite, this.formToolkit, this, section.columns(), property.validValues());
			if (control != null)
			{
				this.controls.put(property.key(), control);
			}
		}
		
		if (input.canCheckConnection())
		{
			GridData gridData = new GridData();

			Button button = this.formToolkit.createButton(composite, "Verbindung prüfen", SWT.PUSH);
			button.setLayoutData(gridData);
			button.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetSelected(SelectionEvent e)
				{
					Map<String, IProperty> testProperties = input.getDefaultProperties();
					Set<String> keys = testProperties.keySet();
					for (String key : keys)
					{
						IProperty property = testProperties.get(key);
						Control control = controls.get(key);
						if (control != null)
						{
							String value = property.value(property, control);
							ProviderProperty providerProperty = ProviderProperty.newInstance(input.getProviderId());
							providerProperty.setKey(key);
							providerProperty.setValue(value, property.defaultValue());
							property.setPersistedProperty(providerProperty);
						}
					}
					IStatus status = input.checkConnection(testProperties);
					if (status.getSeverity() == IStatus.OK)
					{
						MessageDialog
						.openInformation(getSite().getShell(), "Verbindungstest",
								status.getMessage());
					}
					else
					{
						MessageDialog
						.openError(getSite().getShell(), "Verbindungstest",
								status.getException().getMessage());
					}
					for (String key : keys)
					{
						IProperty property = testProperties.get(key);
						property.setPersistedProperty(null);
					}
				}

				@Override
				public void widgetDefaultSelected(SelectionEvent e)
				{
					widgetSelected(e);
				}
			});
		}

		this.formToolkit.paintBordersFor(composite);

		loadValues();
		
		return composite;
	}
	
	private void loadValues()
	{
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		for (IProperty property : input.getProperties().values())
		{
			final Control control = this.controls.get(property.key());
			if (control != null)
			{
				property.set(property, control, property.value());
			}
		}
	}

	public void setDirty(final boolean dirty)
	{
		this.dirty = dirty;
		this.firePropertyChange(IEditorPart.PROP_DIRTY);
	}

	private void showMessage(final String message)
	{
		final MessageDialog dialog = new MessageDialog(this.getSite().getShell(), "Ungültiger Wert", null, message,
				MessageDialog.INFORMATION, new String[] { "OK" }, 0);
		dialog.setBlockOnOpen(true);
		dialog.open();
	}

}
