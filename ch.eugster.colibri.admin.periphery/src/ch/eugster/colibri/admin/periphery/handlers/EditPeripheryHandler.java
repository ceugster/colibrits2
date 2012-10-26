/*
 * Created on 14.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.periphery.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import ch.eugster.colibri.admin.periphery.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.periphery.display.service.CustomerDisplayService;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;

public class EditPeripheryHandler extends AbstractPersistenceClientHandler
{
	public void setEnabled(EvaluationContext context)
	{
		setBaseEnabled(persistenceService != null);
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
				if (ssel.getFirstElement() instanceof ServiceReference)
				{
					final ServiceReference<?> ref = (ServiceReference<?>) ssel.getFirstElement();
					final String componentName = (String) ref.getProperty("component.name");
					if (componentName instanceof String)
					{
						final Integer group = (Integer) ref.getProperty("component.group");
						if (group instanceof Integer)
						{
							final int peripheryGroup = (group).intValue();
							if (peripheryGroup == 0)
							{
								@SuppressWarnings("unchecked")
								ServiceReference<ReceiptPrinterService> reference = (ServiceReference<ReceiptPrinterService>) ref;
								Activator.getDefault().editReceiptPrinterPeriphery(componentName, reference);
							}
							else if (peripheryGroup == 1)
							{
								@SuppressWarnings("unchecked")
								ServiceReference<CustomerDisplayService> reference = (ServiceReference<CustomerDisplayService>) ref;
								Activator.getDefault().editCustomerDisplayPeriphery(componentName, reference);
							}
						}
					}
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
}
