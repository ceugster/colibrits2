/*
 * Created on 14.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "sepo_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "sepo_version")),
		@AttributeOverride(name = "update", column = @Column(name = "sepo_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "sepo_deleted")) })
@Table(name = "colibri_settlement_position")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sepo_type", discriminatorType = DiscriminatorType.STRING, length = 1)
@DiscriminatorValue(value = "I")
public class SettlementInternal extends SettlementAbstractSinglePosition implements Comparable<SettlementInternal>
{
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sepo_date")
	private Date date;

	@JoinColumn(name = "sepo_fcu_id", referencedColumnName = "cu_id")
	@OneToOne(optional = true)
	protected Currency foreignCurrency;

	@Basic
	@Column(name = "sepo_foreign_currency_amount", columnDefinition = "DECIMAL(18, 6)")
	protected double foreignCurrencyAmount;

	protected SettlementInternal()
	{
		super();
	}

	protected SettlementInternal(final Settlement settlement, final Position position)
	{
		super(settlement, position);
		this.setDate(position.getReceipt().getTimestamp());
		this.setForeignCurrency(position.getForeignCurrency());
		this.setForeignCurrencyAmount(position.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY,
				Position.AmountType.NETTO));
	}

	@Override
	public int compareTo(final SettlementInternal other)
	{
		return this.getId().compareTo(other.getId());
	}

	public static SettlementInternal newInstance(final Settlement settlement, final Position position)
	{
		return (SettlementInternal) AbstractEntity.newInstance(new SettlementInternal(settlement, position));
	}

	public void setDate(Date date)
	{
		this.date = date;
	}

	public Date getDate()
	{
		return date;
	}

	public Currency getForeignCurrency()
	{
		return this.foreignCurrency;
	}

	public double getForeignCurrencyAmount()
	{
		return this.foreignCurrencyAmount;
	}

	public void setForeignCurrency(final Currency foreignCurrency)
	{
		this.foreignCurrency = foreignCurrency;
	}

	public void setForeignCurrencyAmount(final double foreignCurrencyAmount)
	{
		this.foreignCurrencyAmount = foreignCurrencyAmount;
	}

}
