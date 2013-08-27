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
import ch.eugster.colibri.persistence.model.DisplayArea;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.PrintoutArea;
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
		return this.replicate(source, target);
	}

	@Override
	protected Display replicate(final Display source, Display target)
	{
		target = super.replicate(source, target);
		CustomerDisplaySettings settings = null;
		if (source.getCustomerDisplaySettings() != null)
		{
			settings = (CustomerDisplaySettings) this.persistenceService.getCacheService().find(CustomerDisplaySettings.class, source.getCustomerDisplaySettings().getId());
		}
		target.setCustomerDisplaySettings(settings);
		target.setDisplayType(source.getDisplayType());
		Display parent = null;
		if (source.getParent() != null)
		{
			parent = (Display) this.persistenceService.getCacheService().find(Display.class, source.getParent().getId());
		}
		target.setParent(parent);
		Salespoint salespoint = null;
		if (source.getSalespoint() != null)
		{
			salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
		}
		target.setSalespoint(salespoint);
		Collection<Display> sourceChildren = source.getChildren();
		for (Display sourceChild : sourceChildren)
		{
			boolean found = false;
			Collection<Display> targetChildren = target.getChildren();
			for (Display targetChild : targetChildren)
			{
				if (targetChild.getId().equals(sourceChild.getId()))
				{
					found = true;
				}
			}
			if (!found)
			{
				Display targetChild = replicate(sourceChild);
				target.addPrintout(targetChild);
			}
		}
		Collection<DisplayArea> areas = target.getDisplayAreas().values();
		for (DisplayArea area : areas)
		{
			area.setDeleted(true);
		}
		areas = source.getDisplayAreas().values();
		for (DisplayArea area : areas)
		{
			replicate(target, area);
		}
		
		return target;
	}

	protected void replicate(final Display targetDisplay, final DisplayArea sourceDisplayArea)
	{
		DisplayArea area = targetDisplay.getDisplayArea(sourceDisplayArea.getDisplayAreaType());
		if (area == null)
		{
			if (!sourceDisplayArea.isDeleted())
			{
				final DisplayArea targetDisplayArea = this.replicate(sourceDisplayArea, DisplayArea.newInstance(targetDisplay, sourceDisplayArea.getDisplayAreaType()));
				targetDisplay.addDisplayArea(targetDisplayArea);
			}
		}
		else
		{
			area = this.replicate(sourceDisplayArea, area);
		}
	}

	protected DisplayArea replicate(final DisplayArea source, DisplayArea target)
	{
		target.setId(source.getId());
		target.setDeleted(source.isDeleted());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getVersion());
		target.setPattern(source.getPattern());
		target.setTimerDelay(source.getTimerDelay());
		return target;
	}
}
