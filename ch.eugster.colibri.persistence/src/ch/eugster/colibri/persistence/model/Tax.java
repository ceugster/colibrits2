/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.text.DecimalFormat;
import java.text.NumberFormat;
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "tx_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "tx_version")),
		@AttributeOverride(name = "update", column = @Column(name = "tx_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "tx_deleted")) })
@Table(name = "colibri_tax")
public class Tax extends AbstractEntity implements IReplicatable
{
	@Transient
	private static NumberFormat percentFormatter;

	@Id
	@Column(name = "tx_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tx_id")
	@TableGenerator(name = "tx_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tx_tr_id", referencedColumnName = "tr_id")
	private TaxRate taxRate;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tx_tt_id", referencedColumnName = "tt_id")
	private TaxType taxType;

	@OneToOne(optional = true)
	@JoinColumn(name = "tx_ct_id", referencedColumnName = "ct_id")
	private CurrentTax currentTax;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "tax")
	private Collection<CurrentTax> currentTaxes = new ArrayList<CurrentTax>();;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "tax")
	private Collection<TaxCodeMapping> taxCodeMappings = new ArrayList<TaxCodeMapping>();;

	@Basic
	@Column(name = "tx_text")
	private String text;

	@Basic
	@Column(name = "tx_account")
	private String account;

	protected Tax()
	{
		super();
	}

	protected Tax(final TaxRate taxRate, final TaxType taxType)
	{
		this();
		this.setTaxRate(taxRate);
		this.setTaxType(taxType);
	}

	public void addCurrentTax(final CurrentTax currentTax)
	{
		this.propertyChangeSupport.firePropertyChange("currentTaxes", this.currentTaxes,
				this.currentTaxes.add(currentTax));
	}

	public void addTaxCodeMapping(final TaxCodeMapping taxMapping)
	{
		this.propertyChangeSupport.firePropertyChange("taxCodeMappings", this.taxCodeMappings,
				this.taxCodeMappings.add(taxMapping));
	}

	public String format()
	{
		if (this.getId() == null)
		{
			return "";
		}
		else
		{
			// if (Tax.percentFormatter == null)
			// {
			Tax.percentFormatter = DecimalFormat.getPercentInstance();
			Tax.percentFormatter.setMaximumFractionDigits(3);
			Tax.percentFormatter.setMinimumFractionDigits(1);
			// }
			String text = this.getTaxType().getName() + " "
					+ Tax.percentFormatter.format(this.getCurrentTax().getPercentage());
			return text;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ITax#getAccount()
	 */
	public String getAccount()
	{
		return this.valueOf(this.account);
	}

	public String getCode()
	{
		return this.taxType.getCode() + this.taxRate.getCode();
	}

	public CurrentTax getCurrentTax()
	{
		return this.currentTax;
	}

	public Collection<CurrentTax> getCurrentTaxes()
	{
		return this.currentTaxes;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public TaxCodeMapping getTaxCodeMapping(final String provider)
	{
		for (final TaxCodeMapping taxMapping : this.taxCodeMappings)
		{
			if (taxMapping.getProvider().equals(provider))
			{
				if (!taxMapping.isDeleted())
				{
					return taxMapping;
				}
			}
		}

		return null;
	}

	public Collection<TaxCodeMapping> getTaxCodeMappings()
	{
		return this.taxCodeMappings;
	}

	public TaxRate getTaxRate()
	{
		return this.taxRate;
	}

	public TaxType getTaxType()
	{
		return this.taxType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ITax#getText()
	 */
	public String getText()
	{
		return this.valueOf(this.text);
	}

	public void removeCurrentTax(final CurrentTax currentTax)
	{
		this.propertyChangeSupport.firePropertyChange("currentTaxes", this.currentTaxes,
				this.currentTaxes.remove(currentTax));
	}

	public void removeTaxCodeMapping(final TaxCodeMapping taxMapping)
	{
		this.propertyChangeSupport.firePropertyChange("taxCodeMappings", this.taxCodeMappings,
				this.taxCodeMappings.remove(taxMapping));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ITax#setAccount(java.lang.String)
	 */
	public void setAccount(final String account)
	{
		this.propertyChangeSupport.firePropertyChange("account", this.account, this.account = account);
	}

	public void setCurrentTax(final CurrentTax currentTax)
	{
		this.propertyChangeSupport.firePropertyChange("currentTax", this.currentTax, this.currentTax = currentTax);
	}

	public void setCurrentTaxes(final Collection<CurrentTax> currentTaxes)
	{
		this.propertyChangeSupport.firePropertyChange("currentTaxes", this.currentTaxes,
				this.currentTaxes = currentTaxes);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setTaxCodeMappings(final Collection<TaxCodeMapping> taxMappings)
	{
		this.propertyChangeSupport.firePropertyChange("taxCodeMappings", this.taxCodeMappings,
				this.taxCodeMappings = taxMappings);
	}

	public void setTaxRate(final TaxRate taxRate)
	{
		this.propertyChangeSupport.firePropertyChange("taxRate", this.taxRate, this.taxRate = taxRate);
	}

	public void setTaxType(final TaxType taxType)
	{
		this.propertyChangeSupport.firePropertyChange("taxType", this.taxType, this.taxType = taxType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ITax#setText(java.lang.String)
	 */
	public void setText(final String text)
	{
		this.propertyChangeSupport.firePropertyChange("text", this.text, this.text = text);
	}

	public static Tax newInstance(final TaxRate taxRate, final TaxType taxType)
	{
		return (Tax) AbstractEntity.newInstance(new Tax(taxRate, taxType));
	}
}
