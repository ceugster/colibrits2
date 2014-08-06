package ch.eugster.log.file.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

import ch.eugster.log.file.Activator;
import ch.eugster.log.file.dialogs.LogLevelComboViewerDialog;

public class OpenLogLevelDialogHandler extends AbstractHandler implements IHandler 
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		final LogLevelComboViewerDialog dialog = new LogLevelComboViewerDialog(Display.getCurrent().getActiveShell());
		if (dialog.open() == Window.OK)
		{
			if (dialog.getFileSelection() != null)
			{
				Activator.getDefault().getProperties().setProperty(Activator.KEY_LOG_LEVEL_FILE, dialog.getFileSelection());
			}
			if (dialog.getConsoleSelection() != null)
			{
				Activator.getDefault().getProperties().setProperty(Activator.KEY_LOG_LEVEL_CONSOLE, dialog.getConsoleSelection());
			}
			if (dialog.getFileSelection() != null)
			{
				Activator.getDefault().getProperties().setProperty(Activator.KEY_DEL_LOGS_OLDER_THAN, dialog.getDays());
				Activator.getDefault().storeProperties();
			}
			Activator.getDefault().storeProperties();
		}
		return Status.OK_STATUS;
	}

}
