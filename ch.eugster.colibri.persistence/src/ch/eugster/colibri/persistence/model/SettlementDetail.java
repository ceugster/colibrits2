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
import javax.persistence.Enumerated;
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
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "sede_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "sede_version")),
		@AttributeOverride(name = "update", column = @Column(name = "sede_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "sede_deleted")) })
@Table(name = "colibri_settlement_detail")
public class SettlementDetail extends AbstractEntity implements Comparable<SettlementDetail>, IPrintable
{
	@Id
	@Column(name = "sede_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sede_id")
	@TableGenerator(name = "sede_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "sede_se_id", referencedColumnName = "se_id")
	private Settlement settlement;

	@JoinColumn(name = "sede_st_id", referencedColumnName = "st_id")
	@OneToOne(optional = true, cascade = CascadeType.ALL)
	private Stock stock;

	@JoinColumn(name = "sede_pt_id", referencedColumnName = "pt_id")
	@OneToOne(optional = false)
	private PaymentType paymentType;

	@Basic
	@Column(name = "sede_quantity")
	private int quantity;

	@Basic
	@Column(name = "sede_debit", columnDefinition = "DECIMAL(18, 6)")
	private double debit;

	@Basic
	@Column(name = "sede_credit", columnDefinition = "DECIMAL(18, 6)")
	private double credit;

	@Basic
	@Enumerated
	@Column(name = "sede_part")
	private Part part;

	@Basic
	@Column(name = "sede_variable_stock")
	private boolean variableStock;

	private SettlementDetail()
	{

	}

	private SettlementDetail(final Settlement settlement, final Stock stock)
	{
		this.settlement = settlement;
		this.setStock(stock);
	}

	private SettlementDetail(final Settlement settlement, final Stock stock, final PaymentType paymentType)
	{
		this.settlement = settlement;
		this.setStock(stock);
		this.setPaymentType(paymentType);
	}

	@Override
	public int compareTo(final SettlementDetail other)
	{
		int result = this.getPart().compareTo(other.getPart());
		return result;
	}

	public void setCredit(double credit)
	{
		this.credit = credit;
	}

	public double getDebit()
	{
		return this.debit;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public double getCredit()
	{
		return this.credit;
	}

	public Part getPart()
	{
		return this.part;
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

	public boolean isVariableStock()
	{
		return this.variableStock;
	}

	public void setDebit(final double debit)
	{
		this.debit = debit;
	}

	@Override
	public void setId(final Long id)
	{
		this.id = id;
	}

	public void setPart(final Part part)
	{
		this.part = part;
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

	public void setVariableStock(final boolean variableStock)
	{
		this.variableStock = variableStock;
	}

	public static SettlementDetail newInstance(final Settlement settlement, final Stock stock)
	{
		return (SettlementDetail) AbstractEntity.newInstance(new SettlementDetail(settlement, stock));
	}

	public static SettlementDetail newInstance(final Settlement settlement, final Stock stock,
			final PaymentType paymentType)
	{
		return (SettlementDetail) AbstractEntity.newInstance(new SettlementDetail(settlement, stock, paymentType));
	}

	public enum Part
	{
		BEGIN_STOCK, INCOME, DIFFERENCE;

		public String label(final SettlementDetail detail)
		{
			switch (this)
			{
				case BEGIN_STOCK:
				{
					return "Anfangsbestand";
				}
				case INCOME:
				{
					return "Bewegungen";
				}
				case DIFFERENCE:
				{
					return "Differenz";
				}
				default:
				{
					throw new RuntimeException("Invalid part");
				}
			}
		}
	}
}
