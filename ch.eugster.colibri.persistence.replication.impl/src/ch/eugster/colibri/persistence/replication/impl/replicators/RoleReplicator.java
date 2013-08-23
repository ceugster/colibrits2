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
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.queries.RoleQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class RoleReplicator extends AbstractEntityReplicator<Role>
{
	public RoleReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final RoleQuery query = (RoleQuery) this.persistenceService.getServerService().getQuery(Role.class);
		final Collection<Role> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Role source : sources)
			{
				Role target = (Role) this.persistenceService.getCacheService().find(Role.class, source.getId());
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
	protected Role replicate(final Role source)
	{
		final Role target = this.replicate(source, Role.newInstance());
		return target;
	}

	@Override
	protected Role replicate(final Role source, Role target)
	{
		target = super.replicate(source, target);
		target.setName(source.getName());
		for (RoleProperty sourceProperty : source.getRoleProperties())
		{
			boolean found = false;
			for (RoleProperty targetProperty : target.getRoleProperties())
			{
				if (targetProperty.getId().equals(sourceProperty.getId()))
				{
					replicate(sourceProperty, targetProperty);
					found = true;
					break;
				}
			}
			if (!found)
			{
				replicate(target, sourceProperty);
			}
		}
		for (User sourceUser : source.getUsers())
		{
			boolean found = false;
			for (User targetUser : target.getUsers())
			{
				if (targetUser.getId().equals(sourceUser.getId()))
				{
					replicate(sourceUser, targetUser);
					found = true;
					break;
				}
			}
			if (!found)
			{
				replicate(target, sourceUser);
			}
		}
		return target;
	}
	
	protected void replicate(final Role targetRole, final RoleProperty sourceRoleProperty)
	{
		final RoleProperty targetRoleProperty = replicate(sourceRoleProperty, RoleProperty.newInstance(targetRole));
		targetRole.addRoleProperty(targetRoleProperty);
	}

	protected RoleProperty replicate(final RoleProperty source, final RoleProperty target)
	{
		target.setId(source.getId());
		target.setDeleted(source.isDeleted());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getVersion());
		target.setKey(source.getKey());
		target.setValue(source.getValue());
		return target;
	}

	protected void replicate(final Role targetRole, final User sourceUser)
	{
		final User targetUser = replicate(sourceUser, User.newInstance(targetRole));
		targetRole.addUser(targetUser);
	}

	protected User replicate(final User source, final User target)
	{
		target.setId(source.getId());
		target.setDeleted(source.isDeleted());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getVersion());
		target.setDefaultUser(source.isDefaultUser());
		target.setPassword(source.getPassword());
		target.setPosLogin(source.getPosLogin());
		target.setUsername(source.getUsername());
		return target;
	}
}
