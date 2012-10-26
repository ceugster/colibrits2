/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.buttons;

import java.awt.Color;

import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.buttons.AbstractProfileButton;

public class ProfileEditorFailOverButton extends AbstractProfileButton
{
	public static final long serialVersionUID = 0l;

	public ProfileEditorFailOverButton(final String text, final Profile profile, final boolean failOver)
	{
		super(profile);
		setText(text);
	}

	@Override
	protected void updateNormal()
	{
		setBackground(new Color(profile.getButtonFailOverBg()));
		setForeground(new Color(profile.getButtonFailOverFg()));
		setFont(getFont().deriveFont(profile.getButtonFailOverFontStyle(), profile.getButtonFailOverFontSize()));
		setHorizontalAlignment(profile.getButtonFailOverHorizontalAlign());
		setVerticalAlignment(profile.getButtonFailOverVerticalAlign());
	}
}
