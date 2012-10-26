package ch.eugster.colibri.client.app;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
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
		final ServiceTracker<ReplicationService, ReplicationService> replicationServiceTracker = new ServiceTracker<ReplicationService, ReplicationService>(Activator.getDefault().getBundle()
				.getBundleContext(), ReplicationService.class, null);
		try
		{
			replicationServiceTracker.open();

			final ReplicationService replicationService = replicationServiceTracker.getService();
			if (replicationService != null)
			{
				Shell shell = new Shell(display);
				replicationService.replicate(shell);
			}
		}
		finally
		{
			replicationServiceTracker.close();
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
		final LogService logService = Activator.getDefault().getLogService();
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, "Anwendung " + ClientApplication.ID + " gestartet");
		}

		final Display display = PlatformUI.createDisplay();
		try
		{
			int returnCode = IApplication.EXIT_OK;
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
				return IApplication.EXIT_RESTART;
			}
			else
			{
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
