/*
 * Created on 10.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;

import java.awt.KeyEventDispatcher;
import java.awt.event.KeyEvent;

public class KeyboardEventDispatcher implements KeyEventDispatcher
{
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent e)
	{
		System.out.print(e.getKeyChar());
		return false;
	}
	
}
