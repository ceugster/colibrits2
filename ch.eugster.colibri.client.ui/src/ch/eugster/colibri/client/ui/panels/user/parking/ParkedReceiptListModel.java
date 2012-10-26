/*
 * Created on 17.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.parking;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Calendar;

import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.BackAction;
import ch.eugster.colibri.client.ui.actions.DownAction;
import ch.eugster.colibri.client.ui.actions.PrintReceiptAction;
import ch.eugster.colibri.client.ui.actions.ReverseReceiptAction;
import ch.eugster.colibri.client.ui.actions.UpAction;
import ch.eugster.colibri.client.ui.buttons.ProfileButton;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ParkedReceiptListModel extends AbstractTableModel implements ActionListener, DisposeListener
{
	public static final long serialVersionUID = 0l;

	private static String[] columnNames = new String[] { "Nummer", "Datum", "Zeit", "Betrag", "Status" };

	private static int[] columnAlignments = new int[] { SwingConstants.LEFT, SwingConstants.CENTER,
			SwingConstants.CENTER, SwingConstants.RIGHT, SwingConstants.LEFT };

	public static final int COL_NUMBER = 0;

	public static final int COL_DATE = 1;

	public static final int COL_TIME = 2;

	public static final int COL_AMOUNT = 3;

	public static final int COL_STATE = 4;

	private ParkedReceiptListSelectionModel selectionListModel;

	private NumberFormat numberFormatter = NumberFormat.getNumberInstance();

	private Calendar calendar = Calendar.getInstance();

	private final DateFormat dateFormatter = DateFormat.getDateInstance();

	private final DateFormat timeFormatter = DateFormat.getTimeInstance();

	private TableColumn[] tableColumns = null;

	private Receipt[] receipts;

	private final UserPanel userPanel;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ParkedReceiptListModel(final UserPanel userPanel)
	{
		this.userPanel = userPanel;
		this.userPanel.addDisposeListener(this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class.getName(), null);
		this.persistenceServiceTracker.open();

		this.createTableColumns();
		this.numberFormatter = NumberFormat.getNumberInstance();
		this.loadReceipts();

	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (event.getSource() instanceof ProfileButton)
		{
			if (event.getActionCommand().equals(UpAction.ACTION_COMMAND))
			{
				int currentIndex = this.selectionListModel.getMinSelectionIndex();
				if (currentIndex > 0)
				{
					currentIndex--;
					this.selectionListModel.setSelectionInterval(currentIndex, currentIndex);
				}
			}
			else if (event.getActionCommand().equals(DownAction.ACTION_COMMAND))
			{
				int currentIndex = this.selectionListModel.getMinSelectionIndex();
				if (currentIndex < this.getRowCount() - 1)
				{
					currentIndex++;
					this.selectionListModel.setSelectionInterval(currentIndex, currentIndex);
				}
			}
			else if (event.getActionCommand().equals(PrintReceiptAction.ACTION_COMMAND))
			{
				if (this.selectionListModel.getMinSelectionIndex() == -1)
				{
					this.fireTableDataChanged();
				}
			}
			else if (event.getActionCommand().equals(ReverseReceiptAction.ACTION_COMMAND))
			{
				if (this.selectionListModel.getMinSelectionIndex() == -1)
				{
					this.fireTableDataChanged();
				}
			}
			else if (event.getActionCommand().equals(BackAction.ACTION_COMMAND))
			{
				if (this.selectionListModel.getMinSelectionIndex() == -1)
				{
					this.fireTableDataChanged();
				}
			}
		}
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	public int[] getColumnAlignments()
	{
		return ParkedReceiptListModel.columnAlignments;
	}

	@Override
	public Class<?> getColumnClass(final int columnIndex)
	{
		switch (columnIndex)
		{
			case COL_NUMBER:
				return String.class;
			case COL_DATE:
				return String.class;
			case COL_TIME:
				return String.class;
			case COL_AMOUNT:
				return String.class;
			case COL_STATE:
				return String.class;
		}
		return null;
	}

	@Override
	public int getColumnCount()
	{
		return ParkedReceiptListModel.columnNames.length;
	}

	@Override
	public String getColumnName(final int colIndex)
	{
		if (colIndex < ParkedReceiptListModel.columnNames.length)
		{
			return ParkedReceiptListModel.columnNames[colIndex];
		}
		return null;
	}

	public String[] getColumnNames()
	{
		return ParkedReceiptListModel.columnNames;
	}

	public Receipt getReceipt(final int rowIndex)
	{
		return this.receipts[rowIndex];
	}

	@Override
	public int getRowCount()
	{
		return this.receipts.length;
	}

	public ParkedReceiptListSelectionModel getSelectionListModel()
	{
		return this.selectionListModel;
	}

	public TableColumn[] getTableColumns()
	{
		return this.tableColumns;
	}

	@Override
	public Object getValueAt(final int rowIndex, final int columnIndex)
	{
		if (rowIndex < this.receipts.length)
		{
			switch (columnIndex)
			{
				case ParkedReceiptListModel.COL_NUMBER:
					return this.receipts[rowIndex].getNumber().toString();
				case COL_DATE:
					this.calendar = this.receipts[rowIndex].getTimestamp();
					return this.dateFormatter.format(this.calendar.getTime());
				case COL_TIME:
					this.calendar = this.receipts[rowIndex].getTimestamp();
					return this.timeFormatter.format(this.calendar.getTime());
				case COL_AMOUNT:
					final java.util.Currency cur = java.util.Currency.getInstance(this.receipts[rowIndex]
							.getDefaultCurrency().getCode());
					this.numberFormatter.setMinimumFractionDigits(cur.getDefaultFractionDigits());
					this.numberFormatter.setMaximumFractionDigits(cur.getDefaultFractionDigits());
					return this.numberFormatter.format(this.receipts[rowIndex]
							.getPositionDefaultCurrencyAmount(AmountType.NETTO));
				case COL_STATE:
					return this.receipts[rowIndex].getState().toString();
			}
		}
		return null;
	}

	public void loadReceipts()
	{
		this.setReceipts(this.getParkedReceipts(this.userPanel.getUser()));
	}

	public void setSelectionListModel(final ParkedReceiptListSelectionModel selectionListModel)
	{
		this.selectionListModel = selectionListModel;
	}

	public void update()
	{
		this.setReceipts(this.getParkedReceipts(this.userPanel.getUser()));
	}

	private void createTableColumns()
	{
		if (this.tableColumns == null)
		{
			this.tableColumns = new TableColumn[ParkedReceiptListModel.columnNames.length];
		}

		for (int i = 0; i < this.getColumnNames().length; i++)
		{
			this.tableColumns[i] = new TableColumn();
			this.tableColumns[i] = new TableColumn();
			this.tableColumns[i].setHeaderValue(this.getColumnNames()[i]);
			this.tableColumns[i].setModelIndex(i);
			this.tableColumns[i].setCellRenderer(new DefaultTableCellRenderer());
			this.tableColumns[i].setResizable(true);
		}
	}

	private Receipt[] getParkedReceipts(final User user)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
			return query.selectParked(user).toArray(new Receipt[0]);
		}
		return new Receipt[0];
	}

	private void setReceipts(final Receipt[] receipts)
	{
		this.receipts = receipts;
		this.fireTableDataChanged();
		if (receipts.length > 0)
		{
			if (this.getSelectionListModel() != null)
			{
				this.getSelectionListModel().setSelectionInterval(0, 0);
			}
		}
	}
}
