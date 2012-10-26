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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import ch.eugster.colibri.persistence.model.print.IPrintable;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "semo_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "semo_version")),
		@AttributeOverride(name = "update", column = @Column(name = "semo_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "semo_deleted")) })
@Table(name = "colibri_settlement_money")
public class SettlementMoney extends AbstractEntity implements Comparable<SettlementMoney>, IPrintable
{
	@Id
	@Column(name = "semo_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "semo_id")
	@TableGenerator(name = "semo_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "semo_se_id", referencedColumnName = "se_id")
	private Settlement settlement;

	@JoinColumn(name = "semo_st_id", referencedColumnName = "st_id")
	@OneToOne(optional = false)
	private Stock stock;

	@JoinColumn(name = "semo_mo_id", referencedColumnName = "mo_id")
	@OneToOne(optional = false)
	private Money money;

	@JoinColumn(name = "semo_pt_id", referencedColumnName = "pt_id")
	@OneToOne(optional = false)
	private PaymentType paymentType;

	@Basic
	@Column(name = "semo_code")
	private String code;

	@Basic
	@Column(name = "semo_text")
	private String text;

	@Basic
	@Column(name = "semo_quantity")
	private int quantity;

	@Basic
	@Column(name = "semo_amount", columnDefinition = "DECIMAL(18, 6)")
	private double amount;

	private SettlementMoney()
	{

	}

	private SettlementMoney(final Settlement settlement, final Stock stock)
	{
		this.settlement = settlement;
		this.setStock(stock);
	}

	private SettlementMoney(final Settlement settlement, final Stock stock, final PaymentType paymentType)
	{
		this.settlement = settlement;
		this.setStock(stock);
		this.setPaymentType(paymentType);
	}

	@Override
	public int compareTo(final SettlementMoney other)
	{
		int result = this.getPaymentType().getCode().compareTo(other.getPaymentType().getCode());
		if (result == 0)
		{
			result = Double.valueOf(this.getMoney().getValue()).compareTo(Double.valueOf(other.getMoney().getValue()));
			if (result == 0)
			{
				result = this.getText().compareTo(other.getText());
			}
		}
		return result;
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

	public int getQuantity()
	{
		return this.quantity;
	}

	public Settlement getSettlement()
	{
		return this.settlement;
	}

	public Stock getStock()
	{
		return this.stock;
	}

	public void setAmount(final double amount)
	{
		this.amount = amount;
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

	public void setStock(final Stock stock)
	{
		this.stock = stock;
		this.setPaymentType(stock.getPaymentType());
	}

	public static SettlementMoney newInstance(final Settlement settlement, final Stock stock)
	{
		return (SettlementMoney) AbstractEntity.newInstance(new SettlementMoney(settlement, stock));
	}

	public static SettlementMoney newInstance(final Settlement settlement, final Stock stock,
			final PaymentType paymentType)
	{
		return (SettlementMoney) AbstractEntity.newInstance(new SettlementMoney(settlement, stock, paymentType));
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getCode()
	{
		return code;
	}

	public void setText(String text)
	{
		this.text = text;
	}

	public String getText()
	{
		return text;
	}

	public void setMoney(Money money)
	{
		this.money = money;
	}

	public Money getMoney()
	{
		return money;
	}
}
