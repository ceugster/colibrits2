/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderIdService;

public final class ProductGroupAction extends ConfigurableAction implements DisposeListener
{
	public static final long serialVersionUID = 0l;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderIdService, ProviderIdService> providerIdServiceTracker;

	public ProductGroupAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		userPanel.addDisposeListener(this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.providerIdServiceTracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderIdService.class, null);
		this.providerIdServiceTracker.open();
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (this.setExternalProductGroup(this.getProductGroup()))
		{
			if (this.userPanel.getValueDisplay().testAmount() != 0d)
			{
				System.out.println(SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()) + ": price button action performed.");
				this.userPanel.getPositionDetailPanel().getPriceButton().doClick();
			}
			System.out.println(SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()) + ": model action started.");
			this.userPanel.getPositionListPanel().getModel().actionPerformed(event);
		}
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		this.providerIdServiceTracker.close();
	}

	public ProductGroup getProductGroup()
	{
		ProductGroup productGroup = null;
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			productGroup = (ProductGroup) persistenceService.getCacheService().find(ProductGroup.class, this.key.getParentId());
		}
		return productGroup;
	}

	public boolean setExternalProductGroup(final ProductGroup productGroup)
	{
		if (productGroup == null)
		{
			return false;
		}

		final ProviderIdService providerIdService = (ProviderIdService) this.providerIdServiceTracker.getService();
		if (providerIdService == null)
		{
			this.userPanel.getPositionWrapper().getPosition().setProductGroup(productGroup);
		}
		else
		{
			if (this.userPanel.getPositionWrapper().getPosition().getProduct() == null)
			{
				this.userPanel.getPositionWrapper().getPosition().setProductGroup(productGroup);
			}
			else
			{
				final Collection<ProductGroupMapping> mappings = productGroup.getProductGroupMappings(providerIdService.getProviderId());
				for (ProductGroupMapping mapping : mappings)
				{
					if (mapping != null)
					{
						this.userPanel.getPositionWrapper().getPosition().getProduct().setExternalProductGroup(mapping.getExternalProductGroup());
					}
				}
			}
		}
		return true;
	}

}
