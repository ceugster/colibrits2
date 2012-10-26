/*
 * Created on 18.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.ui.menus;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;

public class SaveHandler implements IHandler
{
	
	@Override
	public void addHandlerListener(IHandlerListener handlerListener)
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose()
	{
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		Object ctx = event.getApplicationContext();
		if (ctx instanceof IEvaluationContext)
		{
			IEvaluationContext context = (IEvaluationContext) ctx;
			IWorkbenchPart part = (IWorkbenchPart) context.getVariable("activePart"); //$NON-NLS-1$
			if (part instanceof IEditorPart)
			{
				IEditorPart editor = (IEditorPart) part;
				editor.doSave(new NullProgressMonitor());
			}
		}
		return null;
	}
	
	@Override
	public boolean isEnabled()
	{
		return false;
	}
	
	@Override
	public boolean isHandled()
	{
		return false;
	}
	
	@Override
	public void removeHandlerListener(IHandlerListener handlerListener)
	{
	}
	
}
