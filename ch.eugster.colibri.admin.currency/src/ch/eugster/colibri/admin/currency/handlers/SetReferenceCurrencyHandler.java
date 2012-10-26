/*
 * Created on 2009 2 1
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.currency.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbenchPart;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.currency.Activator;
import ch.eugster.colibri.admin.currency.wizards.ReferenceCurrencyWizard;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.admin.ui.wizards.WizardDialog;
import ch.eugster.colibri.persistence.model.CommonSettings;

public class SetReferenceCurrencyHandler extends AbstractPersistenceClientHandler
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
				final IWorkbenchPart part = (IWorkbenchPart) object;
				final CommonSettings settings = (CommonSettings) persistenceService.getServerService().find(CommonSettings.class,
						Long.valueOf(1L));
				final Wizard wizard = new ReferenceCurrencyWizard(settings);
				final WizardDialog dialog = new WizardDialog(part.getSite().getShell(), wizard);
				dialog.open();
			}
		}
		return null;
	}
	@Override
	public void setEnabled(Object evaluationContext) 
	{
		EvaluationContext context = (EvaluationContext) evaluationContext;
		Object selection = context.getVariable("selection");
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
