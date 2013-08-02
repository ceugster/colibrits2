/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.util.Collection;
import java.util.Vector;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "cu_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "cu_version")),
		@AttributeOverride(name = "update", column = @Column(name = "cu_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "cu_deleted")) })
@Table(name = "colibri_currency")
public class Currency extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "cu_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "cu_id")
	@TableGenerator(name = "cu_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@OneToMany(fetch = EAGER, mappedBy = "currency")
	private Collection<PaymentType> paymentTypes = new Vector<PaymentType>();

	@Basic
	@Column(name = "cu_code")
	private String code;

	@Basic
	@Column(name = "cu_name")
	private String name;

	@Basic
	@Column(name = "cu_region")
	private String region;

	@Basic
	@Column(name = "cu_quotation")
	private double quotation;

	@Basic
	@Column(name = "cu_round_factor")
	private double roundFactor;

	protected Currency()
	{
		super();
	}

	public void addPaymentType(final PaymentType paymentType)
	{
		this.paymentTypes.add(paymentType);
	}

	public String format()
	{
		return this.getCode() + " (" + this.getName() + "), " + this.getRegion();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ICurrency#getCode()
	 */
	public String getCode()
	{
		return this.valueOf(this.code);
	}

	public java.util.Currency getCurrency()
	{
		return java.util.Currency.getInstance(this.getCode());
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ICurrency#getName()
	 */
	public String getName()
	{
		return this.valueOf(this.name);
	}

	public Collection<PaymentType> getPaymentTypes()
	{
		return this.paymentTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ICurrency#getQuotation()
	 */
	public double getQuotation()
	{
		return this.quotation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ICurrency#getRegion()
	 */
	public String getRegion()
	{
		return this.valueOf(this.region);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ICurrency#getRoundFactor()
	 */
	public double getRoundFactor()
	{
		return this.roundFactor;
	}

	public void removePaymentType(final PaymentType paymentType)
	{
		this.paymentTypes.remove(paymentType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICurrency#setCode(java.lang.String)
	 */
	public void setCode(final String code)
	{
		this.propertyChangeSupport.firePropertyChange("code", this.code, this.code = code);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICurrency#setName(java.lang.String)
	 */
	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setPaymentTypes(final Collection<PaymentType> paymentTypes)
	{
		this.paymentTypes = paymentTypes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ICurrency#setQuotation(double)
	 */
	public void setQuotation(final double quotation)
	{
		this.propertyChangeSupport.firePropertyChange("quotation", this.quotation, this.quotation = quotation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICurrency#setRegion(java.lang.String
	 * )
	 */
	public void setRegion(final String region)
	{
		this.propertyChangeSupport.firePropertyChange("region", this.region, this.region = region);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICurrency#setRoundFactor(double)
	 */
	public void setRoundFactor(final double roundFactor)
	{
		this.propertyChangeSupport.firePropertyChange("roundFactor", this.roundFactor, this.roundFactor = roundFactor);
	}

	public static Currency newInstance()
	{
		return (Currency) AbstractEntity.newInstance(new Currency());
	}

	public double change(double sourceCurrencyAmount, Currency sourceCurrency)
	{
		double sourceCurrencyQuotation = sourceCurrency.getQuotation();
		double targetCurrencyQuotation = this.getQuotation();
		double targetCurrencyRoundFactor = this.getRoundFactor();

		double targetCurrencyAmount = sourceCurrencyQuotation / targetCurrencyQuotation;
		return Math.floor((targetCurrencyAmount / targetCurrencyRoundFactor) + (0.5 + ROUND_FACTOR))
				* targetCurrencyRoundFactor;
	}
}
