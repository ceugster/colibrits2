package ch.eugster.colibri.persistence.replication.impl.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.replication.impl.Activator;
import ch.eugster.colibri.persistence.replication.service.ReplicationService;

public class ReplicateHandler extends AbstractHandler implements IHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		IStatus status = Status.OK_STATUS;
		final ServiceTracker<ReplicationService, ReplicationService> tracker = new ServiceTracker<ReplicationService, ReplicationService>(Activator.getDefault().getBundle().getBundleContext(),
				ReplicationService.class, null);
		tracker.open();
		final ReplicationService service = (ReplicationService) tracker.getService();
		if (service != null)
		{
			if (event.getApplicationContext() instanceof EvaluationContext)
			{
				final EvaluationContext context = (EvaluationContext) event.getApplicationContext();
				final Shell shell = (Shell) context.getParent().getVariable("activeShell");
				if (service.isLocalService())
				{
					MessageDialog.openInformation(shell, "Replikation unnötig",
					"Ein Abgleich der Daten ist unnötig.");
				}
				else
				{
					if (MessageDialog
							.openQuestion(shell, "Lokale Datenbank abgleichen",
									"Sollen die Daten in der lokalen Datenbank mit denjenigen der Serverdatenbank abgeglichen werden?"))
					{

						status = service.replicate(shell, true);
						if (status.getSeverity() == IStatus.OK)
						{
							MessageDialog.openInformation(shell, "Replikation beendet",
									"Der Datenabgleich wurde erfolgreich durchgeführt.");
						}
						else
						{
							ErrorDialog.openError(shell, "Fehler beim Replizieren",
									"Beim Replizieren ist ein Fehler aufgetreten.", status);
						}
					}
				}
			}
		}
		return status;
	}

	@Override
	public void setEnabled(final Object evaluationContext)
	{
		final ServiceTracker<ReplicationService, ReplicationService> tracker = new ServiceTracker<ReplicationService, ReplicationService>(Activator.getDefault().getBundle().getBundleContext(),
				ReplicationService.class.getName(), null);
		tracker.open();
		this.setBaseEnabled(tracker.getService() != null);
		tracker.close();
	}

}
