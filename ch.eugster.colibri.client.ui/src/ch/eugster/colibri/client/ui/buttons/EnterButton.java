/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.buttons;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.eugster.colibri.client.ui.actions.EnterAction;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.panels.user.PositionWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;

public class EnterButton extends NumericPadButton implements PropertyChangeListener
{
	public static final long serialVersionUID = 0l;

	private final String[] positionProperties = new String[] { PositionWrapper.KEY_PROPERTY_PRICE, PositionWrapper.KEY_PROPERTY_DISCOUNT,
			PositionWrapper.KEY_PROPERTY_QUANTITY, PositionWrapper.KEY_PROPERTY_ROUND_FACTOR, PositionWrapper.KEY_PROPERTY_ORDERED,
			PositionWrapper.KEY_PROPERTY_OPTION, PositionWrapper.KEY_PROPERTY_QUOTATION, PositionWrapper.KEY_PROPERTY_BOOK_SERVER,
			PositionWrapper.KEY_PROPERTY_RECEIPT, PositionWrapper.KEY_PROPERTY_PRODUCT_GROUP, PositionWrapper.KEY_PROPERTY_CURRENT_TAX,
			PositionWrapper.KEY_PROPERTY_CURRENCY, PositionWrapper.KEY_PROPERTY_SEARCH_VALUE, PositionWrapper.KEY_PROPERTY_PRODUCT };

	public EnterButton(final EnterAction action, final UserPanel userPanel, final Profile profile)
	{
		super(action, userPanel, profile);
		addActionListener(this.userPanel.getValueDisplay());
		new PositionChangeMediator(this.userPanel, this, positionProperties);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		this.updateLabel();
	}

	public boolean updateLabel(final String text)
	{
		setText(text);
		return true;
	}

	private boolean testForSelectedPaymentInList()
	{
		return false;
	}

	private boolean testForSelectedPositionInList()
	{
		return userPanel.getPositionListPanel().getModel().getSelectionListModel().getMinSelectionIndex() >= 0;
	}

	private void updateLabel()
	{
		if (userPanel.getCurrentState() != null)
		{
			if (userPanel.getCurrentState().equals(UserPanel.State.POSITION_INPUT))
			{
				if (testForSelectedPositionInList())
				{
					this.updateLabel("Aktualisieren");
				}
				else
				{
					this.updateLabel("Zahlungen");
				}
			}
			else if (userPanel.getCurrentState().equals(UserPanel.State.PAYMENT_INPUT))
			{
				if (!testForSelectedPaymentInList())
				{
					setText("Positionen");
				}
			}
		}
	}
}
