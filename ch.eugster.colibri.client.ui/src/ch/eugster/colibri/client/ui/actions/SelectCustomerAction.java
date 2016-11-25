/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
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

	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker;
	
	public SelectCustomerAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		userPanel.addDisposeListener(this);
		
		final Collection<String> t = new ArrayList<String>();
		t.add(Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		t.add(Topic.PROVIDER_QUERY.topic());
		final String[] topics = t.toArray(new String[t.size()]);
		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.handlerRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
		this.eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventAdminTracker.open();
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
		final ServiceTracker<ProviderQuery, ProviderQuery> ProviderQueryTracker = new ServiceTracker<ProviderQuery, ProviderQuery>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderQuery.class, null);
		ProviderQueryTracker.open();
		try
		{
			final ProviderQuery providerQuery = (ProviderQuery) ProviderQueryTracker.getService();
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
							status.getMessage(), MessageDialog.BUTTON_OK);
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
		finally
		{
			ProviderQueryTracker.close();
		}
	}

	@Override
	public void handleEvent(Event event) 
	{
		if (event.getTopic().equals(Topic.SCHEDULED_PROVIDER_UPDATE.topic()))
		{
			Object property = event.getProperty("status");
			if (property instanceof IStatus)
			{
				IStatus status = (IStatus) property;
				if (status.getSeverity() == IStatus.ERROR)
				{
					this.setEnabled(false);
				}
				else
				{
					this.setEnabled(getState(new StateChangeEvent(this.userPanel.getCurrentState(), this.userPanel.getCurrentState())) && status.getSeverity() == IStatus.OK);
				}
			}
		}
	}
	
	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		this.eventAdminTracker.close();
	}
}
