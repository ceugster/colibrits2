/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import org.eclipse.persistence.annotations.Convert;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "cs_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "cs_version")),
		@AttributeOverride(name = "update", column = @Column(name = "cs_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "cs_deleted")) })
@Table(name = "colibri_common_settings")
public class CommonSettings extends AbstractEntity implements IReplicationRelevant
{
	@Id
	@Column(name = "cs_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "cs_id")
	@TableGenerator(name = "cs_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@OneToOne(optional = false)
	@JoinColumn(name = "cs_cu_id", referencedColumnName = "cu_id")
	private Currency referenceCurrency;

	@OneToOne(optional = true)
	@JoinColumn(name = "cs_pg_id", referencedColumnName = "pg_id")
	private ProductGroup defaultProductGroup;

	@OneToOne(optional = true)
	@JoinColumn(name = "cs_pg_payed_invoice_id", referencedColumnName = "pg_id")
	private ProductGroup payedInvoice;

	@Basic
	@Column(name = "cs_provider")
	private String provider;

	@Basic
	@Column(name = "cs_max_price_range")
	private double maxPriceRange;

	@Basic
	@Column(name = "cs_max_price_amount")
	private double maxPriceAmount;

	@Basic
	@Column(name = "cs_max_qty_range")
	private int maxQuantityRange;

	@Basic
	@Column(name = "cs_max_qty_amount")
	private int maxQuantityAmount;

	@Basic
	@Column(name = "cs_max_pmt_range")
	private double maxPaymentRange;

	@Basic
	@Column(name = "cs_max_pmt_amount")
	private double maxPaymentAmount;

	@Basic
	@Column(name = "cs_receiptnumber_format")
	private String receiptNumberFormat;

	@Basic
	@Column(name = "cs_address")
	private String address;

	@Basic
	@Column(name = "cs_tax_number")
	private String taxNumber;

	@Basic
	@Enumerated
	@Column(name = "cs_hostname_resolver")
	private HostnameResolver hostnameResolver;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "cs_tax_inclusive")
	private boolean taxInclusive;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "cs_allow_test_settlement")
	private boolean allowTestSettlement;

	@Basic
	@Convert("booleanConverter")
	@Column(name = "cs_force_settlement")
	private boolean forceSettlement;

	@Basic
	@Column(name="cs_transfer_delay")
	private int transferDelay;
	
	@Basic
	@Column(name="cs_transfer_repeat_delay")
	private int transferRepeatDelay;
	
	@Basic
	@Column(name="cs_transfer_receipt_count")
	private int transferReceiptCount;
	
	@Basic
	@Convert("booleanConverter")
	@Column(name="cs_maximized_client_window")
	private boolean maximizedClientWindow;
	
	@Basic
	@Convert("booleanConverter")
	@Column(name="cs_export")
	private boolean export;
	
	@Basic
	@Column(name = "cs_export_path")
	private String exportPath;

	@OneToMany(cascade = CascadeType.ALL, fetch = EAGER, mappedBy = "commonSettings")
	private List<CommonSettingsProperty> properties = new Vector<CommonSettingsProperty>();
	
	private CommonSettings()
	{
		super();
	}

	public ProductGroup getDefaultProductGroup()
	{
		return this.defaultProductGroup;
	}

	public HostnameResolver getHostnameResolver()
	{
		return this.hostnameResolver == null ? HostnameResolver.HOSTNAME : this.hostnameResolver;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#getMaxPaymentAmount
	 * ()
	 */
	public double getMaxPaymentAmount()
	{
		return this.maxPaymentAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#getMaxPaymentRange()
	 */
	public double getMaxPaymentRange()
	{
		return this.maxPaymentRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#getMaxPriceAmount()
	 */
	public double getMaxPriceAmount()
	{
		return this.maxPriceAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#getMaxPriceRange()
	 */
	public double getMaxPriceRange()
	{
		return this.maxPriceRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#getMaxQuantityAmount
	 * ()
	 */
	public int getMaxQuantityAmount()
	{
		return this.maxQuantityAmount;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#getMaxQuantityRange
	 * ()
	 */
	public int getMaxQuantityRange()
	{
		return this.maxQuantityRange;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ICommonSettings#getProvider()
	 */
	public String getProvider()
	{
		return this.provider;
	}

	public String getReceiptNumberFormat()
	{
		return this.valueOf(this.receiptNumberFormat);
	}

	public Currency getReferenceCurrency()
	{
		return this.referenceCurrency;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#isTaxInclusive()
	 */
	public boolean isTaxInclusive()
	{
		return this.taxInclusive;
	}

	public void setDefaultProductGroup(final ProductGroup productGroup)
	{
		this.propertyChangeSupport.firePropertyChange("defaultProductGroup", this.defaultProductGroup,
				this.defaultProductGroup = productGroup);
	}

	public void setHostnameResolver(final HostnameResolver resolver)
	{
		this.propertyChangeSupport.firePropertyChange("hostnameResolver", this.hostnameResolver,
				this.hostnameResolver = resolver);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#setMaxPaymentAmount
	 * (double)
	 */
	public void setMaxPaymentAmount(final double maxPaymentAmount)
	{
		this.propertyChangeSupport.firePropertyChange("maxPaymentAmount", this.maxPaymentAmount,
				this.maxPaymentAmount = maxPaymentAmount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#setMaxPaymentRange
	 * (double)
	 */
	public void setMaxPaymentRange(final double maxPaymentRange)
	{
		this.propertyChangeSupport.firePropertyChange("maxPaymentRange", this.maxPaymentRange,
				this.maxPaymentRange = maxPaymentRange);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#setMaxPriceAmount
	 * (double)
	 */
	public void setMaxPriceAmount(final double maxPriceAmount)
	{
		this.propertyChangeSupport.firePropertyChange("maxPriceAmount", this.maxPriceAmount,
				this.maxPriceAmount = maxPriceAmount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#setMaxPriceRange
	 * (double)
	 */
	public void setMaxPriceRange(final double maxPriceRange)
	{
		this.propertyChangeSupport.firePropertyChange("maxPriceRange", this.maxPriceRange,
				this.maxPriceRange = maxPriceRange);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#setMaxQuantityAmount
	 * (int)
	 */
	public void setMaxQuantityAmount(final int maxQuantityAmount)
	{
		this.propertyChangeSupport.firePropertyChange("maxQuantityAmount", this.maxQuantityAmount,
				this.maxQuantityAmount = maxQuantityAmount);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#setMaxQuantityRange
	 * (int)
	 */
	public void setMaxQuantityRange(final int maxQuantityRange)
	{
		this.propertyChangeSupport.firePropertyChange("maxQuantityRange", this.maxQuantityRange,
				this.maxQuantityRange = maxQuantityRange);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#setProvider(java
	 * .lang.String)
	 */
	public void setProvider(final String provider)
	{
		this.propertyChangeSupport.firePropertyChange("provider", this.provider, this.provider = provider);
	}

	public void setReceiptNumberFormat(final String receiptNumberFormat)
	{
		this.receiptNumberFormat = receiptNumberFormat;
	}

	public void setReferenceCurrency(final Currency currency)
	{
		this.propertyChangeSupport.firePropertyChange("referenceCurrency", this.referenceCurrency,
				this.referenceCurrency = currency);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ICommonSettings#setTaxInclusive(
	 * boolean)
	 */
	public void setTaxInclusive(final boolean inclusive)
	{
		this.propertyChangeSupport.firePropertyChange("taxInclusive", this.taxInclusive, this.taxInclusive = inclusive);
	}

	public static CommonSettings newInstance()
	{
		return (CommonSettings) AbstractEntity.newInstance(new CommonSettings());
	}

	public void setAddress(String address)
	{
		this.propertyChangeSupport.firePropertyChange("address", this.address, this.address = address);
	}

	public String getAddress()
	{
		return valueOf(this.address);
	}

	public void setTaxNumber(String taxNumber)
	{
		this.propertyChangeSupport.firePropertyChange("taxNumber", this.taxNumber, this.taxNumber = taxNumber);
	}

	public String getTaxNumber()
	{
		return valueOf(taxNumber);
	}

	public void setPayedInvoice(ProductGroup payedInvoice)
	{
		this.propertyChangeSupport.firePropertyChange("payedInvoice", this.payedInvoice, this.payedInvoice = payedInvoice);
	}

	public ProductGroup getPayedInvoice()
	{
		return payedInvoice;
	}

	public boolean isAllowTestSettlement()
	{
		return allowTestSettlement;
	}

	public void setAllowTestSettlement(boolean allowTestSettlement)
	{
		this.propertyChangeSupport.firePropertyChange("allowTestSettlement", this.allowTestSettlement, this.allowTestSettlement = allowTestSettlement);
	}

	public int getTransferDelay()
	{
		return transferDelay;
	}

	public void setTransferDelay(int transferDelay)
	{
		this.propertyChangeSupport.firePropertyChange("transferDelay", this.transferDelay, this.transferDelay = transferDelay);
	}

	public int getTransferRepeatDelay()
	{
		return transferRepeatDelay;
	}

	public void setTransferRepeatDelay(int transferRepeatDelay)
	{
		this.propertyChangeSupport.firePropertyChange("transferRepeatDelay", this.transferRepeatDelay, this.transferRepeatDelay = transferRepeatDelay);
	}

	public int getTransferReceiptCount()
	{
		return transferReceiptCount;
	}

	public void setTransferReceiptCount(int transferReceiptCount)
	{
		this.propertyChangeSupport.firePropertyChange("transferReceiptCount", this.transferReceiptCount, this.transferReceiptCount = transferReceiptCount);
	}

	public boolean isMaximizedClientWindow() {
		return maximizedClientWindow;
	}

	public void setMaximizedClientWindow(boolean maximizedClientWindow) {
		this.maximizedClientWindow = maximizedClientWindow;
	}

	public boolean isForceSettlement() {
		return forceSettlement;
	}

	public void setForceSettlement(boolean forceSettlement) {
		this.propertyChangeSupport.firePropertyChange("forceSettlement", this.forceSettlement, this.forceSettlement = forceSettlement);
	}

	public enum HostnameResolver
	{
		HOSTNAME, CANONICAL_HOSTNAME, HOSTNAME_IP, IP;

		public String getHostname()
		{
			try
			{
				final InetAddress address = InetAddress.getLocalHost();
				switch (this)
				{
					case HOSTNAME:
					{
						return address.getHostName();
					}
					case CANONICAL_HOSTNAME:
					{
						return address.getCanonicalHostName();
					}
					case HOSTNAME_IP:
					{
						return address.getHostName() + "@" + address.getHostAddress();
					}
					case IP:
					{
						return address.getHostAddress();
					}
					default:
						throw new RuntimeException("Ungültiger Hostname Resolver");
				}
			}
			catch (final UnknownHostException e)
			{
				return "";
			}
		}

		public String getLabel()
		{
			switch (this)
			{
				case HOSTNAME:
				{
					return "Arbeitsplatzname";
				}
				case CANONICAL_HOSTNAME:
				{
					return "Qualifizierter Arbeitsplatzname";
				}
				case HOSTNAME_IP:
				{
					return "Arbeitsplatzname und IP-Adresse";
				}
				case IP:
				{
					return "IP-Adresse";
				}
				default:
					throw new RuntimeException("Ungültiger Hostname Resolver");
			}
		}

		public String getValue()
		{
			return Integer.valueOf(this.ordinal()).toString();
		}
	}

	public boolean isExport() 
	{
		return this.export;
	}

	public void setExport(boolean export) 
	{
		this.propertyChangeSupport.firePropertyChange("export", this.export, this.export = export);
	}

	public String getExportPath() 
	{
		return this.valueOf(exportPath);
	}

	public void setExportPath(String exportPath) {
		this.propertyChangeSupport.firePropertyChange("exportPath", this.exportPath, this.exportPath = exportPath);
	}
	
	public List<CommonSettingsProperty> getProperties()
	{
		return this.properties;
	}

	public void addProperties(CommonSettingsProperty property)
	{
		this.properties.add(property);
	}

	public void removeProperties(CommonSettingsProperty property)
	{
		this.properties.remove(property);
	}
	
	public Map<String, CommonSettingsProperty> getProperties(String discriminator)
	{
		return getProperties(null, discriminator);
	}
	
	public Map<String, CommonSettingsProperty> getProperties(Salespoint salespoint, String discriminator)
	{
		Map<String, CommonSettingsProperty> selectedProperties = new HashMap<String, CommonSettingsProperty>();
		for (CommonSettingsProperty property : this.properties)
		{
			if (property.getDiscriminator().equals(discriminator))
			{
				if (salespoint == null)
				{
					if (property.getSalespoint() == null)
					{
						selectedProperties.put(property.getKey(), property);
					}
				}
				else if (property.getSalespoint() != null && property.getSalespoint().getId().equals(salespoint.getId()))
				{
					selectedProperties.put(property.getKey(), property);
				}
			}
		}
		return selectedProperties;
	}
}
