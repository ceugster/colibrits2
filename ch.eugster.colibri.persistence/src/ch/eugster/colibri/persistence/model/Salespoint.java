package ch.eugster.colibri.persistence.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

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
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.persistence.annotations.Convert;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "sp_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "sp_version")),
		@AttributeOverride(name = "update", column = @Column(name = "sp_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "sp_deleted")) })
@Table(name = "colibri_salespoint")
public class Salespoint extends AbstractEntity implements IAdaptable, IReplicatable
{
	@Id
	@Column(name = "sp_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sp_id")
	@TableGenerator(name = "sp_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	/**
	 * The default currency of this salespoint. The currency is hidden in the
	 * payment type. This currency may not be the same currency as the reference
	 * currency in common settings. While the reference currency must not be
	 * changed, the default currency of a salespoint may be changed, because the
	 * default currency is only used for current receipts to define theirs
	 * default currency. Each receipt references the reference currency of
	 * common settings too.
	 */
	@ManyToOne(optional = false)
	@JoinColumn(name = "sp_pt_id", referencedColumnName = "pt_id")
	private PaymentType paymentType;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sp_tx_id", referencedColumnName = "tx_id")
	private Tax proposalTax;

	@ManyToOne(optional = false, cascade=CascadeType.ALL)
	@JoinColumn(name = "sp_pr_id", referencedColumnName = "pr_id")
	private Profile profile;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sp_cs_id", referencedColumnName = "cs_id")
	private CommonSettings commonSettings;

	@Basic
	@Column(name = "sp_name")
	private String name;

	@Basic
	@Column(name = "sp_location")
	private String location;

	@Basic
	@Column(name = "sp_mapping")
	private String mapping;

	@Basic
	@Column(name = "sp_quantity")
	private int proposalQuantity = 0;

	@Basic
	@Column(name = "sp_price", columnDefinition = "DECIMAL(18, 6)")
	private double proposalPrice = 0d;

	// @Basic
	// @Column(name = "sp_option")
	// private int proposalOption;

	@Basic
	@Column(nullable = false, name = "sp_current_rn")
	private Long currentReceiptNumber = Long.valueOf(0L);

	@Basic
	@Column(nullable = false, name = "sp_parked_rn")
	private long currentParkedReceiptNumber = Long.valueOf(0L);

	@Basic
	@Column(name = "sp_host")
	private String host;

	@Basic
	@Column(name = "sp_local_pp")
	@Convert("booleanConverter")
	private boolean localProviderProperties;

	@Basic
	@Column(name = "sp_force_settlement")
	@Convert("booleanConverter")
	private Boolean forceSettlement;

	@Basic
	@Convert("booleanConverter")
	@Column(name="sp_use_individual_export")
	private boolean useIndividualExport;
	
	@Basic
	@Convert("booleanConverter")
	@Column(name="sp_export")
	private boolean export;
	
	@Basic
	@Column(name = "sp_export_path")
	private String exportPath;

	@OneToOne(optional = true)
	@JoinColumn(name = "sp_di_id")
	private Display display;

	@JoinColumn(name = "sp_se_id")
	@OneToOne(cascade = CascadeType.ALL, optional = true)
	private Settlement settlement;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "salespoint")
	private Collection<Stock> stocks = new Vector<Stock>();

	@JoinColumn(name = "sp_scd_id", referencedColumnName = "scd_id")
	@OneToOne(cascade = CascadeType.ALL, optional = true)
	private SalespointCustomerDisplaySettings customerDisplaySettings;

	@JoinColumn(name = "sp_srp_id", referencedColumnName = "srp_id")
	@OneToOne(cascade = CascadeType.ALL, optional = true)
	private SalespointReceiptPrinterSettings receiptPrinterSettings;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "salespoint")
	@MapKey(name = "key")
	private Map<String, ProviderProperty> providerProperties = new HashMap<String, ProviderProperty>();

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "salespoint")
	@MapKey(name = "printoutType")
	private Map<String, Printout> printouts = new HashMap<String, Printout>();

	protected Salespoint()
	{
		super();
	}

	protected Salespoint(final CommonSettings commonSettings)
	{
		super();
		this.setCommonSettings(commonSettings);
	}

	public void addStock(final Stock stock)
	{
		this.propertyChangeSupport.firePropertyChange("stocks", this.stocks, this.stocks.add(stock));
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter)
	{
		if ((adapter == null) || adapter.equals(Salespoint.class))
		{
			return this;
		}
		return null;
	}

	public CommonSettings getCommonSettings()
	{
		return this.commonSettings;
	}

	public Long getCurrentParkedReceiptNumber()
	{
		return this.currentParkedReceiptNumber;
	}

	public Long getCurrentReceiptNumber()
	{
		return this.currentReceiptNumber;
	}

	public SalespointCustomerDisplaySettings getCustomerDisplaySettings()
	{
		return this.customerDisplaySettings;
	}

	public Display getDisplay()
	{
		return this.display;
	}

	public String getHost()
	{
		return this.valueOf(this.host);
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getLocation()
	{
		return this.valueOf(this.location);
	}

	public String getMapping()
	{
		return this.mapping;
	}

	public String getName()
	{
		return this.valueOf(this.name);
	}

	public Long getNextParkedReceiptNumber()
	{
		if (this.currentParkedReceiptNumber == 0l)
		{
			this.currentParkedReceiptNumber = 0l;
		}
		this.setCurrentParkedReceiptNumber(this.currentParkedReceiptNumber + 1l);
		return this.getCurrentParkedReceiptNumber();
	}

	public Long getNextReceiptNumber()
	{
		if (this.currentReceiptNumber == null)
		{
			this.currentReceiptNumber = new Long(0L);
		}
		this.setCurrentReceiptNumber(new Long(this.currentReceiptNumber.longValue() + 1L));
		return this.getCurrentReceiptNumber();
	}

	public PaymentType getPaymentType()
	{
		return this.paymentType;
	}

	public Printout getPrintout(final String printoutType)
	{
		return this.printouts.get(printoutType);
	}

	public Collection<Printout> getPrintouts()
	{
		return this.printouts.values();
	}

	public Profile getProfile()
	{
		return this.profile;
	}

	// public Position.Option getProposalOption()
	// {
	// return Position.Option.values()[this.proposalOption];
	// }

	public double getProposalPrice()
	{
		return this.proposalPrice;
	}

	public int getProposalQuantity()
	{
		return this.proposalQuantity == 0 ? 1 : this.proposalQuantity;
	}

	public Tax getProposalTax()
	{
		return this.proposalTax;
	}

	public Map<String, ProviderProperty> getProviderProperties()
	{
		return this.providerProperties;
	}

	public SalespointReceiptPrinterSettings getReceiptPrinterSettings()
	{
		return this.receiptPrinterSettings;
	}

	public Settlement getSettlement()
	{
		return this.settlement;
	}

	public Collection<Stock> getStocks()
	{
		Collection<Stock> stocks = new ArrayList<Stock>();
		for (Stock stock : this.stocks)
		{
			if (!stock.isDeleted())
			{
				stocks.add(stock);
			}
		}
		return stocks;
	}

	public boolean isLocalProviderProperties()
	{
		return this.localProviderProperties;
	}

	public void putPrintout(final Printout printout)
	{
		this.propertyChangeSupport.firePropertyChange("printouts", this.printouts,
				this.printouts.put(printout.getPrintoutType(), printout));
	}

	public void putProviderProperties(final Map<String, ProviderProperty> providerProperties)
	{
		this.providerProperties = providerProperties;
	}

	public void putProviderProperty(final ProviderProperty salespointProviderProperty)
	{
		this.propertyChangeSupport.firePropertyChange("providerProperties", this.providerProperties,
				this.providerProperties.put(salespointProviderProperty.getKey(), salespointProviderProperty));
	}

	public void removeProviderProperty(final ProviderProperty providerProperty)
	{
		this.propertyChangeSupport.firePropertyChange("providerProperties", this.providerProperties,
				this.providerProperties.remove(providerProperty));
	}

	public void removeStock(final Stock stock)
	{
		this.propertyChangeSupport.firePropertyChange("stocks", this.stocks, this.stocks.remove(stock));
	}

	public void setCommonSettings(final CommonSettings commonSettings)
	{
		this.commonSettings = commonSettings;
	}

	public void setCurrentParkedReceiptNumber(final long currentParkedReceiptNumber)
	{
		this.propertyChangeSupport.firePropertyChange("currentParkedReceiptNumber", this.currentParkedReceiptNumber,
				this.currentParkedReceiptNumber = currentParkedReceiptNumber);
	}

	public void setCurrentReceiptNumber(final Long currentReceiptNumber)
	{
		this.propertyChangeSupport.firePropertyChange("currentReceiptNumber", this.currentReceiptNumber,
				this.currentReceiptNumber = currentReceiptNumber);
	}

	public void setCustomerDisplaySettings(final SalespointCustomerDisplaySettings customerDisplaySettings)
	{
		this.propertyChangeSupport.firePropertyChange("customerDisplaySettings", this.customerDisplaySettings,
				this.customerDisplaySettings = customerDisplaySettings);
	}

	@Override
	public void setDeleted(final boolean deleted)
	{
		for (final Stock stock : this.getStocks())
		{
			if (stock.isDeleted() != deleted)
			{
				stock.setDeleted(deleted);
			}
		}
		if (!this.isDeleted())
		{
			super.setDeleted(deleted);
		}
	}

	public void setDisplay(final Display display)
	{
		this.propertyChangeSupport.firePropertyChange("display", this.display, this.display = display);
	}

	public void setHost(final String host)
	{
		this.host = host;
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setLocalProviderProperties(final boolean localProviderProperties)
	{
		this.localProviderProperties = localProviderProperties;
	}

	public void setLocation(final String location)
	{
		this.propertyChangeSupport.firePropertyChange("place", this.location, this.location = (location == "" ? null : location));
	}

	public void setMapping(final String mapping)
	{
		this.propertyChangeSupport.firePropertyChange("mapping", this.mapping, this.mapping = (mapping == "" ? null : mapping));
	}

	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = (name == "" ? null : name));
	}

	public void setPaymentType(final PaymentType paymentType)
	{
		this.propertyChangeSupport.firePropertyChange("paymentType", this.paymentType, this.paymentType = paymentType);
	}

	public void setProfile(final Profile profile)
	{
		this.propertyChangeSupport.firePropertyChange("profile", this.profile, this.profile = profile);
	}

	// public void setProposalOption(final int proposalOption)
	// {
	// this.proposalOption = proposalOption;
	// }

	// public void setProposalOption(final Position.Option proposalOption)
	// {
	// this.propertyChangeSupport.firePropertyChange("proposalOption",
	// this.proposalOption, this.proposalOption = proposalOption.ordinal());
	// }

	public void setProposalPrice(final double proposalPrice)
	{
		this.propertyChangeSupport.firePropertyChange("proposalPrice", this.proposalPrice,
				this.proposalPrice = proposalPrice);
	}

	public void setProposalQuantity(final int proposalQuantity)
	{
		this.propertyChangeSupport.firePropertyChange("proposalQuantity", this.proposalQuantity,
				this.proposalQuantity = proposalQuantity);
	}

	public void setProposalTax(final Tax proposalTax)
	{
		this.propertyChangeSupport.firePropertyChange("proposalTax", this.proposalTax, this.proposalTax = proposalTax);
	}

	public void setProviderProperties(final Map<String, ProviderProperty> salespointProviderProperties)
	{
		this.propertyChangeSupport.firePropertyChange("providerProperties", this.providerProperties,
				this.providerProperties = salespointProviderProperties);
	}

	public void setReceiptPrinterSettings(final SalespointReceiptPrinterSettings receiptPrinterSettings)
	{
		this.propertyChangeSupport.firePropertyChange("receiptPrinterSettings", this.receiptPrinterSettings,
				this.receiptPrinterSettings = receiptPrinterSettings);
	}

	public void setSettlement(final Settlement settlement)
	{
		this.propertyChangeSupport.firePropertyChange("settlement", this.settlement, this.settlement = settlement);
	}

	public void setStocks(final Collection<Stock> stocks)
	{
		this.propertyChangeSupport.firePropertyChange("stocks", this.stocks, this.stocks = stocks);
	}

	public static Salespoint newInstance(final CommonSettings commonSettings)
	{
		final Salespoint salespoint = (Salespoint) AbstractEntity.newInstance(new Salespoint(commonSettings));
		salespoint.setSettlement(Settlement.newInstance(salespoint));
		return salespoint;
	}

	public static String getSalespointList(Salespoint[] salespoints)
	{
		StringBuilder names = new StringBuilder();
		for (Salespoint salespoint : salespoints)
		{
			if (names.length() > 0)
			{
				names.append(", ");
			}
			names.append(salespoint.getName());
		}
		return names.toString();
	}

	public boolean isForceSettlement() 
	{
		return forceSettlement == null ? this.getCommonSettings().isForceSettlement() : forceSettlement.booleanValue();
	}

	public void setForceSettlement(boolean forceSettlement) 
	{
		if (forceSettlement == this.getCommonSettings().isForceSettlement())
		{
			if (this.forceSettlement != null)
			{
				this.propertyChangeSupport.firePropertyChange("forceSettlement", this.forceSettlement, this.forceSettlement = null);
			}
		}
		else
		{
			if (this.forceSettlement == null || this.forceSettlement.booleanValue() != forceSettlement)
			{
				this.propertyChangeSupport.firePropertyChange("forceSettlement", this.forceSettlement, this.forceSettlement = null);
			}
		}
	}
	
	public boolean isExport() 
	{
		return useIndividualExport ? export : this.commonSettings.isExport();
	}

	public boolean isExport(boolean useIndividualExport) 
	{
		return useIndividualExport ? export : this.commonSettings.isExport();
	}

	public void setExport(boolean export) 
	{
		this.propertyChangeSupport.firePropertyChange("export", this.export, this.export = export);
	}

	public String getExportPath() 
	{
		return useIndividualExport ? this.valueOf(exportPath) : commonSettings.getExportPath();
	}

	public String getExportPath(boolean useIndividualExport) 
	{
		return useIndividualExport ? this.valueOf(exportPath) : commonSettings.getExportPath();
	}

	public void setExportPath(String exportPath) 
	{
		this.propertyChangeSupport.firePropertyChange("exportPath", this.exportPath, this.exportPath = exportPath);
	}

	public boolean isUseIndividualExport() 
	{
		return useIndividualExport;
	}

	public void setUseIndividualExport(boolean useIndividualExport) 
	{
		this.propertyChangeSupport.firePropertyChange("useIndividualExport", this.useIndividualExport, this.useIndividualExport = useIndividualExport);
	}
}
