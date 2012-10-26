/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.payment.editors;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.ui.editors.AbstractEntityEditorInput;
import ch.eugster.colibri.persistence.model.PaymentType;

public class PaymentEditorInput extends AbstractEntityEditorInput<PaymentType>
{
	public PaymentEditorInput(final PaymentType paymentType)
	{
		super(paymentType);
	}

	@Override
	public boolean equals(final Object object)
	{
		if (object instanceof PaymentEditorInput)
		{
			final PaymentEditorInput input = (PaymentEditorInput) object;
			final PaymentType paymentType = (PaymentType) input.getAdapter(PaymentType.class);
			if ((paymentType.getId() != null) && paymentType.getId().equals(entity.getId()))
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
		if (adapter.getName().equals(PaymentType.class.getName()))
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
