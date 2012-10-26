/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.Font;
import java.awt.Menu;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class FontStyleMenu extends Menu
{
	private static final long serialVersionUID = 0l;
	
	public FontStyleMenu(TabEditor editor, TabEditorButton button)
	{
		super("Schriftstil");
		this.add(new SetFontStyleMenuItem(editor, button, "Normal", "style.normal", Font.PLAIN));
		this.add(new SetFontStyleMenuItem(editor, button, "Fett", "style.bold", Font.BOLD));
		this.add(new SetFontStyleMenuItem(editor, button, "Kursiv", "style.italic", Font.ITALIC));
	}
}
