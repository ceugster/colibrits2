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
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "csp_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "csp_version")),
		@AttributeOverride(name = "update", column = @Column(name = "csp_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "csp_deleted")) })
@Table(name = "colibri_common_settings_property")
public class CommonSettingsProperty extends AbstractEntity implements IReplicationRelevant
{
	@Id
	@Column(name = "csp_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "csp_id")
	@TableGenerator(name = "csp_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@Basic
	@Column(name = "csp_discriminator")
	private String discriminator;

	@ManyToOne(optional = false)
	@JoinColumn(name = "csp_cs_id", referencedColumnName = "cs_id")
	private CommonSettings commonSettings;

	@ManyToOne
	@JoinColumn(name = "csp_sp_id", referencedColumnName = "sp_id")
	private Salespoint salespoint;

	@Basic
	@Column(name = "csp_key")
	private String key;

	@Basic
	@Column(name = "csp_value")
	private String value;

	protected CommonSettingsProperty()
	{

	}

	protected CommonSettingsProperty(CommonSettings settings)
	{
		this(settings, null);
	}

	protected CommonSettingsProperty(CommonSettings settings, final Salespoint salespoint)
	{
		this.commonSettings = settings;
		this.salespoint = salespoint;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getKey()
	{
		return this.key;
	}

	public String getDiscriminator()
	{
		return this.discriminator;
	}

	public CommonSettings getCommonSettings()
	{
		return this.commonSettings;
	}

	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	public String getValue()
	{
		return this.value;
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setKey(final String key)
	{
		this.key = key;
	}

	public void setDiscriminator(final String discriminator)
	{
		this.propertyChangeSupport.firePropertyChange("discriminator", this.discriminator, this.discriminator = discriminator);
	}

	public void setCommonSettings(final CommonSettings commonSettings)
	{
		this.propertyChangeSupport.firePropertyChange("commonSettings", this.commonSettings, this.commonSettings = commonSettings);
	}

	public void setSalespoint(final Salespoint salespoint)
	{
		this.propertyChangeSupport.firePropertyChange("salespoint", this.salespoint, this.salespoint = salespoint);
	}

	public void setValue(final String value)
	{
		this.propertyChangeSupport.firePropertyChange("value", this.value, this.value = value);
	}

	public static CommonSettingsProperty newInstance(final CommonSettings settings)
	{
		return (CommonSettingsProperty) AbstractEntity.newInstance(new CommonSettingsProperty(settings));
	}

	public static CommonSettingsProperty newInstance(final CommonSettings settings, final Salespoint salespoint)
	{
		return (CommonSettingsProperty) AbstractEntity.newInstance(new CommonSettingsProperty(settings, salespoint));
	}
}
