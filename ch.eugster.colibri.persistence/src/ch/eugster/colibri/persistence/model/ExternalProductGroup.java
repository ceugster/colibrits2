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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "epg_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "epg_version")),
		@AttributeOverride(name = "update", column = @Column(name = "epg_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "epg_deleted")) })
@Table(name = "colibri_external_product_group")
public class ExternalProductGroup extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "epg_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "epg_id")
	@TableGenerator(name = "epg_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@OneToOne(optional = true, cascade = CascadeType.ALL)
	@JoinColumn(name = "epg_pgm_id", referencedColumnName = "pgm_id")
	private ProductGroupMapping productGroupMapping;

	@Basic
	@Column(name = "epg_code")
	private String code;

	@Basic
	@Column(name = "epg_provider")
	private String provider;

	@Basic
	@Column(name = "epg_text")
	private String text;

	@Basic
	@Column(name = "epg_account")
	private String account;

//	@OneToMany(mappedBy = "externalProductGroup", cascade = CascadeType.ALL)
//	private Collection<ProductGroupMapping> productGroupMappings = new Vector<ProductGroupMapping>();

	private ExternalProductGroup()
	{
		super();
	}

	private ExternalProductGroup(final String provider)
	{
		super();
		this.setProvider(provider);
	}

	public void setDeleted(boolean deleted)
	{
		super.setDeleted(deleted);
		if (productGroupMapping != null)
		{
			productGroupMapping.setDeleted(deleted);
		}
	}
	
	public String format()
	{
		final StringBuffer sb = new StringBuffer(this.getCode());
		if ((sb.length() > 0) && (this.getText().length() > 0))
		{
			sb.append(" - ");
		}
		return sb.append(this.getText()).toString();
	}

	public String getAccount()
	{
		return this.valueOf(this.account);
	}

	public String getCode()
	{
		return this.valueOf(this.code);
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public String getProvider()
	{
		return this.provider;
	}

	public String getText()
	{
		return this.valueOf(this.text);
	}

	public void setAccount(final String account)
	{
		this.account = account;
	}

	public void setCode(final String code)
	{
		this.propertyChangeSupport.firePropertyChange("code", this.code, this.code = code);
	}

//	@Override
//	public void setDeleted(final boolean deleted)
//	{
//		super.setDeleted(deleted);
//		for (ProductGroupMapping productGroupMapping : productGroupMappings)
//		{
//			productGroupMapping.setDeleted(deleted);
//		}
//	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

//	public void addProductGroupMapping(final ProductGroupMapping productGroupMapping)
//	{
//		this.propertyChangeSupport.firePropertyChange("addMapping", this.productGroupMappings,
//				this.productGroupMappings.add(productGroupMapping));
//	}

	public void setProvider(final String provider)
	{
		this.propertyChangeSupport.firePropertyChange("provider", this.provider, this.provider = provider);
	}

	public void setText(final String text)
	{
		this.propertyChangeSupport.firePropertyChange("text", this.text, this.text = text);
	}

	public static ExternalProductGroup newInstance()
	{
		final ExternalProductGroup externalProductGroup = (ExternalProductGroup) AbstractEntity.newInstance(new ExternalProductGroup());
		return externalProductGroup;
	}

	public static ExternalProductGroup newInstance(final String provider)
	{
		final ExternalProductGroup externalProductGroup = (ExternalProductGroup) AbstractEntity.newInstance(new ExternalProductGroup(provider));
		return externalProductGroup;
	}

	public void setProductGroupMapping(ProductGroupMapping productGroupMapping) 
	{
		this.productGroupMapping = productGroupMapping;
	}

	public ProductGroupMapping getProductGroupMapping() 
	{
		return productGroupMapping;
	}
}
