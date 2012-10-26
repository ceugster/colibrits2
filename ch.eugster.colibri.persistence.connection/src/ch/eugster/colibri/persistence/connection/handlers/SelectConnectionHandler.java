package ch.eugster.colibri.persistence.connection.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;

import ch.eugster.colibri.persistence.connection.wizard.DatabaseWizard;
import ch.eugster.colibri.persistence.connection.wizard.WizardDialog;

public class SelectConnectionHandler extends AbstractHandler implements IHandler
{

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext context = (EvaluationContext) event.getApplicationContext();
			final Shell shell = (Shell) context.getParent().getVariable("activeShell");

			final DatabaseWizard wizard = new DatabaseWizard();
			final WizardDialog dialog = new WizardDialog(shell, wizard);
			if (dialog.open() == Window.OK)
			{
				// TODO EntityManager neu starten...
			}
		}
		return null;
	}

}
