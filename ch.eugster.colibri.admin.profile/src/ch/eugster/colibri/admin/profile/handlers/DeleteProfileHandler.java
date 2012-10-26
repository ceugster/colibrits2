package ch.eugster.colibri.admin.profile.handlers;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.Profile;

/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class DeleteProfileHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
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
					final Object[] elements = ssel.toArray();
					Collection<Profile> undeletableProfiles = new ArrayList<Profile>();
					Collection<Profile> profilesToDelete = new ArrayList<Profile>();
					for (final Object element : elements)
					{
						if (element instanceof Profile)
						{
							final Profile profile = (Profile) element;
							if (profile.getSalespoints().size() == 0)
							{
								profilesToDelete.add(profile);
							}
							else
							{
								undeletableProfiles.add(profile);
							}
						}
					}

					if (undeletableProfiles.size() > 0)
					{
						StringBuilder msg = new StringBuilder();
						if (undeletableProfiles.size() == 1)
						{
							msg = msg.append("Das Profil " + undeletableProfiles.iterator().next().getName()
									+ " kann nicht gelöscht werden, da es noch von Kassen verwendet wird.");
						}
						else
						{
							msg = msg.append("Die Profile\n");
							for (Profile undeletableProfile : undeletableProfiles)
							{
								msg = msg.append(undeletableProfile.getName());
							}
							msg = msg.append(" können nicht gelöscht werden, da sie noch von Kassen verwendet werden.");
						}
						final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
						MessageDialog dialog = new MessageDialog(shell, "Profil entfernen", null, msg.toString(),
								MessageDialog.QUESTION, new String[] { "OK" }, 0);
						dialog.open();
					}
					else if (profilesToDelete.size() > 0)
					{
						StringBuilder msg = new StringBuilder();
						if (profilesToDelete.size() == 1)
						{
							msg = msg.append("Soll das Profil " + profilesToDelete.iterator().next().getName()
									+ " gelöscht werden?");
						}
						else
						{
							msg = msg.append("Sollen die Profile\n");
							for (Profile profileToDelete : profilesToDelete)
							{
								msg = msg.append(profileToDelete.getName());
							}
							msg = msg.append(" gelöscht werden?");
						}
						final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
						MessageDialog dialog = new MessageDialog(shell, "Profil entfernen", null, msg.toString(),
								MessageDialog.QUESTION, new String[] { "Ja", "Nein" }, 0);
						if (dialog.open() == Window.OK)
						{
							for (final Object element : elements)
							{
								if (element instanceof Profile)
								{
									if (persistenceService != null)
									{
										persistenceService.getServerService().delete((Profile) element);
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

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
