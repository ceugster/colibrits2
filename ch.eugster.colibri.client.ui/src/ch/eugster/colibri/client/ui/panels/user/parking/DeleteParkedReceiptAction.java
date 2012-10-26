/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels.user.parking;

import java.awt.event.ActionEvent;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.actions.UserPanelProfileAction;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class DeleteParkedReceiptAction extends UserPanelProfileAction implements ListSelectionListener, TableModelListener, DisposeListener
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Entfernen";

	public static final String ACTION_COMMAND = "delete.parked.receipt.action";

	private ParkedReceiptListModel tableModel;

	private ParkedReceiptListSelectionModel selectionModel;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public DeleteParkedReceiptAction(final UserPanel userPanel, final Profile profile, final ParkedReceiptListModel tableModel,
			final ParkedReceiptListSelectionModel selectionModel)
	{
		super(DeleteParkedReceiptAction.TEXT, DeleteParkedReceiptAction.ACTION_COMMAND, userPanel, profile);
		this.tableModel = tableModel;
		this.selectionModel = selectionModel;
		userPanel.addDisposeListener(this);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			persistenceService.getCacheService().delete(this.selectionModel.getSelectedReceipt());
			this.tableModel.update();
		}
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
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

	private boolean shouldEnable()
	{
		if (this.tableModel.getRowCount() == 0)
		{
			return false;
		}
		else
		{
			return this.selectionModel.getMinSelectionIndex() > -1;
		}
	}
}
