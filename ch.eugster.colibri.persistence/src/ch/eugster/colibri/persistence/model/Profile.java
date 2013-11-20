package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.swing.SwingConstants;

import org.eclipse.persistence.annotations.Convert;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "pr_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "pr_version")),
		@AttributeOverride(name = "update", column = @Column(name = "pr_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "pr_deleted")) })
@Table(name = "colibri_profile")
public class Profile extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "pr_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pr_id")
	@TableGenerator(name = "pr_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@Basic
	@Column(name = "pr_name")
	private String name;

	@Basic
	@Column(name = "pr_top_left")
	@Enumerated
	protected PanelType topLeft;

	@Basic
	@Column(name = "pgm_top_right")
	@Enumerated
	protected PanelType topRight;

	@Basic
	@Column(name = "pgm_bottom_left")
	@Enumerated
	protected PanelType bottomLeft;

	@Basic
	@Column(name = "pgm_bottom_right")
	@Enumerated
	protected PanelType bottomRight;

	@Basic
	@Column(name = "pr_show_display_always")
	@Convert("booleanConverter")
	private boolean displayShowReceivedRemainderAlways;

	@Basic
	@Column(name = "pr_display_font_size")
	private float displayFontSize;

	@Basic
	@Column(name = "pr_display_font_style")
	private int displayFontStyle;

	@Basic
	@Column(name = "pr_display_fg")
	private int displayFg;

	@Basic
	@Column(name = "pr_display_bg")
	private int displayBg;

	@Basic
	@Column(name = "pr_tabbed_pane_font_size")
	private float tabbedPaneFontSize;

	@Basic
	@Column(name = "pr_tabbed_pane_font_style")
	private int tabbedPaneFontStyle;

	@Basic
	@Column(name = "pr_tabbed_pane_fg_selected")
	private int tabbedPaneFgSelected;

	@Basic
	@Column(name = "pr_tabbed_pane_fg")
	private int tabbedPaneFg;

	@Basic
	@Column(name = "pr_tabbed_pane_bg")
	private int tabbedPaneBg;

	/**
	 * Buttons
	 */
	@Basic
	@Column(name = "pr_btn_normal_font_size")
	private float buttonNormalFontSize;

	@Basic
	@Column(name = "pr_btn_normal_font_style")
	private int buttonNormalFontStyle;

	@Basic
	@Column(name = "pr_btn_normal_h_align")
	private int buttonNormalHorizontalAlign;

	@Basic
	@Column(name = "pr_btn_normal_v_align")
	private int buttonNormalVerticalAlign;

	@Basic
	@Column(name = "pr_btn_normal_fg")
	private int buttonNormalFg;

	@Basic
	@Column(name = "pr_btn_normal_bg")
	private int buttonNormalBg;

	@Basic
	@Column(name = "pr_btn_failover_font_size")
	private float buttonFailOverFontSize;

	@Basic
	@Column(name = "pr_btn_failover_font_style")
	private int buttonFailOverFontStyle;

	@Basic
	@Column(name = "pr_btn_failover_h_align")
	private int buttonFailOverHorizontalAlign;

	@Basic
	@Column(name = "pr_btn_failover_v_align")
	private int buttonFailOverVerticalAlign;

	@Basic
	@Column(name = "pr_btn_failover_fg")
	private int buttonFailOverFg;

	@Basic
	@Column(name = "pr_btn_failover_bg")
	private int buttonFailOverBg;

	/**
	 * Input Label
	 */
	@Basic
	@Column(name = "pr_input_name_lbl_font_size")
	private float inputNameLabelFontSize;

	@Basic
	@Column(name = "pr_input_name_lbl_font_style")
	private int inputNameLabelFontStyle;

	@Basic
	@Column(name = "pr_input_name_lbl_fg")
	private int inputNameLabelFg;

	@Basic
	@Column(name = "pr_input_name_lbl_bg")
	private int inputNameLabelBg;

	/**
	 * Labels
	 */
	@Basic
	@Column(name = "pr_name_lbl_font_size")
	private float nameLabelFontSize;

	@Basic
	@Column(name = "pr_name_lbl_font_style")
	private int nameLabelFontStyle;

	@Basic
	@Column(name = "pr_name_lbl_fg")
	private int nameLabelFg;

	@Basic
	@Column(name = "pr_name_lbl_bg")
	private int nameLabelBg;

	@Basic
	@Column(name = "pr_value_lbl_font_size")
	private float valueLabelFontSize;

	@Basic
	@Column(name = "pr_value_lbl_font_style")
	private int valueLabelFontStyle;

	@Basic
	@Column(name = "pr_value_lbl_fg")
	private int valueLabelFg;

	@Basic
	@Column(name = "pr_value_lbl_bg")
	private int valueLabelBg;

	@Basic
	@Column(name = "pr_value_lbl_bg_selected")
	private int valueLabelBgSelected;

	@Basic
	@Column(name = "pr_list_font_size")
	private float listFontSize;

	@Basic
	@Column(name = "pr_list_font_style")
	private int listFontStyle;

	@Basic
	@Column(name = "pr_list_fg")
	private int listFg;

	@Basic
	@Column(name = "pr_list_bg")
	private int listBg;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "profile")
	private List<Configurable> configurables = new ArrayList<Configurable>();

	@OneToMany(fetch = EAGER, mappedBy = "profile")
	private List<Salespoint> salespoints = new ArrayList<Salespoint>();

	private Profile()
	{
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IProfile#addConfigurable( Configurable)
	 */
	public void addConfigurable(final Configurable configurable)
	{
		if (configurable != null)
		{
			if (!this.configurables.contains(configurable))
			{
				this.propertyChangeSupport.firePropertyChange("configurables", this.configurables, this.configurables.add(configurable));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IProfile#addSalespoint(ch
	 * .eugster.colibri.persistence.server.model.Salespoint)
	 */
	public void addSalespoint(final Salespoint salespoint)
	{
		if (salespoint != null)
		{
			if (!this.salespoints.contains(salespoint))
			{
				this.propertyChangeSupport.firePropertyChange("salespoints", this.salespoints, this.salespoints.add(salespoint));
			}
		}
	}

	public PanelType getBottomLeft()
	{
		return this.bottomLeft;
	}

	public PanelType getBottomRight()
	{
		return this.bottomRight;
	}

	public int getButtonFailOverBg()
	{
		return this.buttonFailOverBg;
	}

	public int getButtonFailOverFg()
	{
		return this.buttonFailOverFg;
	}

	public float getButtonFailOverFontSize()
	{
		return this.buttonFailOverFontSize;
	}

	public int getButtonFailOverFontStyle()
	{
		return this.buttonFailOverFontStyle;
	}

	public int getButtonFailOverHorizontalAlign()
	{
		return this.buttonFailOverHorizontalAlign;
	}

	public int getButtonFailOverVerticalAlign()
	{
		return this.buttonFailOverVerticalAlign;
	}

	public int getButtonNormalBg()
	{
		return this.buttonNormalBg;
	}

	public int getButtonNormalFg()
	{
		return this.buttonNormalFg;
	}

	public float getButtonNormalFontSize()
	{
		return this.buttonNormalFontSize;
	}

	public int getButtonNormalFontStyle()
	{
		return this.buttonNormalFontStyle;
	}

	public int getButtonNormalHorizontalAlign()
	{
		return this.buttonNormalHorizontalAlign;
	}

	public int getButtonNormalVerticalAlign()
	{
		return this.buttonNormalVerticalAlign;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IProfile#getConfigurables()
	 */
	public List<Configurable> getConfigurables()
	{
		return this.configurables;
	}

	public List<Configurable> getActiveConfigurables()
	{
		List<Configurable> activeConfigurables = new ArrayList<Configurable>();
		for (Configurable configurable : this.configurables)
		{
			if (!configurable.isDeleted())
			{
				activeConfigurables.add(configurable);
			}
		}
		return activeConfigurables;
	}

	public int getDisplayBg()
	{
		return this.displayBg;
	}

	public int getDisplayFg()
	{
		return this.displayFg;
	}

	public float getDisplayFontSize()
	{
		return this.displayFontSize;
	}

	public int getDisplayFontStyle()
	{
		return this.displayFontStyle;
	}

	public boolean getDisplayShowReceivedRemainderAlways()
	{
		return this.displayShowReceivedRemainderAlways;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public int getListBg()
	{
		return this.listBg;
	}

	public int getListFg()
	{
		return this.listFg;
	}

	public float getListFontSize()
	{
		return this.listFontSize;
	}

	public int getListFontStyle()
	{
		return this.listFontStyle;
	}

	public String getName()
	{
		return this.valueOf(this.name);
	}

	public int getInputNameLabelBg()
	{
		return this.inputNameLabelBg;
	}

	public int getInputNameLabelFg()
	{
		return this.inputNameLabelFg;
	}

	public float getInputNameLabelFontSize()
	{
		return this.inputNameLabelFontSize;
	}

	public int getInputNameLabelFontStyle()
	{
		return this.inputNameLabelFontStyle;
	}

	public int getNameLabelBg()
	{
		return this.nameLabelBg;
	}

	public int getNameLabelFg()
	{
		return this.nameLabelFg;
	}

	public float getNameLabelFontSize()
	{
		return this.nameLabelFontSize;
	}

	public int getNameLabelFontStyle()
	{
		return this.nameLabelFontStyle;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IProfile#getSalespoints()
	 */
	public List<Salespoint> getSalespoints()
	{
		return this.salespoints;
	}

	public int getTabbedPaneBg()
	{
		return this.tabbedPaneBg;
	}

	public int getTabbedPaneFg()
	{
		return this.tabbedPaneFg;
	}

	public int getTabbedPaneFgSelected()
	{
		return this.tabbedPaneFgSelected;
	}

	public float getTabbedPaneFontSize()
	{
		return this.tabbedPaneFontSize;
	}

	public int getTabbedPaneFontStyle()
	{
		return this.tabbedPaneFontStyle;
	}

	public PanelType getTopLeft()
	{
		return this.topLeft;
	}

	public PanelType getTopRight()
	{
		return this.topRight;
	}

	public int getValueLabelBg()
	{
		return this.valueLabelBg;
	}

	public int getValueLabelBgSelected()
	{
		return this.valueLabelBgSelected;
	}

	public int getValueLabelFg()
	{
		return this.valueLabelFg;
	}

	public float getValueLabelFontSize()
	{
		return this.valueLabelFontSize;
	}

	public int getValueLabelFontStyle()
	{
		return this.valueLabelFontStyle;
	}

	public void initialize()
	{
		setDefaultValues();
		addConfigurables();
	}

	public void setDefaultValues()
	{
		this.setTopLeft(PanelType.DISPLAY);
		this.setBottomLeft(PanelType.NUMERIC);
		this.setTopRight(PanelType.SELECTION);
		this.setBottomRight(PanelType.FUNCTION);
		/*
		 * Display
		 */
		this.setDisplayFontSize(18f);
		this.setDisplayFontStyle(java.awt.Font.PLAIN);
		this.setDisplayFg(java.awt.Color.GREEN.getRGB());
		this.setDisplayBg(java.awt.Color.BLACK.getRGB());
		/*
		 * TabbedPane
		 */
		this.setTabbedPaneFontSize(18f);
		this.setTabbedPaneFontStyle(java.awt.Font.PLAIN);
		this.setTabbedPaneFgSelected(java.awt.Color.RED.getRGB());
		this.setTabbedPaneFg(java.awt.Color.GREEN.getRGB());
		this.setTabbedPaneBg(java.awt.Color.WHITE.getRGB());
		/*
		 * Buttons
		 */
		this.setButtonNormalFontSize(12f);
		this.setButtonNormalFontStyle(java.awt.Font.BOLD);
		this.setButtonNormalHorizontalAlign(SwingConstants.CENTER);
		this.setButtonNormalVerticalAlign(SwingConstants.CENTER);
		this.setButtonNormalFg(java.awt.Color.BLACK.getRGB());
		this.setButtonNormalBg(new java.awt.Color(207,207,207).getRGB());

		this.setButtonFailOverFontSize(12f);
		this.setButtonFailOverFontStyle(java.awt.Font.BOLD);
		this.setButtonFailOverHorizontalAlign(SwingConstants.CENTER);
		this.setButtonFailOverVerticalAlign(SwingConstants.CENTER);
		this.setButtonFailOverFg(java.awt.Color.BLACK.getRGB());
		this.setButtonFailOverBg(java.awt.Color.GRAY.getRGB());
		/*
		 * Input Labels
		 */
		this.setInputNameLabelFontSize(18f);
		this.setInputNameLabelFontStyle(Font.BOLD);
		this.setInputNameLabelFg(Color.BLACK.getRGB());
		this.setInputNameLabelBg(Color.WHITE.getRGB());
		/*
		 * Labels
		 */
		this.setNameLabelFontSize(12f);
		this.setNameLabelFontStyle(Font.BOLD);
		this.setNameLabelFg(Color.BLACK.getRGB());
		this.setNameLabelBg(Color.WHITE.getRGB());
		this.setValueLabelFontSize(12f);
		this.setValueLabelFontStyle(Font.BOLD);
		this.setValueLabelFg(Color.BLACK.getRGB());
		this.setValueLabelBg(Color.WHITE.getRGB());
		this.setValueLabelBgSelected(Color.WHITE.getRGB());
		/*
		 * List
		 */
		this.setListBg(Color.WHITE.getRGB());
		this.setListFg(Color.BLACK.getRGB());
		this.setListFontSize(12f);
		this.setListFontStyle(Font.BOLD);
	}
	
	public void addConfigurables()
	{
		this.configurables = new Vector<Configurable>();
		for (final Configurable.ConfigurableType configurableType : Configurable.ConfigurableType.values())
		{
			this.configurables.add(Configurable.newInstance(this, configurableType));
		}
	}
	
	public boolean isDisplayShowReceivedRemainderAlways()
	{
		return this.displayShowReceivedRemainderAlways;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IProfile#removeConfigurable (Configurable)
	 */
	public void removeConfigurable(final Configurable configurable)
	{
		if (configurable != null)
		{
			if (this.configurables.contains(configurable))
			{
				this.propertyChangeSupport.firePropertyChange("configurables", this.configurables, this.configurables.remove(this.configurables));
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IProfile#removeSalespoint (Salespoint)
	 */
	public void removeSalespoint(final Salespoint salespoint)
	{
		if (salespoint != null)
		{
			if (this.salespoints.contains(salespoint))
			{
				this.propertyChangeSupport.firePropertyChange("salespoints", this.salespoints, this.salespoints.remove(this.salespoints));
			}
		}
	}

	public void setBottomLeft(final PanelType bottomLeft)
	{
		this.propertyChangeSupport.firePropertyChange("bottomLeft", this.bottomLeft, this.bottomLeft = bottomLeft);
	}

	public void setBottomRight(final PanelType bottomRight)
	{
		this.propertyChangeSupport.firePropertyChange("bottomRight", this.bottomRight, this.bottomRight = bottomRight);
	}

	public void setButtonFailOverBg(final int buttonFailOverBg)
	{
		this.propertyChangeSupport.firePropertyChange("buttonFailOverBg", this.buttonFailOverBg, this.buttonFailOverBg = buttonFailOverBg);
	}

	public void setButtonFailOverFg(final int buttonFailOverFg)
	{
		this.propertyChangeSupport.firePropertyChange("buttonFailOverFg", this.buttonFailOverFg, this.buttonFailOverFg = buttonFailOverFg);
	}

	public void setButtonFailOverFontSize(final float buttonFailOverFontSize)
	{
		this.propertyChangeSupport.firePropertyChange("buttonFailOverFontSize", this.buttonFailOverFontSize,
				this.buttonFailOverFontSize = buttonFailOverFontSize);
	}

	public void setButtonFailOverFontStyle(final int buttonFailOverFontStyle)
	{
		this.propertyChangeSupport.firePropertyChange("buttonFailOverFontStyle", this.buttonFailOverFontStyle,
				this.buttonFailOverFontStyle = buttonFailOverFontStyle);
	}

	public void setButtonFailOverHorizontalAlign(final int buttonFailOverHorizontalAlign)
	{
		this.propertyChangeSupport.firePropertyChange("buttonFailOverHorizontalAlign", this.buttonFailOverHorizontalAlign,
				this.buttonFailOverHorizontalAlign = buttonFailOverHorizontalAlign);
	}

	public void setButtonFailOverVerticalAlign(final int buttonFailOverVerticalAlign)
	{
		this.propertyChangeSupport.firePropertyChange("buttonFailOverVerticalAlign", this.buttonFailOverVerticalAlign,
				this.buttonFailOverVerticalAlign = buttonFailOverVerticalAlign);
	}

	public void setButtonNormalBg(final int buttonNormalBg)
	{
		this.propertyChangeSupport.firePropertyChange("buttonNormalBg", this.buttonNormalBg, this.buttonNormalBg = buttonNormalBg);
	}

	public void setButtonNormalFg(final int buttonNormalFg)
	{
		this.propertyChangeSupport.firePropertyChange("buttonNormalFg", this.buttonNormalFg, this.buttonNormalFg = buttonNormalFg);
	}

	public void setButtonNormalFontSize(final float buttonNormalFontSize)
	{
		this.propertyChangeSupport.firePropertyChange("buttonNormalFontSize", this.buttonNormalFontSize,
				this.buttonNormalFontSize = buttonNormalFontSize);
	}

	public void setButtonNormalFontStyle(final int buttonNormalFontStyle)
	{
		this.propertyChangeSupport.firePropertyChange("buttonNormalFontStyle", this.buttonNormalFontStyle,
				this.buttonNormalFontStyle = buttonNormalFontStyle);
	}

	public void setButtonNormalHorizontalAlign(final int buttonNormalHorizontalAlign)
	{
		this.propertyChangeSupport.firePropertyChange("buttonNormalHorizontalAlign", this.buttonNormalHorizontalAlign,
				this.buttonNormalHorizontalAlign = buttonNormalHorizontalAlign);
	}

	public void setButtonNormalVerticalAlign(final int buttonNormalVerticalAlign)
	{
		this.propertyChangeSupport.firePropertyChange("buttonNormalVerticalAlign", this.buttonNormalVerticalAlign,
				this.buttonNormalVerticalAlign = buttonNormalVerticalAlign);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IProfile#setConfigurables (java.util.Collection)
	 */
	public void setConfigurables(final List<Configurable> configurables)
	{
		this.propertyChangeSupport.firePropertyChange("configurables", this.configurables, this.configurables = configurables);
	}

	public void setDisplayBg(final int bg)
	{
		this.propertyChangeSupport.firePropertyChange("displayBg", this.displayBg, this.displayBg = bg);
	}

	public void setDisplayFg(final int fg)
	{
		this.propertyChangeSupport.firePropertyChange("displayFg", this.displayFg, this.displayFg = fg);
	}

	public void setDisplayFontSize(final float size)
	{
		this.propertyChangeSupport.firePropertyChange("displayFontSize", this.displayFontSize, this.displayFontSize = size);
	}

	public void setDisplayFontStyle(final int style)
	{
		this.propertyChangeSupport.firePropertyChange("displayFontStyle", this.displayFontStyle, this.displayFontStyle = style);
	}

	public void setDisplayShowReceivedRemainderAlways(final boolean displayShowReceivedRemainderAlways)
	{
		this.propertyChangeSupport.firePropertyChange("displayShowReceivedRemainderAlways", this.displayShowReceivedRemainderAlways,
				this.displayShowReceivedRemainderAlways = displayShowReceivedRemainderAlways);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setListBg(final int listBg)
	{
		this.propertyChangeSupport.firePropertyChange("listBg", this.listBg, this.listBg = listBg);
		this.listBg = listBg;
	}

	public void setListFg(final int listFg)
	{
		this.propertyChangeSupport.firePropertyChange("listFg", this.listFg, this.listFg = listFg);
		this.listFg = listFg;
	}

	public void setListFontSize(final float listFontSize)
	{
		this.propertyChangeSupport.firePropertyChange("listFontSize", this.listFontSize, this.listFontSize = listFontSize);
		this.listFontSize = listFontSize;
	}

	public void setListFontStyle(final int listFontStyle)
	{
		this.propertyChangeSupport.firePropertyChange("listFontStyle", this.listFontStyle, this.listFontStyle = listFontStyle);
		this.listFontStyle = listFontStyle;
	}

	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setInputNameLabelBg(final int nameLabelBg)
	{
		this.propertyChangeSupport.firePropertyChange("inputNameLabelBg", this.inputNameLabelBg, this.inputNameLabelBg = nameLabelBg);
	}

	public void setInputNameLabelFg(final int nameLabelFg)
	{
		this.propertyChangeSupport.firePropertyChange("inputNameLabelFg", this.inputNameLabelFg, this.inputNameLabelFg = nameLabelFg);
	}

	public void setInputNameLabelFontSize(final float nameLabelFontSize)
	{
		this.propertyChangeSupport.firePropertyChange("inputNameLabelFontSize", this.inputNameLabelFontSize, this.inputNameLabelFontSize = nameLabelFontSize);
	}

	public void setInputNameLabelFontStyle(final int nameLabelFontStyle)
	{
		this.propertyChangeSupport.firePropertyChange("inputNameLabelFontStyle", this.inputNameLabelFontStyle, this.inputNameLabelFontStyle = nameLabelFontStyle);
	}

	public void setNameLabelBg(final int nameLabelBg)
	{
		this.propertyChangeSupport.firePropertyChange("nameLabelBg", this.nameLabelBg, this.nameLabelBg = nameLabelBg);
	}

	public void setNameLabelFg(final int nameLabelFg)
	{
		this.propertyChangeSupport.firePropertyChange("nameLabelFg", this.nameLabelFg, this.nameLabelFg = nameLabelFg);
	}

	public void setNameLabelFontSize(final float nameLabelFontSize)
	{
		this.propertyChangeSupport.firePropertyChange("nameLabelFontSize", this.nameLabelFontSize, this.nameLabelFontSize = nameLabelFontSize);
	}

	public void setNameLabelFontStyle(final int nameLabelFontStyle)
	{
		this.propertyChangeSupport.firePropertyChange("nameLabelFontStyle", this.nameLabelFontStyle, this.nameLabelFontStyle = nameLabelFontStyle);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IProfile#setSalespoints(java .util.Collection)
	 */
	public void setSalespoints(final List<Salespoint> salespoints)
	{
		this.propertyChangeSupport.firePropertyChange("salespoints", this.salespoints, this.salespoints = salespoints);
	}

	public void setTabbedPaneBg(final int tabbedPaneBg)
	{
		this.propertyChangeSupport.firePropertyChange("tabbedPaneBg", this.tabbedPaneBg, this.tabbedPaneBg = tabbedPaneBg);
	}

	public void setTabbedPaneFg(final int tabbedPaneFg)
	{
		this.propertyChangeSupport.firePropertyChange("tabbedPaneFg", this.tabbedPaneFg, this.tabbedPaneFg = tabbedPaneFg);
	}

	public void setTabbedPaneFgSelected(final int tabbedPaneFgSelected)
	{
		this.propertyChangeSupport.firePropertyChange("tabbedPaneFgSelected", this.tabbedPaneFgSelected,
				this.tabbedPaneFgSelected = tabbedPaneFgSelected);
	}

	public void setTabbedPaneFontSize(final float tabbedPaneFontSize)
	{
		this.propertyChangeSupport.firePropertyChange("tabbedPaneFontSize", this.tabbedPaneFontSize, this.tabbedPaneFontSize = tabbedPaneFontSize);
	}
	
	public void setDeleted(boolean deleted)
	{
		if (deleted && salespoints.isEmpty())
		{
			for (Configurable configurable : configurables)
			{
				configurable.setDeleted(deleted);
			}
		}
		super.setDeleted(deleted);
	}

	public void setTabbedPaneFontStyle(final int tabbedPaneFontStyle)
	{
		this.propertyChangeSupport.firePropertyChange("tabbedPaneFontStyle", this.tabbedPaneFontStyle,
				this.tabbedPaneFontStyle = tabbedPaneFontStyle);
	}

	public void setTopLeft(final PanelType topLeft)
	{
		this.propertyChangeSupport.firePropertyChange("topLeft", this.topLeft, this.topLeft = topLeft);
	}

	public void setTopRight(final PanelType topRight)
	{
		this.propertyChangeSupport.firePropertyChange("topRight", this.topRight, this.topRight = topRight);
	}

	public void setValueLabelBg(final int valueLabelBg)
	{
		this.propertyChangeSupport.firePropertyChange("valueLabelBg", this.valueLabelBg, this.valueLabelBg = valueLabelBg);
	}

	public void setValueLabelBgSelected(final int valueLabelBgSelected)
	{
		this.propertyChangeSupport.firePropertyChange("valueLabelBgSelected", this.valueLabelBgSelected,
				this.valueLabelBgSelected = valueLabelBgSelected);
	}

	public void setValueLabelFg(final int valueLabelFg)
	{
		this.propertyChangeSupport.firePropertyChange("valueLabelFg", this.valueLabelFg, this.valueLabelFg = valueLabelFg);
	}

	public void setValueLabelFontSize(final float valueLabelFontSize)
	{
		this.propertyChangeSupport.firePropertyChange("valueLabelFontSize", this.valueLabelFontSize, this.valueLabelFontSize = valueLabelFontSize);
	}

	public void setValueLabelFontStyle(final int valueLabelFontStyle)
	{
		this.propertyChangeSupport.firePropertyChange("valueLabelFontStyle", this.valueLabelFontStyle,
				this.valueLabelFontStyle = valueLabelFontStyle);
	}

	public Profile update(final Profile target)
	{
		target.setBottomLeft(this.getBottomLeft());
		target.setBottomRight(this.getBottomRight());
		target.setButtonFailOverBg(this.getButtonFailOverBg());
		target.setButtonFailOverFg(this.getButtonFailOverFg());
		target.setButtonFailOverFontSize(this.getButtonFailOverFontSize());
		target.setButtonFailOverFontStyle(this.getButtonFailOverFontStyle());
		target.setButtonFailOverHorizontalAlign(this.getButtonFailOverHorizontalAlign());
		target.setButtonFailOverVerticalAlign(this.getButtonFailOverVerticalAlign());
		target.setButtonNormalBg(this.getButtonNormalBg());
		target.setButtonNormalFg(this.getButtonNormalFg());
		target.setButtonNormalFontSize(this.getButtonNormalFontSize());
		target.setButtonNormalFontStyle(this.getButtonNormalFontStyle());
		target.setButtonNormalHorizontalAlign(this.getButtonNormalHorizontalAlign());
		target.setButtonNormalVerticalAlign(this.getButtonNormalVerticalAlign());
		target.setDisplayBg(this.getDisplayBg());
		target.setDisplayFg(this.getDisplayFg());
		target.setDisplayFontSize(this.getDisplayFontSize());
		target.setDisplayFontStyle(this.getDisplayFontStyle());
		target.setDisplayShowReceivedRemainderAlways(this.getDisplayShowReceivedRemainderAlways());
		target.setListBg(this.getListBg());
		target.setListFg(this.getListFg());
		target.setListFontSize(this.getListFontSize());
		target.setListFontStyle(this.getListFontStyle());
		target.setName(this.getName());
		target.setNameLabelBg(this.getNameLabelBg());
		target.setNameLabelFg(this.getNameLabelFg());
		target.setNameLabelFontSize(this.getNameLabelFontSize());
		target.setNameLabelFontStyle(this.getNameLabelFontStyle());
		target.setTabbedPaneBg(this.getTabbedPaneBg());
		target.setTabbedPaneFg(this.getTabbedPaneFg());
		target.setTabbedPaneFgSelected(this.getTabbedPaneFgSelected());
		target.setTabbedPaneFontSize(this.getButtonFailOverFontSize());
		target.setTabbedPaneFontStyle(this.getTabbedPaneFontStyle());
		target.setTopLeft(this.getTopLeft());
		target.setTopRight(this.getTopRight());
		target.setValueLabelBg(this.getValueLabelBg());
		target.setValueLabelBgSelected(this.getValueLabelBgSelected());
		target.setValueLabelFg(this.getValueLabelFg());
		target.setValueLabelFontSize(this.getValueLabelFontSize());
		target.setValueLabelFontStyle(this.getValueLabelFontStyle());
		return target;
	}

	public static Profile newInstance()
	{
		final Profile profile = (Profile) AbstractEntity.newInstance(new Profile());
		return profile;
	}

	public enum PanelType
	{
		DISPLAY, NUMERIC, FUNCTION, SELECTION;

		@Override
		public String toString()
		{
			if (this.equals(DISPLAY))
			{
				return "display";
			}
			if (this.equals(NUMERIC))
			{
				return "numeric";
			}
			if (this.equals(FUNCTION))
			{
				return "function";
			}
			if (this.equals(SELECTION))
			{
				return "selection";
			}
			else
			{
				throw new RuntimeException("Invalid panel");
			}
		}
	}

}
