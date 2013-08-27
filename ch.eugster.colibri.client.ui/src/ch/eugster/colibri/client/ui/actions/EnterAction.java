/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.GregorianCalendar;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.PaymentWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.numeric.ValueDisplay;
import ch.eugster.colibri.persistence.model.Profile;

public class EnterAction extends UserPanelProfileAction
{
	private static final long serialVersionUID = 0l;

	public static final String DEFAULT_TEXT = "Zahlungen";

	public static final String ACTION_COMMAND = "enter.action";

	public EnterAction(final UserPanel userPanel, final Profile profile)
	{
		super(EnterAction.DEFAULT_TEXT, EnterAction.ACTION_COMMAND, userPanel, profile);
		this.userPanel.getPositionWrapper().addPropertyChangeListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand().equals(EnterAction.ACTION_COMMAND))
		{
			performEnterAction(event);
		}
	}

	private void performEnterAction(final ActionEvent event)
	{
		final UserPanel.State state = userPanel.getCurrentState();

		if (state.equals(UserPanel.State.POSITION_INPUT))
		{
			if (userPanel.getPositionWrapper().isPositionComplete())
			{
				userPanel.getPositionListPanel().getModel().actionPerformed(event);
			}
			else
			{
				if (userPanel.getValueDisplay().getText().isEmpty())
				{
					userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.PAYMENT_INPUT));
				}
				else
				{
					if (testForBarcode())
					{
						updateBarcode();
					}
					else if (testForQuantity())
					{
						updateQuantity();
					}
					else if (testForPrice())
					{
						updatePrice();
					}
				}
			}
		}
		else if (state.equals(UserPanel.State.PAYMENT_INPUT))
		{
			final PaymentWrapper wrapper = userPanel.getPaymentWrapper();
			final ValueDisplay display = userPanel.getValueDisplay();

			if (wrapper.isPaymentComplete())
			{
				// this.fireActionPerformed(event);
			}
			else if (display.getText().equals(""))
			{
				userPanel.fireStateChange(new StateChangeEvent(state, UserPanel.State.POSITION_INPUT));
			}
		}
	}

	private boolean testForPrice()
	{
		if (userPanel.getPositionWrapper().doesPositionNeedPrice())
		{
			final double maxRange = Math.abs(userPanel.getMainTabbedPane().getSetting().getMaxPriceRange());
			final double price = Math.abs(userPanel.getValueDisplay().testAmount());
			if ((maxRange == 0) || ((price > 0) && (price <= maxRange)))
			{
				firePropertyChange("label", null, "Preis");
				return true;
			}
		}
		return false;
	}

	private boolean testForQuantity()
	{
		if (userPanel.getPositionWrapper().doesPositionNeedQuantity())
		{
			final int maxRange = Math.abs(userPanel.getMainTabbedPane().getSetting().getMaxQuantityRange());
			final int quantity = Math.abs(userPanel.getValueDisplay().testQuantity());
			if ((maxRange == 0) || ((quantity > 0) && (quantity <= maxRange)))
			{
				firePropertyChange("label", null, "Menge");
				return true;
			}
		}

		return false;
	}

	private boolean testForBarcode()
	{
		return userPanel.getValueDisplay().testBarcode() != null;
	}

	private void updatePrice()
	{
		if (userPanel.getPositionWrapper().doesPositionNeedPrice())
		{
			final double maxRange = Math.abs(userPanel.getMainTabbedPane().getSetting().getMaxPriceRange());
			final double price = Math.abs(userPanel.getValueDisplay().testAmount());
			if ((maxRange == 0) || ((price > 0) && (price <= maxRange)))
			{
				userPanel.getPositionDetailPanel().getPriceButton().doClick();
			}
		}
	}

	private void updateQuantity()
	{
		if (userPanel.getPositionWrapper().doesPositionNeedQuantity())
		{
			final int maxRange = Math.abs(userPanel.getMainTabbedPane().getSetting().getMaxQuantityRange());
			final int quantity = Math.abs(userPanel.getValueDisplay().testQuantity());
			if ((maxRange == 0) || ((quantity > 0) && (quantity <= maxRange)))
			{
				userPanel.getPositionDetailPanel().getQuantityButton().doClick();
			}
		}
	}

	private void updateBarcode()
	{
		userPanel.getPositionWrapper().keyPressed(new KeyEvent(this.userPanel, KeyEvent.VK_ENTER, GregorianCalendar.getInstance().getTimeInMillis(), 0, KeyEvent.VK_ENTER, (char)0xd));
	}
}
