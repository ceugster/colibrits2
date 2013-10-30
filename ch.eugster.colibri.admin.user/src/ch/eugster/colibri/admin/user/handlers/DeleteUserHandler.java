/*
 * Created on 07.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.admin.user.Activator;
import ch.eugster.colibri.persistence.model.User;

public class DeleteUserHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event)
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("selection");

			if (object instanceof StructuredSelection)
			{
				final StructuredSelection ssel = (StructuredSelection) object;
				if (!ssel.isEmpty())
				{
					final StringBuffer sb = new StringBuffer();
					final Object[] elements = ssel.toArray();
					for (final Object element : elements)
					{
						if (element instanceof User)
						{
							final User user = (User) element;
							if (sb.length() > 0)
							{
								sb.append("\n");
							}
							sb.append(user.getUsername());
						}
					}

					String msg = null;
					if (ssel.size() == 1)
					{
						msg = "Soll der ausgewählte Benutzer " + sb.toString() + " entfernt werden?";
					}
					else
					{
						msg = "Sollen die ausgewählten Benutzer:\n" + sb.toString() + "\nentfernt werden?";
					}

					final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
					final MessageDialog dialog = new MessageDialog(shell, "Benutzer entfernen", null, msg, MessageDialog.QUESTION, new String[] {
							"Ja", "Nein" }, 0);
					if (dialog.open() == Window.OK)
					{
						if (persistenceService != null)
						{
							for (final Object element : elements)
							{
								if (element instanceof User)
								{
									User user = (User) element;
									try
									{
										persistenceService.getServerService().delete(user);
									} 
									catch (Exception e) 
									{
										e.printStackTrace();
										IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
										ErrorDialog.openError(shell, "Fehler", "Der Benutzer " + user.getUsername() + " konnte nicht entfernt werden.", status);
									}
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	public void setEnabled(EvaluationContext context)
	{
		final EvaluationContext ctx = (EvaluationContext) context;
		final Object object = ctx.getParent().getVariable("selection");

		if (object instanceof StructuredSelection)
		{
			final StructuredSelection ssel = (StructuredSelection) object;
			setBaseEnabled(persistenceService != null && !ssel.isEmpty() && ssel.getFirstElement() instanceof User);
		}
	}
	
	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
