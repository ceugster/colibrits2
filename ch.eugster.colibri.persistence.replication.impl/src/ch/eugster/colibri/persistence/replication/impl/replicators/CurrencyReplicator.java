/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.queries.CurrencyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CurrencyReplicator extends AbstractEntityReplicator<Currency>
{
	public CurrencyReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final CurrencyQuery query = (CurrencyQuery) this.persistenceService.getServerService().getQuery(Currency.class);
		final Collection<Currency> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Currency source : sources)
			{
				Currency target = (Currency) this.persistenceService.getCacheService().find(Currency.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						target = this.replicate(source, target);
					}
					this.persistenceService.getCacheService().merge(target);
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
	protected Currency replicate(final Currency source)
	{
		final Currency target = this.replicate(source, Currency.newInstance());
		return target;
	}

	@Override
	protected Currency replicate(final Currency source, Currency target)
	{
		target = super.replicate(source, target);
		target.setCode(source.getCode());
		target.setName(source.getName());
		target.setQuotation(source.getQuotation());
		target.setRegion(source.getRegion());
		target.setRoundFactor(source.getRoundFactor());
		return target;
	}
}
