/*
 * Created on 14.05.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.TableGenerator;

import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

@MappedSuperclass
public abstract class SettlementAbstractPosition extends AbstractEntity implements IPrintable
{
	@Id
	@Column(name = "sepo_id")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "sepo_id")
	@TableGenerator(name = "sepo_id", table = "colibri_sequence", pkColumnName = "sq_key", valueColumnName = "sq_val")
	protected Long id;

	@ManyToOne(optional = false, cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	@JoinColumn(name = "sepo_se_id", referencedColumnName = "se_id")
	protected Settlement settlement;

	@ManyToOne(optional = false)
	@JoinColumn(name = "sepo_pg_id", referencedColumnName = "pg_id")
	protected ProductGroup productGroup;

	@Basic
	@Column(name = "sepo_product_group_type")
	@Enumerated(EnumType.ORDINAL)
	private ProductGroupType productGroupType;

	@JoinColumn(name = "sepo_dcu_id", referencedColumnName = "cu_id")
	@OneToOne(optional = true)
	protected Currency defaultCurrency;

	@Basic
	@Column(name = "sepo_default_currency_amount", columnDefinition = "DECIMAL(18, 6)")
	protected double defaultCurrencyAmount;

	@Basic
	@Column(name = "sepo_code")
	protected String code;

	@Basic
	@Column(name = "sepo_name")
	protected String name;

	public String getCode()
	{
		return this.code;
	}

	public void setCode(String code)
	{
		this.code = code;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	protected SettlementAbstractPosition()
	{
		super();
	}

	protected SettlementAbstractPosition(final Settlement settlement, final ProductGroup productGroup,
			final Currency currencyDc)
	{
		super();
		this.setSettlement(settlement);
		this.setProductGroup(productGroup);
		this.setDefaultCurrency(currencyDc);
	}

	@Override
	public Long getId()
	{
		return this.id;
	}

	@Override
	public void setId(final Long id)
	{
		this.id = id;
	}

	public Currency getDefaultCurrency()
	{
		return this.defaultCurrency;
	}

	public double getDefaultCurrencyAmount()
	{
		return this.defaultCurrencyAmount;
	}

	public ProductGroup getProductGroup()
	{
		return this.productGroup;
	}

	public Settlement getSettlement()
	{
		return this.settlement;
	}

	public void setDefaultCurrency(final Currency defaultCurrency)
	{
		this.defaultCurrency = defaultCurrency;
	}

	public void setDefaultCurrencyAmount(final double amount)
	{
		this.defaultCurrencyAmount = amount;
	}

	public void setProductGroup(final ProductGroup productGroup)
	{
		this.productGroup = productGroup;
		this.setProductGroupType(productGroup == null ? null : productGroup.getProductGroupType());
		String code = null;
		if (productGroup.getProductGroupMappings().isEmpty())
		{
			if (productGroup.getCode() != null && !productGroup.getCode().equals(productGroup.getName()))
			{
				code = productGroup.getCode();
			}
		}
		else
		{
			code = productGroup.getProductGroupMappings().iterator().next().getExternalProductGroup().getCode();
		}
		this.setCode(code == null ? null : code);
		this.setName(productGroup == null ? null : productGroup.getName());
	}

	public void setSettlement(final Settlement settlement)
	{
		this.settlement = settlement;
	}

	public void setProductGroupType(ProductGroupType productGroupType)
	{
		this.productGroupType = productGroupType;
	}

	public ProductGroupType getProductGroupType()
	{
		return productGroupType;
	}
}
