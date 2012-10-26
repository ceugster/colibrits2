/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Position;

public class PositionChangeMediator implements PositionChangeListener
{
	private PropertyChangeListener propertyChangeListener;

	private String[] properties;

	public PositionChangeMediator(final UserPanel userPanel, final PropertyChangeListener propertyChangeListener, final String[] properties)
	{
		this.propertyChangeListener = propertyChangeListener;
		this.properties = properties;
		userPanel.getPositionWrapper().addPositionChangeListener(this);
	}

	public void positionChange(final PositionChangeEvent event)
	{
		if (event.getOldPosition() != null)
		{
			final Position oldPosition = event.getOldPosition();
			for (final String property : properties)
			{
				oldPosition.removePropertyChangeListener(property, propertyChangeListener);
			}
		}

		if (event.getNewPosition() != null)
		{
			final Position newPosition = event.getNewPosition();
			for (final String property : properties)
			{
				newPosition.addPropertyChangeListener(property, propertyChangeListener);
			}
		}

		propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "position", event.getOldPosition(), event.getNewPosition()));
	}

}
