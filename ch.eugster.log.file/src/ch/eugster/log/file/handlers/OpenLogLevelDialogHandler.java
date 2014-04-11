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
			if (dialog.getSelection() != null)
			{
				Activator.getDefault().getProperties().setProperty(Activator.KEY_MAX_LOG_LEVEL, dialog.getSelection());
				Activator.getDefault().storeProperties();
			}
		}
		return Status.OK_STATUS;
	}

}
