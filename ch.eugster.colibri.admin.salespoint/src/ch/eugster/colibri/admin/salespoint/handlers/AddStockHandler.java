package ch.eugster.colibri.admin.salespoint.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.salespoint.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;

/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class AddStockHandler extends AbstractPersistenceClientHandler
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
				if ((ssel.getFirstElement() instanceof Salespoint) && (ssel.size() == 1))
				{
					final Stock stock = Stock.newInstance((Salespoint) ssel.getFirstElement());
					Activator.getDefault().editStock(stock);
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
	
	public void setEnabled(EvaluationContext context)
	{
		final EvaluationContext ctx = (EvaluationContext) context;
		final Object object = ctx.getParent().getVariable("selection");

		if (object instanceof StructuredSelection)
		{
			final StructuredSelection ssel = (StructuredSelection) object;
			setBaseEnabled(ssel.getFirstElement() instanceof Salespoint);
		}
	}
	
}
