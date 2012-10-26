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
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "setx_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "setx_version")),
		@AttributeOverride(name = "update", column = @Column(name = "setx_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "setx_deleted")) })
@Table(name = "colibri_settlement_tax")
public class SettlementTax extends AbstractEntity implements Comparable<SettlementTax>, IPrintable
{
	@Id
	@Column(name = "setx_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "setx_id")
	@TableGenerator(name = "setx_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "setx_se_id", referencedColumnName = "se_id")
	private Settlement settlement;

	@ManyToOne(optional = false)
	@JoinColumn(name = "seop_ct_id", referencedColumnName = "ct_id")
	private CurrentTax currentTax;

	@Basic
	@Column(name = "setx_base_amount", columnDefinition = "DECIMAL(18, 6)")
	private double baseAmount;

	@Basic
	@Column(name = "setx_tax_amount", columnDefinition = "DECIMAL(18, 6)")
	private double taxAmount;

	@Basic
	@Column(name = "setx_quantity")
	private int quantity;

	private SettlementTax()
	{

	}

	private SettlementTax(final Settlement settlement, final CurrentTax currentTax)
	{
		this.settlement = settlement;
		this.currentTax = currentTax;
	}

	@Override
	public int compareTo(final SettlementTax other)
	{
		int result = this.compareIds(this.getCurrentTax().getTax().getTaxType().getId(), other.getCurrentTax().getTax()
				.getTaxType().getId());
		if (result == 0)
		{
			result = this.compareIds(this.getCurrentTax().getTax().getTaxRate().getId(), other.getCurrentTax().getTax()
					.getTaxRate().getId());
			if (result == 0)
			{
				result = this.compareIds(this.getCurrentTax().getId(), other.getCurrentTax().getId());
			}
		}
		return result;
	}

	public double getBaseAmount()
	{
		return this.baseAmount;
	}

	public CurrentTax getCurrentTax()
	{
		return this.currentTax;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public int getQuantity()
	{
		return this.quantity;
	}

	public Settlement getSettlement()
	{
		return this.settlement;
	}

	public double getTaxAmount()
	{
		return this.taxAmount;
	}

	public void setBaseAmount(final double amount)
	{
		this.baseAmount = amount;
	}

	public void setCurrentTax(final CurrentTax currentTax)
	{
		this.currentTax = currentTax;
	}

	@Override
	public void setId(final Long id)
	{
		this.id = id;
	}

	public void setQuantity(final int quantity)
	{
		this.quantity = quantity;
	}

	public void setSettlement(final Settlement settlement)
	{
		this.settlement = settlement;
	}

	public void setTaxAmount(final double taxAmount)
	{
		this.taxAmount = taxAmount;
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

	public static SettlementTax newInstance(final Settlement settlement, final CurrentTax currentTax)
	{
		return (SettlementTax) AbstractEntity.newInstance(new SettlementTax(settlement, currentTax));
	}

}
