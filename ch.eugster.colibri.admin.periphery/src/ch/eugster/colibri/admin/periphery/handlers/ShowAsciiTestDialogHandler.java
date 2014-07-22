/*
 * Created on 14.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;

import ch.eugster.colibri.admin.periphery.dialogs.AsciiTestDialog;

public class ShowAsciiTestDialogHandler extends AbstractHandler implements IHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("activeShell");
			if (object instanceof Shell)
			{
				AsciiTestDialog dialog = new AsciiTestDialog((Shell) object);
				dialog.open();
			}
		}
		return Status.OK_STATUS;
	}
}
