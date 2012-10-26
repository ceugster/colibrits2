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
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.tax.wizards.TaxRateWizard;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.admin.ui.wizards.ListAndEditWizardDialog;
import ch.eugster.colibri.persistence.model.TaxRate;

public class EditTaxRateHandler extends AbstractPersistenceClientHandler
{
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("activePart");
			if (object instanceof IWorkbenchPart)
			{
				final IWorkbenchPart part = (IWorkbenchPart) object;
				final TaxRateWizard wizard = new TaxRateWizard();
				final ListAndEditWizardDialog dialog = new ListAndEditWizardDialog(part.getSite().getShell(), wizard);
				dialog.open();
			}
		}
		return null;
	}

	public void setEnabled(EvaluationContext context)
	{
		final EvaluationContext ctx = (EvaluationContext) context;
		final Object object = ctx.getParent().getVariable("selection");

		if (object instanceof StructuredSelection)
		{
			final StructuredSelection ssel = (StructuredSelection) object;
			setBaseEnabled(ssel.size() == 1 && ssel.getFirstElement() instanceof TaxRate);
		}
	}
	
	@Override
	protected BundleContext getBundleContext()
	{
		return TaxActivator.getDefault().getBundle().getBundleContext();
	}
	
}
