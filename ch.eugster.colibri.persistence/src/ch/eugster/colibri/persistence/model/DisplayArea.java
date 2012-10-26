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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "dia_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "dia_version")),
		@AttributeOverride(name = "update", column = @Column(name = "dia_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "dia_deleted")) })
@Table(name = "colibri_display_area")
public class DisplayArea extends AbstractEntity implements Comparable<DisplayArea>, IReplicationRelevant
{
	@Id
	@Column(name = "dia_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "dia_id")
	@TableGenerator(name = "dia_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = true)
	@JoinColumn(name = "dia_di_id")
	private Display display;

	@Basic
	@Column(name = "dia_display_area_type")
	protected int displayAreaType;

	@Basic
	@Lob
	@Column(name = "dia_pattern")
	private String pattern;

	@Basic
	@Column(name = "dia_timer_delay")
	private int timerDelay = 5;

	protected DisplayArea()
	{
		super();
	}

	protected DisplayArea(final Display display, final int displayAreaType)
	{
		this.setDisplay(display);
		this.setDisplayAreaType(displayAreaType);
	}

	@Override
	public int compareTo(final DisplayArea other)
	{
		return this.displayAreaType - other.getDisplayAreaType();
	}

	public Display getDisplay()
	{
		return this.display;
	}

	public int getDisplayAreaType()
	{
		return this.displayAreaType;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getPattern()
	{
		return this.valueOf(this.pattern);
	}

	public int getTimerDelay()
	{
		return this.timerDelay;
	}

	public void setDisplay(final Display display)
	{
		this.propertyChangeSupport.firePropertyChange("display", this.display, this.display = display);
	}

	public void setDisplayAreaType(final int displayAreaType)
	{
		this.propertyChangeSupport.firePropertyChange("displayAreaType", this.displayAreaType, this.displayAreaType = displayAreaType);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setPattern(final String pattern)
	{
		this.propertyChangeSupport.firePropertyChange("pattern", this.pattern, this.pattern = pattern);
	}

	public void setTimerDelay(final int timerDelay)
	{
		this.propertyChangeSupport.firePropertyChange("timerDelay", this.timerDelay, this.timerDelay = timerDelay);
	}

	public static DisplayArea newInstance(final Display display, final int displayAreaType)
	{
		return (DisplayArea) AbstractEntity.newInstance(new DisplayArea(display, displayAreaType));
	}
}
