/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SetOtherFontStyleMenuItem extends AbstractSetOtherMenuItem
{
	private static final long serialVersionUID = 0l;

	public SetOtherFontStyleMenuItem(final TabEditor editor, final TabEditorButton button, final String label, final String actionCommand)
	{
		super(editor, button, label, actionCommand);
	}

	@Override
	protected void updateNormalButton(TabEditorButton button) 
	{
		button.getKeys()[1].setNormalFontStyle(this.button.getKeys()[1].getNormalFontStyle());
	}

	@Override
	protected void updateFailOverButton(TabEditorButton button) 
	{
		button.getKeys()[1].setFailOverFontStyle(this.button.getKeys()[1].getFailOverFontStyle());
	}

}
