/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.MainTabbedPane;
import ch.eugster.colibri.client.ui.panels.user.PositionWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;

public class RestitutionAction extends ConfigurableAction implements PropertyChangeListener, ListSelectionListener
{
	private static final long serialVersionUID = 0l;

	private int maxRange = 0;

	private int maxAmount = 0;

	private final String[] positionProperties = new String[] { PositionWrapper.KEY_PROPERTY_QUANTITY };

	public RestitutionAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		this.maxRange = Math.abs(MainTabbedPane.getTabbedPane().getSetting().getMaxQuantityRange());
		this.maxAmount = Math.abs(MainTabbedPane.getTabbedPane().getSetting().getMaxQuantityAmount());
		new PositionChangeMediator(userPanel, this, this.positionProperties);
		userPanel.getValueDisplay().addPropertyChangeListener(this);
		userPanel.getPositionListPanel().getModel().getSelectionListModel().addListSelectionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		int quantity = this.userPanel.getValueDisplay().testQuantity();
		if (quantity == 0)
		{
			quantity = this.userPanel.getPositionWrapper().getPosition().getQuantity();
		}

		int result = ch.eugster.colibri.client.ui.dialogs.MessageDialog.BUTTON_YES;
		if ((this.maxAmount > 0) && (this.maxAmount < Math.abs(quantity)))
		{
			final String title = "Eingabeüberprüfung";
			final String message = "Die eingegebene Menge ist sehr hoch. Soll sie trotzdem akzeptiert werden?";
			final int messageType = ch.eugster.colibri.client.ui.dialogs.MessageDialog.TYPE_QUESTION;
			result = ch.eugster.colibri.client.ui.dialogs.MessageDialog.showSimpleDialog(Activator.getDefault()
					.getFrame(), this.userPanel.getProfile(), title, message, messageType);
		}
		if (result == ch.eugster.colibri.client.ui.dialogs.MessageDialog.BUTTON_YES)
		{
			quantity = this.userPanel.getValueDisplay().getQuantity();
			if (quantity == 0)
			{
				quantity = this.userPanel.getPositionWrapper().getPosition().getQuantity();
			}

			this.userPanel.getPositionWrapper().getPosition().setQuantity(-quantity);
			if (this.userPanel.getPositionWrapper().getPosition().getQuantity() < 0)
			{
				if (this.userPanel.getPositionWrapper().getPosition().getReceipt().getCustomer() == null)
				{
					final String title = "Kunde";
					final String message = "Kundennummer erfassen nicht vergessen!";
					final int messageType = ch.eugster.colibri.client.ui.dialogs.MessageDialog.TYPE_INFORMATION;
					result = ch.eugster.colibri.client.ui.dialogs.MessageDialog.showSimpleDialog(Activator.getDefault()
							.getFrame(), this.userPanel.getProfile(), title, message, messageType);
				}
			}
		}
	}

	@Override
	public boolean getState(final StateChangeEvent event)
	{
		boolean enabled = super.getState(event);
		if (enabled)
		{
			if (event.getNewState().equals(UserPanel.State.POSITION_INPUT))
			{
				enabled = this.shouldEnable();
			}
		}
		return enabled;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getSource().equals(this.userPanel.getValueDisplay()))
		{
			if (event.getPropertyName().equals("value"))
			{
				if (!this.userPanel.getCurrentState().equals(UserPanel.State.LOCKED))
				{
					this.setEnabled(this.shouldEnable());
				}
			}
		}
		else if (event.getSource().equals(this.userPanel.getPositionWrapper().getPosition()))
		{
			if (event.getPropertyName().equals(PositionWrapper.KEY_PROPERTY_QUANTITY))
			{
				this.setEnabled(this.shouldEnable());
			}
		}
	}

	@Override
	public void valueChanged(final ListSelectionEvent event)
	{
		this.setEnabled(this.shouldEnable());
	}

	private boolean shouldEnable()
	{
		int quantity = 0;
		if (this.userPanel.getPositionListPanel().getModel().getSelectionListModel().getMinSelectionIndex() > -1)
		{
			return true;
		}
		else
		{
			quantity = Math.abs(this.userPanel.getPositionWrapper().getPosition().getQuantity());
			if (quantity == 0)
			{
				quantity = Math.abs(this.userPanel.getValueDisplay().testQuantity());
			}
			return (quantity > 0) && ((this.maxRange == 0) || (quantity < this.maxRange));
		}
	}
}
