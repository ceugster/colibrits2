package ch.eugster.colibri.admin.common.settings.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.admin.common.settings.Activator;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.Parent;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.ProviderPropertyParent;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.VoucherServiceParent;

public class CommonSettingsLabelProvider extends LabelProvider
{

	@Override
	public Image getImage(final Object element)
	{
		if (element instanceof ProviderPropertyParent)
		{
			return Activator.getDefault().getImageRegistry().get(Activator.IMAGE_STOCK);
		}
		else if (element instanceof VoucherServiceParent)
		{
			return Activator.getDefault().getImageRegistry().get(Activator.IMAGE_GEARWHEEL);
		}
		// else if (element instanceof SalespointRegistrationParent)
		// {
		// return
		// Activator.getDefault().getImageRegistry().get(Activator.IMAGE_REGISTER);
		// }
		return null;
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof Parent)
		{
			return ((Parent) element).getName();
		}
		return "";
	}

}
