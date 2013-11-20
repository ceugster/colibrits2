package ch.eugster.colibri.admin.layout.display.editors;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IEditorInput;
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
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.layout.display.Activator;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.display.area.ILayoutArea;
import ch.eugster.colibri.display.area.ILayoutAreaType;
import ch.eugster.colibri.display.area.ILayoutType;
import ch.eugster.colibri.display.service.DisplayService;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.DisplayArea;

public class DisplayEditor extends AbstractEntityEditor<Display> implements EventHandler
{
	public static final String ID = "ch.eugster.colibri.admin.layout.editor.display";

	private ILayoutType layoutType;

	private final Map<ILayoutAreaType, Button> salespointSpecifics = new HashMap<ILayoutAreaType, Button>();

	private final Map<ILayoutAreaType, Spinner> timerDelays = new HashMap<ILayoutAreaType, Spinner>();

	private final Map<ILayoutAreaType, StyledText> patterns = new HashMap<ILayoutAreaType, StyledText>();

	private final Map<ILayoutAreaType, Button> testPrints = new HashMap<ILayoutAreaType, Button>();

	private ServiceTracker<EventAdmin, EventAdmin> eventTracker;

	@Override
	public void dispose()
	{
		this.eventTracker.close();
		super.dispose();
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(Topic.DISPLAY_ERROR.topic()))
		{
			StringBuilder message = new StringBuilder("Das Kundendisplay kann nicht angesprochen werden. ");
			message = message.append("Bitte vergewissern Sie sich, dass es:\n");
			message = message.append("- eingeschaltet ist");
			message = message.append("- am Computer angeschlossen ist");
			message = message.append("- vom Computer erkannt worden ist");
			final Shell shell = this.getSite().getShell();
			final ErrorDialog dialog = new ErrorDialog(shell, "Kundendisplay", message.toString(), (IStatus) event.getProperty("status"), 0);
			dialog.open();
		}
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		final DisplayEditorInput displayEditorInput = (DisplayEditorInput) input;
		final DisplayService service = (DisplayService) displayEditorInput.getAdapter(DisplayService.class);
		final Display display = (Display) displayEditorInput.getAdapter(Display.class);
		this.layoutType = service.getLayoutType(display.getCustomerDisplaySettings().getComponentName());
		this.eventTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
		this.eventTracker.open();
	}

	@Override
	public void setFocus()
	{
		if (this.patterns.values().iterator().hasNext())
		{
			final StyledText text = this.patterns.values().iterator().next();
			text.setFocus();
		}
	}

	@Override
	protected void createSections(final ScrolledForm parent)
	{
		for (final ILayoutAreaType layoutAreaType : this.layoutType.getLayoutAreaTypes())
		{
			this.createAreaSection(parent, layoutAreaType.getLayoutArea());
		}
	}

	@Override
	protected String getName()
	{
		final DisplayService service = (DisplayService) this.getEditorInput().getAdapter(DisplayService.class);
		final Object name = service.getContext().getProperties().get("custom.label");
		if (name instanceof String)
		{
			return (String) name;
		}
		else
		{
			return "???";
		}
	}

	@Override
	protected String getText()
	{
		final DisplayService service = (DisplayService) this.getEditorInput().getAdapter(DisplayService.class);
		if (service instanceof DisplayService)
		{
			final Display display = (Display) this.getEditorInput().getAdapter(Display.class);
			if (display.getSalespoint() == null)
			{
				if (display.getCustomerDisplaySettings() != null)
				{
					return service.getLayoutType(display.getCustomerDisplaySettings().getComponentName()).getName() + " "
							+ display.getCustomerDisplaySettings().getName();
				}
				else
				{
					return service.getLayoutType(display.getCustomerDisplaySettings().getComponentName()).getName();
				}
			}
			else
			{
				return service.getLayoutType(display.getCustomerDisplaySettings().getComponentName()).getName() + " "
						+ display.getSalespoint().getName();
			}
		}
		return "???";
	}

	@Override
	protected void loadValues()
	{
		final Display display = ((DisplayEditorInput) this.getEditorInput()).getEntity();

		for (final ILayoutAreaType layoutAreaType : this.layoutType.getLayoutAreaTypes())
		{
			DisplayArea displayArea = display.getDisplayArea(layoutAreaType.ordinal());
			if ((displayArea == null) || displayArea.isDeleted())
			{
				if (display.getParent() != null)
				{
					displayArea = display.getParent().getDisplayArea(layoutAreaType.ordinal());
				}
			}
			if ((displayArea == null) || displayArea.isDeleted())
			{
				this.loadAreaValues(layoutAreaType.getLayoutArea());
			}
			else
			{
				this.loadAreaValues(displayArea);
			}
		}
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final Display display = ((DisplayEditorInput) this.getEditorInput()).getEntity();

		for (final ILayoutAreaType layoutAreaType : this.layoutType.getLayoutAreaTypes())
		{
			DisplayArea displayArea = display.getDisplayArea(layoutAreaType.ordinal());

			if (this.salespointSpecifics.isEmpty())
			{
				if ((displayArea == null) || displayArea.isDeleted())
				{
					final ILayoutArea layoutArea = layoutAreaType.getLayoutArea();
					final boolean changedPattern = !layoutArea.getDefaultPattern().equals(this.patterns.get(layoutAreaType).getText());
					final boolean changedTimerDelay = layoutArea.getDefaultTimerDelay() != this.getTimerDelay(layoutAreaType);
					if (changedPattern || changedTimerDelay)
					{
						if (displayArea == null)
						{
							displayArea = DisplayArea.newInstance(display, Integer.valueOf(layoutAreaType.ordinal()));
							displayArea.setPattern(this.patterns.get(layoutAreaType).getText());
							displayArea.setTimerDelay(this.getTimerDelay(layoutAreaType));
							display.putDisplayArea(displayArea);
						}
						else
						{
							displayArea.setDeleted(false);
							displayArea.setPattern(this.patterns.get(layoutAreaType).getText());
							displayArea.setTimerDelay(this.getTimerDelay(layoutAreaType));
						}
					}
				}
				else
				{
					final boolean changedPattern = !displayArea.getPattern().equals(this.patterns.get(layoutAreaType).getText());
					final boolean changedPrintOption = displayArea.getTimerDelay() != this.getTimerDelay(layoutAreaType);
					if (changedPattern || changedPrintOption)
					{
						displayArea.setPattern(this.patterns.get(layoutAreaType).getText());
						displayArea.setTimerDelay(this.getTimerDelay(layoutAreaType));
					}
				}
			}
			else
			{
				if (this.salespointSpecifics.get(layoutAreaType).getSelection())
				{
					if ((displayArea == null) || displayArea.isDeleted())
					{
						if (displayArea == null)
						{
							displayArea = DisplayArea.newInstance(display, layoutAreaType.ordinal());
							displayArea.setPattern(this.patterns.get(layoutAreaType).getText());
							displayArea.setTimerDelay(this.getTimerDelay(layoutAreaType));
							display.addDisplayArea(displayArea);
						}
						else
						{
							displayArea.setDeleted(false);
							displayArea.setPattern(this.patterns.get(layoutAreaType).getText());
							displayArea.setTimerDelay(this.getTimerDelay(layoutAreaType));
						}
					}
					else
					{
						displayArea.setPattern(this.patterns.get(layoutAreaType).getText());
						displayArea.setTimerDelay(this.getTimerDelay(layoutAreaType));
					}
				}
				else
				{
					if (displayArea != null)
					{
						displayArea.setDeleted(true);
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
	protected boolean validateType(final AbstractEntityEditorInput<Display> input)
	{
		return input.getEntity() instanceof Display;
	}

	private Section createAreaSection(final ScrolledForm scrolledForm, final ILayoutArea layoutArea)
	{

		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText(layoutArea.getLayoutAreaType().getSectionTitle());
		section.setClient(this.fillAreaSection(section, layoutArea));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				DisplayEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillAreaSection(final Section parent, final ILayoutArea layoutArea)
	{
		final Display display = ((DisplayEditorInput) this.getEditorInput()).getEntity();

		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final GridLayout layout = new GridLayout(2, false);

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		if (display.isSalespointSpecific())
		{
			final Button button = this.formToolkit.createButton(composite, "Kassenspezifisches Layout", SWT.CHECK);
			button.setLayoutData(gridData);
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
					final boolean enabled = ((Button) e.getSource()).getSelection();
					DisplayEditor.this.setValues(layoutArea.getLayoutAreaType(), enabled);
					DisplayEditor.this.setWidgetsEnabled(layoutArea.getLayoutAreaType(), enabled);
					DisplayEditor.this.setDirty(true);
				}
			});
			this.salespointSpecifics.put(layoutArea.getLayoutAreaType(), button);
		}

		Label label = this.formToolkit.createLabel(composite, "Verzögerung vor Aktivierung (in Sekunden)");
		label.setLayoutData(new GridData());

		final Spinner spinner = new Spinner(composite, SWT.NONE);
		spinner.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		spinner.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				DisplayEditor.this.setDirty(true);
			}
		});
		spinner.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				DisplayEditor.this.setDirty(true);
			}
		});
		spinner.setIncrement(1);
		spinner.setPageIncrement(10);
		this.formToolkit.adapt(spinner);
		this.timerDelays.put(layoutArea.getLayoutAreaType(), spinner);

		label = this.formToolkit.createLabel(composite, layoutArea.getHelp());
		label.setLayoutData(new GridData());

		final Font font = new Font(this.getSite().getShell().getDisplay(), "Courier", 10, SWT.NORMAL);
		final StyledText pattern = new StyledText(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
		pattern.setFont(font);

		final GC gc = new GC(pattern);
		final FontMetrics fm = gc.getFontMetrics();
		final int width = this.getColumns() * fm.getAverageCharWidth() + 4;
		gc.dispose();

		gridData = new GridData(GridData.FILL_VERTICAL);
		gridData.widthHint = width;
		gridData.heightHint = this.getRows() * 12;

		pattern.setLayoutData(gridData);
		pattern.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		pattern.addModifyListener(new ModifyListener()
		{
			@Override
			public void modifyText(final ModifyEvent e)
			{
				DisplayEditor.this.setDirty(true);
			}

		});
		this.formToolkit.adapt(pattern);
		this.patterns.put(layoutArea.getLayoutAreaType(), pattern);

		final Button button = this.formToolkit.createButton(composite, "Anzeige testen", SWT.PUSH);
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
				final Display display = (Display) DisplayEditor.this.getEditorInput().getAdapter(Display.class);
				final ServiceTracker<CustomerDisplayService, CustomerDisplayService> customerDisplayTracker = new ServiceTracker<CustomerDisplayService, CustomerDisplayService>(Activator.getDefault().getBundle().getBundleContext(),
						CustomerDisplayService.class, null);
				customerDisplayTracker.open();
				final ServiceReference<CustomerDisplayService>[] receiptPrinterReferences = customerDisplayTracker.getServiceReferences();
				for (final ServiceReference<CustomerDisplayService> customerDisplayReference : receiptPrinterReferences)
				{
					if (customerDisplayReference.getProperty("component.name").equals(display.getCustomerDisplaySettings().getComponentName()))
					{
						final CustomerDisplayService customerDisplayService = (CustomerDisplayService) customerDisplayTracker
								.getService(customerDisplayReference);
						if (customerDisplayService != null)
						{
							final ServiceTracker<DisplayService, DisplayService> displayTracker = new ServiceTracker<DisplayService, DisplayService>(Activator.getDefault().getBundle().getBundleContext(),
									DisplayService.class, null);
							displayTracker.open();
							final ServiceReference<DisplayService>[] displayReferences = displayTracker.getServiceReferences();
							for (final ServiceReference<DisplayService> displayReference : displayReferences)
							{
								if (displayReference.getProperty("component.name").toString().startsWith(display.getDisplayType()))
								{
									final DisplayService displayService = (DisplayService) displayTracker.getService(displayReference);
									layoutArea.setPattern(pattern.getText());
									layoutArea.setTimerDelay(spinner.getSelection());
									displayService.displayTestMessage(layoutArea, display);
								}
							}
							displayTracker.close();
						}
					}
				}
				customerDisplayTracker.close();
			}
		});
		this.testPrints.put(layoutArea.getLayoutAreaType(), button);

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private int getColumns()
	{
		final Display display = (Display) this.getEditorInput().getAdapter(Display.class);
		return display.getColumns();
	}

	private int getRows()
	{
		final Display display = (Display) this.getEditorInput().getAdapter(Display.class);
		return display.getRows();
	}

	private int getTimerDelay(final ILayoutAreaType layoutAreaType)
	{
		return this.timerDelays.get(layoutAreaType).getSelection();
	}

	private void loadAreaValues(final DisplayArea displayArea)
	{
		if (!this.salespointSpecifics.isEmpty())
		{
			final ILayoutAreaType layoutAreaType = this.layoutType.getLayoutAreaTypes()[displayArea.getDisplayAreaType()];
			final boolean enabled = !displayArea.isDeleted() && displayArea.getDisplay().isSalespointSpecific();
			this.salespointSpecifics.get(layoutAreaType).setSelection(enabled);
			this.setWidgetsEnabled(layoutAreaType, enabled);
		}
		this.loadTimerDelayValue(displayArea);
		this.loadPatternValue(displayArea);
	}

	private void loadAreaValues(final ILayoutArea layoutArea)
	{
		if (!this.salespointSpecifics.isEmpty())
		{
			final ILayoutAreaType layoutAreaType = layoutArea.getLayoutAreaType();
			this.salespointSpecifics.get(layoutAreaType).setSelection(false);
			this.timerDelays.get(layoutAreaType).setEnabled(false);
			this.patterns.get(layoutAreaType).setEnabled(false);
			this.patterns.get(layoutAreaType).setBackground(
					org.eclipse.swt.widgets.Display.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
		}
		this.loadTimerDelayValue(layoutArea);
		this.loadPatternValue(layoutArea);
	}

	private void loadPatternValue(final DisplayArea printoutArea)
	{
		final StyledText text = this.patterns.get(this.layoutType.getLayoutAreaTypes()[printoutArea.getDisplayAreaType()]);
		text.setText(printoutArea.getPattern());
	}

	private void loadPatternValue(final ILayoutArea layoutArea)
	{
		final StyledText text = this.patterns.get(layoutArea.getLayoutAreaType());
		text.setText(layoutArea.getDefaultPattern());
	}

	private void loadTimerDelayValue(final DisplayArea displayArea)
	{
		final ILayoutAreaType layoutAreaType = this.layoutType.getLayoutAreaTypes()[displayArea.getDisplayAreaType()];
		final Spinner spinner = this.timerDelays.get(layoutAreaType);
		spinner.setSelection(displayArea.getTimerDelay());
	}

	private void loadTimerDelayValue(final ILayoutArea layoutArea)
	{
		final Spinner spinner = this.timerDelays.get(this.layoutType.getLayoutAreaTypes()[layoutArea.getLayoutAreaType().ordinal()]);
		spinner.setSelection(layoutArea.getDefaultTimerDelay());
	}

	private void setWidgetsEnabled(final ILayoutAreaType layoutAreaType, final boolean enabled)
	{
		this.timerDelays.get(layoutAreaType).setEnabled(enabled);
		this.patterns.get(layoutAreaType).setEnabled(enabled);
		this.patterns.get(layoutAreaType).setBackground(
				enabled ? org.eclipse.swt.widgets.Display.getCurrent().getSystemColor(SWT.COLOR_WHITE) : org.eclipse.swt.widgets.Display
						.getCurrent().getSystemColor(SWT.COLOR_WIDGET_LIGHT_SHADOW));
	}

	private void setValues(final ILayoutAreaType layoutAreaType, final boolean enabled)
	{
		if (!enabled)
		{
			this.timerDelays.get(layoutAreaType).setSelection(layoutAreaType.getLayoutArea().getDefaultTimerDelay());
			this.patterns.get(layoutAreaType).setText(layoutAreaType.getLayoutArea().getDefaultPattern());
		}
	}
}
