/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.util.ArrayList;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "tr_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "tr_version")),
		@AttributeOverride(name = "update", column = @Column(name = "tr_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "tr_deleted")) })
@Table(name = "colibri_tax_rate")
public class TaxRate extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "tr_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tr_id")
	@TableGenerator(name = "tr_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@OneToMany(cascade=CascadeType.ALL , fetch = EAGER, mappedBy = "taxRate")
	private List<Tax> taxes = new ArrayList<Tax>();

	@Basic
	@Column(name = "tr_code")
	private String code;

	@Basic
	@Column(name = "tr_name")
	private String name;

	protected TaxRate()
	{
		super();
	}

	public void addTax(final Tax tax)
	{
		this.propertyChangeSupport.firePropertyChange("taxes", this.taxes, this.taxes.add(tax));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ITaxRate#getCode()
	 */
	public String getCode()
	{
		return this.valueOf(this.code);
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ch.eugster.colibri.persistence.model.ITaxRate#getName()
	 */
	public String getName()
	{
		return this.valueOf(this.name);
	}

	public List<Tax> getTaxes()
	{
		return this.taxes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ITaxRate#setCode(java.lang.String)
	 */
	public void setCode(final String code)
	{
		this.propertyChangeSupport.firePropertyChange("code", this.code, this.code = code);
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
	 * ch.eugster.colibri.persistence.model.ITaxRate#setName(java.lang.String)
	 */
	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setTaxes(final List<Tax> taxes)
	{
		this.propertyChangeSupport.firePropertyChange("taxes", this.taxes, this.taxes = taxes);
	}

	public static TaxRate newInstance()
	{
		return (TaxRate) AbstractEntity.newInstance(new TaxRate());
	}
}
