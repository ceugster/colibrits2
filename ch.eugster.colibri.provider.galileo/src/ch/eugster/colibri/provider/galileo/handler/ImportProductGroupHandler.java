package ch.eugster.colibri.provider.galileo.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.service.ProviderConfigurator;

public class ImportProductGroupHandler extends AbstractHandler implements IHandler
{

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		final ServiceTracker<ProviderConfigurator, ProviderConfigurator> tracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(Platform.getBundle(Activator.getDefault().getBundle().getSymbolicName()).getBundleContext(),
				ProviderConfigurator.class, null);
		try
		{
			final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			tracker.open();
			final ProviderConfigurator configurator = (ProviderConfigurator) tracker.getService();
			if (configurator == null)
			{
				final String msg = "Der Import kann nicht durchgeführt werden. Der Dienst für den Import der Warengruppen aus der Warenbewirtschaftung ist zur Zeit nicht verfügbar.";
				final String title = "Dienst nicht verfügbar";
				final String[] buttons = new String[] { "OK" };
				final MessageDialog dialog = new MessageDialog(shell, title, null, msg, MessageDialog.INFORMATION, buttons,
						0);
				dialog.open();
			}
			else
			{
				if (configurator.isConnect())
				{
					final String msg = "Sollen die Warengruppen aus der Warenbewirtschaftung '" + configurator.getName()
							+ "' importiert werden?";
					final String title = "Warengruppen importieren";
					final String[] buttons = new String[] { "Ja", "Nein" };
					final MessageDialog dialog = new MessageDialog(shell, title, null, msg, MessageDialog.QUESTION, buttons, 0);
					if (dialog.open() == 0)
					{
						final Job importJob = new Job("Warengruppen importieren")
						{
							@Override
							protected IStatus run(final IProgressMonitor monitor)
							{
								return configurator.importProductGroups(monitor);
							}
						};
						importJob.addJobChangeListener(new JobChangeAdapter()
						{
							@Override
							public void done(final IJobChangeEvent event)
							{
								final IStatus status = event.getResult();
								if (status != null)
								{
									final UIJob uiJob = new UIJob("send message")
									{
										@Override
										public IStatus runInUIThread(final IProgressMonitor monitor)
										{
											final String title = "Import abgeschlossen";
											if (status.getSeverity() == IStatus.ERROR)
											{
												ErrorDialog.openError(shell, title, "Beim Import ist ein Fehler aufgetreten.",
														status);
											}
											else
											{
												MessageDialog.openInformation(shell, title, status.getMessage());
											}
											return Status.OK_STATUS;
										}
									};
									uiJob.schedule();
								}
							}
						});
						importJob.schedule();
					}
				}
				else
				{
					MessageDialog dialog = new MessageDialog(shell, "Verbindung inaktiv", null, "Die Verbindung zu " + configurator.getName() + " ist deaktiviert.", MessageDialog.INFORMATION, new String[] { "OK" }, 0);
					dialog.open();
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return Status.OK_STATUS;
	}

	@Override
	public void setEnabled(final Object evaluationContext)
	{
//		final ServiceTracker<ProviderConfigurator, ProviderConfigurator> tracker = new ServiceTracker<ProviderConfigurator, ProviderConfigurator>(Activator.getDefault().getBundle().getBundleContext(),
//				ProviderConfigurator.class, null);
//		try
//		{
//			tracker.open();
//			final ProviderConfigurator configurator = (ProviderConfigurator) tracker.getService();
//			this.setBaseEnabled(configurator != null && configurator.isConnect());
//		}
//		finally
//		{
//		tracker.close();
//		}
	}

}
