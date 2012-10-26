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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

@Entity
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "tcm_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "tcm_version")),
		@AttributeOverride(name = "update", column = @Column(name = "tcm_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "tcm_deleted")) })
@Table(name = "colibri_tax_code_mapping")
public class TaxCodeMapping extends AbstractEntity implements IReplicationRelevant
{
	@Id
	@Column(name = "tcm_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "tcm_id")
	@TableGenerator(name = "tcm_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "tcm_tx_id", referencedColumnName = "tx_id")
	private Tax tax;

	@Basic
	@Column(name = "tcm_provider")
	private String provider;

	@Basic
	@Column(name = "tcm_code")
	private String code;

	@Basic
	@Column(name = "tcm_account")
	private String account;

	protected TaxCodeMapping()
	{
		super();
	}

	protected TaxCodeMapping(final Tax tax)
	{
		this.setTax(tax);
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

	public Tax getTax()
	{
		return this.tax;
	}

	public void setAccount(final String account)
	{
		this.account = account;
	}

	public void setCode(final String code)
	{
		this.propertyChangeSupport.firePropertyChange("code", this.code, this.code = code);
	}

	@Override
	public void setId(final Long id)
	{
		this.propertyChangeSupport.firePropertyChange("id", this.id, this.id = id);
	}

	public void setProvider(final String provider)
	{
		this.propertyChangeSupport.firePropertyChange("provider", this.provider, this.provider = provider);
	}

	public void setTax(final Tax tax)
	{
		this.propertyChangeSupport.firePropertyChange("tax", this.tax, this.tax = tax);
	}

	public static TaxCodeMapping newInstance(final Tax tax)
	{
		return (TaxCodeMapping) AbstractEntity.newInstance(new TaxCodeMapping(tax));
	}
}
