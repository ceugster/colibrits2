package ch.eugster.colibri.admin.app;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.admin.Activator;

public class AdminApplication implements IApplication
{
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.equinox.app.IApplication#start(org.eclipse.equinox.app.
	 * IApplicationContext)
	 */
	@Override
	public Object start(final IApplicationContext context) throws Exception
	{
		Activator.getDefault().log(LogService.LOG_DEBUG, "Anwendung " + Activator.PLUGIN_ID + " gestartet");

		final Display display = PlatformUI.createDisplay();
		try
		{
			int returnCode = IApplication.EXIT_OK;
			if (!ApplicationInstanceManager.registerInstance(Activator.PLUGIN_ID))
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
				returnCode = PlatformUI.createAndRunWorkbench(display, new AdminApplicationWorkbenchAdvisor(context));
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
