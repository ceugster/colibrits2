/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Convert;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "st_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "st_version")),
		@AttributeOverride(name = "update", column = @Column(name = "st_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "st_deleted")) })
@Table(name = "colibri_stock")
public class Stock extends AbstractEntity implements Comparable<Stock>, IReplicatable
{
	@Id
	@Column(name = "st_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "st_id")
	@TableGenerator(name = "st_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "st_sp_id", referencedColumnName = "sp_id")
	private Salespoint salespoint;

	@ManyToOne(optional = false)
	@JoinColumn(name = "st_pt_id", referencedColumnName = "pt_id")
	private PaymentType paymentType;

	@OneToOne(optional = true)
	@JoinColumn(name = "st_se_id", referencedColumnName = "se_id")
	private Settlement lastCashSettlement;

	@Basic
	@Column(name = "st_amount", columnDefinition = "DECIMAL(18, 6)")
	private double amount;

	@Basic
	@Column(name = "st_variable")
	@Convert("booleanConverter")
	private boolean variable;

	protected Stock()
	{
		super();
	}
	
//	@PostPersist
//	public void postPersist()
//	{
//		this.getSalespoint().addStock(this);
//	}

	protected Stock(final Salespoint salespoint)
	{
		this.setSalespoint(salespoint);
	}

	private Stock(final Salespoint salespoint, final PaymentType paymentType)
	{
		this(salespoint);
		this.setPaymentType(paymentType);
	}

	public double getAmount()
	{
		return this.amount;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public PaymentType getPaymentType()
	{
		return this.paymentType;
	}

	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	public boolean isVariable()
	{
		return this.variable;
	}

	public void setAmount(final double amount)
	{
		this.propertyChangeSupport.firePropertyChange("amount", this.amount, this.amount = amount);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setPaymentType(final PaymentType paymentType)
	{
		this.propertyChangeSupport.firePropertyChange("paymentType", this.paymentType, this.paymentType = paymentType);
	}

	public void setSalespoint(final Salespoint salespoint)
	{
		this.propertyChangeSupport.firePropertyChange("salespoint", this.salespoint, this.salespoint = salespoint);
	}

	public void setVariable(final boolean variable)
	{
		this.propertyChangeSupport.firePropertyChange("variable", this.variable, this.variable = variable);
	}

	public static Stock newInstance(final Salespoint salespoint)
	{
		return new Stock(salespoint);
	}

	public static Stock newInstance(final Salespoint salespoint, final PaymentType paymentType)
	{
		final Stock stock = (Stock) AbstractEntity.newInstance(new Stock(salespoint, paymentType));
		stock.setAmount(0d);
		stock.setVariable(false);
		return stock;
	}

	public void setLastCashSettlement(Settlement settlement)
	{
		this.propertyChangeSupport.firePropertyChange("lastCashSettlement", this.lastCashSettlement,
				this.lastCashSettlement = settlement);
	}

	public Settlement getLastCashSettlement()
	{
		return lastCashSettlement;
	}

	@Override
	public int compareTo(Stock other)
	{
		if (this.getSalespoint().getPaymentType() != null)
		{
			if (this.getPaymentType().getId().equals(this.getSalespoint().getPaymentType().getId()))
			{
				return -1;
			}
			else if (other.getPaymentType().getId().equals(this.getSalespoint().getPaymentType().getId()))
			{
				return 1;
			}
		}
		return this.getPaymentType().getCurrency().getName().compareTo(other.getPaymentType().getCurrency().getName());
	}
}
