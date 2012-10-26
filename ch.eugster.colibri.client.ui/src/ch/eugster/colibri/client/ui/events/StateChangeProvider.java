/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;



public interface StateChangeProvider
{
	void addStateChangeListener(StateChangeListener listener);
	
	void removeStateChangeListener(StateChangeListener listener);
	
	void fireStateChange(StateChangeEvent event);
}
