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
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "r_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "r_version")),
		@AttributeOverride(name = "update", column = @Column(name = "r_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "r_deleted")) })
@Table(name = "colibri_role_property")
public class RoleProperty extends AbstractEntity implements IReplicationRelevant
{
	@Id
	@Column(name = "r_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "r_id")
	@TableGenerator(name = "r_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "r_ro_id", referencedColumnName = "ro_id")
	private Role role;

	@Basic
	@Column(name = "r_key")
	private String key;

	@Basic
	@Column(name = "r_value")
	private String value;

	protected RoleProperty()
	{

	}

	protected RoleProperty(final Role role)
	{
		this.role = role;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getKey()
	{
		return this.valueOf(this.key);
	}

	public Role getRole()
	{
		return this.role;
	}

	public String getValue()
	{
		return this.valueOf(this.value);
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

	public void setRole(final Role role)
	{
		this.role = role;
	}

	public void setValue(final String value)
	{
		this.value = value;
	}

	public static RoleProperty newInstance(final Role role)
	{
		return (RoleProperty) AbstractEntity.newInstance(new RoleProperty(role));
	}
}
