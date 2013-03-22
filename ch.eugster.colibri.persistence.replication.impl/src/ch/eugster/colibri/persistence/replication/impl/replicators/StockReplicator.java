/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.queries.StockQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class StockReplicator extends AbstractEntityReplicator<Stock>
{
	public StockReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final StockQuery query = (StockQuery) this.persistenceService.getServerService().getQuery(Stock.class);
		final Collection<Stock> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Stock source : sources)
			{
				Stock target = (Stock) this.persistenceService.getCacheService().find(Stock.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getSalespoint().getId().equals(source.getSalespoint().getId()))
						{
							final Salespoint salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
							target.setSalespoint(salespoint);
						}
						target = this.replicate(source, target);
					}
					target = (Stock) this.persistenceService.getCacheService().merge(target);
					target.getSalespoint().addStock(target);
					target.getPaymentType().addStock(target);
				}
				if (monitor != null)
				{
					i++;
					monitor.worked(i);
				}
			}
		}
		finally
		{
			if (monitor != null)
			{
				monitor.done();
			}
		}

	}

	@Override
	protected Stock replicate(final Stock source)
	{
		final Salespoint salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
		final Stock target = this.replicate(source, Stock.newInstance(salespoint));
		return target;
	}

	@Override
	protected Stock replicate(final Stock source, Stock target)
	{
		target = super.replicate(source, target);
		target.setAmount(source.getAmount());
		target.setPaymentType((PaymentType) this.persistenceService.getCacheService().find(PaymentType.class, source.getPaymentType().getId()));
		target.setVariable(source.isVariable());
		return target;
	}
}
