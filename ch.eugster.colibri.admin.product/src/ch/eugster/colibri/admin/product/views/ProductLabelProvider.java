/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.views;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class ProductLabelProvider extends LabelProvider implements ILabelProvider
{
	public static final NumberFormat nf = NumberFormat.getCurrencyInstance();

	@Override
	public Image getImage(final Object element)
	{
		if (element instanceof ProductGroupGroup)
		{
			if (((ProductGroupGroup) element).equals(ProductGroupGroup.SALES))
			{
				return Activator.getDefault().getImageRegistry().get("books.gif");
			}
			else if (((ProductGroupGroup) element).equals(ProductGroupGroup.EXPENSES))
			{
				return Activator.getDefault().getImageRegistry().get("money_violett.png");
			}
			else if (((ProductGroupType) element).equals(ProductGroupGroup.INTERNAL))
			{
				return Activator.getDefault().getImageRegistry().get("money_gelb.png");
			}
		}
		else if (element instanceof ProductGroupType)
		{
			if (((ProductGroupType) element).equals(ProductGroupType.SALES_RELATED))
			{
				return Activator.getDefault().getImageRegistry().get("books.gif");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.NON_SALES_RELATED))
			{
				return Activator.getDefault().getImageRegistry().get("sonstiges.gif");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.EXPENSES_MATERIAL))
			{
				return Activator.getDefault().getImageRegistry().get("money_violett.png");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.EXPENSES_INVESTMENT))
			{
				return Activator.getDefault().getImageRegistry().get("money_blau.png");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.ALLOCATION))
			{
				return Activator.getDefault().getImageRegistry().get("money_gelb.png");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.WITHDRAWAL))
			{
				return Activator.getDefault().getImageRegistry().get("money_rot.png");
			}
		}
		else if (element instanceof ProductGroup)
		{
			final ProductGroupType type = ((ProductGroup) element).getProductGroupType();
			ProductGroup productGroup = (ProductGroup) element;
			if (productGroup.getCommonSettings().getPayedInvoice() != null)
			{
				if (productGroup.getCommonSettings().getPayedInvoice().getId().equals(productGroup.getId()))
				{
					return Activator.getDefault().getImageRegistry().get("INVOICE");
				}
				
			}
			else if (type.equals(ProductGroupType.SALES_RELATED))
			{
				return Activator.getDefault().getImageRegistry().get("openbook.gif");
			}
			else if (type.equals(ProductGroupType.NON_SALES_RELATED))
			{
				return Activator.getDefault().getImageRegistry().get("sonstiges.gif");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.EXPENSES_MATERIAL))
			{
				return Activator.getDefault().getImageRegistry().get("money_gray.png");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.EXPENSES_INVESTMENT))
			{
				return Activator.getDefault().getImageRegistry().get("money_gray.png");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.ALLOCATION))
			{
				return Activator.getDefault().getImageRegistry().get("money_gray.png");
			}
			else if (((ProductGroupType) element).equals(ProductGroupType.WITHDRAWAL))
			{
				return Activator.getDefault().getImageRegistry().get("money_gray.png");
			}
		}

		return null;
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof ProductGroupType)
		{
			final ProductGroupType productGroupType = (ProductGroupType) element;
			return productGroupType.toCode();
		}
		if (element instanceof ProductGroup)
		{
			StringBuilder sb = new StringBuilder();
			final ProductGroup productGroup = (ProductGroup) element;
			if (productGroup.getCode().toLowerCase().equals(productGroup.getName().toLowerCase()))
			{
				sb = sb.append(productGroup.getCode());
			}
			else
			{
				sb = sb.append(productGroup.getCode() + " - " + productGroup.getName());
			}
			return sb.toString();
		}

		return "";
	}
}
