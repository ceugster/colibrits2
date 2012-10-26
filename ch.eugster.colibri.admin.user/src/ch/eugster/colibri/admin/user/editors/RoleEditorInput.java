/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.Role;

public class RoleEditorInput extends AbstractEntityEditorInput<Role>
{
	public RoleEditorInput(final Role role)
	{
		super(role);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof RoleEditorInput)
		{
			final RoleEditorInput input = (RoleEditorInput) object;
			final Role role = (Role) input.getAdapter(Role.class);
			if ((role.getId() != null) && role.getId().equals(entity.getId()))
			{
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter)
	{
		if (adapter.getName().equals(Role.class.getName()))
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
