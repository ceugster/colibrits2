/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.buttons;

import java.awt.Dimension;

import ch.eugster.colibri.client.ui.actions.UserPanelProfileAction;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;

public class PositionButton extends ProfileButton
{
	public static final long serialVersionUID = 0l;

	private static final Dimension MIN_BUTTON_SIZE = new Dimension(60, 40);

	private static final Dimension PREFERRED_BUTTON_SIZE = new Dimension(80, 60);

	private static final Dimension MAX_BUTTON_SIZE = new Dimension(480, 360);

	private final UserPanel userPanel;

	public PositionButton(final UserPanelProfileAction action, final UserPanel userPanel, final Profile profile, boolean isFailOver)
	{
		super(action, profile, isFailOver);
		this.userPanel = userPanel;
		this.userPanel.getValueDisplay().addPropertyChangeListener("value", action);

		setMaximumSize(PositionButton.MAX_BUTTON_SIZE);
		setPreferredSize(PositionButton.PREFERRED_BUTTON_SIZE);
		setMinimumSize(PositionButton.MIN_BUTTON_SIZE);
	}
}
