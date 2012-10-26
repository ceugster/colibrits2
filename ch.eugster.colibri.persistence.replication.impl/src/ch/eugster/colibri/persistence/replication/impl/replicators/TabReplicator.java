/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.queries.TabQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TabReplicator extends AbstractEntityReplicator<Tab>
{
	public TabReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{

		int i = 0;

		final TabQuery query = (TabQuery) this.persistenceService.getServerService().getQuery(Tab.class);
		final Collection<Tab> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Tab source : sources)
			{
				Tab target = (Tab) this.persistenceService.getCacheService().find(Tab.class, source.getId());
				if ((target == null) || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getConfigurable().getId().equals(source.getConfigurable().getId()))
						{
							final Configurable configurable = (Configurable) this.persistenceService.getCacheService().find(Configurable.class, source.getConfigurable()
									.getId());
							target.setConfigurable(configurable);
						}
						target = this.replicate(source, target);
					}
					target = (Tab) this.persistenceService.getCacheService().merge(target);
					target.getConfigurable().addTab(target);
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
	protected Tab replicate(final Tab source)
	{
		final Configurable configurable = (Configurable) this.persistenceService.getCacheService().find(Configurable.class, source.getConfigurable().getId());
		final Tab target = this.replicate(source, Tab.newInstance(configurable));
		return target;
	}

	@Override
	protected Tab replicate(final Tab source, Tab target)
	{
		target = super.replicate(source, target);
		target.setCols(source.getCols());
		target.setName(source.getName());
		target.setPos(source.getPos());
		target.setRows(source.getRows());
		return target;
	}
}
