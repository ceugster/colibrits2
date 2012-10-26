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

public class HorizontalAlignMenu extends Menu
{
	private static final long serialVersionUID = 0l;
	
	public HorizontalAlignMenu(TabEditor editor, TabEditorButton button)
	{
		super("Horizontal");
		this.add(new SetAlignMenuItem(editor, button, "Links", Integer
						.toString(SwingConstants.LEADING), SwingConstants.HORIZONTAL));
		this.add(new SetAlignMenuItem(editor, button, "Mitte", Integer
						.toString(SwingConstants.CENTER), SwingConstants.HORIZONTAL));
		this.add(new SetAlignMenuItem(editor, button, "Rechts", Integer
						.toString(SwingConstants.TRAILING), SwingConstants.HORIZONTAL));
	}
}
