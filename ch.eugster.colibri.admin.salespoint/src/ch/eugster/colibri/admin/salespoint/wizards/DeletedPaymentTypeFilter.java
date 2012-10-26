/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.wizards;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.persistence.model.PaymentType;

public class DeletedPaymentTypeFilter extends ViewerFilter
{

	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element)
	{
		if (element instanceof PaymentType)
		{
			final PaymentType paymentType = (PaymentType) element;
			return !paymentType.isDeleted();
		}

		return false;
	}

}
