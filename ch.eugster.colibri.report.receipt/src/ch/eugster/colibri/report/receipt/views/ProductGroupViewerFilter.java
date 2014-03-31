package ch.eugster.colibri.report.receipt.views;

import java.util.Collection;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;

public class ProductGroupViewerFilter extends ViewerFilter
{
	private ProductGroup filteredProductGroup;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		boolean show = true;
		if (filteredProductGroup != null)
		{
			if (filteredProductGroup.getId() != null)
			{
				Receipt receipt = null;
				if (element instanceof Receipt)
				{
					receipt = (Receipt) element;
					boolean found = false;
					Collection<Position> positions = receipt.getPositions();
					for (Position position : positions)
					{
						if (position.getProductGroup().getId().equals(filteredProductGroup.getId()))
						{
							found = true;
						}
					}
					show = found;
				}
				else if (element instanceof Position)
				{
					Position position = (Position) element;
					return position.getProductGroup().getId().equals(filteredProductGroup.getId());
				}
			}
		}
		return show;
	}

	public void setProductGroup(ProductGroup ProductGroup)
	{
		filteredProductGroup = ProductGroup;
	}

}
