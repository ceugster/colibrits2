/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.common.settings.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.common.settings.Activator;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.CommonSettings;

public class GeneralSettingsEditorInput extends AbstractEntityEditorInput<CommonSettings>
{
	public GeneralSettingsEditorInput(final CommonSettings commonSettings)
	{
		super(commonSettings);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof GeneralSettingsEditorInput)
		{
			final GeneralSettingsEditorInput input = (GeneralSettingsEditorInput) object;
			final CommonSettings commonSettings = (CommonSettings) input.getAdapter(CommonSettings.class);
			if ((commonSettings.getId() != null) && commonSettings.getId().equals(this.entity.getId()))
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
		if (adapter.getName().equals(CommonSettings.class.getName()))
		{
			return this.entity;
		}
		else
		{
			return null;
		}
	}

	@Override
	public ImageDescriptor getImageDescriptor()
	{
		return Activator.getDefault().getImageRegistry().getDescriptor(Activator.IMAGE_BULP);
	}

	@Override
	public String getName()
	{
		return "Allgemeine Einstellungen";
	}

	@Override
	public String getToolTipText()
	{
		return "Allgemeine Einstellungen";
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
