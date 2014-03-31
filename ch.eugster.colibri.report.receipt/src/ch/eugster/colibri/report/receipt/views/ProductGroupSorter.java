package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;

import ch.eugster.colibri.persistence.model.ProductGroup;

public class ProductGroupSorter extends ViewerSorter
{

	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2)
	{
		if ((e1 instanceof ProductGroup) && (e2 instanceof ProductGroup))
		{
			final ProductGroup pt1 = (ProductGroup) e1;
			final ProductGroup pt2 = (ProductGroup) e2;

			return pt1.getName().compareTo(pt2.getName());
		}
		return super.compare(viewer, e1, e2);
	}

}
