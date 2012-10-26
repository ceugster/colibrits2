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

import org.eclipse.persistence.annotations.Convert;

import ch.eugster.colibri.persistence.model.print.IPrintable;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "pa_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "pa_version")),
		@AttributeOverride(name = "update", column = @Column(name = "pa_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "pa_deleted")) })
@Table(name = "colibri_payment")
public class Payment extends AbstractEntity implements IPrintable
{
	@Id
	@Column(name = "pa_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pa_id")
	@TableGenerator(name = "pa_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "pa_re_id", referencedColumnName = "re_id")
	private Receipt receipt;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "pa_pt_id", referencedColumnName = "pt_id")
	private PaymentType paymentType;

	@Basic
	@Column(name = "pa_amount", columnDefinition = "DECIMAL(18, 6)")
	private double amount;

	@Basic
	@Column(name = "pa_fc_round_factor", columnDefinition = "DECIMAL(18, 6)")
	private double foreignCurrencyRoundFactor;

	@Basic
	@Column(name = "pa_fc_quotation", columnDefinition = "DECIMAL(18, 6)")
	private double foreignCurrencyQuotation;

	@Basic
	@Column(name = "pa_back")
	@Convert("booleanConverter")
	private boolean back;

	private Payment()
	{
		super();
	}

	private Payment(final PaymentType paymentType)
	{
		this();
		this.propertyChangeSupport.firePropertyChange("paymentType", this.paymentType, this.paymentType = paymentType);
	}

	private Payment(final Receipt receipt)
	{
		this();
		this.setReceipt(receipt);
	}

	public double getAmount()
	{
		return this.amount;
	}

	public double getAmount(final Receipt.QuotationType quotationType)
	{
		return this.round(this.getAmount() * this.quotation(quotationType), quotationType);
	}

	public double getForeignCurrencyQuotation()
	{
		return this.foreignCurrencyQuotation;
	}

	public double getForeignCurrencyRoundFactor()
	{
		return this.foreignCurrencyRoundFactor;
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

	public Receipt getReceipt()
	{
		return this.receipt;
	}

	public boolean isBack()
	{
		return this.back;
	}

	public void setAmount(final double amount)
	{
		this.propertyChangeSupport.firePropertyChange("amount", this.amount, this.amount = amount);
	}

	public void setAmount(double amount, final Receipt.QuotationType quotationType,
			final Receipt.QuotationType roundingQuotationType)
	{
		amount = this.round(amount * this.inverseQuotation(quotationType), roundingQuotationType);
		this.setAmount(amount);
	}

	public void setBack(final boolean back)
	{
		this.back = back;
	}

	public void setForeignCurrencyQuotation(final double quotation)
	{
		this.propertyChangeSupport.firePropertyChange("foreignCurrencyQuotation", this.foreignCurrencyQuotation,
				this.foreignCurrencyQuotation = quotation);
	}

	public void setForeignCurrencyRoundFactor(final double roundFactor)
	{
		this.propertyChangeSupport.firePropertyChange("foreignCurrencyRoundFactor", this.foreignCurrencyRoundFactor,
				this.foreignCurrencyRoundFactor = roundFactor);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setPaymentType(final PaymentType paymentType)
	{
		this.propertyChangeSupport.firePropertyChange("paymentType", this.paymentType, this.paymentType = paymentType);
		this.setForeignCurrencyQuotation(this.paymentType.getCurrency().getQuotation());
		this.setForeignCurrencyRoundFactor(this.paymentType.getCurrency().getRoundFactor());
		this.getReceipt().setForeignCurrency(paymentType.getCurrency());
	}

	public void setReceipt(final Receipt receipt)
	{
		this.propertyChangeSupport.firePropertyChange("receipt", this.receipt, this.receipt = receipt);
	}

	private double inverseQuotation(final Receipt.QuotationType quotationType)
	{
		switch (quotationType)
		{
			case REFERENCE_CURRENCY:
			{
				return this.getReceipt().getReferenceCurrencyQuotation() / this.getForeignCurrencyQuotation();
			}
			case DEFAULT_CURRENCY:
			{
				return this.getReceipt().getDefaultCurrencyQuotation() / this.getForeignCurrencyQuotation();
			}
			case FOREIGN_CURRENCY:
			{
				return 1;
			}
			case DEFAULT_FOREIGN_CURRENCY:
			{
				return this.getReceipt().getForeignCurrencyQuotation() / this.getForeignCurrencyQuotation();
			}
			default:
				return 0d;
		}
	}

	private double quotation(final Receipt.QuotationType quotationType)
	{
		double quotation = 0D;
		switch (quotationType)
		{
			case REFERENCE_CURRENCY:
			{
				quotation = this.getForeignCurrencyQuotation() / this.getReceipt().getReferenceCurrencyQuotation();
				break;
			}
			case DEFAULT_CURRENCY:
			{
				quotation = this.getForeignCurrencyQuotation() / this.getReceipt().getDefaultCurrencyQuotation();
				break;
			}
			case FOREIGN_CURRENCY:
			{
				quotation = 1;
				break;
			}
			case DEFAULT_FOREIGN_CURRENCY:
			{
				quotation = this.getForeignCurrencyQuotation() / this.getReceipt().getForeignCurrencyQuotation();
				break;
			}
		}
		return quotation;
	}

	private double round(final double amount, final Receipt.QuotationType quotationType)
	{
		double roundedAmount = 0D;
		switch (quotationType)
		{
			case REFERENCE_CURRENCY:
			{
				roundedAmount = Math.floor((amount / this.getReceipt().getReferenceCurrencyRoundFactor())
						+ (0.5 + ROUND_FACTOR))
						* this.getReceipt().getReferenceCurrencyRoundFactor();
				break;
			}
			case DEFAULT_CURRENCY:
			{
				roundedAmount = Math.floor((amount / this.getReceipt().getDefaultCurrencyRoundFactor())
						+ (0.5 + ROUND_FACTOR))
						* this.getReceipt().getDefaultCurrencyRoundFactor();
				break;
			}
			case FOREIGN_CURRENCY:
			{
				roundedAmount = Math.floor((amount / this.getForeignCurrencyRoundFactor()) + (0.5 + ROUND_FACTOR))
						* this.getForeignCurrencyRoundFactor();
				break;
			}
			case DEFAULT_FOREIGN_CURRENCY:
			{
				roundedAmount = Math.floor((amount / this.getReceipt().getForeignCurrencyRoundFactor())
						+ (0.5 + ROUND_FACTOR))
						* this.getReceipt().getForeignCurrencyRoundFactor();
				break;
			}
		}
		return roundedAmount;
	}

	public static Payment newInstance(final Receipt receipt)
	{
		final Payment payment = (Payment) AbstractEntity.newInstance(new Payment(receipt));
		return payment;
	}
}
