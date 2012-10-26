/*
 * Created on 07.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.user.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.admin.user.Activator;
import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.model.User;

public class DeleteRoleHandler extends AbstractPersistenceClientHandler
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
					if (ssel.size() == 1)
					{
						if (ssel.getFirstElement() instanceof Role)
						{
							int count = 0;
							final Role role = (Role) ssel.getFirstElement();
							for (final User user : role.getUsers())
							{
								if (!user.isDeleted())
								{
									count++;
								}
							}
							if (count == 0)
							{
								final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
								final String msg = "Soll die ausgewählte Rolle " + role.getName() + " entfernt werden?";
								final MessageDialog dialog = new MessageDialog(shell, "Rolle entfernen", null, msg.toString(),
										MessageDialog.QUESTION, new String[] { "Ja", "Nein" }, 0);
								if (dialog.open() == Window.OK)
								{
									if (persistenceService != null)
									{
										persistenceService.getServerService().delete(role);
									}
								}
							}
							else if (count == 1)
							{
								final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
								final String msg = "Die ausgewählte Rolle kann nicht entfernt werden, da ihr ein Benutzer zugeordnet ist.";
								final MessageDialog dialog = new MessageDialog(shell, "Rolle entfernen", null, msg.toString(),
										MessageDialog.QUESTION, new String[] { "Ok" }, 0);
								dialog.open();
								return null;
							}
							else
							{
								final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
								final String msg = "Die ausgewählte Rolle kann nicht entfernt werden, da ihr " + role.getUsers().size()
										+ " Benutzer zugeordnet sind.";
								final MessageDialog dialog = new MessageDialog(shell, "Rolle entfernen", null, msg.toString(),
										MessageDialog.QUESTION, new String[] { "Ok" }, 0);
								dialog.open();
								return null;
							}
						}
					}
					else
					{
						final StringBuilder sb1 = new StringBuilder();
						final StringBuilder sb2 = new StringBuilder();
						final Object[] elements = ssel.toArray();
						for (final Object element : elements)
						{
							if (element instanceof Role)
							{
								int count = 0;
								final Role role = (Role) element;
								for (final User user : role.getUsers())
								{
									if (!user.isDeleted())
									{
										count++;
									}
								}
								if (count == 0)
								{
									if (sb1.length() > 0)
									{
										sb1.append("\n");
									}
									sb1.append(role.getName());
								}
								else
								{
									if (sb2.length() > 0)
									{
										sb2.append("\n");
									}
									sb2.append(role.getName());
								}
							}
						}

						StringBuilder msg = new StringBuilder();
						if (sb1.length() > 0)
						{
							if (ssel.size() == 1)
							{
								msg.append("Soll die ausgewählte Rolle " + sb1.toString() + " entfernt werden?");
							}
							else
							{
								msg.append("Sollen die ausgewählten Rollen:\n" + sb1.toString() + "\nentfernt werden?");
							}
						}
						if (sb2.length() > 0)
						{
							msg = msg.append(msg.length() == 0 ? "" : "\n");
							msg = msg
									.append("Die ausgewählten Rollen:\n" + sb2.toString() + "\nsind in Benutzung und können nicht entfernt werden");
						}
						final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
						final MessageDialog dialog = new MessageDialog(shell, "Rolle entfernen", null, msg.toString(), MessageDialog.QUESTION,
								new String[] { "Ja", "Nein" }, 0);
						if (dialog.open() == Window.OK)
						{
							if (persistenceService != null)
							{
								for (final Object element : elements)
								{
									if (element instanceof Role)
									{
										persistenceService.getServerService().delete((Role) element);
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
			setBaseEnabled(persistenceService != null && !ssel.isEmpty() && ssel.getFirstElement() instanceof Role);
		}
	}
	
	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
