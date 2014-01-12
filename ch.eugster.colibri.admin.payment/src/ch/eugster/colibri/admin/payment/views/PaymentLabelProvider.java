/*
 * Created on 17.12.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.payment.views;

import java.text.NumberFormat;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.payment.Activator;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class PaymentLabelProvider extends LabelProvider implements ILabelProvider
{
	private static NumberFormat moneyFormatter;
	
	private CommonSettings settings = null;

	public PaymentLabelProvider()
	{
		PaymentLabelProvider.moneyFormatter = NumberFormat.getNumberInstance();
	}

	@Override
	public Image getImage(final Object element)
	{
		if (element instanceof PaymentTypeGroup)
		{
			final PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) element;
			if (paymentTypeGroup.equals(PaymentTypeGroup.CASH))
			{
				return Activator.getDefault().getImageRegistry().get("MONEY_GREEN");
			}
			else if (paymentTypeGroup.equals(PaymentTypeGroup.VOUCHER))
			{
				return Activator.getDefault().getImageRegistry().get("GUTSCHEIN");
			}
			else if (paymentTypeGroup.equals(PaymentTypeGroup.CREDIT))
			{
				return Activator.getDefault().getImageRegistry().get("CREDITCARD");
			}
			else if (paymentTypeGroup.equals(PaymentTypeGroup.DEBIT))
			{
				return Activator.getDefault().getImageRegistry().get("DEBITCARD");
			}
		}
		else if (element instanceof PaymentType)
		{
			final PaymentType paymentType = (PaymentType) element;
			if (paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.CASH))
			{
				return Activator.getDefault().getImageRegistry().get("MONEY_GREEN");
			}
			else if (paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
			{
				if (paymentType.getCode().toLowerCase().contains("sbvv") || paymentType.getName().toLowerCase().contains("schweizer"))
				{
					return Activator.getDefault().getImageRegistry().get("SBVV");
				}
				else
				{
					return Activator.getDefault().getImageRegistry().get("GUTSCHEIN");
				}
			}
			else if (paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.CREDIT))
			{
				if (paymentType.getCode().toLowerCase().contains("ae") || paymentType.getName().toLowerCase().contains("ae"))
				{
					return Activator.getDefault().getImageRegistry().get("AE");
				}
				else if (paymentType.getCode().toLowerCase().contains("american express")
						|| paymentType.getName().toLowerCase().contains("american express"))
				{
					return Activator.getDefault().getImageRegistry().get("AE");
				}
				else if (paymentType.getCode().toLowerCase().contains("diners") || paymentType.getName().toLowerCase().contains("diners"))
				{
					return Activator.getDefault().getImageRegistry().get("DINERS");
				}
				else if (paymentType.getCode().toLowerCase().contains("mc") || paymentType.getName().toLowerCase().contains("mc"))
				{
					return Activator.getDefault().getImageRegistry().get("MASTERCARD");
				}
				else if (paymentType.getCode().toLowerCase().contains("mastercard") || paymentType.getName().toLowerCase().contains("mastercard"))
				{
					return Activator.getDefault().getImageRegistry().get("MASTERCARD");
				}
				else if (paymentType.getCode().toLowerCase().contains("visa") || paymentType.getName().toLowerCase().contains("visa"))
				{
					return Activator.getDefault().getImageRegistry().get("VISA");
				}
				else
				{
					return Activator.getDefault().getImageRegistry().get("CREDITCARD");
				}
			}
			else if (paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.DEBIT))
			{
				if (paymentType.getCode().toLowerCase().contains("maestro") || paymentType.getName().toLowerCase().contains("maestro"))
				{
					return Activator.getDefault().getImageRegistry().get("MAESTRO");
				}
				else if (paymentType.getCode().toLowerCase().contains("ec") || paymentType.getName().toLowerCase().contains("ec"))
				{
					return Activator.getDefault().getImageRegistry().get("EC");
				}
				else if (paymentType.getCode().toLowerCase().contains("euro") || paymentType.getName().toLowerCase().contains("euro"))
				{
					return Activator.getDefault().getImageRegistry().get("EC");
				}
				else if (paymentType.getCode().toLowerCase().contains("pc") || paymentType.getName().toLowerCase().contains("pc"))
				{
					return Activator.getDefault().getImageRegistry().get("POSTCARD");
				}
				else if (paymentType.getCode().toLowerCase().contains("postcard") || paymentType.getName().toLowerCase().contains("postcard"))
				{
					return Activator.getDefault().getImageRegistry().get("POSTCARD");
				}
				else
				{
					return Activator.getDefault().getImageRegistry().get("DEBITCARD");
				}
			}
		}
		else if (element instanceof Money)
		{
			return Activator.getDefault().getImageRegistry().get("money.png");
		}
		return null;
	}

	@Override
	public String getText(final Object element)
	{
		if (element instanceof PaymentTypeGroup)
		{
			final PaymentTypeGroup paymentTypeGroup = (PaymentTypeGroup) element;
			return paymentTypeGroup.toString();
		}
		else if (element instanceof PaymentType)
		{
			StringBuilder sb = new StringBuilder();
			final PaymentType paymentType = (PaymentType) element;
			if (paymentType.getCode().toLowerCase().equals(paymentType.getName().toLowerCase()))
			{
				sb = sb.append(paymentType.getCode());
			}
			else
			{
				sb = sb.append(paymentType.getCode() + " - " + paymentType.getName());
			}
			if (paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.CASH))
			{
				String currency = paymentType.getCurrency().format();
				if (!sb.toString().contains(currency))
				{
					sb = sb.append(" " + currency);
				}
			}
			CommonSettings settings = getCommonSettings();
			
			if (settings.getDefaultVoucherPaymentType() != null && settings.getDefaultVoucherPaymentType().getId().equals(paymentType.getId()))
			{
				sb = sb.append(" (eGutschein)");
			}
			return sb.toString();
		}
		else if (element instanceof Money)
		{
			final Money money = (Money) element;
			final java.util.Currency cur = java.util.Currency.getInstance(money.getPaymentType().getCurrency().getCode());
			PaymentLabelProvider.moneyFormatter.setMinimumFractionDigits(cur.getDefaultFractionDigits());
			PaymentLabelProvider.moneyFormatter.setMaximumFractionDigits(cur.getDefaultFractionDigits());
			return PaymentLabelProvider.moneyFormatter.format(money.getValue());
		}

		return "";
	}
	
	private CommonSettings getCommonSettings()
	{
		if (settings == null)
		{
			ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
			try
			{
				tracker.open();
				PersistenceService service = tracker.getService();
				CommonSettingsQuery query = (CommonSettingsQuery) service.getServerService().getQuery(CommonSettings.class);
				settings = query.findDefault();
			}
			finally
			{
				tracker.close();
			}
		}
		return settings;
	}
}
