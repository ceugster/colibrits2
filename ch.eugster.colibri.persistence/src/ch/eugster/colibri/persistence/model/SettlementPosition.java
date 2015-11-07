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
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;

import ch.eugster.colibri.persistence.model.product.ProductGroupType;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "sepo_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "sepo_version")),
		@AttributeOverride(name = "update", column = @Column(name = "sepo_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "sepo_deleted")) })
@Table(name = "colibri_settlement_position")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "sepo_type", discriminatorType = DiscriminatorType.STRING, length = 1)
@DiscriminatorValue(value = "P")
public class SettlementPosition extends SettlementAbstractPosition implements Comparable<SettlementPosition>
{
	@Basic
	@Column(name = "sepo_tax_amount", columnDefinition = "DECIMAL(18, 6)")
	private double taxAmount;

	@Basic
	@Column(name = "sepo_quantity")
	private int quantity;

	protected SettlementPosition()
	{
		super();
	}

	protected SettlementPosition(final Settlement settlement, final ProductGroup productGroup, final Currency currency)
	{
		super(settlement, productGroup, currency);
	}

	@Override
	public int compareTo(SettlementPosition other)
	{
		int comparison = compareTypes(this.getProductGroup().getProductGroupType(), other.getProductGroup().getProductGroupType());
		if (comparison == 0)
		{
			comparison = compareCodes(this.getCode(), other.getCode());
			if (comparison == 0)
			{
				comparison = compareNames(this.getName(), other.getName());
			}
		}
		return comparison;
	}

//	@Override
//	public int compareTo(final SettlementPosition other)
//	{
//		int result = this.compareTypes(this.getProductGroup().getProductGroupType(), other.getProductGroup()
//				.getProductGroupType());
//		if (result == 0)
//		{
//			result = this.compareCodes(this.getProductGroup().getCode(), other.getProductGroup().getCode());
//			if (result == 0)
//			{
//				result = this.compareNames(this.getProductGroup().getName(), other.getProductGroup().getName());
//			}
//		}
//		return result;
//	}

	public int getQuantity()
	{
		return this.quantity;
	}

	public double getTaxAmount()
	{
		return this.taxAmount;
	}

	public void setQuantity(final int quantity)
	{
		this.quantity = quantity;
	}

	public void setTaxAmount(final double taxAmount)
	{
		this.taxAmount = taxAmount;
	}

	private int compareCodes(final String thisCode, final String otherCode)
	{
		if ((thisCode == null) && (otherCode == null))
		{
			return 0;
		}
		if (thisCode == null)
		{
			return 1;
		}
		else if (otherCode == null)
		{
			return -1;
		}
		else
		{
			return thisCode.compareTo(otherCode);
		}
	}

	private int compareNames(final String thisName, final String otherName)
	{
		if ((thisName == null) && (otherName == null))
		{
			return 0;
		}
		if (thisName == null)
		{
			return -1;
		}
		else if (otherName == null)
		{
			return 1;
		}
		else
		{
			if (thisName.equals(otherName))
			{
				return 0;
			}
			else
			{
				return thisName.compareTo(otherName);
			}
		}
	}

	private int compareTypes(final ProductGroupType thisType, final ProductGroupType otherType)
	{
		return thisType.compareTo(otherType);
	}

	public static SettlementPosition newInstance(final Settlement settlement, final ProductGroup productGroup,
			final Currency currency)
	{
		return (SettlementPosition) AbstractEntity.newInstance(new SettlementPosition(settlement, productGroup,
				currency));
	}

}
