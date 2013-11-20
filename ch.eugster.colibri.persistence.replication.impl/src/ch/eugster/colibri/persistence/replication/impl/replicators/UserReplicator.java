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
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class UserReplicator extends AbstractEntityReplicator<User>
{
	public UserReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final UserQuery query = (UserQuery) this.persistenceService.getServerService().getQuery(User.class);
		final Collection<User> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final User source : sources)
			{
				User target = (User) this.persistenceService.getCacheService().find(User.class, source.getId());
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
					target = (User) merge(target);
					target.getRole().addUser(target);
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
	protected User replicate(final User source)
	{
		final User target = this.replicate(source, User.newInstance());
		return target;
	}

	@Override
	protected User replicate(final User source, User target)
	{
		target = super.replicate(source, target);
		target.setDefaultUser(source.isDefaultUser());
		target.setPassword(source.getPassword());
		target.setPosLogin(source.getPosLogin());
		target.setRole((Role) this.persistenceService.getCacheService().find(Role.class, source.getRole().getId()));
		target.setUsername(source.getUsername());
		return target;
	}
}
