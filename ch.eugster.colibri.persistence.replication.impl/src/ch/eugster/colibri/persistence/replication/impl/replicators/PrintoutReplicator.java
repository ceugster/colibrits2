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
	public void replicate(final IProgressMonitor monitor, boolean force)
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

			for (final Printout source : children)
			{
				Printout target = (Printout) this.persistenceService.getCacheService().find(Printout.class, source.getId());
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
	protected Printout replicate(final Printout source)
	{
		ReceiptPrinterSettings settings = (ReceiptPrinterSettings) this.persistenceService.getCacheService().find(ReceiptPrinterSettings.class, source.getId());
		final Printout target = Printout.newInstance(source.getPrintoutType(), settings);
		return this.replicate(source, target);
	}

	@Override
	protected Printout replicate(final Printout source, Printout target)
	{
		target = super.replicate(source, target);
		Printout parent = null;
		if (source.getParent() != null)
		{
			parent = (Printout) this.persistenceService.getCacheService().find(Printout.class, source.getParent().getId());
		}
		target.setParent(parent);
		target.setPrintoutType(source.getPrintoutType());
		target.setAutomaticPrint(source.isAutomaticPrint());
		ReceiptPrinterSettings settings = null;
		if (source.getReceiptPrinterSettings() != null)
		{
			settings = (ReceiptPrinterSettings) this.persistenceService.getCacheService().find(ReceiptPrinterSettings.class, source.getReceiptPrinterSettings().getId());
		}
		target.setReceiptPrinterSettings(settings);
		Salespoint salespoint = null;
		if (source.getSalespoint() != null)
		{
			salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
		}
		target.setSalespoint(salespoint);
		Collection<Printout> sourceChildren = source.getChildren();
		for (Printout sourceChild : sourceChildren)
		{
			boolean found = false;
			Collection<Printout> targetChildren = target.getChildren();
			for (Printout targetChild : targetChildren)
			{
				if (targetChild.getId().equals(sourceChild.getId()))
				{
					found = true;
				}
			}
			if (!found)
			{
				Printout targetChild = replicate(sourceChild);
				target.addPrintout(targetChild);
			}
		}
		Collection<PrintoutArea> areas = target.getPrintoutAreas().values();
		for (PrintoutArea area : areas)
		{
			area.setDeleted(true);
		}
		areas = source.getPrintoutAreas().values();
		for (PrintoutArea area : areas)
		{
			replicate(target, area);
		}
		
		return target;
	}

	protected void replicate(final Printout targetPrintout, final PrintoutArea sourcePrintoutArea)
	{
		PrintoutArea area = targetPrintout.getPrintoutArea(sourcePrintoutArea.getPrintAreaType());
		if (area == null)
		{
			if (!sourcePrintoutArea.isDeleted())
			{
				final PrintoutArea targetPrintoutArea = this.replicate(sourcePrintoutArea, PrintoutArea.newInstance(targetPrintout, sourcePrintoutArea.getPrintAreaType()));
				targetPrintout.addPrintoutArea(targetPrintoutArea);
			}
		}
		else
		{
			area = this.replicate(sourcePrintoutArea, area);
		}
	}

	protected PrintoutArea replicate(final PrintoutArea source, PrintoutArea target)
	{
		target.setId(source.getId());
		target.setDeleted(source.isDeleted());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getVersion());
		target.setTitlePattern(source.getTitlePattern());
		target.setDetailPattern(source.getDetailPattern());
		target.setTotalPattern(source.getTotalPattern());
		target.setTitlePrintOption(source.getTitlePrintOption());
		target.setDetailPrintOption(source.getDetailPrintOption());
		target.setTotalPrintOption(source.getTotalPrintOption());
		return target;
	}
}
