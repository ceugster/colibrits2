/*
 * Created on 14.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import java.util.Calendar;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ch.eugster.colibri.persistence.model.print.IPrintable;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "serr_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "serr_version")),
		@AttributeOverride(name = "update", column = @Column(name = "serr_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "serr_deleted")) })
@Table(name = "colibri_settlement_receipt")
public class SettlementReceipt extends AbstractEntity implements Comparable<SettlementReceipt>, IPrintable
{
	@Id
	@Column(name = "serr_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "serr_id")
	@TableGenerator(name = "serr_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "serr_se_id", referencedColumnName = "se_id")
	private Settlement settlement;

	@ManyToOne(optional = true)
	@JoinColumn(name = "serr_re_id", referencedColumnName = "re_id")
	private Receipt receipt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "serr_dcu_id", referencedColumnName = "cu_id")
	private Currency defaultCurrency;

	@Basic
	@Column(name = "serr_amount", columnDefinition = "DECIMAL(18, 6)")
	private double amount;

	@Basic
	@Column(name = "serr_number")
	private Long number;

	@Basic
	@Column(name = "serr_receipt_id")
	private Long receiptId;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "serr_time")
	private Calendar time;

	private SettlementReceipt()
	{

	}

	private SettlementReceipt(final Settlement settlement, final Receipt receipt)
	{
		this.settlement = settlement == null ? receipt.getSettlement() : settlement;
		this.setReceipt(receipt);
	}

	@Override
	public int compareTo(final SettlementReceipt other)
	{
		final int result = this.compareNumbers(this.getNumber(), other.getNumber());
		return result;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public Receipt getReceipt()
	{
		return this.receipt;
	}

	public Settlement getSettlement()
	{
		return this.settlement;
	}

	@Override
	public void setId(final Long id)
	{
		this.id = id;
	}

	public void setReceipt(final Receipt receipt)
	{
		this.receipt = receipt;
		this.setDefaultCurrency(receipt.getDefaultCurrency());
		this.setAmount(this.receipt
				.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
		this.setNumber(receipt.getNumber());
		this.setReceiptId(receipt.getId());
		this.setTime(receipt.getTimestamp());
	}

	public void setSettlement(final Settlement settlement)
	{
		this.settlement = settlement;
	}

	public Long getNumber()
	{
		return this.number;
	}

	public void setNumber(Long number)
	{
		this.number = number;
	}

	public Calendar getTime()
	{
		return this.time;
	}

	public void setTime(Calendar time)
	{
		this.time = time;
	}

	private int compareNumbers(final Long thisId, final Long otherId)
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

	public static SettlementReceipt newInstance(final Settlement settlement, final Receipt receipt)
	{
		return (SettlementReceipt) AbstractEntity.newInstance(new SettlementReceipt(settlement, receipt));
	}

	public void setReceiptId(Long receiptId)
	{
		this.receiptId = receiptId;
	}

	public Long getReceiptId()
	{
		return receiptId;
	}

	public void setAmount(double amount)
	{
		this.amount = amount;
	}

	public double getAmount()
	{
		return this.amount;
	}

	public void setDefaultCurrency(Currency defaultCurrency)
	{
		this.defaultCurrency = defaultCurrency;
	}

	public Currency getDefaultCurrency()
	{
		return defaultCurrency;
	}
}
