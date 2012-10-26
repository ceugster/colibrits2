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
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.queries.CurrentTaxQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CurrentTaxReplicator extends AbstractEntityReplicator<CurrentTax>
{
	public CurrentTaxReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{
		int i = 0;

		final CurrentTaxQuery query = (CurrentTaxQuery) this.persistenceService.getServerService().getQuery(CurrentTax.class);
		final Collection<CurrentTax> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final CurrentTax source : sources)
			{
				CurrentTax target = (CurrentTax) this.persistenceService.getCacheService().find(CurrentTax.class, source.getId());
				if ((target == null) || (target.getUpdate() != source.getVersion()))
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
					target = (CurrentTax) this.persistenceService.getCacheService().merge(target);
					target.getTax().addCurrentTax(target);
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
	protected CurrentTax replicate(final CurrentTax source)
	{
		final Tax tax = (Tax) this.persistenceService.getCacheService().find(Tax.class, source.getTax().getId());
		final CurrentTax target = this.replicate(source, CurrentTax.newInstance(tax));
		return target;
	}

	@Override
	protected CurrentTax replicate(final CurrentTax source, CurrentTax target)
	{
		target = super.replicate(source, target);
		target.setPercentage(source.getPercentage());
		target.setValidFrom(source.getValidFrom());
		return target;
	}
}
