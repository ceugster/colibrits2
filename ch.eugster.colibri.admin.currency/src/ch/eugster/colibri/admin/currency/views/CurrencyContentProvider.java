/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.currency.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.queries.CurrencyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CurrencyContentProvider implements IStructuredContentProvider
{
	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getElements(final Object element)
	{
		if (element instanceof PersistenceService)
		{
			final PersistenceService service = (PersistenceService) element;
			final CurrencyQuery query = (CurrencyQuery) service.getServerService().getQuery(Currency.class);
			return query.selectAll(true).toArray(new Currency[0]);
		}
		return new Currency[0];
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

}
