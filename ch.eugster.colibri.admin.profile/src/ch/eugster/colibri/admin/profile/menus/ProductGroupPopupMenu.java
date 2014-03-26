/*
 * Created on 14.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.MenuItem;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProductGroupPopupMenu extends KeyPopupMenu
{
	public static final long serialVersionUID = 0l;

	public ProductGroupPopupMenu(final TabEditorButton button, final TabEditor editor)
	{
		super(button, editor);
	}

	@Override
	protected void init()
	{
		final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		persistenceServiceTracker.open();

		this.add(new KeyTypeMenuItem(this.editor, this.button));

		this.addSeparator();

		/*
		 * Name
		 */
		this.add(new SetNameMenuItem(this.editor, this.button, "Beschriftung...", "action.name"));

		this.addSeparator();

		this.add(new SetValueMenuItem(this.editor, this.button, "Wert...", "action.value"));

		final PersistenceService persistenceService = (PersistenceService) persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final Long productGroupId = this.button.getKeys()[1].getParentId();
			final ProductGroup productGroup = (ProductGroup) persistenceService.getServerService().find(ProductGroup.class, productGroupId);
			if (productGroup.getProductGroupType().getParent().equals(ProductGroupGroup.SALES))
			{
				this.add(new SetArticleCodeMenuItem(this.editor, this.button, "Artikelcode...", "action.article.code"));
			}
		}

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
		if ((this.button.getKeys()[1] != null) && !this.button.getKeys()[1].isDeleted())
		{
			final MenuItem menuItem = new DeleteMenuItem(this.button, "Entfernen", "button.delete");
			this.add(menuItem);
		}
		persistenceServiceTracker.close();
	}
}
