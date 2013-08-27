package ch.eugster.colibri.admin.periphery.editors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.nebula.jface.viewer.radiogroup.RadioGroupViewer;
import org.eclipse.nebula.widgets.radiogroup.RadioGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
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

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.PrintoutArea;
import ch.eugster.colibri.persistence.model.PrintoutArea.PrintOption;
import ch.eugster.colibri.print.section.ILayoutSection;
import ch.eugster.colibri.print.section.ILayoutSection.AreaType;
import ch.eugster.colibri.print.section.ILayoutSectionType;
import ch.eugster.colibri.print.section.ILayoutType;
import ch.eugster.colibri.print.service.PrintService;

public class PrintoutEditor extends AbstractEntityEditor<Printout> implements EventHandler
{
	public static final String ID = "ch.eugster.colibri.admin.periphery.printout.layout.editor";

	private Button automaticPrint;

	private Button printTest;

	private IDialogSettings dialogSettings;

	private final Collection<Font> fonts = new ArrayList<Font>();

	private ILayoutType layoutType;

	private final Map<ILayoutSectionType, Map<AreaType, RadioGroupViewer>> printOptions = new HashMap<ILayoutSectionType, Map<AreaType, RadioGroupViewer>>();

	private final Map<ILayoutSectionType, Map<AreaType, StyledText>> patterns = new HashMap<ILayoutSectionType, Map<AreaType, StyledText>>();

	private ServiceTracker<EventAdmin, EventAdmin> eventTracker;

	@Override
	public void dispose()
	{
		for (final Font font : this.fonts)
		{
			font.dispose();
		}
		this.eventTracker.close();
		super.dispose();
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals("ch/eugster/colibri/periphery/printer/error"))
		{
			StringBuilder message = new StringBuilder("Der Belegdrucker kann nicht angesprochen werden. ");
			message = message.append("Bitte vergewissern Sie sich, dass er:\n");
			message = message.append("- eingeschaltet ist");
			message = message.append("- am Computer angeschlossen ist");
			message = message.append("- vom Computer erkannt worden ist");
			final Shell shell = this.getSite().getShell();
			final ErrorDialog dialog = new ErrorDialog(shell, "Belegdrucker", message.toString(),
					(IStatus) event.getProperty("status"), 0);
			dialog.open();
		}
	}

	@Override
	public void init(final IEditorSite site, final IEditorInput input) throws PartInitException
	{
		super.init(site, input);
		final PrintoutEditorInput printoutEditorInput = (PrintoutEditorInput) input;
		final PrintService service = (PrintService) printoutEditorInput.getAdapter(PrintService.class);
		final Printout printout = (Printout) printoutEditorInput.getAdapter(Printout.class);
		this.layoutType = service.getLayoutType(printout.getReceiptPrinterSettings().getComponentName());
		this.eventTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventTracker.open();

		this.dialogSettings = Activator.getDefault().getDialogSettings().getSection("printout.editor.sections");
		if (this.dialogSettings == null)
		{
			this.dialogSettings = new DialogSettings("printout.editor.sections");
		}
	}

	@Override
	public void setFocus()
	{
		if (!this.patterns.isEmpty())
		{
			final Collection<Map<AreaType, StyledText>> patterns = this.patterns.values();
			if (!patterns.isEmpty())
			{
				final Collection<StyledText> texts = patterns.iterator().next().values();
				if (!texts.isEmpty())
				{
					texts.iterator().next().setFocus();
				}
			}
		}
	}

	@Override
	protected void createSections(final ScrolledForm parent)
	{
		this.createMainSection(parent);
		for (final ILayoutSectionType layoutAreaType : this.layoutType.getLayoutSectionTypes())
		{
			if (layoutAreaType.isCustomerEditable())
			{
				this.createAreaSection(parent, layoutAreaType);
			}
		}
	}

	@Override
	protected String getName()
	{
		final PrintService service = (PrintService) this.getEditorInput().getAdapter(PrintService.class);
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
		final PrintService service = (PrintService) this.getEditorInput().getAdapter(PrintService.class);
		if (service instanceof PrintService)
		{
			final Printout printout = (Printout) this.getEditorInput().getAdapter(Printout.class);
			if (printout.getSalespoint() == null)
			{
				if (printout.getReceiptPrinterSettings() != null)
				{
					return service.getLayoutType(printout.getReceiptPrinterSettings().getComponentName()).getName()
							+ " " + printout.getReceiptPrinterSettings().getName();
				}
			}
			else
			{
				if (printout.getSalespoint().getReceiptPrinterSettings() != null)
				{
					return printout.getSalespoint().getReceiptPrinterSettings().getName() + " "
							+ printout.getSalespoint().getName();
				}
			}
		}
		return "???";
	}

	@Override
	protected void loadValues()
	{
		final Printout printout = ((PrintoutEditorInput) this.getEditorInput()).getEntity();

		if (this.layoutType.automaticPrintSelectable())
		{
			if (printout.getId() == null)
			{
				printout.setAutomaticPrint(this.layoutType.automaticPrint());
			}
			this.automaticPrint.setSelection(printout.isAutomaticPrint());
		}

		for (final ILayoutSectionType layoutSectionType : this.layoutType.getLayoutSectionTypes())
		{
			if (layoutSectionType.isCustomerEditable())
			{
				PrintoutArea printoutArea = printout.getPrintoutArea(layoutSectionType.ordinal());
				if ((printoutArea == null) || printoutArea.isDeleted())
				{
					if (printout.getParent() != null)
					{
						printoutArea = printout.getParent().getPrintoutArea(layoutSectionType.ordinal());
					}
				}
				if ((printoutArea == null) || printoutArea.isDeleted())
				{
					this.loadDefaultValues(layoutSectionType);
				}
				else
				{
					this.loadPrintoutAreaValues(printoutArea);
				}
			}
		}
		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		final Printout printout = ((PrintoutEditorInput) this.getEditorInput()).getEntity();

		if (this.layoutType.automaticPrintSelectable())
		{
			printout.setAutomaticPrint(this.automaticPrint.getSelection());
		}

		for (final ILayoutSectionType layoutSectionType : this.layoutType.getLayoutSectionTypes())
		{
			PrintoutArea printoutArea = printout.getPrintoutArea(layoutSectionType.ordinal());

			if (this.inputEqualsLayoutAreaDefault(layoutSectionType))
			{
				if (printoutArea != null)
				{
					if (!printoutArea.isDeleted())
					{
						printoutArea.setDeleted(true);
					}
				}
			}
			else
			{
				if ((printoutArea == null))
				{
					printoutArea = PrintoutArea.newInstance(printout, layoutSectionType.ordinal());
					printout.addPrintoutArea(printoutArea);
				}
				else if (printoutArea.isDeleted())
				{
					printoutArea.setDeleted(false);
				}

				this.savePatterns(layoutSectionType, printoutArea);
				this.savePrintOptions(layoutSectionType, printoutArea);
			}
		}
	}

	@Override
	protected boolean validate()
	{
		return true;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Printout> input)
	{
		return input.getEntity() instanceof Printout;
	}

	private Section createAreaSection(final ScrolledForm scrolledForm, final ILayoutSectionType layoutAreaType)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText(layoutAreaType.getSectionTitle());
		section.setClient(this.fillAreaSection(section, layoutAreaType));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				PrintoutEditor.this.scrolledForm.reflow(true);
				PrintoutEditor.this.dialogSettings.put(layoutAreaType.getSectionId() + ".expanded", e.getState());
			}
		});
		section.setExpanded(this.dialogSettings.getBoolean(layoutAreaType.getSectionId() + ".expanded"));
		return section;
	}

	private Section createMainSection(final ScrolledForm parent)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(parent.getBody(), ExpandableComposite.EXPANDED
				| ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Gesamtlayout");
		section.setClient(this.fillMainSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				PrintoutEditor.this.scrolledForm.reflow(true);
				PrintoutEditor.this.dialogSettings.put("section.main.expanded", e.getState());
			}
		});
		section.setExpanded(this.dialogSettings.getBoolean("section.main.expanded"));
		return section;
	}

	private Control fillAreaSection(final Section parent, final ILayoutSectionType layoutSectionType)
	{
		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final GridLayout layout = new GridLayout(2, false);

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = 2;

		for (final AreaType areaType : AreaType.values())
		{
			if (layoutSectionType.getLayoutSection().hasArea(areaType))
			{
				Map<AreaType, RadioGroupViewer> printOptions = this.printOptions.get(layoutSectionType);
				if (printOptions == null)
				{
					printOptions = new HashMap<AreaType, RadioGroupViewer>();
					this.printOptions.put(layoutSectionType, printOptions);
				}
				Map<AreaType, StyledText> patterns = this.patterns.get(layoutSectionType);
				if (patterns == null)
				{
					patterns = new HashMap<AreaType, StyledText>();
					this.patterns.put(layoutSectionType, patterns);
				}

				Label label = this.formToolkit.createLabel(composite, areaType.label(), SWT.BOLD);
				final FontData labelFontData = label.getFont().getFontData()[0];
				final Font bold = new Font(this.getSite().getShell().getDisplay(), labelFontData.getName(),
						labelFontData.getHeight(), SWT.BOLD);
				label.setFont(bold);
				this.fonts.add(bold);
				label.setLayoutData(new GridData());

				final RadioGroup radioGroup = new RadioGroup(composite, SWT.NONE);
				radioGroup.setLayout(new RowLayout(SWT.HORIZONTAL));
				radioGroup.setBackground(Display.getCurrent().getSystemColor(SWT.COLOR_WHITE));

				final RadioGroupViewer viewer = new RadioGroupViewer(radioGroup);
				viewer.setContentProvider(new ArrayContentProvider());
				viewer.setLabelProvider(new PrintOptionLabelProvider());
				viewer.setInput(PrintOption.values());
				for (final Control control : radioGroup.getChildren())
				{
					control.setBackground(composite.getBackground());
				}
				printOptions.put(areaType, viewer);

				label = this.formToolkit.createLabel(composite, layoutSectionType.getLayoutSection().getHelp(areaType));
				label.setLayoutData(new GridData());

				final Font font = new Font(this.getSite().getShell().getDisplay(), "Courier", 10, SWT.NORMAL);
				final StyledText pattern = new StyledText(composite, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.H_SCROLL);
				pattern.setFont(font);
				this.fonts.add(font);

				final GC gc = new GC(pattern);
				final FontMetrics fm = gc.getFontMetrics();
				final int width = this.getColumns() * fm.getAverageCharWidth() + 4;
				gc.dispose();

				gridData = new GridData(GridData.FILL_VERTICAL);
				gridData.widthHint = width;
				gridData.heightHint = layoutSectionType.getLayoutAreaHeight() < 1 ? SWT.DEFAULT : layoutSectionType
						.getLayoutAreaHeight();

				pattern.setLayoutData(gridData);
				pattern.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
				pattern.addModifyListener(new ModifyListener()
				{
					@Override
					public void modifyText(final ModifyEvent e)
					{
						PrintoutEditor.this.setDirty(true);
					}

				});
				this.formToolkit.adapt(pattern);
				patterns.put(areaType, pattern);
			}
		}

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillMainSection(final Section parent)
	{
		final TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final GridLayout layout = new GridLayout(2, false);

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		if (this.layoutType.automaticPrintSelectable())
		{
			this.automaticPrint = this.formToolkit.createButton(composite, "Automatischer Druck aktiviert", SWT.CHECK);
			this.automaticPrint.setLayoutData(new GridData());
			this.automaticPrint.addSelectionListener(new SelectionListener()
			{
				@Override
				public void widgetDefaultSelected(final SelectionEvent e)
				{
					this.widgetSelected(e);
				}

				@Override
				public void widgetSelected(final SelectionEvent e)
				{
					PrintoutEditor.this.setDirty(true);
				}
			});
		}
		this.printTest = this.formToolkit.createButton(composite, "Testdruck", SWT.PUSH);
		this.printTest.setLayoutData(new GridData());
		this.printTest.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				PrintoutEditorInput input = (PrintoutEditorInput) PrintoutEditor.this.getEditorInput();
				PrintService printService = (PrintService) input.getAdapter(PrintService.class);

				final Printout printout = (Printout) PrintoutEditor.this.getEditorInput().getAdapter(Printout.class);
				final ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptPrinterTracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle()
						.getBundleContext(), ReceiptPrinterService.class, null);
				receiptPrinterTracker.open();
				final ServiceReference<ReceiptPrinterService>[] receiptPrinterReferences = receiptPrinterTracker.getServiceReferences();
				for (final ServiceReference<ReceiptPrinterService> receiptPrinterReference : receiptPrinterReferences)
				{
					if (receiptPrinterReference.getProperty("component.name").equals(
							printout.getReceiptPrinterSettings().getComponentName()))
					{
						final ReceiptPrinterService receiptPrinterService = (ReceiptPrinterService) receiptPrinterTracker
								.getService(receiptPrinterReference);
						if (receiptPrinterService != null)
						{
							final ILayoutSectionType[] layoutSectionTypes = PrintoutEditor.this.layoutType
									.getLayoutSectionTypes();
							for (final ILayoutSectionType layoutSectionType : layoutSectionTypes)
							{
								final ILayoutSection layoutSection = layoutSectionType.getLayoutSection();
								for (final AreaType areaType : AreaType.values())
								{
									if (layoutSection.hasArea(areaType))
									{
										final StyledText pattern = PrintoutEditor.this.getStyledText(
												layoutSectionType, areaType);
										if (pattern != null)
										{
											layoutSection.setPattern(areaType, pattern.getText());
										}
										final RadioGroupViewer viewer = PrintoutEditor.this
												.getRadioGroupViewer(layoutSectionType, areaType);
										if (viewer != null)
										{
											final StructuredSelection ssel = (StructuredSelection) viewer
													.getSelection();
											if (ssel.getFirstElement() instanceof PrintOption)
											{
												layoutSection.setPrintOption(areaType,
														((PrintOption) ssel.getFirstElement()));
											}
										}
									}
								}
								layoutSectionType.setColumnCount(receiptPrinterService
										.getReceiptPrinterSettings().getCols());
							}
							printService.testDocument(PrintoutEditor.this.layoutType, printout);
						}
					}
				}
				receiptPrinterTracker.close();
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private int getColumns()
	{
		final Printout printout = (Printout) this.getEditorInput().getAdapter(Printout.class);
		return printout.getColumns();
	}

	private RadioGroupViewer getRadioGroupViewer(final ILayoutSectionType layoutSectionType, final AreaType areaType)
	{
		if (layoutSectionType.isCustomerEditable())
		{
			final Map<AreaType, RadioGroupViewer> printOptions = this.printOptions.get(layoutSectionType);
			if ((printOptions != null) && !printOptions.isEmpty())
			{
				return printOptions.get(areaType);
			}
		}
		return null;
	}

	// private PrintOption getPrintOption(final ButtonViewer viewer)
	// {
	// final StructuredSelection ssel = (StructuredSelection)
	// viewer.getSelection();
	// final int value = ((Integer) ssel.getFirstElement()).intValue();
	// return PrintOption.values()[value];
	// }

	private StyledText getStyledText(final ILayoutSectionType layoutSectionType, final AreaType areaType)
	{
		if (layoutSectionType.isCustomerEditable())
		{
			final Map<AreaType, StyledText> patterns = this.patterns.get(layoutSectionType);
			if ((patterns != null) && !patterns.isEmpty())
			{
				return patterns.get(areaType);
			}
		}
		return null;
	}

	private boolean inputEqualsLayoutAreaDefault(final ILayoutSectionType layoutSectionType)
	{
		if (layoutSectionType.isCustomerEditable())
		{
			for (final AreaType areaType : AreaType.values())
			{
				if (layoutSectionType.getLayoutSection().hasArea(areaType))
				{
					if (!this.printOptionInputEqualsLayoutAreaDefault(layoutSectionType, areaType))
					{
						return false;
					}

					final StyledText text = this.getStyledText(layoutSectionType, areaType);
					if (!text.getText().equals(layoutSectionType.getLayoutSection().getDefaultPattern(areaType)))
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	private void loadDefaultValues(final ILayoutSectionType layoutSectionType)
	{
		if (layoutSectionType.isCustomerEditable())
		{
			for (final AreaType areaType : AreaType.values())
			{
				if (layoutSectionType.getLayoutSection().hasArea(areaType))
				{
					final RadioGroupViewer viewer = this.getRadioGroupViewer(layoutSectionType, areaType);
					final PrintOption printOption = layoutSectionType.getLayoutSection().getDefaultPrintOption(areaType);
					final StructuredSelection ssel = new StructuredSelection(printOption);
					viewer.setSelection(ssel);

					final StyledText pattern = this.getStyledText(layoutSectionType, areaType);
					pattern.setText(layoutSectionType.getLayoutSection().getDefaultPattern(areaType));
				}
			}
		}
	}

	private void loadPrintoutAreaValues(final PrintoutArea printoutArea)
	{
		final ILayoutSectionType layoutSectionType = this.layoutType.getLayoutSectionTypes()[printoutArea
				.getPrintAreaType()];

		if (layoutSectionType.isCustomerEditable())
		{
			for (final AreaType areaType : AreaType.values())
			{
				if (layoutSectionType.getLayoutSection().hasArea(areaType))
				{
					final StyledText text = this.getStyledText(layoutSectionType, areaType);
					if (text != null)
					{
						final String pattern = printoutArea.getPattern(areaType.ordinal());
						text.setText(pattern == null ? "" : pattern);

						final RadioGroupViewer viewer = this.getRadioGroupViewer(layoutSectionType, areaType);
						final PrintOption printOption = printoutArea.getPrintOption(areaType.ordinal());
						if (printOption == null)
						{
							viewer.setSelection(new StructuredSelection(new PrintOption[] { layoutSectionType
									.getLayoutSection().getDefaultPrintOption(areaType) }));
						}
						else
						{
							viewer.setSelection(new StructuredSelection(new PrintOption[] { printOption }));
						}
					}
				}
			}
		}
	}

	private boolean printOptionInputEqualsLayoutAreaDefault(final ILayoutSectionType layoutSectionType,
			final AreaType areaType)
	{
		if (layoutSectionType.isCustomerEditable())
		{
			final RadioGroupViewer viewer = this.getRadioGroupViewer(layoutSectionType, areaType);
			if (viewer != null)
			{
				final StructuredSelection ssel = (StructuredSelection) viewer.getSelection();
				if (ssel.getFirstElement() instanceof PrintOption)
				{
					return layoutSectionType.getLayoutSection().getDefaultPrintOption(areaType)
							.equals(ssel.getFirstElement());
				}
			}
		}
		return false;
	}

	private void savePatterns(final ILayoutSectionType layoutSectionType, final PrintoutArea printoutArea)
	{
		if (layoutSectionType.isCustomerEditable())
		{
			for (final AreaType areaType : AreaType.values())
			{
				if (layoutSectionType.getLayoutSection().hasArea(areaType))
				{
					final StyledText pattern = this.getStyledText(layoutSectionType, areaType);
					if (pattern != null)
					{
						if ((printoutArea.getPattern(areaType.ordinal()) == null)
								|| !pattern.getText().equals(printoutArea.getPattern(areaType.ordinal())))
						{
							printoutArea.setPattern(areaType.ordinal(), pattern.getText());
						}
					}
				}
			}
		}
	}

	private void savePrintOptions(final ILayoutSectionType layoutSectionType, final PrintoutArea printoutArea)
	{
		if (layoutSectionType.isCustomerEditable())
		{
			for (final AreaType areaType : AreaType.values())
			{
				if (layoutSectionType.getLayoutSection().hasArea(areaType))
				{
					final RadioGroupViewer viewer = this.getRadioGroupViewer(layoutSectionType, areaType);
					if (viewer != null)
					{
						final StructuredSelection ssel = (StructuredSelection) viewer.getSelection();
						if (ssel.getFirstElement() instanceof PrintOption)
						{
							printoutArea.setPrintOption(areaType.ordinal(), (PrintOption) ssel.getFirstElement());
						}
					}
				}
			}
		}
	}
}
