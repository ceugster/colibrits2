package ch.eugster.colibri.admin.provider.editors;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
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
import org.eclipse.ui.progress.UIJob;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.provider.Activator;
import ch.eugster.colibri.admin.ui.editors.Resetable;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.SchedulerProperty;
import ch.eugster.colibri.provider.service.ProviderConfigurator;
import ch.eugster.colibri.provider.service.ProviderIdService;
import ch.eugster.colibri.provider.service.ProviderInterface;

public class ProviderPropertiesEditor extends EditorPart implements IPropertyListener, Resetable, ModifyListener
{
	public static final String ID = "ch.eugster.colibri.admin.provider.editor";

	private Map<String, ProviderProperty> providerProperties;

	private final Map<String, Control> controls = new HashMap<String, Control>();

	private boolean dirty;

	private FormToolkit formToolkit;

	private ScrolledForm scrolledForm;

	public ProviderPropertiesEditor()
	{
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		this.formToolkit = new FormToolkit(parent.getDisplay());

		final ColumnLayout columnLayout = new ColumnLayout();
		columnLayout.maxNumColumns = 2;
		columnLayout.minNumColumns = 1;

		this.scrolledForm = this.formToolkit.createScrolledForm(parent);
		this.scrolledForm.getBody().setLayout(columnLayout);
		this.scrolledForm.setText("Galileo");

		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		this.createSections(this.scrolledForm);

		this.loadValues();

		this.setDirty(false);
	}

	@Override
	public void dispose()
	{
	}

	@Override
	public void doSave(final IProgressMonitor monitor)
	{
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		final PersistenceService persistenceService = input.getPersistenceService();
		if (persistenceService != null)
		{
			final ProviderConfigurator configurator = input.getProviderConfigurator();
			if (configurator != null)
			{
				if (this.checkValues())
				{
					final Collection<IProperty> properties = configurator.getProperties().values();
					boolean update = false;
					for (final IProperty property : properties)
					{
						final Control control = this.controls.get(property.key());
						ProviderProperty providerProperty = this.providerProperties.get(property.key());
						if (control != null)
						{
							if (providerProperty == null)
							{
								providerProperty = ProviderProperty.newInstance(configurator.getProviderId());
								providerProperty.setKey(property.key());
							}

							if (property.control().equals(FileDialog.class.getName()))
							{
								final Text text = (Text) this.controls.get(property.key());
								if (!text.getText().equals(providerProperty.getValue()))
								{
									providerProperty.setValue(text.getText());
									update = true;
								}
							}
							else if (property.control().equals(Text.class.getName()))
							{
								final Text text = (Text) this.controls.get(property.key());
								if (!text.getText().equals(providerProperty.getValue()))
								{
									providerProperty.setValue(text.getText());
									update = true;
								}
							}
							else if (property.control().equals(Button.class.getName()))
							{
								final Button button = (Button) this.controls.get(property.key());
								final boolean oldValue = Boolean.valueOf(providerProperty.getValue()).booleanValue();
								if (button.getSelection() != oldValue)
								{
									providerProperty.setValue(Boolean.valueOf(button.getSelection()).toString());
									update = true;
								}
							}
							if (update)
							{
								if (persistenceService != null)
								{
									persistenceService.getServerService().merge(providerProperty);
								}
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
		this.setPartName("Warenbewirtschaftung");
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
	public void modifyText(final ModifyEvent e)
	{
		final Text text = (Text) e.getSource();
		text.setData("value", text.getText());
		ProviderPropertiesEditor.this.setDirty(true);
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
		final ProviderConfigurator configurator = input.getProviderConfigurator();
		if (configurator != null)
		{
			final Control control = this.controls
					.get(configurator.getProperties().values().toArray(new IProperty[0])[0].key());
			if (control != null)
			{
				control.setFocus();
			}
		}
	}

	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.createProviderSection(scrolledForm);
		this.createSchedulerSection(scrolledForm);
	}

	private boolean checkValues()
	{
		Text text = (Text) this.controls.get(SchedulerProperty.SCHEDULER_DELAY);
		if (text != null)
		{
			if (!this.isValidLongValue(text))
			{
				text.setFocus();
				text.setSelection(0, text.getText().length());
				this.showMessage("Die Startverzögerung muss eine ganze Zahl zwischen 0 und " + Long.MAX_VALUE + " sein");
				return false;
			}
		}
		text = (Text) this.controls.get(SchedulerProperty.SCHEDULER_PERIOD);
		if (text != null)
		{
			if (!this.isValidLongValue(text))
			{
				text.setFocus();
				text.setSelection(0, text.getText().length());
				this.showMessage("Die Wiederholungsrate muss eine ganze Zahl zwischen 0 und " + Long.MAX_VALUE
						+ " sein");
				return false;
			}
		}
		text = (Text) this.controls.get(SchedulerProperty.SCHEDULER_RECEIPT_COUNT);
		if (text != null)
		{
			if (!this.isValidIntegerValue(text))
			{
				text.setFocus();
				text.setSelection(0, text.getText().length());
				this.showMessage("Die Anzahl der pro Lauf zu übertragenden Belege muss eine ganze Zahl zwischen 0 und "
						+ Integer.MAX_VALUE + " sein");
				return false;
			}
		}
		return true;
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
				ProviderPropertiesEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createSchedulerSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Aktualisierungsplanung");
		section.setClient(this.fillSchedulerSection(section));
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

	private Control fillProviderSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(3, false));

		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		final PersistenceService persistenceService = input.getPersistenceService();
		if (persistenceService != null)
		{
			final ProviderConfigurator configurator = input.getProviderConfigurator();
			if (configurator != null)
			{
				final IProperty[] properties = configurator.getProperties().values().toArray(new IProperty[0]);
				final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getServerService()
						.getQuery(ProviderProperty.class);
				this.providerProperties = query.selectByProviderAsMap(configurator.getProviderId());
				for (final IProperty property : properties)
				{
					if (!(property instanceof SchedulerProperty))
					{
						final Label label = this.formToolkit.createLabel(composite, property.label());
						label.setLayoutData(new GridData());

						if (property.control().equals(Text.class.getName()))
						{
							final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
							gridData.horizontalSpan = 2;

							final Text text = this.formToolkit.createText(composite, "");
							text.setData("key", property.key());
							text.setLayoutData(gridData);
							text.addModifyListener(this);
							this.controls.put(property.key(), text);

						}
						else if (property.control().equals(FileDialog.class.getName()))
						{
							final Text text = this.formToolkit.createText(composite, "");
							text.setData("key", property.key());
							text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
							text.addModifyListener(this);
							this.controls.put(property.key(), text);

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
									final FileDialog dialog = new FileDialog(ProviderPropertiesEditor.this.getSite()
											.getShell());
									final Button button = (Button) event.getSource();
									final String key = (String) button.getData("key");
									final Text text = (Text) ProviderPropertiesEditor.this.controls.get(key);
									File file = new File(text.getText());
									dialog.setFileName(file.exists() ? file.getAbsolutePath() : "");
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
										text.setText(value);
										ProviderPropertiesEditor.this.setDirty(true);
									}
								}
							});
						}
						else if (property.control().equals(Button.class.getName()))
						{
							final GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
							layoutData.horizontalSpan = 2;

							final Button button = this.formToolkit.createButton(composite, property.label(), SWT.CHECK);
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
									ProviderPropertiesEditor.this.setDirty(true);
								}
							});
							this.controls.put(property.key(), button);
						}
					}
				}

				Label label = this.formToolkit.createLabel(composite, "");
				label.setLayoutData(new GridData());

				GridData gridData = new GridData();
				gridData.horizontalSpan = 2;

				Button button = this.formToolkit.createButton(composite, "Verbindung prüfen", SWT.PUSH);
				button.setLayoutData(gridData);
				button.addSelectionListener(new SelectionListener()
				{
					@Override
					public void widgetSelected(SelectionEvent e)
					{
						String providerLabel = null;
						ServiceTracker<ProviderIdService, ProviderIdService> idTracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle()
								.getBundleContext(), ProviderIdService.class, null);
						idTracker.open();
						try
						{
							ProviderIdService idService = (ProviderIdService) idTracker.getService();
							if (idService == null)
							{
								MessageDialog
										.openWarning(getSite().getShell(), "Warenbewirtschaftung",
												"Es ist kein Service für eine Anbindung an eine Warenbewirtschaftung eingerichtet.");
							}
							else
							{
								providerLabel = idService.getProviderLabel();

								ServiceTracker<ProviderInterface, ProviderInterface> providerTracker = new ServiceTracker<ProviderInterface, ProviderInterface>(Activator.getDefault().getBundle()
										.getBundleContext(), ProviderInterface.class, null);
								providerTracker.open();
								try
								{
									final ProviderInterface service = (ProviderInterface) providerTracker.getService();
									if (service == null)
									{
										MessageDialog.openWarning(getSite().getShell(), "Warenbewirtschaft",
												"Der Service für die Anbindung an die Warenbewirtschaftung " + providerLabel
														+ " ist nicht verfügbar.");
									}
									else
									{
										UIJob job = new UIJob("Stelle Verbindung zur Warenbewirtschaftung her...")
										{
											@Override
											public IStatus runInUIThread(IProgressMonitor monitor)
											{
												IStatus status = Status.OK_STATUS;
												try
												{
													monitor.beginTask(
															"Es wird versucht, eine Verbindung zur Warenbewirtschftung herzustellen...",
															IProgressMonitor.UNKNOWN);
													Map<String, IProperty> localProperties = new HashMap<String, IProperty>();
													Map<String, IProperty> properties = configurator.getProperties();
													for (IProperty property : properties.values())
													{
														Property localProperty = new Property(property.control(), property
																.filter(), property.key(), property.label(), property.value());
														Control control = controls.get(property.key());
														if (control instanceof Text)
														{
															localProperty.setValue(((Text) control).getText());
														}
														else if (control instanceof Button)
														{
															localProperty.setValue(String.valueOf(((Button) control)
																	.getSelection()));
														}
														localProperties.put(localProperty.key(), localProperty);

													}
													status = service.checkConnection(localProperties);
												}
												finally
												{
													monitor.done();
												}
												return status;
											}
										};
										job.addJobChangeListener(new JobChangeAdapter()
										{
											@Override
											public void done(IJobChangeEvent event)
											{
												if (event.getResult().getSeverity() == IStatus.ERROR)
												{
													MessageDialog.openError(getSite().getShell(), "Warenbewirtschaftung",
															event.getResult().getMessage());
												}
												else
												{
													MessageDialog.openInformation(getSite().getShell(), "Warenbewirtschaftung",
															event.getResult().getMessage());
												}
											}
										});
										job.setUser(true);
										job.schedule();
									}
								}
								finally
								{
									providerTracker.close();
								}
							}
						}
						finally
						{
							idTracker.close();
						}
					}

					@Override
					public void widgetDefaultSelected(SelectionEvent e)
					{
						widgetSelected(e);
					}
				});
			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillSchedulerSection(final Section parent)
	{
		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
		composite.setLayout(new GridLayout(3, false));

		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		final PersistenceService persistenceService = input.getPersistenceService();
		if (persistenceService != null)
		{
			final ProviderConfigurator configurator = input.getProviderConfigurator();
			if (configurator != null)
			{
				final Collection<IProperty> properties = configurator.getProperties().values();
				final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getServerService()
						.getQuery(ProviderProperty.class);
				this.providerProperties = query.selectByProviderAsMap(configurator.getProviderId());
				for (final IProperty property : properties)
				{
					if (property instanceof SchedulerProperty)
					{
						final Label label = this.formToolkit.createLabel(composite, property.label());
						label.setLayoutData(new GridData());

						if (property.control().equals(Text.class.getName()))
						{
							final GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
							gridData.horizontalSpan = 2;

							final Text text = this.formToolkit.createText(composite, "");
							text.setData("key", property.key());
							text.setLayoutData(gridData);
							text.addModifyListener(this);
							text.addVerifyListener(new VerifyListener()
							{
								@Override
								public void verifyText(final VerifyEvent e)
								{
									if (e.getSource() instanceof Text)
									{
										final Text text = (Text) e.getSource();
										if ((text.getData("key") != null)
												&& (text.getData("key").equals(SchedulerProperty.SCHEDULER_DELAY)
														|| text.getData("key").equals(
																SchedulerProperty.SCHEDULER_PERIOD) || text.getData(
														"key").equals(SchedulerProperty.SCHEDULER_RECEIPT_COUNT)))
										{
											if ("0123456789".indexOf(e.character) == -1)
											{
												e.doit = false;
											}
										}
									}

								}
							});
							this.controls.put(property.key(), text);

						}
						else if (property.control().equals(FileDialog.class.getName()))
						{
							final Text text = this.formToolkit.createText(composite, "");
							text.setData("key", property.key());
							text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
							text.addModifyListener(this);
							this.controls.put(property.key(), text);

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
									final FileDialog dialog = new FileDialog(ProviderPropertiesEditor.this.getSite()
											.getShell());
									final Button button = (Button) event.getSource();
									final String key = (String) button.getData("key");
									final Text text = (Text) ProviderPropertiesEditor.this.controls.get(key);
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
										text.setText(value);
										ProviderPropertiesEditor.this.setDirty(true);
									}
								}
							});
						}
						else if (property.control().equals(Button.class.getName()))
						{
							final GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
							layoutData.horizontalSpan = 2;

							final Button button = this.formToolkit.createButton(composite, property.label(), SWT.CHECK);
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
									ProviderPropertiesEditor.this.setDirty(true);
								}
							});
							this.controls.put(property.key(), button);
						}
					}
				}
			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private boolean isValidIntegerValue(final Text text)
	{
		try
		{
			Integer.valueOf(text.getText());
		}
		catch (final NumberFormatException e)
		{
			return false;
		}
		return true;
	}

	private boolean isValidLongValue(final Text text)
	{
		try
		{
			Long.valueOf(text.getText());
		}
		catch (final NumberFormatException e)
		{
			return false;
		}
		return true;
	}

	private void loadValues()
	{
		final ProviderPropertiesEditorInput input = (ProviderPropertiesEditorInput) this.getEditorInput();
		final ProviderConfigurator configurator = input.getProviderConfigurator();
		if (configurator != null)
		{
			final Collection<IProperty> properties = configurator.getProperties().values();
			for (final IProperty property : properties)
			{
				final Control control = this.controls.get(property.key());
				if (control != null)
				{
					if (property.control().equals(FileDialog.class.getName()))
					{
						final Text text = (Text) this.controls.get(property.key());
						final ProviderProperty providerProperty = this.providerProperties.get(property.key());
						if (providerProperty == null)
						{
							String value = property.value();
							if (System.getProperty("os.name").toLowerCase().contains("win"))
							{
								value = value.replace('/', '\\');
							}
							text.setText(value);
						}
						else
						{
							String value = providerProperty.getValue();
							if (System.getProperty("os.name").toLowerCase().contains("win"))
							{
								value = value.replace('/', '\\');
							}
							text.setText(value);
						}
					}
					else if (property.control().equals(Text.class.getName()))
					{
						final Text text = (Text) this.controls.get(property.key());
						final ProviderProperty providerProperty = this.providerProperties.get(property.key());
						if (providerProperty == null)
						{
							text.setText(property.value());
						}
						else
						{
							text.setText(providerProperty.getValue());
						}
					}
					else if (property.control().equals(Button.class.getName()))
					{
						final Button button = (Button) this.controls.get(property.key());
						final ProviderProperty providerProperty = this.providerProperties.get(property.key());
						if (providerProperty == null)
						{
							button.setSelection(Boolean.valueOf(property.value()));
						}
						else
						{
							final boolean value = Boolean.valueOf(providerProperty.getValue()).booleanValue();
							button.setSelection(value);
						}
					}
				}
			}
		}
	}

	private void setDirty(final boolean dirty)
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

	private class Property implements IProperty
	{
		private final String control;

		private final String[] filter;

		private final String key;

		private final String label;

		private String value;

		public Property(String control, String[] filter, String key, String label, String value)
		{
			this.control = control;
			this.filter = filter;
			this.key = key;
			this.label = label;
			this.value = value;
		}

		public void setValue(String value)
		{
			this.value = value;
		}

		@Override
		public String control()
		{
			return control;
		}

		@Override
		public String[] filter()
		{
			return filter;
		}

		@Override
		public String key()
		{
			return key;
		}

		@Override
		public String label()
		{
			return label;
		}

		@Override
		public String value()
		{
			return value;
		}

	}
}
