/*
 * Created on 01.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.service.SettlementService;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "se_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "se_version")),
		@AttributeOverride(name = "update", column = @Column(name = "se_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "se_deleted")) })
@Table(name = "colibri_settlement")
public class Settlement extends AbstractEntity implements IPrintable
{
	@Id
	@Column(name = "se_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "se_id")
	@TableGenerator(name = "se_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@OneToOne
	@JoinColumn(name = "se_sp_id")
	private Salespoint salespoint;

	@OneToOne
	@JoinColumn(name = "se_us_id")
	private User user;

	@Basic
	@Column(name = "se_receipt_count")
	private long receiptCount;

	@Basic
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "se_settled")
	private Calendar settled;

	@Basic
	@Column(name = "se_other_id")
	private Long otherId;

	
	@Transient
	private SettlementService.State state;
	
	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementPosition> positions = new ArrayList<SettlementPosition>();

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementInternal> internals = new ArrayList<SettlementInternal>();

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementPayment> payments = new ArrayList<SettlementPayment>();

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementTax> taxes = new ArrayList<SettlementTax>();;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementPayedInvoice> invoices = new ArrayList<SettlementPayedInvoice>();

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementRestitutedPosition> restituted = new ArrayList<SettlementRestitutedPosition>();

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementReceipt> receipts = new ArrayList<SettlementReceipt>();

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementDetail> details = new ArrayList<SettlementDetail>();

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "settlement")
	private List<SettlementMoney> moneys = new ArrayList<SettlementMoney>();

	protected Settlement()
	{
		super();
	}

	protected Settlement(final Salespoint salespoint)
	{
		this();
		this.setSalespoint(salespoint);
	}

	public SalespointCustomerDisplaySettings getCustomerDisplay()
	{
		return this.salespoint.getCustomerDisplaySettings();
	}

//	public void setOtherId(Long otherId)
//	{
//		this.otherId = otherId;
//	}
//	
//	public Long getOtherId()
//	{
//		return this.otherId;
//	}
	
	public List<SettlementDetail> getDetails()
	{
		return this.details;
	}

	public int countPositions(ProductGroupType productGroupType)
	{
		int count = 0;
		for (SettlementPosition position : positions)
		{
			if (position.getProductGroupType().equals(productGroupType))
			{
				count++;
			}
		}
		return count;
	}

	public List<SettlementMoney> getMoneys()
	{
		return this.moneys;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public List<SettlementPayment> getPayments()
	{
		return this.payments;
	}

	public List<SettlementPosition> getPositions()
	{
		return this.positions;
	}

	public List<SettlementPayedInvoice> getPayedInvoices()
	{
		return this.invoices;
	}

	public SalespointReceiptPrinterSettings getReceiptPrinter()
	{
		return this.salespoint.getReceiptPrinterSettings();
	}

	public List<SettlementReceipt> getReversedReceipts()
	{
		return this.receipts;
	}

	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	public Calendar getSettled()
	{
		return this.settled;
	}

	public List<SettlementTax> getTaxes()
	{
		return this.taxes;
	}

	public User getUser()
	{
		return this.user;
	}

	public void setDetails(final List<SettlementDetail> details)
	{
		this.details = details;
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setPayments(final List<SettlementPayment> payments)
	{
		this.propertyChangeSupport.firePropertyChange("payments", this.payments, this.payments = payments);
	}

	public void setPositions(final List<SettlementPosition> positions)
	{
		this.propertyChangeSupport.firePropertyChange("positions", this.positions, this.positions = positions);
	}

	public void setMoneys(final List<SettlementMoney> moneys)
	{
		this.propertyChangeSupport.firePropertyChange("moneys", this.moneys, this.moneys = moneys);
	}

	public void setReversedReceipts(final List<SettlementReceipt> receipts)
	{
		this.propertyChangeSupport.firePropertyChange("receipts", this.receipts, this.receipts = receipts);
	}

	public void setSalespoint(final Salespoint salespoint)
	{
		this.propertyChangeSupport.firePropertyChange("salespoint", this.salespoint, this.salespoint = salespoint);
	}

	public void setSettled(final Calendar settled)
	{
		this.propertyChangeSupport.firePropertyChange("settled", this.settled, this.settled = settled);
	}

	public void setTaxes(final List<SettlementTax> taxes)
	{
		this.propertyChangeSupport.firePropertyChange("taxes", this.taxes, this.taxes = taxes);
	}

	public void setPayedInvoices(final List<SettlementPayedInvoice> invoices)
	{
		this.propertyChangeSupport.firePropertyChange("invoices", this.invoices, this.invoices = invoices);
	}

	public void setUser(final User user)
	{
		this.propertyChangeSupport.firePropertyChange("user", this.user, this.user = user);
	}

	public static Settlement newInstance(final Salespoint salespoint)
	{
		return (Settlement) AbstractEntity.newInstance(new Settlement(salespoint));
	}

	public void setReceiptCount(long receiptCount)
	{
		this.propertyChangeSupport.firePropertyChange("receiptCount", this.receiptCount,
				this.receiptCount = receiptCount);
	}

	public long getReceiptCount()
	{
		return receiptCount;
	}

	public List<SettlementPayment> getVouchers()
	{
		List<SettlementPayment> vouchers = new ArrayList<SettlementPayment>();
		List<SettlementPayment> payments = this.getPayments();
		for (SettlementPayment payment : payments)
		{
			if (payment.getPaymentType().getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
			{
				vouchers.add(payment);
			}
		}
		return vouchers;
	}
	
	public void setRestitutedPositions(List<SettlementRestitutedPosition> restituted)
	{
		this.propertyChangeSupport.firePropertyChange("restituted", this.restituted, this.restituted = restituted);
	}

	public List<SettlementRestitutedPosition> getRestitutedPositions()
	{
		return restituted;
	}

	public void setInternals(List<SettlementInternal> internals)
	{
		this.propertyChangeSupport.firePropertyChange("internals", this.internals, this.internals = internals);
	}

	public List<SettlementInternal> getInternals()
	{
		return internals;
	}

	public Long getOtherId() {
		return otherId;
	}

	public void setOtherId(Long otherId) {
		this.propertyChangeSupport.firePropertyChange("otherId", this.otherId, this.otherId = otherId);
	}

	public SettlementService.State getState() {
		return state;
	}

	public void setState(SettlementService.State state) {
		this.state = state;
	}
}
