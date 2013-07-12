/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.CommonSettingsProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.CommonSettingsPropertyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CommonSettingsPropertyReplicator extends AbstractEntityReplicator<CommonSettingsProperty>
{
	public CommonSettingsPropertyReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final CommonSettingsPropertyQuery query = (CommonSettingsPropertyQuery) this.persistenceService.getServerService().getQuery(CommonSettingsProperty.class);
		final Collection<CommonSettingsProperty> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final CommonSettingsProperty source : sources)
			{
				CommonSettingsProperty target = (CommonSettingsProperty) this.persistenceService.getCacheService().find(CommonSettingsProperty.class, source.getId());
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
	protected CommonSettingsProperty replicate(final CommonSettingsProperty source)
	{
		CommonSettings settings = (CommonSettings) this.persistenceService.getCacheService().find(CommonSettings.class, source.getCommonSettings().getId());
		Salespoint salespoint = null;
		if (source.getSalespoint() != null)
		{
			salespoint = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class, source.getSalespoint().getId());
		}
		final CommonSettingsProperty target = this.replicate(source, CommonSettingsProperty.newInstance(settings, salespoint));
		return target;
	}

	@Override
	protected CommonSettingsProperty replicate(final CommonSettingsProperty source, CommonSettingsProperty target)
	{
		target = super.replicate(source, target);
		target.setKey(source.getKey());
		target.setDiscriminator(source.getDiscriminator());
		target.setValue(source.getValue());
		return target;
	}
}
