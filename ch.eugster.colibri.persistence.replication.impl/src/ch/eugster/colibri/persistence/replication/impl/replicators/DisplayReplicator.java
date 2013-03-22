/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.DisplayQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class DisplayReplicator extends AbstractEntityReplicator<Display>
{
	public DisplayReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final DisplayQuery query = (DisplayQuery) this.persistenceService.getServerService().getQuery(Display.class);
		final Collection<Display> parents = query.selectDisplayParents();
		final Collection<Display> children = query.selectDisplayChildren();

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", parents.size() + children.size());
		}

		try
		{
			for (final Display source : parents)
			{
				Display target = (Display) this.persistenceService.getCacheService().find(Display.class, source.getId());
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

			for (final Display source : children)
			{
				Display target = (Display) this.persistenceService.getCacheService().find(Display.class, source.getId());
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
	protected Display replicate(final Display source)
	{
		final CustomerDisplaySettings settings = (CustomerDisplaySettings) this.persistenceService.getCacheService().find(CustomerDisplaySettings.class, source.getCustomerDisplaySettings().getId());

		final Display target = Display.newInstance(source.getDisplayType(), settings);

		if (source.getSalespoint() != null)
		{
			final Salespoint salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
			target.setSalespoint(salespoint);
		}

		if (source.getParent() != null)
		{
			final Display parent = (Display) this.persistenceService.getCacheService().find(Display.class, source.getParent().getId());
			target.setParent(parent);
		}

		return this.replicate(source, target);
	}

	@Override
	protected Display replicate(final Display source, Display target)
	{
		target = super.replicate(source, target);
		return target;
	}
}
