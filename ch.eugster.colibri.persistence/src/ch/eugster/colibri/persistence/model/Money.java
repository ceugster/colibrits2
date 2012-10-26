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
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "mo_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "mo_version")),
		@AttributeOverride(name = "update", column = @Column(name = "mo_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "mo_deleted")) })
@Table(name = "colibri_money")
public class Money extends AbstractEntity implements Comparable<Money>, IReplicationRelevant
{
	@Id
	@Column(name = "mo_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "mo_id")
	@TableGenerator(name = "mo_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "mo_pt_id", referencedColumnName = "pt_id")
	private PaymentType paymentType;

	@Basic
	@Column(name = "mo_value")
	private double value;

	@Transient
	private int count;

	protected Money()
	{
		super();
	}

	protected Money(final PaymentType paymentType)
	{
		this();
		this.setPaymentType(paymentType);
	}

	public void addCount(final int count)
	{
		this.count += count;
	}

	public int compareTo(final Money money)
	{
		return this.getValue() > money.getValue() ? -1 : this.getValue() == money.getValue() ? 0 : 1;
	}

	public int getCount()
	{
		return this.count;
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

	public double getSum()
	{
		return this.count * this.value;
	}

	public double getValue()
	{
		return this.value;
	}

	public void setCount(final int count)
	{
		this.count = count;
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

	public void setValue(final double value)
	{
		this.propertyChangeSupport.firePropertyChange("value", this.value, this.value = value);
	}

	public static Money newInstance(final PaymentType paymentType)
	{
		final Money money = (Money) AbstractEntity.newInstance(new Money(paymentType));
		return money;
	}
}
