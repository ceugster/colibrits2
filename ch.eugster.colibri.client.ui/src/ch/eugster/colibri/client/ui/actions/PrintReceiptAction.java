/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListModel;
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListSelectionModel;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.provider.service.ProviderQuery;

public class PrintReceiptAction extends UserPanelProfileAction implements ListSelectionListener, TableModelListener
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Drucken";

	public static final String ACTION_COMMAND = "print.action";

	private final CurrentReceiptListModel tableModel;

	private final CurrentReceiptListSelectionModel selectionModel;

	public PrintReceiptAction(final UserPanel userPanel, final Profile profile,
			final CurrentReceiptListModel tableModel, final CurrentReceiptListSelectionModel selectionModel)
	{
		super(PrintReceiptAction.TEXT, PrintReceiptAction.ACTION_COMMAND, userPanel, profile);
		this.tableModel = tableModel;
		this.selectionModel = selectionModel;
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		final Receipt receipt = this.tableModel.getReceipt(this.selectionModel.getMinSelectionIndex());
		final SalespointReceiptPrinterSettings settings = receipt.getSettlement().getSalespoint()
				.getReceiptPrinterSettings();
		if (settings != null)
		{
			final ServiceTracker<ProviderQuery, ProviderQuery> ProviderQueryTracker = new ServiceTracker<ProviderQuery, ProviderQuery>(Activator.getDefault().getBundle().getBundleContext(),
					ProviderQuery.class, null);
			ProviderQueryTracker.open();
			try
			{
				final ProviderQuery providerQuery = (ProviderQuery) ProviderQueryTracker.getService();
				if (providerQuery != null)
				{
					if (receipt.getCustomer() == null && receipt.getCustomerCode() != null && !receipt.getCustomerCode().isEmpty())
					{
						providerQuery.updateCustomer(receipt);
					}
				}
			}
			finally
			{
				ProviderQueryTracker.close();
			}
			final ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle()
					.getBundleContext(), EventAdmin.class, null);
			eventAdminTracker.open();
			try
			{
				final EventAdmin eventAdmin = (EventAdmin) eventAdminTracker.getService();
				if (eventAdmin != null)
				{
					eventAdmin.sendEvent(this.getEvent(receipt));
				}
			}
			finally
			{
				eventAdminTracker.close();
			}
		}
	}

	@Override
	public void tableChanged(final TableModelEvent event)
	{
		if (this.isEnabled())
		{
			this.setEnabled(this.shouldEnable());
		}
	}

	@Override
	public void valueChanged(final ListSelectionEvent event)
	{
		this.setEnabled(this.shouldEnable());
	}

	private Event getEvent(final Receipt receipt)
	{
		final Dictionary<String, Object> eventProps = new Hashtable<String, Object>();
		eventProps.put(EventConstants.BUNDLE, Activator.getDefault().getBundle());
		eventProps.put(EventConstants.BUNDLE_ID, Long.valueOf(Activator.getDefault().getBundle().getBundleId()));
		eventProps.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
		eventProps.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
		eventProps.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
		eventProps.put(IPrintable.class.getName(), receipt);
		eventProps.put("force", true);
		return new Event(Topic.PRINT_RECEIPT.topic(), eventProps);
	}

	private boolean shouldEnable()
	{
		return (this.tableModel.getRowCount() > 0) && (this.selectionModel.getMinSelectionIndex() > -1);
	}
}
