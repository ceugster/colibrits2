/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;

public class ExternalProductGroupEditorInput extends AbstractEntityEditorInput<ExternalProductGroup>
{
	public ExternalProductGroupEditorInput(final ExternalProductGroup productGroup)
	{
		super(productGroup);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof ExternalProductGroupEditorInput)
		{
			final ExternalProductGroupEditorInput input = (ExternalProductGroupEditorInput) object;
			final ExternalProductGroup productGroup = (ExternalProductGroup) input.getAdapter(ExternalProductGroup.class);
			if ((productGroup.getId() != null) && productGroup.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(ExternalProductGroup.class.getName()))
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
		return null;
	}

	@Override
	public String getName()
	{
		return entity.getCode();
	}

	@Override
	public String getToolTipText()
	{
		return entity.getText();
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
