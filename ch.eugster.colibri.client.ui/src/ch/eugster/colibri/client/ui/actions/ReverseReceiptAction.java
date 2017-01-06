/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListModel;
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListSelectionModel;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.RoleProperty;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderQuery;
import ch.eugster.colibri.provider.service.ProviderService;

public class ReverseReceiptAction extends UserPanelProfileAction implements ListSelectionListener, TableModelListener
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Stornieren";

	public static final String ACTION_COMMAND = FunctionType.FUNCTION_REVERSE_RECEIPT.key();

	private final CurrentReceiptListModel tableModel;

	private final CurrentReceiptListSelectionModel selectionModel;

	private PrintReceiptAction printReceiptAction;

	private ServiceTracker<EventAdmin, EventAdmin> tracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
	
	public ReverseReceiptAction(final UserPanel userPanel, final Profile profile,
			final CurrentReceiptListModel tableModel, final CurrentReceiptListSelectionModel selectionModel)
	{
		super(ReverseReceiptAction.TEXT, ReverseReceiptAction.ACTION_COMMAND, userPanel, profile);
		this.tableModel = tableModel;
		this.selectionModel = selectionModel;
		this.tracker.open();
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		if (this.selectionModel.getMinSelectionIndex() > -1)
		{
			final ServiceTracker<PersistenceService, PersistenceService> serviceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle()
					.getBundleContext(), PersistenceService.class, null);
			serviceTracker.open();
			final PersistenceService persistenceService = (PersistenceService) serviceTracker.getService();
			if (persistenceService != null)
			{
				Receipt receipt = this.tableModel.getReceipt(this.selectionModel.getMinSelectionIndex());
				if (receipt.getState().equals(Receipt.State.SAVED))
				{
					this.showMessage(receipt);
					receipt.setState(Receipt.State.REVERSED);
				}
				else if (receipt.getState().equals(Receipt.State.REVERSED))
				{
					receipt.setState(Receipt.State.SAVED);
				}
				try
				{
					receipt = (Receipt) persistenceService.getCacheService().merge(receipt, false);
					this.tableModel.setReceipt(receipt, this.selectionModel.getMinSelectionIndex());
				} 
				catch (Exception ex) 
				{
					ex.printStackTrace();
					MessageDialog.showInformation(Activator.getDefault().getFrame(), userPanel.getProfile(), "Fehler", "Der Status des Belegs konnte nicht gändert werden.", MessageDialog.TYPE_ERROR, this.userPanel.getMainTabbedPane().isFailOver());
				}

				if (this.printReceiptAction != null)
				{
					this.printReceiptAction.actionPerformed(e);
				}

				this.tableModel.fireTableDataChanged();
				final EventAdmin eventAdmin = (EventAdmin) this.tracker.getService();
				if (eventAdmin != null)
				{
					eventAdmin.sendEvent(this.getEvent(Topic.STORE_RECEIPT.topic(), receipt));
				}
			}
			serviceTracker.close();
		}
	}
	
	private Event getEvent(final String topics, final Receipt receipt)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.SERVICE, this.tracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID,
				this.tracker.getServiceReference().getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
		IStatus status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.STORE_RECEIPT.topic());
		properties.put(IPrintable.class.getName(), receipt);
		properties.put("status", status);
		properties.put("open.drawer", Boolean.valueOf(false));
		properties.put("copies", Integer.valueOf(0));
		return new Event(topics, properties);
	}

	public void addPrintReceiptAction(PrintReceiptAction action)
	{
		this.printReceiptAction = action;
	}

	@Override
	public void tableChanged(final TableModelEvent event)
	{
		this.setEnabled(this.shouldEnable());
	}

	@Override
	public void valueChanged(final ListSelectionEvent event)
	{
		this.setEnabled(this.shouldEnable());
	}

	private String getProviderInterfaceMessage()
	{
		String message = null;
		final ServiceTracker<ProviderQuery, ProviderQuery> providerQueryTracker = new ServiceTracker<ProviderQuery, ProviderQuery>(Activator.getDefault().getBundle()
				.getBundleContext(), ProviderQuery.class, null);
		providerQueryTracker.open();
		final ProviderQuery providerQuery = (ProviderQuery) providerQueryTracker.getService();
		if (providerQuery != null)
		{
			message = "Bitte stellen Sie sicher, dass die Rückbuchung in " + providerQuery.getName()
					+ " manuell vorgenommen wird:\n";
		}
		providerQueryTracker.close();
		return message;
	}

	private boolean shouldEnable()
	{
		if ((this.tableModel.getRowCount() == 0) || (this.selectionModel.getMinSelectionIndex() == -1))
		{
			return false;
		}
		else
		{
			if (this.userPanel.getUser().getRole().getId().equals(Long.valueOf(1l)))
			{
				return true;
			}
			else
			{
				final RoleProperty roleProperty = this.userPanel.getUser().getRole()
						.getRoleProperty(ReverseReceiptAction.ACTION_COMMAND);
				return (!((roleProperty == null) || roleProperty.isDeleted() || !Boolean.valueOf(
						roleProperty.getValue()).booleanValue()));
			}
		}
	}

	private void showMessage(final Receipt receipt)
	{
		final Position[] payedInvoices = receipt.getPayedInvoices();
		if (payedInvoices.length > 0)
		{
			int count = 0;
			StringBuilder msg = new StringBuilder();
			final DateFormat df = DateFormat.getDateInstance();
			for (Position payedInvoice : payedInvoices)
			{
				if (payedInvoice.getProduct() != null)
				{
					Calendar calendar = payedInvoice.getProduct().getInvoiceDate();
					StringBuilder invoice = new StringBuilder();
					if (calendar != null)
					{
						invoice = invoice.append("Datum: " + df.format(calendar.getTime()));
					}
					String number = payedInvoice.getProduct().getInvoiceNumber();
					if (number != null)
					{
						if (invoice.length() > 0)
						{
							invoice.append(", ");
						}
						invoice = invoice.append("Nummer: " + payedInvoice.getProduct().getInvoiceNumber());
					}
					if (invoice.length() > 0)
					{
						count++;
						invoice = invoice.insert(0, "\n");
					}
					msg = msg.append(invoice.toString());
				}
			}
			if (msg.length() > 0)
			{
				StringBuilder message = new StringBuilder("Der Beleg enthält "
						+ (count == 1 ? "eine Position" : count + " Positionen") + " mit bezahlten Rechnungen.\n");
				message = message.append("Diese " + (count == 1 ? "kann" : "können")
						+ " nicht automatisch zurückgebucht werden.\n");

				String pim = this.getProviderInterfaceMessage();
				if (pim != null)
				{
					message = message.append(pim);
					message = message.append(msg);
				}
				MessageDialog.showInformation(Activator.getDefault().getFrame(), this.userPanel.getSalespoint()
						.getProfile(), "Bezahlte Rechnungen", message.toString(), MessageDialog.TYPE_INFORMATION, this.userPanel.getMainTabbedPane().isFailOver());
			}
		}
		else if (receipt.getState().equals(Receipt.State.REVERSED))
		{
			List<Position> ordereds = receipt.getOrderedPositions();
			if (!ordereds.isEmpty())
			{
				ServiceTracker<ProviderService, ProviderService> tracker = new ServiceTracker<ProviderService, ProviderService>(Activator.getDefault().getBundle().getBundleContext(), ProviderService.class, null);
				tracker.open();
				try
				{
					StringBuilder message = new StringBuilder("Der Beleg enthält folgende bestellten Titel:\n");
					for (Position ordered : ordereds)
					{
						if (ordered.getProduct() != null)
						{
							message = message.append("Artikelcode:  " + ordered.getProduct().getCode());
						}
						message = message.append("\nBestellung: " + ordered.getSearchValue());
						message = message.append("Herkunft:     ");
						ServiceReference<ProviderService>[] references = tracker.getServiceReferences();
						if (references != null && references.length > 0)
						{
							for (ServiceReference<ProviderService> reference : references)
							{
								ProviderService service = tracker.getService(reference);
								if (service != null)
								{
									if (ordered.getProvider() != null && ordered.getProvider().equals(service.getProviderId()))
									{
										message = message.append(service.getName());
									}
								}
							}
						}
						message = message.append("\n");
					}
					message = message.append("\nDiese Titel können nicht automatisch in der Lagerbewirtschaftung zurückgebucht werden.\n");
					message = message.append("Bitte führen Sie die Buchungskorrektur manuell durch.");
					MessageDialog.showInformation(Activator.getDefault().getFrame(), this.userPanel.getSalespoint()
							.getProfile(), "Bestellte Titel", message.toString(), MessageDialog.TYPE_INFORMATION, this.userPanel.getMainTabbedPane().isFailOver());
				}
				finally
				{
					tracker.close();
				}
			}
		}
	}
}
