/*
 * Created on 16.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.tax.wizards.NewCurrentTaxWizard;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.admin.ui.wizards.WizardDialog;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;

public class StartNewCurrentTaxWizardHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("activePart");
			if (object instanceof IWorkbenchPart)
			{
				if (persistenceService != null)
				{
					final TaxTypeQuery typeQuery = (TaxTypeQuery) persistenceService.getServerService().getQuery(
							TaxType.class);
					final TaxType[] taxTypes = typeQuery.selectAll(false).toArray(new TaxType[0]);
					final TaxRateQuery rateQuery = (TaxRateQuery) persistenceService.getServerService().getQuery(
							TaxRate.class);
					final TaxRate[] taxRates = rateQuery.selectExceptCode("F").toArray(new TaxRate[0]);

					final IWorkbenchPart part = (IWorkbenchPart) object;
					final Wizard wizard = new NewCurrentTaxWizard(taxTypes, taxRates);
					final WizardDialog dialog = new WizardDialog(part.getSite().getShell(), wizard);
					dialog.open();
				}
			}
		}
		return null;
	}

	@Override
	protected BundleContext getBundleContext()
	{
		return TaxActivator.getDefault().getBundle().getBundleContext();
	}
	
}
