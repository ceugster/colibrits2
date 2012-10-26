package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementViewerFilter extends ViewerFilter
{
	private Settlement filteredSettlement;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		boolean show = true;
		if (filteredSettlement != null)
		{
			if (filteredSettlement.getId() != null)
			{
				Receipt receipt = null;
				if (element instanceof Receipt)
				{
					receipt = (Receipt) element;
				}
				else if (element instanceof Position)
				{
					Position position = (Position) element;
					receipt = position.getReceipt();
				}
				else if (element instanceof Payment)
				{
					Payment payment = (Payment) element;
					receipt = payment.getReceipt();
				}
				if (receipt != null)
				{
					show = receipt.getSettlement().getId().equals(filteredSettlement.getId());
				}
			}
		}
		return show;
	}

	public void setSettlement(Settlement settlement)
	{
		filteredSettlement = settlement;
	}

}
