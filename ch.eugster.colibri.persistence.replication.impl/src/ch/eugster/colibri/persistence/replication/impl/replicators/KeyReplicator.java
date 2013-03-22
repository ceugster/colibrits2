/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.queries.KeyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class KeyReplicator extends AbstractEntityReplicator<Key>
{
	public KeyReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final KeyQuery query = (KeyQuery) this.persistenceService.getServerService().getQuery(Key.class);
		final Collection<Key> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Key source : sources)
			{
				Key target = (Key) this.persistenceService.getCacheService().find(Key.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getTab().getId().equals(source.getTab().getId()))
						{
							final Tab tab = (Tab) this.persistenceService.getCacheService().find(Tab.class, source.getTab().getId());
							target.setTab(tab);
						}
						target = this.replicate(source, target);
					}
					target = (Key) this.persistenceService.getCacheService().merge(target);
					target.getTab().addKey(target);
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
	protected Key replicate(final Key source)
	{
		final Tab tab = (Tab) this.persistenceService.getCacheService().find(Tab.class, source.getTab().getId());
		final Key target = this.replicate(source, Key.newInstance(tab));
		return target;
	}

	@Override
	protected Key replicate(final Key source, Key target)
	{
		target = super.replicate(source, target);
		target.setTabRow(source.getTabRow());
		target.setTabCol(source.getTabCol());
		target.setLabel(source.getLabel());
		target.setFunctionType(source.getFunctionType());
		target.setValue(source.getValue());
		target.setImageId(source.getImageId());
		target.setTextImageHorizontalPosition(source.getTextImageHorizontalPosition());
		target.setTextImageVerticalPosition(source.getTextImageVerticalPosition());
		target.setNormalFontSize(source.getNormalFontSize());
		target.setNormalFontStyle(source.getNormalFontStyle());
		target.setNormalHorizontalAlign(source.getNormalHorizontalAlign());
		target.setNormalVerticalAlign(source.getNormalVerticalAlign());
		target.setNormalFg(source.getNormalFg());
		target.setNormalBg(source.getNormalBg());
		target.setFailOverFontSize(source.getFailOverFontSize());
		target.setFailOverFontStyle(source.getFailOverFontStyle());
		target.setFailOverHorizontalAlign(source.getFailOverHorizontalAlign());
		target.setFailOverVerticalAlign(source.getFailOverVerticalAlign());
		target.setFailOverFg(source.getFailOverFg());
		target.setFailOverBg(source.getFailOverBg());
		target.setProductCode(source.getProductCode());
		target.setKeyType(source.getKeyType());
		target.setParentId(source.getParentId());
		target.setCount(source.getCount());
		return target;
	}
}
