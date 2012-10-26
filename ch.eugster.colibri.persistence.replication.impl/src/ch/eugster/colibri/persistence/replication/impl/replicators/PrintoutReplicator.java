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
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.PrintoutQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class PrintoutReplicator extends AbstractEntityReplicator<Printout>
{
	public PrintoutReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{

		int i = 0;

		final PrintoutQuery query = (PrintoutQuery) this.persistenceService.getServerService().getQuery(Printout.class);
		final Collection<Printout> parents = query.selectPrintoutParents();
		final Collection<Printout> children = query.selectPrintoutChildren();

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", parents.size() + children.size());
		}

		try
		{
			for (final Printout source : parents)
			{
				Printout target = (Printout) this.persistenceService.getCacheService().find(Printout.class, source.getId());
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

			for (final Printout source : children)
			{
				Printout target = (Printout) this.persistenceService.getCacheService().find(Printout.class, source.getId());
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
	protected Printout replicate(final Printout source)
	{
		ReceiptPrinterSettings settings = (ReceiptPrinterSettings) this.persistenceService.getCacheService().find(ReceiptPrinterSettings.class, source.getId());
		
		final Printout target = Printout.newInstance(source.getPrintoutType(), settings);

		if (source.getSalespoint() != null)
		{
			Salespoint salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
			target.setSalespoint(salespoint);
		}

		if (source.getParent() != null)
		{
			final Printout parent = (Printout) this.persistenceService.getCacheService().find(Printout.class, source.getParent().getId());
			target.setParent(parent);
		}

		return this.replicate(source, target);
	}

	@Override
	protected Printout replicate(final Printout source, Printout target)
	{
		target = super.replicate(source, target);
		target.setAutomaticPrint(source.isAutomaticPrint());
		return target;
	}
}
