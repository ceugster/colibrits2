package ch.eugster.colibri.settlement;

import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementInternal;
import ch.eugster.colibri.persistence.model.SettlementMoney;
import ch.eugster.colibri.persistence.model.SettlementPayedInvoice;
import ch.eugster.colibri.persistence.model.SettlementPayment;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.SettlementReceipt;
import ch.eugster.colibri.persistence.model.SettlementRestitutedPosition;
import ch.eugster.colibri.persistence.model.SettlementTax;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.rules.LocalDatabaseRule;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.SettlementService;

public class SettlementServiceComponent implements SettlementService
{
	private ComponentContext context;

	private PersistenceService persistenceService;

	private EventAdmin eventAdmin;
	
	private LogService logService;

	protected void activate(ComponentContext componentContext)
	{
		this.context = componentContext;
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Service " + context.getProperties().get("component.name") + " aktiviert.");
		}
	}

	protected void deactivate(ComponentContext componentContext)
	{
		if (logService != null)
		{
			logService.log(LogService.LOG_DEBUG, "Service " + context.getProperties().get("component.name") + " deaktiviert.");
		}
		this.context = null;
	}

	protected void setPersistenceService(PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void unsetPersistenceService(PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	protected void setEventAdmin(EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	protected void unsetEventAdmin(EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	protected void setLogService(LogService logService)
	{
		this.logService = logService;
	}

	protected void unsetLogService(LogService logService)
	{
		this.logService = null;
	}

	@Override
	public Settlement settle(Settlement settlement, final State state) throws Exception
	{
		if (this.persistenceService != null)
		{
			IJobManager manager = Job.getJobManager();
			manager.beginRule(LocalDatabaseRule.getRule(), null);
			try
			{
				settlement.setReceiptCount(SettlementServiceComponent.this.countReceipts(settlement));
				settlement.setTimestamp(GregorianCalendar.getInstance(Locale.getDefault()));
				settlement.setPositions(SettlementServiceComponent.this.getPositions(persistenceService.getCacheService(), settlement));
				settlement.setPayments(SettlementServiceComponent.this.getPayments(persistenceService.getCacheService(), settlement));
				settlement.setTaxes(SettlementServiceComponent.this.getTaxes(persistenceService.getCacheService(), settlement));
				settlement.setPayedInvoices(SettlementServiceComponent.this.getPayedInvoices(persistenceService.getCacheService(), settlement));
				settlement
						.setRestitutedPositions(SettlementServiceComponent.this.getRestitutedPositions(persistenceService.getCacheService(), settlement));
				settlement.setInternals(SettlementServiceComponent.this.getInternals(persistenceService.getCacheService(), settlement));
				settlement.setReversedReceipts(SettlementServiceComponent.this.getReversedReceipts(persistenceService.getCacheService(), settlement));

				if (state.equals(State.DEFINITIVE))
				{
					settlement.setSettled(GregorianCalendar.getInstance(Locale.getDefault()));
					settlement.setTimestamp(settlement.getSettled());
					settlement.setUser(settlement.getUser());
					updateStocks(settlement);
					ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
					query.removeParked(settlement.getSalespoint());

					try
					{
						settlement = (Settlement) persistenceService.getCacheService().merge(settlement);
//						settlement.setSalespoint((Salespoint) persistenceService.getCacheService().merge(settlement.getSalespoint()));
						eventAdmin.sendEvent(getEvent());
					}
					catch (Exception e)
					{
						throw e;
					}
				}
			}
			finally
			{
				manager.endRule(LocalDatabaseRule.getRule());
			}
		}
		return settlement;
	}
	
	private Event getEvent()
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, context.getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID, Long.valueOf(context.getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, context.getBundleContext().getBundle().getSymbolicName());
		properties.put(EventConstants.SERVICE, context.getServiceReference());
		properties.put(EventConstants.SERVICE_ID, context.getProperties().get("component.id"));
		properties.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
		properties.put(EventConstants.SERVICE_PID, context.getProperties().get("component.name"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
		properties.put("status", Status.OK_STATUS);
		Event event = new Event(Topic.SETTLE_PERFORMED.topic(), properties);
		return event;
	}
	
	@Override
	public Salespoint updateSettlement(Salespoint salespoint)
	{
		salespoint.setSettlement(Settlement.newInstance(salespoint));
		try
		{
			salespoint = (Salespoint) persistenceService.getCacheService().merge(salespoint);
		}
		catch (Exception e)
		{
			
		}

		return salespoint;
	}

	private List<SettlementPayment> getPayments(final ConnectionService service, final Settlement settlement)
	{
		final PaymentQuery query = (PaymentQuery) service.getQuery(Payment.class);
		return query.selectPayments(settlement);
	}

	private List<SettlementPosition> getPositions(final ConnectionService service, final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		List<SettlementPosition> positions = query.selectPositions(settlement);
		return positions;
	}

	private List<SettlementPayedInvoice> getPayedInvoices(final ConnectionService service,
			final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectPayedInvoices(settlement);
	}

	private List<SettlementRestitutedPosition> getRestitutedPositions(final ConnectionService service,
			final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectRestitutedPositions(settlement);
	}

	private List<SettlementInternal> getInternals(final ConnectionService service, final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectInternals(settlement);
	}

	private List<SettlementReceipt> getReversedReceipts(final ConnectionService service,
			final Settlement settlement)
	{
		final ReceiptQuery query = (ReceiptQuery) service.getQuery(Receipt.class);
		return query.selectReversed(settlement);
	}

	private List<SettlementTax> getTaxes(final ConnectionService service, final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectTaxes(settlement);
	}

	private void updateStocks(Settlement settlement)
	{
		final List<Stock> stocks = settlement.getSalespoint().getStocks();
		for (final Stock stock : stocks)
		{
			if (stock.isVariable())
			{
				stock.setLastCashSettlement(settlement);
				double newStock = 0D;
				List<SettlementMoney> moneys = settlement.getMoneys();
				for (SettlementMoney money : moneys)
				{
					if (money.getStock().getId().equals(stock.getId()))
					{
						newStock += money.getAmount();
					}
				}
				stock.setAmount(newStock);
			}
		}
	}

	@Override
	public long countReceipts(final Settlement settlement)
	{
		final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
		return query.countSavedBySettlement(settlement);
	}

	@Override
	public List<SettlementReceipt> getReversedReceipts(final Settlement settlement)
	{
		final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
		return query.selectReversed(settlement);
	}
}
