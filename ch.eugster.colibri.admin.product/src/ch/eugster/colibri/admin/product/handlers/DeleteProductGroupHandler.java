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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.product.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.ProductGroup;

public class DeleteProductGroupHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("activeMenuSelection");
			final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");

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
							if (!productGroup.isDeletable())
							{
								MessageDialog.openWarning(new Shell(), "Fehler", productGroup.getName() + " darf nicht gelöscht werden.");
								return Status.OK_STATUS;
							}
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
									ProductGroup pg = (ProductGroup) element;
									try
									{
										pg = (ProductGroup) persistenceService.getServerService().delete(pg);
									} 
									catch (Exception e) 
									{
										e.printStackTrace();
										IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
										ErrorDialog.openError(shell, "Fehler", pg.getName() + " konnte nicht entfernt werden.", status);
									}
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
		boolean enabled = true;
		EvaluationContext context = (EvaluationContext) evaluationContext;
		if (context.getVariable("selection") instanceof StructuredSelection)
		{
			StructuredSelection ssel = (StructuredSelection) context.getVariable("selection");
			@SuppressWarnings("unchecked")
			Iterator<Object> iterator = ssel.iterator();
			while (iterator.hasNext())
			{
				Object element = iterator.next();
				if (element instanceof ProductGroup)
				{
					ProductGroup productGroup = (ProductGroup) element;
					if (!productGroup.isDeletable())
					{
						enabled = false;
						break;
					}
				}
			}
		}
		super.setBaseEnabled(enabled);
	}

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
