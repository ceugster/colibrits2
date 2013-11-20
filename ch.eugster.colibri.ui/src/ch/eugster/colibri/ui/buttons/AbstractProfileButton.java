/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.ui.buttons;

import java.awt.Color;

import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.actions.ProfileAction;

public abstract class AbstractProfileButton extends HTMLButton implements EntityListener
{
	private static final long serialVersionUID = 0l;

	protected Profile profile;

	public AbstractProfileButton(final Profile profile)
	{
		super(profile.getButtonNormalFg(), profile.getButtonFailOverFg());
		this.init(profile);
	}

	public AbstractProfileButton(final ProfileAction action, final Profile profile)
	{
		this(action, profile, false);
	}

	public AbstractProfileButton(final ProfileAction action, final Profile profile, boolean isFailOver)
	{
		super(action, profile.getButtonNormalFg(), profile.getButtonFailOverFg(), isFailOver);
		this.init(profile);
	}

	@Override
	public void finalize()
	{
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void postLoad(final AbstractEntity entity)
	{
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
	}

	@Override
	public void postRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (entity instanceof Profile)
		{
			if (entity.getId().equals(profile.getId()))
			{
				profile = (Profile) entity;
				this.update(!true); // TODO
			}
		}
	}

	@Override
	public void preDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void prePersist(final AbstractEntity entity)
	{
	}

	@Override
	public void preRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void preUpdate(final AbstractEntity entity)
	{
	}

	@Override
	public void update(final boolean failOver)
	{
		if (failOver)
		{
			updateFailOver();
		}
		else
		{
			updateNormal();
		}
	}

	protected void updateFailOver()
	{
		setBackground(new Color(profile.getButtonFailOverBg()));
		setForeground(new Color(profile.getButtonFailOverFg()));
		setFont(getFont().deriveFont(profile.getButtonFailOverFontStyle(), profile.getButtonFailOverFontSize()));
		setHorizontalAlignment(profile.getButtonFailOverHorizontalAlign());
		setVerticalAlignment(profile.getButtonFailOverVerticalAlign());
	}

	protected void updateNormal()
	{
		setBackground(new Color(profile.getButtonNormalBg()));
		setForeground(new Color(profile.getButtonNormalFg()));
		setFont(getFont().deriveFont(profile.getButtonNormalFontStyle(), profile.getButtonNormalFontSize()));
		setHorizontalAlignment(profile.getButtonNormalHorizontalAlign());
		setVerticalAlignment(profile.getButtonNormalVerticalAlign());
	}

	private void init(final Profile profile)
	{
		this.profile = profile;
	}

}
