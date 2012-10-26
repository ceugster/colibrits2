/*
 * Created on 01.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import java.util.Collection;
import java.util.Vector;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Convert;

import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "pt_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "pt_version")),
		@AttributeOverride(name = "update", column = @Column(name = "pt_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "pt_deleted")) })
@Table(name = "colibri_payment_type")
public class PaymentType extends AbstractEntity implements IReplicationRelevant
{
	public static final Long CASH_ID = new Long(1l);

	@Transient
	private double value;

	@Id
	@Column(name = "pt_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pt_id")
	@TableGenerator(name = "pt_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "pt_cu_id", referencedColumnName = "cu_id")
	private Currency currency;

	/**
	 * Used for Credit Cards. The product group that is used to charge with credit card charges
	 */
	@ManyToOne
	@JoinColumn(name = "pt_pg_id", referencedColumnName = "pg_id")
	private ProductGroup productGroup;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "paymentType")
	private Collection<Money> moneys = new Vector<Money>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "paymentType")
	private Collection<Stock> stocks = new Vector<Stock>();

	@Basic
	@Column(name = "pt_code")
	private String code;

	/**
	 * Used for Credit Cards. See also <link>productGroup</link>. The charge for this credit card. Depends on charge type (percentual/absolute)
	 */
	@Basic
	@Column(name = "pt_percentual_charge")
	private double percentualCharge;

	@Basic
	@Column(name = "pt_fix_charge")
	private double fixCharge;

	@Basic
	@Column(name = "pt_charge")
	private double charge;
	/**
	 * Used for Credit Cards. See also <link>productGroup</link>
	 */
	@Basic
	@Column(name = "pt_charge_type")
	@Enumerated
	private ChargeType chargeType;

	@Basic
	@Column(name = "pt_name")
	private String name;

	@Basic
	@Column(name = "pt_mapping_id")
	private String mappingId;

	@Basic
	@Column(name = "pt_account")
	private String account;

	@Basic
	@Column(name = "pt_payment_type_group")
	@Enumerated
	private PaymentTypeGroup paymentTypeGroup;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "pt_open_cashdrawer")
	private boolean openCashdrawer;

	/**
	 * true if this paymentType is enabled to give change
	 */
	@Basic
	@Convert("booleanConverter")
	@Column(name = "pt_changer")
	private boolean changer;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "pt_undeletable")
	private boolean undeletable;

	protected PaymentType()
	{

	}

	protected PaymentType(final PaymentTypeGroup paymentTypeGroup)
	{
		this.paymentTypeGroup = paymentTypeGroup;
	}

	public void addMoney(final Money money)
	{
		this.moneys.add(money);
	}

	public void addStock(final Stock stock)
	{
		this.stocks.add(stock);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#getAccount()
	 */
	public String getAccount()
	{
		return this.valueOf(this.account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#getCode()
	 */
	public String getCode()
	{
		return this.valueOf(this.code);
	}

	public Currency getCurrency()
	{
		return this.currency;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#getMappingId()
	 */
	public String getMappingId()
	{
		return this.valueOf(this.mappingId);
	}

	public Collection<Money> getMoneys()
	{
		return this.moneys;
	}

	public Collection<Money> getMoneys(boolean deletedToo)
	{
		if (deletedToo)
		{
			return this.moneys;
		}
		else
		{
			Collection<Money> moneys = new Vector<Money>();
			for (Money money : this.moneys)
			{
				if (!money.isDeleted())
				{
					moneys.add(money);
				}
			}
			return moneys;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#getName()
	 */
	public String getName()
	{
		return this.valueOf(this.name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#getPaymentTypeGroup()
	 */
	public PaymentTypeGroup getPaymentTypeGroup()
	{
		return this.paymentTypeGroup;
	}

	public Collection<Stock> getStocks()
	{
		return this.stocks;
	}

	public double getValue()
	{
		return this.value;
	}

	@Override
	public int hashCode()
	{
		if (this.id == null)
		{
			return Integer.MIN_VALUE + this.getPaymentTypeGroup().ordinal();
		}
		else
		{
			return Integer.MIN_VALUE + this.getPaymentTypeGroup().ordinal() + this.id.intValue() * 10;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#isChange()
	 */
	public boolean isChange()
	{
		return this.changer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#isDeletable()
	 */
	public boolean isDeletable()
	{
		return !this.undeletable;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#isOpenCashdrawer()
	 */
	public boolean isOpenCashdrawer()
	{
		return this.openCashdrawer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#isUndeletable()
	 */
	public boolean isUndeletable()
	{
		return this.undeletable;
	}

	public void removeMoney(final Money money)
	{
		this.moneys.remove(money);
	}

	public void removeStock(final Stock stock)
	{
		this.stocks.remove(stock);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IMoney#setAccount(java.lang.String)
	 */
	public void setAccount(final String account)
	{
		this.propertyChangeSupport.firePropertyChange("account", this.account, this.account = account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#setChange(boolean)
	 */
	public void setChange(final boolean change)
	{
		this.propertyChangeSupport.firePropertyChange("change", this.changer, this.changer = change);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IMoney#setCode(java.lang.String)
	 */
	public void setCode(final String code)
	{
		this.propertyChangeSupport.firePropertyChange("code", this.code, this.code = code);
	}

	public void setCurrency(final Currency currency)
	{
		this.propertyChangeSupport.firePropertyChange("currency", this.currency, this.currency = currency);
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
	 * ch.eugster.colibri.persistence.model.IMoney#setMappingId(java.lang.String
	 * )
	 */
	public void setMappingId(final String mappingId)
	{
		this.propertyChangeSupport.firePropertyChange("mappingId", this.mappingId, this.mappingId = mappingId);
	}

	public void setMoneys(final Collection<Money> moneys)
	{
		this.propertyChangeSupport.firePropertyChange("moneys", this.moneys, this.moneys = moneys);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IMoney#setName(java.lang.String)
	 */
	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IMoney#setOpenCashdrawer(boolean)
	 */
	public void setOpenCashdrawer(final boolean openCashdrawer)
	{
		this.propertyChangeSupport.firePropertyChange("openCashdrawer", this.openCashdrawer,
				this.openCashdrawer = openCashdrawer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IMoney#setPaymentTypeGroup(ch.eugster
	 * .colibri.persistence.model.PaymentTypeGroup)
	 */
	public void setPaymentTypeGroup(final PaymentTypeGroup paymentTypeGroup)
	{
		this.propertyChangeSupport.firePropertyChange("group", this.paymentTypeGroup,
				this.paymentTypeGroup = paymentTypeGroup);
	}

	public void setStocks(final Collection<Stock> stocks)
	{
		this.propertyChangeSupport.firePropertyChange("stocks", this.stocks, this.stocks = stocks);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IMoney#setUndeletable(boolean)
	 */
	public void setUndeletable(final boolean undeletable)
	{
		this.undeletable = undeletable;
	}

	public void setValue(final double value)
	{
		this.value = value;
	}

	public static PaymentType newInstance(final PaymentTypeGroup paymentTypeGroup)
	{
		final PaymentType paymentType = (PaymentType) AbstractEntity.newInstance(new PaymentType(paymentTypeGroup));
		return paymentType;
	}

	public void setProductGroup(ProductGroup productGroup) 
	{
		this.propertyChangeSupport.firePropertyChange("productGroup", this.productGroup, this.productGroup = productGroup);
	}

	public ProductGroup getProductGroup() {
		return productGroup;
	}

	public void setPercentualCharge(double percentualCharge) 
	{
		this.propertyChangeSupport.firePropertyChange("percentualCharge", this.percentualCharge, this.percentualCharge = percentualCharge);
	}

	public double getPercentualCharge() {
		return percentualCharge;
	}

	public void setFixCharge(double fixCharge) 
	{
		this.propertyChangeSupport.firePropertyChange("fixCharge", this.fixCharge, this.fixCharge = fixCharge);
	}

	public double getFixCharge() {
		return fixCharge;
	}

	public void setChargeType(ChargeType chargeType) 
	{
		this.propertyChangeSupport.firePropertyChange("chargeType", this.chargeType, this.chargeType = chargeType);
	}

	public ChargeType getChargeType() {
		return chargeType;
	}
}
