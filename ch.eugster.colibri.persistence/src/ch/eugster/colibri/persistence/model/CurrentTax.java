/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "ct_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "ct_version")),
		@AttributeOverride(name = "update", column = @Column(name = "ct_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "ct_deleted")) })
@Table(name = "colibri_current_tax")
public class CurrentTax extends AbstractEntity implements IReplicatable
{
	private static NumberFormat nf = NumberFormat.getPercentInstance();

	private static DateFormat df = DateFormat.getDateInstance(DateFormat.LONG);

	@Id
	@Column(name = "ct_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ct_id")
	@TableGenerator(name = "ct_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ct_tx_id", referencedColumnName = "tx_id")
	private Tax tax;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "currentTax")
	private List<CurrentTaxCodeMapping> currentTaxCodeMappings = new ArrayList<CurrentTaxCodeMapping>();;

	@Basic
	@Column(name = "ct_percentage", columnDefinition = "DECIMAL(18, 6)")
	private double percentage;

	@Basic
	@Column(name = "ct_valid_from")
	private Long validFrom;

	private CurrentTax()
	{
		super();
	}

	private CurrentTax(final Tax tax)
	{
		this();
		this.setTax(tax);
	}

	public void addCurrentTaxCodeMapping(final CurrentTaxCodeMapping currentTaxCodeMapping)
	{
		this.propertyChangeSupport.firePropertyChange("currentTaxCodeMappings", this.currentTaxCodeMappings,
				this.currentTaxCodeMappings.add(currentTaxCodeMapping));
	}

	public String format()
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(this.getValidFrom().longValue());
		CurrentTax.nf.setMaximumFractionDigits(3);
		CurrentTax.nf.setMinimumFractionDigits(0);
		return CurrentTax.nf.format(this.getPercentage()) + ", Gültig ab " + CurrentTax.df.format(calendar.getTime());
	}

	public CurrentTaxCodeMapping getCurrentTaxCodeMapping(final String provider)
	{
		for (final CurrentTaxCodeMapping currentTaxCodeMapping : this.currentTaxCodeMappings)
		{
			if (currentTaxCodeMapping.getProvider().equals(provider))
			{
				return currentTaxCodeMapping;
			}
		}

		return null;
	}

	public List<CurrentTaxCodeMapping> getCurrentTaxCodeMappings()
	{
		return this.currentTaxCodeMappings;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public double getPercentage()
	{
		return this.percentage;
	}

	public Tax getTax()
	{
		return this.tax;
	}

	public Long getValidFrom()
	{
		return this.validFrom;
	}

	@Override
	public int hashCode()
	{
		if (this.id == null)
		{
			return Integer.MIN_VALUE + this.getTax().getId().intValue();
		}
		else
		{
			return Integer.MIN_VALUE + this.getTax().getId().intValue() + this.id.intValue() * 100;
		}
	}

	public void removeCurrentTaxCodeMapping(final CurrentTaxCodeMapping currentTaxCodeMapping)
	{
		this.propertyChangeSupport.firePropertyChange("currentTaxCodeMappings", this.currentTaxCodeMappings,
				this.currentTaxCodeMappings.remove(currentTaxCodeMapping));
	}

	public void setCurrentTaxCodeMappings(final List<CurrentTaxCodeMapping> currentTaxCodeMappings)
	{
		this.propertyChangeSupport.firePropertyChange("currentTaxCodeMappings", this.currentTaxCodeMappings,
				this.currentTaxCodeMappings = currentTaxCodeMappings);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setPercentage(final double percentage)
	{
		this.propertyChangeSupport.firePropertyChange("percentage", this.percentage, this.percentage = percentage);
	}

	public void setTax(final Tax tax)
	{
		this.propertyChangeSupport.firePropertyChange("tax", this.tax, this.tax = tax);
	}

	public void setValidFrom(final Long validFrom)
	{
		this.propertyChangeSupport.firePropertyChange("validFrom", this.validFrom, this.validFrom = validFrom);
	}

	public static String format(final CurrentTax currentTax)
	{
		final Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentTax.getValidFrom().longValue());
		CurrentTax.nf.setMaximumFractionDigits(3);
		CurrentTax.nf.setMinimumFractionDigits(0);
		return CurrentTax.nf.format(currentTax.getPercentage()) + ", Gültig ab "
				+ CurrentTax.df.format(calendar.getTime());
	}

	public static CurrentTax newInstance(final Tax tax)
	{
		final CurrentTax currentTax = (CurrentTax) AbstractEntity.newInstance(new CurrentTax(tax));
		return currentTax;
	}
}
