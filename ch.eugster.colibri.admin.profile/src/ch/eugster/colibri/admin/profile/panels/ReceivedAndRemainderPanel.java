/*
 * Created on 2009 3 4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.panels;

import java.awt.GridLayout;

import javax.swing.JPanel;

import ch.eugster.colibri.persistence.model.Profile;

public class ReceivedAndRemainderPanel extends JPanel
{
	public static final long serialVersionUID = 0l;

	private ReceivedPanel receivedPanel;

	private RemainderPanel remainderPanel;

	// private boolean showAlways = false;

	public ReceivedAndRemainderPanel(final Profile profile)
	{
		// this.showAlways = profile.getDisplayShowReceivedRemainderAlways();

		setLayout(new GridLayout(2, 1));

		receivedPanel = new ReceivedPanel(profile);
		this.add(receivedPanel);

		remainderPanel = new RemainderPanel(profile);
		this.add(remainderPanel);

		// this.showAlways = profile.isDisplayShowReceivedRemainderAlways();
	}
}
