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
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.queries.ProfileQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProfileReplicator extends AbstractEntityReplicator<Profile>
{
	public ProfileReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
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
		target.setInputNameLabelBg(source.getInputNameLabelBg());
		target.setInputNameLabelFg(source.getInputNameLabelFg());
		target.setInputNameLabelFontSize(source.getInputNameLabelFontSize());
		target.setInputNameLabelFontStyle(source.getInputNameLabelFontStyle());
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
		for (Configurable sourceConfigurable : source.getConfigurables())
		{
			boolean found = false;
			for (Configurable targetConfigurable : target.getConfigurables())
			{
				if (targetConfigurable.getId().equals(sourceConfigurable.getId()))
				{
					replicate(sourceConfigurable, targetConfigurable);
					found = true;
					break;
				}
			}
			if (!found)
			{
				replicate(target, sourceConfigurable);
			}
		}
		return target;
	}

	protected void replicate(final Profile targetProfile, final Configurable sourceConfigurable)
	{
		final Configurable targetConfigurable = replicate(sourceConfigurable, Configurable.newInstance(targetProfile, sourceConfigurable.getConfigurableType()));
		targetProfile.addConfigurable(targetConfigurable);
	}

	protected Configurable replicate(final Configurable source, final Configurable target)
	{
		target.setId(source.getId());
		target.setDeleted(source.isDeleted());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getVersion());
		target.setBg(source.getBg());
		target.setFg(source.getFg());
		target.setFgSelected(source.getFgSelected());
		target.setFontSize(source.getFontSize());
		target.setFontStyle(source.getFontStyle());
		for (Tab sourceTab : source.getTabs())
		{
			boolean found = false;
			for (Tab targetTab : target.getTabs())
			{
				if (targetTab.getId().equals(sourceTab.getId()))
				{
					replicate(sourceTab, targetTab);
					found = true;
					break;
				}
			}
			if (!found)
			{
				replicate(target, sourceTab);
			}
		}
		return target;
	}

	protected void replicate(final Configurable targetConfigurable, final Tab sourceTab)
	{
		final Tab targetTab= replicate(sourceTab, Tab.newInstance(targetConfigurable));
		targetConfigurable.addTab(targetTab);
	}

	protected Tab replicate(final Tab source, Tab target)
	{
		target.setId(source.getId());
		target.setDeleted(source.isDeleted());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getVersion());
		target.setCols(source.getCols());
		target.setName(source.getName());
		target.setPos(source.getPos());
		target.setRows(source.getRows());
		if (source.getConfigurable().getPaymentDefaultTab() != null)
		{
			if (source.getConfigurable().getPaymentDefaultTab().getId().equals(source.getId()))
			{
				target.getConfigurable().setPaymentDefaultTab(target);
			}
		}
		if (source.getConfigurable().getPositionDefaultTab() != null)
		{
			if (source.getConfigurable().getPositionDefaultTab().getId().equals(source.getId()))
			{
				target.getConfigurable().setPositionDefaultTab(target);
			}
		}
		for (Key sourceKey : source.getKeys())
		{
			boolean found = false;
			for (Key targetKey : target.getKeys())
			{
				if (targetKey.getId().equals(sourceKey.getId()))
				{
					replicate(sourceKey, targetKey);
					found = true;
					break;
				}
			}
			if (!found)
			{
				replicate(target, sourceKey);
			}
		}
		return target;
	}

	protected void replicate(final Tab targetTab, final Key sourceKey)
	{
		final Key targetKey = replicate(sourceKey, Key.newInstance(targetTab));
		targetTab.addKey(targetKey);
	}

	protected Key replicate(final Key source, Key target)
	{
		target.setId(source.getId());
		target.setDeleted(source.isDeleted());
		target.setTimestamp(source.getTimestamp());
		target.setUpdate(source.getVersion());
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
