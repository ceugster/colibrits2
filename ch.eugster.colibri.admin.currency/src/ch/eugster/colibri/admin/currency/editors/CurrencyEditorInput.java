/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.currency.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Currency;

public class CurrencyEditorInput extends AbstractEntityEditorInput<Currency>
{
	public CurrencyEditorInput(final Currency currency)
	{
		super(currency);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof CurrencyEditorInput)
		{
			final CurrencyEditorInput input = (CurrencyEditorInput) object;
			final Currency currency = (Currency) input.getAdapter(Currency.class);
			if ((currency.getId() != null) && currency.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(Currency.class.getName()))
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
		return null;
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
