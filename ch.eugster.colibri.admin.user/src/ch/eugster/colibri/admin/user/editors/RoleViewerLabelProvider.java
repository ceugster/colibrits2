/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.editors;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.persistence.model.Role;

public class RoleViewerLabelProvider extends LabelProvider implements ILabelProvider
{
	public static final NumberFormat nf = NumberFormat.getCurrencyInstance();

	@Override
	public Image getImage(final Object element)
	{
		return null;
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof Role)
		{
			final Role role = (Role) element;
			return role.getName();
		}

		return "";
	}
}
