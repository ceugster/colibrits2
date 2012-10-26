/*
 * Created on 14.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;

public class SetPayedInvoiceHandler extends AbstractPersistenceClientHandler
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
				if (ssel.getFirstElement() instanceof ProductGroup)
				{
					if (persistenceService != null)
					{
						final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService()
								.getQuery(CommonSettings.class);
						final ProductGroup productGroup = (ProductGroup) ssel.getFirstElement();
						CommonSettings settings = query.findDefault();
						if (settings != null)
						{
							settings.setPayedInvoice(productGroup);
							settings = (CommonSettings) persistenceService.getServerService().merge(settings);
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setEnabled(final Object evaluationContext)
	{
		if (evaluationContext instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) evaluationContext;
			final Object object = ctx.getParent().getVariable("selection");

			if (object instanceof StructuredSelection)
			{
				final StructuredSelection ssel = (StructuredSelection) object;
				if (ssel.getFirstElement() instanceof ProductGroup)
				{
					final ProductGroup productGroup = (ProductGroup) ssel.getFirstElement();
					ProductGroup payedInvoice = getPayedInvoice();
					boolean enabled = payedInvoice == null || !payedInvoice.getId().equals(productGroup.getId());
					this.setBaseEnabled(enabled);
				}
			}
		}
	}
	
	private ProductGroup getPayedInvoice()
	{
		if (persistenceService != null)
		{
			CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService().getQuery(CommonSettings.class);
			CommonSettings settings = query.findDefault();
			return settings.getPayedInvoice();
		}
		return null;
	}

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
