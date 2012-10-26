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
@Table(name = "colibri_current_tax_code_mapping")
@AttributeOverrides({ @AttributeOverride(name = "timestamp", column = @Column(name = "ctcm_timestamp")),
		@AttributeOverride(name = "version", column = @Column(name = "ctcm_version")),
		@AttributeOverride(name = "update", column = @Column(name = "ctcm_update")),
		@AttributeOverride(name = "deleted", column = @Column(name = "ctcm_deleted")) })
public class CurrentTaxCodeMapping extends AbstractEntity implements IReplicationRelevant
{
	@Id
	@Column(name = "ctcm_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "ctcm_id")
	@TableGenerator(name = "ctcm_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "ctcm_ct_id", referencedColumnName = "ct_id")
	private CurrentTax currentTax;

	@Basic
	@Column(name = "ctcm_provider")
	private String provider;

	@Basic
	@Column(name = "ctcm_code")
	private String code;

	@Basic
	@Column(name = "ctcm_account")
	private String account;

	private CurrentTaxCodeMapping()
	{
		super();
	}

	private CurrentTaxCodeMapping(final CurrentTax currentTax)
	{
		this();
		this.setCurrentTax(currentTax);
	}

	public String getAccount()
	{
		return this.account;
	}

	public String getCode()
	{
		return this.valueOf(this.code);
	}

	public CurrentTax getCurrentTax()
	{
		return this.currentTax;
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

	public void setAccount(final String account)
	{
		this.account = account;
	}

	public void setCode(final String code)
	{
		this.propertyChangeSupport.firePropertyChange("code", this.code, this.code = code);
	}

	public void setCurrentTax(final CurrentTax currentTax)
	{
		this.propertyChangeSupport.firePropertyChange("currentTax", this.currentTax, this.currentTax = currentTax);
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

	public static CurrentTaxCodeMapping newInstance(final CurrentTax currentTax)
	{
		final CurrentTaxCodeMapping currentTaxCodeMapping = (CurrentTaxCodeMapping) AbstractEntity
				.newInstance(new CurrentTaxCodeMapping(currentTax));
		return currentTaxCodeMapping;
	}
}
