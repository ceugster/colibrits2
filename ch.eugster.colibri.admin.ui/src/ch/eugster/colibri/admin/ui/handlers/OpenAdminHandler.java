package ch.eugster.colibri.admin.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.ui.Activator;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class OpenAdminHandler extends AbstractHandler implements IHandler
{
	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public OpenAdminHandler()
	{
		persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class.getName(), null);
		persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		persistenceServiceTracker.close();
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		try
		{
			PlatformUI.getWorkbench().openWorkbenchWindow("ch.eugster.colibri.admin.salespoint.perspective", null);
		}
		catch (final WorkbenchException e)
		{
			e.printStackTrace();
		}
		return null;
	}
}
