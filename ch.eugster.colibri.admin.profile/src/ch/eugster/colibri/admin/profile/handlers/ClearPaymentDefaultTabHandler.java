/*
 * Created on 28.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.Tab;

public class ClearPaymentDefaultTabHandler extends AbstractPersistenceClientHandler
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
				if (ssel.getFirstElement() instanceof Tab)
				{
					final Tab tab = (Tab) ssel.getFirstElement();
					tab.getConfigurable().setPaymentDefaultTab(null);
					if (persistenceService != null)
					{
						persistenceService.getServerService().merge(tab.getConfigurable());
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setEnabled(final Object object)
	{
		if (object instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) object;
			final Object sel = ctx.getParent().getVariable("selection");

			if (sel instanceof StructuredSelection)
			{
				final StructuredSelection ssel = (StructuredSelection) sel;
				if (ssel.size() == 1)
				{
					if (ssel.getFirstElement() instanceof Tab)
					{
						final Tab selectedTab = (Tab) ssel.getFirstElement();
						final Tab defaultTab = ((Tab) ssel.getFirstElement()).getConfigurable().getPaymentDefaultTab();
						this.setBaseEnabled((defaultTab != null) && selectedTab.equals(defaultTab));
					}
				}
			}
		}
	}

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
