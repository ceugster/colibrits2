/*
 * Created on 14.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.MenuItem;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class DefaultPopupMenu extends KeyPopupMenu
{
	public static final long serialVersionUID = 0l;

	public DefaultPopupMenu(final TabEditorButton button, TabEditor editor)
	{
		super(button, editor);
	}

	@Override
	protected void init()
	{
		this.add(new KeyTypeMenuItem(this.editor, this.button));

		this.addSeparator();
		/*
		 * Schriftgrösse
		 */
		this.add(new SelectFontSizeTargetMenu(this.editor, this.button));
		/*
		 * Schriftstil
		 */
		this.add(new SelectFontStyleTargetMenu(this.editor, this.button));

		this.addSeparator();
		/*
		 * Farbe
		 */
		this.add(new SelectForegroundColorTargetMenu(this.editor, this.button));
		this.add(new SelectBackgroundColorTargetMenu(this.editor, this.button));

		this.addSeparator();
		/*
		 * Ausrichtung
		 */
		this.add(new AlignMenu(this.editor, this.button));

		this.addSeparator();
		/*
		 * Standard wiederherstellen
		 */
		this.add(new ResetToStandardMenuItem(this.editor, this.button));

		this.addSeparator();
		/*
		 * Entfernen
		 */
		if (this.button.getKeys()[1] != null && !this.button.getKeys()[1].isDeleted())
		{
			MenuItem menuItem = new DeleteMenuItem(this.button, "Entfernen", "button.delete");
			this.add(menuItem);
		}
	}
}
