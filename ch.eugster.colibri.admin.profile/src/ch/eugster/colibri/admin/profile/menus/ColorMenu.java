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

public class ColorMenu extends Menu
{
	private static final long serialVersionUID = 0l;
	
	public ColorMenu(TabEditor editor, TabEditorButton button)
	{
		super("Farben");
		this.add(new SetColorMenuItem(editor, button, "Schrift", "foreground"));
		this.add(new SetColorMenuItem(editor, button, "Hintergrund", "background"));
	}
}
