/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.queries.ConfigurableQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ConfigurableReplicator extends AbstractEntityReplicator<Configurable>
{
	public ConfigurableReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{
		int i = 0;

		final ConfigurableQuery query = (ConfigurableQuery) this.persistenceService.getServerService().getQuery(Configurable.class);
		final Collection<Configurable> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Configurable source : sources)
			{
				Configurable target = (Configurable) this.persistenceService.getCacheService().find(Configurable.class, source.getId());
				if ((target == null) || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getProfile().getId().equals(source.getProfile().getId()))
						{
							final Profile profile = (Profile) this.persistenceService.getCacheService().find(Profile.class, source.getProfile().getId());
							target.setProfile(profile);
						}
						target = this.replicate(source, target);
					}
					target = (Configurable) this.persistenceService.getCacheService().merge(target);
					if (!target.getProfile().getConfigurables().contains(target))
					{
						target.getProfile().addConfigurable(target);
					}
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

	public void setDefaultTabs(final IProgressMonitor monitor)
	{
		int i = 0;

		final ConfigurableQuery query = (ConfigurableQuery) this.persistenceService.getServerService().getQuery(Configurable.class);
		final Collection<Configurable> sources = query.selectAll(true);
		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Configurable source : sources)
			{
				boolean changed = false;
				final Configurable target = (Configurable) this.persistenceService.getCacheService().find(Configurable.class, source.getId());
				if (source.getPaymentDefaultTab() == null)
				{
					if (target.getPaymentDefaultTab() != null)
					{
						target.setPaymentDefaultTab(null);
						changed = true;
					}
				}
				else
				{
					if (target.getPaymentDefaultTab() == null)
					{
						final Tab tab = (Tab) this.persistenceService.getCacheService().find(Tab.class, source.getPaymentDefaultTab().getId());
						target.setPaymentDefaultTab(tab);
						changed = true;
					}
					else if (!target.getPaymentDefaultTab().getId().equals(source.getPaymentDefaultTab().getId()))
					{
						final Tab tab = (Tab) this.persistenceService.getCacheService().find(Tab.class, source.getPaymentDefaultTab().getId());
						target.setPaymentDefaultTab(tab);
						changed = true;
					}
				}

				if (source.getPositionDefaultTab() == null)
				{
					if (target.getPositionDefaultTab() != null)
					{
						target.setPositionDefaultTab(null);
						changed = true;
					}
				}
				else
				{
					if (target.getPositionDefaultTab() == null)
					{
						final Tab tab = (Tab) this.persistenceService.getCacheService().find(Tab.class, source.getPositionDefaultTab().getId());
						target.setPositionDefaultTab(tab);
						changed = true;
					}
					else if (!target.getPositionDefaultTab().getId().equals(source.getPositionDefaultTab().getId()))
					{
						final Tab tab = (Tab) this.persistenceService.getCacheService().find(Tab.class, source.getPositionDefaultTab().getId());
						target.setPositionDefaultTab(tab);
						changed = true;
					}
				}

				if (changed)
				{
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
	protected Configurable replicate(final Configurable source)
	{
		final Profile profile = (Profile) this.persistenceService.getCacheService().find(Profile.class, source.getProfile().getId());
		Configurable target = null;
		target = this.replicate(source, Configurable.newInstance(profile, source.getType()));
		return target;
	}

	@Override
	protected Configurable replicate(final Configurable source, Configurable target)
	{
		target = super.replicate(source, target);
		target.setBg(source.getBg());
		target.setFg(source.getFg());
		target.setFgSelected(source.getFgSelected());
		target.setFontSize(source.getFontSize());
		target.setFontStyle(source.getFontStyle());
		return target;
	}

}
