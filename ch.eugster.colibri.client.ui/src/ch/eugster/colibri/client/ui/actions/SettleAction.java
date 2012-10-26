/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.Status;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.panels.user.settlement.CoinCounterPanel;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementDetail;
import ch.eugster.colibri.persistence.model.SettlementMoney;
import ch.eugster.colibri.persistence.model.print.IPrintable;
import ch.eugster.colibri.persistence.service.SettlementService;
import ch.eugster.colibri.ui.actions.ProfileAction;

public class SettleAction extends ProfileAction
{
	private static final long serialVersionUID = 0l;

	public static final String TEXT = "Abschluss";

	public static final String ACTION_COMMAND = "settle.action";

	private final CoinCounterPanel coinCounterPanel;

	public SettleAction(final CoinCounterPanel coinCounterPanel)
	{
		super(SettleAction.TEXT, SettleAction.ACTION_COMMAND, coinCounterPanel.getProfile());
		this.coinCounterPanel = coinCounterPanel;
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
				
				if (settlement.getReceiptCount() == 0L && settlement.getMoneys().size() == 0)
				{
					MessageDialog.showInformation(Activator.getDefault().getFrame(), this.profile,
							"Keine Vorgänge", "Es stehen keine Vorgänge zur Durchführung an.",
							MessageDialog.TYPE_INFORMATION);
				}
				else
				{
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
						this.coinCounterPanel.getUserPanel().setSalespoint(salespoint);
						this.coinCounterPanel.clear();
						this.coinCounterPanel.getUserPanel().fireStateChange(
								new StateChangeEvent(coinCounterPanel.getUserPanel().getCurrentState(),
										UserPanel.State.POSITION_INPUT));
					}
					printSettlement(settlement);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
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
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
		properties.put(EventConstants.SERVICE, tracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID, tracker.getServiceReference().getProperty("component.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put(IPrintable.class.getName(), settlement);
		properties.put("force", true);
		properties.put("status", Status.OK_STATUS);
		return new Event(topics, properties);
	}

	private Collection<SettlementDetail> getSettlementDetails(final Settlement settlement)
	{
		return this.coinCounterPanel.getSettlementDetails(settlement);
	}

	private Collection<SettlementMoney> getSettlementMoney(final Settlement settlement)
	{
		return this.coinCounterPanel.getSettlementMoney(settlement);
	}
}
