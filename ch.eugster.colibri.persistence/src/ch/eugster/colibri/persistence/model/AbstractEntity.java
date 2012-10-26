package ch.eugster.colibri.persistence.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;

import javax.persistence.Basic;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.eclipse.persistence.annotations.ConversionValue;
import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Customizer;
import org.eclipse.persistence.annotations.ObjectTypeConverter;

import ch.eugster.colibri.persistence.events.EntityMediator;

@MappedSuperclass
@EntityListeners(EntityMediator.class)
@ObjectTypeConverter(name = "booleanConverter", dataType = java.lang.Short.class, objectType = java.lang.Boolean.class, conversionValues = {
		@ConversionValue(dataValue = "0", objectValue = "false"),
		@ConversionValue(dataValue = "1", objectValue = "true") }, defaultObjectValue = "false")
public abstract class AbstractEntity implements Entity
{
	public static final String ATTRIBUTE_NAME_VERSION = "version";

	public static final String ATTRIBUTE_NAME_UPDATE = "update";

	public static final String ATTRIBUTE_NAME_TIMESTAMP = "timestamp";

	public static final double ROUND_FACTOR = 0.0000001;

	@Transient
	protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	@Version
	private int version;

	@Basic
	@Convert("booleanConverter")
	private boolean deleted;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar timestamp;

	@Basic
	private int update;

	public AbstractEntity()
	{
	}

	public void addPropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
	{
		this.propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public boolean equals(final Object other)
	{
		if (this == other)
		{
			return true;
		}

		if ((other != null) && other.getClass().equals(this.getClass()))
		{
			final AbstractEntity entity = (AbstractEntity) other;
			if ((((AbstractEntity) other).getId() != null) && (this.getId() != null))
			{
				if (entity.getId().equals(this.getId()))
				{
					return true;
				}
			}
		}
		return false;
	}

	public PropertyChangeSupport getPropertyChangeSupport()
	{
		return this.propertyChangeSupport;
	}

	@Override
	public Calendar getTimestamp()
	{
		return this.timestamp;
	}

	@Override
	public int getUpdate()
	{
		return this.update;
	}

	@Override
	public int getVersion()
	{
		return this.version;
	}

	@Override
	public boolean isDeleted()
	{
		return this.deleted;
	}

	public void removePropertyChangeListener(final String propertyName, final PropertyChangeListener listener)
	{
		this.propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	@Override
	public void setDeleted(final boolean deleted)
	{
		this.propertyChangeSupport.firePropertyChange("deleted", this.deleted, this.deleted = deleted);
	}

	@Override
	public void setTimestamp(final Calendar timestamp)
	{
		this.propertyChangeSupport.firePropertyChange("timestamp", this.timestamp, this.timestamp = timestamp);
	}

	@Override
	public void setUpdate(final int update)
	{
		this.propertyChangeSupport.firePropertyChange("update", this.update, this.update = update);
	}

	@Override
	public void setVersion(final int version)
	{
		this.propertyChangeSupport.firePropertyChange("version", this.version, this.version = version);
	}

	public String valueOf(final String value)
	{
		return value == null ? "" : value;
	}

	protected AbstractEntity copy(final AbstractEntity target)
	{
		target.setDeleted(this.isDeleted());
		target.setId(this.getId());
		target.setTimestamp(this.getTimestamp());
		target.setUpdate(this.getUpdate());
		target.setVersion(this.getVersion());
		return target;
	}

	protected static AbstractEntity newInstance(final AbstractEntity entity)
	{
		entity.setId(null);
		entity.setTimestamp(Calendar.getInstance());
		entity.setVersion(0);
		entity.setDeleted(false);
		entity.setUpdate(0);
		return entity;
	}
}
