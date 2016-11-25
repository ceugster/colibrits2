/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.buttons;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.actions.ProfileAction;
import ch.eugster.colibri.ui.buttons.AbstractProfileButton;

public class ProfileButton extends AbstractProfileButton implements EventHandler
{
	private static final long serialVersionUID = 0l;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

	public ProfileButton(final Profile profile)
	{
		super(profile);
		this.init();
	}

	public ProfileButton(final ProfileAction action, final Profile profile, boolean isFailOver)
	{
		super(action, profile, isFailOver);
		this.init();
		this.update(isFailOver);
	}

	@Override
	public void finalize()
	{
		this.eventHandlerServiceRegistration.unregister();
		EntityMediator.removeListener(Profile.class, this);
	}

	public void handleEvent(final Event event)
	{
		Boolean isFailover = (Boolean)event.getProperty("failover");
		if (isFailover != null)
		{
			String provider = (String) event.getProperty("provider");
			if (isFailover.booleanValue())
			{
				failOver.put(provider, isFailover);
			}
			else
			{
				if (failOver.containsKey(provider))
				{
					failOver.remove(provider);
				}
			}
			this.update(this.isFailOver());
		}
	}

	private void init()
	{
		EntityMediator.addListener(Profile.class, this);
		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		List<String> topics = new ArrayList<String>();
		topics.add(Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		topics.add(Topic.SCHEDULED_TRANSFER.topic());
		topics.add(Topic.PROVIDER_QUERY.topic());
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);
	}
}
