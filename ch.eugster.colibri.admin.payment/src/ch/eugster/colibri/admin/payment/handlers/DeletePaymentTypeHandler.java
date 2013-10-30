/*
 * Created on 14.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.payment.handlers;

import java.text.DecimalFormat;

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

import ch.eugster.colibri.admin.payment.Activator;
import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;

public class DeletePaymentTypeHandler extends AbstractPersistenceClientHandler
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
				if (!ssel.isEmpty())
				{
					final StringBuffer sb = new StringBuffer();
					final Object[] elements = ssel.toArray();
					for (final Object element : elements)
					{
						if (element instanceof PaymentType)
						{
							final PaymentType paymentType = (PaymentType) element;
							if (sb.length() > 0)
							{
								sb.append("\n");
							}
							sb.append(paymentType.getCode() + " " + paymentType.getName());
						}
					}

					String msg = null;
					if (ssel.size() == 1)
					{
						msg = "Soll die ausgewählte Zahlungsart " + sb.toString() + " entfernt werden?";
					}
					else
					{
						msg = "Sollen die ausgewählten Zahlungsarten:\n" + sb.toString() + "\nentfernt werden?";
					}

					final Shell shell = (Shell) ctx.getParent().getVariable("activeShell");
					final MessageDialog dialog = new MessageDialog(shell, "Zahlungsart entfernen", null, msg, MessageDialog.QUESTION, new String[] {
							"Ja", "Nein" }, 0);
					if (dialog.open() == Window.OK)
					{
						if (persistenceService != null)
						{
							for (final Object element : elements)
							{
								if (element instanceof PaymentType)
								{
									PaymentType paymentType = (PaymentType) element;
									try
									{
										persistenceService.getServerService().delete(paymentType);
									} 
									catch (Exception e) 
									{
										e.printStackTrace();
										IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
										ErrorDialog.openError(shell, "Fehler", paymentType.getName() + " konnte nicht entfernt werden.", status);
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
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundle().getBundleContext();
	}
	
}
