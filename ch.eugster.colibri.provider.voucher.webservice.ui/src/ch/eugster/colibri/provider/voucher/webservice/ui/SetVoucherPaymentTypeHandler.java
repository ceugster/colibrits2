package ch.eugster.colibri.provider.voucher.webservice.ui;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.osgi.framework.BundleContext;

import ch.eugster.colibri.admin.ui.handlers.AbstractPersistenceClientHandler;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;

public class SetVoucherPaymentTypeHandler extends AbstractPersistenceClientHandler implements
		IHandler 
{
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException 
	{
		if (event.getApplicationContext() instanceof EvaluationContext)
		{
			final EvaluationContext ctx = (EvaluationContext) event.getApplicationContext();
			final Object object = ctx.getParent().getVariable("selection");

			if (object instanceof StructuredSelection)
			{
				final StructuredSelection ssel = (StructuredSelection) object;
				if (ssel.getFirstElement() instanceof PaymentType)
				{
					if (persistenceService != null)
					{
						final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService()
								.getQuery(CommonSettings.class);
						final PaymentType paymentType = (PaymentType) ssel.getFirstElement();
						CommonSettings settings = query.findDefault();
						if (settings != null)
						{
							settings.setDefaultVoucherPaymentType(paymentType);
							try
							{
								settings = (CommonSettings) persistenceService.getServerService().merge(settings);
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
								IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
								ErrorDialog.openError((Shell) ctx.getVariable("activeShell"), "Fehler", paymentType.getName() + " konnte nicht als Standardzahlungsart für eGutscheine gespeichert werden.", status);
							}
						}
					}
				}
			}
		}
		return Status.OK_STATUS;
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
				if (ssel.getFirstElement() instanceof PaymentType)
				{
					final PaymentType paymentType = (PaymentType) ssel.getFirstElement();
					if (paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
					{
						PaymentType defaultPaymentType = getDefaultPaymentType();
						boolean enabled = defaultPaymentType == null || !defaultPaymentType.getId().equals(paymentType.getId());
						this.setBaseEnabled(enabled);
					}
				}
			}
		}
	}
	
	private PaymentType getDefaultPaymentType()
	{
		if (persistenceService != null)
		{
			CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService().getQuery(CommonSettings.class);
			CommonSettings settings = query.findDefault();
			return settings.getDefaultVoucherPaymentType();
		}
		return null;
	}

	@Override
	protected BundleContext getBundleContext()
	{
		return Activator.getDefault().getBundleContext();
	}
	
}
