/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.DisplayArea;
import ch.eugster.colibri.persistence.queries.DisplayAreaQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class DisplayAreaReplicator extends AbstractEntityReplicator<DisplayArea>
{
	public DisplayAreaReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{
		int i = 0;

		final DisplayAreaQuery query = (DisplayAreaQuery) this.persistenceService.getServerService().getQuery(DisplayArea.class);
		final Collection<DisplayArea> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final DisplayArea source : sources)
			{
				DisplayArea target = (DisplayArea) this.persistenceService.getCacheService().find(DisplayArea.class, source.getId());
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
	protected DisplayArea replicate(final DisplayArea source)
	{
		final Display display = (Display) this.persistenceService.getCacheService().find(Display.class, source.getDisplay().getId());
		return this.replicate(source, DisplayArea.newInstance(display, source.getDisplayAreaType()));
	}

	@Override
	protected DisplayArea replicate(final DisplayArea source, DisplayArea target)
	{
		target = super.replicate(source, target);
		target.setPattern(source.getPattern());
		target.setTimerDelay(source.getTimerDelay());
		return target;
	}
}
