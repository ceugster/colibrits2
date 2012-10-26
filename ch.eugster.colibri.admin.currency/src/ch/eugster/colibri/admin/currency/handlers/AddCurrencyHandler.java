package ch.eugster.colibri.admin.currency.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.currency.Activator;
import ch.eugster.colibri.admin.currency.views.CurrencyView;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.Currency;

/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class AddCurrencyHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext context = (EvaluationContext) event.getApplicationContext();
			if (context.getParent().getVariable("activePart") instanceof CurrencyView)
			{
				Activator.getDefault().editCurrency(Currency.newInstance());
			}
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) 
	{
		setBaseEnabled(persistenceService != null);
	}
	

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
