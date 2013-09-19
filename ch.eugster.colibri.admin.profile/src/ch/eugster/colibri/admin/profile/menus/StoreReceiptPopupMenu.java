/*
 * Created on 14.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerFilter;

import ch.eugster.colibri.admin.profile.editors.tab.StoreReceiptLabelProvider;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;
import ch.eugster.colibri.admin.ui.filters.PaymentTypeChangeOnlyViewerFilter;
import ch.eugster.colibri.ui.filters.DeletedEntityViewerFilter;

public class StoreReceiptPopupMenu extends KeyPopupMenu
{
	public static final long serialVersionUID = 0l;

	public StoreReceiptPopupMenu(final TabEditorButton button, final TabEditor editor)
	{
		super(button, editor);
	}

	@Override
	protected void init()
	{
		this.add(new KeyTypeMenuItem(editor, button));

		addSeparator();
		/*
		 * Währung
		 */
		final LabelProvider labelProvider = new StoreReceiptLabelProvider();
		final ViewerFilter[] filters = new ViewerFilter[] { new DeletedEntityViewerFilter(), new PaymentTypeChangeOnlyViewerFilter() };
		this.add(new SelectPaymentTypeMenuItem(editor, button, "Wechselgeld...", "action.change", labelProvider, filters, "Währung"));

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
		this.add(new DeleteMenuItem(button, "Entfernen", "button.delete"));
	}
}
