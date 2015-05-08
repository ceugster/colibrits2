/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

/*
 * In der Position ist kein Betrag gespeichert. Der Betrag ist je nach Anforderung zu berechnen.
 * 
 * Folgende Beträge können errechnet werden (siehe Methode <code>amount(AmountType)</code>)
 * 
 * Bruttobetrag: Der Bruttobetrag setzt sich zusammen aus <code>quantity</code>, <code>price</code> 
 * und wird wie folgt berechnet: <code>quantity * price</code>
 * 
 * Nettobetrag: Der Nettobetrag setzt sich zusammen aus <code>quantity</code>, <code>price</code> und 
 * <code>discount</code> und wird wie folgt berechnet: <code>quantity * price * (1 - discount)</code>
 * 
 * Rabattbetrag: Der Rabattbetrag setzt sich zusammen aus <code>quantity</code>, <code>price</code> und 
 * <code>discount</code> und wird wie folgt berechnet: <code>quantity * price * discount</code>
 * 
 * Mehrwertsteuerbetrag: Der Mehrwertsteuerbetrag setzt sich zusammen aus <code>quantity</code>, 
 * <code>price</code>, <code>discount</code> und <code>taxPercents</code> und wird, abhängig davon, 
 * ob die Mehrwertsteuer im Betrag enthalten ist oder nicht, wie folgt berechnet:
 * Mehrwertsteuer ist in Betrag enthalten (CommonSettings.taxInclusive == true):
 * <code>quantity * price * (1 - discount) * taxPercents / 1 + taxPercents</code>
 * Mehrwertsteuer ist nicht in Betrag enthalten (CommonSettings.taxInclusive == false):
 * <code>quantity * price * (1 - discount) * taxPercents</code>
 * 
 * Eine zusätzliche Verkomplizierung erfolgt durch die Währung, die berücksichtigt werden muss: Der abgespeicherte Preis 
 * (<code>price</code>) liegt in der gewählten Fremdwährung vor. Wenn wir den Betrag der Position in der 
 * Referenzwährung ermitteln wollen, muss er ebenfalls errechnet werden: Ausgehend von der Betragsberechnung 
 * (siehe oben), für deren Resultat im folgenden die Bezeichnung amount verwendet wird, erfolgt die Berechnung unter Berücksichtigung
 * der Werte aus <code>foreignCurrencyQuotation</code> und <code>Receipt.referenceCurrencyQuotation</code> sowie
 * <code>Receipt.referenceCurrencyRoundFactor</code> wie folgt (siehe Methoden <code>quotation(QuotationType)</code> und 
 * <code>round(double, Receipt.QuotationType)</code>:
 * <code>round((amount / foreignCurrencyQuotation * Receipt.referenceCurrencyQuotation) / Receipt.referenceCurrencyRoundFactor) * Receipt.referenceCurrencyRoundFactor</code>
 * 
 */
import java.util.Comparator;
import java.util.List;

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

import org.eclipse.persistence.annotations.Convert;
import org.eclipse.persistence.annotations.Index;

import ch.eugster.colibri.persistence.Activator;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "po_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "po_version")),
		@AttributeOverride(name = "update", column = @Column(name = "po_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "po_deleted")) })
@Table(name = "colibri_position")
public class Position extends AbstractEntity implements IPrintable, Comparator<Position>, Comparable<Position>
{
	@Id
	@Column(name = "po_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "po_id")
	@TableGenerator(name = "po_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "po_re_id", referencedColumnName = "re_id")
	private Receipt receipt;

	@ManyToOne(optional = false)
	@JoinColumn(name = "po_pg_id", referencedColumnName = "pg_id")
	private ProductGroup productGroup;

	// @ManyToOne(optional = true)
	// @JoinColumn(name = "po_epg_id", referencedColumnName = "epg_id")
	// private ExternalProductGroup externalProductGroup;

	@ManyToOne(optional = true)
	@JoinColumn(name = "po_ct_id", referencedColumnName = "ct_id")
	private CurrentTax currentTax;

	/**
	 * Diese Währung darf nicht <code>null</code> sein, da sie als
	 * Berechnungsgrundlage für den Positionsbetrag dient
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "po_cu_id", referencedColumnName = "cu_id")
	private Currency foreignCurrency;

	@OneToOne(optional = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "po_pd_id", referencedColumnName = "pd_id")
	private Product product;

	@Basic
	@Column(name = "po_fc_round_factor", columnDefinition = "DECIMAL(18, 6)")
	private double foreignCurrencyRoundFactor;

	@Basic
	@Column(name = "po_fc_quotation", columnDefinition = "DECIMAL(18, 6)")
	private double foreignCurrencyQuotation;

	@Basic
	@Column(name = "po_tax_percents", columnDefinition = "DECIMAL(18, 6)")
	private double taxPercents;

	@Basic
	@Column(name = "po_search_value")
	private String searchValue;

	@Basic
	@Column(name = "po_price", columnDefinition = "DECIMAL(18, 6)")
	private double price;

	@Basic
	@Column(name = "po_discount", columnDefinition = "DECIMAL(18, 6)")
	private double discount;

	@Basic
	@Column(name = "po_quantity")
	private int quantity;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "po_ebook")
	private boolean ebook;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "po_discount_prohibited")
	private boolean discountProhibited;

	// @Basic
	// @Column(name = "po_dc_amount", columnDefinition = "DECIMAL(18, 6)")
	// private double defaultCurrencyAmount;

	// @Basic
	// @Column(name = "po_fc_amount", columnDefinition = "DECIMAL(18, 6)")
	// private double foreignCurrencyAmount;

	@Basic
	@Column(name = "po_ordered")
	@Convert("booleanConverter")
	private boolean ordered;

	@Basic
	@Column(name = "po_order")
	private String order;

	@Basic
	@Column(name = "po_from_stock")
	@Convert("booleanConverter")
	private boolean fromStock;

	@Basic
	@Index
	@Column(name = "po_provider")
	private String provider;

	@Basic
	@Column(name = "po_provider_state")
	private int providerState = 0;

	@Basic
	@Index
	@Column(name = "po_book_provider")
	@Convert("booleanConverter")
	private boolean bookProvider;

	@Basic
	@Index
	@Column(name = "po_provider_booked")
	@Convert("booleanConverter")
	private boolean providerBooked;

	@Basic
	@Column(name = "po_option")
	@Enumerated
	private Option option;

	@Basic
	@Column(name = "po_other_id")
	private Long otherId;

	@Basic
	@Column(name = "po_server_updated", columnDefinition="SMALLINT")
	@Convert("booleanConverter")
	private boolean serverUpdated;

	@Basic
	@Column(name = "po_ordered_quantity")
	private int orderedQuantity;
	
	protected Position()
	{
		super();
	}

	protected Position(final Receipt receipt)
	{
		this();
		this.setReceipt(receipt);
	}

	public double getAmount(final Receipt.QuotationType quotationType, final AmountType amountType)
	{
		double amount = this.round(this.amount(amountType) * this.quotation(quotationType), quotationType);
		return amount;
	}

	public CurrentTax getCurrentTax()
	{
		return this.currentTax;
	}

	public double getDiscount()
	{
		return this.discount;
	}

	// public ExternalProductGroup getExternalProductGroup()
	// {
	// return this.externalProductGroup;
	// }

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

	public Option getOption()
	{
		return this.option;
	}

	public String getOrder()
	{
		return this.order;
	}

	public double getPrice()
	{
		return this.price;
	}

	public Product getProduct()
	{
		return this.product;
	}

	public ProductGroup getProductGroup()
	{
		return this.productGroup;
	}

	public String getProvider()
	{
		return this.provider;
	}

	public int getQuantity()
	{
		return this.quantity;
	}

	public Receipt getReceipt()
	{
		return this.receipt;
	}

	public String getSearchValue()
	{
		return this.searchValue;
	}

	public double getTaxAmount(final Receipt.QuotationType quotationType)
	{
		return this.round(this.getAmount(quotationType, AmountType.NETTO) * this.taxFactor(),
				this.receipt.getDefaultCurrency());
	}

	public double getTaxPercents()
	{
		return this.taxPercents;
	}

	public boolean isBookProvider()
	{
		return this.bookProvider;
	}

	public boolean isFromStock()
	{
		return this.fromStock;
	}

	public boolean isEbook()
	{
		return ebook;
	}
	
	public boolean isOrdered()
	{
		return this.ordered;
	}

	public boolean isProviderBooked()
	{
		return this.providerBooked;
	}

	public void setBookProvider(final boolean bookProvider)
	{
		this.bookProvider = bookProvider;
	}

	public void setCurrentTax(final CurrentTax currentTax)
	{
		this.propertyChangeSupport.firePropertyChange("currentTax", this.currentTax, this.currentTax = currentTax);
		this.setTaxPercents(currentTax == null ? 0d : this.getCurrentTax().getPercentage());
	}

	public boolean applyDiscount()
	{
		if (this.isDiscountProhibited())
		{
			this.propertyChangeSupport.firePropertyChange("discount", this.discount, this.discount = 0D);
			return false;
		}
		return this.getQuantity() > 0 && (this.getProductGroup() == null || this.getProductGroup().getProductGroupType().equals(ProductGroupType.SALES_RELATED));
	}
	
	public void setDiscount(double discount)
	{
		if (this.applyDiscount())
		{
			discount = -Math.abs(discount);
			this.propertyChangeSupport.firePropertyChange("discount", this.discount, this.discount = discount);
		}
	}

	// public void setExternalProductGroup(final ExternalProductGroup
	// externalProductGroup)
	// {
	// this.propertyChangeSupport.firePropertyChange("externalProductGroup",
	// this.externalProductGroup,
	// this.externalProductGroup = externalProductGroup);
	//
	// if (this.externalProductGroup != null)
	// {
	// if ((externalProductGroup.getProductGroupMapping() != null) &&
	// !externalProductGroup.getProductGroupMapping().isDeleted())
	// {
	// final ProductGroup productGroup =
	// this.externalProductGroup.getProductGroupMapping().getProductGroup();
	// if ((productGroup != null) && !productGroup.isDeleted())
	// {
	// this.setProductGroup(productGroup);
	// }
	// }
	// }
	// if (this.getProductGroup() == null)
	// {
	// this.setProductGroup(this.getReceipt().getSettlement().getSalespoint().getCommonSettings().getDefaultProductGroup());
	// }
	// }

	public void setForeignCurrency(final Currency foreignCurrency)
	{
		this.propertyChangeSupport.firePropertyChange("foreignCurrency", this.foreignCurrency,
				this.foreignCurrency = foreignCurrency);
		this.setForeignCurrencyQuotation(this.foreignCurrency.getQuotation());
		this.setForeignCurrencyRoundFactor(this.foreignCurrency.getRoundFactor());
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

	public void setFromStock(final boolean fromStock)
	{
		this.propertyChangeSupport.firePropertyChange("fromStock", this.fromStock, this.fromStock = fromStock);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setOption(final Option option)
	{
		this.propertyChangeSupport.firePropertyChange("option", this.option, this.option = option);
	}

	public void setOrder(final String order)
	{
		this.propertyChangeSupport.firePropertyChange("order", this.order, this.order = order);
	}

	public void setEbook(boolean ebook)
	{
		this.ebook = ebook;
	}
	
	public void setOrdered(final boolean ordered)
	{
		this.propertyChangeSupport.firePropertyChange("ordered", this.ordered, this.ordered = ordered);
		if (this.ordered)
		{
			this.setOption(Option.ORDERED);
		}
	}

	public void setPrice(double price)
	{
		Activator.getDefault().log("Enter Position.setPrice()");
		if (this.getProductGroup() != null)
		{
			price = this.getProductGroup().getProductGroupType().computePrice(price);
		}
		this.propertyChangeSupport.firePropertyChange("price", this.price, this.price = price);
		Activator.getDefault().log("Exit Position.setPrice()");
	}

	public void setProduct(final Product product)
	{
		this.propertyChangeSupport.firePropertyChange("product", this.product, this.product = product);
	}

	private void setBookProvider()
	{
		ProductGroup payedInvoice = this.getReceipt().getSettlement().getSalespoint().getCommonSettings()
				.getPayedInvoice();
		ProductGroup productGroup = this.getProductGroup();
		if (productGroup != null)
		{
			if (payedInvoice != null)
			{
				setBookProvider(payedInvoice.getId().equals(productGroup.getId()));
			}
			if (!isBookProvider())
			{
				setBookProvider(productGroup.getProductGroupType().equals(ProductGroupType.SALES_RELATED));
			}
		}
	}

	public void setProductGroup(final ProductGroup productGroup)
	{
		Activator.getDefault().log("Enter Position.setProductGroup()");
		this.propertyChangeSupport.firePropertyChange("productGroup", this.productGroup,
				this.productGroup = productGroup);
		if (this.productGroup == null)
		{
			this.setPrice(this.receipt.getSettlement().getSalespoint().getProposalPrice());
			this.setQuantity(this.receipt.getSettlement().getSalespoint().getProposalQuantity());
		}
		else
		{
			if (this.product != null && this.product.getExternalProductGroup() == null)
			{
				List<ProductGroupMapping> mappings = this.productGroup.getProductGroupMappings(this.getProvider());
				if (!mappings.isEmpty())
				{
					this.product.setExternalProductGroup(mappings.get(0).getExternalProductGroup());
				}
			}
			
			final Tax tax = productGroup.getDefaultTax();
			if ((this.getCurrentTax() == null) || !this.getCurrentTax().getId().equals(tax.getCurrentTax().getId()))
			{
				this.setCurrentTax(tax == null ? null : tax.getCurrentTax());
			}
			if ((this.getOption() == null))
			{
				this.setOption(productGroup.getProposalOption());
			}
			else if (this.isOrdered())
			{
				this.setOption(Option.ORDERED);
			}
			else
			{
				final Option[] options = productGroup.getProductGroupType().getOptions();
				boolean found = false;
				for (final Option option : options)
				{
					if (this.getOption().equals(option))
					{
						found = true;
						break;
					}
				}
				if (!found)
				{
					this.setOption(productGroup.getProposalOption());
				}
			}
			if (this.getPrice() == this.receipt.getSettlement().getSalespoint().getProposalPrice())
			{
				this.setPrice(productGroup.getPriceProposal());
			}

			if (this.getQuantity() == this.receipt.getSettlement().getSalespoint().getProposalQuantity())
			{
				if (this.getQuantity() != productGroup.getQuantityProposal())
				{
					this.setQuantity(productGroup.getQuantityProposal());
				}
			}
		}
		setBookProvider();
		Activator.getDefault().log("Exit Position.setProductGroup()");
	}

	public void setProvider(final String provider)
	{
		this.provider = provider;
	}

	public void setProviderBooked(final boolean providerBooked)
	{
		this.providerBooked = providerBooked;
	}

	public void setQuantity(int quantity)
	{
		this.propertyChangeSupport.firePropertyChange("quantity", this.quantity, this.quantity = quantity);
	}

	public void setReceipt(final Receipt receipt)
	{
		this.propertyChangeSupport.firePropertyChange("receipt", this.receipt, this.receipt = receipt);
		this.setForeignCurrency(receipt.getDefaultCurrency());
	}

	public void setSearchValue(final String searchValue)
	{
		this.propertyChangeSupport.firePropertyChange("searchValue", this.searchValue, this.searchValue = searchValue);
	}

	public void setTaxPercents(final double percents)
	{
		this.propertyChangeSupport.firePropertyChange("taxPercents", this.taxPercents, this.taxPercents = percents);
	}

	protected double taxFactor()
	{
		if (this.getReceipt().getSettlement().getSalespoint().getCommonSettings().isTaxInclusive())
		{
			return this.getTaxPercents() / (1 + this.getTaxPercents());
		}
		else
		{
			return this.getTaxPercents();
		}
	}

	private double amount(final AmountType amountType)
	{
		double amount = 0D;
		switch (amountType)
		{
			case BRUTTO:
			{
				amount = this.getQuantity() * this.getPrice();
				break;
			}
			case NETTO:
			{
				amount = this.getQuantity() * this.getPrice() * (1 + (this.getDiscount()));
				break;
			}
			case DISCOUNT:
			{
				amount = this.getQuantity() * this.getPrice() * this.getDiscount();
				break;
			}
			default:
				throw new RuntimeException("Invalid quotation type");
		}
		return amount;
	}

	private double quotation(final Receipt.QuotationType quotationType)
	{
		double quotation = 1D;
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
				quotation = this.getForeignCurrencyQuotation() / this.getForeignCurrencyQuotation();
				break;
			}
			case DEFAULT_FOREIGN_CURRENCY:
			{
				quotation = this.getForeignCurrencyQuotation() / this.getReceipt().getForeignCurrencyQuotation();
				break;
			}
			default:
				throw new RuntimeException("Invalid quotation type");
		}
		return quotation;
	}

	private double round(final double amount, final Currency currency)
	{
		final int digits = currency.getCurrency().getDefaultFractionDigits();
		final double value = Math.floor(amount * Math.pow(10D, digits) + (0.5 + ROUND_FACTOR)) / Math.pow(10D, digits);
		return value;
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
				roundedAmount = Math.floor((amount / this.getReceipt().getReferenceCurrencyRoundFactor())
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
			default:
				throw new RuntimeException("Invalid quotation type");
		}
		return roundedAmount;
	}

	public static Position newInstance(final Receipt receipt)
	{
		final Position position = (Position) AbstractEntity.newInstance(new Position(receipt));
		position.setQuantity(receipt.getSettlement().getSalespoint().getProposalQuantity());
		position.setPrice(receipt.getSettlement().getSalespoint().getProposalPrice());
		if (receipt.getSettlement().getSalespoint().getProposalTax() != null)
		{
			position.setCurrentTax(receipt.getSettlement().getSalespoint().getProposalTax().getCurrentTax());
		}
		return position;
	}

	public static Position reinitialize(final Position position)
	{
		position.setProduct(null);
		position.setForeignCurrency(position.getReceipt().getDefaultCurrency());
		final Tax tax = position.getReceipt().getSettlement().getSalespoint().getProposalTax();
		position.setCurrentTax(tax == null ? null : tax.getCurrentTax());
		position.setCurrentTax(null);
		position.setDiscount(0d);
		position.setOrdered(false);
		position.setPrice(position.getReceipt().getSettlement().getSalespoint().getProposalPrice());
		position.setProductGroup(null);
		position.setQuantity(position.getReceipt().getSettlement().getSalespoint().getProposalQuantity());
		position.setSearchValue(null);
		return position;
	}

	public String getCode()
	{
		if (this.getProduct() == null)
		{
			return this.getSearchValue();
		}
		else
		{
			return this.getProduct().getCode();
		}
	}
	
	public void setOtherId(Long otherId)
	{
		this.otherId = otherId;
	}

	public Long getOtherId()
	{
		return otherId;
	}

	public int getProviderState() 
	{
		return providerState;
	}

	public void setProviderState(int providerState) 
	{
		this.propertyChangeSupport.firePropertyChange("providerState", this.providerState, this.providerState = providerState);
	}

	public static double getAmount(double amount, double quotation1, double quotation2, final double roundFactor)
	{
		return Math.floor((amount * quotation1 / quotation2 / roundFactor) + (0.5 + ROUND_FACTOR)) * roundFactor;
	}

	// public void setDefaultCurrencyAmount(double defaultCurrencyAmount)
	// {
	// this.defaultCurrencyAmount = defaultCurrencyAmount;
	// }

	// public double getDefaultCurrencyAmount()
	// {
	// return defaultCurrencyAmount;
	// }

	// public void setForeignCurrencyAmount(double foreignCurrencyAmount)
	// {
	// this.foreignCurrencyAmount = foreignCurrencyAmount;
	// }

	// public double getForeignCurrencyAmount()
	// {
	// return foreignCurrencyAmount;
	// }

	public boolean isServerUpdated() {
		return serverUpdated;
	}

	public void setServerUpdated(boolean serverUpdated) {
		this.serverUpdated = serverUpdated;
	}

	public int getOrderedQuantity() 
	{
		return orderedQuantity == 0 ? getQuantity() : orderedQuantity;
	}

	public void setOrderedQuantity(int orderedQuantity) 
	{
		this.propertyChangeSupport.firePropertyChange("orderedQuantity", this.orderedQuantity, this.orderedQuantity = orderedQuantity);
	}

	public boolean isDiscountProhibited() 
	{
		return discountProhibited;
	}

	public void setDiscountProhibited(boolean discountProhibited) 
	{
		this.propertyChangeSupport.firePropertyChange("discountProhibited", this.discountProhibited, this.discountProhibited = discountProhibited);
	}

	public enum AmountType
	{
		BRUTTO, NETTO, DISCOUNT;
	}

	public enum Option
	{
		ARTICLE, ORDERED, PAYED_INVOICE, NONE;

		public String toCode()
		{
			switch (this)
			{
				case ARTICLE:
				{
					return "L";
				}
				case ORDERED:
				{
					return "B";
				}
				case PAYED_INVOICE:
				{
					return "Q";
				}
				case NONE:
				{
					return "";
				}
				default:
					throw new RuntimeException("Invalid option");
			}
		}
		
		public static Option[] getValidValues()
		{
			return new Option[] { ARTICLE, ORDERED, PAYED_INVOICE };
		}

		@Override
		public String toString()
		{
			switch (this)
			{
				case ARTICLE:
				{
					return "Lagerverkauf";
				}
				case ORDERED:
				{
					return "Kundenbestellung";
				}
				case PAYED_INVOICE:
				{
					return "Bezahlte Rechnung";
				}
				case NONE:
				{
					return "";
				}
				default:
				{
					throw new RuntimeException("Invalid option");
				}
			}
		}
	}

	@Override
	public int compareTo(Position other) 
	{
		return other.getTimestamp().compareTo(this.getTimestamp());
	}

	@Override
	public int compare(Position p1, Position p2) 
	{
		return p2.getTimestamp().compareTo(p1.getTimestamp());
	}
}
