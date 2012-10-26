/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.ui.actions;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public abstract class BasicAction extends AbstractAction implements IActionKeys
{
	private static final long serialVersionUID = 0l;
	
	public BasicAction(String text)
	{
		this(text, text);
	}
	
	public BasicAction(String text, String actionCommand)
	{
		super(text);
		this.putText(text);
		this.putActionCommand(actionCommand);
	}
	
	public void putText(String text)
	{
		this.putValue(IActionKeys.KEY_TEXT, text);
	}
	
	public String getText()
	{
		return (String) this.getValue(IActionKeys.KEY_TEXT);
	}
	
	public void putActionCommand(String actionCommand)
	{
		this.putValue(IActionKeys.KEY_ACTION_COMMAND, actionCommand);
	}
	
	public String getActionCommand()
	{
		return (String) this.getValue(IActionKeys.KEY_ACTION_COMMAND);
	}
	
	public void actionPerformed(ActionEvent event)
	{
		
	}
}
