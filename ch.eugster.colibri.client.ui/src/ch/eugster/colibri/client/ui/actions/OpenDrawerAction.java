/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.periphery.printer.service.ReceiptPrinterService;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class OpenDrawerAction extends ConfigurableAction implements DisposeListener
{
	private static final long serialVersionUID = 0l;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ReceiptPrinterService, ReceiptPrinterService> receiptPrinterServiceTracker;
	
	public OpenDrawerAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		userPanel.addDisposeListener(this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
		
		this.receiptPrinterServiceTracker = new ServiceTracker<ReceiptPrinterService, ReceiptPrinterService>(Activator.getDefault().getBundle().getBundleContext(), ReceiptPrinterService.class, null);
		this.receiptPrinterServiceTracker.open();
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		String componentName = this.getUserPanel().getLocalSalespoint().getReceiptPrinterSettings().getComponentName();
		Object[] objects = this.receiptPrinterServiceTracker.getServices();
		if (objects != null)
		{
			for (Object object : objects)
			{
				if (object instanceof ReceiptPrinterService)
				{
					ReceiptPrinterService service = (ReceiptPrinterService) object;
					if (service.getReceiptPrinterSettings().getComponentName().equals(componentName))
					{
						service.openDrawer(getCurrency());
					}
				}
			}
		}
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		this.receiptPrinterServiceTracker.close();
	}

	public Currency getCurrency()
	{
		Currency currency = null;
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			currency = (Currency) persistenceService.getCacheService().find(Currency.class, this.key.getParentId());
		}
		return currency;
	}
	
//	public PaymentType getPaymentType()
//	{
//		PaymentType paymentType = null;
//		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
//		if (persistenceService != null)
//		{
//			paymentType = (PaymentType) persistenceService.getCacheService().find(PaymentType.class, this.key.getParentId());
//		}
//
//		return paymentType;
//	}
}
