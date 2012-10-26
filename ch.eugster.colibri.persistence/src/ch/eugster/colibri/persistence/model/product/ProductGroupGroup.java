/*
 * Created on 14.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.model.product;

public enum ProductGroupGroup
{
	SALES, EXPENSES, INTERNAL;

	public ProductGroupType[] getChildren()
	{
		if (equals(SALES))
		{
			return new ProductGroupType[] { ProductGroupType.SALES_RELATED, ProductGroupType.NON_SALES_RELATED };
		}
		else if (equals(EXPENSES))
		{
			return new ProductGroupType[] { ProductGroupType.EXPENSES_MATERIAL, ProductGroupType.EXPENSES_INVESTMENT };
		}
		else if (equals(INTERNAL))
		{
			return new ProductGroupType[] { ProductGroupType.ALLOCATION, ProductGroupType.WITHDRAWAL };
		}
		else
		{
			throw new RuntimeException("Invalid ProductGroup Group");
		}
	}

	@Override
	public String toString()
	{
		if (equals(SALES))
		{
			return "Einnahmen";
		}
		else if (equals(EXPENSES))
		{
			return "Ausgaben";
		}
		else if (equals(INTERNAL))
		{
			return "Einlagen/Entnahmen";
		}
		else
		{
			throw new RuntimeException("Invalid ProductGroup Group");
		}
	}
}
