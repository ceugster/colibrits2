/*
 * Created on 14.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.product.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;

public class DeleteProductGroupHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("activeMenuSelection");

			if (object instanceof StructuredSelection)
			{
				final StructuredSelection ssel = (StructuredSelection) object;
				if (!ssel.isEmpty())
				{
					final StringBuffer sb = new StringBuffer();
					final Object[] elements = ssel.toArray();
					for (final Object element : elements)
					{
						if (element instanceof ProductGroup)
						{
							final ProductGroup productGroup = (ProductGroup) element;
							if (sb.length() > 0)
							{
								sb.append("\n");
							}
							sb.append(productGroup.getCode() + " " + productGroup.getName());
						}
					}

					String msg = null;
					if (ssel.size() == 1)
					{
						msg = "Soll die ausgewählte Warengruppe " + sb.toString() + " entfernt werden?";
					}
					else
					{
						msg = "Sollen die ausgewählten Warengruppen:\n" + sb.toString() + "\nentfernt werden?";
					}

					final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
					final MessageDialog dialog = new MessageDialog(shell, "Warengruppe entfernen", null, msg, MessageDialog.QUESTION, new String[] {
							"Ja", "Nein" }, 0);
					if (dialog.open() == Window.OK)
					{
						if (persistenceService != null)
						{
							for (final Object element : elements)
							{
								if (element instanceof ProductGroup)
								{
									persistenceService.getServerService().delete((ProductGroup) element);
								}
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public void setEnabled(Object evaluationContext) 
	{
		if (persistenceService == null)
		{
			super.setBaseEnabled(false);
		}
		else
		{
			CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService().getQuery(CommonSettings.class);
			CommonSettings settings = query.findDefault();
			ProductGroup defaultProductGroup = settings.getDefaultProductGroup();

			EvaluationContext context = (EvaluationContext) evaluationContext;
			if (context.getVariable("selection") instanceof StructuredSelection)
			{
				StructuredSelection ssel = (StructuredSelection) context.getVariable("selection");
				if (!ssel.isEmpty())
				{
					@SuppressWarnings("unchecked")
					Iterator<Object> iterator = ssel.iterator();
					while (iterator.hasNext())
					{
						Object element = iterator.next();
						if (element instanceof ProductGroup)
						{
							if (defaultProductGroup != null)
							{
								ProductGroup productGroup = (ProductGroup) element;
								if (productGroup.getId().equals(defaultProductGroup.getId()))
								{
									setBaseEnabled(false);
									return;
								}
							}
						}
						else
						{
							setBaseEnabled(false);
							return;
						}
					}
				}
			}
			super.setBaseEnabled(true);
		}
	}

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
