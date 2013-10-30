/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.settlement.CoinCounterPanel;
import ch.eugster.colibri.persistence.events.EventTopic;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementDetail;
import ch.eugster.colibri.persistence.model.SettlementMoney;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.SettlementService;
import ch.eugster.colibri.ui.actions.ProfileAction;

public class SettleAction extends ProfileAction implements EventHandler
{
	@Override
	protected void finalize() throws Throwable 
	{
		if (this.eventHandlerServiceRegistration != null)
		{
			this.eventHandlerServiceRegistration.unregister();
		}
		super.finalize();
	}

	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Abschluss";

	public static final String ACTION_COMMAND = "settle.action";

	private final CoinCounterPanel coinCounterPanel;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;
	
	public SettleAction(final CoinCounterPanel coinCounterPanel)
	{
		super(SettleAction.TEXT, SettleAction.ACTION_COMMAND, coinCounterPanel.getProfile());
		this.coinCounterPanel = coinCounterPanel;
		final Collection<String> t = new ArrayList<String>();
		t.add(EventTopic.SERVER.topic());
		final String[] topics = t.toArray(new String[t.size()]);
		this.setEnabled(checkReceipts(null));
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, this, properties);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		ServiceTracker<SettlementService, SettlementService> tracker = null;
		try
		{
			tracker = new ServiceTracker<SettlementService, SettlementService>(Activator.getDefault().getBundle().getBundleContext(),
					SettlementService.class, null);
			tracker.open();
			SettlementService service = (SettlementService) tracker.getService();
			if (service != null)
			{
				Settlement settlement = this.coinCounterPanel.getUserPanel().getSalespoint().getSettlement();
				settlement.setDetails(this.getSettlementDetails(settlement));
				settlement.setMoneys(this.getSettlementMoney(settlement));
				settlement.setReceiptCount(service.countReceipts(settlement));
				settlement.setReversedReceipts(service.getReversedReceipts(settlement));

				if (settlement.getReceiptCount() == 0L && settlement.getMoneys().size() == 0 && settlement.getReversedReceipts().size() == 0)
				{
					MessageDialog.showInformation(Activator.getDefault().getFrame(), this.profile,
							"Keine Vorgänge", "Es stehen keine Vorgänge zur Durchführung an.",
							MessageDialog.TYPE_INFORMATION);
					return;
				}
				if (this.coinCounterPanel.getUserPanel().getSalespoint().isForceCashCheck())
				{
					if (settlement.getReceiptCount() > 0L && this.coinCounterPanel.getCountMoneySum() == 0D)
					{
						MessageDialog.showInformation(Activator.getDefault().getFrame(), this.profile,
								"Kassensturz", "Vor dem Abschluss müssen Sie den Kassensturz vornehmen.",
								MessageDialog.TYPE_INFORMATION);
						return;
					}
				}

				SettlementService.State state = SettlementService.State.DEFINITIVE;
				if (coinCounterPanel.getUserPanel().getSalespoint().getCommonSettings().isAllowTestSettlement())
				{
					int result = MessageDialog.showQuestion(Activator.getDefault().getFrame(), this.profile,
							"Provisorischer Abschluss", "Wollen Sie einen provisorischen Abschluss vornehmen?",
							MessageDialog.TYPE_QUESTION, new int[] { MessageDialog.BUTTON_NO, MessageDialog.BUTTON_YES,
									MessageDialog.BUTTON_CANCEL }, MessageDialog.BUTTON_CANCEL);
					if (result == MessageDialog.BUTTON_CANCEL)
					{
						return;
					}
					else
					{
						state = result == MessageDialog.BUTTON_NO ? SettlementService.State.DEFINITIVE
								: SettlementService.State.PROVISIONAL;
					}
				}
				settlement = service.settle(settlement, state);
				if (state.equals(SettlementService.State.DEFINITIVE))
				{
					Salespoint salespoint = service.updateSettlement(settlement.getSalespoint());
					this.coinCounterPanel.getUserPanel().setSalespoint(salespoint);
//						this.coinCounterPanel.getUserPanel().getReceiptWrapper().prepareReceipt();
					this.coinCounterPanel.clear();
					this.coinCounterPanel.getUserPanel().fireStateChange(
							new StateChangeEvent(coinCounterPanel.getUserPanel().getCurrentState(),
									UserPanel.State.POSITION_INPUT));
				}
				printSettlement(settlement);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			MessageDialog.showInformation(Activator.getDefault().getFrame(), coinCounterPanel.getProfile(), "Fehler", "Beim Speichern des Tagesabschlusses ist ein Fehler aufgetreten.", MessageDialog.TYPE_ERROR);
		}
		finally
		{
			if (tracker != null)
			{
				tracker.close();
			}
		}
	}

	private void printSettlement(Settlement settlement)
	{
		final ServiceTracker<EventAdmin, EventAdmin> tracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		tracker.open();
		final EventAdmin eventAdmin = (EventAdmin) tracker.getService();
		if (eventAdmin != null)
		{
			eventAdmin.sendEvent(this.getEvent(tracker, "ch/eugster/colibri/client/print/settlement", settlement));
		}
		tracker.close();
	}

	private Event getEvent(final ServiceTracker<EventAdmin, EventAdmin> tracker, final String topics, final Settlement settlement)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext());
		properties.put(EventConstants.BUNDLE_ID, Long.valueOf(Activator.getDefault().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.SERVICE, tracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID, tracker.getServiceReference().getProperty("component.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put(IPrintable.class.getName(), settlement);
		properties.put("force", true);
		properties.put("status", Status.OK_STATUS);
		return new Event(topics, properties);
	}

	private List<SettlementDetail> getSettlementDetails(final Settlement settlement)
	{
		return this.coinCounterPanel.getSettlementDetails(settlement);
	}

	private List<SettlementMoney> getSettlementMoney(final Settlement settlement)
	{
		return this.coinCounterPanel.getSettlementMoney(settlement);
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(EventTopic.SERVER.topic()))
		{
			final IStatus status = (IStatus) event.getProperty("status");
			UIJob job = new UIJob("Aktualisieren")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					setEnabled(checkReceipts(status));
					return Status.OK_STATUS;
				}
			};
			job.schedule();
		}
	}

	private boolean checkReceipts(IStatus status)
	{
		boolean result = false;
		ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(), PersistenceService.class, null);
		tracker.open();
		try
		{
			PersistenceService service = tracker.getService();
			if (service != null)
			{
				if (service.getServerService().isLocal())
				{
					result = true;
				}
				final ReceiptQuery query = (ReceiptQuery) service.getCacheService().getQuery(Receipt.class);
				final long count = query.countRemainingToTransfer();
				if ((status == null) || (status.getSeverity() == IStatus.OK))
				{
					result = count == 0;
				}
			}
		}
		finally
		{
			tracker.close();
		}
		return result;
	}
}
