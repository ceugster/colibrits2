/*
 * Created on 11.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "cfg_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "cfg_version")),
		@AttributeOverride(name = "update", column = @Column(name = "cfg_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "cfg_deleted")) })
@Table(name = "colibri_configurable")
public class Configurable extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "cfg_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "cfg_id")
	@TableGenerator(name = "cfg_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "cfg_pr_id", referencedColumnName = "pr_id")
	private Profile profile;

	@OneToOne(optional = true, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "cfg_pos_default_tab_id")
	private Tab positionDefaultTab;

	@OneToOne(optional = true, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "cfg_pay_default_tab_id")
	private Tab paymentDefaultTab;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "configurable")
	private Collection<Tab> tabs = new Vector<Tab>();

	@Basic
	@Column(name = "cfg_font_size", nullable = true)
	private Float fontSize;

	@Basic
	@Column(name = "cfg_font_style", nullable = true)
	private Integer fontStyle;

	@Basic
	@Column(name = "cfg_fg_selected", nullable = true)
	private Integer fgSelected;

	@Basic
	@Column(name = "cfg_fg", nullable = true)
	private Integer fg;

	@Basic
	@Column(name = "cfg_bg", nullable = true)
	private Integer bg;

	@Basic
	@Column(name = "cfg_type")
	@Enumerated
	private ConfigurableType configurableType;

	private Configurable()
	{
		super();
	}

	private Configurable(final Profile profile, final ConfigurableType configurableType)
	{
		this();
		this.setProfile(profile);
		this.setConfigurableType(configurableType);
	}

	public void addTab(final Tab tab)
	{
		if (!tabs.contains(tab))
		{
			this.propertyChangeSupport.firePropertyChange("tabs", this.tabs, this.tabs.add(tab));
		}
	}

	public int getBg()
	{
		return this.bg == null ? this.profile.getTabbedPaneBg() : this.bg.intValue();
	}

	public ConfigurableType getConfigurableType()
	{
		return this.configurableType;
	}

	public int getFg()
	{
		return this.fg == null ? this.profile.getTabbedPaneFg() : this.fg.intValue();
	}

	public int getFgSelected()
	{
		return this.fgSelected == null ? this.profile.getTabbedPaneFgSelected() : this.fgSelected.intValue();
	}

	public float getFontSize()
	{
		return this.fontSize == null ? this.profile.getTabbedPaneFontSize() : this.fontSize.floatValue();
	}

	public int getFontStyle()
	{
		return this.fontStyle == null ? this.profile.getTabbedPaneFontStyle() : this.fontStyle.intValue();
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public Tab getPaymentDefaultTab()
	{
		return this.paymentDefaultTab;
	}

	public Tab getPositionDefaultTab()
	{
		return this.positionDefaultTab;
	}

	public Profile getProfile()
	{
		return this.profile;
	}

	public Collection<Tab> getTabs()
	{
		return this.tabs;
	}

	public Collection<Tab> getActiveTabs()
	{
		Collection<Tab> activeTabs = new ArrayList<Tab>();
		for (Tab tab : this.tabs)
		{
			if (!tab.isDeleted())
			{
				activeTabs.add(tab);
			}
		}
		return activeTabs;
	}

	public ConfigurableType getType()
	{
		return this.configurableType;
	}

	@Override
	public int hashCode()
	{
		if (this.id == null)
		{
			return Integer.MIN_VALUE + this.getType().ordinal();
		}
		else
		{
			return Integer.MIN_VALUE + this.getType().ordinal() + this.id.intValue() * 10;
		}
	}

	public void removeTab(final Tab tab)
	{
		this.propertyChangeSupport.firePropertyChange("tabs", this.tabs, this.tabs.remove(tab));
	}

	public void setBg(final int bg)
	{
		this.propertyChangeSupport.firePropertyChange("fontSize", this.fontSize, this.bg = bg == this.profile.getTabbedPaneBg() ? null : bg);
	}

	public void setConfigurableType(final ConfigurableType configurableType)
	{
		this.propertyChangeSupport.firePropertyChange("type", this.configurableType, this.configurableType = configurableType);
	}

	public void setFg(final int fg)
	{
		this.propertyChangeSupport.firePropertyChange("fontSize", this.fontSize, this.fg = fg == this.profile.getTabbedPaneFg() ? null : fg);
	}

	public void setFgSelected(final int fgSelected)
	{
		this.propertyChangeSupport.firePropertyChange("fontSize", this.fontSize,
				this.fgSelected = fgSelected == this.profile.getTabbedPaneFgSelected() ? null : fgSelected);
	}

	public void setFontSize(final float fontSize)
	{
		this.propertyChangeSupport.firePropertyChange("fontSize", this.fontSize,
				this.fontSize = fontSize == this.profile.getTabbedPaneFontSize() ? null : fontSize);
	}

	public void setFontStyle(final int fontStyle)
	{
		this.propertyChangeSupport.firePropertyChange("fontSize", this.fontSize,
				this.fontStyle = fontStyle == this.profile.getTabbedPaneFontStyle() ? null : fontStyle);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setPaymentDefaultTab(final Tab paymentDefaultTab)
	{
		this.propertyChangeSupport.firePropertyChange("paymentDefaultTab", this.paymentDefaultTab, this.paymentDefaultTab = paymentDefaultTab);
	}

	public void setPositionDefaultTab(final Tab positionDefaultTab)
	{
		this.propertyChangeSupport.firePropertyChange("positionDefaultTab", this.positionDefaultTab, this.positionDefaultTab = positionDefaultTab);
	}

	public void setProfile(final Profile profile)
	{
		this.propertyChangeSupport.firePropertyChange("profile", this.profile, this.profile = profile);
		this.setBg(profile.getTabbedPaneBg());
		this.setFg(profile.getTabbedPaneFg());
		this.setFgSelected(profile.getTabbedPaneFgSelected());
		this.setFontSize(profile.getTabbedPaneFontSize());
		this.setFontStyle(profile.getTabbedPaneFontStyle());
		this.setPositionDefaultTab(null);
		this.setPaymentDefaultTab(null);
	}
	
	public void setDeleted(boolean deleted)
	{
		for (Tab tab : tabs)
		{
			tab.setDeleted(deleted);
		}
		super.setDeleted(deleted);
	}

	public void setTabs(final Collection<Tab> tabs)
	{
		this.propertyChangeSupport.firePropertyChange("tabs", this.tabs, this.tabs = tabs);
	}

	public static Configurable newInstance(final Profile profile, final ConfigurableType configurableType)
	{
		final Configurable configurable = (Configurable) AbstractEntity.newInstance(new Configurable(profile, configurableType));
		return configurable;
	}

	public enum ConfigurableType
	{
		PRODUCT_GROUP, PAYMENT_TYPE, FUNCTION;

		@Override
		public String toString()
		{
			if (this.equals(ConfigurableType.PRODUCT_GROUP))
			{
				return "Warengruppen";
			}
			else if (this.equals(ConfigurableType.PAYMENT_TYPE))
			{
				return "Zahlungsarten";
			}
			else if (this.equals(ConfigurableType.FUNCTION))
			{
				return "Funktionen";
			}

			throw new RuntimeException("Configurable Type invalid");
		}
	}

}
