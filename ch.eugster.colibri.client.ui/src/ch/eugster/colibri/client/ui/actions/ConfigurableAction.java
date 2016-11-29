/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.util.Dictionary;
import java.util.Hashtable;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.buttons.ConfigurableButton;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.ui.actions.BasicAction;

public abstract class ConfigurableAction extends BasicAction implements StateChangeListener, EventHandler
{
	public static final long serialVersionUID = 0l;

	protected ServiceRegistration<EventHandler> handlerRegistration;
	
	protected UserPanel userPanel;

	protected Key key;

	protected ConfigurableButton button;

	public ConfigurableAction(final UserPanel userPanel, final Key key)
	{
		super(key.getLabel());
		this.userPanel = userPanel;
		this.key = key;
		this.userPanel.addStateChangeListener(this);
		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, new String[] { Topic.FAIL_OVER.topic() });
		registerHandler(properties);
	}

	protected void registerHandler(Dictionary<String, Object> properties)
	{
		handlerRegistration = Activator.getDefault().getBundle().getBundleContext().registerService(EventHandler.class, this, properties);
	}
	
	public UserPanel getUserPanel()
	{
		return userPanel;
	}
	
	public Key getKey()
	{
		return key;
	}

	@Override
	public final void stateChange(final StateChangeEvent event)
	{
		setEnabled(getState(event));
		firePropertyChange("state", event.getOldState(), event.getNewState());
	}

	protected boolean getState(final StateChangeEvent event)
	{
		UserPanel.State newState = event.getNewState();
		boolean state = newState == null ? false : newState.configurableActionState();
		if (state)
		{
			if (key.getKeyType().equals(KeyType.FUNCTION))
			{
				final FunctionType functionType = key.getFunctionType();
				if (functionType.isFailOverEnabled() || !true) // TODO
				{
					state = userPanel.getUser().getRole().getPropertyValue(functionType.key());
				}
				else
				{
					state = true;
				}
			}
			else
			{
				final KeyType keyType = key.getKeyType();
				state = userPanel.getUser().getRole().getPropertyValue(keyType.getActionCommand());
			}
		}
		return state;
	}
	
	@Override
	public void handleEvent(Event event) 
	{
	}
}
