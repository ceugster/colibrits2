/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;
import ch.eugster.colibri.persistence.queries.SalespointReceiptPrinterQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class SalespointReceiptPrinterReplicator extends AbstractEntityReplicator<SalespointReceiptPrinterSettings>
{
	public SalespointReceiptPrinterReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final SalespointReceiptPrinterQuery query = (SalespointReceiptPrinterQuery) this.persistenceService.getServerService()
				.getQuery(SalespointReceiptPrinterSettings.class);
		final Collection<SalespointReceiptPrinterSettings> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final SalespointReceiptPrinterSettings source : sources)
			{
				SalespointReceiptPrinterSettings target = (SalespointReceiptPrinterSettings) this.persistenceService.getCacheService().find(
						SalespointReceiptPrinterSettings.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getSalespoint().getId().equals(source.getSalespoint().getId()))
						{
							final Salespoint salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
							target.setSalespoint(salespoint);
						}
						if (!target.getReceiptPrinterSettings().getId().equals(source.getReceiptPrinterSettings().getId()))
						{
							final ReceiptPrinterSettings receiptPrinterSettings = (ReceiptPrinterSettings) this.persistenceService.getCacheService().find(
									ReceiptPrinterSettings.class, source.getReceiptPrinterSettings().getId());
							target.setReceiptPrinterSettings(receiptPrinterSettings);
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
	protected SalespointReceiptPrinterSettings replicate(final SalespointReceiptPrinterSettings source)
	{
		final ReceiptPrinterSettings periphery = (ReceiptPrinterSettings) this.persistenceService.getCacheService().find(ReceiptPrinterSettings.class, source
				.getReceiptPrinterSettings().getId());
		final Salespoint salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
		return this.replicate(source, SalespointReceiptPrinterSettings.newInstance(periphery, salespoint));
	}

	@Override
	protected SalespointReceiptPrinterSettings replicate(final SalespointReceiptPrinterSettings source, SalespointReceiptPrinterSettings target)
	{
		target = super.replicate(source, target);
		target.setCols(source.getCols());
		target.setConverter(source.getConverter());
		target.setPort(source.getPort());
		target.setLinesBeforeCut(source.getLinesBeforeCut());
		return target;
	}

}
