/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.CurrentTaxCodeMapping;
import ch.eugster.colibri.persistence.queries.CurrentTaxCodeMappingQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CurrentTaxCodeMappingReplicator extends AbstractEntityReplicator<CurrentTaxCodeMapping>
{
	public CurrentTaxCodeMappingReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final CurrentTaxCodeMappingQuery query = (CurrentTaxCodeMappingQuery) this.persistenceService.getServerService().getQuery(CurrentTaxCodeMapping.class);
		final Collection<CurrentTaxCodeMapping> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final CurrentTaxCodeMapping source : sources)
			{
				CurrentTaxCodeMapping target = (CurrentTaxCodeMapping) this.persistenceService.getCacheService().find(CurrentTaxCodeMapping.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getCurrentTax().getId().equals(source.getCurrentTax().getId()))
						{
							final CurrentTax currentTax = (CurrentTax) this.persistenceService.getCacheService().find(CurrentTax.class, source.getCurrentTax().getId());
							target.setCurrentTax(currentTax);
						}
						target = this.replicate(source, target);
					}
					target = (CurrentTaxCodeMapping) merge(target);
					target.getCurrentTax().addCurrentTaxCodeMapping(target);
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
	protected CurrentTaxCodeMapping replicate(final CurrentTaxCodeMapping source)
	{
		final CurrentTax currentTax = (CurrentTax) this.persistenceService.getCacheService().find(CurrentTax.class, source.getCurrentTax().getId());
		final CurrentTaxCodeMapping target = this.replicate(source, CurrentTaxCodeMapping.newInstance(currentTax));
		return target;
	}

	@Override
	protected CurrentTaxCodeMapping replicate(final CurrentTaxCodeMapping source, CurrentTaxCodeMapping target)
	{
		target = super.replicate(source, target);
		target.setAccount(source.getAccount());
		target.setCode(source.getCode());
		target.setProvider(source.getProvider());
		return target;
	}
}
