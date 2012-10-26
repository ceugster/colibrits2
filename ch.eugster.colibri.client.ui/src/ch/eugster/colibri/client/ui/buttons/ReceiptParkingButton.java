/*
 * Created on 15.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.buttons;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.ConfigurableAction;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.PaymentChangeMediator;
import ch.eugster.colibri.client.ui.events.PositionChangeMediator;
import ch.eugster.colibri.client.ui.events.ReceiptChangeMediator;
import ch.eugster.colibri.client.ui.panels.user.PaymentWrapper;
import ch.eugster.colibri.client.ui.panels.user.PositionWrapper;
import ch.eugster.colibri.client.ui.panels.user.ReceiptWrapper;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ReceiptParkingButton extends ConfigurableButton implements PropertyChangeListener, DisposeListener
{
	public static final long serialVersionUID = 0l;

	private UserPanel userPanel;

	private String[] receiptProperties = new String[] { ReceiptWrapper.KEY_RECEIPT };

	private String[] positionProperties = new String[] { ReceiptWrapper.KEY_PROPERTY_POSITIONS, PositionWrapper.KEY_POSITION };

	private String[] paymentProperties = new String[] { ReceiptWrapper.KEY_PROPERTY_PAYMENTS, PaymentWrapper.KEY_PAYMENT };

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ReceiptParkingButton(final ConfigurableAction action, final Key key)
	{
		super(action, key);
		this.userPanel = action.getUserPanel();
		new ReceiptChangeMediator(this.userPanel, this, this.receiptProperties);
		new PositionChangeMediator(this.userPanel, this, this.positionProperties);
		new PaymentChangeMediator(this.userPanel, this, this.paymentProperties);
		this.userPanel.addDisposeListener(this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		if (this.userPanel.getReceiptWrapper().isReceiptEmpty())
		{
			final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
			if (persistenceService != null)
			{
				final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
				final long size = query.countParked(this.userPanel.getUser());
				this.setText("Parkliste (" + size + ")");
			}
		}
		else
		{
			this.setText("Parkieren");
		}
	}
}
