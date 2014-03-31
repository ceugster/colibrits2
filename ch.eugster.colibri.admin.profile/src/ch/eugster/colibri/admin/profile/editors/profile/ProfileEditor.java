/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.profile;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelListener;
import javax.swing.plaf.BorderUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.admin.profile.buttons.ProfileEditorFailOverButton;
import ch.eugster.colibri.admin.profile.buttons.ProfileEditorNormalButton;
import ch.eugster.colibri.admin.profile.editors.composites.AlignComposite;
import ch.eugster.colibri.admin.profile.editors.composites.FontSizeComposite;
import ch.eugster.colibri.admin.profile.editors.composites.FontStyleComposite;
import ch.eugster.colibri.admin.profile.panels.DisplayPanel;
import ch.eugster.colibri.admin.profile.panels.ReceivedPanel;
import ch.eugster.colibri.admin.profile.panels.RemainderPanel;
import ch.eugster.colibri.admin.profile.panels.TotalPanel;
import ch.eugster.colibri.admin.ui.dialogs.Message;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditor;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Profile.PanelType;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.queries.ProfileQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProfileEditor extends AbstractEntityEditor<Profile>
{
	public static final String ID = "ch.eugster.colibri.admin.profile.editor";

	private static final String KEY_PANEL = "panel";

	public static final String DISPLAY_PANEL = "display";

	public static final String NUMERIC_PANEL = "numeric";

	public static final String ARTICLE_PANEL = "article";

	public static final String FUNCTION_PANEL = "function";

	private ScrolledForm scrolledForm;

	/*
	 * name section
	 */
	private Text name;

	/*
	 * panel section
	 */
	private Label topLeft;

	private Label topRight;

	private Label bottomLeft;

	private Label bottomRight;

	private Spinner leftPercent;
	
	private Spinner topPercent;
	
	private Color lightGrey = new Color(PlatformUI.getWorkbench().getDisplay(), 230, 230, 230);

	/*
	 * Display
	 */
	private java.awt.Frame totalFrame;

	private DisplayPanel[] displayPanels;

	private FontSizeComposite displayFontSizeComposite;

	private FontStyleComposite displayFontStyleComposite;

	private ColorSelector displayFgSelector;

	private ColorSelector displayBgSelector;

	private Button showReceivedBackAlways;

	/*
	 * TabbedPane
	 */
	private java.awt.Frame tabbedPaneFrame;

	private JTabbedPane exampleTabbedPane;

	private FontSizeComposite tabbedPaneFontSizeComposite;

	private FontStyleComposite tabbedPaneFontStyleComposite;

	private ColorSelector tabbedPaneFgSelected;

	private ColorSelector tabbedPaneFg;

	private ColorSelector tabbedPaneBg;

	/*
	 * Buttons
	 */
	private JButton exampleNormalButton;

	private JButton exampleFailOverButton;

	private FontSizeComposite buttonNormalFontSizeComposite;

	private FontStyleComposite buttonNormalFontStyleComposite;

	private AlignComposite buttonNormalAlignComposite;

	private ColorSelector buttonNormalFg;

	private ColorSelector buttonNormalBg;

	private Button buttonNormalBgEmpty;

	private FontSizeComposite buttonFailOverFontSizeComposite;

	private FontStyleComposite buttonFailOverFontStyleComposite;

	private AlignComposite buttonFailOverAlignComposite;

	private ColorSelector buttonFailOverFg;

	private ColorSelector buttonFailOverBg;

	private Button buttonFailOverBgEmpty;

	/*
	 * Input Label
	 */
	private JLabel exampleInputNameLabel;

	private FontSizeComposite inputNameLabelFontSizeComposite;

	private FontStyleComposite inputNameLabelFontStyleComposite;

	private ColorSelector inputNameLabelFg;

	private ColorSelector inputNameLabelBg;
	/*
	 * Labels
	 */
	private JLabel exampleNameLabel;

	private JLabel exampleValueLabel;

	private JLabel exampleValueLabelSelected;

	private FontSizeComposite nameLabelFontSizeComposite;

	private FontStyleComposite nameLabelFontStyleComposite;

	private FontSizeComposite valueLabelFontSizeComposite;

	private FontStyleComposite valueLabelFontStyleComposite;

	private ColorSelector nameLabelFg;

	private ColorSelector nameLabelBg;

	private ColorSelector valueLabelFg;

	private ColorSelector valueLabelBg;

	private ColorSelector selectedValueLabelBg;

	/*
	 * Lists
	 */
	private JTable exampleTable;

	private FontSizeComposite listFontSizeComposite;

	private FontStyleComposite listFontStyleComposite;

	private ColorSelector listFg;

	private ColorSelector listBg;

	public ProfileEditor()
	{
	}

	@Override
	public void dispose()
	{
		this.lightGrey.dispose();
		EntityMediator.removeListener(Profile.class, this);
		super.dispose();
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
		if (entity instanceof Profile)
		{
			if (entity.equals(this.getEditorInput().getAdapter(Profile.class)))
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
		else if (propId == IEditorPart.PROP_INPUT)
		{
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
		this.createGeneralSection(scrolledForm);
		this.createPanelSection(scrolledForm);
		this.createDisplaySection(scrolledForm);
		this.createTabSection(scrolledForm);
		this.createButtonSection(scrolledForm);
		this.createInputLabelSection(scrolledForm);
		this.createLabelSection(scrolledForm);
		this.createListSection(scrolledForm);
		EntityMediator.addListener(Profile.class, this);
	}

	@Override
	protected String getName()
	{
		final Profile profile = (Profile) ((ProfileEditorInput) this.getEditorInput()).getAdapter(Profile.class);
		if (profile.getId() == null)
		{
			return "Neu";
		}
		return profile.getName();
	}

	// @Override
	// protected Message getMessage(final ErrorCode errorCode)
	// {
	// Message msg = null;
	//
	// if (errorCode.equals(""))
	// {
	// msg = getUniqueNameMessage();
	// }
	// return msg;
	// }

	@Override
	protected String getText()
	{
		return "Profil";
	}

	@Override
	protected void loadValues()
	{
		final Profile profile = (Profile) ((ProfileEditorInput) this.getEditorInput()).getAdapter(Profile.class);
		this.name.setText(profile.getName());

		/*
		 * Panel Anordnung
		 */
		if (profile.getTopLeft() != null)
		{
			final Image image = Activator.getDefault().getImageRegistry().get(profile.getTopLeft() + ".png");
			this.topLeft.setImage(image);
		}
		this.topLeft.setData(ProfileEditor.KEY_PANEL, profile.getTopLeft());

		if (profile.getTopRight() != null)
		{
			final Image image = Activator.getDefault().getImageRegistry().get(profile.getTopRight() + ".png");
			this.topRight.setImage(image);
		}
		this.topRight.setData(ProfileEditor.KEY_PANEL, profile.getTopRight());

		if (profile.getBottomLeft() != null)
		{
			final Image image = Activator.getDefault().getImageRegistry().get(profile.getBottomLeft() + ".png");
			this.bottomLeft.setImage(image);
		}
		this.bottomLeft.setData(ProfileEditor.KEY_PANEL, profile.getBottomLeft());

		if (profile.getBottomRight() != null)
		{
			final Image image = Activator.getDefault().getImageRegistry().get(profile.getBottomRight() + ".png");
			this.bottomLeft.setImage(image);
		}
		this.bottomRight.setData(ProfileEditor.KEY_PANEL, profile.getBottomRight());

		this.leftPercent.setSelection(profile.getLeftPercent());
		this.topPercent.setSelection(profile.getTopPercent());
		/*
		 * Display
		 */
		float size = profile.getDisplayFontSize();
		int style = profile.getDisplayFontStyle();
		java.awt.Color fg = new java.awt.Color(profile.getDisplayFg());
		java.awt.Color bg = new java.awt.Color(profile.getDisplayBg());
		this.displayFontSizeComposite.setSize(profile.getDisplayFontSize());
		this.displayFontStyleComposite.setStyle(profile.getDisplayFontStyle());
		this.displayFgSelector.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		this.displayBgSelector.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));
		for (final DisplayPanel displayPanel : this.displayPanels)
		{
			displayPanel.setDisplayFont(displayPanel.getDisplayFont().deriveFont(style, size));
			displayPanel.setDisplayForeground(fg);
			displayPanel.setDisplayBackground(bg);
		}
		/*
		 * TabbedPane
		 */
		size = profile.getTabbedPaneFontSize();
		style = profile.getTabbedPaneFontStyle();
		final java.awt.Color fgSelected = new java.awt.Color(profile.getTabbedPaneFgSelected());
		fg = new java.awt.Color(profile.getTabbedPaneFg());
		bg = new java.awt.Color(profile.getTabbedPaneBg());
		this.tabbedPaneFontSizeComposite.setSize(size);
		this.tabbedPaneFontStyleComposite.setStyle(style);
		this.tabbedPaneFgSelected.setColorValue(new RGB(fgSelected.getRed(), fgSelected.getGreen(), fgSelected.getBlue()));
		this.tabbedPaneFg.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		this.tabbedPaneBg.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));
		this.exampleTabbedPane.setFont(this.exampleTabbedPane.getFont().deriveFont(style, size));
		for (int i = 0; i < this.exampleTabbedPane.getTabCount(); i++)
		{
			if (i == this.exampleTabbedPane.getSelectedIndex())
			{
				this.exampleTabbedPane.setForegroundAt(i, fgSelected);
			}
			else
			{
				this.exampleTabbedPane.setForegroundAt(i, fg);
				this.exampleTabbedPane.setBackgroundAt(i, bg);
			}
		}

		/*
		 * Buttons
		 */
		size = profile.getButtonNormalFontSize();
		style = profile.getButtonNormalFontStyle();
		int horizontalAlignment = profile.getButtonNormalHorizontalAlign();
		int verticalAlignment = profile.getButtonNormalVerticalAlign();
		this.buttonNormalFontSizeComposite.setSize(size);
		this.buttonNormalFontStyleComposite.setStyle(style);
		this.buttonNormalAlignComposite.setHorizontalSelection(horizontalAlignment);
		this.buttonNormalAlignComposite.setVerticalSelection(verticalAlignment);
		fg = new java.awt.Color(profile.getButtonNormalFg());
		bg = new java.awt.Color(profile.getButtonNormalBg());
		this.buttonNormalFg.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		this.buttonNormalBg.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));

		this.exampleNormalButton.setFont(this.exampleNormalButton.getFont().deriveFont(style, size));
		this.exampleNormalButton.setHorizontalAlignment(horizontalAlignment);
		this.exampleNormalButton.setVerticalAlignment(verticalAlignment);
		this.exampleNormalButton.setForeground(fg);
		this.exampleNormalButton.setBackground(bg.getRGB() == UIManager.getColor("Button.background").getRGB() ? UIManager
				.getColor("Button.background") : bg);

		size = profile.getButtonFailOverFontSize();
		style = profile.getButtonFailOverFontStyle();
		horizontalAlignment = profile.getButtonFailOverHorizontalAlign();
		verticalAlignment = profile.getButtonFailOverVerticalAlign();
		fg = new java.awt.Color(profile.getButtonFailOverFg());
		bg = new java.awt.Color(profile.getButtonFailOverBg());
		this.buttonFailOverFontSizeComposite.setSize(size);
		this.buttonFailOverFontStyleComposite.setStyle(style);
		this.buttonFailOverAlignComposite.setHorizontalSelection(horizontalAlignment);
		this.buttonFailOverAlignComposite.setVerticalSelection(verticalAlignment);
		this.buttonFailOverFg.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		this.buttonFailOverBg.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));

		this.exampleFailOverButton.setFont(this.exampleFailOverButton.getFont().deriveFont(style, size));
		this.exampleFailOverButton.setHorizontalAlignment(horizontalAlignment);
		this.exampleFailOverButton.setVerticalAlignment(verticalAlignment);
		this.exampleFailOverButton.setForeground(fg);
		this.exampleFailOverButton.setBackground(bg.getRGB() == UIManager.getColor("Button.background").getRGB() ? UIManager
				.getColor("Button.background") : bg);

		size = profile.getInputNameLabelFontSize();
		style = profile.getInputNameLabelFontStyle();
		fg = new java.awt.Color(profile.getNameLabelFg());
		bg = new java.awt.Color(profile.getNameLabelBg());
		this.inputNameLabelFontSizeComposite.setSize(size);
		this.inputNameLabelFontStyleComposite.setStyle(style);
		this.inputNameLabelFg.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		this.inputNameLabelBg.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));
		this.exampleInputNameLabel.setFont(this.exampleNameLabel.getFont().deriveFont(style, size));
		this.exampleInputNameLabel.setForeground(fg);
		this.exampleInputNameLabel.setBackground(bg);

		size = profile.getNameLabelFontSize();
		style = profile.getNameLabelFontStyle();
		fg = new java.awt.Color(profile.getNameLabelFg());
		bg = new java.awt.Color(profile.getNameLabelBg());
		this.nameLabelFontSizeComposite.setSize(size);
		this.nameLabelFontStyleComposite.setStyle(style);
		this.nameLabelFg.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		this.nameLabelBg.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));
		this.exampleNameLabel.setFont(this.exampleNameLabel.getFont().deriveFont(style, size));
		this.exampleNameLabel.setForeground(fg);
		this.exampleNameLabel.setBackground(bg);

		size = profile.getValueLabelFontSize();
		style = profile.getValueLabelFontStyle();
		fg = new java.awt.Color(profile.getValueLabelFg());
		bg = new java.awt.Color(profile.getValueLabelBg());
		final java.awt.Color bgSelected = new java.awt.Color(profile.getValueLabelBgSelected());
		this.valueLabelFontSizeComposite.setSize(size);
		this.valueLabelFontStyleComposite.setStyle(style);
		this.valueLabelFg.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		this.valueLabelBg.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));
		this.selectedValueLabelBg.setColorValue(new RGB(bgSelected.getRed(), bgSelected.getGreen(), bgSelected.getBlue()));
		this.exampleValueLabel.setFont(this.exampleValueLabel.getFont().deriveFont(style, size));
		this.exampleValueLabel.setForeground(fg);
		this.exampleValueLabel.setBackground(bg);
		this.exampleValueLabelSelected.setFont(this.exampleValueLabelSelected.getFont().deriveFont(style, size));
		this.exampleValueLabelSelected.setForeground(fg);
		this.exampleValueLabelSelected.setBackground(bg);
		this.exampleValueLabelSelected.setBackground(bgSelected);

		size = profile.getListFontSize();
		style = profile.getListFontStyle();
		fg = new java.awt.Color(profile.getListFg());
		bg = new java.awt.Color(profile.getListBg());
		this.listFontSizeComposite.setSize(size);
		this.listFontStyleComposite.setStyle(style);
		this.listFg.setColorValue(new RGB(fg.getRed(), fg.getGreen(), fg.getBlue()));
		this.listBg.setColorValue(new RGB(bg.getRed(), bg.getGreen(), bg.getBlue()));
		this.exampleTable.setFont(this.exampleTable.getFont().deriveFont(style, size));
		this.exampleTable.setForeground(fg);
		this.exampleTable.setBackground(bg);

		this.setDirty(false);
	}

	@Override
	protected void saveValues()
	{
		/*
		 * Panels
		 */
		final Profile profile = (Profile) ((ProfileEditorInput) this.getEditorInput()).getAdapter(Profile.class);
		profile.setName(this.name.getText());
		profile.setTopLeft((PanelType) this.topLeft.getData(ProfileEditor.KEY_PANEL));
		profile.setTopRight((PanelType) this.topRight.getData(ProfileEditor.KEY_PANEL));
		profile.setBottomLeft((PanelType) this.bottomLeft.getData(ProfileEditor.KEY_PANEL));
		profile.setBottomRight((PanelType) this.bottomRight.getData(ProfileEditor.KEY_PANEL));
		
		profile.setLeftPercent(leftPercent.getSelection());
		profile.setTopPercent(topPercent.getSelection());
		/*
		 * Display panel
		 */
		profile.setDisplayFontSize(this.displayPanels[0].getDisplayFont().getSize2D());
		profile.setDisplayFontStyle(this.displayPanels[0].getDisplayFont().getStyle());
		profile.setDisplayFg(this.displayPanels[0].getDisplayForeground().getRGB());
		profile.setDisplayBg(this.displayPanels[0].getDisplayBackground().getRGB());
		/**
		 * Tabbed Pane
		 */
		profile.setTabbedPaneFontSize(this.exampleTabbedPane.getFont().getSize2D());
		profile.setTabbedPaneFontStyle(this.exampleTabbedPane.getFont().getStyle());
		profile.setTabbedPaneFgSelected(this.exampleTabbedPane.getForegroundAt(this.exampleTabbedPane.getSelectedIndex()).getRGB());
		final RGB fg = this.tabbedPaneFg.getColorValue();
		final RGB bg = this.tabbedPaneBg.getColorValue();
		profile.setTabbedPaneFg(new java.awt.Color(fg.red, fg.green, fg.blue).getRGB());
		profile.setTabbedPaneBg(new java.awt.Color(bg.red, bg.green, bg.blue).getRGB());
		/*
		 * Buttons
		 */
		final Collection<Configurable> configurables = profile.getConfigurables();
		for (final Configurable configurable : configurables)
		{
			final Collection<Tab> tabs = configurable.getTabs();
			for (final Tab tab : tabs)
			{
				final Collection<Key> keys = tab.getKeys();
				for (final Key key : keys)
				{
					if (profile.getButtonNormalFontSize() != this.exampleNormalButton.getFont().getSize())
					{
						if (profile.getButtonNormalFontSize() == key.getNormalFontSize())
						{
							key.setNormalFontSize(this.exampleNormalButton.getFont().getSize2D());
						}
					}
					if (profile.getButtonNormalFontStyle() != this.exampleNormalButton.getFont().getStyle())
					{
						if (profile.getButtonNormalFontStyle() == key.getNormalFontStyle())
						{
							key.setNormalFontStyle(this.exampleNormalButton.getFont().getStyle());
						}
					}
					if (profile.getButtonNormalHorizontalAlign() != this.exampleNormalButton.getHorizontalAlignment())
					{
						if (profile.getButtonNormalHorizontalAlign() == key.getNormalHorizontalAlign())
						{
							key.setNormalHorizontalAlign(this.exampleNormalButton.getHorizontalAlignment());
						}
					}
					if (profile.getButtonNormalVerticalAlign() != this.exampleNormalButton.getVerticalAlignment())
					{
						if (profile.getButtonNormalVerticalAlign() == key.getNormalVerticalAlign())
						{
							key.setNormalVerticalAlign(this.exampleNormalButton.getVerticalAlignment());
						}
					}
					if (profile.getButtonNormalFg() != this.exampleNormalButton.getForeground().getRGB())
					{
						if (profile.getButtonNormalFg() == key.getNormalFg())
						{
							key.setNormalFg(this.exampleNormalButton.getForeground().getRGB());
						}
					}
					if (profile.getButtonNormalBg() != this.exampleNormalButton.getBackground().getRGB())
					{
						if (profile.getButtonNormalBg() == key.getNormalBg())
						{
							key.setNormalBg(this.exampleNormalButton.getBackground().getRGB());
						}
					}

					if (profile.getButtonFailOverFontSize() != this.exampleFailOverButton.getFont().getSize())
					{
						if (profile.getButtonFailOverFontSize() == key.getFailOverFontSize())
						{
							key.setFailOverFontSize(this.exampleFailOverButton.getFont().getSize2D());
						}
					}
					if (profile.getButtonFailOverFontStyle() != this.exampleFailOverButton.getFont().getStyle())
					{
						if (profile.getButtonFailOverFontStyle() == key.getFailOverFontStyle())
						{
							key.setFailOverFontStyle(this.exampleFailOverButton.getFont().getStyle());
						}
					}
					if (profile.getButtonFailOverHorizontalAlign() != this.exampleFailOverButton.getHorizontalAlignment())
					{
						if (profile.getButtonFailOverHorizontalAlign() == key.getFailOverHorizontalAlign())
						{
							key.setFailOverHorizontalAlign(this.exampleFailOverButton.getHorizontalAlignment());
						}
					}
					if (profile.getButtonFailOverVerticalAlign() != this.exampleFailOverButton.getVerticalAlignment())
					{
						if (profile.getButtonFailOverVerticalAlign() == key.getFailOverVerticalAlign())
						{
							key.setFailOverVerticalAlign(this.exampleFailOverButton.getVerticalAlignment());
						}
					}
					if (profile.getButtonFailOverFg() != this.exampleFailOverButton.getForeground().getRGB())
					{
						if (profile.getButtonFailOverFg() == key.getFailOverFg())
						{
							key.setFailOverFg(this.exampleFailOverButton.getForeground().getRGB());
						}
					}
					if (profile.getButtonFailOverBg() != this.exampleFailOverButton.getBackground().getRGB())
					{
						if (profile.getButtonFailOverBg() == key.getFailOverBg())
						{
							key.setFailOverBg(this.exampleFailOverButton.getBackground().getRGB());
						}
					}
				}
			}
		}

		profile.setButtonNormalFontSize(this.exampleNormalButton.getFont().getSize2D());
		profile.setButtonNormalFontStyle(this.exampleNormalButton.getFont().getStyle());
		profile.setButtonNormalHorizontalAlign(this.exampleNormalButton.getHorizontalAlignment());
		profile.setButtonNormalVerticalAlign(this.exampleNormalButton.getVerticalAlignment());
		profile.setButtonNormalFg(this.exampleNormalButton.getForeground().getRGB());
		profile.setButtonNormalBg(this.exampleNormalButton.getBackground().getRGB());

		profile.setButtonFailOverFontSize(this.exampleFailOverButton.getFont().getSize2D());
		profile.setButtonFailOverFontStyle(this.exampleFailOverButton.getFont().getStyle());
		profile.setButtonFailOverHorizontalAlign(this.exampleFailOverButton.getHorizontalAlignment());
		profile.setButtonFailOverVerticalAlign(this.exampleFailOverButton.getVerticalAlignment());
		profile.setButtonFailOverFg(this.exampleFailOverButton.getForeground().getRGB());
		profile.setButtonFailOverBg(this.exampleFailOverButton.getBackground().getRGB());
		/*
		 * Input Label
		 */
		profile.setInputNameLabelFontSize(this.exampleInputNameLabel.getFont().getSize2D());
		profile.setInputNameLabelFontStyle(this.exampleInputNameLabel.getFont().getStyle());
		profile.setInputNameLabelFg(this.exampleInputNameLabel.getForeground().getRGB());
		profile.setInputNameLabelBg(this.exampleInputNameLabel.getBackground().getRGB());
		/*
		 * Name Labels
		 */
		profile.setNameLabelFontSize(this.exampleNameLabel.getFont().getSize2D());
		profile.setNameLabelFontStyle(this.exampleNameLabel.getFont().getStyle());
		profile.setNameLabelFg(this.exampleNameLabel.getForeground().getRGB());
		profile.setNameLabelBg(this.exampleNameLabel.getBackground().getRGB());
		/*
		 * ValueLabels
		 */
		profile.setValueLabelFontSize(this.exampleValueLabel.getFont().getSize2D());
		profile.setValueLabelFontStyle(this.exampleValueLabel.getFont().getStyle());
		profile.setValueLabelFg(this.exampleValueLabel.getForeground().getRGB());
		profile.setValueLabelBg(this.exampleValueLabel.getBackground().getRGB());
		profile.setValueLabelBgSelected(this.exampleValueLabelSelected.getBackground().getRGB());
		/*
		 * List
		 */
		profile.setListFontSize(this.exampleTable.getFont().getSize2D());
		profile.setListFontStyle(this.exampleTable.getFont().getStyle());
		profile.setListFg(this.exampleTable.getForeground().getRGB());
		profile.setListBg(this.exampleTable.getBackground().getRGB());
	}

	@Override
	protected void updateControls()
	{
		super.updateControls();
	}

	@Override
	protected boolean validate()
	{
		Message msg = this.getEmptyNameMessage();

		if (msg == null)
		{
			msg = this.getUniqueNameMessage();
		}

		if (msg != null)
		{
			this.showWarningMessage(msg);
		}

		return msg == null;
	}

	@Override
	protected boolean validateType(final AbstractEntityEditorInput<Profile> input)
	{
		return input.getAdapter(Profile.class) instanceof Profile;
	}

	private Section createButtonSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Erscheinungsbild Tasten");
		section.setClient(this.fillButtonSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProfileEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createDisplaySection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Betragsanzeige");
		section.setClient(this.fillDisplaySection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProfileEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private JTable createExampleTable()
	{

		final JTable table = new JTable(new TableModel()
		{
			public void addTableModelListener(final TableModelListener listener)
			{

			}

			public Class<?> getColumnClass(final int col)
			{
				return String.class;
			}

			public int getColumnCount()
			{
				return 5;
			}

			public String getColumnName(final int col)
			{
				switch (col)
				{
					case 0:
						return "Warengruppe - Titel";
					case 1:
						return "Menge";
					case 2:
						return "Preis";
					case 3:
						return "Rabatt";
					case 4:
						return "Betrag";
				}
				return "";
			}

			public int getRowCount()
			{
				return 2;
			}

			public Object getValueAt(final int row, final int col)
			{
				switch (row)
				{
					case 0:
						switch (col)
						{
							case 0:
								return "Belletristik - Orwell, Georg: Animal Farm";
							case 1:
								return "1";
							case 2:
								return "24.80";
							case 3:
								return "10%";
							case 4:
								return "22.30";
						}
					case 1:
						switch (col)
						{
							case 0:
								return "Fachbuch - Stangl, Martin: Obstbaumschnitt";
							case 1:
								return "2";
							case 2:
								return "17.90";
							case 3:
								return "0%";
							case 4:
								return "35.80";
						}
				}
				return "";
			}

			public boolean isCellEditable(final int row, final int col)
			{
				return false;
			}

			public void removeTableModelListener(final TableModelListener listener)
			{

			}

			public void setValueAt(final Object element, final int row, final int col)
			{

			}
		}, this.createTableColumnModel());

		this.resizeColumns(table);
		return table;
	}

	private Section createGeneralSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Allgemein");
		section.setClient(this.fillGeneralSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProfileEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createLabelSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Erscheinungsbild Beschriftung - Eingaben");
		section.setClient(this.fillLabelSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProfileEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createInputLabelSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Erscheinungsbild Beschriftung Eingabebereich");
		section.setClient(this.fillInputLabelSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProfileEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createListSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Erscheinungsbild Listen");
		section.setClient(this.fillListSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProfileEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Section createPanelSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Panelanordnung");
		section.setClient(this.fillPanelSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProfileEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private TableColumn createTableColumn(final String title, final int index)
	{
		TableColumn tableColumn = new TableColumn();
		tableColumn = new TableColumn();
		tableColumn.setHeaderValue(title);
		tableColumn.setModelIndex(index);
		tableColumn.setCellRenderer(new DefaultTableCellRenderer());
		tableColumn.setResizable(true);
		return tableColumn;
	}

	private TableColumnModel createTableColumnModel()
	{
		final DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		columnModel.addColumn(this.createTableColumn("Warengruppe - Titel", 0));
		columnModel.addColumn(this.createTableColumn("Menge", 1));
		columnModel.addColumn(this.createTableColumn("Preis", 2));
		columnModel.addColumn(this.createTableColumn("Rabatt", 3));
		columnModel.addColumn(this.createTableColumn("Betrag", 4));
		return columnModel;
	}

	private Section createTabSection(final ScrolledForm scrolledForm)
	{
		final ColumnLayoutData layoutData = new ColumnLayoutData();
		layoutData.widthHint = 200;
		final TableWrapLayout sectionLayout = new TableWrapLayout();
		sectionLayout.numColumns = 1;

		final Section section = this.formToolkit.createSection(scrolledForm.getBody(), ExpandableComposite.EXPANDED | ExpandableComposite.COMPACT
				| ExpandableComposite.TITLE_BAR | ExpandableComposite.TWISTIE);
		section.setLayoutData(layoutData);
		section.setLayout(sectionLayout);
		section.setText("Erscheinungsbild Tabs");
		section.setClient(this.fillTabSection(section));
		section.addExpansionListener(new ExpansionAdapter()
		{
			@Override
			public void expansionStateChanged(final ExpansionEvent e)
			{
				ProfileEditor.this.scrolledForm.reflow(true);
			}
		});

		return section;
	}

	private Control fillButtonSection(final Section parent)
	{
		final Profile profile = (Profile) ((ProfileEditorInput) this.getEditorInput()).getAdapter(Profile.class);

		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = false;

		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		final Composite c = this.formToolkit.createComposite(composite, SWT.NONE);
		c.setLayoutData(layoutData);
		c.setLayout(gridLayout);

		final GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 60;
		gridData.heightHint = 60;
		gridData.minimumWidth = 80;
		gridData.widthHint = 160;

		final Composite key = this.formToolkit.createComposite(c, SWT.EMBEDDED);
		key.setLayoutData(gridData);

		final Frame frame = SWT_AWT.new_Frame(key);
		frame.setLayout(new java.awt.GridLayout(1, 2));

		this.exampleNormalButton = new ProfileEditorNormalButton("Normal", profile, false);
		this.exampleNormalButton.setPreferredSize(new Dimension(80, 60));
		this.exampleNormalButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				ProfileEditor.this.exampleFailOverButton.setEnabled(!ProfileEditor.this.exampleFailOverButton.isEnabled());
			}
		});
		frame.add(this.exampleNormalButton);

		this.exampleFailOverButton = new ProfileEditorFailOverButton("Failover", profile, true);
		this.exampleFailOverButton.setPreferredSize(new Dimension(80, 60));
		this.exampleFailOverButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(final ActionEvent e)
			{
				ProfileEditor.this.exampleNormalButton.setEnabled(!ProfileEditor.this.exampleNormalButton.isEnabled());
			}
		});
		frame.add(this.exampleFailOverButton);
		frame.pack();

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Group buttonNormalControlGroup = new Group(composite, SWT.None);
		buttonNormalControlGroup.setLayoutData(layoutData);
		buttonNormalControlGroup.setLayout(new GridLayout(4, false));
		buttonNormalControlGroup.setText("Normalmodus");
		this.formToolkit.adapt(buttonNormalControlGroup);

		this.buttonNormalFontSizeComposite = new FontSizeComposite(buttonNormalControlGroup, SWT.NONE, 8, 48, 1, 8);
		this.buttonNormalFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.buttonNormalFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				ProfileEditor.this.exampleNormalButton.setFont(ProfileEditor.this.exampleNormalButton.getFont().deriveFont(size));
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.buttonNormalFontSizeComposite);

		this.buttonNormalFontStyleComposite = new FontStyleComposite(this.formToolkit, buttonNormalControlGroup, SWT.NONE);
		this.buttonNormalFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.buttonNormalFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleNormalButton.getFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleNormalButton.setFont(ProfileEditor.this.exampleNormalButton.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.buttonNormalFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleNormalButton.getFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleNormalButton.setFont(ProfileEditor.this.exampleNormalButton.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.buttonNormalFontStyleComposite);

		this.buttonNormalAlignComposite = new AlignComposite(this.formToolkit, buttonNormalControlGroup, SWT.NONE);
		this.buttonNormalAlignComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.buttonNormalAlignComposite.addHorizontalListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Widget widget = e.widget;
				final Button button = (Button) widget;
				final int alignment = ((Integer) button.getData()).intValue();
				ProfileEditor.this.exampleNormalButton.setHorizontalAlignment(alignment);
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.buttonNormalAlignComposite.addVerticalListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Widget widget = e.widget;
				final Button button = (Button) widget;
				final int alignment = ((Integer) button.getData()).intValue();
				ProfileEditor.this.exampleNormalButton.setVerticalAlignment(alignment);
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.buttonNormalAlignComposite);

		Group colorGroup = new Group(buttonNormalControlGroup, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(3, false));
		this.formToolkit.adapt(colorGroup);

		Label label = this.formToolkit.createLabel(colorGroup, "Schrift", SWT.NONE);
		label.setLayoutData(new GridData());

		this.buttonNormalFg = new ColorSelector(colorGroup);
		this.buttonNormalFg.getButton().setLayoutData(new GridData());
		this.buttonNormalFg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleNormalButton.setForeground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "", SWT.NONE);
		label.setLayoutData(new GridData());

		label = this.formToolkit.createLabel(colorGroup, "Hintergrund", SWT.NONE);
		label.setLayoutData(new GridData());

		this.buttonNormalBg = new ColorSelector(colorGroup);
		this.buttonNormalBg.getButton().setLayoutData(new GridData());
		this.buttonNormalBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleNormalButton.setBackground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		this.buttonNormalBgEmpty = new Button(colorGroup, SWT.PUSH);
		this.buttonNormalBgEmpty.setLayoutData(new GridData());
		this.buttonNormalBgEmpty.setText("default");
		this.buttonNormalBgEmpty.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				final java.awt.Color color = UIManager.getColor("Button.background");
				ProfileEditor.this.buttonNormalBg.setColorValue(new RGB(color.getRed(), color.getGreen(), color.getBlue()));
				ProfileEditor.this.exampleNormalButton.setBackground(color);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}

		});

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Group buttonFailOverControlGroup = new Group(composite, SWT.None);
		buttonFailOverControlGroup.setLayoutData(layoutData);
		buttonFailOverControlGroup.setLayout(new GridLayout(4, false));
		buttonFailOverControlGroup.setText("Failover Modus");
		this.formToolkit.adapt(buttonFailOverControlGroup);

		this.buttonFailOverFontSizeComposite = new FontSizeComposite(buttonFailOverControlGroup, SWT.NONE, 8, 48, 1, 8);
		this.buttonFailOverFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.buttonFailOverFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				ProfileEditor.this.exampleFailOverButton.setFont(ProfileEditor.this.exampleFailOverButton.getFont().deriveFont(size));
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.buttonFailOverFontSizeComposite);

		this.buttonFailOverFontStyleComposite = new FontStyleComposite(this.formToolkit, buttonFailOverControlGroup, SWT.NONE);
		this.buttonFailOverFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.buttonFailOverFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleFailOverButton.getFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleFailOverButton.setFont(ProfileEditor.this.exampleFailOverButton.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.buttonFailOverFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleFailOverButton.getFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleFailOverButton.setFont(ProfileEditor.this.exampleFailOverButton.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.buttonFailOverFontStyleComposite);

		this.buttonFailOverAlignComposite = new AlignComposite(this.formToolkit, buttonFailOverControlGroup, SWT.NONE);
		this.buttonFailOverAlignComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.buttonFailOverAlignComposite.addHorizontalListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Widget widget = e.widget;
				final Button button = (Button) widget;
				final int alignment = ((Integer) button.getData()).intValue();
				ProfileEditor.this.exampleFailOverButton.setHorizontalAlignment(alignment);
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.buttonFailOverAlignComposite.addVerticalListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Widget widget = e.widget;
				final Button button = (Button) widget;
				final int alignment = ((Integer) button.getData()).intValue();
				ProfileEditor.this.exampleFailOverButton.setVerticalAlignment(alignment);
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.buttonFailOverAlignComposite);

		colorGroup = new Group(buttonFailOverControlGroup, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(3, false));
		this.formToolkit.adapt(colorGroup);

		label = this.formToolkit.createLabel(colorGroup, "Schrift", SWT.NONE);
		label.setLayoutData(new GridData());

		this.buttonFailOverFg = new ColorSelector(colorGroup);
		this.buttonFailOverFg.getButton().setLayoutData(new GridData());
		this.buttonFailOverFg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleFailOverButton.setForeground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "", SWT.NONE);
		label.setLayoutData(new GridData());

		label = this.formToolkit.createLabel(colorGroup, "Hintergrund", SWT.NONE);
		label.setLayoutData(new GridData());

		this.buttonFailOverBg = new ColorSelector(colorGroup);
		this.buttonFailOverBg.getButton().setLayoutData(new GridData());
		this.buttonFailOverBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleFailOverButton.setBackground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		this.buttonFailOverBgEmpty = new Button(colorGroup, SWT.PUSH);
		this.buttonFailOverBgEmpty.setLayoutData(new GridData());
		this.buttonFailOverBgEmpty.setText("default");
		this.buttonFailOverBgEmpty.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			@Override
			public void widgetSelected(final SelectionEvent e)
			{
				final java.awt.Color color = UIManager.getColor("Button.background");
				ProfileEditor.this.buttonFailOverBg.setColorValue(new RGB(color.getRed(), color.getGreen(), color.getBlue()));
				ProfileEditor.this.exampleFailOverButton.setBackground(color);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}

		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillDisplaySection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;
		layoutData.grabVertical = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);
		/*
		 * Display
		 */
		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.heightHint = 144;
		layoutData.grabHorizontal = true;
		layoutData.grabVertical = true;

		final Profile profile = (Profile) ((ProfileEditorInput) this.getEditorInput()).getAdapter(Profile.class);

		this.displayPanels = new DisplayPanel[3];
		this.displayPanels[0] = new TotalPanel(profile);
		this.displayPanels[0].setTestData();
		this.displayPanels[1] = new ReceivedPanel(profile);
		this.displayPanels[1].setTestData();
		this.displayPanels[2] = new RemainderPanel(profile);
		this.displayPanels[2].setTestData();

		final Composite totalComposite = this.formToolkit.createComposite(composite, SWT.EMBEDDED);
		totalComposite.setLayoutData(layoutData);
		totalComposite.setBackground(this.lightGrey);

		this.totalFrame = SWT_AWT.new_Frame(totalComposite);
		this.totalFrame.setLayout(new java.awt.GridLayout(1, 1));

		final JPanel displayContainer = new JPanel();
		displayContainer.setLayout(new BorderLayout());
		displayContainer.add(this.displayPanels[0], BorderLayout.NORTH);
		displayContainer.add(this.displayPanels[1], BorderLayout.CENTER);
		displayContainer.add(this.displayPanels[2], BorderLayout.SOUTH);

		final JPanel horizontalPanel = new JPanel();
		horizontalPanel.setLayout(new BorderLayout());
		horizontalPanel.add(displayContainer, BorderLayout.WEST);

		final JPanel verticalPanel = new JPanel();
		verticalPanel.setLayout(new BorderLayout());
		verticalPanel.add(horizontalPanel, BorderLayout.NORTH);

		this.totalFrame.add(verticalPanel);

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Composite totalControlComposite = this.formToolkit.createComposite(composite, SWT.NONE);
		totalControlComposite.setLayoutData(layoutData);
		totalControlComposite.setLayout(new GridLayout(3, false));

		this.displayFontSizeComposite = new FontSizeComposite(totalControlComposite, SWT.NONE);
		this.displayFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.displayFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				for (final DisplayPanel displayPanel : ProfileEditor.this.displayPanels)
				{
					displayPanel.setDisplayFont(displayPanel.getDisplayFont().deriveFont(size));
				}
				ProfileEditor.this.totalFrame.validate();
				ProfileEditor.this.totalFrame.pack();
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.displayFontSizeComposite);

		this.displayFontStyleComposite = new FontStyleComposite(this.formToolkit, totalControlComposite, SWT.NONE);
		this.displayFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.displayFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.displayPanels[0].getDisplayFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				for (final DisplayPanel displayPanel : ProfileEditor.this.displayPanels)
				{
					displayPanel.setDisplayFont(displayPanel.getDisplayFont().deriveFont(newStyle));
				}
				ProfileEditor.this.totalFrame.validate();
				ProfileEditor.this.totalFrame.pack();
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.displayFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.displayPanels[0].getDisplayFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				for (final DisplayPanel displayPanel : ProfileEditor.this.displayPanels)
				{
					displayPanel.setDisplayFont(displayPanel.getDisplayFont().deriveFont(newStyle));
				}
				ProfileEditor.this.totalFrame.validate();
				ProfileEditor.this.totalFrame.pack();
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.displayFontStyleComposite);

		final Group colorGroup = new Group(totalControlComposite, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(2, false));
		this.formToolkit.adapt(colorGroup);

		Label label = new Label(colorGroup, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Schrift");

		this.displayFgSelector = new ColorSelector(colorGroup);
		this.displayFgSelector.getButton().setLayoutData(new GridData());
		this.displayFgSelector.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				for (final DisplayPanel displayPanel : ProfileEditor.this.displayPanels)
				{
					displayPanel.setDisplayForeground(newColor);
				}
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = new Label(colorGroup, SWT.NONE);
		label.setLayoutData(new GridData());
		label.setText("Hintergrund");

		this.displayBgSelector = new ColorSelector(colorGroup);
		this.displayBgSelector.getButton().setLayoutData(new GridData());
		this.displayBgSelector.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				for (final DisplayPanel displayPanel : ProfileEditor.this.displayPanels)
				{
					displayPanel.setDisplayBackground(newColor);
				}
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = false;

		this.showReceivedBackAlways = this.formToolkit.createButton(composite, "'Gegeben' und 'Rückgeld' immer sichtbar", SWT.CHECK);
		this.showReceivedBackAlways.setLayoutData(layoutData);
		this.showReceivedBackAlways.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				ProfileEditor.this.setDirty(true);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillGeneralSection(final Section parent)
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

		final Label label = this.formToolkit.createLabel(composite, "Bezeichnung", SWT.NONE);
		label.setLayoutData(layoutData);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = true;

		this.name = this.formToolkit.createText(composite, "");
		this.name.setLayoutData(layoutData);
		this.name.addModifyListener(new ModifyListener()
		{
			public void modifyText(final ModifyEvent e)
			{
				ProfileEditor.this.propertyChanged(ProfileEditor.this.name, IEditorPart.PROP_DIRTY);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillLabelSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = false;

		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		final Composite c = this.formToolkit.createComposite(composite, SWT.NONE);
		c.setLayoutData(layoutData);
		c.setLayout(gridLayout);

		final GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 60;
		gridData.minimumWidth = 180;

		final Composite key = this.formToolkit.createComposite(c, SWT.EMBEDDED);
		key.setLayoutData(gridData);

		final Frame frame = SWT_AWT.new_Frame(key);
		frame.setLayout(new java.awt.GridLayout(1, 3));

		this.exampleNameLabel = new JLabel("Beschriftung");
		this.exampleNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.exampleNameLabel.setOpaque(true);
		this.exampleNameLabel.setBorder(BorderUIResource.getEtchedBorderUIResource());
		frame.add(this.exampleNameLabel);

		this.exampleValueLabel = new JLabel("Eingabefeld");
		this.exampleValueLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.exampleValueLabel.setOpaque(true);
		this.exampleValueLabel.setBorder(BorderUIResource.getEtchedBorderUIResource());
		frame.add(this.exampleValueLabel);

		this.exampleValueLabelSelected = new JLabel("Eingabefeld (aktuell)");
		this.exampleValueLabelSelected.setHorizontalAlignment(SwingConstants.CENTER);
		this.exampleValueLabelSelected.setOpaque(true);
		this.exampleValueLabelSelected.setBorder(BorderUIResource.getEtchedBorderUIResource());
		frame.add(this.exampleValueLabelSelected);

		frame.pack();

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Group nameLabelControlGroup = new Group(composite, SWT.None);
		nameLabelControlGroup.setLayoutData(layoutData);
		nameLabelControlGroup.setLayout(new GridLayout(3, false));
		nameLabelControlGroup.setText("Beschriftungen");
		this.formToolkit.adapt(nameLabelControlGroup);

		this.nameLabelFontSizeComposite = new FontSizeComposite(nameLabelControlGroup, SWT.NONE, 8, 48, 1, 8);
		this.nameLabelFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.nameLabelFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				ProfileEditor.this.exampleNameLabel.setFont(ProfileEditor.this.exampleNameLabel.getFont().deriveFont(size));
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.nameLabelFontSizeComposite);

		this.nameLabelFontStyleComposite = new FontStyleComposite(this.formToolkit, nameLabelControlGroup, SWT.NONE);
		this.nameLabelFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.nameLabelFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleNameLabel.getFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleNameLabel.setFont(ProfileEditor.this.exampleNameLabel.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.nameLabelFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleNameLabel.getFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleNameLabel.setFont(ProfileEditor.this.exampleNameLabel.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.nameLabelFontStyleComposite);

		Group colorGroup = new Group(nameLabelControlGroup, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(2, false));
		this.formToolkit.adapt(colorGroup);

		Label label = this.formToolkit.createLabel(colorGroup, "Schrift", SWT.NONE);
		label.setLayoutData(new GridData());

		this.nameLabelFg = new ColorSelector(colorGroup);
		this.nameLabelFg.getButton().setLayoutData(new GridData());
		this.nameLabelFg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleNameLabel.setForeground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "Hintergrund", SWT.NONE);
		label.setLayoutData(new GridData());

		this.nameLabelBg = new ColorSelector(colorGroup);
		this.nameLabelBg.getButton().setLayoutData(new GridData());
		this.nameLabelBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleNameLabel.setBackground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Group valueLabelControlGroup = new Group(composite, SWT.None);
		valueLabelControlGroup.setLayoutData(layoutData);
		valueLabelControlGroup.setLayout(new GridLayout(3, false));
		valueLabelControlGroup.setText("Eingabefelder");
		this.formToolkit.adapt(valueLabelControlGroup);

		this.valueLabelFontSizeComposite = new FontSizeComposite(valueLabelControlGroup, SWT.NONE, 8, 48, 1, 8);
		this.valueLabelFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.valueLabelFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				ProfileEditor.this.exampleValueLabel.setFont(ProfileEditor.this.exampleValueLabel.getFont().deriveFont(size));
				ProfileEditor.this.exampleValueLabelSelected.setFont(ProfileEditor.this.exampleValueLabelSelected.getFont().deriveFont(size));
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.valueLabelFontSizeComposite);

		this.valueLabelFontStyleComposite = new FontStyleComposite(this.formToolkit, valueLabelControlGroup, SWT.NONE);
		this.valueLabelFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.valueLabelFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleValueLabel.getFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleValueLabel.setFont(ProfileEditor.this.exampleValueLabel.getFont().deriveFont(newStyle));
				ProfileEditor.this.exampleValueLabelSelected.setFont(ProfileEditor.this.exampleValueLabelSelected.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.valueLabelFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleValueLabel.getFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleValueLabel.setFont(ProfileEditor.this.exampleValueLabel.getFont().deriveFont(newStyle));
				ProfileEditor.this.exampleValueLabelSelected.setFont(ProfileEditor.this.exampleValueLabelSelected.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.valueLabelFontStyleComposite);

		colorGroup = new Group(valueLabelControlGroup, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(2, false));
		this.formToolkit.adapt(colorGroup);

		label = this.formToolkit.createLabel(colorGroup, "Schrift", SWT.NONE);
		label.setLayoutData(new GridData());

		this.valueLabelFg = new ColorSelector(colorGroup);
		this.valueLabelFg.getButton().setLayoutData(new GridData());
		this.valueLabelFg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleValueLabel.setForeground(newColor);
				ProfileEditor.this.exampleValueLabelSelected.setForeground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "Hintergrund", SWT.NONE);
		label.setLayoutData(new GridData());

		this.valueLabelBg = new ColorSelector(colorGroup);
		this.valueLabelBg.getButton().setLayoutData(new GridData());
		this.valueLabelBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleValueLabel.setBackground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "Hintergrund aktuell", SWT.NONE);
		label.setLayoutData(new GridData());

		this.selectedValueLabelBg = new ColorSelector(colorGroup);
		this.selectedValueLabelBg.getButton().setLayoutData(new GridData());
		this.selectedValueLabelBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleValueLabelSelected.setBackground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillInputLabelSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = false;

		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		final Composite c = this.formToolkit.createComposite(composite, SWT.NONE);
		c.setLayoutData(layoutData);
		c.setLayout(gridLayout);

		final GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 60;
		gridData.minimumWidth = 180;

		final Composite key = this.formToolkit.createComposite(c, SWT.EMBEDDED);
		key.setLayoutData(gridData);

		final Frame frame = SWT_AWT.new_Frame(key);
		frame.setLayout(new java.awt.GridLayout(1, 3));

		this.exampleInputNameLabel = new JLabel("Beschriftung");
		this.exampleInputNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		this.exampleInputNameLabel.setOpaque(true);
		this.exampleInputNameLabel.setBorder(BorderUIResource.getEtchedBorderUIResource());
		frame.add(this.exampleInputNameLabel);

		frame.pack();

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Group inputNameLabelControlGroup = new Group(composite, SWT.None);
		inputNameLabelControlGroup.setLayoutData(layoutData);
		inputNameLabelControlGroup.setLayout(new GridLayout(3, false));
		inputNameLabelControlGroup.setText("Beschriftungen");
		this.formToolkit.adapt(inputNameLabelControlGroup);

		this.inputNameLabelFontSizeComposite = new FontSizeComposite(inputNameLabelControlGroup, SWT.NONE, 8, 48, 1, 8);
		this.inputNameLabelFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.inputNameLabelFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				ProfileEditor.this.exampleInputNameLabel.setFont(ProfileEditor.this.exampleInputNameLabel.getFont().deriveFont(size));
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.inputNameLabelFontSizeComposite);

		this.inputNameLabelFontStyleComposite = new FontStyleComposite(this.formToolkit, inputNameLabelControlGroup, SWT.NONE);
		this.inputNameLabelFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.inputNameLabelFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleNameLabel.getFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleInputNameLabel.setFont(ProfileEditor.this.exampleInputNameLabel.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.inputNameLabelFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleNameLabel.getFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleInputNameLabel.setFont(ProfileEditor.this.exampleInputNameLabel.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.inputNameLabelFontStyleComposite);

		Group colorGroup = new Group(inputNameLabelControlGroup, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(2, false));
		this.formToolkit.adapt(colorGroup);

		Label label = this.formToolkit.createLabel(colorGroup, "Schrift", SWT.NONE);
		label.setLayoutData(new GridData());

		this.inputNameLabelFg = new ColorSelector(colorGroup);
		this.inputNameLabelFg.getButton().setLayoutData(new GridData());
		this.inputNameLabelFg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleInputNameLabel.setForeground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "Hintergrund", SWT.NONE);
		label.setLayoutData(new GridData());

		this.inputNameLabelBg = new ColorSelector(colorGroup);
		this.inputNameLabelBg.getButton().setLayoutData(new GridData());
		this.inputNameLabelBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleInputNameLabel.setBackground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillListSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.align = TableWrapData.FILL;
		layoutData.grabHorizontal = false;

		final GridLayout gridLayout = new GridLayout();
		gridLayout.horizontalSpacing = 0;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.verticalSpacing = 0;

		final Composite c = this.formToolkit.createComposite(composite, SWT.NONE);
		c.setLayoutData(layoutData);
		c.setLayout(gridLayout);

		final GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 120;
		gridData.minimumWidth = 180;

		final Composite key = this.formToolkit.createComposite(c, SWT.EMBEDDED);
		key.setLayoutData(gridData);

		final Frame frame = SWT_AWT.new_Frame(key);
		frame.setLayout(new java.awt.GridLayout(1, 3));

		this.exampleTable = this.createExampleTable();
		final JScrollPane scrollPane = new JScrollPane(this.exampleTable);
		scrollPane.setFont(this.exampleTable.getFont());
		frame.add(scrollPane);
		frame.pack();

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Group listControlGroup = new Group(composite, SWT.None);
		listControlGroup.setLayoutData(layoutData);
		listControlGroup.setLayout(new GridLayout(3, false));
		listControlGroup.setText("Beschriftungen");
		this.formToolkit.adapt(listControlGroup);

		this.listFontSizeComposite = new FontSizeComposite(listControlGroup, SWT.NONE, 8, 48, 1, 8);
		this.listFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.listFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				ProfileEditor.this.exampleTable.setFont(ProfileEditor.this.exampleTable.getFont().deriveFont(size));
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.listFontSizeComposite);

		this.listFontStyleComposite = new FontStyleComposite(this.formToolkit, listControlGroup, SWT.NONE);
		this.listFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.listFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleTable.getFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleTable.setFont(ProfileEditor.this.exampleTable.getFont().deriveFont(newStyle));
				ProfileEditor.this.resizeColumns(ProfileEditor.this.exampleTable);
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.listFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleTable.getFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleTable.setFont(ProfileEditor.this.exampleTable.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.listFontStyleComposite);

		final Group colorGroup = new Group(listControlGroup, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(2, false));
		this.formToolkit.adapt(colorGroup);

		Label label = this.formToolkit.createLabel(colorGroup, "Schrift", SWT.NONE);
		label.setLayoutData(new GridData());

		this.listFg = new ColorSelector(colorGroup);
		this.listFg.getButton().setLayoutData(new GridData());
		this.listFg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleTable.setForeground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "Hintergrund", SWT.NONE);
		label.setLayoutData(new GridData());

		this.listBg = new ColorSelector(colorGroup);
		this.listBg.getButton().setLayoutData(new GridData());
		this.listBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleTable.setBackground(newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillPanelSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		layout.makeColumnsEqualWidth = false;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
		composite.setLayoutData(layoutData);
		composite.setLayout(layout);

		layoutData = new TableWrapData();
		layoutData.grabHorizontal = false;

		final Composite userPanel = new Composite(composite, SWT.BORDER_SOLID);
		userPanel.setLayoutData(new TableWrapData(TableWrapData.FILL, TableWrapData.FILL));
		userPanel.setLayout(new GridLayout(2, true));

		final Image image = Activator.getDefault().getImageRegistry().get("display.png");
		final Rectangle bounds = image.getBounds();

		this.topLeft = this.formToolkit.createLabel(userPanel, "oben links", SWT.BORDER_SOLID);
		this.topLeft.setMenu(this.getPanelMenu(this.topLeft));
		this.topLeft.setBackground(this.lightGrey);
		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumWidth = bounds.width;
		gridData.minimumHeight = bounds.height;
		this.topLeft.setLayoutData(gridData);

		this.topRight = this.formToolkit.createLabel(userPanel, "oben rechts", SWT.BORDER_SOLID);
		this.topRight.setMenu(this.getPanelMenu(this.topRight));
		this.topRight.setBackground(this.lightGrey);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumWidth = bounds.width;
		gridData.minimumHeight = bounds.height;
		this.topRight.setLayoutData(gridData);

		this.bottomLeft = this.formToolkit.createLabel(userPanel, "unten links", SWT.BORDER_SOLID);
		this.bottomLeft.setMenu(this.getPanelMenu(this.bottomLeft));
		this.bottomLeft.setBackground(this.lightGrey);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumWidth = bounds.width;
		gridData.minimumHeight = bounds.height;
		this.bottomLeft.setLayoutData(gridData);

		this.bottomRight = this.formToolkit.createLabel(userPanel, "unten rechts", SWT.BORDER_SOLID);
		this.bottomRight.setMenu(this.getPanelMenu(this.bottomRight));
		this.bottomRight.setBackground(this.lightGrey);
		gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumWidth = bounds.width;
		gridData.minimumHeight = bounds.height;
		this.bottomRight.setLayoutData(gridData);

		final Composite sizePanel = new Composite(composite, SWT.None);
		sizePanel.setLayoutData(new TableWrapData(TableWrapData.FILL));
		sizePanel.setLayout(new GridLayout(3, true));

		Label label = this.formToolkit.createLabel(sizePanel, "Breite linke Fensterhälfte");
		label.setLayoutData(new GridData());
		
		gridData = new GridData();
		gridData.widthHint = 32;
		
		this.leftPercent = new Spinner(sizePanel, SWT.None);
		this.leftPercent.setLayoutData(gridData);
		this.leftPercent.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		this.leftPercent.setDigits(0);
		this.leftPercent.setIncrement(1);
		this.leftPercent.setPageIncrement(10);
		this.leftPercent.setMaximum(100);
		this.leftPercent.setMinimum(0);
		this.leftPercent.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				
			}});
		this.leftPercent.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ProfileEditor.this.setDirty(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		formToolkit.adapt(leftPercent);
		
		label = this.formToolkit.createLabel(sizePanel, "%");
		label.setLayoutData(new GridData());
		
		label = this.formToolkit.createLabel(sizePanel, "Höhe linker oberer Fensterteil");
		label.setLayoutData(new GridData());
		
		gridData = new GridData();
		gridData.widthHint = 32;
		
		this.topPercent = new Spinner(sizePanel, SWT.None);
		this.topPercent.setLayoutData(gridData);
		this.topPercent.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TREE_BORDER);
		this.topPercent.setDigits(0);
		this.topPercent.setIncrement(1);
		this.topPercent.setPageIncrement(10);
		this.topPercent.setMaximum(100);
		this.topPercent.setMinimum(0);
		this.topPercent.addModifyListener(new ModifyListener() 
		{
			@Override
			public void modifyText(ModifyEvent e)
			{
				
			}});
		this.topPercent.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e)
			{
				ProfileEditor.this.setDirty(true);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		formToolkit.adapt(topPercent);
		this.formToolkit.paintBordersFor(sizePanel);
		
		label = this.formToolkit.createLabel(sizePanel, "%");
		label.setLayoutData(new GridData());
		
		this.formToolkit.paintBordersFor(composite);

		return composite;
	}

	private Control fillTabSection(final Section parent)
	{
		final TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;

		TableWrapData layoutData = new TableWrapData();
		layoutData.grabHorizontal = true;

		final Composite composite = this.formToolkit.createComposite(parent);
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

		final Composite c = this.formToolkit.createComposite(composite, SWT.NONE);
		c.setLayoutData(layoutData);
		c.setLayout(gridLayout);

		final GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.minimumHeight = 60;
		gridData.minimumWidth = 200;

		final Composite tab = this.formToolkit.createComposite(c, SWT.EMBEDDED);
		tab.setLayoutData(gridData);

		this.tabbedPaneFrame = SWT_AWT.new_Frame(tab);
		this.tabbedPaneFrame.setLayout(new java.awt.GridLayout());

		this.exampleTabbedPane = new JTabbedPane();
		this.exampleTabbedPane.add("Tab 1", new JPanel());
		this.exampleTabbedPane.add("Tab 2", new JPanel());
		this.exampleTabbedPane.add("Tab 3", new JPanel());
		this.exampleTabbedPane.setSelectedIndex(0);
		this.exampleTabbedPane.addChangeListener(new ChangeListener()
		{

			@Override
			public void stateChanged(final ChangeEvent e)
			{
				final int tabCount = ProfileEditor.this.exampleTabbedPane.getTabCount();
				final int selectedIndex = ProfileEditor.this.exampleTabbedPane.getSelectedIndex();

				for (int i = 0; i < tabCount; i++)
				{
					if (selectedIndex == i)
					{
						final RGB rgb = ProfileEditor.this.tabbedPaneFgSelected.getColorValue();
						ProfileEditor.this.exampleTabbedPane.setForegroundAt(i, new java.awt.Color(rgb.red, rgb.green, rgb.blue));
					}
					else
					{
						final RGB fg = ProfileEditor.this.tabbedPaneFg.getColorValue();
						final RGB bg = ProfileEditor.this.tabbedPaneBg.getColorValue();
						ProfileEditor.this.exampleTabbedPane.setForegroundAt(i, new java.awt.Color(fg.red, fg.green, fg.blue));
						ProfileEditor.this.exampleTabbedPane.setBackgroundAt(i, new java.awt.Color(bg.red, bg.green, bg.blue));
					}
				}
			}

		});
		this.tabbedPaneFrame.add(this.exampleTabbedPane);

		layoutData = new TableWrapData(TableWrapData.FILL);
		layoutData.align = TableWrapData.FILL;

		final Composite tabbedPaneControlComposite = this.formToolkit.createComposite(composite, SWT.None);
		tabbedPaneControlComposite.setLayoutData(layoutData);
		tabbedPaneControlComposite.setLayout(new GridLayout(3, false));

		this.tabbedPaneFontSizeComposite = new FontSizeComposite(tabbedPaneControlComposite, SWT.NONE);
		this.tabbedPaneFontSizeComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.tabbedPaneFontSizeComposite.addSelectionListener(new SelectionListener()
		{
			public void widgetDefaultSelected(final SelectionEvent e)
			{
				this.widgetSelected(e);
			}

			public void widgetSelected(final SelectionEvent e)
			{
				final int selection = ((Scale) e.getSource()).getSelection();
				final float size = selection;
				for (int i = 0; i < ProfileEditor.this.exampleTabbedPane.getTabCount(); i++)
				{
					if (i == ProfileEditor.this.exampleTabbedPane.getSelectedIndex())
					{
						ProfileEditor.this.exampleTabbedPane.setFont(ProfileEditor.this.exampleTabbedPane.getFont().deriveFont(size));
					}
				}
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.tabbedPaneFontSizeComposite);

		this.tabbedPaneFontStyleComposite = new FontStyleComposite(this.formToolkit, tabbedPaneControlComposite, SWT.NONE);
		this.tabbedPaneFontStyleComposite.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		this.tabbedPaneFontStyleComposite.addBoldListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleTabbedPane.getFont();
				final int italic = font.getStyle() & java.awt.Font.ITALIC;
				final int bold = selection ? java.awt.Font.BOLD : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleTabbedPane.setFont(ProfileEditor.this.exampleTabbedPane.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.tabbedPaneFontStyleComposite.addItalicListener(new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Button button = (Button) e.widget;
				final boolean selection = button.getSelection();
				final java.awt.Font font = ProfileEditor.this.exampleTabbedPane.getFont();
				final int bold = font.getStyle() & java.awt.Font.BOLD;
				final int italic = selection ? java.awt.Font.ITALIC : 0;
				final int newStyle = italic | bold;
				ProfileEditor.this.exampleTabbedPane.setFont(ProfileEditor.this.exampleTabbedPane.getFont().deriveFont(newStyle));
				ProfileEditor.this.propertyChanged(e.widget, IEditorPart.PROP_DIRTY);
			}
		});
		this.formToolkit.adapt(this.tabbedPaneFontStyleComposite);

		final Group colorGroup = new Group(tabbedPaneControlComposite, SWT.NONE);
		colorGroup.setText("Farben");
		colorGroup.setLayoutData(new GridData(GridData.FILL_VERTICAL));
		colorGroup.setLayout(new GridLayout(2, false));
		this.formToolkit.adapt(colorGroup);

		Label label = this.formToolkit.createLabel(colorGroup, "Schrift selektiert", SWT.NONE);
		label.setLayoutData(new GridData());

		this.tabbedPaneFgSelected = new ColorSelector(colorGroup);
		this.tabbedPaneFgSelected.getButton().setLayoutData(new GridData());
		this.tabbedPaneFgSelected.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				ProfileEditor.this.exampleTabbedPane.setForegroundAt(ProfileEditor.this.exampleTabbedPane.getSelectedIndex(), newColor);
				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "Schrift nicht selektiert", SWT.NONE);
		label.setLayoutData(new GridData());

		this.tabbedPaneFg = new ColorSelector(colorGroup);
		this.tabbedPaneFg.getButton().setLayoutData(new GridData());
		this.tabbedPaneFg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				for (int i = 0; i < ProfileEditor.this.exampleTabbedPane.getTabCount(); i++)
				{
					if (i != ProfileEditor.this.exampleTabbedPane.getSelectedIndex())
					{
						ProfileEditor.this.exampleTabbedPane.setForegroundAt(i, newColor);
					}
				}

				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
			}
		});

		label = this.formToolkit.createLabel(colorGroup, "Hintergrund", SWT.NONE);
		label.setLayoutData(new GridData());

		this.tabbedPaneBg = new ColorSelector(colorGroup);
		this.tabbedPaneBg.getButton().setLayoutData(new GridData());
		this.tabbedPaneBg.addListener(new IPropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent e)
			{
				final RGB rgb = (RGB) e.getNewValue();
				final java.awt.Color newColor = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
				for (int i = 0; i < ProfileEditor.this.exampleTabbedPane.getTabCount(); i++)
				{
					if (i != ProfileEditor.this.exampleTabbedPane.getSelectedIndex())
					{
						ProfileEditor.this.exampleTabbedPane.setBackgroundAt(i, newColor);
					}
				}

				ProfileEditor.this.propertyChanged(e.getSource(), IEditorPart.PROP_DIRTY);
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
			msg.setMessage("Das Profil muss eine Bezeichnung haben.");
		}
		return msg;
	}

	private Menu getPanelMenu(final Label label)
	{
		final Menu menu = new Menu(label);

		MenuItem item = new MenuItem(menu, SWT.PUSH);
		item.setText("Betragsanzeige, Positionen- und Zahlungsdetails");
		item.setData(label);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Label label = (Label) ((MenuItem) e.widget).getData();
				label.setData(ProfileEditor.KEY_PANEL, PanelType.DISPLAY);
				final Image image = Activator.getDefault().getImageRegistry().get(ProfileEditor.DISPLAY_PANEL + ".png");
				label.setImage(image);
				ProfileEditor.this.setDirty(true);
			}
		});

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Numerischer Block");
		item.setData(label);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Label label = (Label) ((MenuItem) e.widget).getData();
				label.setData(ProfileEditor.KEY_PANEL, PanelType.NUMERIC);
				final Image image = Activator.getDefault().getImageRegistry().get(ProfileEditor.NUMERIC_PANEL + ".png");
				label.setImage(image);
				ProfileEditor.this.setDirty(true);
			}
		});

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Artikel und Zahlungsarten");
		item.setData(label);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Label label = (Label) ((MenuItem) e.widget).getData();
				label.setData(ProfileEditor.KEY_PANEL, PanelType.SELECTION);
				final Image image = Activator.getDefault().getImageRegistry().get(ProfileEditor.ARTICLE_PANEL + ".png");
				label.setImage(image);
				ProfileEditor.this.setDirty(true);
			}
		});

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Funktionen");
		item.setData(label);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Label label = (Label) ((MenuItem) e.widget).getData();
				label.setData(ProfileEditor.KEY_PANEL, PanelType.FUNCTION);
				final Image image = Activator.getDefault().getImageRegistry().get(ProfileEditor.FUNCTION_PANEL + ".png");
				label.setImage(image);
				ProfileEditor.this.setDirty(true);
			}
		});

		item = new MenuItem(menu, SWT.PUSH);
		item.setText("Leer");
		item.setData(label);
		item.addListener(SWT.Selection, new Listener()
		{
			public void handleEvent(final Event e)
			{
				final Label label = (Label) ((MenuItem) e.widget).getData();
				label.setData(ProfileEditor.KEY_PANEL, null);
				label.setImage(null);
				ProfileEditor.this.setDirty(true);
			}
		});

		return menu;
	}

	private Message getUniqueNameMessage()
	{
		Message msg = null;

		final Profile profile = (Profile) ((ProfileEditorInput) this.getEditorInput()).getAdapter(Profile.class);

		final String name = this.name.getText();

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ProfileQuery query = (ProfileQuery) persistenceService.getServerService().getQuery(Profile.class);
			if (!query.isUniqueName(name, profile.getId()))
			{
				msg = new Message(this.name, "Fehler");
				msg.setMessage("Die gewählte Profilbezeichnung wird bereits verwendet.");
				return msg;
			}
		}

		return msg;
	}

	private void resizeColumns(final JTable table)
	{
		final FontMetrics fm = table.getFontMetrics(table.getFont());

		for (int i = 1; i < table.getColumnCount(); i++)
		{
			final String title = (String) table.getColumnModel().getColumn(i).getHeaderValue();
			int stringWidth = fm.stringWidth(title);
			for (int j = 0; j < table.getRowCount(); j++)
			{
				final String val = table.getValueAt(j, i).toString();
				if (fm.stringWidth(val) + 6 > stringWidth)
				{
					stringWidth = fm.stringWidth(val) + 6;
				}
			}

			final TableColumn tableColumn = table.getColumnModel().getColumn(i);
			tableColumn.setMinWidth(stringWidth);
			tableColumn.setMaxWidth(stringWidth);
			tableColumn.setPreferredWidth(stringWidth);

			if ((i > 0) && (i < table.getColumnModel().getColumnCount() - 1))
			{
				((JLabel) tableColumn.getCellRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
			}
			else
			{
				((JLabel) tableColumn.getCellRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
			}
		}
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(0).setMaxWidth(600);
		table.doLayout();
	}
}
