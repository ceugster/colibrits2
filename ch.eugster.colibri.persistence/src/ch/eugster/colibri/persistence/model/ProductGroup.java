/*
 * Created on 01.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import java.util.ArrayList;
import java.util.List;
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

import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "pg_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "pg_version")),
		@AttributeOverride(name = "update", column = @Column(name = "pg_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "pg_deleted")) })
@Table(name = "colibri_product_group")
public class ProductGroup extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "pg_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pg_id")
	@TableGenerator(name = "pg_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	/**
	 * <code>defaultTax</code> is optional. So <code>getDefaultTax()</code> may
	 * return <code>null</code>
	 */
	@OneToOne(optional = true)
	@JoinColumn(name = "pg_tx_id", referencedColumnName = "tx_id")
	private Tax defaultTax;

	/**
	 * <code>PaymentType</code> is only used in case
	 * <code>productGroupType.getParent()</code> equals
	 * <code>ProductGroupGroup.INTERNAL</code>. In all other cases it is
	 * <code>null</code>
	 */

	@OneToOne(optional = true)
	@JoinColumn(name = "pg_pt_id", referencedColumnName = "pt_id")
	private PaymentType paymentType;

	@OneToOne(optional = false)
	@JoinColumn(name = "pg_cs_id", referencedColumnName = "cs_id")
	private CommonSettings commonSettings;

	@Basic
	@Column(name = "pg_code")
	private String code;

	@Basic
	@Column(name = "pg_name")
	private String name;

	@Basic
	@Column(name = "pg_mapping_id")
	private String mappingId;

	@Basic
	@Column(name = "pg_quantity")
	private int quantityProposal;

	@Basic
	@Column(name = "pg_price", columnDefinition = "DECIMAL(18, 6)")
	private double priceProposal;

	@Basic
	@Enumerated
	@Column(name = "pg_option")
	private Option proposalOption;

	@Basic
	@Column(name = "pg_account")
	private String account;

	@Basic
	@Column(name = "pg_product_group_type")
	@Enumerated
	private ProductGroupType productGroupType;

	@OneToMany(mappedBy = "productGroup", cascade = CascadeType.ALL)
	private List<ProductGroupMapping> productGroupMappings = new Vector<ProductGroupMapping>();

	protected ProductGroup()
	{
		super();
	}

	protected ProductGroup(final ProductGroupType productGroupType, final CommonSettings commonSettings)
	{
		this.setProductGroupType(productGroupType);
		this.setCommonSettings(commonSettings);
	}

	public CommonSettings getCommonSettings()
	{
		return commonSettings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IProductGroup#getAccount()
	 */
	public String getAccount()
	{
		return this.valueOf(this.account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IProductGroup#getCode()
	 */
	public String getCode()
	{
		return this.valueOf(this.code);
	}
	
	public Tax getDefaultTax()
	{
		return this.defaultTax;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IProductGroup#getMappingId()
	 */
	public String getMappingId()
	{
		return this.valueOf(this.mappingId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.IProductGroup#getName()
	 */
	public String getName()
	{
		return this.valueOf(this.name);
	}

	public PaymentType getPaymentType()
	{
		return this.paymentType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IProductGroup#getPriceProposal()
	 */
	public double getPriceProposal()
	{
		return this.priceProposal;
	}

	public List<ProductGroupMapping> getProductGroupMappings(final String provider)
	{
		List<ProductGroupMapping> mappings = new ArrayList<ProductGroupMapping>();
		for (ProductGroupMapping productGroupMapping : productGroupMappings)
		{
			if (productGroupMapping.getProvider().equals(provider))
			{
				mappings.add(productGroupMapping);
			}
		}
		return mappings;
	}

	public List<ProductGroupMapping> getProductGroupMappings()
	{
		return this.productGroupMappings;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IProductGroup#getProductGroupType()
	 */
	public ProductGroupType getProductGroupType()
	{
		return this.productGroupType;
	}

	public Position.Option getProposalOption()
	{
		return this.proposalOption;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IProductGroup#getQuantityProposal()
	 */
	public int getQuantityProposal()
	{
		return this.quantityProposal == 0 ? 1 : this.quantityProposal;
	}

	@Override
	public int hashCode()
	{
		if (this.id == null)
		{
			return Integer.MIN_VALUE + this.getProductGroupType().ordinal();
		}
		else
		{
			return Integer.MIN_VALUE + this.getProductGroupType().ordinal() + this.id.intValue() * 10;
		}
	}

	public boolean isDeletable()
	{
		if (this.commonSettings.getDefaultProductGroup() != null && this.commonSettings.getDefaultProductGroup().getId().equals(this.getId()))
		{
			return false;
		}
		if (this.commonSettings.getPayedInvoice() != null && this.commonSettings.getPayedInvoice().getId().equals(this.getId()))
		{
			return false;
		}
		return true;
	}

	public void addProductGroupMapping(final ProductGroupMapping productGroupMapping)
	{
		this.propertyChangeSupport.firePropertyChange("addMapping", this.productGroupMappings,
				this.productGroupMappings.add(productGroupMapping));
	}

	public void removeProductGroupMapping(final ProductGroupMapping productGroupMapping)
	{
		this.propertyChangeSupport.firePropertyChange("productGroupMappings", this.productGroupMappings,
				this.productGroupMappings.remove(productGroupMapping));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IProductGroup#setAccount(java.lang
	 * .String)
	 */
	public void setAccount(final String account)
	{
		this.propertyChangeSupport.firePropertyChange("account", this.account, this.account = account);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IProductGroup#setCode(java.lang.
	 * String)
	 */
	public void setCode(final String code)
	{
		this.propertyChangeSupport.firePropertyChange("code", this.code, this.code = code);
	}

	public void setDefaultTax(final Tax defaultTax)
	{
		this.propertyChangeSupport.firePropertyChange("defaultTax", this.defaultTax, this.defaultTax = defaultTax);
	}

	@Override
	public void setDeleted(final boolean deleted)
	{
		super.setDeleted(deleted);
		for (final ProductGroupMapping productGroupMapping : productGroupMappings)
		{
			productGroupMapping.setDeleted(deleted);
		}
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
	 * ch.eugster.colibri.persistence.model.IProductGroup#setMappingId(java.
	 * lang.String)
	 */
	public void setMappingId(final String mappingId)
	{
		this.propertyChangeSupport.firePropertyChange("mappingId", this.mappingId, this.mappingId = mappingId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IProductGroup#setName(java.lang.
	 * String)
	 */
	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setPaymentType(final PaymentType paymentType)
	{
		this.propertyChangeSupport.firePropertyChange("paymentType", this.paymentType, this.paymentType = paymentType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IProductGroup#setPriceProposal(double
	 * )
	 */
	public void setPriceProposal(final double priceProposal)
	{
		this.propertyChangeSupport.firePropertyChange("priceProposal", this.priceProposal,
				this.priceProposal = priceProposal);
	}

	public void setProductGroupType(final ProductGroupType productGroupType)
	{
		this.propertyChangeSupport.firePropertyChange("productGroupType", this.productGroupType,
				this.productGroupType = productGroupType);
		
		ProductGroupGroup group = productGroupType.getParent();
		if (this.getProposalOption() == null)
		{
			if (group.equals(ProductGroupGroup.SALES))
			{
				Option[] options = productGroupType.getOptions();
				this.setProposalOption(options.length == 0 ? null : options[0]);
			}
		}
		else
		{
			if (group.equals(ProductGroupGroup.EXPENSES) || group.equals(ProductGroupGroup.INTERNAL))
			{
				this.setProposalOption(null);
			}
		}
	}

	public void setProposalOption(final Position.Option proposalOption)
	{
		this.propertyChangeSupport.firePropertyChange("proposalOption", this.proposalOption,
				this.proposalOption = proposalOption);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.IProductGroup#setQuantityProposal
	 * (int)
	 */
	public void setQuantityProposal(final int quantityProposal)
	{
		this.propertyChangeSupport.firePropertyChange("quantityProposal", this.quantityProposal,
				this.quantityProposal = quantityProposal);
	}

	private void setCommonSettings(final CommonSettings commonSettings)
	{
		this.propertyChangeSupport.firePropertyChange("commonSettings", this.commonSettings,
				this.commonSettings = commonSettings);
	}
	
	public boolean isPayedInvoice()
	{
		if (this.getId() == null)
		{
			return false;
		}
		if (this.getCommonSettings().getPayedInvoice() != null && this.getCommonSettings().getPayedInvoice().getId().equals(this.getId()))
		{
			return true;
		}
		return false;
	}

	public boolean isEBooks()
	{
		if (this.getId() == null)
		{
			return false;
		}
		if (this.getCommonSettings().getEBooks() != null && this.getCommonSettings().getEBooks().getId().equals(this.getId()))
		{
			return true;
		}
		return false;
	}

	public boolean isVoucherDefault()
	{
		if (this.getId() == null)
		{
			return false;
		}
		if (this.getCommonSettings().getDefaultVoucherProductGroup() != null && this.getCommonSettings().getDefaultVoucherProductGroup().getId().equals(this.getId()))
		{
			return true;
		}
		return false;
	}

	public boolean isDefault()
	{
		if (this.getId() == null)
		{
			return false;
		}
		if (this.getCommonSettings().getDefaultProductGroup() != null && this.getCommonSettings().getDefaultProductGroup().getId().equals(this.getId()))
		{
			return true;
		}
		return false;
	}

	public static ProductGroup newInstance(final ProductGroupType productGroupType, final CommonSettings commonSettings)
	{
		final ProductGroup productGroup = (ProductGroup) AbstractEntity.newInstance(new ProductGroup(productGroupType,
				commonSettings));
		return productGroup;
	}

	public String compatibleState()
	{
		if (this.getProductGroupType().equals(ProductGroupType.SALES_RELATED))
		{
			return "0";
		}
		else if (this.getProductGroupType().equals(ProductGroupType.NON_SALES_RELATED))
		{
			return "1";
		}
		else if (this.getProductGroupType().equals(ProductGroupType.EXPENSES_MATERIAL))
		{
			return "2";
		}
		else if (this.getProductGroupType().equals(ProductGroupType.EXPENSES_INVESTMENT))
		{
			return "2";
		}
		else if (this.getProductGroupType().equals(ProductGroupType.ALLOCATION))
		{
			return "3";
		}
		else if (this.getProductGroupType().equals(ProductGroupType.WITHDRAWAL))
		{
			return "4";
		}
		return "0";
	}
}
