/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class KeyTypeLabelProvider extends LabelProvider implements ILabelProvider
{
	public static final NumberFormat nf = NumberFormat.getCurrencyInstance();

	@Override
	public Image getImage(final Object element)
	{
		return null;
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof KeyType)
		{
			return ((KeyType) element).toString();
		}
		else if (element instanceof ProductGroupGroup)
		{
			return ((ProductGroupGroup) element).toString();
		}
		else if (element instanceof ProductGroupType)
		{
			return ((ProductGroupType) element).toString();
		}
		else if (element instanceof ProductGroup)
		{
			return ((ProductGroup) element).getName();
		}
		else if (element instanceof PaymentTypeGroup)
		{
			return ((PaymentTypeGroup) element).toString();
		}
		else if (element instanceof PaymentType)
		{
			return ((PaymentType) element).getName();
		}
		else if (element instanceof TaxRate)
		{
			return ((TaxRate) element).getName();
		}
		else if (element instanceof Tax)
		{
			return ((Tax) element).getTaxType().getName();
		}
		else if (element instanceof Position.Option)
		{
			return ((Position.Option) element).toString();
		}
		else if (element instanceof FunctionType)
		{
			return ((FunctionType) element).toString();
		}

		return "";
	}
}
