/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.configurable;

import java.awt.Frame;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.eugster.colibri.admin.profile.editors.composites.FontSizeComposite;
import ch.eugster.colibri.admin.profile.editors.composites.FontStyleComposite;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Profile;

public class ConfigurableEditor extends AbstractEntityEditor<Configurable>
{
	public static final String ID = "ch.eugster.colibri.admin.profile.configurable.editor";

	private ScrolledForm scrolledForm;

	private JTabbedPane exampleTabbedPane;

	private FontSizeComposite tabFontSizeComposite;

	private FontStyleComposite tabFontStyleComposite;

	private ColorSelector tabFgSelected;

	private ColorSelector tabFg;

	private ColorSelector tabBg;

	public ConfigurableEditor()
	{
	}

	@Override
	public void dispose()
	{
		EntityMediator.removeListener(Profile.class, this);
		EntityMediator.removeListener(Configurable.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		final ConfigurableEditorInput input = (ConfigurableEditorInput) getEditorInput();
		final Configurable configurable = (Configurable) input.getAdapter(Configurable.class);

		if (entity instanceof Profile)
		{
			if (entity.equals(configurable.getProfile()))
			{
				dispose();
			}
		}
		else if (entity instanceof Configurable)
		{
			if (entity.equals(configurable))
			{
				dispose();
			}
		}
	}

	@Override
	public void propertyChanged(final Object source, final int propId)
	{
		if (propId == IEditorPart.PROP_DIRTY)
		{
			setDirty(true);
		}
//		else if (propId == IEditorPart.PROP_INPUT)
//		{
//		}
	}

	@Override
	public void setFocus()
	{
		tabFontSizeComposite.setFocus();
	}

	@Override
	protected void createSections(final ScrolledForm scrolledForm)
	{
		this.scrolledForm = scrolledForm;
		createDesignSection(scrolledForm);
		EntityMediator.addListener(Profile.class, this);
		EntityMediator.addListener(Configurable.class, this);
	}

	@Override
	protected String getName()
	{
		final Configurable configurable = (Configurable) ((ConfigurableEditorInput) getEditorInput()).getAdapter(Configurable.class);
		if (configurable.getId() == null)
		{
			return "Neu";
		}
		return configurable.getType().toString();
	}

	// @Override
	// protected Message getMessage(ErrorCode errorCode)
	// {
	// Message msg = null;
	//
	// return msg;
	// }

	@Override
	protected String getText()
	{
		final Configurable configurable = (Configurable) ((ConfigurableEditorInput) getEditorInput()).getAdapter(Configurable.class);
		return configurable.getType().toString();
	}

	@Override
	protected void loadValues()
	{
		final Configurable configurable = (Configurable) ((ConfigurableEditorInput) getEditorInput()).getAdapter(Configurable.class);
		/*
		 * TabbedPane
		 */
		final float size = configurable.getFontSize();
		final int style = configurable.getFontStyle();
		final java.awt.Color fgSelected = new java.awt.Color(configurable.getFgSelected());
		final java.awt.Color fg = new java.awt.Color(configurable.getFg());
		final java.awt.Color bg = new java.awt.Color(configurable.getBg());

		tabFontSizeComposite.setSize(size);
		tabFontStyleComposite.setStyle(style);
		tabFgSelected.setColorValue(new RGB(fgSelected.getRed(), fgSelected.getGreen(), fgSelected.getBlue()));
		tabFg.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		tabBg.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));

		exampleTabbedPane.setFont(exampleTabbedPane.getFont().deriveFont(style, size));
		for (int i = 0; i < exampleTabbedPane.getTabCount(); i++)
		{
			if (i == exampleTabbedPane.getSelectedIndex())
			{
				exampleTabbedPane.setForegroundAt(i, fgSelected);
			}
			else
			{
				exampleTabbedPane.setForegroundAt(i, fg);
				exampleTabbedPane.setBackgroundAt(i, bg);
			}
		}
		setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		/*
		 * Panels
		 */
		final Configurable tab = (Configurable) ((ConfigurableEditorInput) getEditorInput()).getAdapter(Configurable.class);
		/**
		 * Tabbed Pane
		 */
		tab.setFontSize(exampleTabbedPane.getFont().getSize2D());
		tab.setFontStyle(exampleTabbedPane.getFont().getStyle());
		tab.setFgSelected(exampleTabbedPane.getForegroundAt(exampleTabbedPane.getSelectedIndex()).getRGB());
		final RGB fg = tabFg.getColorValue();
		final RGB bg = tabBg.getColorValue();
		tab.setFg(new java.awt.Color(fg.red, fg.green, fg.blue).getRGB());
		tab.setBg(new java.awt.Color(bg.red, bg.green, bg.blue).getRGB());
	}

	@Override
	protected void updateControls()
	{
		super.updateControls();
	}

	@Override
	protected boolean validate()
	{
		return true;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Configurable> input)
	{
		return input.getAdapter(Configurable.class) instanceof Configurable;
	}

	private Section createDesignSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Erscheinungsbild");
		section.setClient(fillDesignSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ConfigurableEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillDesignSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);
		/*
		 * tab font examples
		 */
		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		final GridLayout gridLayout = new GridLayout(2, true);
		gridLayout.horizontalSpacing = 5;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		final Composite c = formToolkit.createComposite(composite, SWT.NONE);
		c.setLayoutData(layoutData);
		c.setLayout(gridLayout);

		final GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 60;
		gridData.minimumWidth = 200;

		final Composite tab = formToolkit.createComposite(c, SWT.EMBEDDED);
		tab.setLayoutData(gridData);

		final Frame tabbedPaneFrame = SWT_AWT.new_Frame(tab);
		tabbedPaneFrame.setLayout(new java.awt.GridLayout());

		exampleTabbedPane = new JTabbedPane();
		exampleTabbedPane.add("Tab 1", new JPanel());
		exampleTabbedPane.add("Tab 2", new JPanel());
		exampleTabbedPane.add("Tab 3", new JPanel());
		exampleTabbedPane.setSelectedIndex(0);
		exampleTabbedPane.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(final ChangeEvent e)
			{
				final int tabCount = exampleTabbedPane.getTabCount();
				final int selectedIndex = exampleTabbedPane.getSelectedIndex();

				for (int i = 0; i < tabCount; i++)
				{
					if (selectedIndex == i)
					{
						final RGB rgb = tabFgSelected.getColorValue();
						exampleTabbedPane.setForegroundAt(i, new java.awt.Color(rgb.red, rgb.green, rgb.blue));
					}
					else
					{
						final RGB fg = tabFg.getColorValue();
						final RGB bg = tabBg.getColorValue();
						exampleTabbedPane.setForegroundAt(i, new java.awt.Color(fg.red, fg.green, fg.blue));
						exampleTabbedPane.setBackgroundAt(i, new java.awt.Color(bg.red, bg.green, bg.blue));
					}
				}
			}

		});
		tabbedPaneFrame.add(exampleTabbedPane);

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Composite tabbedPaneControlComposite = formToolkit.createComposite(composite, SWT.None);
		tabbedPaneControlComposite.setLayoutData(layoutData);
		tabbedPaneControlComposite.setLayout(new GridLayout(3, false));

		tabFontSizeComposite = new FontSizeComposite(tabbedPaneControlComposite, SWT.NONE);
		tabFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		tabFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				for (int i = 0; i < exampleTabbedPane.getTabCount(); i++)
				{
					if (i == exampleTabbedPane.getSelectedIndex())
					{
						exampleTabbedPane.setFont(exampleTabbedPane.getFont().deriveFont(size));
					}
				}
				ConfigurableEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		formToolkit.adapt(tabFontSizeComposite);

		tabFontStyleComposite = new FontStyleComposite(formToolkit, tabbedPaneControlComposite, SWT.NONE);
		tabFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		tabFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = exampleTabbedPane.getFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				exampleTabbedPane.setFont(exampleTabbedPane.getFont().deriveFont(newStyle));
				ConfigurableEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		tabFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = exampleTabbedPane.getFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				exampleTabbedPane.setFont(exampleTabbedPane.getFont().deriveFont(newStyle));
				ConfigurableEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		formToolkit.adapt(tabFontStyleComposite);

		final Group colorGroup = new Group(tabbedPaneControlComposite, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(2, false));
		formToolkit.adapt(colorGroup);

		Label label = formToolkit.createLabel(colorGroup, "Schrift selektiert", SWT.NONE);
		label.setLayoutData(new GridData());

		tabFgSelected = new ColorSelector(colorGroup);
		tabFgSelected.getButton().setLayoutData(new GridData());
		tabFgSelected.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				exampleTabbedPane.setForegroundAt(exampleTabbedPane.getSelectedIndex(), newColor);
				ConfigurableEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = formToolkit.createLabel(colorGroup, "Schrift nicht selektiert", SWT.NONE);
		label.setLayoutData(new GridData());

		tabFg = new ColorSelector(colorGroup);
		tabFg.getButton().setLayoutData(new GridData());
		tabFg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				for (int i = 0; i < exampleTabbedPane.getTabCount(); i++)
				{
					if (i != exampleTabbedPane.getSelectedIndex())
					{
						exampleTabbedPane.setForegroundAt(i, newColor);
					}
				}

				ConfigurableEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = formToolkit.createLabel(colorGroup, "Hintergrund", SWT.NONE);
		label.setLayoutData(new GridData());

		tabBg = new ColorSelector(colorGroup);
		tabBg.getButton().setLayoutData(new GridData());
		tabBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				for (int i = 0; i < exampleTabbedPane.getTabCount(); i++)
				{
					if (i != exampleTabbedPane.getSelectedIndex())
					{
						exampleTabbedPane.setBackgroundAt(i, newColor);
					}
				}

				ConfigurableEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		formToolkit.paintBordersFor(composite);

		return composite;
	}
}
