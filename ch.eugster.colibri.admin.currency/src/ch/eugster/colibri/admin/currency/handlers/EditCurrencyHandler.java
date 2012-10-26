package ch.eugster.colibri.admin.currency.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.currency.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.Currency;

/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class EditCurrencyHandler extends AbstractPersistenceClientHandler
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
				if (ssel.getFirstElement() instanceof Currency)
				{
					Activator.getDefault().editCurrency((Currency) ssel.getFirstElement());
				}
			}
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) 
	{
		EvaluationContext context = (EvaluationContext) evaluationContext;
		Object selection = context.getParent().getVariable("selection");
		if (selection instanceof StructuredSelection)
		{
			StructuredSelection ssel = (StructuredSelection) selection;
			setBaseEnabled(persistenceService != null && !ssel.isEmpty());
		}
		else
		{
			setBaseEnabled(false);
		}
	}
	

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
