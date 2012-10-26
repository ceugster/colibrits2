package ch.eugster.colibri.report.receipt.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.User;

public class UserViewerFilter extends ViewerFilter
{
	private User filteredUser;

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element)
	{
		if (filteredUser != null)
		{
			if (filteredUser.getId() != null)
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
					if (receipt.getUser() != null)
					{
						return receipt.getUser().getId().equals(filteredUser.getId());
					}
				}
			}
		}
		return true;
	}

	public void setUser(User user)
	{
		filteredUser = user;
	}

}
