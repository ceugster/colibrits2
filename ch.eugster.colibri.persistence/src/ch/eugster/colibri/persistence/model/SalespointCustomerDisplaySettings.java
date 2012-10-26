package ch.eugster.colibri.persistence.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@Table(name = "colibri_salespoint_customer_display_settings")
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "scd_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "scd_version")),
		@AttributeOverride(name = "update", column = @Column(name = "scd_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "scd_deleted")) })
public class SalespointCustomerDisplaySettings extends AbstractEntity implements IReplicationRelevant
{
	@Id
	@Column(name = "scd_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "scd_id")
	@TableGenerator(name = "scd_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = true)
	@JoinColumn(name = "scd_cd_id", referencedColumnName = "cd_id")
	private CustomerDisplaySettings customerDisplaySettings;

	@OneToOne(optional = true)
	@JoinColumn(name = "scd_sp_id", referencedColumnName = "sp_id")
	private Salespoint salespoint;

	@Basic
	@Column(name = "scd_port")
	private String port;

	@Basic
	@Lob
	@Column(name = "scd_converter")
	private String converter;

	@Basic
	@Column(name = "scd_cols")
	private Integer cols;

	@Basic
	@Column(name = "scd_rows")
	private Integer rows;

//	@Basic
//	@Column(name = "scd_delay")
//	private Integer delay;

	protected SalespointCustomerDisplaySettings()
	{
		super();
	}

	protected SalespointCustomerDisplaySettings(final CustomerDisplaySettings customerDisplaySettings, final Salespoint salespoint)
	{
		this.customerDisplaySettings = customerDisplaySettings;
		this.salespoint = salespoint;
	}

	public int getCols()
	{
		if (this.cols == null)
		{
			return this.getCustomerDisplaySettings().getCols();
		}
		return this.cols.intValue();
	}

	public String getComponentName()
	{
		return this.customerDisplaySettings.getComponentName();
	}

	public String getConverter()
	{
		if (this.converter instanceof String)
		{
			return this.converter;
		}
		return this.customerDisplaySettings.getConverter();
	}

	public CustomerDisplaySettings getCustomerDisplaySettings()
	{
		return this.customerDisplaySettings;
	}

//	public int getDelay()
//	{
//		if (this.delay == null)
//		{
//			return this.getCustomerDisplaySettings().getDelay();
//		}
//		return this.delay.intValue();
//	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.customerDisplaySettings.getName();
	}

	public String getPort()
	{
		return this.port == null ? this.getCustomerDisplaySettings().getPort() : this.port;
	}

	public int getRows()
	{
		if (this.rows == null)
		{
			return this.getCustomerDisplaySettings().getRows();
		}
		return this.rows.intValue();
	}

	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	public void setCols(final int cols)
	{
		final Integer value = (cols == this.getCustomerDisplaySettings().getCols()) ? null : Integer.valueOf(cols);
		this.propertyChangeSupport.firePropertyChange("cols", this.cols, this.cols = value);
	}

	public void setConverter(String converter)
	{
		if (this.getCustomerDisplaySettings().getConverter() != null && converter != null && converter.equals(this.getCustomerDisplaySettings().getConverter()))
		{
			converter = null;
		}
		this.propertyChangeSupport.firePropertyChange("converter", converter, this.converter = converter);
	}

	public void setCustomerDisplaySettings(final CustomerDisplaySettings customerDisplaySettings)
	{
		this.propertyChangeSupport.firePropertyChange("customerDisplaySettings", this.customerDisplaySettings,
				this.customerDisplaySettings = customerDisplaySettings);
	}

//	public void setDelay(final int delay)
//	{
//		final Integer value = delay == this.getCustomerDisplaySettings().getDelay() ? null : Integer.valueOf(delay);
//		this.propertyChangeSupport.firePropertyChange("delay", this.delay, this.delay = value);
//	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setPort(String port)
	{
		if (port == this.getCustomerDisplaySettings().getPort())
		{
			port = null;
		}
		this.propertyChangeSupport.firePropertyChange("port", this.port, this.port = port);
	}

	public void setRows(final int rows)
	{
		final Integer value = rows == this.getCustomerDisplaySettings().getRows() ? null : Integer.valueOf(rows);
		this.propertyChangeSupport.firePropertyChange("rows", this.rows, this.rows = value);
	}

	public void setSalespoint(final Salespoint salespoint)
	{
		this.propertyChangeSupport.firePropertyChange("salespoint", this.salespoint, this.salespoint = salespoint);
	}

	public static SalespointCustomerDisplaySettings newInstance()
	{
		return (SalespointCustomerDisplaySettings) AbstractEntity.newInstance(new SalespointCustomerDisplaySettings());
	}

	public static SalespointCustomerDisplaySettings newInstance(final CustomerDisplaySettings periphery, final Salespoint salespoint)
	{
		return (SalespointCustomerDisplaySettings) AbstractEntity.newInstance(new SalespointCustomerDisplaySettings(periphery, salespoint));
	}

}
