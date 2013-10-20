/*
 * Created on 2009 3 4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.display.ReceivedAndRemainderPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.display.TotalPanel;
import ch.eugster.colibri.persistence.model.Profile;

public class InfoPanel extends JPanel implements StateChangeListener, ActionListener
{
	public static final long serialVersionUID = 0l;

	private TotalPanel totalPanel;

	private ReceivedAndRemainderPanel receivedAndRemainderPanel;

	private InfoListPanel infoListPanel;

	private Collection<StateChangeListener> stateChangeListeners = new ArrayList<StateChangeListener>();

	public InfoPanel(final UserPanel userPanel, final Profile profile)
	{
		setLayout(new BorderLayout());

		final JPanel displayPanel = new JPanel();
		displayPanel.setLayout(new BorderLayout());

		totalPanel = new TotalPanel(userPanel, profile);
		displayPanel.add(totalPanel, BorderLayout.CENTER);
		userPanel.getNumericPadPanel().addActionListener(totalPanel);

		receivedAndRemainderPanel = new ReceivedAndRemainderPanel(userPanel, profile);
		addStateChangeListener(receivedAndRemainderPanel);
		displayPanel.add(receivedAndRemainderPanel, BorderLayout.SOUTH);
		this.add(displayPanel, BorderLayout.NORTH);

		infoListPanel = new InfoListPanel(userPanel, profile);
		addStateChangeListener(infoListPanel);
		this.add(infoListPanel, BorderLayout.CENTER);
	}

	public void actionPerformed(final ActionEvent event)
	{
		infoListPanel.actionPerformed(event);
	}

	public void addPaymentListSelectionListener(final ListSelectionListener listener)
	{
		infoListPanel.addPaymentListSelectionListener(listener);
	}

	public void addPositionListSelectionListener(final ListSelectionListener listener)
	{
		infoListPanel.addPositionListSelectionListener(listener);
	}

	public void addStateChangeListener(final StateChangeListener listener)
	{
		if (listener != null)
		{
			if (!stateChangeListeners.contains(listener))
			{
				stateChangeListeners.add(listener);
			}
		}
	}

	public void fireStateChange(final StateChangeEvent event)
	{
		final StateChangeListener[] listeners = stateChangeListeners.toArray(new StateChangeListener[0]);
		for (final StateChangeListener listener : listeners)
		{
			listener.stateChange(event);
		}
	}

	public void removePaymentListSelectionListener(final ListSelectionListener listener)
	{
		infoListPanel.removePaymentListSelectionListener(listener);
	}

	public void removePositionListSelectionListener(final ListSelectionListener listener)
	{
		infoListPanel.removePositionListSelectionListener(listener);
	}

	public void removeStateChangeListener(final StateChangeListener listener)
	{
		if (listener != null)
		{
			if (stateChangeListeners.contains(listener))
			{
				stateChangeListeners.remove(listener);
			}
		}
	}

	public void stateChange(final StateChangeEvent event)
	{
		fireStateChange(event);
	}
}
