/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.User;

public class UserEditorInput extends AbstractEntityEditorInput<User>
{
	public UserEditorInput(final User user)
	{
		super(user);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof UserEditorInput)
		{
			final UserEditorInput input = (UserEditorInput) object;
			final User user = (User) input.getAdapter(User.class);
			if ((user.getId() != null) && user.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(User.class.getName()))
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
		return (entity).getUsername();
	}

	@Override
	public String getToolTipText()
	{
		return (entity).getUsername();
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
