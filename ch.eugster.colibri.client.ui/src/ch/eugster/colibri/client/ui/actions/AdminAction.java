/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Role;

public final class AdminAction extends ConfigurableAction implements EntityListener
{
	private static final long serialVersionUID = 1L;

	public AdminAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		Display.getDefault().asyncExec(new Runnable()
		{
			public void run()
			{
				boolean found = false;

				final IWorkbenchWindow[] windows = PlatformUI.getWorkbench().getWorkbenchWindows();
				for (final IWorkbenchWindow window : windows)
				{
					final IPerspectiveDescriptor[] perspectives = window.getActivePage().getOpenPerspectives();
					for (final IPerspectiveDescriptor perspective : perspectives)
					{
						if (perspective.getId().startsWith("ch.eugster.colibri.admin."))
						{
							try
							{
								PlatformUI.getWorkbench()
										.showPerspective(perspective.getId(), PlatformUI.getWorkbench().getActiveWorkbenchWindow());
								found = true;
							}
							catch (final WorkbenchException e)
							{
								final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
								final String title = "Administrator öffnen";
								final String msg = "Beim Öffnen des Administratorfensters ist ein Fehler aufgetreten.";
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
						PlatformUI.getWorkbench().openWorkbenchWindow("ch.eugster.colibri.admin.product.perspective", null);
					}
					catch (final WorkbenchException e)
					{
						final Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						final String title = "Administrator öffnen";
						final String msg = "Beim Öffnen des Administratorfensters ist ein Fehler aufgetreten.";
						final Status status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, msg, e);
						final ErrorDialog dialog = new ErrorDialog(shell, title, msg, status, 0);
						dialog.open();
					}
				}
			}
		});
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void postLoad(final AbstractEntity entity)
	{
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
	}

	@Override
	public void postRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
		if (entity instanceof Role)
		{
			final Role role = (Role) entity;
			if (userPanel.getUser().getRole().getId().equals(role.getId()))
			{
				userPanel.getUser().setRole(role);
			}
		}
	}

	@Override
	public void preDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void prePersist(final AbstractEntity entity)
	{
	}

	@Override
	public void preRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void preUpdate(final AbstractEntity entity)
	{
	}
}
