/*
 * Created on 14.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.MenuItem;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.admin.profile.editors.tab.DrawerLabelProvider;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;
import ch.eugster.colibri.admin.ui.filters.UsedCurrenciesOnlyViewerFilter;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class DrawerPopupMenu extends KeyPopupMenu
{
	public static final long serialVersionUID = 0l;

	public DrawerPopupMenu(final TabEditorButton button, final TabEditor editor)
	{
		super(button, editor);
	}

	@Override
	protected void init()
	{
		this.add(new KeyTypeMenuItem(editor, button));

		addSeparator();
		/*
		 * Drawer
		 */

		final LabelProvider labelProvider = new DrawerLabelProvider();
		final ViewerFilter[] filters = new ViewerFilter[] { new DeletedEntityViewerFilter(), new UsedCurrenciesOnlyViewerFilter() };
		this.add(new SelectCurrencyMenuItem(editor, button, "Schublade...", "action.drawer", labelProvider, filters, "Schublade"));

		addSeparator();
		/*
		 * Name
		 */
		this.add(new SetNameMenuItem(editor, button, "Beschriftung", "action.name"));

		addSeparator();
		/*
		 * Schriftgrösse
		 */
		this.add(new FontSizeMenu(editor, button, 8, 25));
		/*
		 * Schriftstil
		 */
		this.add(new FontStyleMenu(editor, button));

		addSeparator();
		/*
		 * Farbe
		 */
		this.add(new ColorMenu(editor, button));
		/*
		 * Ausrichtung
		 */
		this.add(new AlignMenu(editor, button));

		addSeparator();
		/*
		 * Standard wiederherstellen
		 */
		this.add(new ResetToStandardMenuItem(editor, button));

		addSeparator();
		/*
		 * Entfernen
		 */
		if ((button.getKeys()[1] != null) && !button.getKeys()[1].isDeleted())
		{
			final MenuItem menuItem = new DeleteMenuItem(button, "Entfernen", "button.delete");
			this.add(menuItem);
		}
	}
}
