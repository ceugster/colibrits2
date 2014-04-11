/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderIdService;

public final class ProductGroupAction extends ConfigurableAction implements DisposeListener, PropertyChangeListener
{
	public static final long serialVersionUID = 0l;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderIdService, ProviderIdService> providerIdServiceTracker;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	public ProductGroupAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);

		new PositionChangeMediator(userPanel, this, new String[] { "position" });

		userPanel.addDisposeListener(this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.providerIdServiceTracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderIdService.class, null);
		this.providerIdServiceTracker.open();

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		this.logServiceTracker.open();
}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		log(LogService.LOG_DEBUG, "Enter ProductGroupAction.actionPerformed()");
		if (this.setExternalProductGroup(this.getProductGroup()))
		{
			if (this.userPanel.getValueDisplay().testAmount() != 0d)
			{
				this.userPanel.getPositionDetailPanel().getPriceButton().doClick();
			}
			this.userPanel.getPositionListPanel().getModel().actionPerformed(event);
		}
		log(LogService.LOG_DEBUG, "Exit ProductGroupAction.actionPerformed()");
	}

	protected void log(int level, String message)
	{
		LogService logService = logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(level, message);
		}
	}
	
	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		this.providerIdServiceTracker.close();
		this.logServiceTracker.close();
	}

	public ProductGroup getProductGroup()
	{
		log(LogService.LOG_DEBUG, "Enter ProductGroupAction.getProductGroup()");
		ProductGroup productGroup = null;
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			productGroup = (ProductGroup) persistenceService.getCacheService().find(ProductGroup.class, this.key.getParentId());
		}
		log(LogService.LOG_DEBUG, "Exit ProductGroupAction.getProductGroup()");
		return productGroup;
	}

	public boolean setExternalProductGroup(final ProductGroup productGroup)
	{
		log(LogService.LOG_DEBUG, "Enter ProductGroupAction.setExternalProductGroup()");
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
		log(LogService.LOG_DEBUG, "Exit ProductGroupAction.setExternalProductGroup()");
		return true;
	}

	protected boolean getState(final StateChangeEvent event)
	{
		boolean state = super.getState(event);
		if (state)
		{
			state = shouldEnable();
		}
		return state;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		this.setEnabled(this.shouldEnable());
	}

	private boolean shouldEnable()
	{
		boolean isReceiptInternal = this.userPanel.getReceiptWrapper().getReceipt().isInternal(); 
		boolean hasNoPositions = this.userPanel.getReceiptWrapper().getReceipt().getPositions().isEmpty();
		boolean isThisInternal = this.getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL);
		return hasNoPositions ? true : (isReceiptInternal && isThisInternal || !isReceiptInternal && !isThisInternal);
	}
}
