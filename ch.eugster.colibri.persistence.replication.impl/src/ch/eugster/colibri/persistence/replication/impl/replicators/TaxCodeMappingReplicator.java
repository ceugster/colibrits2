/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.queries.TaxCodeMappingQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TaxCodeMappingReplicator extends AbstractEntityReplicator<TaxCodeMapping>
{
	public TaxCodeMappingReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final TaxCodeMappingQuery query = (TaxCodeMappingQuery) this.persistenceService.getServerService().getQuery(TaxCodeMapping.class);
		final Collection<TaxCodeMapping> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final TaxCodeMapping source : sources)
			{
				TaxCodeMapping target = (TaxCodeMapping) this.persistenceService.getCacheService().find(TaxCodeMapping.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getTax().getId().equals(source.getTax().getId()))
						{
							final Tax tax = (Tax) this.persistenceService.getCacheService().find(Tax.class, source.getTax().getId());
							target.setTax(tax);
						}
						target = this.replicate(source, target);
					}
					target = (TaxCodeMapping) merge(target);
					target.getTax().addTaxCodeMapping(target);
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
	protected TaxCodeMapping replicate(final TaxCodeMapping source)
	{
		final Tax tax = (Tax) this.persistenceService.getCacheService().find(Tax.class, source.getTax().getId());
		final TaxCodeMapping target = this.replicate(source, TaxCodeMapping.newInstance(tax));
		return target;
	}

	@Override
	protected TaxCodeMapping replicate(final TaxCodeMapping source, TaxCodeMapping target)
	{
		target = super.replicate(source, target);
		target.setAccount(source.getAccount());
		target.setCode(source.getCode());
		target.setProvider(source.getProvider());
		return target;
	}
}
