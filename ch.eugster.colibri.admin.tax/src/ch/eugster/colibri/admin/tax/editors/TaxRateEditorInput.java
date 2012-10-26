/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.TaxRate;

public class TaxRateEditorInput extends AbstractEntityEditorInput<TaxRate>
{
	public TaxRateEditorInput(final TaxRate taxRate)
	{
		super(taxRate);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof TaxRateEditorInput)
		{
			final TaxRateEditorInput input = (TaxRateEditorInput) object;
			final TaxRate taxRate = (TaxRate) input.getAdapter(TaxRate.class);
			if ((taxRate.getId() != null) && taxRate.getId().equals(entity.getId()))
			{
				return true;
			}
		}

		return false;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class adapter)
	{
		if (adapter.getName().equals(TaxRate.class.getName()))
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
