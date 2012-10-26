/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.TaxType;

public class TaxTypeEditorInput extends AbstractEntityEditorInput<TaxType>
{
	public TaxTypeEditorInput(final TaxType taxType)
	{
		super(taxType);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof TaxTypeEditorInput)
		{
			final TaxTypeEditorInput input = (TaxTypeEditorInput) object;
			final TaxType taxType = (TaxType) input.getAdapter(TaxType.class);
			if ((taxType.getId() != null) && taxType.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(TaxType.class.getName()))
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
		return entity.getName();
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
