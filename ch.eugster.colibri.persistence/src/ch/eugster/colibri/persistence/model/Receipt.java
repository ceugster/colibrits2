package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Transient;

import org.eclipse.persistence.annotations.Convert;

import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.Customer;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "re_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "re_version")),
		@AttributeOverride(name = "update", column = @Column(name = "re_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "re_deleted")) })
@Table(name = "colibri_receipt")
public class Receipt extends AbstractEntity implements IPrintable
{
	public static final String ATTRIBUTE_NAME_ID = "id";

	public static final String ATTRIBUTE_NAME_SETTLEMENT = "settlement";

	public static final String ATTRIBUTE_NAME_NUMBER = "number";

	public static final String ATTRIBUTE_NAME_TRANSFERRED = "transferred";

	public static final String ATTRIBUTE_NAME_TRANSACTION = "transaction";

	public static final String ATTRIBUTE_NAME_REFERENCE_CURRENCY = "referenceCurrency";

	public static final String ATTRIBUTE_NAME_REFERENCE_CURRENCY_ROUND_FACTOR = "referenceCurrencyRoundFactor";

	public static final String ATTRIBUTE_NAME_DEFAULT_CURRENCY = "defaultCurrency";

	public static final String ATTRIBUTE_NAME_DEFAULT_CURRENCY_QUOTATION = "defaultCurrencyQuotation";

	public static final String ATTRIBUTE_NAME_DEFAULT_CURRENCY_ROUND_FACTOR = "defaultCurrencyRoundFactor";

	public static final String ATTRIBUTE_NAME_FOREIGN_CURRENCY = "foreignCurrency";

	public static final String ATTRIBUTE_NAME_FOREIGN_CURRENCY_QUOTATION = "foreignCurrencyQuotation";

	public static final String ATTRIBUTE_NAME_FOREIGN_CURRENCY_ROUND_FACTOR = "foreignCurrencyRoundFactor";

	public static final String ATTRIBUTE_NAME_BOOKKEEPING_TRANSACTION = "bookkeepingTransaction";

	public static final String ATTRIBUTE_NAME_STATE = "state";

	public static final String ATTRIBUTE_NAME_CUSTOMER_CODE = "customerCode";

	public static final String ATTRIBUTE_NAME_USER = "user";

	public static final String ATTRIBUTE_NAME_POSITIONS = "positions";

	public static final String ATTRIBUTE_NAME_PAYMENTS = "payments";

	@Id
	@Column(name = "re_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "re_id")
	@TableGenerator(allocationSize = 2, name = "re_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(cascade = CascadeType.PERSIST, optional = false)
	@JoinColumn(name = "re_se_id", referencedColumnName = "se_id")
	private Settlement settlement;

	@OneToOne(optional = false)
	@JoinColumn(name = "re_reference_cu_id", referencedColumnName = "cu_id")
	private Currency referenceCurrency;

	@OneToOne(optional = false)
	@JoinColumn(name = "re_default_cu_id", referencedColumnName = "cu_id")
	private Currency defaultCurrency;

	@OneToOne(optional = false)
	@JoinColumn(name = "re_foreign_cu_id", referencedColumnName = "cu_id")
	private Currency foreignCurrency;

	@Basic
	@Column(name = "re_number")
	private Long number;

	@Basic
	@Column(name = "re_other_id")
	private Long otherId;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "re_transferred")
	private boolean transferred;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "re_provider_updated")
	private boolean providerUpdated;

	@Basic
	@Column(name = "re_transaction")
	private long transaction;

	@Basic
	@Column(name = "re_rc_quotation")
	private double referenceCurrencyQuotation;

	@Basic
	@Column(name = "re_rc_round_factor", columnDefinition = "DECIMAL(18, 6)")
	private double referenceCurrencyRoundFactor;

	@Basic
	@Column(name = "re_dc_quotation", columnDefinition = "DECIMAL(18, 6)")
	private double defaultCurrencyQuotation;

	@Basic
	@Column(name = "re_dc_round_factor", columnDefinition = "DECIMAL(18, 6)")
	private double defaultCurrencyRoundFactor;

	@Basic
	@Column(name = "re_fc_quotation", columnDefinition = "DECIMAL(18, 6)")
	private double foreignCurrencyQuotation;

	@Basic
	@Column(name = "re_fc_round_factor", columnDefinition = "DECIMAL(18, 6)")
	private double foreignCurrencyRoundFactor;

	@Basic
	@Column(name = "re_bookkeeping_trx")
	private long bookkeepingTransaction;

	@Basic
	@Column(name = "re_state")
	@Enumerated
	private State state;

	@Basic
	@Column(name = "re_customer_code")
	private String customerCode;

	@Transient
	private Customer customer;

	@ManyToOne(optional = false)
	@JoinColumn(name = "re_us_id", referencedColumnName = "us_id")
	private User user;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "receipt")
	private Collection<Position> positions = new Vector<Position>();

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "receipt")
	private Collection<Payment> payments = new Vector<Payment>();

	private Receipt()
	{
		super();
	}

	private Receipt(final Settlement settlement)
	{
		this();
		this.setSettlement(settlement);
	}

	private Receipt(final Settlement settlement, final User user)
	{
		this(settlement);
		this.setUser(user);
	}

	public void addPayment(final Payment payment)
	{
		this.propertyChangeSupport.firePropertyChange("payments", this.payments, this.payments.add(payment));
	}

	public void addPosition(final Position position)
	{
		this.propertyChangeSupport.firePropertyChange("positions", this.positions, this.positions.add(position));
	}

	public void clearPayments()
	{
		final Payment[] payments = this.payments.toArray(new Payment[0]);
		for (final Payment payment : payments)
		{
			this.removePayment(payment);
		}
	}

	public Collection<Payment> getAllPayments()
	{
		return this.payments;
	}

	public Collection<Position> getAllPositions()
	{
		return this.positions;
	}

	public long getBookkeepingTransaction()
	{
		return this.bookkeepingTransaction;
	}

	public Customer getCustomer()
	{
		return this.customer;
	}

	public String getCustomerCode()
	{
		return this.valueOf(this.customerCode);
	}

	public SalespointCustomerDisplaySettings getCustomerDisplay()
	{
		return this.settlement.getCustomerDisplay();
	}

	public SalespointCustomerDisplaySettings getCustomerDisplayPeriphery()
	{
		return this.getSettlement().getSalespoint().getCustomerDisplaySettings();
	}

	public Currency getDefaultCurrency()
	{
		return this.defaultCurrency;
	}

	public double getDefaultCurrencyQuotation()
	{
		return this.defaultCurrencyQuotation;
	}

	public double getDefaultCurrencyRoundFactor()
	{
		return this.defaultCurrencyRoundFactor;
	}

	public Collection<Payment> getDeletedPayments()
	{
		final Collection<Payment> payments = new Vector<Payment>();
		for (final Payment payment : this.payments)
		{
			if (payment.isDeleted())
			{
				payments.add(payment);
			}
		}

		return payments;
	}

	public double getDifference()
	{
		final double positionAmount = this.getPositionDefaultForeignCurrencyAmount(AmountType.NETTO);
		final double paymentAmount = this.getPaymentDefaultForeignCurrencyAmount();
		double difference = Math.abs(positionAmount - paymentAmount);
		return difference < .000001 ? 0D : difference;
	}

	public double getFCDifference()
	{
		final double positionAmount = this.getPositionAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO);
		final double paymentAmount = this.getPaymentAmount(Receipt.QuotationType.FOREIGN_CURRENCY);
		double difference = Math.abs(positionAmount - paymentAmount);
		return difference < .000001 ? 0D : difference;
	}

	public Currency getForeignCurrency()
	{
		return this.foreignCurrency;
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

	public Collection<Payment> getPayments()
	{
		return this.payments;
	}

	public Collection<Position> getPositions()
	{
		return positions;
	}

	public Long getNumber()
	{
		return this.number;
	}

	public Position[] getPayedInvoices()
	{
		final Collection<Position> payedInvoices = new ArrayList<Position>();
		final Position[] positions = this.getPositions().toArray(new Position[0]);
		for (final Position position : positions)
		{
			if (!position.isDeleted() && position.getOption().equals(Position.Option.PAYED_INVOICE))
			{
				payedInvoices.add(position);
			}
		}
		return payedInvoices.toArray(new Position[0]);
	}

	public double getPaymentAmount(final Receipt.QuotationType quotationType)
	{
		double amount = 0d;
		final Collection<Payment> payments = this.getPayments();
		for (final Payment payment : payments)
		{
			if (!payment.isDeleted())
			{
				amount += payment.getAmount(quotationType);
			}
		}
		return amount;
	}

	public double getPaymentBackAmount(final Receipt.QuotationType quotationType)
	{
		double amount = 0d;
		final Collection<Payment> payments = this.getPayments();
		for (final Payment payment : payments)
		{
			if (!payment.isDeleted() && payment.isBack())
			{
				amount += payment.getAmount(quotationType);
			}
		}
		return amount;
	}

	public double getPaymentDefaultCurrencyAmount()
	{
		return this.getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
	}

	public double getPaymentDefaultCurrencyBackAmount()
	{
		return this.getPaymentBackAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
	}

	public double getPaymentDefaultForeignCurrencyAmount()
	{
		return this.getPaymentAmount(Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY);
	}

	public double getPaymentDefaultForeignCurrencyBackAmount()
	{
		return this.getPaymentBackAmount(Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY);
	}

	public double getPaymentReferenceCurrencyAmount()
	{
		return this.getPaymentAmount(Receipt.QuotationType.REFERENCE_CURRENCY);
	}

	public double getPaymentReferenceCurrencyBackAmount()
	{
		return this.getPaymentBackAmount(Receipt.QuotationType.REFERENCE_CURRENCY);
	}

	public double getPositionAmount(final Receipt.QuotationType quotationType, final AmountType amountType)
	{
		double amount = 0d;
		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			if (!position.isDeleted())
			{
				amount += position.getAmount(quotationType, amountType);
			}
		}
		return amount;
	}

	public double getPositionAmount(final Receipt.QuotationType quotationType, final AmountType amountType,
			final Option[] options)
	{
		double amount = 0d;
		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			for (final Option option : options)
			{
				if (!position.isDeleted() && position.getOption().equals(option))
				{
					amount += position.getAmount(quotationType, amountType);
				}
			}
		}
		return amount;
	}

	public double getPositionDefaultCurrencyAmount(final AmountType amountType)
	{
		return this.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY, amountType);
	}

	public double getPositionDefaultForeignCurrencyAmount(final AmountType amountType)
	{
		return this.getPositionAmount(Receipt.QuotationType.DEFAULT_FOREIGN_CURRENCY, amountType);
	}

	public int getPositionDiscountQuantity()
	{
		int quantity = 0;
		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			if (!position.isDeleted() && (position.getDiscount() != 0d))
			{
				quantity += position.getQuantity();
			}
		}
		return quantity;
	}

	public int getPositionQuantity(final Option[] options)
	{
		int quantity = 0;
		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			for (final Option option : options)
			{
				if (position.getOption() == option)
				{
					quantity += position.getQuantity();
				}
			}
		}
		return quantity;
	}

	public double getPositionReferenceCurrencyAmount(final AmountType amountType)
	{
		return this.getPositionAmount(Receipt.QuotationType.REFERENCE_CURRENCY, amountType);
	}

	public Collection<Position> getPositions(final CurrentTax currentTax)
	{
		final Collection<Position> selected = new ArrayList<Position>();
		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			if (position.getCurrentTax().getId().equals(currentTax.getId()))
			{
				selected.add(position);
			}
		}
		return selected;
	}

	public double getPositionsBrutAmount(final CurrentTax currentTax)
	{
		double brutAmount = 0D;

		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			if (position.getCurrentTax().getId().equals(currentTax.getId()))
			{
				brutAmount += position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO);
			}
		}
		return brutAmount;
	}

	public double getPositionsTaxAmount()
	{
		double taxAmount = 0D;
		final Collection<CurrentTax> taxes = this.getTaxes();
		for (final CurrentTax tax : taxes)
		{
			taxAmount += this.getPositionsTaxAmount(tax);
		}
		return taxAmount;
	}

	public double getPositionsTaxAmount(final CurrentTax currentTax)
	{
		double taxAmount = 0D;

		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			if (position.getCurrentTax().getId().equals(currentTax.getId()))
			{
				taxAmount += position.getTaxAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
			}
		}
		return taxAmount;
	}

	public int getPositionTotalQuantity()
	{
		int quantity = 0;
		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			quantity += position.getQuantity();
		}
		return quantity;
	}

	public int getPositionWithDiscountQuantity()
	{
		int quantity = 0;
		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			if (position.getDiscount() != 0D)
			{
				quantity += position.getQuantity();
			}
		}
		return quantity;
	}

	public SalespointReceiptPrinterSettings getReceiptPrinter()
	{
		return this.settlement.getReceiptPrinter();
	}

	public Currency getReferenceCurrency()
	{
		return this.referenceCurrency;
	}

	public double getReferenceCurrencyQuotation()
	{
		return this.referenceCurrencyQuotation;
	}

	public double getReferenceCurrencyRoundFactor()
	{
		return this.referenceCurrencyRoundFactor;
	}

	public Settlement getSettlement()
	{
		return this.settlement;
	}

	public State getState()
	{
		return this.state;
	}

	public Collection<CurrentTax> getTaxes()
	{
		final Collection<CurrentTax> taxes = new ArrayList<CurrentTax>();
		final Collection<Position> positions = this.getPositions();
		for (final Position position : positions)
		{
			if (!taxes.contains(position.getCurrentTax()))
			{
				taxes.add(position.getCurrentTax());
			}
		}
		return taxes;
	}

	public long getTransaction()
	{
		return this.transaction;
	}

	public User getUser()
	{
		return this.user;
	}

	public boolean hasVoucherPayment()
	{
		for (final Payment payment : this.getPayments())
		{
			if (payment.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasVoucherBack()
	{
		for (final Payment payment : this.getPayments())
		{
			if (payment.isBack() && payment.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
			{
				return true;
			}
		}
		return false;
	}

	public boolean hasRestitution()
	{
		for (final Position position : this.getPositions())
		{
			if (position.getQuantity() < 0)
			{
				return true;
			}
		}
		return false;
	}

	public boolean isProviderUpdated()
	{
		return this.providerUpdated;
	}

	public boolean isTransferred()
	{
		return this.transferred;
	}

	public void removePayment(final Payment payment)
	{
		this.propertyChangeSupport.firePropertyChange("payments", this.payments, this.payments.remove(payment));
	}

	public void removePosition(final Position position)
	{
		this.propertyChangeSupport.firePropertyChange("positions", this.positions, this.positions.remove(position));
	}

	public void setBookkeepingTransaction(final long bookkeepingTransaction)
	{
		this.propertyChangeSupport.firePropertyChange("bookkeepingTransaction", this.bookkeepingTransaction,
				this.bookkeepingTransaction = bookkeepingTransaction);
	}

	public void setCustomer(final Customer customer)
	{
		this.propertyChangeSupport.firePropertyChange("customer", this.customer, this.customer = customer);
		this.customerCode = customer == null ? null : customer.getId().toString();
	}

	public void setCustomerCode(final String customerCode)
	{
		this.propertyChangeSupport.firePropertyChange("customerCode", this.customerCode,
				this.customerCode = customerCode);
	}

	public void setDefaultCurrency(final Currency currency)
	{
		this.propertyChangeSupport.firePropertyChange("defaultCurrency", this.defaultCurrency,
				this.defaultCurrency = currency);
		this.setDefaultCurrencyQuotation(this.getDefaultCurrency().getQuotation());
		this.setDefaultCurrencyRoundFactor(this.getDefaultCurrency().getRoundFactor());
		if (this.foreignCurrency == null)
		{
			this.setForeignCurrency(currency);
		}
	}

	public void setDefaultCurrencyQuotation(final double defaultCurrencyQuotation)
	{
		this.propertyChangeSupport.firePropertyChange("defaultCurrencyQuotation", this.defaultCurrencyQuotation,
				this.defaultCurrencyQuotation = defaultCurrencyQuotation);
	}

	public void setDefaultCurrencyRoundFactor(final double defaultCurrencyRoundFactor)
	{
		this.propertyChangeSupport.firePropertyChange("defaultCurrencyRoundFactor", this.defaultCurrencyRoundFactor,
				this.defaultCurrencyRoundFactor = defaultCurrencyRoundFactor);
	}

	@Override
	public void setDeleted(final boolean deleted)
	{
		for (final Position position : this.getPositions())
		{
			if (position.isDeleted() != deleted)
			{
				position.setDeleted(deleted);
			}
		}
		for (final Payment payment : this.getPayments())
		{
			if (payment.isDeleted() != deleted)
			{
				payment.setDeleted(deleted);
			}
		}
		if (!this.isDeleted())
		{
			super.setDeleted(deleted);
		}
	}

	public void setForeignCurrency(final Currency foreignCurrency)
	{
		if (foreignCurrency != null)
		{
			if (this.foreignCurrency == null)
			{
				this.propertyChangeSupport.firePropertyChange("defaultForeignCurrency", this.foreignCurrency,
						this.foreignCurrency = foreignCurrency);
				this.setForeignCurrencyQuotation(this.getForeignCurrency().getQuotation());
				this.setForeignCurrencyRoundFactor(this.getForeignCurrency().getRoundFactor());
			}
			else if (!this.foreignCurrency.getId().equals(foreignCurrency.getId()))
			{
				if (!foreignCurrency.getId().equals(
						this.settlement.getSalespoint().getPaymentType().getCurrency().getId()))
				{
					for (final Stock stock : this.settlement.getSalespoint().getStocks())
					{
						if (stock.getPaymentType().getCurrency().getId().equals(foreignCurrency.getId()))
						{
							this.propertyChangeSupport.firePropertyChange("defaultForeignCurrency",
									this.foreignCurrency, this.foreignCurrency = foreignCurrency);
							this.setForeignCurrencyQuotation(this.getForeignCurrency().getQuotation());
							this.setForeignCurrencyRoundFactor(this.getForeignCurrency().getRoundFactor());
						}
					}
				}
			}
		}
	}

	public void setForeignCurrencyQuotation(final double defaultForeignCurrencyQuotation)
	{
		this.propertyChangeSupport.firePropertyChange("foreignCurrencyQuotation", this.foreignCurrencyQuotation,
				this.foreignCurrencyQuotation = defaultForeignCurrencyQuotation);
	}

	public void setForeignCurrencyRoundFactor(final double foreignCurrencyRoundFactor)
	{
		this.propertyChangeSupport.firePropertyChange("foreignCurrencyRoundFactor", this.foreignCurrencyRoundFactor,
				this.foreignCurrencyRoundFactor = foreignCurrencyRoundFactor);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setNumber(final Long number)
	{
		this.propertyChangeSupport.firePropertyChange("number", this.number, this.number = number);
	}

	public void setPayments(final Collection<Payment> payments)
	{
		this.propertyChangeSupport.firePropertyChange("payments", this.payments, this.payments = payments);
	}

	public void setPositions(final Collection<Position> positions)
	{
		this.propertyChangeSupport.firePropertyChange("positions", this.positions, this.positions = positions);
	}

	public void setProviderUpdated(final boolean providerUpdated)
	{
		this.propertyChangeSupport.firePropertyChange("providerUpdated", this.providerUpdated,
				this.providerUpdated = providerUpdated);
	}

	public void setReferenceCurrency(final Currency currency)
	{
		this.propertyChangeSupport.firePropertyChange("referenceCurrency", this.referenceCurrency,
				this.referenceCurrency = currency);
		this.setReferenceCurrencyQuotation(this.getReferenceCurrency().getQuotation());
		this.setReferenceCurrencyRoundFactor(this.getReferenceCurrency().getRoundFactor());
	}

	public void setReferenceCurrencyQuotation(final double referenceCurrencyQuotation)
	{
		this.propertyChangeSupport.firePropertyChange("referenceCurrencyQuotation", this.referenceCurrencyQuotation,
				this.referenceCurrencyQuotation = referenceCurrencyQuotation);
	}

	public void setReferenceCurrencyRoundFactor(final double referenceCurrencyRoundFactor)
	{
		this.propertyChangeSupport.firePropertyChange("referenceCurrencyRoundFactor",
				this.referenceCurrencyRoundFactor, this.referenceCurrencyRoundFactor = referenceCurrencyRoundFactor);
	}

	public void setSettlement(final Settlement settlement)
	{
		this.propertyChangeSupport.firePropertyChange("settlement", this.settlement, this.settlement = settlement);
		this.setDefaultCurrency(settlement.getSalespoint().getPaymentType().getCurrency());
	}

	public void setState(final State state)
	{
		if ((this.state == null) || !this.state.equals(state))
		{
			if (state.equals(State.REVERSED) || state.equals(State.SAVED))
			{
				this.setProviderUpdated(false);
			}
			// else if (state.equals(State.CLOSED))
			// {
			// setTransferred(false);
			// }
			this.propertyChangeSupport.firePropertyChange("state", this.state, this.state = state);
		}
	}

	public void setTransaction(final long transaction)
	{
		this.propertyChangeSupport.firePropertyChange("transaction", this.transaction, this.transaction = transaction);
	}

	public void setTransferred(final boolean transferred)
	{
		this.propertyChangeSupport.firePropertyChange("transferred", this.transferred, this.transferred = transferred);
	}

	public void setUser(final User user)
	{
		this.propertyChangeSupport.firePropertyChange("user", this.user, this.user = user);
	}

	public static Receipt newInstance(final Settlement settlement, final User user)
	{
		final Receipt receipt = (Receipt) AbstractEntity.newInstance(new Receipt(settlement));
		receipt.setReferenceCurrency(settlement.getSalespoint().getCommonSettings().getReferenceCurrency());
		receipt.setDefaultCurrency(receipt.getSettlement().getSalespoint().getPaymentType().getCurrency());
		receipt.setBookkeepingTransaction(0l);
		receipt.setState(Receipt.State.NEW);
		receipt.setTransaction(0l);
		receipt.setUser(user);
		return receipt;
	}

	public void setOtherId(Long otherId)
	{
		this.otherId = otherId;
	}

	public Long getOtherId()
	{
		return otherId;
	}

	/*
	 * Legt fest, für welche Währung der Betrag einer Position oder Zahlung
	 * berechnet werden soll:
	 * 
	 * <code>REFERENCE_CURRENCY</code>: Zielbetrag in Referenzwährung
	 * §code>DEFAULT_CURRENCY</code>
	 */
	public enum QuotationType
	{
		REFERENCE_CURRENCY, DEFAULT_CURRENCY, FOREIGN_CURRENCY, DEFAULT_FOREIGN_CURRENCY;
	}

	public enum State
	{
		/*
		 * NEW: initialisierter Beleg ohne Zahlungen und Positionen
		 * 
		 * PARKED: parkierter Beleg
		 * 
		 * REVERSED: gespeicherter Beleg, der storniert worden ist
		 * 
		 * SAVED: gespeicherter Beleg, der nicht transferriert worden ist
		 */
		NEW, PARKED, REVERSED, SAVED;

		public String code()
		{
			switch (this)
			{
				case NEW:
				{
					return "N";
				}
				case PARKED:
				{
					return "P";
				}
				case REVERSED:
				{
					return "S";
				}
				case SAVED:
				{
					return "";
				}
				default:
					throw new RuntimeException("Ungültiger Belegstatus");
			}
		}

		@Override
		public String toString()
		{
			switch (this)
			{
				case NEW:
				{
					return "Neu";
				}
				case PARKED:
				{
					return "Parkiert";
				}
				case REVERSED:
				{
					return "Storniert";
				}
				case SAVED:
				{
					return "Gespeichert";
				}
				default:
					throw new RuntimeException("Ungültiger Belegstatus");
			}
		}

		public String compatibleState()
		{
			switch (this)
			{
				case NEW:
				{
					return "1";
				}
				case PARKED:
				{
					return "2";
				}
				case REVERSED:
				{
					return "3";
				}
				case SAVED:
				{
					return "4";
				}
				default:
					throw new RuntimeException("Ungültiger Belegstatus");
			}
		}
	}

}
