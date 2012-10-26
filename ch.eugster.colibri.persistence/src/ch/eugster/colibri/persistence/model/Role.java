package ch.eugster.colibri.persistence.model;

import java.util.ArrayList;
import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "ro_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "ro_version")),
		@AttributeOverride(name = "update", column = @Column(name = "ro_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "ro_deleted")) })
@Table(name = "colibri_role")
public class Role extends AbstractEntity implements IReplicationRelevant
{
	@Id
	@Column(name = "ro_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ro_id")
	@TableGenerator(name = "ro_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "role")
	private Collection<RoleProperty> roleProperties = new Vector<RoleProperty>();

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "role")
	private Collection<User> users = new ArrayList<User>();

	@Basic
	@Column(name = "ro_name")
	private String name;

	public void addRoleProperty(final RoleProperty roleProperty)
	{
		this.propertyChangeSupport.firePropertyChange("roleProperties", this.roleProperties, this.roleProperties.add(roleProperty));
	}

	public void addUser(final User user)
	{
		this.propertyChangeSupport.firePropertyChange("users", this.users, this.users.add(user));
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getName()
	{
		return this.valueOf(this.name);
	}

	public boolean getPropertyValue(final String key)
	{
		if (this.getId().equals(Long.valueOf(1L)))
		{
			return true;
		}

		final Collection<RoleProperty> properties = this.getRoleProperties();
		for (final RoleProperty property : properties)
		{
			if (!property.isDeleted() && property.getKey().equals(key))
			{
				return Boolean.parseBoolean(property.getValue());
			}
		}
		return false;
	}

	public Collection<RoleProperty> getRoleProperties()
	{
		return this.roleProperties;
	}

	public RoleProperty getRoleProperty(final String name)
	{
		for (final RoleProperty property : this.roleProperties)
		{
			if (property.getKey().toLowerCase().equals(name.toLowerCase()))
			{
				return property;
			}
		}
		return null;
	}

	public String getRolePropertyValue(final String name)
	{
		for (final RoleProperty property : this.roleProperties)
		{
			if (property.getKey().toLowerCase().equals(name.toLowerCase()))
			{
				return property.getValue();
			}
		}
		return null;
	}

	public Collection<User> getUsers()
	{
		return this.users;
	}

	public void removeRoleProperty(final RoleProperty roleProperty)
	{
		this.propertyChangeSupport.firePropertyChange("roleProperties", this.roleProperties, this.roleProperties.remove(roleProperty));
	}

	public void removeUser(final User user)
	{
		this.propertyChangeSupport.firePropertyChange("users", this.users, this.users.remove(user));
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setRoleProperties(final Collection<RoleProperty> roleProperties)
	{
		this.propertyChangeSupport.firePropertyChange("roleProperties", this.roleProperties, this.roleProperties = roleProperties);
	}

	public void setUsers(final Collection<User> users)
	{
		this.propertyChangeSupport.firePropertyChange("users", this.users, this.users = users);
	}

	public static Role newInstance()
	{
		return (Role) AbstractEntity.newInstance(new Role());
	}
}
