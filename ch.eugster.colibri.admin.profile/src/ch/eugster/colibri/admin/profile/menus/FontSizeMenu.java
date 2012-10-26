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

public class FontSizeMenu extends Menu
{
	private static final long serialVersionUID = 0l;
	
	public FontSizeMenu(TabEditor editor, TabEditorButton button, int min, int max)
	{
		super("Schriftgrösse");
		
		for (int i = min; i < max; i++)
		{
			this.add(new SetFontSizeMenuItem(editor, button, Integer.toString(i), Integer.toString(i),
							i));
		}
	}
}
