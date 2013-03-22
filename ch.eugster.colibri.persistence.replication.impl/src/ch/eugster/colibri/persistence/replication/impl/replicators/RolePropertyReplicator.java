/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.model.RoleProperty;
import ch.eugster.colibri.persistence.queries.RolePropertyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class RolePropertyReplicator extends AbstractEntityReplicator<RoleProperty>
{
	public RolePropertyReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final RolePropertyQuery query = (RolePropertyQuery) this.persistenceService.getServerService().getQuery(RoleProperty.class);
		final Collection<RoleProperty> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final RoleProperty source : sources)
			{
				RoleProperty target = (RoleProperty) this.persistenceService.getCacheService().find(RoleProperty.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getRole().getId().equals(source.getRole().getId()))
						{
							final Role role = (Role) this.persistenceService.getCacheService().find(Role.class, source.getRole().getId());
							target.setRole(role);
						}
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
	protected RoleProperty replicate(final RoleProperty source)
	{
		final Role role = (Role) this.persistenceService.getCacheService().find(Role.class, source.getRole().getId());
		final RoleProperty target = this.replicate(source, RoleProperty.newInstance(role));
		return target;
	}

	@Override
	protected RoleProperty replicate(final RoleProperty source, RoleProperty target)
	{
		target = super.replicate(source, target);
		target.setKey(source.getKey());
		target.setValue(source.getValue());
		return target;
	}
}
