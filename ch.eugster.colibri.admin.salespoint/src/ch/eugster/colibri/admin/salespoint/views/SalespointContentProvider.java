/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.views;

import java.util.Collection;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class SalespointContentProvider implements ITreeContentProvider
{
	@Override
	public void dispose()
	{
	}

	@Override
	public Object[] getChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			final PersistenceService persistenceService = (PersistenceService) parent;
			if (persistenceService != null)
			{
				final SalespointQuery query = (SalespointQuery) persistenceService.getServerService().getQuery(Salespoint.class);
				final Collection<Salespoint> salespoints = query.selectAll(true);
				return salespoints.toArray(new Salespoint[0]);
			}
		}
		else if (parent instanceof Salespoint)
		{
			final Salespoint salespoint = (Salespoint) parent;
			return salespoint.getStocks().toArray(new Stock[0]);
		}

		return new Salespoint[0];
	}

	@Override
	public Object[] getElements(final Object element)
	{
		return this.getChildren(element);
	}

	@Override
	public Object getParent(final Object child)
	{
		if (child instanceof Stock)
		{
			final Stock stock = (Stock) child;
			return stock.getSalespoint();
		}

		return null;
	}

	@Override
	public boolean hasChildren(final Object parent)
	{
		if (parent instanceof PersistenceService)
		{
			final PersistenceService service = (PersistenceService) parent;
			final SalespointQuery query = (SalespointQuery) service.getServerService().getQuery(Salespoint.class);
			return query.countValid() > 0l;
		}
		else if (parent instanceof Salespoint)
		{
			final Salespoint salespoint = (Salespoint) parent;
			return salespoint.getStocks().size() > 0;
		}

		return false;
	}

	@Override
	public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput)
	{
	}

}
