/*
 * Created on 28.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.profile.Activator;
import ch.eugster.colibri.persistence.model.Configurable.ConfigurableType;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class SetPaymentDefaultTabHandler extends AbstractHandler implements IHandler
{
	@Override
	public void setEnabled(Object evaluationContext) 
	{
		EvaluationContext context = (EvaluationContext) evaluationContext;
		Object object = context.getVariable("selection");
		if (object instanceof StructuredSelection)
		{
			StructuredSelection ssel = (StructuredSelection) object;
			if (ssel != null && !ssel.isEmpty())
			{
				if (ssel.getFirstElement() instanceof Tab)
				{
					Tab tab = (Tab) ssel.getFirstElement();
					ConfigurableType type = tab.getConfigurable().getConfigurableType();
					if (type.equals(ConfigurableType.PAYMENT_TYPE) || type.equals(ConfigurableType.FUNCTION))
					{
						this.setBaseEnabled(true);
						return;
					}
				}
			}
		}
		setBaseEnabled(false);
	}

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public SetPaymentDefaultTabHandler()
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
					tab.getConfigurable().setPaymentDefaultTab(tab);
					final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker
							.getService();
					if (persistenceService != null)
					{
						persistenceService.getServerService().merge(tab.getConfigurable());
					}
				}
			}
		}
		return null;
	}
}
