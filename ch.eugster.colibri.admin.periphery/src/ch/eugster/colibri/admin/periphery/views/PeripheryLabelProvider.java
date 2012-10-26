/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.views;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.ServiceReference;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.persistence.model.Printout;

public class PeripheryLabelProvider extends LabelProvider implements ILabelProvider
{
	public static final NumberFormat nf = NumberFormat.getCurrencyInstance();

	@Override
	public Image getImage(final Object element)
	{
		if (element instanceof PeripheryGroup)
		{
			final PeripheryGroup peripheryGroup = (PeripheryGroup) element;
			return Activator.getDefault().getImageRegistry().get(peripheryGroup.image());
		}
		else if (element instanceof ServiceReference)
		{
			final ServiceReference<?> reference = (ServiceReference<?>) element;
			final Integer group = (Integer) reference.getProperty("custom.group");
			if (group instanceof Integer)
			{
				final PeripheryGroup peripheryGroup = PeripheryGroup.values()[group.intValue()];
				return Activator.getDefault().getImageRegistry().get(peripheryGroup.image());
			}
		}
		return null;
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof PeripheryGroup)
		{
			final PeripheryGroup peripheryGroup = (PeripheryGroup) element;
			return peripheryGroup.toString();
		}
		else if (element instanceof ServiceReference)
		{
			final ServiceReference<?> reference = (ServiceReference<?>) element;
			String deviceName = (String) reference.getProperty("custom.device");
			if (deviceName == null)
			{
				deviceName = (String) reference.getProperty("custom.name");
			}
			return deviceName;
		}
		else if (element instanceof Printout)
		{
			final Printout printout = (Printout) element;
			if (printout.getPrintoutType().equals("ch.eugster.colibri.printing.receipt"))
			{
				return "Beleglayout";
			}
		}
		return "";
	}
}
