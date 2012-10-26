/*
 * Created on 2009 3 4
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.position;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.event.ListSelectionListener;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;

public class PositionPanel extends JPanel implements ActionListener
{
	public static final long serialVersionUID = 0l;

	private PositionListPanel positionListPanel;

	private PositionDetailPanel positionDetailPanel;

	public PositionPanel(final UserPanel userPanel, final Profile profile)
	{
		setLayout(new BorderLayout());

		positionListPanel = new PositionListPanel(userPanel, profile);
		this.add(positionListPanel, BorderLayout.CENTER);
		userPanel.setPositionListPanel(positionListPanel);
		userPanel.addStateChangeListener(positionListPanel);

		positionDetailPanel = new PositionDetailPanel(userPanel, profile);
		this.add(positionDetailPanel, BorderLayout.SOUTH);
		userPanel.setPositionDetailPanel(positionDetailPanel);

		positionListPanel.addListSelectionListener(positionDetailPanel);
		positionListPanel.getModel().addTableModelListener(positionDetailPanel);
		positionDetailPanel.addActionListener(positionListPanel);
	}

	public void actionPerformed(final ActionEvent event)
	{
		positionListPanel.actionPerformed(event);
		positionDetailPanel.actionPerformed(event);
	}

	public void addListSelectionListener(final ListSelectionListener listener)
	{
		positionListPanel.addListSelectionListener(listener);
	}

	public void removeListSelectionListener(final ListSelectionListener listener)
	{
		positionListPanel.removeListSelectionListener(listener);
	}
}
