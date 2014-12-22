package ch.eugster.colibri.insert.receipt.app;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.osgi.service.component.ComponentContext;

import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.SettlementService;
import ch.eugster.colibri.persistence.transfer.services.TransferAgent;
import ch.eugster.colibri.print.service.PrintService;

public class InsertReceiptService
{
	Map<String, PrintService> printServices = new HashMap<String, PrintService>();

	private PersistenceService persistenceService;

	private SettlementService settlementService;

	private TransferAgent transferAgent;

	private ProductGroup[] productGroups;

	private PaymentType[] paymentTypes;

	private Salespoint salespoint;

	private User[] users;

	protected void activate(ComponentContext componentContext)
	{

	}

	public void settleDay(Calendar date)
	{
		if (date == null)
		{
			date = GregorianCalendar.getInstance(Locale.getDefault());
		}
		prepare();

		try
		{
			Settlement settlement = settlementService
					.settle(salespoint.getSettlement(), SettlementService.State.DEFINITIVE);
			PrintService service = printServices.get("ch.eugster.colibri.print.settlement");
			if (service != null)
			{
				service.printDocument(settlement);
			}

			settlement.getSalespoint().setSettlement(settlement);
			salespoint = settlementService.updateSettlement(salespoint);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}

	private void prepare()
	{
		if (productGroups == null)
		{
			ProductGroupQuery productGroupQuery = (ProductGroupQuery) persistenceService.getCacheService().getQuery(
					ProductGroup.class);
			productGroups = productGroupQuery.selectAll(false).toArray(new ProductGroup[0]);
		}

		if (paymentTypes == null)
		{
			PaymentTypeQuery paymentTypeQuery = (PaymentTypeQuery) persistenceService.getCacheService().getQuery(
					PaymentType.class);
			paymentTypes = paymentTypeQuery.selectAll(false).toArray(new PaymentType[0]);
		}

		if (salespoint == null)
		{
			SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getCacheService().getQuery(
					Salespoint.class);
			salespoint = salespointQuery.getCurrentSalespoint();
		}

		if (users == null)
		{
			UserQuery userQuery = (UserQuery) persistenceService.getCacheService().getQuery(User.class);
			users = userQuery.selectAll(false).toArray(new User[0]);
		}

	}

	public void insertReceipts(int count)
	{
		prepare();
		for (int i = 0; i < count; i++)
		{
			Receipt receipt = Receipt.newInstance(salespoint.getSettlement(), getArbitraryUser());
			receipt.setNumber(salespoint.getNextParkedReceiptNumber());
			receipt.setState(i > 0 && i % 20 == 0 ? Receipt.State.REVERSED : Receipt.State.SAVED);
			int max = getArbitraryPositionCount(5);
			for (int j = 0; j < max; j++)
			{
				if (j > 0)
				{
					Position[] positions = receipt.getAllPositions().toArray(new Position[0]);
					if (!positions[0].getProductGroup().getProductGroupType().getParent()
							.equals(ProductGroupGroup.SALES))
					{
						break;
					}
				}
				Position position = Position.newInstance(receipt);
				position.setProductGroup(getArbitraryProductGroup());
				if (position.getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.SALES))
				{
					if (position.getOption().equals(Position.Option.PAYED_INVOICE))
					{
						position.setQuantity(1);
					}
					else
					{
						position.setQuantity(getArbitraryQuantity(10));
					}
					position.setPrice(getArbitraryPrice(position.getProductGroup().getProductGroupType(), 50D));
					if (position.getProductGroup().getProductGroupType().equals(ProductGroupType.SALES_RELATED))
					{
						position.setDiscount(getArbitraryDiscount(.2));
					}
				}
				else
				{
					if (position.getProductGroup().getProductGroupType().getParent().equals(ProductGroupGroup.INTERNAL))
					{
						position.setPrice(100D);
					}
					else
					{
						position.setPrice(getArbitraryPrice(position.getProductGroup().getProductGroupType(), 50D));
					}
					position.setQuantity(1);
				}
				position.setBookProvider(position.getProductGroup().getProductGroupType().getParent()
						.equals(ProductGroupGroup.SALES));
				position.setFromStock(true);
				position.setOption(position.getProductGroup().getProductGroupType().getOptions()[0]);
				position.setOrdered(false);
				position.setProvider("ch.eugster.colibri.provider.galileo");
				receipt.addPosition(position);
			}

			Payment payment = Payment.newInstance(receipt);
			double positionAmount = receipt.getPositionAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
					Position.AmountType.NETTO);
			PaymentType paymentType = getArbitraryPaymentType();
			payment.setPaymentType(paymentType);
			payment.setAmount(positionAmount);
			receipt.addPayment(payment);

			double paymentAmount = receipt.getPaymentAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
			double difference = positionAmount - paymentAmount;
			if (Math.abs(difference) > 0.000001)
			{
				payment = Payment.newInstance(receipt);
				payment.setPaymentType(getPaymentTypeById(PaymentType.CASH_ID));
				payment.setAmount(difference);
				payment.setBack(difference < 0D);
				receipt.addPayment(payment);
			}

			try
			{
				receipt = (Receipt) persistenceService.getCacheService().merge(receipt);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}

			PrintService service = printServices.get("ch.eugster.colibri.print.receipt");
			if (service != null)
			{
				service.printDocument(receipt);
			}
			transferAgent.transfer(receipt);
		}
	}

	private int getArbitraryPositionCount(int max)
	{
		double random = Math.random();
		return Math.max(1, (int) Math.round(random * max));
	}

	private int getArbitraryQuantity(int max)
	{
		double random = Math.random();
		return Math.max(1, (int) Math.round(random * max));
	}

	private double getArbitraryPrice(ProductGroupType type, double max)
	{
		double random = Math.random();
		int factor = type.getParent().equals(ProductGroupGroup.EXPENSES) || type.equals(ProductGroupType.WITHDRAWAL) ? -1
				: 1;
		return factor * Math.max(.75, Math.round(random * max / .05) * .05);
	}

	private double getArbitraryDiscount(double max)
	{
		double random = Math.random();
		return Math.round(random * max / .05) * .05;
	}

	private ProductGroup getArbitraryProductGroup()
	{
		double random = Math.random();
		int value = Math.max(0, (int) Math.round(random * productGroups.length - 1));
		return productGroups[value];
	}

	private PaymentType getArbitraryPaymentType()
	{
		double random = Math.random();
		int value = Math.max(0, (int) Math.round(random * paymentTypes.length - 1));
		return paymentTypes[value];
	}

	private PaymentType getPaymentTypeById(Long id)
	{
		for (PaymentType paymentType : paymentTypes)
		{
			if (paymentType.getId().equals(id))
			{
				return paymentType;
			}
		}
		return paymentTypes[0];
	}

	private User getArbitraryUser()
	{
		double random = Math.random();
		int value = Math.max(0, (int) Math.round(random * users.length - 1));
		return users[value];
	}

	protected void deactivate(ComponentContext componentContext)
	{
	}
	protected void setPersistenceService(PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void unsetPersistenceService(PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	protected void setSettlementService(SettlementService settlementService)
	{
		this.settlementService = settlementService;
	}

	protected void unsetSettlementService(SettlementService settlementService)
	{
		this.settlementService = null;
	}

	protected void setTransferAgent(TransferAgent transferAgent)
	{
		this.transferAgent = transferAgent;
	}

	protected void unsetTransferAgent(TransferAgent transferAgent)
	{
		this.transferAgent = null;
	}

	protected void setPrintService(PrintService printService)
	{
		this.printServices.put(printService.getLayoutTypeId(), printService);
	}

	protected void unsetPrintService(PrintService printService)
	{
		this.printServices.remove(printService.getLayoutTypeId());
	}

}
