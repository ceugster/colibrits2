/*

 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.configurable;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Configurable;

public class ConfigurableEditorInput extends AbstractEntityEditorInput<Configurable>
{
	public ConfigurableEditorInput(final Configurable configurable)
	{
		super(configurable);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof ConfigurableEditorInput)
		{
			final ConfigurableEditorInput input = (ConfigurableEditorInput) object;
			final Configurable configurable = (Configurable) input.getAdapter(Configurable.class);
			if ((configurable.getId() != null) && configurable.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(Configurable.class.getName()))
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
		return entity.getType().toString();
	}

	@Override
	public AbstractEntity getParent()
	{
		return entity.getProfile();
	}

	@Override
	public String getToolTipText()
	{
		return entity.getType().toString();
	}

	@Override
	public boolean hasParent()
	{
		return entity.getProfile() != null;
	}
}
