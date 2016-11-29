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
import java.util.Map;

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
		this.init(false);
	}

	public ProfileButton(final Profile profile, boolean isFailOver)
	{
		super(profile, isFailOver);
		this.init(isFailOver);
	}

	public ProfileButton(final ProfileAction action, final Profile profile, boolean isFailOver)
	{
		super(action, profile, isFailOver);
		this.init(isFailOver);
	}

	@Override
	public void finalize()
	{
		this.eventHandlerServiceRegistration.unregister();
		EntityMediator.removeListener(Profile.class, this);
	}

	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(Topic.FAIL_OVER.topic()))
		{
			@SuppressWarnings("unchecked")
			Map<String, Object> failovers = (Map<String, Object>) event.getProperty("failover-list");
			this.update(failovers == null ? false : !failovers.isEmpty());
		}
	}

	private void init(boolean isFailOver)
	{
		EntityMediator.addListener(Profile.class, this);
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		List<String> topics = new ArrayList<String>();
		topics.add(Topic.FAIL_OVER.topic());
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, this, properties);
		this.update(isFailOver);
	}
}
