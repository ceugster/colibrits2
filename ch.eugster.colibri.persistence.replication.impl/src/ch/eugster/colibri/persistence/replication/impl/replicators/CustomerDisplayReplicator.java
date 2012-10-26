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
import ch.eugster.colibri.persistence.queries.CustomerDisplaySettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CustomerDisplayReplicator extends AbstractEntityReplicator<CustomerDisplaySettings>
{
	public CustomerDisplayReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{
		int i = 0;

		final CustomerDisplaySettingsQuery query = (CustomerDisplaySettingsQuery) this.persistenceService.getServerService()
				.getQuery(CustomerDisplaySettings.class);
		final Collection<CustomerDisplaySettings> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final CustomerDisplaySettings source : sources)
			{
				CustomerDisplaySettings target = (CustomerDisplaySettings) this.persistenceService.getCacheService().find(CustomerDisplaySettings.class, source.getId());
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
	protected CustomerDisplaySettings replicate(final CustomerDisplaySettings source)
	{
		return this.replicate(source, CustomerDisplaySettings.newInstance());
	}

	@Override
	protected CustomerDisplaySettings replicate(final CustomerDisplaySettings source, CustomerDisplaySettings target)
	{
		target = super.replicate(source, target);
		target.setCols(source.getCols());
		target.setComponentName(source.getComponentName());
		target.setConverter(source.getConverter());
//		target.setDelay(source.getDelay());
		target.setName(source.getName());
		target.setPort(source.getPort());
		target.setRows(source.getRows());
		return target;
	}

}
