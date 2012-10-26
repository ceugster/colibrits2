/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CurrentTax;

public class CurrentTaxEditorInput extends AbstractEntityEditorInput<CurrentTax>
{
	public CurrentTaxEditorInput(final CurrentTax currentTax)
	{
		super(currentTax);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof CurrentTaxEditorInput)
		{
			final CurrentTaxEditorInput input = (CurrentTaxEditorInput) object;
			final CurrentTax currentTax = (CurrentTax) input.getAdapter(CurrentTax.class);
			if ((currentTax.getId() != null) && currentTax.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(CurrentTax.class.getName()))
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
		return entity.getTax().getCode();
	}

	@Override
	public AbstractEntity getParent()
	{
		return entity.getTax();
	}

	@Override
	public String getToolTipText()
	{
		return CurrentTax.format(entity);
	}

	@Override
	public boolean hasParent()
	{
		return true;
	}
}
