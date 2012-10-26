/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;

public class UpAction extends UserPanelProfileAction implements TableModelListener, ListSelectionListener
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Auf";

	public static final String ACTION_COMMAND = "up.action";

	private TableModel tableModel;

	private ListSelectionModel selectionModel;

	public UpAction(final UserPanel userPanel, final Profile profile, final TableModel tableModel, final ListSelectionModel selectionModel)
	{
		super(UpAction.TEXT, UpAction.ACTION_COMMAND, userPanel, profile);
		this.tableModel = tableModel;
		this.selectionModel = selectionModel;
	}

	@Override
	public boolean getState(final StateChangeEvent event)
	{
		return shouldEnable();
	}

	@Override
	public void tableChanged(final TableModelEvent event)
	{
		setEnabled(shouldEnable());
	}

	@Override
	public void valueChanged(final ListSelectionEvent event)
	{
		setEnabled(shouldEnable());
	}

	private boolean shouldEnable()
	{
		if (tableModel.getRowCount() == 0)
		{
			return false;
		}
		else
		{
			final int selection = selectionModel.getMinSelectionIndex();
			return selection > 0;
		}
	}
}
