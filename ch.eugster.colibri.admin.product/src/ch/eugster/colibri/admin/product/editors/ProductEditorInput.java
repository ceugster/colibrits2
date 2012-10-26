/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.ProductGroup;

public class ProductEditorInput extends AbstractEntityEditorInput<ProductGroup>
{
	public ProductEditorInput(final ProductGroup productGroup)
	{
		super(productGroup);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof ProductEditorInput)
		{
			final ProductEditorInput input = (ProductEditorInput) object;
			final ProductGroup productGroup = (ProductGroup) input.getAdapter(ProductGroup.class);
			if ((productGroup.getId() != null) && productGroup.getId().equals(entity.getId()))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter)
	{
		if (adapter.getName().equals(ProductGroup.class.getName()))
		{
			return entity;
		}
		else
		{
			return null;
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	@Override
	public String getName()
	{
		return entity.getCode();
	}

	@Override
	public String getToolTipText()
	{
		return entity.getName();
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
