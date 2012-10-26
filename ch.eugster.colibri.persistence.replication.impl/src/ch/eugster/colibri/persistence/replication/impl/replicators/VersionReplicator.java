/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.queries.VersionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class VersionReplicator extends AbstractEntityReplicator<Version>
{
	public VersionReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{
		int i = 0;

		final VersionQuery query = (VersionQuery) this.persistenceService.getServerService().getQuery(Version.class);
		final Collection<Version> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Version source : sources)
			{
				Version target = (Version) this.persistenceService.getCacheService().find(Version.class, source.getId());
				if ((target == null) || (target.getUpdate() != source.getVersion()))
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
	protected Version replicate(final Version source)
	{
		final Version target = this.replicate(source, Version.newInstance());
		return target;
	}

	@Override
	protected Version replicate(final Version source, Version target)
	{
		target = super.replicate(source, target);
		target.setData(source.getData());
		target.setStructure(source.getStructure());
		target.setMigrate(source.isMigrate());
		target.setReplicationValue(source.getReplicationValue());
		return target;
	}

}
