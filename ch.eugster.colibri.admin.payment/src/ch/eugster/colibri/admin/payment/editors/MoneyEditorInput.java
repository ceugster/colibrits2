/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.payment.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Money;

public class MoneyEditorInput extends AbstractEntityEditorInput<Money>
{
	public MoneyEditorInput(final Money money)
	{
		super(money);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof MoneyEditorInput)
		{
			final MoneyEditorInput input = (MoneyEditorInput) object;
			final Money money = (Money) input.getAdapter(Money.class);
			if ((money.getId() != null) && money.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(Money.class.getName()))
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
		return entity.getPaymentType().getCurrency().getCode() + " " + entity.getValue();
	}

	@Override
	public AbstractEntity getParent()
	{
		return entity.getPaymentType();
	}

	@Override
	public String getToolTipText()
	{
		return entity.getPaymentType().getCurrency().getCode() + " " + entity.getValue();
	}

	@Override
	public boolean hasParent()
	{
		return false;
	}
}
