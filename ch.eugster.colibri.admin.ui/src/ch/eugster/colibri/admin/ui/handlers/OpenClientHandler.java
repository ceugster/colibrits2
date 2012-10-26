package ch.eugster.colibri.admin.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.ui.Activator;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class OpenClientHandler extends AbstractHandler implements IHandler
{
	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public OpenClientHandler()
	{
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final SalespointQuery query = (SalespointQuery) persistenceService.getCacheService().getQuery(
					Salespoint.class);
			Salespoint salespoint = query.getCurrentSalespoint();
			if (salespoint == null)
			{
				final EvaluationContext context = (EvaluationContext) event.getApplicationContext();
				final Shell shell = (Shell) context.getParent().getVariable("activeShell");
				final MessageDialog dialog = new MessageDialog(shell, "Kasse nicht registriert", null,
						"Für diese Station wurde noch keine Kasse registriert.", MessageDialog.WARNING,
						new String[] { "OK" }, 0);
				dialog.open();
			}
			else
			{
				try
				{
					PlatformUI.getWorkbench().openWorkbenchWindow("ch.eugster.colibri.client.perspective", null);
				}
				catch (final WorkbenchException e)
				{
					e.printStackTrace();
				}
			}
		}
		else
		{
			final EvaluationContext context = (EvaluationContext) event.getApplicationContext();
			final Shell shell = (Shell) context.getParent().getVariable("activeShell");
			final MessageDialog dialog = new MessageDialog(shell, "Kasse nicht verfügbar", null,
					"Die Kasse ist zur Zeit nicht verfügbar. Die lokale Datenhaltung ist nicht aktiv.",
					MessageDialog.WARNING, new String[] { "OK" }, 0);
			dialog.open();
		}
		return null;
	}
}
