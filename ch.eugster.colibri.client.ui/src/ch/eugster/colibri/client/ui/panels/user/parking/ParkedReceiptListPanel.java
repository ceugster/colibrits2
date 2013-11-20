/*
 * Created on 17.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.parking;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;

import ch.eugster.colibri.client.ui.actions.BackAction;
import ch.eugster.colibri.client.ui.actions.DownAction;
import ch.eugster.colibri.client.ui.actions.LoadParkedReceiptAction;
import ch.eugster.colibri.client.ui.actions.PrintReceiptAction;
import ch.eugster.colibri.client.ui.actions.UpAction;
import ch.eugster.colibri.client.ui.buttons.ProfileButton;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.ui.panels.ProfilePanel;

public class ParkedReceiptListPanel extends ProfilePanel implements TableModelListener, ActionListener, StateChangeListener
{
	public static final long serialVersionUID = 0l;

	private static final String TITLE = "Parkierte Belege von ";

	private final UserPanel userPanel;

	private JTable table;

	private ParkedReceiptListModel parkedReceiptListModel;

	public ParkedReceiptListPanel(final UserPanel userPanel)
	{
		super(userPanel.getProfile());
		this.userPanel = userPanel;
		init();
	}

	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand() != null)
		{
			if (event.getActionCommand().equals(UpAction.ACTION_COMMAND))
			{
				parkedReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(DownAction.ACTION_COMMAND))
			{
				parkedReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(PrintReceiptAction.ACTION_COMMAND))
			{
				parkedReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(LoadParkedReceiptAction.ACTION_COMMAND))
			{
				parkedReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(BackAction.ACTION_COMMAND))
			{
				parkedReceiptListModel.actionPerformed(event);
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

	public ParkedReceiptListModel getModel()
	{
		return parkedReceiptListModel;
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

		final JLabel label = new JLabel(ParkedReceiptListPanel.TITLE + userPanel.getUser().getUsername());
		label.setFont(label.getFont().deriveFont(profile.getTabbedPaneFontStyle(), profile.getTabbedPaneFontSize()));
		this.add(label, BorderLayout.NORTH);

		parkedReceiptListModel = new ParkedReceiptListModel(userPanel);
		parkedReceiptListModel.addTableModelListener(this);

		final ParkedReceiptListSelectionModel selectionListModel = new ParkedReceiptListSelectionModel(parkedReceiptListModel);
		parkedReceiptListModel.setSelectionListModel(selectionListModel);

		final DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		for (final TableColumn tableColumn : parkedReceiptListModel.getTableColumns())
		{
			columnModel.addColumn(tableColumn);
		}

		table = new JTable(parkedReceiptListModel, columnModel);
		table.setColumnSelectionAllowed(false);
		table.setFillsViewportHeight(true);
		table.setFocusable(false);
		table.setRowSelectionAllowed(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		table.setSelectionModel(selectionListModel);
		this.update();

		final JScrollPane scrollPane = new JScrollPane(table);
		this.add(scrollPane, BorderLayout.CENTER);

		/*
		 * Buttons
		 */
		final JPanel buttonPanel = new JPanel(new GridLayout(1, 5));

		boolean failOver = userPanel.getMainTabbedPane().isFailOver();
		final UpAction upAction = new UpAction(userPanel, profile, parkedReceiptListModel, selectionListModel);
		table.getSelectionModel().addListSelectionListener(upAction);
		table.getModel().addTableModelListener(upAction);
		ProfileButton button = new ProfileButton(upAction, profile, failOver);
		button.addActionListener(this);
		buttonPanel.add(button);

		final DownAction downAction = new DownAction(userPanel, profile, parkedReceiptListModel, selectionListModel);
		table.getModel().addTableModelListener(downAction);
		table.getSelectionModel().addListSelectionListener(downAction);
		button = new ProfileButton(downAction, profile, failOver);
		button.addActionListener(this);
		buttonPanel.add(button);

		final DeleteParkedReceiptAction deleteParkedReceiptAction = new DeleteParkedReceiptAction(userPanel, profile, parkedReceiptListModel,
				selectionListModel);
		table.getModel().addTableModelListener(deleteParkedReceiptAction);
		table.getSelectionModel().addListSelectionListener(deleteParkedReceiptAction);
		button = new ProfileButton(deleteParkedReceiptAction, profile, failOver);
		buttonPanel.add(button);

		final LoadParkedReceiptAction loadParkedReceiptAction = new LoadParkedReceiptAction(userPanel, profile, parkedReceiptListModel,
				selectionListModel);
		table.getModel().addTableModelListener(loadParkedReceiptAction);
		table.getSelectionModel().addListSelectionListener(loadParkedReceiptAction);
		button = new ProfileButton(loadParkedReceiptAction, profile, failOver);
		buttonPanel.add(button);

		final BackAction backAction = new BackAction(profile);
		button = new ProfileButton(backAction, profile, failOver);
		button.addActionListener(userPanel);
		buttonPanel.add(button);

		this.add(buttonPanel, BorderLayout.SOUTH);

	}

	private void resizeColumns()
	{
//		final FontMetrics fm = table.getFontMetrics(table.getFont());
//
//		for (int i = 1; i < table.getColumnCount(); i++)
//		{
//			final String title = (String) table.getColumnModel().getColumn(i).getHeaderValue();
//			int stringWidth = fm.stringWidth(title);
//			for (int j = 0; j < table.getRowCount(); j++)
//			{
//				final String val = table.getValueAt(j, i).toString();
//				if (fm.stringWidth(val) + 6 > stringWidth)
//				{
//					stringWidth = fm.stringWidth(val) + 6;
//				}
//			}
//
//			final TableColumn tableColumn = table.getColumnModel().getColumn(i);
//			tableColumn.setMinWidth(stringWidth);
//			tableColumn.setMaxWidth(stringWidth);
//			tableColumn.setPreferredWidth(stringWidth);
//
//			if ((i > 0) && (i < ((ParkedReceiptListModel) table.getModel()).getColumnNames().length - 1))
//			{
//				((JLabel) tableColumn.getCellRenderer()).setHorizontalAlignment(SwingConstants.RIGHT);
//			}
//			else
//			{
//				((JLabel) tableColumn.getCellRenderer()).setHorizontalAlignment(SwingConstants.LEFT);
//			}
//		}
//		table.getColumnModel().getColumn(0).setMinWidth(30);
//		table.getColumnModel().getColumn(0).setMaxWidth(600);
//		table.doLayout();
		int[] alignments = parkedReceiptListModel.getColumnAlignments();
		for (int i = 0; i < alignments.length; i++)
		{
			final TableColumn tableColumn = table.getColumnModel().getColumn(i);
			((JLabel) tableColumn.getCellRenderer()).setHorizontalAlignment(alignments[i]);
		}
	}
}
