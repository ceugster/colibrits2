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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "pgm_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "pgm_version")),
		@AttributeOverride(name = "update", column = @Column(name = "pgm_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "pgm_deleted")) })
@Table(name = "colibri_product_group_mapping")
public class ProductGroupMapping extends AbstractEntity implements IReplicationRelevant
{
	@Id
	@Column(name = "pgm_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "pgm_id")
	@TableGenerator(name = "pgm_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = true, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "pgm_pg_id", referencedColumnName = "pg_id")
	private ProductGroup productGroup;

	@OneToOne(optional = false, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "pgm_epg_id", referencedColumnName = "epg_id")
	private ExternalProductGroup externalProductGroup;

	@Basic
	@Column(name = "pgm_provider")
	private String provider;

	private ProductGroupMapping()
	{
		super();
	}

	private ProductGroupMapping(final ProductGroup productGroup, final ExternalProductGroup externalProductGroup)
	{
		this();
		this.setProductGroup(productGroup);
		this.setExternalProductGroup(externalProductGroup);
	}

	public ExternalProductGroup getExternalProductGroup()
	{
		return this.externalProductGroup;
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	public ProductGroup getProductGroup()
	{
		return this.productGroup;
	}

	public String getProvider()
	{
		return this.provider;
	}

	public void setExternalProductGroup(final ExternalProductGroup externalProductGroup)
	{
		this.propertyChangeSupport.firePropertyChange("externalProductGroup", this.externalProductGroup,
				this.externalProductGroup = externalProductGroup);
		this.setProvider(externalProductGroup.getProvider());
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setProductGroup(final ProductGroup productGroup)
	{
		this.propertyChangeSupport.firePropertyChange("productGroup", this.productGroup, this.productGroup = productGroup);
	}

	public void setProvider(final String provider)
	{
		this.propertyChangeSupport.firePropertyChange("provider", this.provider, this.provider = provider);
	}

	public static ProductGroupMapping newInstance(final ProductGroup productGroup, final ExternalProductGroup externalProductGroup)
	{
		final ProductGroupMapping productGroupMapping = (ProductGroupMapping) AbstractEntity.newInstance(new ProductGroupMapping(productGroup,
				externalProductGroup));
		return productGroupMapping;
	}
}
