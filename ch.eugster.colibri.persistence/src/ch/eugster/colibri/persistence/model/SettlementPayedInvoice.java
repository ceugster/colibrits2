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
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
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
@DiscriminatorValue(value = "Q")
public class SettlementPayedInvoice extends SettlementAbstractSinglePosition implements
		Comparable<SettlementPayedInvoice>
{
	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "sepo_date")
	private Calendar date;

	@Basic
	@Column(name = "sepo_number")
	private String number;

	@Basic
	@Column(name = "sepo_quantity")
	private int quantity;

	protected SettlementPayedInvoice()
	{
		super();
	}

	protected SettlementPayedInvoice(final Settlement settlement)
	{
		super(settlement);
	}

	protected SettlementPayedInvoice(final Settlement settlement, final Position position)
	{
		super(settlement, position);
		this.setPosition(position);
	}
	
	public void setPosition(Position position)
	{
		super.setPosition(position);
		this.setQuantity(1);
		this.setDate(position.getProduct() == null ? null : position.getProduct().getInvoiceDate());
		this.setNumber(position.getProduct() == null ? null : position.getProduct().getInvoiceNumber());
	}

	@Override
	public int compareTo(final SettlementPayedInvoice other)
	{
		return this.getId().compareTo(other.getId());
	}

	public static SettlementPayedInvoice newInstance(final Settlement settlement, final Position position)
	{
		return (SettlementPayedInvoice) AbstractEntity.newInstance(new SettlementPayedInvoice(settlement, position));
	}

	public static SettlementPayedInvoice newInstance(final Settlement settlement)
	{
		return (SettlementPayedInvoice) AbstractEntity.newInstance(new SettlementPayedInvoice(settlement));
	}

	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}

	public int getQuantity()
	{
		return quantity;
	}

	public void setDate(Calendar date)
	{
		this.date = date;
	}

	public Calendar getDate()
	{
		return date;
	}

	public void setNumber(String number)
	{
		this.number = number;
	}

	public String getNumber()
	{
		return this.valueOf(number);
	}
}
