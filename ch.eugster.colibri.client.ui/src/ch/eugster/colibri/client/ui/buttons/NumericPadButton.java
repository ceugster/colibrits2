/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.buttons;

import java.awt.Dimension;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.actions.ProfileAction;

public class NumericPadButton extends ProfileButton
{
	public static final long serialVersionUID = 0l;

	public static final Dimension MIN_BUTTON_SIZE = new Dimension(80, 60);

	public static final Dimension PREFERRED_BUTTON_SIZE = new Dimension(100, 80);

	public static final Dimension MAX_BUTTON_SIZE = new Dimension(500, 200);

	protected UserPanel userPanel;

	public NumericPadButton(final ProfileAction action, final UserPanel userPanel, final Profile profile)
	{
		super(action, profile);
		this.userPanel = userPanel;

		setMaximumSize(NumericPadButton.MAX_BUTTON_SIZE);
		setPreferredSize(NumericPadButton.PREFERRED_BUTTON_SIZE);
		setMinimumSize(NumericPadButton.MIN_BUTTON_SIZE);
		addActionListener(userPanel.getValueDisplay());
	}
}
