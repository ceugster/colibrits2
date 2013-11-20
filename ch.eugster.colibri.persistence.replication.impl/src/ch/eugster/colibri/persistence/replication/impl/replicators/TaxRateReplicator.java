/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TaxRateReplicator extends AbstractEntityReplicator<TaxRate>
{
	public TaxRateReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final TaxRateQuery query = (TaxRateQuery) this.persistenceService.getServerService().getQuery(TaxRate.class);
		final Collection<TaxRate> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final TaxRate source : sources)
			{
				TaxRate target = (TaxRate) this.persistenceService.getCacheService().find(TaxRate.class, source.getId());
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
					merge(target);
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
	protected TaxRate replicate(final TaxRate source)
	{
		final TaxRate target = this.replicate(source, TaxRate.newInstance());
		return target;
	}

	@Override
	protected TaxRate replicate(final TaxRate source, TaxRate target)
	{
		target = super.replicate(source, target);
		target.setCode(source.getCode());
		target.setName(source.getName());
		return target;
	}
}
