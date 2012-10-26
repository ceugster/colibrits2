/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.editors.tab;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Tab;

public class TabEditorInput extends AbstractEntityEditorInput<Tab>
{
	public TabEditorInput(final Tab tab)
	{
		super(tab);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof TabEditorInput)
		{
			final TabEditorInput input = (TabEditorInput) object;
			final Tab tab = (Tab) input.getAdapter(Tab.class);
			if ((tab.getId() != null) && tab.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(Tab.class.getName()))
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
	public void setParent(AbstractEntity parent)
	{
		this.entity.setConfigurable((Configurable) parent);
	}

	@Override
	public AbstractEntity getParent()
	{
		return entity.getConfigurable();
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
