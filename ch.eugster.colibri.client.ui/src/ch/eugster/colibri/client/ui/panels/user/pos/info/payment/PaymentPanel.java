/*
 * Created on 06.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.payment;

import java.awt.BorderLayout;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import ch.eugster.colibri.client.ui.actions.ClearAction;
import ch.eugster.colibri.client.ui.actions.DeleteAction;
import ch.eugster.colibri.client.ui.actions.DownAction;
import ch.eugster.colibri.client.ui.actions.EnterAction;
import ch.eugster.colibri.client.ui.actions.UpAction;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.panels.ProfilePanel;

public class PaymentPanel extends ProfilePanel implements TableModelListener, ActionListener, StateChangeListener
{
	public static final long serialVersionUID = 0l;

	private final UserPanel userPanel;

	private JTable table;

	private PaymentListModel paymentListModel;

	public PaymentPanel(final UserPanel userPanel, final Profile profile)
	{
		super(profile);
		this.userPanel = userPanel;
		this.userPanel.setPaymentPanel(this);
		init();
	}

	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand() != null)
		{
			if (userPanel.getCurrentState().equals(UserPanel.State.PAYMENT_INPUT))
			{
				if (event.getActionCommand().equals(EnterAction.ACTION_COMMAND))
				{
					paymentListModel.actionPerformed(event);
				}
				else if (event.getActionCommand().equals(ClearAction.ACTION_COMMAND))
				{
					if (table.getSelectedRow() > -1)
					{
						table.clearSelection();
					}
				}
				else if (event.getActionCommand().equals(DeleteAction.ACTION_COMMAND))
				{
					if (userPanel.getValueDisplay().getText().isEmpty())
					{
						if (table.getRowCount() > 0)
						{
							if (table.getSelectedRow() > -1)
							{
								final PaymentListModel model = (PaymentListModel) table.getModel();
								final Payment payment = model.getPayment(table.getSelectedRow());
								if (payment.getId() == null)
								{
									userPanel.getReceiptWrapper().getReceipt().removePayment(payment);
								}
								else
								{
									payment.setDeleted(true);
									userPanel.getReceiptWrapper().storeReceipt();
								}

								model.fireTableRowsDeleted(table.getSelectedRow(), table.getSelectedRow());

							}
							else
							{
								//Wurde von Adriano gewünscht 2012-05-15
//								final String title = "Alle Zahlungen löschen";
//								final String message = "Sollen alle vorhandenen Zahlungen gelöscht werden?";
//								if (ch.eugster.colibri.client.ui.dialogs.MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(),
//										PaymentPanel.this.profile, title, message, MessageDialog.TYPE_QUESTION) == ch.eugster.colibri.client.ui.dialogs.MessageDialog.BUTTON_YES)
//								{
									final PaymentListModel model = (PaymentListModel) table.getModel();
									userPanel.getReceiptWrapper().clearPayments();
									if (userPanel.getReceiptWrapper().getReceipt().getId() != null)
									{
										userPanel.getReceiptWrapper().getReceipt().setDeleted(true);
										userPanel.getReceiptWrapper().storeReceipt();
									}
									model.fireTableDataChanged();
//								}
							}
						}
					}
				}
				else if (event.getActionCommand().equals(UpAction.ACTION_COMMAND))
				{
					int selectionIndex = table.getSelectedRow();
					if (selectionIndex > 0)
					{
						--selectionIndex;
					}
					table.changeSelection(selectionIndex, 0, false, false);
				}
				else if (event.getActionCommand().equals(DownAction.ACTION_COMMAND))
				{
					int selectionIndex = table.getSelectedRow();
					if (selectionIndex < table.getRowCount() - 1)
					{
						++selectionIndex;
					}
					table.changeSelection(selectionIndex, 0, false, false);
				}
			}
		}
	}

	public void addListSelectionListener(final ListSelectionListener listener)
	{
		if (listener != null)
		{
			table.getSelectionModel().addListSelectionListener(listener);
		}
	}

	public PaymentListModel getModel()
	{
		return paymentListModel;
	}

	public void removeListSelectionListener(final ListSelectionListener listener)
	{
		if (listener != null)
		{
			table.getSelectionModel().addListSelectionListener(listener);
		}
	}

	public void stateChange(final StateChangeEvent event)
	{
		table.setEnabled(!event.getNewState().equals(UserPanel.State.LOCKED));
	}

	public void tableChanged(final TableModelEvent event)
	{
		resizeColumns();
	}

	@Override
	protected void update()
	{
		table.setFont(table.getFont().deriveFont(profile.getListFontStyle(), profile.getListFontSize()));
		table.setForeground(new java.awt.Color(profile.getListFg()));
		table.setBackground(new java.awt.Color(profile.getListBg()));
	}

	private void init()
	{
		setLayout(new BorderLayout());

		paymentListModel = new PaymentListModel(userPanel);
		paymentListModel.addTableModelListener(this);

		final PaymentListSelectionModel selectionListModel = new PaymentListSelectionModel(paymentListModel);
		paymentListModel.setSelectionListModel(selectionListModel);

		final DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		for (final TableColumn tableColumn : paymentListModel.getTableColumns())
		{
			columnModel.addColumn(tableColumn);
		}

		table = new JTable(paymentListModel, columnModel);
		table.setColumnSelectionAllowed(false);
		table.setFillsViewportHeight(true);
		table.setFocusable(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setSelectionModel(selectionListModel);
		update();

		final JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.CENTER);
	}

	private void resizeColumns()
	{
		final FontMetrics fm = table.getFontMetrics(table.getFont());

		for (int i = 1; i < table.getColumnCount(); i++)
		{
			final String title = (String) table.getColumnModel().getColumn(i).getHeaderValue();
			int stringWidth = fm.stringWidth(title);
			for (int j = 0; j < table.getRowCount(); j++)
			{
				final String val = table.getValueAt(j, i).toString();
				if (fm.stringWidth(val) + 6 > stringWidth)
				{
					stringWidth = fm.stringWidth(val) + 6;
				}
			}

			final TableColumn tableColumn = table.getColumnModel().getColumn(i);
			tableColumn.setMinWidth(stringWidth);
			tableColumn.setMaxWidth(stringWidth);
			tableColumn.setPreferredWidth(stringWidth);

			// if (i > 0 && i < ((PaymentListModel)
			// this.table.getModel()).getColumnNames().length - 1)
			((DefaultTableCellRenderer) tableColumn.getCellRenderer()).setHorizontalAlignment(PaymentListModel.columnAlignments[i]);
			// else
			// ((JLabel)
			// tableColumn.getCellRenderer()).setHorizontalAlignment(JLabel.LEFT);
		}
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(0).setMaxWidth(600);
		table.doLayout();
	}
}
