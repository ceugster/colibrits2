package ch.eugster.colibri.admin.common.settings.views;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import ch.eugster.colibri.admin.common.settings.Activator;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.GeneralSettingsParent;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.Parent;
import ch.eugster.colibri.admin.common.settings.views.CommonSettingsContentProvider.ProviderUpdaterParent;
import ch.eugster.colibri.provider.scheduler.service.ProviderUpdateScheduler;
import ch.eugster.colibri.provider.service.ProviderUpdater;

public class CommonSettingsLabelProvider extends LabelProvider
{

	@Override
	public Image getImage(final Object element)
	{
		if (element instanceof ProviderUpdaterParent)
		{
			return Activator.getDefault().getImageRegistry().get(Activator.IMAGE_GEARWHEEL_BLUE);
		}
		else if (element instanceof GeneralSettingsParent)
		{
			return Activator.getDefault().getImageRegistry().get(Activator.IMAGE_GEARWHEEL_RED);
		}
		else if (element instanceof ProviderUpdater)
		{
			return Activator.getDefault().getImageRegistry().get(Activator.IMAGE_GEARWHEEL_YELLOW);
		}
		else if (element instanceof ProviderUpdateScheduler)
		{
			return Activator.getDefault().getImageRegistry().get(Activator.IMAGE_GEARWHEEL_GREEN);
		}
		return null;
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof Parent)
		{
			return ((Parent) element).getName();
		}
		else if (element instanceof ProviderUpdater)
		{
			ProviderUpdater updater = (ProviderUpdater) element;
			return updater.getName();
		}
		else if (element instanceof ProviderUpdateScheduler)
		{
			ProviderUpdateScheduler scheduler = (ProviderUpdateScheduler) element;
			return scheduler.getName();
		}
		return "";
	}

}
