package ch.eugster.colibri.admin.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import ch.eugster.colibri.admin.ui.Activator;

public class OpenReportHandler extends AbstractHandler implements IHandler
{

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		boolean found = false;
		final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
		for (final IWorkbenchWindow window : windows)
		{
			final IPerspectiveDescriptor[] perspectives = window.getActivePage().getOpenPerspectives();
			for (final IPerspectiveDescriptor perspective : perspectives)
			{
				if (perspective.getId().startsWith("ch.eugster.colibri.report."))
				{
					try
					{
						PlatformUI.getWorkbench().showPerspective(perspective.getId(), PlatformUI.getWorkbench().getActiveWorkbenchWindow());
						found = true;
					}
					catch (final WorkbenchException e)
					{
						final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						final String title = "Auswertungen öffnen";
						final String msg = "Beim Öffnen des Auswertungsfensters ist ein Fehler aufgetreten.";
						final Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e);
						final ErrorDialog dialog = new ErrorDialog(shell, title, msg, status, 0);
						dialog.open();
					}
				}
			}
		}
		if (!found)
		{
			try
			{
				PlatformUI.getWorkbench().openWorkbenchWindow("ch.eugster.colibri.report.settlement.perspective", null);
			}
			catch (final WorkbenchException e)
			{
				final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				final String title = "Auswertungen öffnen";
				final String msg = "Beim Öffnen des Auswertungsfensters ist ein Fehler aufgetreten.";
				final Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e);
				final ErrorDialog dialog = new ErrorDialog(shell, title, msg, status, 0);
				dialog.open();
			}
		}
		return null;
	}
}
