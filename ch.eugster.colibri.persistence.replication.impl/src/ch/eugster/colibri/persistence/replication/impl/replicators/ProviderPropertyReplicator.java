/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProviderPropertyReplicator extends AbstractEntityReplicator<ProviderProperty>
{
	public ProviderPropertyReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final ProviderPropertyQuery query = (ProviderPropertyQuery) this.persistenceService.getServerService().getQuery(ProviderProperty.class);
		final Collection<ProviderProperty> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final ProviderProperty source : sources)
			{
				ProviderProperty target = (ProviderProperty) this.persistenceService.getCacheService().find(ProviderProperty.class, source.getId());
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
	protected ProviderProperty replicate(final ProviderProperty source)
	{
		final ProviderProperty target = this.replicate(source, ProviderProperty.newInstance());
		return target;
	}

	@Override
	protected ProviderProperty replicate(final ProviderProperty source, ProviderProperty target)
	{
		target = super.replicate(source, target);
		target.setKey(source.getKey());
		target.setProvider(source.getProvider());
		target.setValue(source.getValue());
		target.setSalespoint(source.getSalespoint());
		return target;
	}
}
