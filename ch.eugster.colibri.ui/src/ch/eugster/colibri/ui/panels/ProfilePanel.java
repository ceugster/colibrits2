/*
 * Created on 23.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.ui.panels;

import javax.swing.JPanel;

import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Profile;

public abstract class ProfilePanel extends JPanel implements EntityListener
{
	protected static final long serialVersionUID = 1L;

	protected Profile profile;

	public ProfilePanel(final Profile profile)
	{
		this.profile = profile;
	}

	@Override
	public void finalize()
	{
	}

	public Profile getProfile()
	{
		return profile;
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
				this.update();
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

	protected abstract void update();

}
