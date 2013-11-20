/*
 * Created on 17.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.payment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.eclipse.core.runtime.Status;
import org.osgi.framework.ServiceReference;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.ui.buttons.HTMLButton;

public class PaymentListModel extends AbstractTableModel implements ActionListener
{
	public static final long serialVersionUID = 0l;

	public static final String[] columnNames = new String[] { "Zahlungsart", "Betrag FW", "Betrag" };

	public static final int[] columnAlignments = new int[] { SwingConstants.LEFT, SwingConstants.RIGHT,
			SwingConstants.RIGHT };

	public static final int COL_PAYMENT_TYPE = 0;

	public static final int COL_AMOUNT_FC = 1;

	public static final int COL_AMOUNT = 2;

	private PaymentListSelectionModel selectionListModel;

	private final NumberFormat numberFormatter = NumberFormat.getNumberInstance();

	private TableColumn[] tableColumns = null;

	private final UserPanel userPanel;

	public PaymentListModel(final UserPanel userPanel)
	{
		this.userPanel = userPanel;
		createTableColumns();
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (userPanel.getCurrentState().equals(UserPanel.State.PAYMENT_INPUT))
		{
			if (event.getSource() instanceof HTMLButton)
			{
				if (userPanel.getPaymentWrapper().isPaymentComplete())
				{
					final Payment newPayment = userPanel.getPaymentWrapper().getPayment();
					addPayment(newPayment);
				}
			}
		}
	}

	public int[] getColumnAlignments()
	{
		return PaymentListModel.columnAlignments;
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex)
	{
		switch (columnIndex)
		{
			case COL_PAYMENT_TYPE:
				return String.class;
			case COL_AMOUNT:
				return Double.class;
			case COL_AMOUNT_FC:
				return Double.class;
		}
		return null;
	}

	@Override
	public int getColumnCount()
	{
		return PaymentListModel.columnNames.length;
	}

	@Override
	public String getColumnName(final int colIndex)
	{
		if (colIndex < PaymentListModel.columnNames.length)
		{
			return PaymentListModel.columnNames[colIndex];
		}
		return null;
	}

	public String[] getColumnNames()
	{
		return PaymentListModel.columnNames;
	}

	public Payment getPayment(final int rowIndex)
	{
		if ((rowIndex >= 0) && (rowIndex < userPanel.getReceiptWrapper().getReceipt().getPayments().size()))
		{
			final Payment[] payments = userPanel.getReceiptWrapper().getReceipt().getPayments().toArray(new Payment[0]);
			return payments[rowIndex];
		}

		return null;
	}

	@Override
	public int getRowCount()
	{
		int rowCount = userPanel.getReceiptWrapper().getReceipt().getPayments() == null ? 0 : userPanel.getReceiptWrapper()
				.getReceipt().getPayments().size();
		return rowCount;
	}

	public PaymentListSelectionModel getSelectionListModel()
	{
		return selectionListModel;
	}

	public TableColumn[] getTableColumns()
	{
		return tableColumns;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		final Payment[] payments = userPanel.getReceiptWrapper().getReceipt().getPayments().toArray(new Payment[0]);

		java.util.Currency cur = null;

		if (rowIndex < payments.length)
		{
			switch (columnIndex)
			{
				case COL_PAYMENT_TYPE:
					return new StringBuffer(payments[rowIndex].getPaymentType().getName());
				case COL_AMOUNT_FC:
					cur = java.util.Currency.getInstance(payments[rowIndex].getPaymentType().getCurrency().getCode());
					numberFormatter.setMinimumFractionDigits(cur.getDefaultFractionDigits());
					numberFormatter.setMaximumFractionDigits(cur.getDefaultFractionDigits());
					return numberFormatter.format(payments[rowIndex].getAmount(Receipt.QuotationType.FOREIGN_CURRENCY));
				case COL_AMOUNT:
					cur = java.util.Currency
							.getInstance(payments[rowIndex].getReceipt().getDefaultCurrency().getCode());
					numberFormatter.setMinimumFractionDigits(cur.getDefaultFractionDigits());
					numberFormatter.setMaximumFractionDigits(cur.getDefaultFractionDigits());
					return numberFormatter.format(payments[rowIndex].getAmount(Receipt.QuotationType.DEFAULT_CURRENCY));
			}
		}
		return null;
	}

	public void setSelectionListModel(final PaymentListSelectionModel selectionListModel)
	{
		this.selectionListModel = selectionListModel;
	}

	private void displayPayment(Payment payment)
	{
		sendEvent(payment);
	}
	
	private Event getEvent(ServiceReference<EventAdmin> reference, final String topics, final Payment payment)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.SERVICE, reference);
		properties.put(EventConstants.SERVICE_ID, reference.getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put(IPrintable.class.getName(), payment);
		properties.put("status", Status.OK_STATUS);
		return new Event(topics, properties);
	}

	private void sendEvent(final Payment payment)
	{
		ServiceTracker<EventAdmin, EventAdmin> tracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
		try
		{
			tracker.open();
			final EventAdmin eventAdmin = (EventAdmin) tracker.getService();
			if (eventAdmin != null)
			{
				eventAdmin.sendEvent(this.getEvent(tracker.getServiceReference(), Topic.PAYMENT_ADDED.topic(), payment));
			}
		}
		finally
		{
			tracker.close();
		}
	}

	private void addPayment(final Payment newPayment)
	{
		newPayment.getReceipt().addPayment(newPayment);
		final int row = newPayment.getReceipt().getPayments().size() - 1;
		fireTableRowsInserted(row, row);
		displayPayment(newPayment);
		userPanel.getPaymentWrapper().preparePayment(newPayment.getReceipt());
	}

	private void createTableColumns()
	{
		if (tableColumns == null)
		{
			tableColumns = new TableColumn[PaymentListModel.columnNames.length];
		}

		for (int i = 0; i < getColumnNames().length; i++)
		{
			tableColumns[i] = new TableColumn(i);
			tableColumns[i].setHeaderValue(getColumnNames()[i]);
			final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
			renderer.setHorizontalAlignment(PaymentListModel.columnAlignments[i]);
			tableColumns[i].setCellRenderer(renderer);
			tableColumns[i].setResizable(true);
		}
	}
}
