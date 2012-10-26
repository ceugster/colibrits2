/*
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
import ch.eugster.colibri.admin.product.views.ProductView;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;

public class AddProductGroupHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext context = (EvaluationContext) event.getApplicationContext();
			if (context.getParent().getVariable("activePart") instanceof ProductView)
			{
				final ProductView view = (ProductView) context.getParent().getVariable("activePart");
				final StructuredSelection ssel = (StructuredSelection) view.getViewer().getSelection();
				if (ssel.getFirstElement() instanceof ProductGroupType)
				{
					if (persistenceService != null)
					{
						final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService()
								.getQuery(CommonSettings.class);
						final CommonSettings commonSettings = query.findDefault();

						final ProductGroupType type = (ProductGroupType) ssel.getFirstElement();
						Activator.getDefault().editProductGroup(ProductGroup.newInstance(type, commonSettings));
					}
				}
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
