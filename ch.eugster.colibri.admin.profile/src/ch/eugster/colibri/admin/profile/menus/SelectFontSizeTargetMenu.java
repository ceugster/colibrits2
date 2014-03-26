/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.Menu;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SelectFontSizeTargetMenu extends Menu
{
	private static final long serialVersionUID = 0l;
	
	public SelectFontSizeTargetMenu(TabEditor editor, TabEditorButton button)
	{
		super("Schriftgrösse");
		this.add(new FontSizeMenu(editor, button, 8, 25));
		this.add(new SetOtherFontSizeMenuItem(editor, button, "Andere Buttons auf diese Schriftgrösse setzen", "set.other.size"));
	}
}
