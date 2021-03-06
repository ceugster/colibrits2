/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.PrintMode;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.queries.ReceiptPrinterSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ReceiptPrinterReplicator extends AbstractEntityReplicator<ReceiptPrinterSettings>
{
	public ReceiptPrinterReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final ReceiptPrinterSettingsQuery query = (ReceiptPrinterSettingsQuery) this.persistenceService.getServerService().getQuery(ReceiptPrinterSettings.class);
		final Collection<ReceiptPrinterSettings> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final ReceiptPrinterSettings source : sources)
			{
				ReceiptPrinterSettings target = (ReceiptPrinterSettings) this.persistenceService.getCacheService().find(ReceiptPrinterSettings.class, source.getId());
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
	protected ReceiptPrinterSettings replicate(final ReceiptPrinterSettings source)
	{
		return this.replicate(source, ReceiptPrinterSettings.newInstance());
	}

	@Override
	protected ReceiptPrinterSettings replicate(final ReceiptPrinterSettings source, ReceiptPrinterSettings target)
	{
		target = super.replicate(source, target);
		target.setCols(source.getCols());
		target.setComponentName(source.getComponentName());
		target.setConverter(source.getConverter());
		target.setLogo(source.getLogo());
		target.setName(source.getName());
		target.setPort(source.getPort());
		target.setPrintLogo(source.isPrintLogo());
		target.setPrintLogoMode(source.getPrintLogoMode() == null ? PrintMode.NORMAL : source.getPrintLogoMode());
		target.setLinesBeforeCut(source.getLinesBeforeCut());
		return target;
	}

}
