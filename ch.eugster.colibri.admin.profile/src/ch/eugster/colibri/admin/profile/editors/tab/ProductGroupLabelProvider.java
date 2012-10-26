/*
 * Created on 30.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import org.eclipse.jface.viewers.LabelProvider;

import ch.eugster.colibri.persistence.model.ProductGroup;

public class ProductGroupLabelProvider extends LabelProvider
{
	@Override
	public String getText(final Object element)
	{
		if (element instanceof ProductGroup)
		{
			final ProductGroup productGroup = (ProductGroup) element;
			return productGroup.getName();
		}
		return "";
	}
}
