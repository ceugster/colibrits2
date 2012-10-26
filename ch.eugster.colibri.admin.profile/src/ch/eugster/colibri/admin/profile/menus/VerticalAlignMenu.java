/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.Menu;

import javax.swing.SwingConstants;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class VerticalAlignMenu extends Menu
{
	private static final long serialVersionUID = 0l;
	
	public VerticalAlignMenu(TabEditor editor, TabEditorButton button)
	{
		super("Vertikal");
		this.add(new SetAlignMenuItem(editor, button, "Oben", Integer
						.toString(SwingConstants.LEADING), SwingConstants.TOP));
		this.add(new SetAlignMenuItem(editor, button, "Mitte", Integer
						.toString(SwingConstants.CENTER), SwingConstants.HORIZONTAL));
		this.add(new SetAlignMenuItem(editor, button, "Unten", Integer
						.toString(SwingConstants.TRAILING), SwingConstants.BOTTOM));
	}
}
