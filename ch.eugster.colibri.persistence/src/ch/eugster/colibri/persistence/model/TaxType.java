/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import static javax.persistence.FetchType.EAGER;

import java.util.Collection;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "version", column = @Column(name = "tt_version")),
		@AttributeOverride(name = "timestamp", column = @Column(name = "tt_timestamp")),
		@AttributeOverride(name = "update", column = @Column(name = "tt_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "tt_deleted")) })
@Table(name = "colibri_tax_type")
public class TaxType extends AbstractEntity implements IReplicatable
{
	@Id
	@Column(name = "tt_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tt_id")
	@TableGenerator(name = "tt_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@OneToMany(cascade=CascadeType.ALL, fetch = EAGER, mappedBy = "taxType")
	private Collection<Tax> taxes = new Vector<Tax>();

	@Basic
	@Column(name = "tt_code")
	private String code;

	@Basic
	@Column(name = "tt_name")
	private String name;

	protected TaxType()
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
	 * @see ch.eugster.colibri.persistence.model.ITaxType#getCode()
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
	 * @see ch.eugster.colibri.persistence.model.ITaxType#getName()
	 */
	public String getName()
	{
		return this.valueOf(this.name);
	}

	public Collection<Tax> getTaxes()
	{
		return this.taxes;
	}

	public void removeTax(final Tax tax)
	{
		this.propertyChangeSupport.firePropertyChange("taxes", this.taxes, this.taxes.remove(tax));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ch.eugster.colibri.persistence.model.ITaxType#setCode(java.lang.String)
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
	 * ch.eugster.colibri.persistence.model.ITaxType#setName(java.lang.String)
	 */
	public void setName(final String name)
	{
		this.propertyChangeSupport.firePropertyChange("name", this.name, this.name = name);
	}

	public void setTaxes(final Collection<Tax> taxes)
	{
		this.propertyChangeSupport.firePropertyChange("taxes", this.taxes, this.taxes = taxes);
	}

	public static TaxType newInstance()
	{
		return (TaxType) AbstractEntity.newInstance(new TaxType());
	}
}
