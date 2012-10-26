/*
 * Created on 14.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

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
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import ch.eugster.colibri.persistence.model.print.IPrintable;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "sepa_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "sepa_version")),
		@AttributeOverride(name = "update", column = @Column(name = "sepa_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "sepa_deleted")) })
@Table(name = "colibri_settlement_payment")
public class SettlementPayment extends AbstractEntity implements Comparable<SettlementPayment>, IPrintable
{
	@Id
	@Column(name = "sepa_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sepa_id")
	@TableGenerator(name = "sepa_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "sepa_se_id", referencedColumnName = "se_id")
	private Settlement settlement;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sepa_pt_id", referencedColumnName = "pt_id")
	private PaymentType paymentType;

	@Basic
	@Column(name = "sepa_base_amount", columnDefinition = "DECIMAL(18, 6)")
	private double defaultCurrencyAmount;

	@Basic
	@Column(name = "sepa_tax_amount", columnDefinition = "DECIMAL(18, 6)")
	private double foreignCurrencyAmount;

	@Basic
	@Column(name = "sepa_quantity")
	private int quantity;

	private SettlementPayment()
	{

	}

	private SettlementPayment(final Settlement settlement, final PaymentType paymentType)
	{
		this.settlement = settlement;
		this.paymentType = paymentType;
	}

	@Override
	public int compareTo(final SettlementPayment other)
	{
		final int result = this.compareIds(this.getPaymentType().getId(), other.getPaymentType().getId());
		return result;
	}

	public double getDefaultCurrencyAmount()
	{
		return this.defaultCurrencyAmount;
	}

	public double getForeignCurrencyAmount()
	{
		return this.foreignCurrencyAmount;
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

	public int getQuantity()
	{
		return this.quantity;
	}

	public Settlement getSettlement()
	{
		return this.settlement;
	}

	public void setDefaultCurrencyAmount(final double defaultCurrencyAmount)
	{
		this.defaultCurrencyAmount = defaultCurrencyAmount;
	}

	public void setForeignCurrencyAmount(final double foreignCurrencyAmount)
	{
		this.foreignCurrencyAmount = foreignCurrencyAmount;
	}

	@Override
	public void setId(final Long id)
	{
		this.id = id;
	}

	public void setPaymentType(final PaymentType paymentType)
	{
		this.paymentType = paymentType;
	}

	public void setQuantity(final int quantity)
	{
		this.quantity = quantity;
	}

	public void setSettlement(final Settlement settlement)
	{
		this.settlement = settlement;
	}

	private int compareIds(final Long thisId, final Long otherId)
	{
		if ((thisId == null) && (otherId == null))
		{
			return 0;
		}
		if (thisId == null)
		{
			return -1;
		}
		else if (otherId == null)
		{
			return 1;
		}
		else
		{
			if (thisId.equals(otherId))
			{
				return 0;
			}
			else
			{
				return thisId.compareTo(otherId);
			}
		}
	}

	public static SettlementPayment newInstance(final Settlement settlement, final PaymentType paymentType)
	{
		return (SettlementPayment) AbstractEntity.newInstance(new SettlementPayment(settlement, paymentType));
	}

}
