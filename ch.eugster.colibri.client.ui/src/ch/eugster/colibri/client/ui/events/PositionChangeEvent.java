/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;

import ch.eugster.colibri.persistence.model.Position;

public class PositionChangeEvent
{
	private Position oldPosition;

	private Position newPosition;

	public PositionChangeEvent(final Position oldPosition, final Position newPosition)
	{
		this.oldPosition = oldPosition;
		this.newPosition = newPosition;
	}

	public Position getNewPosition()
	{
		return newPosition;
	}

	public Position getOldPosition()
	{
		return oldPosition;
	}
}
