/*
 * Created on 2009 3 4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.display;

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JPanel;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;

public class ReceivedAndRemainderPanel extends JPanel implements StateChangeListener, PropertyChangeListener
{
	public static final long serialVersionUID = 0l;

	private ReceivedPanel receivedPanel;

	private RemainderPanel remainderPanel;

	// private boolean showAlways = false;

	public ReceivedAndRemainderPanel(final UserPanel userPanel, final Profile profile)
	{
		// this.showAlways = profile.getDisplayShowReceivedRemainderAlways();

		setLayout(new GridLayout(2, 1));

		receivedPanel = new ReceivedPanel(userPanel, profile);
		this.add(receivedPanel);

		remainderPanel = new RemainderPanel(userPanel, profile);
		this.add(remainderPanel);

		// this.showAlways = profile.isDisplayShowReceivedRemainderAlways();
	}

	public void propertyChange(final PropertyChangeEvent event)
	{
		receivedPanel.propertyChange(event);
		remainderPanel.propertyChange(event);
	}

	public void stateChange(final StateChangeEvent event)
	{
		// TODO
		setVisible(true);
		// if (!this.showAlways)
		// this.setVisible(this.receivedPanel.getDefaultCurrencyAmount() != 0);
	}
}
