/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.profile;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.Profile;

public class ProfileEditorInput extends AbstractEntityEditorInput<Profile>
{
	public ProfileEditorInput(final Profile profile)
	{
		super(profile);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof ProfileEditorInput)
		{
			final ProfileEditorInput input = (ProfileEditorInput) object;
			final Profile profile = (Profile) input.getAdapter(Profile.class);
			if ((profile.getId() != null) && profile.getId().equals(entity.getId()))
			{
				return true;
			}
		}

		return false;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class adapter)
	{
		if (adapter.getName().equals(Profile.class.getName()))
		{
			return entity;
		}
		else
		{
			return null;
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName()
	{
		return entity.getName();
	}

	@Override
	public String getToolTipText()
	{
		return entity.getName();
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
