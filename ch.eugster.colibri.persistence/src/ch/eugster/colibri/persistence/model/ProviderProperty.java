package ch.eugster.colibri.persistence.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "pp_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "pp_version")),
		@AttributeOverride(name = "update", column = @Column(name = "pp_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "pp_deleted")) })
@Table(name = "colibri_provider_property")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "pp_type", discriminatorType = DiscriminatorType.STRING, length = 100)
@DiscriminatorValue("provider_property")
public class ProviderProperty extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "pp_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pp_id")
	@TableGenerator(name = "pp_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@Basic
	@Column(name = "pp_provider")
	private String provider;

	@ManyToOne(optional = false)
	@JoinColumn(name = "pp_sp_id", referencedColumnName = "sp_id")
	private Salespoint salespoint;

	@Basic
	@Column(name = "pp_key")
	private String key;

	@Basic
	@Column(name = "pp_value")
	private String value;

	protected ProviderProperty()
	{

	}

	protected ProviderProperty(final String provider)
	{
		this.provider = provider;
	}

	protected ProviderProperty(final String provider, final Salespoint salespoint)
	{
		this.provider = provider;
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

	public String getProvider()
	{
		return this.provider;
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

	public void setProvider(final String provider)
	{
		this.propertyChangeSupport.firePropertyChange("provider", this.provider, this.provider = provider);
	}

	public void setSalespoint(final Salespoint salespoint)
	{
		this.propertyChangeSupport.firePropertyChange("salespoint", this.salespoint, this.salespoint = salespoint);
	}

	public void setValue(final String value)
	{
		this.propertyChangeSupport.firePropertyChange("value", this.value, this.value = value);
	}

	public static ProviderProperty newInstance()
	{
		return (ProviderProperty) AbstractEntity.newInstance(new ProviderProperty());
	}

	public static ProviderProperty newInstance(final String provider)
	{
		return (ProviderProperty) AbstractEntity.newInstance(new ProviderProperty(provider));
	}

	public static ProviderProperty newInstance(final String provider, final Salespoint salespoint)
	{
		return (ProviderProperty) AbstractEntity.newInstance(new ProviderProperty(provider, salespoint));
	}
}
