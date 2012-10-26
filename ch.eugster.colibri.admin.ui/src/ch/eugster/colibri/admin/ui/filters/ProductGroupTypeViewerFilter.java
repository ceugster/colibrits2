package ch.eugster.colibri.admin.ui.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class ProductGroupTypeViewerFilter extends ViewerFilter
{
	private final ProductGroupType productGroupType;

	public ProductGroupTypeViewerFilter(ProductGroupType productGroupType)
	{
		this.productGroupType = productGroupType;
	}

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element)
	{
		if (element instanceof ProductGroup)
		{
			final ProductGroup productGroup = (ProductGroup) element;
			return productGroup.getProductGroupType().equals(productGroupType);
		}
		return true;
	}

}
