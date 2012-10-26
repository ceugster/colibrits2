package ch.eugster.colibri.report.settlement.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementNotSettledFilter extends ViewerFilter
{

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (element instanceof Settlement)
		{
			Settlement settlement = (Settlement) element;
			if (settlement.isDeleted() || settlement.getSalespoint().isDeleted())
			{
				return false;
			}
			if (settlement.getSettled() == null)
			{
				return false;
			}
		}
		return true;
	}

}
