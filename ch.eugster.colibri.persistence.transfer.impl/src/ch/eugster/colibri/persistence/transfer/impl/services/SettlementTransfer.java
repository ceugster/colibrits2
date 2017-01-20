package ch.eugster.colibri.persistence.transfer.impl.services;

import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.service.PersistenceService;

public class SettlementTransfer extends AbstractTransfer 
{
	public SettlementTransfer(LogService logService, PersistenceService persistenceService)
	{
		super(logService, persistenceService);
	}
	
//	public IStatus transferSettlements(int count)
//	{
//		IStatus status = getStatus(null);
//		if (!persistenceService.getServerService().isLocal())
//		{
//			try
//			{
//				List<Settlement> settlements = getSettlements();
//				status = transfer(settlements);
//			}
//			catch (Exception e)
//			{
//				status = getStatus(e);
//			}
//		}
//		return status;
//	}
	
//	private void updateServerStock(Settlement settlement)
//	{
//		final List<Stock> stocks = settlement.getSalespoint().getStocks();
//		for (final Stock stock : stocks)
//		{
//			if (stock.isVariable())
//			{
//				stock.setLastCashSettlement(settlement);
//				double newStock = 0D;
//				List<SettlementDetail> details = settlement.getDetails();
//				for (SettlementDetail detail : details)
//				{
//					if (detail.getStock().getId().equals(stock.getId()))
//					{
//						if (detail.getPart().equals(Part.INCOME))
//						{
//							newStock = detail.getCredit();
//						}
//					}
//				}
//				stock.setAmount(stock.getAmount() + newStock);
//			}
//		}
//	}

//	private IStatus transfer(List<Settlement> localSettlements) throws Exception
//	{
//		IStatus status = getStatus(null);
//		for (Settlement localSettlement : localSettlements)
//		{
//			if (localSettlement.getOtherId() == null)
//			{
//				Salespoint serverSalespoint = (Salespoint) persistenceService.getServerService().find(Salespoint.class, localSettlement.getSalespoint().getId());
//				Settlement serverSettlement = null;
//				try
//				{
//					if (serverSalespoint.getSettlement() == null || serverSalespoint.getSettlement().getSettled() != null)
//					{
//						serverSalespoint.setSettlement(Settlement.newInstance(serverSalespoint));
//						serverSalespoint = (Salespoint) persistenceService.getServerService().merge(serverSalespoint, true, false);
//					}
//					serverSettlement = serverSalespoint.getSettlement();
//					serverSettlement = updateServerSettlement(localSettlement, serverSettlement);
//					updateServerStock(serverSettlement);
//					localSettlement.setOtherId(serverSettlement.getId());
//					persistenceService.getCacheService().merge(localSettlement);
//					serverSalespoint.setSettlement(Settlement.newInstance(serverSalespoint));
//					serverSalespoint = (Salespoint) persistenceService.getServerService().merge(serverSalespoint, true, false);
//				}
//				catch (Exception e)
//				{
//					status = getStatus(e);
//				}
//			}
//		}
//		return status;
//	}

//	public List<Settlement> getSettlements() throws Exception
//	{
//		final CacheService cacheService = this.persistenceService.getCacheService();
//		final SettlementQuery queryService = (SettlementQuery) cacheService.getQuery(Settlement.class);
//		return queryService.selectTransferables();
//	}

//	private Settlement updateServerSettlement(Settlement localSettlement, Settlement serverSettlement) throws Exception
//	{
//		serverSettlement.setOtherId(localSettlement.getId());
//		serverSettlement.setReceiptCount(localSettlement.getReceiptCount());
//		serverSettlement.setSettled(localSettlement.getSettled());
//		serverSettlement.setTimestamp(localSettlement.getTimestamp());
//		serverSettlement.setUser(localSettlement.getUser());
//
//		serverSettlement.setPositions(copySettlementPositions(persistenceService.getServerService(), localSettlement,
//						serverSettlement));
//		serverSettlement.setPayments(copySettlementPayments(persistenceService.getServerService(), localSettlement,
//						serverSettlement));
//		serverSettlement.setTaxes(copySettlementTaxes(persistenceService.getServerService(), localSettlement, serverSettlement));
//		serverSettlement.setInternals(copySettlementInternals(persistenceService.getServerService(), localSettlement,
//						serverSettlement));
//		serverSettlement.setRestitutedPositions(copySettlementRestituted(persistenceService.getServerService(), localSettlement,
//						serverSettlement));
//		serverSettlement.setPayedInvoices(copySettlementPayedInvoices(persistenceService.getServerService(), localSettlement,
//						serverSettlement));
//		serverSettlement.setReversedReceipts(copySettlementReceipts(persistenceService.getServerService(), localSettlement,
//						serverSettlement));
//		serverSettlement.setDetails(copySettlementDetails(persistenceService.getServerService(), localSettlement,
//								serverSettlement));
//		serverSettlement.setMoneys(copySettlementMoneys(persistenceService.getServerService(), localSettlement,
//								serverSettlement));
//		return (Settlement) persistenceService.getServerService().merge(serverSettlement);
//	}

//	private List<SettlementPosition> copySettlementPositions(ServerService serverService, Settlement localSettlement,
//			Settlement serverSettlement)
//	{
//		List<SettlementPosition> positions = localSettlement.getPositions();
//		List<SettlementPosition> serverPositions = new ArrayList<SettlementPosition>();
//		for (SettlementPosition position : positions)
//		{
//			ProductGroup productGroup = (ProductGroup) serverService.find(ProductGroup.class, position
//					.getProductGroup().getId());
//			Currency currency = (Currency) serverService.find(Currency.class, position.getDefaultCurrency().getId());
//			SettlementPosition serverPosition = SettlementPosition
//					.newInstance(serverSettlement, productGroup, currency);
//			serverPosition.setDefaultCurrencyAmount(position.getDefaultCurrencyAmount());
//			serverPosition.setDeleted(position.isDeleted());
//			serverPosition.setQuantity(position.getQuantity());
//			serverPosition.setTaxAmount(position.getTaxAmount());
//			serverPosition.setTimestamp(position.getTimestamp());
//			serverPosition.setUpdate(0);
//			serverPosition.setVersion(0);
//			serverPositions.add(serverPosition);
//		}
//		return serverPositions;
//	}
//
//	private List<SettlementPayment> copySettlementPayments(ServerService serverService, Settlement localSettlement,
//			Settlement serverSettlement)
//	{
//		List<SettlementPayment> payments = localSettlement.getPayments();
//		List<SettlementPayment> serverPayments = new ArrayList<SettlementPayment>();
//		for (SettlementPayment payment : payments)
//		{
//			PaymentType paymentType = (PaymentType) serverService.find(PaymentType.class, payment.getPaymentType()
//					.getId());
//			SettlementPayment serverPayment = SettlementPayment.newInstance(serverSettlement, paymentType);
//			serverPayment.setDefaultCurrencyAmount(payment.getDefaultCurrencyAmount());
//			serverPayment.setDeleted(payment.isDeleted());
//			serverPayment.setForeignCurrencyAmount(payment.getForeignCurrencyAmount());
//			serverPayment.setQuantity(payment.getQuantity());
//			serverPayment.setTimestamp(payment.getTimestamp());
//			serverPayments.add(serverPayment);
//		}
//		return serverPayments;
//	}
//
//	private List<SettlementTax> copySettlementTaxes(ServerService serverService, Settlement settlement,
//			Settlement serverSettlement)
//	{
//		List<SettlementTax> taxes = settlement.getTaxes();
//		List<SettlementTax> serverTaxes = new ArrayList<SettlementTax>();
//		for (SettlementTax tax : taxes)
//		{
//			CurrentTax currentTax = (CurrentTax) serverService.find(CurrentTax.class, tax.getCurrentTax().getId());
//			SettlementTax serverTax = SettlementTax.newInstance(serverSettlement, currentTax);
//			serverTax.setBaseAmount(tax.getBaseAmount());
//			serverTax.setDeleted(tax.isDeleted());
//			serverTax.setQuantity(tax.getQuantity());
//			serverTax.setTaxAmount(tax.getTaxAmount());
//			serverTax.setTimestamp(tax.getTimestamp());
//			serverTaxes.add(serverTax);
//		}
//		return serverTaxes;
//	}
//
//	private List<SettlementInternal> copySettlementInternals(ServerService serverService, Settlement localSettlement,
//			Settlement serverSettlement)
//	{
//		List<SettlementInternal> internals = localSettlement.getInternals();
//		List<SettlementInternal> serverInternals = new ArrayList<SettlementInternal>();
//		for (SettlementInternal internal : internals)
//		{
//			ProductGroup productGroup = (ProductGroup) serverService.find(ProductGroup.class, internal
//					.getProductGroup().getId());
//			Currency currency = (Currency) serverService.find(Currency.class, internal.getDefaultCurrency().getId());
//			SettlementInternal serverInternal = SettlementInternal
//					.newInstance(serverSettlement, productGroup, currency);
//			serverInternal.setDefaultCurrencyAmount(internal.getDefaultCurrencyAmount());
//			serverInternal.setDeleted(internal.isDeleted());
//			serverInternal.setQuantity(internal.getQuantity());
//			serverInternal.setTimestamp(internal.getTimestamp());
//			serverInternal.setUpdate(0);
//			serverInternal.setVersion(0);
//			serverInternals.add(serverInternal);
//		}
//		return serverInternals;
//	}
//
//	private List<SettlementRestitutedPosition> copySettlementRestituted(ServerService serverService,
//			Settlement settlement, Settlement serverSettlement)
//	{
//		List<SettlementRestitutedPosition> restituteds = settlement.getRestitutedPositions();
//		List<SettlementRestitutedPosition> serverRestituteds = new ArrayList<SettlementRestitutedPosition>();
//		for (SettlementRestitutedPosition restituted : restituteds)
//		{
//			if (restituted.getPosition().getOtherId() != null)
//			{
//				Position position = (Position) serverService.find(Position.class, restituted.getPosition().getOtherId());
//				SettlementRestitutedPosition serverRestituted = SettlementRestitutedPosition.newInstance(serverSettlement,
//						position);
//				serverRestituted.setDeleted(restituted.isDeleted());
//				serverRestituted.setTimestamp(restituted.getTimestamp());
//				serverRestituted.setUpdate(0);
//				serverRestituted.setVersion(0);
//				serverRestituteds.add(serverRestituted);
//			}
//		}
//		return serverRestituteds;
//	}
//
//	private List<SettlementPayedInvoice> copySettlementPayedInvoices(ServerService serverService,
//			Settlement settlement, Settlement serverSettlement)
//	{
//		List<SettlementPayedInvoice> payedInvoices = settlement.getPayedInvoices();
//		List<SettlementPayedInvoice> serverPayedInvoices = new ArrayList<SettlementPayedInvoice>();
//		for (SettlementPayedInvoice payedInvoice : payedInvoices)
//		{
//			if (payedInvoice.getPosition().getOtherId() != null)
//			{
//				Position position = (Position) serverService.find(Position.class, payedInvoice.getPosition().getOtherId());
//				SettlementPayedInvoice serverPayedInvoice = SettlementPayedInvoice.newInstance(serverSettlement, position);
//				serverPayedInvoice.setDate(payedInvoice.getDate());
//				serverPayedInvoice.setNumber(payedInvoice.getNumber());
//				serverPayedInvoice.setDefaultCurrency(payedInvoice.getDefaultCurrency());
//				serverPayedInvoice.setDefaultCurrencyAmount(payedInvoice.getDefaultCurrencyAmount());
//				serverPayedInvoice.setQuantity(payedInvoice.getQuantity());
//				serverPayedInvoice.setDeleted(payedInvoice.isDeleted());
//				serverPayedInvoice.setTimestamp(payedInvoice.getTimestamp());
//				serverPayedInvoice.setUpdate(0);
//				serverPayedInvoice.setVersion(0);
//				serverPayedInvoices.add(serverPayedInvoice);
//			}
//		}
//		return serverPayedInvoices;
//	}
//
//	private List<SettlementReceipt> copySettlementReceipts(ServerService serverService, Settlement settlement,
//			Settlement serverSettlement)
//	{
//		List<SettlementReceipt> receipts = settlement.getReversedReceipts();
//		List<SettlementReceipt> serverReceipts = new ArrayList<SettlementReceipt>();
//		for (SettlementReceipt receipt : receipts)
//		{
//			Receipt reversedServerReceipt = (Receipt) serverService.find(Receipt.class, receipt.getReceipt()
//					.getOtherId());
//			SettlementReceipt serverReceipt = SettlementReceipt.newInstance(serverSettlement, reversedServerReceipt);
//			serverReceipt.setDeleted(receipt.isDeleted());
//			serverReceipt.setTimestamp(receipt.getTimestamp());
//			serverReceipts.add(serverReceipt);
//		}
//		return serverReceipts;
//	}
//
//	private List<SettlementDetail> copySettlementDetails(ServerService serverService, Settlement settlement,
//			Settlement serverSettlement)
//	{
//		List<SettlementDetail> details = settlement.getDetails();
//		List<SettlementDetail> serverDetails = new ArrayList<SettlementDetail>();
//		for (SettlementDetail detail : details)
//		{
//			PaymentType paymentType = (PaymentType) serverService.find(PaymentType.class, detail.getPaymentType()
//					.getId());
//			Stock stock = (Stock) serverService.find(Stock.class, detail.getStock().getId());
//			SettlementDetail serverDetail = SettlementDetail.newInstance(serverSettlement, stock);
//			serverDetail.setCredit(detail.getCredit());
//			serverDetail.setDebit(detail.getDebit());
//			serverDetail.setDeleted(detail.isDeleted());
//			serverDetail.setPart(detail.getPart());
//			serverDetail.setPaymentType(paymentType);
//			serverDetail.setQuantity(detail.getQuantity());
//			serverDetail.setTimestamp(detail.getTimestamp());
//			serverDetail.setVariableStock(detail.isVariableStock());
//			serverDetails.add(serverDetail);
//		}
//		return serverDetails;
//	}
//
//	private List<SettlementMoney> copySettlementMoneys(ServerService serverService, Settlement settlement,
//			Settlement serverSettlement)
//	{
//		List<SettlementMoney> moneys = settlement.getMoneys();
//		List<SettlementMoney> serverMoneys = new ArrayList<SettlementMoney>();
//		for (SettlementMoney money : moneys)
//		{
//			PaymentType paymentType = (PaymentType) serverService.find(PaymentType.class, money.getPaymentType()
//					.getId());
//			Stock stock = (Stock) serverService.find(Stock.class, money.getStock().getId());
//			SettlementMoney serverMoney = SettlementMoney.newInstance(serverSettlement, stock);
//			serverMoney.setMoney(money.getMoney());
//			serverMoney.setAmount(money.getAmount());
//			serverMoney.setCode(money.getCode());
//			serverMoney.setDeleted(money.isDeleted());
//			serverMoney.setPaymentType(paymentType);
//			serverMoney.setQuantity(money.getQuantity());
//			serverMoney.setText(money.getText());
//			serverMoney.setTimestamp(money.getTimestamp());
//			serverMoneys.add(serverMoney);
//		}
//		return serverMoneys;
//	}
}
