/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TaxTypeReplicator extends AbstractEntityReplicator<TaxType>
{
	public TaxTypeReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final TaxTypeQuery query = (TaxTypeQuery) this.persistenceService.getServerService().getQuery(TaxType.class);
		final Collection<TaxType> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final TaxType source : sources)
			{
				TaxType target = (TaxType) this.persistenceService.getCacheService().find(TaxType.class, source.getId());
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
	protected TaxType replicate(final TaxType source)
	{
		final TaxType target = this.replicate(source, TaxType.newInstance());
		return target;
	}

	@Override
	protected TaxType replicate(final TaxType source, TaxType target)
	{
		target = super.replicate(source, target);
		target.setCode(source.getCode());
		target.setName(source.getName());
		return target;
	}
}
