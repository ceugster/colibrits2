/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.buttons;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.eugster.colibri.client.ui.actions.ConfigurableAction;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;

public class LockButton extends ConfigurableButton implements PropertyChangeListener
{
	public static final long serialVersionUID = 0l;

	public LockButton(final ConfigurableAction action, final Key key)
	{
		super(action, key);
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (event.getPropertyName().equals("state"))
		{
			if (event.getNewValue().equals(UserPanel.State.LOCKED))
			{
				setText("Entsperren");
			}
			else
			{
				setText("Sperren");
			}
		}
	}
}
