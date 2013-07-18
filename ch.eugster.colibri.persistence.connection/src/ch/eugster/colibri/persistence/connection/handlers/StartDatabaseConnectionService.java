package ch.eugster.colibri.persistence.connection.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

import ch.eugster.colibri.persistence.connection.Activator;

public class StartDatabaseConnectionService extends AbstractHandler implements IHandler
{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException
	{
		IStatus status = Status.OK_STATUS;
		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (final Bundle bundle : bundles)
		{
			if (bundle.getSymbolicName().equals("org.eclipse.persistence.jpa"))
			{
				if (bundle.getState() == Bundle.RESOLVED)
				{
					if (event.getApplicationContext() instanceof EvaluationContext)
					{
						EvaluationContext context = (EvaluationContext) event.getApplicationContext();
						final Shell shell = (Shell) context.getVariable("activeShell");
						if (shell != null)
						{
							try
							{
						       IRunnableWithProgress operation = new IRunnableWithProgress()
						       {
									@Override
									public void run(IProgressMonitor monitor) throws InvocationTargetException,
											InterruptedException
									{
										try
										{
											monitor.beginTask("Starte Datenbankverbindung...", IProgressMonitor.UNKNOWN);
											bundle.start();
										}
										catch (BundleException e)
										{
											MessageDialog.openError(shell, "Fehler", "Beim Versuch, die Datenbankverbindung herzustellen, ist ein Fehler aufgetreten.\n\n(" + e.getLocalizedMessage() + ")");
										}
										finally
										{
											monitor.done();
										}
									}
						       };
						       new ProgressMonitorDialog(shell).run(true, true, operation);
						    }
							catch (InvocationTargetException e) 
						    {
								MessageDialog.openError(shell, "Fehler", "Beim Versuch, die Datenbankverbindung herzustellen, ist ein Fehler aufgetreten.\n\n(" + e.getLocalizedMessage() + ")");
						    } 
							catch (InterruptedException e) 
							{
								
						    }
						}
					}
				}
			}
		}
		return status;
	}

	@Override
	public void setEnabled(Object evaluationContext)
	{
		boolean enabled = false;
		for (Bundle bundle : Activator.getDefault().getBundle().getBundleContext().getBundles())
		{
			if (bundle.getSymbolicName().equals("org.eclipse.persistence.jpa"))
			{
				switch(bundle.getState())
				{
					case Bundle.RESOLVED:
					{
						enabled = true;
					}
				}
			}
		}
		setBaseEnabled(enabled);
	}
	
}
