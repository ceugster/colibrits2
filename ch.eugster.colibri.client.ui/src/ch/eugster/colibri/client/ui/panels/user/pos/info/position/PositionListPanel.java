/*
 * Created on 17.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.pos.info.position;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Frame;
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

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.ClearAction;
import ch.eugster.colibri.client.ui.actions.DeleteAction;
import ch.eugster.colibri.client.ui.actions.DiscountAction;
import ch.eugster.colibri.client.ui.actions.DownAction;
import ch.eugster.colibri.client.ui.actions.EnterAction;
import ch.eugster.colibri.client.ui.actions.UpAction;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.pos.info.position.PositionListModel.Column;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.panels.ProfilePanel;

public class PositionListPanel extends ProfilePanel implements TableModelListener, ActionListener, StateChangeListener
{
	public static final long serialVersionUID = 0l;

	private final UserPanel userPanel;

	private JTable table;

	private PositionListModel positionListModel;

	public PositionListPanel(final UserPanel userPanel, final Profile profile)
	{
		super(profile);
		this.userPanel = userPanel;
		init();
	}

	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand() != null)
		{
			if (userPanel.getCurrentState().equals(UserPanel.State.POSITION_INPUT))
			{
				if (event.getActionCommand().equals(EnterAction.ACTION_COMMAND))
				{
					positionListModel.actionPerformed(event);
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
							final PositionListModel model = (PositionListModel) table.getModel();
							if (table.getSelectedRow() > -1)
							{
								final Position position = model.getPosition(table.convertRowIndexToModel(table.getSelectedRow()));
								userPanel.getReceiptWrapper().getReceipt().removePosition(position);
								model.fireTableDataChanged();

							}
							else
							{
								final String title = "Beleg verwerfen";
								final String message = "Soll der aktuelle Beleg verworfen werden?";
								Frame frame = Activator.getDefault().getFrame();
								Profile profile = PositionListPanel.this.userPanel.getProfile();
								if (ch.eugster.colibri.client.ui.dialogs.MessageDialog.showSimpleDialog(frame,
										profile, title, message, MessageDialog.TYPE_QUESTION, this.userPanel.getMainTabbedPane().isFailOver()) == ch.eugster.colibri.client.ui.dialogs.MessageDialog.BUTTON_YES)
								{
									if (userPanel.getReceiptWrapper().getReceipt().getId() != null)
									{
										userPanel.getReceiptWrapper().getReceipt().setDeleted(true);
										userPanel.getReceiptWrapper().storeReceipt(true);
									}
									userPanel.prepareReceipt();
									model.fireTableDataChanged();
								}
							}
						}
					}
				}
				else if (event.getActionCommand().equals(DiscountAction.ACTION_COMMAND))
				{
					if (table.getSelectedRow() == -1)
					{
						positionListModel.actionPerformed(event);
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

	public PositionListModel getModel()
	{
		return positionListModel;
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
		resizeColumns();
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

		positionListModel = new PositionListModel(userPanel);
		positionListModel.addTableModelListener(this);

		final PositionListSelectionModel selectionListModel = new PositionListSelectionModel(positionListModel);
		positionListModel.setSelectionListModel(selectionListModel);

		final DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		for (final TableColumn tableColumn : positionListModel.getTableColumns())
		{
			tableColumn.setCellRenderer(new PositionListCellRenderer(userPanel, Color.BLACK, Color.RED, Color.RED));
			columnModel.addColumn(tableColumn);
		}

		table = new JTable(positionListModel, columnModel);
		table.setColumnSelectionAllowed(false);
		table.setFillsViewportHeight(true);
		table.setFocusable(false);
		table.setRowSelectionAllowed(true);
//		table.setRowSorter(new PositionListRowSorter(positionListModel));
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
				table.getColumnModel().getColumn(i).getCellRenderer().getTableCellRendererComponent(table, table.getValueAt(j, i), false, false, j, i);
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

			((DefaultTableCellRenderer) tableColumn.getCellRenderer()).setHorizontalAlignment(Column.values()[i].align());
		}
		table.getColumnModel().getColumn(0).setMinWidth(30);
		table.getColumnModel().getColumn(0).setMaxWidth(600);
		table.doLayout();
	}
}
