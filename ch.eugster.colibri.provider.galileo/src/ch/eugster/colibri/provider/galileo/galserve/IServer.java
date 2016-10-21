/*
 * Created on 24.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import org.eclipse.core.runtime.IStatus;

public interface IServer
{
	public static final String PROPERTY_DATA_PATH = "CDATAPATH";

	IStatus start();
	
	void stop();
	
	void close();
	
	IStatus getStatus();
}
