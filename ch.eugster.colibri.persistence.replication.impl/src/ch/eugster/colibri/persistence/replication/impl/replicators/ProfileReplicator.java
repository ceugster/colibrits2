/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.queries.ProfileQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProfileReplicator extends AbstractEntityReplicator<Profile>
{
	public ProfileReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{

		int i = 0;

		final ProfileQuery query = (ProfileQuery) this.persistenceService.getServerService().getQuery(Profile.class);
		final Collection<Profile> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Profile source : sources)
			{
				Profile target = (Profile) this.persistenceService.getCacheService().find(Profile.class, source.getId());
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
	protected Profile replicate(final Profile source)
	{
		final Profile target = this.replicate(source, Profile.newInstance());
		return target;
	}

	@Override
	protected Profile replicate(final Profile source, Profile target)
	{
		target = super.replicate(source, target);
		target.setBottomLeft(source.getBottomLeft());
		target.setBottomRight(source.getBottomRight());
		target.setButtonFailOverBg(source.getButtonFailOverBg());
		target.setButtonFailOverFg(source.getButtonFailOverFg());
		target.setButtonFailOverFontSize(source.getButtonFailOverFontSize());
		target.setButtonFailOverFontStyle(source.getButtonFailOverFontStyle());
		target.setButtonFailOverHorizontalAlign(source.getButtonFailOverHorizontalAlign());
		target.setButtonFailOverVerticalAlign(source.getButtonFailOverVerticalAlign());
		target.setButtonNormalBg(source.getButtonNormalBg());
		target.setButtonNormalFg(source.getButtonNormalFg());
		target.setButtonNormalFontSize(source.getButtonNormalFontSize());
		target.setButtonNormalFontStyle(source.getButtonNormalFontStyle());
		target.setButtonNormalHorizontalAlign(source.getButtonNormalHorizontalAlign());
		target.setButtonNormalVerticalAlign(source.getButtonNormalVerticalAlign());
		target.setDisplayBg(source.getDisplayBg());
		target.setDisplayFg(source.getDisplayFg());
		target.setDisplayFontSize(source.getDisplayFontSize());
		target.setDisplayFontStyle(source.getDisplayFontStyle());
		target.setDisplayShowReceivedRemainderAlways(source.getDisplayShowReceivedRemainderAlways());
		target.setListBg(source.getListBg());
		target.setListFg(source.getListFg());
		target.setListFontSize(source.getListFontSize());
		target.setListFontStyle(source.getListFontStyle());
		target.setName(source.getName());
		target.setNameLabelBg(source.getNameLabelBg());
		target.setNameLabelFg(source.getNameLabelFg());
		target.setNameLabelFontSize(source.getNameLabelFontSize());
		target.setNameLabelFontStyle(source.getNameLabelFontStyle());
		target.setTabbedPaneBg(source.getTabbedPaneBg());
		target.setTabbedPaneFg(source.getTabbedPaneFg());
		target.setTabbedPaneFgSelected(source.getTabbedPaneFgSelected());
		target.setTabbedPaneFontSize(source.getTabbedPaneFontSize());
		target.setTabbedPaneFontStyle(source.getTabbedPaneFontStyle());
		target.setTopLeft(source.getTopLeft());
		target.setTopRight(source.getTopRight());
		target.setValueLabelBg(source.getValueLabelBg());
		target.setValueLabelBgSelected(source.getValueLabelBgSelected());
		target.setValueLabelFg(source.getValueLabelFg());
		target.setValueLabelFontSize(source.getValueLabelFontSize());
		target.setValueLabelFontStyle(source.getValueLabelFontStyle());
		return target;
	}
}
