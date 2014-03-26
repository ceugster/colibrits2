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

public class SelectBackgroundColorTargetMenu extends Menu
{
	private static final long serialVersionUID = 0l;
	
	public SelectBackgroundColorTargetMenu(TabEditor editor, TabEditorButton button)
	{
		super("Hintergrundfarbe");
		this.add(new SetColorMenuItem(editor, button, "Hintergrundfarbe", "background"));
		this.add(new SetOtherBackgroundColorMenuItem(editor, button, "Andere Buttons auf diese Hintergrundfarbe setzen", "set.other.bg.color"));
	}
}
