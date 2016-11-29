/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderQuery;

public class SelectCustomerAction extends ConfigurableAction implements DisposeListener
{
	private static final long serialVersionUID = 0l;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderQuery, ProviderQuery> providerQueryTracker;

	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker;
	
	public SelectCustomerAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		userPanel.addDisposeListener(this);
		
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
		this.eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventAdminTracker.open();
		this.providerQueryTracker = new ServiceTracker<ProviderQuery, ProviderQuery>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderQuery.class, null);
		this.providerQueryTracker.open();
	}

	public ProductGroup getProductGroup()
	{
		ProductGroup productGroup = null;
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			productGroup = (ProductGroup) persistenceService.getCacheService().find(ProductGroup.class, this.key.getParentId());
			if (!productGroup.isPayedInvoice())
			{
				productGroup = productGroup.getCommonSettings().getPayedInvoice();
			}
		}
		return productGroup;
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final ProviderQuery providerQuery = (ProviderQuery) this.providerQueryTracker.getService();
		if (providerQuery != null)
		{
			IStatus status = providerQuery.selectCustomer(userPanel.getPositionWrapper().getPosition(), getProductGroup());
			if (status.getSeverity() == IStatus.OK)
			{
				userPanel.getPositionListPanel().getModel().actionPerformed(event);
			}
			else if (status.getSeverity() == IStatus.INFO)
			{
				MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), userPanel.getProfile(), "Lesen der Kundendaten",
						status.getMessage(), MessageDialog.BUTTON_OK, this.userPanel.getMainTabbedPane().isFailOver());
			}
			EventAdmin eventAdmin = this.eventAdminTracker.getService();
			if (eventAdmin != null)
			{
				Map<String, Object> properties = new HashMap<String, Object>();
				properties.put("provider", providerQuery.getProviderId());
				properties.put("failover", Boolean.valueOf(status.getException() != null));
				properties.put("status", status);
				properties.put("topic", Topic.PROVIDER_QUERY);
				eventAdmin.sendEvent(new Event(Topic.PROVIDER_QUERY.topic(), properties));
			}
		}
	}

	@Override
	public void handleEvent(Event event) 
	{
		if (event.getTopic().equals(Topic.FAIL_OVER.topic()))
		{
			final ProviderQuery providerQuery = (ProviderQuery) this.providerQueryTracker.getService();
			if (providerQuery != null)
			{
				String providerId = providerQuery.getProviderId();
				Boolean result = (Boolean) event.getProperty(providerId);
				this.setEnabled(result == null || !result.booleanValue());
			}
		}
	}
	
	@Override
	public void dispose()
	{
		this.providerQueryTracker.close();
		this.persistenceServiceTracker.close();
		this.eventAdminTracker.close();
	}
}
