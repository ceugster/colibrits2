/*
 * Created on 16.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.events;

import org.eclipse.equinox.app.IApplication;

public class ShutdownEvent
{
	private Integer exitCode;
	
	public ShutdownEvent()
	{
		this.setExitCode(IApplication.EXIT_OK);
	}
	
	public ShutdownEvent(Integer exitCode)
	{
		this.setExitCode(exitCode);
	}
	
	public void setExitCode(Integer exitCode)
	{
		this.exitCode = exitCode;
	}
	
	public Integer getExitCode()
	{
		return this.exitCode;
	}
	
}
