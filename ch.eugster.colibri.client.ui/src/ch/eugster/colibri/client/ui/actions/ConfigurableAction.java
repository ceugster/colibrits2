/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventHandler;

import ch.eugster.colibri.client.ui.buttons.ConfigurableButton;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
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
	}

	public UserPanel getUserPanel()
	{
		return userPanel;
	}

	@Override
	public final void stateChange(final StateChangeEvent event)
	{
		if (this.key.getFunctionType() == FunctionType.FUNCTION_LOGOUT)
		{
			System.out.println();
		}

		setEnabled(getState(event));
		firePropertyChange("state", event.getOldState(), event.getNewState());
	}

	protected boolean getState(final StateChangeEvent event)
	{
		UserPanel.State newState = event.getNewState();
		boolean state = newState.configurableActionState();
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
					state = false;
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
