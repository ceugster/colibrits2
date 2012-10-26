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

public class ProfileEditorNormalButton extends AbstractProfileButton
{
	public static final long serialVersionUID = 0l;

	private Profile profile;

	public ProfileEditorNormalButton(final String text, final Profile profile, final boolean failOver)
	{
		super(profile);
		setText(text);
	}

	@Override
	protected void updateFailOver()
	{
		setBackground(new Color(profile.getButtonNormalBg()));
		setForeground(new Color(profile.getButtonNormalFg()));
		setFont(getFont().deriveFont(profile.getButtonNormalFontStyle(), profile.getButtonNormalFontSize()));
		setHorizontalAlignment(profile.getButtonNormalHorizontalAlign());
		setVerticalAlignment(profile.getButtonNormalVerticalAlign());
	}

}
