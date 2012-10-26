package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.report.receipt.views.ReceiptFilterView.ReceiptStateSelector;

public class ReceiptStateViewerFilter extends ViewerFilter
{
	private ReceiptStateSelector filteredState;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (filteredState != null)
		{
			if (!filteredState.equals(ReceiptStateSelector.ALL))
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
					return receipt.getState().equals(filteredState.getState());
				}
			}
		}
		return true;
	}

	public void setReceiptState(ReceiptStateSelector stateSelector)
	{
		filteredState = stateSelector;
	}

}
