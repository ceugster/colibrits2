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
import ch.eugster.colibri.persistence.events.EventTopic;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.provider.service.ProviderService;
import ch.eugster.colibri.scheduler.service.UpdateScheduler;
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

	public ProfileButton(final ProfileAction action, final Profile profile)
	{
		super(action, profile);
		this.init();
		this.update(false);
	}

	@Override
	public void finalize()
	{
		this.eventHandlerServiceRegistration.unregister();
		EntityMediator.removeListener(Profile.class, this);
	}

	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(ProviderService.Topic.PROVIDER_FAILOVER.topic()) || event.getTopic().equals(EventTopic.FAILOVER.topic()) || event.getTopic().equals(UpdateScheduler.SchedulerTopic.FAILOVER.topic()))
		{
			this.failOver = event.getProperty(EventConstants.EXCEPTION) != null;
			this.update(this.failOver);
		}
		else if (event.getTopic().equals(UpdateScheduler.SchedulerTopic.OK.topic()))
		{
			this.update(false);
		}
	}

	private void init()
	{
		EntityMediator.addListener(Profile.class, this);

		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
//		final String[] topics = ProviderService.Topic.topics();
		List<String> topics = new ArrayList<String>();
		for (ProviderService.Topic topic : ProviderService.Topic.values())
		{
			topics.add(topic.topic());
		}
		topics.add(EventTopic.FAILOVER.topic());
		for(UpdateScheduler.SchedulerTopic topic : UpdateScheduler.SchedulerTopic.values())
		{
			topics.add(topic.topic());
		}
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);
	}
}
