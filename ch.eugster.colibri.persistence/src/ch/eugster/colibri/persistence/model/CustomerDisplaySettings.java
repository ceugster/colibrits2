package ch.eugster.colibri.persistence.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "colibri_customer_display_settings")
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "cd_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "cd_version")),
		@AttributeOverride(name = "update", column = @Column(name = "cd_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "cd_deleted")) })
public class CustomerDisplaySettings extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "cd_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "cd_id")
	@TableGenerator(name = "cd_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@Basic
	@Column(name = "cd_port")
	private String port;

	@Basic
	@Lob
	@Column(name = "cd_converter")
	private String converter;

	@Basic
	@Column(name = "cd_cols")
	private int cols;

	@Basic
	@Column(name = "cd_name")
	private String name;

	@Basic
	@Column(name = "cd_component_name")
	private String componentName;

	@Basic
	@Column(name = "cd_rows")
	private int rows;

//	@Basic
//	@Column(name = "cd_delay")
//	private int delay;

	@OneToMany(mappedBy = "customerDisplaySettings", cascade=CascadeType.ALL)
	@MapKey(name = "salespoint")
	private Map<Salespoint, SalespointCustomerDisplaySettings> salespointPeripheries = new HashMap<Salespoint, SalespointCustomerDisplaySettings>();

	protected CustomerDisplaySettings()
	{
		super();
	}

	public Integer getCols()
	{
		return this.cols;
	}

	public String getComponentName()
	{
		return valueOf(this.componentName);
	}

	public String getConverter()
	{
		return valueOf(converter);
	}

//	public int getDelay()
//	{
//		return this.delay;
//	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getName()
	{
		return valueOf(this.name);
	}

	public String getPort()
	{
		return valueOf(this.port);
	}

	public int getRows()
	{
		return this.rows;
	}

	public Map<Salespoint, SalespointCustomerDisplaySettings> getSalespointPeripheries()
	{
		return this.salespointPeripheries;
	}

	public SalespointCustomerDisplaySettings getSalespointPeriphery(final Salespoint salespoint)
	{
		return this.salespointPeripheries.get(salespoint);
	}

	public void setCols(final int cols)
	{
		this.propertyChangeSupport.firePropertyChange("cols", cols, this.cols = cols);
	}

	public void setComponentName(final String componentName)
	{
		this.propertyChangeSupport.firePropertyChange("componentName", componentName,
				this.componentName = componentName);
	}

	public void setConverter(final String converter)
	{
		this.propertyChangeSupport.firePropertyChange("converter", converter, this.converter = converter);
	}

//	public void setDelay(final int delay)
//	{
//		this.propertyChangeSupport.firePropertyChange("delay", delay, this.delay = delay);
//	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setPort(final String port)
	{
		this.propertyChangeSupport.firePropertyChange("port", port, this.port = port);
	}

	public void setRows(final int rows)
	{
		this.propertyChangeSupport.firePropertyChange("rows", rows, this.rows = rows);
	}
	
	public static CustomerDisplaySettings newInstance()
	{
		return (CustomerDisplaySettings) AbstractEntity.newInstance(new CustomerDisplaySettings());
	}
}
