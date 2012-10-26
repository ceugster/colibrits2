/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Payment;

public class PaymentChangeMediator implements PaymentChangeListener
{
	private PropertyChangeListener propertyChangeListener;

	private String[] properties;

	public PaymentChangeMediator(final UserPanel userPanel, final PropertyChangeListener propertyChangeListener, final String[] properties)
	{
		this.propertyChangeListener = propertyChangeListener;
		this.properties = properties;
		userPanel.getPaymentWrapper().addPaymentChangeListener(this);
	}

	public void paymentChange(final PaymentChangeEvent event)
	{
		if (event.getOldPayment() != null)
		{
			final Payment oldPayment = event.getOldPayment();
			for (final String property : properties)
			{
				oldPayment.removePropertyChangeListener(property, propertyChangeListener);
			}
		}

		if (event.getNewPayment() != null)
		{
			final Payment newPayment = event.getNewPayment();
			for (final String property : properties)
			{
				newPayment.addPropertyChangeListener(property, propertyChangeListener);
			}
		}

		propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "payment", event.getOldPayment(), event.getNewPayment()));
	}

}
