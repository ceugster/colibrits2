/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.views.ClientView;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ShowCoinCounterPanelAction extends ConfigurableAction
{
	private static final long serialVersionUID = 0L;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ShowCoinCounterPanelAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);

		persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		persistenceServiceTracker.open();

		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		final Collection<String> topicNames = new ArrayList<String>();
		topicNames.add(Topic.SCHEDULED_TRANSFER.topic());
		topicNames.add(Topic.STORE_RECEIPT.topic());
		final String[] topics = topicNames.toArray(new String[topicNames.size()]);
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.handlerRegistration = Activator.getDefault().getBundle().getBundleContext().registerService(EventHandler.class, this, properties);
		this.setEnabled(ClientView.getClientView().countTransfers(this.getUserPanel().getLocalSalespoint()) == 0L);
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.COIN_COUNTER));
	}

	protected boolean getState(final StateChangeEvent event)
	{
		boolean enabled = super.getState(event);
		if (event.getNewState().equals(UserPanel.State.MUST_SETTLE))
		{
			enabled = userPanel.getUser().getRole().getPropertyValue(FunctionType.FUNCTION_SHOW_COIN_COUNTER_PANEL.key());
		}
		long count = ClientView.getClientView().countTransfers(this.getUserPanel().getLocalSalespoint());
		return enabled && count == 0L;
	}

	@Override
	protected void finalize() throws Throwable
	{
		persistenceServiceTracker.close();
		super.finalize();
	}
}
