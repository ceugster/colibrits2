/*
 * Created on 2009 3 4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.payment.PaymentPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.position.PositionPanel;
import ch.eugster.colibri.persistence.model.Profile;

public class InfoListPanel extends JPanel implements StateChangeListener, ActionListener
{
	public static final long serialVersionUID = 0l;

	private static final String POSITION_PANEL = "position.panel";

	private static final String PAYMENT_PANEL = "payment.panel";

	private PositionPanel positionPanel;

	private PaymentPanel paymentPanel;

	private CardLayout layout;

	public InfoListPanel(final UserPanel userPanel, final Profile profile)
	{
		layout = new CardLayout();
		setLayout(layout);

		positionPanel = new PositionPanel(userPanel, profile);
		this.add(InfoListPanel.POSITION_PANEL, positionPanel);

		paymentPanel = new PaymentPanel(userPanel, profile);
		this.add(InfoListPanel.PAYMENT_PANEL, paymentPanel);
	}

	public void actionPerformed(final ActionEvent event)
	{
		positionPanel.actionPerformed(event);
		paymentPanel.actionPerformed(event);
	}

	public void addPaymentListSelectionListener(final ListSelectionListener listener)
	{
		paymentPanel.addListSelectionListener(listener);
	}

	public void addPositionListSelectionListener(final ListSelectionListener listener)
	{
		positionPanel.addListSelectionListener(listener);
	}

	public void removePaymentListSelectionListener(final ListSelectionListener listener)
	{
		paymentPanel.removeListSelectionListener(listener);
	}

	public void removePositionListSelectionListener(final ListSelectionListener listener)
	{
		positionPanel.removeListSelectionListener(listener);
	}

	public void stateChange(final StateChangeEvent event)
	{
		if (event.getNewState() != null)
		{
			if (event.getNewState().equals(UserPanel.State.POSITION_INPUT))
			{
				layout.show(this, InfoListPanel.POSITION_PANEL);
			}
			else if (event.getNewState().equals(UserPanel.State.PAYMENT_INPUT))
			{
				layout.show(this, InfoListPanel.PAYMENT_PANEL);
			}
		}
	}
}
