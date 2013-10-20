/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.internal.resolver.UserState;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ShowCoinCounterPanelAction extends ConfigurableAction implements EventHandler
{
	private static final long serialVersionUID = 0L;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private final ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

	public ShowCoinCounterPanelAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);

		persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		persistenceServiceTracker.open();

		final Collection<String> t = new ArrayList<String>();
		t.add("ch/eugster/colibri/client/store/receipt");
		t.add("ch/eugster/colibri/persistence/server/database");
		final String[] topics = t.toArray(new String[t.size()]);
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, this, properties);
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.COIN_COUNTER));
	}

	protected boolean getState(final StateChangeEvent event)
	{
		boolean enabled = super.getState(event);
		if (event.getNewState().equals(UserPanel.State.MUST_SETTLE))
		{
			enabled = userPanel.getUser().getRole().getPropertyValue(FunctionType.FUNCTION_SHOW_COIN_COUNTER_PANEL.key());
		}
		if (enabled)
		{
			final ServiceTracker<PersistenceService, PersistenceService> serviceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
					PersistenceService.class, null);
			serviceTracker.open();
			try
			{
				final PersistenceService service = (PersistenceService) serviceTracker.getService();
				if (service.getServerService().isConnected())
				{
					enabled = countRemainingReceipts() == 0L;
				}
				else
				{
					enabled = false;
				}
			}
			finally
			{
				serviceTracker.close();
			}
		}
		return enabled;
	}

	private long countRemainingReceipts()
	{
		final PersistenceService persistenceService = (PersistenceService) persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			if (persistenceService.getServerService().isConnected())
			{
				final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
				return query.countRemainingToTransfer();
			}
		}
		return -1;
	}

	@Override
	public void handleEvent(Event event)
	{
		if (event.getTopic().equals("ch/eugster/colibri/persistence/server/database"))
		{
			Object property = event.getProperty("status");
			if (property instanceof IStatus)
			{
				IStatus status = (IStatus) property;
				if (status.getSeverity() == IStatus.ERROR)
				{
					this.setEnabled(false);
				}
				else
				{
					UserPanel.State state = getUserPanel().getCurrentState();
					if (state != null)
					{
						boolean enabled = !state.equals(UserPanel.State.LOCKED);
						this.setEnabled(enabled && countRemainingReceipts() == 0);
					}
				}
			}
		}
//
//		final UIJob uiJob = new UIJob("Aktualisiere Meldung...")
//		{
//			@Override
//			public IStatus runInUIThread(final IProgressMonitor monitor)
//			{
//				UserPanel.State state = getUserPanel().getCurrentState();
//				if (state != null)
//				{
//					boolean enabled = !state.equals(UserPanel.State.LOCKED);
//					ShowCoinCounterPanelAction.this.setEnabled(enabled && countRemainingReceipts() == 0);
//				}
//				return Status.OK_STATUS;
//			}
//		};
//		uiJob.schedule();
	}

	@Override
	protected void finalize() throws Throwable
	{
		this.eventHandlerServiceRegistration.unregister();
		persistenceServiceTracker.close();
		super.finalize();
	}

}
