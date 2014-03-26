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

import ch.eugster.colibri.admin.profile.editors.tab.ProductGroupLabelProvider;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;
import ch.eugster.colibri.admin.ui.filters.ProductGroupTypeViewerFilter;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class SelectCustomerPopupMenu extends KeyPopupMenu
{
	public static final long serialVersionUID = 0l;

	public SelectCustomerPopupMenu(TabEditorButton button, TabEditor editor)
	{
		super(button, editor);
	}

	@Override
	protected void init()
	{
		this.add(new KeyTypeMenuItem(this.editor, this.button));

		addSeparator();
		/*
		 * Währung
		 */
		final LabelProvider labelProvider = new ProductGroupLabelProvider();
		final ViewerFilter[] filters = new ViewerFilter[] { new DeletedEntityViewerFilter(), new ProductGroupTypeViewerFilter(ProductGroupType.NON_SALES_RELATED) };
		this.add(new SelectProductGroupMenuItem(editor, button, "Warengruppe...", "action.group", labelProvider, filters, "Warengruppe"));

		addSeparator();
		/*
		 * Name
		 */
		this.add(new SetNameMenuItem(this.editor, this.button, "Beschriftung", "action.name"));

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
