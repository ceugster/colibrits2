package ch.eugster.colibri.client.app;

import java.util.HashMap;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.Activator;
import ch.eugster.colibri.persistence.replication.service.ReplicationService;

public class ClientApplication implements IApplication
{
	public static final String ID = "ch.eugster.colibri.client.application";

//	private void replicate()
//	{
//		final ServiceTracker<ReplicationService, ReplicationService> replicationServiceTracker = new ServiceTracker<ReplicationService, ReplicationService>(Activator.getDefault().getBundle()
//				.getBundleContext(), ReplicationService.class, null);
//		try
//		{
//			replicationServiceTracker.open();
//
//			final ReplicationService replicationService = replicationServiceTracker.getService();
//			if (replicationService != null)
//			{
//				UIJob job = new UIJob("Lokale Daten werden abgeglichen...")
//				{
//					@Override
//					public IStatus runInUIThread(IProgressMonitor monitor) 
//					{
//						Shell shell = new Shell(this.getDisplay());
//						replicationService.replicate(shell);
//						return Status.OK_STATUS;
//					}
//					
//				};
//				job.setUser(true);
//				job.setPriority(Job.INTERACTIVE);
//				job.schedule();
//			}
//		}
//		finally
//		{
//			replicationServiceTracker.close();
//		}
//	}
	
	private void replicate(Display display)
	{
		Activator.getDefault().log(LogService.LOG_INFO, "Suche Service für die Replikation...");
		final ServiceTracker<ReplicationService, ReplicationService> replicationServiceTracker = new ServiceTracker<ReplicationService, ReplicationService>(Activator.getDefault().getBundle()
				.getBundleContext(), ReplicationService.class, null);
		try
		{
			replicationServiceTracker.open();

			final ReplicationService replicationService = replicationServiceTracker.getService();
			if (replicationService != null)
			{
				Activator.getDefault().log(LogService.LOG_INFO, "Service für die Replikation gefunden.");
				Shell shell = new Shell(display);
				Activator.getDefault().log(LogService.LOG_INFO, "Starte Replikation...");
				IStatus status = replicationService.replicate(shell, false);
				if (status.getSeverity() == IStatus.ERROR)
				{
					Activator.getDefault().log(LogService.LOG_INFO, "Replikation mit Fehler beendet.");
					MessageDialog.open(MessageDialog.ERROR, shell, "Replikationsfehler", status.getMessage(), SWT.None);
				}
				else
				{
					Activator.getDefault().log(LogService.LOG_INFO, "Replikation erfolgreich durchgeführt.");
				}
			}
		}
		finally
		{
			replicationServiceTracker.close();
			ServiceTracker<EventAdmin, EventAdmin> tracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
			tracker.open();
			EventAdmin eventAdmin = tracker.getService();
			if (eventAdmin != null)
			{
				Event event = new Event("ch/eugster/colibri/persistence/replication/completed", new HashMap<String, Object>());
				eventAdmin.sendEvent(event);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception
	{
		Activator.getDefault().log(LogService.LOG_INFO, "Starte " + ClientApplication.ID + ".");

		Activator.getDefault().log(LogService.LOG_INFO, "Kreiere Display...");
		final Display display = PlatformUI.createDisplay();
		try
		{
			int returnCode = IApplication.EXIT_OK;
			Activator.getDefault().log(LogService.LOG_INFO, "Registriere Anwendungs-Instanz.");
			if (ApplicationInstanceManager.registerInstance(Activator.PLUGIN_ID))
			{
				replicate(display);
			}
			else
			{
				final String msg = "Es wurde bereits eine andere Instanz des Programms gestartet. Das Programm wird beendet.";

				final Shell shell = new Shell(Display.getDefault());
				final MessageDialog dialog = new MessageDialog(shell, "Programmstart", null, msg, MessageDialog.ERROR,
						new String[] { "OK" }, 0);
				dialog.open();
				returnCode = PlatformUI.RETURN_UNSTARTABLE;
			}
			if (returnCode != PlatformUI.RETURN_UNSTARTABLE)
			{
				returnCode = PlatformUI.createAndRunWorkbench(display, new ClientApplicationWorkbenchAdvisor(context));
			}
			if (returnCode == PlatformUI.RETURN_RESTART)
			{
				Activator.getDefault().log(LogService.LOG_INFO, "Restarte Anwendung.");
				return IApplication.EXIT_RESTART;
			}
			else
			{
				Activator.getDefault().log(LogService.LOG_INFO, "Öffne Anwendung.");
				return IApplication.EXIT_OK;
			}
		}
		finally
		{
			display.dispose();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.equinox.app.IApplication#stop()
	 */
	@Override
	public void stop()
	{
		final IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench == null)
		{
			return;
		}
		final Display display = workbench.getDisplay();
		display.syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				if (!display.isDisposed())
				{
					workbench.close();
				}
			}
		});
	}
}
