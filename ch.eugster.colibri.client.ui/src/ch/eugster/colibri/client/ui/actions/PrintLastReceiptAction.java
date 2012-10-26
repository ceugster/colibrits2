/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.Status;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.events.EntityAdapter;
import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public class PrintLastReceiptAction extends ConfigurableAction implements DisposeListener
{
	private static final long serialVersionUID = 0l;

	private ServiceTracker<EventAdmin, EventAdmin> eventServiceTracker;
	
	private Receipt lastReceipt;
	
	private EntityListener entityListener;
	
	public PrintLastReceiptAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		userPanel.addDisposeListener(this);

		this.eventServiceTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventServiceTracker.open();
		
		entityListener = new EntityAdapter() 
		{
			@Override
			public void postPersist(AbstractEntity entity) 
			{
				if (entity instanceof Receipt)
				{
					PrintLastReceiptAction.this.lastReceipt = (Receipt) entity;
					stateChange(new StateChangeEvent(userPanel.getCurrentState(), userPanel.getCurrentState()));
				}
			}

			@Override
			public void postRemove(AbstractEntity entity) 
			{
				if (entity instanceof Receipt)
				{
					if (lastReceipt != null && lastReceipt.getId().equals(((Receipt) entity).getId()))
					{
						lastReceipt = null;
						stateChange(new StateChangeEvent(userPanel.getCurrentState(), userPanel.getCurrentState()));
					}
				}
			}

			@Override
			public void postUpdate(AbstractEntity entity) 
			{
				if (entity instanceof Receipt)
				{
					PrintLastReceiptAction.this.lastReceipt = (Receipt) entity;
					stateChange(new StateChangeEvent(userPanel.getCurrentState(), userPanel.getCurrentState()));
				}
			}
		};
		EntityMediator.addListener(Receipt.class, entityListener);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final EventAdmin eventAdmin = (EventAdmin) this.eventServiceTracker.getService();
		if (eventAdmin != null)
		{
			eventAdmin.sendEvent(this.getEvent("ch/eugster/colibri/print/receipt", this.lastReceipt));
		}
	}

	private Event getEvent(final String topics, final Receipt receipt)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
		properties.put(EventConstants.SERVICE, this.eventServiceTracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID,
				this.eventServiceTracker.getServiceReference().getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put(IPrintable.class.getName(), receipt);
		properties.put("status", Status.OK_STATUS);
		properties.put("force", true);
		return new Event(topics, properties);
	}
	
	protected boolean getState(StateChangeEvent event)
	{
		return this.lastReceipt != null;
	}

	@Override
	public void dispose()
	{
		this.eventServiceTracker.close();
		EntityMediator.removeListener(Receipt.class, entityListener);
	}
}
