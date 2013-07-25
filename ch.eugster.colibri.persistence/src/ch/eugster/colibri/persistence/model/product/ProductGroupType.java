/*
 * Created on 14.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model.product;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;

public enum ProductGroupType implements IProductGroupType
{
	SALES_RELATED, NON_SALES_RELATED, EXPENSES_MATERIAL, EXPENSES_INVESTMENT, ALLOCATION, WITHDRAWAL;

	// private static final String SALES_RELATED_NAME_CODE = "Warengruppen";
	//
	// private static final String NON_SALES_RELATED_NAME_CODE =
	// "Umsatzneutrale Einnahmen";
	//
	// private static final String EXPENSES_MATERIAL_NAME_CODE =
	// "Ausgaben Mat./DL";
	//
	// private static final String EXPENSES_INVESTMENT_NAME_CODE =
	// "Ausgaben Inv.";
	//
	// private static final String ALLOCATION_NAME_CODE = "Geldeinlagen";
	//
	// private static final String WITHDRAWAL_NAME_CODE = "Geldentnahmen";

	private static final String SALES_RELATED_NAME_PL = "Warengruppen";

	private static final String NON_SALES_RELATED_NAME_PL = "Sonstiges (Umsatzneutral)";

	private static final String EXPENSES_MATERIAL_NAME_PL = "Ausgaben Mat./DL";

	private static final String EXPENSES_INVESTMENT_NAME_PL = "Ausgaben Inv.";

	private static final String ALLOCATION_NAME_PL = "Geldeinlagen";

	private static final String WITHDRAWAL_NAME_PL = "Geldentnahmen";

	private static final String SALES_RELATED_NAME = "Warengruppe";

	private static final String NON_SALES_RELATED_NAME = "Sonstiges";

	private static final String EXPENSES_MATERIAL_NAME = "Ausgabe Mat./DL";

	private static final String EXPENSES_INVESTMENT_NAME = "Ausgabe Inv.";

	private static final String ALLOCATION_NAME = "Geldeinlage";

	private static final String WITHDRAWAL_NAME = "Geldentnahme";

	private Collection<ProductGroup> productGroups;

	@Override
	public String asPlural()
	{
		if (this.equals(SALES_RELATED))
		{
			return ProductGroupType.SALES_RELATED_NAME_PL;
		}
		else if (this.equals(NON_SALES_RELATED))
		{
			return ProductGroupType.NON_SALES_RELATED_NAME_PL;
		}
		else if (this.equals(EXPENSES_MATERIAL))
		{
			return ProductGroupType.EXPENSES_MATERIAL_NAME_PL;
		}
		else if (this.equals(EXPENSES_INVESTMENT))
		{
			return ProductGroupType.EXPENSES_INVESTMENT_NAME_PL;
		}
		else if (this.equals(ALLOCATION))
		{
			return ProductGroupType.ALLOCATION_NAME_PL;
		}
		else if (this.equals(WITHDRAWAL))
		{
			return ProductGroupType.WITHDRAWAL_NAME_PL;
		}
		else
		{
			throw new RuntimeException("Invalid ProductGroupType");
		}
	}

	public Collection<ProductGroup> getChildren()
	{
		return this.productGroups;
	}

	public Option[] getOptions()
	{
		switch (this)
		{
			case SALES_RELATED:
			{
				return new Option[] { Position.Option.ARTICLE, Position.Option.ORDERED };
			}
			case NON_SALES_RELATED:
			{
				return new Option[] { Position.Option.ARTICLE, Position.Option.ORDERED, Position.Option.PAYED_INVOICE };
			}
			default:
			{
				return new Option[0];
			}
		}
	}

	@Override
	public ProductGroupGroup getParent()
	{
		if (this.equals(SALES_RELATED))
		{
			return ProductGroupGroup.SALES;
		}
		else if (this.equals(NON_SALES_RELATED))
		{
			return ProductGroupGroup.SALES;
		}
		else if (this.equals(EXPENSES_MATERIAL))
		{
			return ProductGroupGroup.EXPENSES;
		}
		else if (this.equals(EXPENSES_INVESTMENT))
		{
			return ProductGroupGroup.EXPENSES;
		}
		else if (this.equals(ALLOCATION))
		{
			return ProductGroupGroup.INTERNAL;
		}
		else if (this.equals(WITHDRAWAL))
		{
			return ProductGroupGroup.INTERNAL;
		}
		else
		{
			throw new RuntimeException("Invalid ProductGroupType");
		}
	}

	public Long getTaxTypeId()
	{
		if (this.equals(SALES_RELATED))
		{
			return Long.valueOf(1l);
		}
		else if (this.equals(NON_SALES_RELATED))
		{
			return Long.valueOf(1L);
		}
		else if (this.equals(EXPENSES_MATERIAL))
		{
			return Long.valueOf(2l);
		}
		else if (this.equals(EXPENSES_INVESTMENT))
		{
			return Long.valueOf(3l);
		}
		else if (this.equals(ALLOCATION))
		{
			return null;
		}
		else if (this.equals(WITHDRAWAL))
		{
			return null;
		}
		else
		{
			return null;
		}

	}

	@Override
	public boolean isAsDefaultProductGroupAvailable()
	{
		return this.equals(SALES_RELATED);
	}
	
	public boolean isMappable()
	{
		return true;
	}

	public void setChildren(final Collection<ProductGroup> productGroups)
	{
		this.productGroups = productGroups;
	}
	
	public void addChild(ProductGroup productGroup)
	{
		if (this.productGroups != null)
		{
			if (!this.productGroups.contains(productGroup))
			{
				this.productGroups.add(productGroup);
			}
		}
	}

	public void removeChild(ProductGroup productGroup)
	{
		if (this.productGroups != null)
		{
			if (this.productGroups.contains(productGroup))
			{
				this.productGroups.remove(productGroup);
			}
		}
	}

	@Override
	public String toCode()
	{
		if (this.equals(SALES_RELATED))
		{
			return ProductGroupType.SALES_RELATED_NAME;
		}
		else if (this.equals(NON_SALES_RELATED))
		{
			return ProductGroupType.NON_SALES_RELATED_NAME;
		}
		else if (this.equals(EXPENSES_MATERIAL))
		{
			return ProductGroupType.EXPENSES_MATERIAL_NAME;
		}
		else if (this.equals(EXPENSES_INVESTMENT))
		{
			return ProductGroupType.EXPENSES_INVESTMENT_NAME;
		}
		else if (this.equals(ALLOCATION))
		{
			return ProductGroupType.ALLOCATION_NAME;
		}
		else if (this.equals(WITHDRAWAL))
		{
			return ProductGroupType.WITHDRAWAL_NAME;
		}
		else
		{
			throw new RuntimeException("Invalid ProductGroupType");
		}
	}

	@Override
	public String toString()
	{
		if (this.equals(SALES_RELATED))
		{
			return ProductGroupType.SALES_RELATED_NAME;
		}
		else if (this.equals(NON_SALES_RELATED))
		{
			return ProductGroupType.NON_SALES_RELATED_NAME;
		}
		else if (this.equals(EXPENSES_MATERIAL))
		{
			return ProductGroupType.EXPENSES_MATERIAL_NAME;
		}
		else if (this.equals(EXPENSES_INVESTMENT))
		{
			return ProductGroupType.EXPENSES_INVESTMENT_NAME;
		}
		else if (this.equals(ALLOCATION))
		{
			return ProductGroupType.ALLOCATION_NAME;
		}
		else if (this.equals(WITHDRAWAL))
		{
			return ProductGroupType.WITHDRAWAL_NAME;
		}
		else
		{
			throw new RuntimeException("Invalid ProductGroupType");
		}
	}

	@Override
	public double computePrice(double price)
	{
		if (this.equals(SALES_RELATED))
		{
			return Math.abs(price);
		}
		else if (this.equals(NON_SALES_RELATED))
		{
			return Math.abs(price);
		}
		else if (this.equals(EXPENSES_MATERIAL))
		{
			return -Math.abs(price);
		}
		else if (this.equals(EXPENSES_INVESTMENT))
		{
			return -Math.abs(price);
		}
		else if (this.equals(ALLOCATION))
		{
			return Math.abs(price);
		}
		else if (this.equals(WITHDRAWAL))
		{
			return -Math.abs(price);
		}
		else
		{
			throw new RuntimeException("Invalid ProductGroupType");
		}
	}
	
	public static ProductGroupType[] restitutionAffectedProductGroupTypes()
	{
		List<ProductGroupType> productGroupTypes = new ArrayList<ProductGroupType>();
		productGroupTypes.add(ProductGroupType.SALES_RELATED);
		productGroupTypes.add(ProductGroupType.NON_SALES_RELATED);
		return productGroupTypes.toArray(new ProductGroupType[0]);
	}
}
