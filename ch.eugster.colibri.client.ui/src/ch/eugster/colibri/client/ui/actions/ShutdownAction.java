/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.key.FunctionType;

public final class ShutdownAction extends ConfigurableAction
{
	private static final long serialVersionUID = 1L;

	public ShutdownAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final UIJob uiJob = new UIJob("close window")
		{
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor)
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().close();
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}
}
