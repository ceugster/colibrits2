/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides( { @AttributeOverride(name = "timestamp", column = @Column(name = "tab_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "tab_version")),
		@AttributeOverride(name = "update", column = @Column(name = "tab_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "tab_deleted")) })
@Table(name = "colibri_tab")
public class Tab extends AbstractEntity implements Comparable<Tab>, IReplicationRelevant
{
	@Id
	@Column(name = "tab_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tab_id")
	@TableGenerator(name = "tab_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tab_cfg_id", referencedColumnName = "cfg_id")
	private Configurable configurable;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "tab")
	private Collection<Key> keys = new ArrayList<Key>();

	@Basic
	@Column(name = "tab_name")
	private String name;

	@Basic
	@Column(name = "tab_rows")
	private int rows;

	@Basic
	@Column(name = "tab_cols")
	private int cols;

	@Basic
	@Column(name = "tab_pos")
	private int pos;

	private Tab()
	{
		super();
	}

	private Tab(final Configurable configurable)
	{
		this();
		this.configurable = configurable;
	}

	public void addKey(final Key key)
	{
		if (key != null)
		{
			if (!keys.contains(key))
			{
				propertyChangeSupport.firePropertyChange("keys", keys, keys.add(key));
			}
		}
	}

	public int compareTo(final Tab other)
	{
		return getPos() - other.getPos();
	}

	public int getCols()
	{
		return cols;
	}

	public Configurable getConfigurable()
	{
		return configurable;
	}

	@Override
	public Long getId()
	{
		return id;
	}

	public Collection<Key> getKeys()
	{
		return keys;
	}

	public String getName()
	{
		return valueOf(name);
	}

	public int getPos()
	{
		return pos;
	}

	public int getRows()
	{
		return rows;
	}

	public void removeKey(final Key key)
	{
		if (key != null)
		{
			if (keys.contains(key))
			{
				propertyChangeSupport.firePropertyChange("keys", keys, keys.remove(key));
			}
		}
	}

	public void setCols(final int cols)
	{
		propertyChangeSupport.firePropertyChange("cols", this.cols, this.cols = cols);
	}

	public void setConfigurable(final Configurable configurable)
	{
		propertyChangeSupport.firePropertyChange("configurable", this.configurable, this.configurable = configurable);
	}

	@Override
	public void setId(final Long id)
	{
		propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setKeys(final Collection<Key> keys)
	{
		propertyChangeSupport.firePropertyChange("keys", this.keys, this.keys = keys);
	}

	public void setName(final String name)
	{
		propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setPos(final int pos)
	{
		propertyChangeSupport.firePropertyChange("pos", this.pos, this.pos = pos);
	}

	public void setRows(final int rows)
	{
		propertyChangeSupport.firePropertyChange("rows", this.rows, this.rows = rows);
	}

	public void setDeleted(boolean deleted)
	{
		for (Key key : keys)
		{
			key.setDeleted(deleted);
		}
		super.setDeleted(deleted);
	}
	
	public static Tab newInstance(final Configurable configurable)
	{
		return (Tab) AbstractEntity.newInstance(new Tab(configurable));
	}

}
