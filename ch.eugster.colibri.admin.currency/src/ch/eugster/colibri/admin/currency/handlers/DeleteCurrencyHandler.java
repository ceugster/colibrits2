package ch.eugster.colibri.admin.currency.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.currency.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.Currency;

/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

public class DeleteCurrencyHandler extends AbstractPersistenceClientHandler
{
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("selection"); //$NON-NLS-1$

			if (object instanceof StructuredSelection)
			{
				final StructuredSelection ssel = (StructuredSelection) object;
				if (!ssel.isEmpty())
				{
					final StringBuffer sb = new StringBuffer();
					final Object[] elements = ssel.toArray();
					for (final Object element : elements)
					{
						if (element instanceof Currency)
						{
							final Currency currency = (Currency) element;
							if (sb.length() > 0)
							{
								sb.append("\n"); //$NON-NLS-1$
							}
							sb.append(currency.getName());
						}
					}

					String msg = null;
					if (ssel.size() == 1)
					{
						msg = "Soll die ausgew�hlte W�hrung:\n" + sb.toString() + " entfernt werden?";
					}
					else
					{
						msg = "Sollen die ausgew�hlten W�hrungen:\n" + sb.toString() + " entfernt werden?";
					}

					final Shell shell = (Shell) ctx.getParent().getVariable("activeShell"); //$NON-NLS-1$
					final MessageDialog dialog = new MessageDialog(shell, "W�hrung entfernen", null, msg, MessageDialog.QUESTION, new String[] {
							"Ja", "Nein" }, 0);
					if (dialog.open() == Window.OK)
					{
						for (final Object element : elements)
						{
							if (element instanceof Currency)
							{
								persistenceService.getServerService().delete((Currency) element);
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
		EvaluationContext context = (EvaluationContext) evaluationContext;
		Object selection = context.getParent().getVariable("selection");
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
