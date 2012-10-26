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
import ch.eugster.colibri.persistence.model.Receipt;

public class ReceiptChangeMediator implements ReceiptChangeListener
{
	private PropertyChangeListener propertyChangeListener;

	private UserPanel userPanel;

	private String[] properties;

	public ReceiptChangeMediator(final UserPanel userPanel, final PropertyChangeListener propertyChangeListener, final String[] properties)
	{
		this.propertyChangeListener = propertyChangeListener;
		this.userPanel = userPanel;
		this.properties = properties;
		this.userPanel.getReceiptWrapper().addReceiptChangeListener(this);
	}

	public void dispose()
	{
		propertyChangeListener = null;
		userPanel.getReceiptWrapper().removeReceiptChangeListener(this);
	}

	public void receiptChange(final ReceiptChangeEvent event)
	{
		if (event.getOldReceipt() != null)
		{
			final Receipt oldReceipt = event.getOldReceipt();
			for (final String property : properties)
			{
				oldReceipt.removePropertyChangeListener(property, propertyChangeListener);
			}
		}

		if (event.getNewReceipt() != null)
		{
			final Receipt newReceipt = event.getNewReceipt();
			for (final String property : properties)
			{
				newReceipt.addPropertyChangeListener(property, propertyChangeListener);
			}
		}

		propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "receipt", event.getOldReceipt(), event.getNewReceipt()));
	}
}
