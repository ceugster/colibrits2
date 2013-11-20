/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.PrintoutArea;
import ch.eugster.colibri.persistence.queries.PrintoutAreaQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class PrintoutAreaReplicator extends AbstractEntityReplicator<PrintoutArea>
{
	public PrintoutAreaReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final PrintoutAreaQuery query = (PrintoutAreaQuery) this.persistenceService.getServerService().getQuery(PrintoutArea.class);
		final Collection<PrintoutArea> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final PrintoutArea source : sources)
			{
				PrintoutArea target = (PrintoutArea) this.persistenceService.getCacheService().find(PrintoutArea.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getPrintout().getId().equals(source.getPrintout().getId()))
						{
							final Printout printout = (Printout) this.persistenceService.getCacheService().find(Printout.class, source.getPrintout().getId());
							target.setPrintout(printout);
						}
						target = this.replicate(source, target);
					}
					merge(target);
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
	protected PrintoutArea replicate(final PrintoutArea source)
	{
		final Printout printout = (Printout) this.persistenceService.getCacheService().find(Printout.class, source.getPrintout().getId());
		final PrintoutArea target = this.replicate(source, PrintoutArea.newInstance(printout, source.getPrintAreaType()));
		return target;
	}

	@Override
	protected PrintoutArea replicate(final PrintoutArea source, PrintoutArea target)
	{
		target = super.replicate(source, target);
		target.setTitlePattern(source.getTitlePattern());
		target.setDetailPattern(source.getDetailPattern());
		target.setTotalPattern(source.getTotalPattern());
		target.setTitlePrintOption(source.getTitlePrintOption());
		target.setDetailPrintOption(source.getDetailPrintOption());
		target.setTotalPrintOption(source.getTotalPrintOption());
		return target;
	}
}
