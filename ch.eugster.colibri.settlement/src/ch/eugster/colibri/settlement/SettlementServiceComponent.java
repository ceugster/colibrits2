package ch.eugster.colibri.settlement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementDetail;
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
import ch.eugster.colibri.persistence.rules.ServerDatabaseRule;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.ServerService;
import ch.eugster.colibri.persistence.service.SettlementService;

public class SettlementServiceComponent implements SettlementService
{
	private ComponentContext context;

	private PersistenceService persistenceService;

	private LogService logService;

	protected void activate(ComponentContext componentContext)
	{
		this.context = componentContext;
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, "Service " + context.getProperties().get("component.name") + " aktiviert.");
		}
	}

	protected void deactivate(ComponentContext componentContext)
	{
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, "Service " + context.getProperties().get("component.name") + " deaktiviert.");
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

	protected void setLogService(LogService logService)
	{
		this.logService = logService;
	}

	protected void unsetLogService(LogService logService)
	{
		this.logService = null;
	}

	@Override
	public Settlement settle(Settlement settlement, final State state)
	{
		if (this.persistenceService != null && this.persistenceService.getServerService().isConnected())
		{
			IJobManager manager = Job.getJobManager();
			manager.beginRule(LocalDatabaseRule.getRule(), null);
			try
			{
				settlement.setReceiptCount(SettlementServiceComponent.this.countReceipts(settlement));
				settlement.setTimestamp(GregorianCalendar.getInstance());
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
					settlement.setSettled(GregorianCalendar.getInstance());
					settlement.setTimestamp(settlement.getSettled());
					settlement.setUser(settlement.getUser());
					updateStocks(settlement);
					ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
					int result = query.removeParked(settlement.getSalespoint());
					System.out.println(result);
				}
				settlement = (Settlement) persistenceService.getCacheService().merge(settlement);
			}
			finally
			{
				manager.endRule(LocalDatabaseRule.getRule());
			}
			if (state.equals(State.DEFINITIVE))
			{
				manager.beginRule(ServerDatabaseRule.getRule(), null);
				try
				{
					updateServer(persistenceService, settlement);
				}
				finally
				{
					manager.endRule(ServerDatabaseRule.getRule());
				}
			}
		}
		return settlement;
	}
	
	@Override
	public Salespoint updateSettlement(Salespoint salespoint)
	{
		salespoint.setSettlement(Settlement.newInstance(salespoint));
		salespoint = (Salespoint) persistenceService.getCacheService().merge(salespoint);

		if (!persistenceService.getServerService().isLocal())
		{
			Salespoint serverSalespoint = (Salespoint) persistenceService.getServerService().find(Salespoint.class,
					salespoint.getId());
			serverSalespoint.setSettlement(Settlement.newInstance(serverSalespoint));
			serverSalespoint = (Salespoint) persistenceService.getServerService().merge(serverSalespoint);
		}
		return salespoint;
	}

	private Collection<SettlementPayment> getPayments(final ConnectionService service, final Settlement settlement)
	{
		final PaymentQuery query = (PaymentQuery) service.getQuery(Payment.class);
		return query.selectPayments(settlement);
	}

	private Collection<SettlementPosition> getPositions(final ConnectionService service, final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		Collection<SettlementPosition> positions = query.selectPositions(settlement);
		return positions;
	}

	private Collection<SettlementPayedInvoice> getPayedInvoices(final ConnectionService service,
			final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectPayedInvoices(settlement);
	}

	private Collection<SettlementRestitutedPosition> getRestitutedPositions(final ConnectionService service,
			final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectRestitutedPositions(settlement);
	}

	private Collection<SettlementInternal> getInternals(final ConnectionService service, final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectInternals(settlement);
	}

	private Collection<SettlementReceipt> getReversedReceipts(final ConnectionService service,
			final Settlement settlement)
	{
		final ReceiptQuery query = (ReceiptQuery) service.getQuery(Receipt.class);
		return query.selectReversed(settlement);
	}

	private Collection<SettlementTax> getTaxes(final ConnectionService service, final Settlement settlement)
	{
		final PositionQuery query = (PositionQuery) service.getQuery(Position.class);
		return query.selectTaxes(settlement);
	}

	private void updateStocks(Settlement settlement)
	{
		final Collection<Stock> stocks = settlement.getSalespoint().getStocks();
		for (final Stock stock : stocks)
		{
			if (stock.isVariable())
			{
				stock.setLastCashSettlement(settlement);
				double newStock = 0D;
				Collection<SettlementMoney> moneys = settlement.getMoneys();
				for (SettlementMoney money : moneys)
				{
					if (money.getStock().getId().equals(stock.getId()))
					{
						newStock += stock.getAmount();
					}
				}
				stock.setAmount(newStock);
			}
		}
	}

	private void updateServer(PersistenceService persistenceService, Settlement settlement)
	{
		if (!persistenceService.getServerService().isLocal())
		{
			Salespoint salespoint = (Salespoint) persistenceService.getServerService().find(Salespoint.class,
					settlement.getSalespoint().getId());

			Settlement serverSettlement = salespoint.getSettlement();
			serverSettlement.setReceiptCount(settlement.getReceiptCount());
			serverSettlement.setSettled(settlement.getSettled());
			serverSettlement.setTimestamp(settlement.getTimestamp());
			serverSettlement.setUser(settlement.getUser());

			salespoint.getSettlement().setPositions(
					copySettlementPositions(persistenceService.getServerService(), settlement,
							salespoint.getSettlement()));
			salespoint.getSettlement().setPayments(
					copySettlementPayments(persistenceService.getServerService(), settlement,
							salespoint.getSettlement()));
			salespoint.getSettlement().setTaxes(
					copySettlementTaxes(persistenceService.getServerService(), settlement, salespoint.getSettlement()));
			salespoint.getSettlement().setInternals(
					copySettlementInternals(persistenceService.getServerService(), settlement,
							salespoint.getSettlement()));
			salespoint.getSettlement().setRestitutedPositions(
					copySettlementRestituted(persistenceService.getServerService(), settlement,
							salespoint.getSettlement()));
			salespoint.getSettlement().setPayedInvoices(
					copySettlementPayedInvoices(persistenceService.getServerService(), settlement,
							salespoint.getSettlement()));
			salespoint.getSettlement().setReversedReceipts(
					copySettlementReceipts(persistenceService.getServerService(), settlement,
							salespoint.getSettlement()));
			salespoint.getSettlement()
					.setDetails(
							copySettlementDetails(persistenceService.getServerService(), settlement,
									salespoint.getSettlement()));
			salespoint.getSettlement()
					.setMoneys(
							copySettlementMoneys(persistenceService.getServerService(), settlement,
									salespoint.getSettlement()));
			persistenceService.getServerService().merge(salespoint.getSettlement());
		}
	}

	private Collection<SettlementPosition> copySettlementPositions(ServerService serverService, Settlement settlement,
			Settlement serverSettlement)
	{
		Collection<SettlementPosition> positions = settlement.getPositions();
		Collection<SettlementPosition> serverPositions = new ArrayList<SettlementPosition>();
		for (SettlementPosition position : positions)
		{
			ProductGroup productGroup = (ProductGroup) serverService.find(ProductGroup.class, position
					.getProductGroup().getId());
			Currency currency = (Currency) serverService.find(Currency.class, position.getDefaultCurrency().getId());
			SettlementPosition serverPosition = SettlementPosition
					.newInstance(serverSettlement, productGroup, currency);
			serverPosition.setDefaultCurrencyAmount(position.getDefaultCurrencyAmount());
			serverPosition.setDeleted(position.isDeleted());
			serverPosition.setQuantity(position.getQuantity());
			serverPosition.setTaxAmount(position.getTaxAmount());
			serverPosition.setTimestamp(position.getTimestamp());
			serverPosition.setUpdate(0);
			serverPosition.setVersion(0);
			serverPositions.add(serverPosition);
		}
		return serverPositions;
	}

	private Collection<SettlementPayment> copySettlementPayments(ServerService serverService, Settlement settlement,
			Settlement serverSettlement)
	{
		Collection<SettlementPayment> payments = settlement.getPayments();
		Collection<SettlementPayment> serverPayments = new ArrayList<SettlementPayment>();
		for (SettlementPayment payment : payments)
		{
			PaymentType paymentType = (PaymentType) serverService.find(PaymentType.class, payment.getPaymentType()
					.getId());
			SettlementPayment serverPayment = SettlementPayment.newInstance(serverSettlement, paymentType);
			serverPayment.setDefaultCurrencyAmount(payment.getDefaultCurrencyAmount());
			serverPayment.setDeleted(payment.isDeleted());
			serverPayment.setForeignCurrencyAmount(payment.getForeignCurrencyAmount());
			serverPayment.setQuantity(payment.getQuantity());
			serverPayment.setTimestamp(payment.getTimestamp());
			serverPayments.add(serverPayment);
		}
		return serverPayments;
	}

	private Collection<SettlementTax> copySettlementTaxes(ServerService serverService, Settlement settlement,
			Settlement serverSettlement)
	{
		Collection<SettlementTax> taxes = settlement.getTaxes();
		Collection<SettlementTax> serverTaxes = new ArrayList<SettlementTax>();
		for (SettlementTax tax : taxes)
		{
			CurrentTax currentTax = (CurrentTax) serverService.find(CurrentTax.class, tax.getCurrentTax().getId());
			SettlementTax serverTax = SettlementTax.newInstance(serverSettlement, currentTax);
			serverTax.setBaseAmount(tax.getBaseAmount());
			serverTax.setDeleted(tax.isDeleted());
			serverTax.setQuantity(tax.getQuantity());
			serverTax.setTaxAmount(tax.getTaxAmount());
			serverTax.setTimestamp(tax.getTimestamp());
			serverTaxes.add(serverTax);
		}
		return serverTaxes;
	}

	private Collection<SettlementInternal> copySettlementInternals(ServerService serverService, Settlement settlement,
			Settlement serverSettlement)
	{
		Collection<SettlementInternal> internals = settlement.getInternals();
		Collection<SettlementInternal> serverInternals = new ArrayList<SettlementInternal>();
		for (SettlementInternal internal : internals)
		{
			Position position = (Position) serverService.find(Position.class, internal.getPosition().getOtherId());
			SettlementInternal serverInternal = SettlementInternal.newInstance(serverSettlement, position);
			serverInternal.setDeleted(internal.isDeleted());
			serverInternal.setTimestamp(internal.getTimestamp());
			serverInternal.setUpdate(0);
			serverInternal.setVersion(0);
			serverInternals.add(serverInternal);
		}
		return serverInternals;
	}

	private Collection<SettlementRestitutedPosition> copySettlementRestituted(ServerService serverService,
			Settlement settlement, Settlement serverSettlement)
	{
		Collection<SettlementRestitutedPosition> restituteds = settlement.getRestitutedPositions();
		Collection<SettlementRestitutedPosition> serverRestituteds = new ArrayList<SettlementRestitutedPosition>();
		for (SettlementRestitutedPosition restituted : restituteds)
		{
			Position position = (Position) serverService.find(Position.class, restituted.getPosition().getOtherId());
			SettlementRestitutedPosition serverRestituted = SettlementRestitutedPosition.newInstance(serverSettlement,
					position);
			serverRestituted.setDeleted(restituted.isDeleted());
			serverRestituted.setTimestamp(restituted.getTimestamp());
			serverRestituted.setUpdate(0);
			serverRestituted.setVersion(0);
			serverRestituteds.add(serverRestituted);
		}
		return serverRestituteds;
	}

	private Collection<SettlementPayedInvoice> copySettlementPayedInvoices(ServerService serverService,
			Settlement settlement, Settlement serverSettlement)
	{
		Collection<SettlementPayedInvoice> payedInvoices = settlement.getPayedInvoices();
		Collection<SettlementPayedInvoice> serverPayedInvoices = new ArrayList<SettlementPayedInvoice>();
		for (SettlementPayedInvoice payedInvoice : payedInvoices)
		{
			Position position = (Position) serverService.find(Position.class, payedInvoice.getPosition().getOtherId());
			SettlementPayedInvoice serverPayedInvoice = SettlementPayedInvoice.newInstance(serverSettlement, position);
			serverPayedInvoice.setDate(payedInvoice.getDate());
			serverPayedInvoice.setNumber(payedInvoice.getNumber());
			serverPayedInvoice.setDefaultCurrency(payedInvoice.getDefaultCurrency());
			serverPayedInvoice.setDefaultCurrencyAmount(payedInvoice.getDefaultCurrencyAmount());
			serverPayedInvoice.setQuantity(payedInvoice.getQuantity());
			serverPayedInvoice.setDeleted(payedInvoice.isDeleted());
			serverPayedInvoice.setTimestamp(payedInvoice.getTimestamp());
			serverPayedInvoice.setUpdate(0);
			serverPayedInvoice.setVersion(0);
			serverPayedInvoices.add(serverPayedInvoice);
		}
		return serverPayedInvoices;
	}

	private Collection<SettlementReceipt> copySettlementReceipts(ServerService serverService, Settlement settlement,
			Settlement serverSettlement)
	{
		Collection<SettlementReceipt> receipts = settlement.getReversedReceipts();
		Collection<SettlementReceipt> serverReceipts = new ArrayList<SettlementReceipt>();
		for (SettlementReceipt receipt : receipts)
		{
			Receipt reversedServerReceipt = (Receipt) serverService.find(Receipt.class, receipt.getReceipt()
					.getOtherId());
			SettlementReceipt serverReceipt = SettlementReceipt.newInstance(serverSettlement, reversedServerReceipt);
			serverReceipt.setDeleted(receipt.isDeleted());
			serverReceipt.setTimestamp(receipt.getTimestamp());
			serverReceipts.add(serverReceipt);
		}
		return serverReceipts;
	}

	private Collection<SettlementDetail> copySettlementDetails(ServerService serverService, Settlement settlement,
			Settlement serverSettlement)
	{
		Collection<SettlementDetail> details = settlement.getDetails();
		Collection<SettlementDetail> serverDetails = new ArrayList<SettlementDetail>();
		for (SettlementDetail detail : details)
		{
			PaymentType paymentType = (PaymentType) serverService.find(PaymentType.class, detail.getPaymentType()
					.getId());
			Stock stock = (Stock) serverService.find(Stock.class, detail.getStock().getId());
			SettlementDetail serverDetail = SettlementDetail.newInstance(serverSettlement, stock);
			serverDetail.setCredit(detail.getCredit());
			serverDetail.setDebit(detail.getDebit());
			serverDetail.setDeleted(detail.isDeleted());
			serverDetail.setPart(detail.getPart());
			serverDetail.setPaymentType(paymentType);
			serverDetail.setQuantity(detail.getQuantity());
			serverDetail.setTimestamp(detail.getTimestamp());
			serverDetail.setVariableStock(detail.isVariableStock());
			serverDetails.add(serverDetail);
		}
		return serverDetails;
	}

	private Collection<SettlementMoney> copySettlementMoneys(ServerService serverService, Settlement settlement,
			Settlement serverSettlement)
	{
		Collection<SettlementMoney> moneys = settlement.getMoneys();
		Collection<SettlementMoney> serverMoneys = new ArrayList<SettlementMoney>();
		for (SettlementMoney money : moneys)
		{
			PaymentType paymentType = (PaymentType) serverService.find(PaymentType.class, money.getPaymentType()
					.getId());
			Stock stock = (Stock) serverService.find(Stock.class, money.getStock().getId());
			SettlementMoney serverMoney = SettlementMoney.newInstance(serverSettlement, stock);
			serverMoney.setMoney(money.getMoney());
			serverMoney.setAmount(money.getAmount());
			serverMoney.setCode(money.getCode());
			serverMoney.setDeleted(money.isDeleted());
			serverMoney.setPaymentType(paymentType);
			serverMoney.setQuantity(money.getQuantity());
			serverMoney.setText(money.getText());
			serverMoney.setTimestamp(money.getTimestamp());
			serverMoneys.add(serverMoney);
		}
		return serverMoneys;
	}

	@Override
	public long countReceipts(final Settlement settlement)
	{
		final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
		return query.countSavedBySettlement(settlement);
	}

	@Override
	public Collection<SettlementReceipt> getReversedReceipts(final Settlement settlement)
	{
		final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
		return query.selectReversed(settlement);
	}
}
