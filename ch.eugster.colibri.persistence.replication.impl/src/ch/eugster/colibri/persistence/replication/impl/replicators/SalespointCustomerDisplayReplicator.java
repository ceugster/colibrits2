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
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;
import ch.eugster.colibri.persistence.queries.SalespointCustomerDisplayQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class SalespointCustomerDisplayReplicator extends AbstractEntityReplicator<SalespointCustomerDisplaySettings>
{
	public SalespointCustomerDisplayReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final SalespointCustomerDisplayQuery query = (SalespointCustomerDisplayQuery) this.persistenceService.getServerService()
				.getQuery(SalespointCustomerDisplaySettings.class);
		final Collection<SalespointCustomerDisplaySettings> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final SalespointCustomerDisplaySettings source : sources)
			{
				SalespointCustomerDisplaySettings target = (SalespointCustomerDisplaySettings) this.persistenceService.getCacheService().find(
						SalespointCustomerDisplaySettings.class, source.getId());
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
						if (!target.getCustomerDisplaySettings().getId().equals(source.getCustomerDisplaySettings().getId()))
						{
							final CustomerDisplaySettings customerDisplaySettings = (CustomerDisplaySettings) this.persistenceService.getCacheService().find(
									CustomerDisplaySettings.class, source.getCustomerDisplaySettings().getId());
							target.setCustomerDisplaySettings(customerDisplaySettings);
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
	protected SalespointCustomerDisplaySettings replicate(final SalespointCustomerDisplaySettings source)
	{
		final CustomerDisplaySettings periphery = (CustomerDisplaySettings) this.persistenceService.getCacheService().find(CustomerDisplaySettings.class, source
				.getCustomerDisplaySettings().getId());
		final Salespoint salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
		return this.replicate(source, SalespointCustomerDisplaySettings.newInstance(periphery, salespoint));
	}

	@Override
	protected SalespointCustomerDisplaySettings replicate(final SalespointCustomerDisplaySettings source, SalespointCustomerDisplaySettings target)
	{
		target = super.replicate(source, target);
		target.setCols(source.getCols());
		target.setConverter(source.getConverter());
//		target.setDelay(source.getDelay());
		target.setPort(source.getPort());
		target.setRows(source.getRows());
		return target;
	}

}
