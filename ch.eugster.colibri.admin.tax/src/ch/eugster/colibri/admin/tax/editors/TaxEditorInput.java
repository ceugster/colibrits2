/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.tax.views.IModeSelectionListener;
import ch.eugster.colibri.admin.tax.views.TaxView.Mode;
import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Tax;

public class TaxEditorInput extends AbstractEntityEditorInput<Tax> implements IModeSelectionListener
{
	private Mode mode;

	public TaxEditorInput(final Tax tax, final Mode mode)
	{
		super(tax);
		this.mode = mode;
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof TaxEditorInput)
		{
			final TaxEditorInput input = (TaxEditorInput) object;
			final Tax tax = (Tax) input.getAdapter(Tax.class);
			if ((tax.getId() != null) && tax.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(Tax.class.getName()))
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
	public AbstractEntity getParent()
	{
		if (mode.equals(Mode.GROUP))
		{
			return entity.getTaxRate();
		}
		else if (mode.equals(Mode.TYPE))
		{
			return entity.getTaxType();
		}
		else
		{
			throw new RuntimeException("Invalid Tax.Mode");
		}
	}

	@Override
	public String getToolTipText()
	{
		return entity.format();
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}

	@Override
	public void modeSelected(final Mode mode)
	{
		this.mode = mode;
	}
}
