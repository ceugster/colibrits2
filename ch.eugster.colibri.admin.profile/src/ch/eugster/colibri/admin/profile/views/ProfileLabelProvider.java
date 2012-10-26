/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.views;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Tab;

public class ProfileLabelProvider extends LabelProvider implements ILabelProvider
{
	public static final NumberFormat nf = NumberFormat.getCurrencyInstance();

	@Override
	public Image getImage(final Object element)
	{
		if (element instanceof Profile)
		{
			return Activator.getDefault().getImageRegistry().get("PROFILE");
		}
		else if (element instanceof Configurable)
		{
			return Activator.getDefault().getImageRegistry().get("CONFIGURABLE");
		}
		else if (element instanceof Tab)
		{
			Tab tab = (Tab) element;
			Tab defaultTab = tab.getConfigurable().getPaymentDefaultTab();
			if (defaultTab != null)
			{
				if (tab.getId().equals(defaultTab.getId()))
				{
					return Activator.getDefault().getImageRegistry().get("pin-yellow.gif");
				}
			}
			defaultTab = tab.getConfigurable().getPositionDefaultTab();
			if (defaultTab != null)
			{
				if (tab.getId().equals(defaultTab.getId()))
				{
					return Activator.getDefault().getImageRegistry().get("pin-blue.gif");
				}
			}
			return Activator.getDefault().getImageRegistry().get("TAB");
		}
		else
		{
			return null;
		}
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof Profile)
		{
			final Profile profile = (Profile) element;
			return profile.getName();
		}
		else if (element instanceof Configurable)
		{
			return ((Configurable) element).getType().toString();
		}
		else if (element instanceof Tab)
		{
			return ((Tab) element).getName();
		}

		return "";
	}
}
