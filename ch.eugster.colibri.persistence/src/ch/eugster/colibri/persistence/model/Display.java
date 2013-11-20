package ch.eugster.colibri.persistence.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "di_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "di_version")),
		@AttributeOverride(name = "update", column = @Column(name = "di_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "di_deleted")) })
@Table(name = "colibri_display")
public class Display extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "di_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "di_id")
	@TableGenerator(name = "di_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(cascade = CascadeType.ALL, optional = true)
	@JoinColumn(name = "di_di_id")
	private Display display;

	@ManyToOne(optional = true)
	@JoinColumn(name = "di_sp_id")
	private Salespoint salespoint;

	@OneToOne(optional = true)
	@JoinColumn(name = "di_cd_id")
	private CustomerDisplaySettings customerDisplaySettings;

	@Basic
	@Column(name = "di_display_type")
	private String displayType;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "display")
	@MapKey(name = "displayAreaType")
	private Map<Integer, DisplayArea> displayAreas = new HashMap<Integer, DisplayArea>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "display")
	@MapKey(name = "display")
	private List<Display> displays = new Vector<Display>();

	protected Display()
	{
		super();
	}

	/**
	 * 
	 * @param displayType
	 * @param customerDisplaySettings
	 * 
	 *            this printout is a template devoted to a receipt printer
	 */
	protected Display(final String displayType, final CustomerDisplaySettings customerDisplaySettings)
	{
		super();
		this.setDisplayType(displayType);
		this.setCustomerDisplaySettings(customerDisplaySettings);
	}

	/**
	 * 
	 * @param displayType
	 * @param salespoint
	 * 
	 *            this printout is a printout for one salespoint
	 */
	protected Display(final String displayType, final Salespoint salespoint)
	{
		super();
		this.setDisplayType(displayType);
		this.setSalespoint(salespoint);
	}

	public void addDisplayArea(final DisplayArea displayArea)
	{
		DisplayArea da = this.displayAreas.get(Integer.valueOf(displayArea.getDisplayAreaType()));
		if (da == null)
		{
			da = DisplayArea.newInstance(this, displayArea.getDisplayAreaType());
		}
		else if (da.isDeleted())
		{
			da.setDeleted(false);
		}
		da.setPattern(displayArea.getPattern());
		da.setTimerDelay(displayArea.getTimerDelay());
		this.propertyChangeSupport.firePropertyChange("displayAreas", this.displayAreas,
				this.displayAreas.put(Integer.valueOf(displayArea.getDisplayAreaType()), da));
	}

	public void addPrintout(final Display display)
	{
		this.propertyChangeSupport.firePropertyChange("displays", this.displays, this.displays.add(display));
	}

	public List<Display> getChildren()
	{
		return this.displays;
	}

	public int getColumns()
	{
		if (this.getSalespoint() == null)
		{
			return this.customerDisplaySettings.getCols();
		}
		else if (this.getSalespoint().getCustomerDisplaySettings() == null)
		{
			return this.customerDisplaySettings.getCols();
		}
		else
		{
			return this.getSalespoint().getCustomerDisplaySettings().getCols();
		}
	}

	public CustomerDisplaySettings getCustomerDisplaySettings()
	{
		return this.customerDisplaySettings;
	}

	public DisplayArea getDisplayArea(final Integer displayAreaType)
	{
		return this.displayAreas.get(displayAreaType);
	}

	public Map<Integer, DisplayArea> getDisplayAreas()
	{
		return this.displayAreas;
	}

	public String getDisplayType()
	{
		return this.displayType;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public Display getParent()
	{
		return this.display;
	}

	public int getRows()
	{
		if (this.getSalespoint() == null)
		{
			return this.customerDisplaySettings.getRows();
		}
		else if (this.getSalespoint().getCustomerDisplaySettings() == null)
		{
			return this.customerDisplaySettings.getRows();
		}
		else
		{
			return this.getSalespoint().getCustomerDisplaySettings().getRows();
		}
	}

	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	public SalespointCustomerDisplaySettings getSalespointCustomerDisplaySettings()
	{
		return this.salespoint == null ? null : this.salespoint.getCustomerDisplaySettings();
	}

	public boolean hasParent()
	{
		return this.display instanceof Display;
	}

	public boolean isSalespointSpecific()
	{
		return this.salespoint instanceof Salespoint;
	}

	public void putDisplayArea(final DisplayArea displayArea)
	{
		this.propertyChangeSupport.firePropertyChange("displayAreas", this.displayAreas,
				this.displayAreas.put(displayArea.getDisplayAreaType(), displayArea));
	}

	public void removeDisplay(final Display display)
	{
		this.propertyChangeSupport.firePropertyChange("displays", this.displays, this.displays.remove(display));
	}

	public void removeDisplayArea(final DisplayArea displayArea)
	{
		final DisplayArea pa = this.displayAreas.get(displayArea.getDisplayAreaType());
		pa.setDeleted(true);
		this.propertyChangeSupport.firePropertyChange("displayAreas", this.displayAreas,
				this.displayAreas.put(displayArea.getDisplayAreaType(), pa));
	}

	public void setCustomerDisplaySettings(final CustomerDisplaySettings customerDisplaySettings)
	{
		this.propertyChangeSupport.firePropertyChange("customerDisplaySettings", customerDisplaySettings,
				this.customerDisplaySettings = customerDisplaySettings);
	}

	public void setDisplayAreas(final Map<Integer, DisplayArea> displayAreas)
	{
		this.propertyChangeSupport.firePropertyChange("displayAreas", displayAreas, this.displayAreas = displayAreas);
	}

	public void setDisplays(final List<Display> displays)
	{
		this.propertyChangeSupport.firePropertyChange("displays", displays, this.displays = displays);
	}

	public void setDisplayType(final String displayType)
	{
		this.propertyChangeSupport.firePropertyChange("displayType", displayType, this.displayType = displayType);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", id, this.id = id);
	}

	public void setParent(final Display display)
	{
		this.propertyChangeSupport.firePropertyChange("printout", display, this.display = display);
		if (display != null)
		{
			this.setDisplayType(display.getDisplayType());
			this.setCustomerDisplaySettings(display.getCustomerDisplaySettings());
		}
	}

	public void setSalespoint(final Salespoint salespoint)
	{
		this.propertyChangeSupport.firePropertyChange("salespoint", salespoint, this.salespoint = salespoint);
		if (salespoint != null && salespoint.getCustomerDisplaySettings() != null)
		{
			this.setCustomerDisplaySettings(salespoint.getCustomerDisplaySettings().getCustomerDisplaySettings());
		}
	}

	public static Display newInstance(final String displayType, final CustomerDisplaySettings customerDisplaySettings)
	{
		return (Display) AbstractEntity.newInstance(new Display(displayType, customerDisplaySettings));
	}

	public static Display newInstance(final String displayType, final Salespoint salespoint)
	{
		return (Display) AbstractEntity.newInstance(new Display(displayType, salespoint));
	}
}
