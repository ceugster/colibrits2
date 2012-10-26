/*
 * Created on 01.04.2009
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
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class KeyTypeMenuItem extends MenuItem
{
	private static final long serialVersionUID = 0l;

	public KeyTypeMenuItem(final TabEditor editor, final TabEditorButton button)
	{
		super();

		final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		persistenceServiceTracker.open();

		final KeyType keyType = button.getKeys()[1].getKeyType();
		if (keyType.equals(KeyType.FUNCTION))
		{
			final FunctionType functionType = button.getKeys()[1].getFunctionType();
			this.setLabel(functionType.toString());
		}
		else
		{
			final PersistenceService persistenceService = (PersistenceService) persistenceServiceTracker.getService();

			String value = null;
			if (keyType.equals(KeyType.PAYMENT_TYPE))
			{
				if (persistenceService != null)
				{
					final PaymentType paymentType = (PaymentType) persistenceService.getServerService().find(PaymentType.class,
							button.getKeys()[1].getParentId());
					value = keyType.toString() + ": " + paymentType.getName();
				}
			}
			else if (keyType.equals(KeyType.PRODUCT_GROUP))
			{
				if (persistenceService != null)
				{
					final ProductGroup productGroup = (ProductGroup) persistenceService.getServerService().find(ProductGroup.class,
							button.getKeys()[1].getParentId());
					value = keyType.toString() + ": " + productGroup.getName();
				}
			}
			else
			{
				value = keyType.toString();
			}

			this.setLabel(value);
		}
		persistenceServiceTracker.close();
	}
}
