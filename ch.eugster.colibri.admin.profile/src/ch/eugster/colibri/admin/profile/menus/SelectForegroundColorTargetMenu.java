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

public class SelectForegroundColorTargetMenu extends Menu
{
	private static final long serialVersionUID = 0l;
	
	public SelectForegroundColorTargetMenu(TabEditor editor, TabEditorButton button)
	{
		super("Schriftfarbe");
		this.add(new SetColorMenuItem(editor, button, "Schriftfarbe", "foreground"));
		this.add(new SetOtherForegroundColorMenuItem(editor, button, "Andere Buttons auf diese Schriftfarbe setzen", "set.other.fg.color"));
	}
}
