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

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListModel;
import ch.eugster.colibri.client.ui.panels.user.receipts.CurrentReceiptListSelectionModel;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.RoleProperty;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderInterface;

public class ReverseReceiptAction extends UserPanelProfileAction implements ListSelectionListener, TableModelListener
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Stornieren";

	public static final String ACTION_COMMAND = "reverse.action";

	private final CurrentReceiptListModel tableModel;

	private final CurrentReceiptListSelectionModel selectionModel;

	private PrintReceiptAction printReceiptAction;
	
	public ReverseReceiptAction(final UserPanel userPanel, final Profile profile,
			final CurrentReceiptListModel tableModel, final CurrentReceiptListSelectionModel selectionModel)
	{
		super(ReverseReceiptAction.TEXT, ReverseReceiptAction.ACTION_COMMAND, userPanel, profile);
		this.tableModel = tableModel;
		this.selectionModel = selectionModel;
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
				receipt.setTransferred(false);
				this.tableModel.setReceipt((Receipt) persistenceService.getCacheService().merge(receipt, false),
						this.selectionModel.getMinSelectionIndex());

				if (this.printReceiptAction != null)
				{
					this.printReceiptAction.actionPerformed(e);
				}

				this.tableModel.fireTableDataChanged();
			}
			serviceTracker.close();
		}
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
		final ServiceTracker<ProviderInterface, ProviderInterface> providerInterfaceTracker = new ServiceTracker<ProviderInterface, ProviderInterface>(Activator.getDefault().getBundle()
				.getBundleContext(), ProviderInterface.class, null);
		providerInterfaceTracker.open();
		final ProviderInterface providerInterface = (ProviderInterface) providerInterfaceTracker.getService();
		if (providerInterface != null)
		{
			message = "Bitte stellen Sie sicher, dass die Rückbuchung in " + providerInterface.getName()
					+ " manuell vorgenommen wird:\n";
		}
		providerInterfaceTracker.close();
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
						.getProfile(), "Bezahlte Rechnungen", message.toString(), MessageDialog.TYPE_INFORMATION);
			}
		}
	}
}
