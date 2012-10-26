package ch.eugster.colibri.admin.salespoint.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.salespoint.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;

/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class SetSalespointHandler extends AbstractPersistenceClientHandler
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
				if (ssel.getFirstElement() instanceof Salespoint)
				{
					final Salespoint serverSalespoint = (Salespoint) ssel.getFirstElement();

					if (persistenceService != null)
					{
						final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService()
								.getQuery(CommonSettings.class);
						final CommonSettings settings = query.findDefault();
						if (settings != null)
						{
							final String host = settings.getHostnameResolver().getHostname();
							if (host != null)
							{
								final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getServerService().getQuery(
										Salespoint.class);
								salespointQuery.clearSalespointHosts(host);
								serverSalespoint.setHost(host);
								persistenceService.getServerService().merge(serverSalespoint);
							}
						}
					}

				}
			}
		}
		return null;
	}

	public void setEnabledState()
	{
		if (persistenceService != null)
		{
			this.setBaseEnabled(true);
		}
		else
		{
			this.setBaseEnabled(false);
		}
	}

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
