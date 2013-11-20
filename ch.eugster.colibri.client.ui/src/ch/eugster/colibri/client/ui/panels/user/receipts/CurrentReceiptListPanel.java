/*
 * Created on 17.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.receipts;

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
import ch.eugster.colibri.client.ui.actions.LoadReceiptAction;
import ch.eugster.colibri.client.ui.actions.PrintReceiptAction;
import ch.eugster.colibri.client.ui.actions.ReverseReceiptAction;
import ch.eugster.colibri.client.ui.actions.UpAction;
import ch.eugster.colibri.client.ui.buttons.ProfileButton;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.events.StateChangeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.ui.panels.ProfilePanel;

public class CurrentReceiptListPanel extends ProfilePanel implements TableModelListener, ActionListener,
		StateChangeListener
{
	public static final long serialVersionUID = 0l;

	private static final String TITLE = "Erfasste Belege dieser Kasse";

	private ProfileButton upButton;

	private ProfileButton downButton;

	private ProfileButton printButton;

	private ProfileButton reverseButton;

	// private ProfileButton loadButton;

	private ProfileButton backButton;

	private final UserPanel userPanel;

	private JTable table;

	private CurrentReceiptListModel currentReceiptListModel;

	public CurrentReceiptListPanel(final UserPanel userPanel, final Profile profile)
	{
		super(profile);
		this.userPanel = userPanel;
		init();
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (event.getActionCommand() != null)
		{
			if (event.getActionCommand().equals(UpAction.ACTION_COMMAND))
			{
				currentReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(DownAction.ACTION_COMMAND))
			{
				currentReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(PrintReceiptAction.ACTION_COMMAND))
			{
				currentReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(ReverseReceiptAction.ACTION_COMMAND))
			{
				currentReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(LoadReceiptAction.ACTION_COMMAND))
			{
				currentReceiptListModel.actionPerformed(event);
			}
			else if (event.getActionCommand().equals(BackAction.ACTION_COMMAND))
			{
				currentReceiptListModel.actionPerformed(event);
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

	public CurrentReceiptListModel getModel()
	{
		return currentReceiptListModel;
	}

	public void removeListSelectionListener(final ListSelectionListener listener)
	{
		if (listener != null)
		{
			table.getSelectionModel().addListSelectionListener(listener);
		}
	}

	@Override
	public void stateChange(final StateChangeEvent event)
	{
		table.setEnabled(!event.getNewState().equals(UserPanel.State.LOCKED));
	}

	@Override
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

		final JPanel northPanel = new JPanel();
		northPanel.setLayout(new BorderLayout());

		final JLabel label = new JLabel(CurrentReceiptListPanel.TITLE);
		label.setFont(label.getFont().deriveFont(profile.getTabbedPaneFontStyle(), profile.getTabbedPaneFontSize()));
		northPanel.add(label, BorderLayout.NORTH);

		this.add(northPanel, BorderLayout.NORTH);

		currentReceiptListModel = new CurrentReceiptListModel(userPanel);
		currentReceiptListModel.addTableModelListener(this);

		final CurrentReceiptListSelectionModel selectionListModel = new CurrentReceiptListSelectionModel(
				currentReceiptListModel);
		currentReceiptListModel.setSelectionListModel(selectionListModel);

		final DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
		for (final TableColumn tableColumn : currentReceiptListModel.getTableColumns())
		{
			columnModel.addColumn(tableColumn);
		}

		table = new JTable(currentReceiptListModel, columnModel);
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

		/*
		 * Buttons
		 */
		boolean failOver = userPanel.getMainTabbedPane().isFailOver();
		final UpAction upAction = new UpAction(userPanel, profile, table.getModel(), table.getSelectionModel());
		upButton = new ProfileButton(upAction, profile, failOver);
		upButton.addActionListener(this);
		table.getSelectionModel().addListSelectionListener(upAction);
		table.getModel().addTableModelListener(upAction);

		final DownAction downAction = new DownAction(userPanel, profile, table.getModel(), table.getSelectionModel());
		downButton = new ProfileButton(downAction, profile, failOver);
		downButton.addActionListener(this);
		table.getModel().addTableModelListener(downAction);
		table.getSelectionModel().addListSelectionListener(downAction);

		final PrintReceiptAction printAction = new PrintReceiptAction(userPanel, profile,
				(CurrentReceiptListModel) table.getModel(),
				(CurrentReceiptListSelectionModel) table.getSelectionModel());
		printButton = new ProfileButton(printAction, profile, failOver);
		table.getModel().addTableModelListener(printAction);
		table.getSelectionModel().addListSelectionListener(printAction);

		final ReverseReceiptAction reverseAction = new ReverseReceiptAction(userPanel, profile,
				(CurrentReceiptListModel) table.getModel(),
				(CurrentReceiptListSelectionModel) table.getSelectionModel());
		reverseAction.addPrintReceiptAction(printAction);
		reverseButton = new ProfileButton(reverseAction, profile, failOver);
		table.getSelectionModel().addListSelectionListener(reverseAction);
		table.getModel().addTableModelListener(reverseAction);

		// final LoadReceiptAction loadAction = new LoadReceiptAction(userPanel,
		// profile, currentReceiptListModel, selectionListModel);
		// loadButton = new ProfileButton(loadAction, profile);
		// table.getSelectionModel().addListSelectionListener(loadAction);
		// table.getModel().addTableModelListener(loadAction);

		final BackAction backAction = new BackAction(profile);
		backButton = new ProfileButton(backAction, profile, failOver);
		backButton.addActionListener(userPanel);

		final JPanel buttonPanel = new JPanel(new GridLayout(1, 5));
		buttonPanel.add(upButton);
		buttonPanel.add(downButton);
		buttonPanel.add(printButton);
		buttonPanel.add(reverseButton);
		// buttonPanel.add(loadButton);
		buttonPanel.add(backButton);

		this.add(buttonPanel, BorderLayout.SOUTH);

	}

	private void resizeColumns()
	{
//		final FontMetrics fm = table.getFontMetrics(table.getFont());

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
//			
//			if ((i > 0) && (i < ((CurrentReceiptListModel) table.getModel()).getColumnNames().length - 1))
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
		int[] alignments = currentReceiptListModel.getColumnAlignments();
		for (int i = 0; i < alignments.length; i++)
		{
			final TableColumn tableColumn = table.getColumnModel().getColumn(i);
			((JLabel) tableColumn.getCellRenderer()).setHorizontalAlignment(alignments[i]);
		}
//		table.doLayout();
	}
}
