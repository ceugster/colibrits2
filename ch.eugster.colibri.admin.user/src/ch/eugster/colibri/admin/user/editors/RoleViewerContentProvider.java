/*
 * Created on 07.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.editors;

import org.eclipse.jface.viewers.ArrayContentProvider;

import ch.eugster.colibri.persistence.model.Role;

public class RoleViewerContentProvider extends ArrayContentProvider
{
	@Override
	public Object[] getElements(final Object object)
	{
		if (object instanceof Role[])
		{
			return (Role[]) object;
		}

		return new Role[0];
	}
}
