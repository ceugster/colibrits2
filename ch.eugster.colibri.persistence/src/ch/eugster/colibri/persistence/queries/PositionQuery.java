package ch.eugster.colibri.persistence.queries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;
import org.eclipse.persistence.expressions.ExpressionMath;
import org.eclipse.persistence.jpa.JpaHelper;
import org.eclipse.persistence.queries.ReportQuery;
import org.eclipse.persistence.queries.ReportQueryResult;

import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.DayTimeRow;
import ch.eugster.colibri.persistence.model.DiscountEntry;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupEntry;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementInternal;
import ch.eugster.colibri.persistence.model.SettlementPayedInvoice;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.SettlementRestitutedPosition;
import ch.eugster.colibri.persistence.model.SettlementTax;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class PositionQuery extends AbstractQuery<Position>
{
	private CommonSettings settings;

	public long countByCurrentTax(final CurrentTax currentTax)
	{
		final Expression expression = new ExpressionBuilder().get("currentTax").equal(currentTax);
		return this.count(expression);
	}

	public Collection<Position> countBySearchValue(final String searchValue)
	{
		final Expression parked = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.PARKED);
		final Expression value = new ExpressionBuilder().get("searchValue").equal(searchValue);
		Expression deleted = new ExpressionBuilder(Position.class).get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		return this.select(deleted.and(parked.and(value)));
	}

	private Expression createProviderUpdatesExpression(Salespoint salespoint, String providerId, boolean updateServer)
	{
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression provider = new ExpressionBuilder().get("provider").equal(providerId);
		Expression update = new ExpressionBuilder().get("bookProvider").equal(true);
		if (updateServer)
		{
			Expression server = new ExpressionBuilder().get("serverUpdated").equal(false);
			server = server.and(new ExpressionBuilder().get("otherId").notNull());
			provider = provider.and(update.or(server));
		}
		else
		{
			provider = provider.and(update);
		}
		
		Expression saved = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED);
		saved = saved.and(new ExpressionBuilder().get("providerBooked").equal(false));

		Expression reversed = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.REVERSED);
		reversed = reversed.and(new ExpressionBuilder().get("providerBooked").equal(true));
		
		return deleted.and(provider).and(saved.or(reversed));
	}
	
	public long countProviderUpdates(Salespoint salespoint, String providerId, boolean updateServer)
	{
		final Expression select = createProviderUpdatesExpression(salespoint, providerId, updateServer);
		final long value = this.count(select);
		return value;
	}

	public List<SettlementPosition> selectPositions(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		List<ReportQueryResult> results = this.selectPositionsBySalespointsAndDateRange(salespoints, dateRange);
		List<SettlementPosition> positions = this.getPositionsDetails(results, null);
		return positions;
	}

	public List<DiscountEntry> selectDiscounts(final Salespoint[] salespoints, Calendar[] dateRange, boolean onlyWithDiscount)
	{
		List<ReportQueryResult> results = this.selectPositionsBySalespointsAndDateRangeAndDiscount(salespoints, dateRange, onlyWithDiscount);
		List<DiscountEntry> entries = this.getDiscountEntries(results);
		return entries;
	}

	public List<SettlementPosition> selectPositions(final Settlement settlement)
	{
		List<ReportQueryResult> results = this.selectPositionsBySettlement(settlement);
		List<SettlementPosition> positions = this.getPositionsDetails(results, settlement);
		return positions;
	}

	public List<SettlementPayedInvoice> selectPayedInvoices(final Settlement settlement)
	{
		return this.getPayedInvoicesDetails(this.selectPayedInvoicesBySettlement(settlement), settlement);
	}

	public Collection<SettlementPayedInvoice> selectPayedInvoices(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		return this.getPayedInvoicesDetails(this.selectPayedInvoicesBySalespointsAndDateRange(salespoints, dateRange),
				null);
	}

	public List<SettlementRestitutedPosition> selectRestitutedPositions(final Settlement settlement)
	{
		return this.getRestitutedPositionDetails(this.selectRestitutedPositionsBySettlement(settlement), settlement);
	}

	public Collection<SettlementRestitutedPosition> selectRestitutedPositions(final Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		return this.getRestitutedPositionDetails(
				this.selectRestitutedPositionsBySalespointsAndDateRange(salespoints, dateRange), null);
	}

	public List<SettlementInternal> selectInternals(final Settlement settlement)
	{
		List<ReportQueryResult> results = this.selectInternalsBySettlement(settlement);
		List<SettlementInternal> internals = this.getInternalsDetails(results, settlement);
		return internals;
	}

	public List<SettlementInternal> selectInternals(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		List<ReportQueryResult> results = this.selectInternalsBySalespointsAndDateRange(salespoints, dateRange);
		List<SettlementInternal> internals = this.getInternalsDetails(results, null);
		return internals;
	}

//	public List<SettlementInternal> selectInternals(final Salespoint[] salespoints, Calendar[] dateRange)
//	{
//		return this.getInternalDetails(this.selectInternalsBySalespointsAndDateRange(salespoints, dateRange), null);
//	}

	public List<Position> selectProviderUpdates(final Salespoint salespoint, String providerId, boolean updateServer, final int maxRows)
	{
		final Expression select = createProviderUpdatesExpression(salespoint, providerId, updateServer);
		final List<Position> positions = this.select(select, maxRows);
		return positions;
	}

	public List<SettlementTax> selectTaxes(final Settlement settlement)
	{
		return this.getTaxDetails(this.selectTaxesBySettlement(settlement), settlement);
	}

	public Collection<SettlementTax> selectTaxes(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		return this.getTaxDetails(this.selectTaxesBySalespointsAndDateRange(salespoints, dateRange), null);
	}

	public double sumCurrent(final ProductGroupType productGroupType)
	{
		Double amount = null;
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.get("productGroup").get("productGroupType").equal(productGroupType);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		final Expression settled = new ExpressionBuilder().get("receipt").get("settlement").get("settled").isNull();
		expression = expression.and(settled);

		Collection<Position> positions = this.select(expression);
		if (!positions.isEmpty())
		{
			final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
			reportQuery.addSum("amount", this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
			reportQuery.setShouldReturnSingleResult(true);
			Query query = createQuery(this.getConnectionService().getEntityManager(), reportQuery, 0);
			ReportQueryResult result = (ReportQueryResult) query.getSingleResult();
			amount = (Double) result.get("amount");
		}
		return amount == null ? 0D : amount.doubleValue();
	}

	public double sumCurrent(final ProductGroupType productGroupType, final Salespoint salespoint)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.get("productGroup").get("productGroupType").equal(productGroupType);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoint));
		final Expression settled = new ExpressionBuilder().get("receipt").get("settlement").get("settled").isNull();
		expression = expression.and(settled);

//		Collection<Position> positions = this.select(expression);
		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addSum("amount", this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
		reportQuery.setShouldReturnSingleResult(true);

		final Query query = JpaHelper.createQuery(reportQuery, this.getConnectionService().getEntityManager());
//		Double result = (Double) query.getSingleResult();
		ReportQueryResult result = (ReportQueryResult) query.getSingleResult();
		Double amount = (Double) result.get("amount");
		return amount == null ? 0D : amount.doubleValue();
	}

	public double sumCurrent(final ProductGroupType productGroupType, final User user)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.get("productGroup").get("productGroupType").equal(productGroupType);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("user").equal(user));
		final Expression settled = new ExpressionBuilder().get("receipt").get("settlement").get("settled").isNull();
		expression = expression.and(settled);

//		Collection<Position> positions = this.select(expression);
		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addSum("amount", this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO));
		reportQuery.setShouldReturnSingleResult(true);

		final Query query = JpaHelper.createQuery(reportQuery, this.getConnectionService().getEntityManager());
//		Double result = (Double) query.getSingleResult();
		ReportQueryResult result = (ReportQueryResult) query.getSingleResult();
		Double amount = (Double) result.get("amount");
		return amount == null ? 0D : amount.doubleValue();
	}

	@Override
	protected Class<Position> getEntityClass()
	{
		return Position.class;
	}

	private Expression amount(final AmountType amountType)
	{
		switch (amountType)
		{
			case BRUTTO:
			{
				return this.brutto();
			}
			case NETTO:
			{
				return this.netto();
			}
			case DISCOUNT:
			{
				return this.discount();
			}
			default:
				throw new RuntimeException("Invalid quotation type");
		}
	}

	private Expression brutto()
	{
		final Expression quantity = new ExpressionBuilder().get("quantity");
		final Expression price = new ExpressionBuilder().get("price");
		return ExpressionMath.multiply(quantity, price);
	}

	private Expression decimals(final Currency currency)
	{
		// final Expression digits = new
		// ExpressionBuilder().value(currency.getCurrency().getDefaultFractionDigits());
		// final Expression base = new ExpressionBuilder().value(10D);
		// return ExpressionMath.power(base, digits);
		return new ExpressionBuilder().value(.01D);
	}

	private Expression discount()
	{
		final Expression discount = ExpressionMath.negate(new ExpressionBuilder().get("discount"));
		return ExpressionMath.multiply(this.brutto(), discount);
	}

	private Expression getAmount(final Receipt.QuotationType quotationType, final AmountType amountType)
	{
		final Expression amount = this.amount(amountType);
		final Expression quotation = this.quotation(quotationType);
		final Expression product = ExpressionMath.multiply(amount, quotation);
		return this.round(product, quotationType);
	}

	private CommonSettings getCommonSettings()
	{
		if (this.settings == null)
		{
			final CommonSettingsQuery query = (CommonSettingsQuery) this.getConnectionService().getQuery(
					CommonSettings.class);
			this.settings = query.findDefault();
		}
		return this.settings;
	}

	private List<SettlementPosition> getPositionsDetails(final Collection<ReportQueryResult> results,
			final Settlement settlement)
	{
		final List<SettlementPosition> details = new ArrayList<SettlementPosition>();
		for (final ReportQueryResult result : results)
		{
			Long id = (Long) result.get("productGroup");
			final ProductGroup productGroup = (ProductGroup) this.getConnectionService().find(ProductGroup.class, id);

			Currency defaultCurrency = null;
			if (settlement == null)
			{
				defaultCurrency = this.getConnectionService().getEntityManager().find(Currency.class, result.get("defaultCurrency"));
			}
			else
			{
				defaultCurrency = settlement.getSalespoint().getPaymentType().getCurrency();
			}

//			System.out.println(productGroup.getId() + ", " + productGroup.getName() + ", "
//					+ productGroup.getProductGroupType().toString());

			final SettlementPosition detail = SettlementPosition.newInstance(settlement, productGroup, defaultCurrency);
			detail.setProductGroupType(productGroup.getProductGroupType());

			final Integer quantity = (Integer) result.get("quantity");
			detail.setQuantity(quantity == null ? 0 : quantity);

			Double amount = (Double) result.get("defaultCurrencyAmount");
			detail.setDefaultCurrencyAmount(amount == null ? 0D : amount);

			amount = (Double) result.get("taxAmount");
			detail.setTaxAmount(amount == null ? 0D : amount);
			details.add(detail);
		}
		return details;
	}

	private List<SettlementInternal> getInternalsDetails(final Collection<ReportQueryResult> results,
			final Settlement settlement)
	{
		final List<SettlementInternal> details = new ArrayList<SettlementInternal>();
		for (final ReportQueryResult result : results)
		{
			Long id = (Long) result.get("productGroup");
			final ProductGroup productGroup = (ProductGroup) this.getConnectionService().find(ProductGroup.class, id);

			Currency defaultCurrency = null;
			if (settlement == null)
			{
				defaultCurrency = this.getConnectionService().getEntityManager().find(Currency.class, result.get("defaultCurrency"));
			}
			else
			{
				defaultCurrency = settlement.getSalespoint().getPaymentType().getCurrency();
			}

//			System.out.println(productGroup.getId() + ", " + productGroup.getName() + ", "
//					+ productGroup.getProductGroupType().toString());

			final SettlementInternal detail = SettlementInternal.newInstance(settlement, productGroup, defaultCurrency);
			detail.setProductGroupType(productGroup.getProductGroupType());

			final Integer quantity = (Integer) result.get("quantity");
			detail.setQuantity(quantity == null ? 0 : quantity);

			Double amount = (Double) result.get("defaultCurrencyAmount");
			detail.setDefaultCurrencyAmount(amount == null ? 0D : amount);
			details.add(detail);
		}
		return details;
	}

	private List<DiscountEntry> getDiscountEntries(final Collection<ReportQueryResult> results)
	{
		DateFormat format = new SimpleDateFormat("yyyyMMdd");
		final Map<Long, Map<String, DiscountEntry>> entries = new HashMap<Long, Map<String, DiscountEntry>>();
		for (final ReportQueryResult result : results)
		{
			Long id = (Long)result.get("salespointId");
			Map<String, DiscountEntry> salespoint = entries.get(id);
			if (salespoint == null)
			{
				salespoint = new HashMap<String, DiscountEntry>();
				entries.put(id, salespoint);
			}
			Calendar date = (Calendar)result.get("date");
			String key = format.format(date.getTime());
			DiscountEntry entry = salespoint.get(key);
			if (entry == null)
			{
				entry = new DiscountEntry();
				salespoint.put(key, entry);
			}
			Double brutAmount = (Double)result.get("brutAmount");
			Double existingBrutAmount = entry.getFullAmount();
			double newBrutAmount = (brutAmount == null ? 0D : brutAmount.doubleValue()) + (existingBrutAmount == null ? 0D : existingBrutAmount.doubleValue());
			Double netAmount = (Double)result.get("netAmount");
			Double existingNetAmount = entry.getAmount();
			double newNetAmount = (netAmount == null ? 0D : netAmount.doubleValue()) + (existingNetAmount == null ? 0D : existingNetAmount.doubleValue());
			entry.setAmount(Double.valueOf(newNetAmount));
			entry.setFullAmount(Double.valueOf(newBrutAmount));
			entry.setDiscount(newBrutAmount - newNetAmount);
			entry.setPercent(1 - newNetAmount/newBrutAmount);
			entry.setDay(date.get(Calendar.DAY_OF_MONTH));
			entry.setMonth(date.get(Calendar.MONTH));
			entry.setYear(date.get(Calendar.YEAR));
			entry.setSalespoint((String)result.get("salespointName"));
		}
		List<DiscountEntry> allEntries = new ArrayList<DiscountEntry>();
		Collection<Map<String, DiscountEntry>> salespoints = entries.values();
		for (Map<String, DiscountEntry> s : salespoints)
		{
			allEntries.addAll(s.values());
		}
		return allEntries;
	}

//	private List<SettlementInternal> getInternalsDetails(final Collection<ReportQueryResult> results,
//			final Settlement settlement)
//	{
//		final List<SettlementInternal> details = new ArrayList<SettlementInternal>();
//		for (final ReportQueryResult result : results)
//		{
//			Long id = (Long) result.get("productGroup");
//			final ProductGroup productGroup = (ProductGroup) this.getConnectionService().find(ProductGroup.class, id);
//
//			Currency defaultCurrency = null;
//			if (settlement == null)
//			{
//				defaultCurrency = this.getConnectionService().getEntityManager().find(Currency.class, result.get("defaultCurrency"));
//			}
//			else
//			{
//				defaultCurrency = settlement.getSalespoint().getPaymentType().getCurrency();
//			}
//
////			System.out.println(productGroup.getId() + ", " + productGroup.getName() + ", "
////					+ productGroup.getProductGroupType().toString());
//
//			final SettlementInternal detail = SettlementInternal.newInstance(settlement, productGroup, defaultCurrency);
//			detail.setProductGroupType(productGroup.getProductGroupType());
//
//			final Integer quantity = (Integer) result.get("quantity");
//			detail.setQuantity(quantity == null ? 0 : quantity);
//
//			Double amount = (Double) result.get("defaultCurrencyAmount");
//			detail.setDefaultCurrencyAmount(amount == null ? 0D : amount);
//
//			amount = (Double) result.get("taxAmount");
//			detail.setTaxAmount(amount == null ? 0D : amount);
//			details.add(detail);
//		}
//		return details;
//	}

	private List<SettlementPayedInvoice> getPayedInvoicesDetails(final Collection<Position> positions,
			final Settlement settlement)
	{
		final List<SettlementPayedInvoice> payedInvoices = new ArrayList<SettlementPayedInvoice>();
		for (final Position position : positions)
		{
			final SettlementPayedInvoice payedInvoice = SettlementPayedInvoice.newInstance(settlement, position);
			payedInvoices.add(payedInvoice);
		}
		return payedInvoices;
	}

	private List<SettlementRestitutedPosition> getRestitutedPositionDetails(final Collection<Position> positions,
			final Settlement settlement)
	{
		final List<SettlementRestitutedPosition> details = new ArrayList<SettlementRestitutedPosition>();
		for (final Position position : positions)
		{
			final SettlementRestitutedPosition detail = SettlementRestitutedPosition.newInstance(settlement, position);
			details.add(detail);
		}
		return details;
	}

//	private List<SettlementInternal> getInternalDetails(final List<Position> positions,
//			final Settlement settlement)
//	{
//		final List<SettlementInternal> details = new ArrayList<SettlementInternal>();
//		for (final Position position : positions)
//		{
//			final SettlementInternal detail = SettlementInternal.newInstance(settlement, position);
//			details.add(detail);
//		}
//		return details;
//	}

	public Map<String, Map<Long, ProductGroupEntry>> selectProductGroupStatistics(Salespoint[] salespoints, Calendar[] dateRange,
			String provider, boolean withExpenses, boolean withOtherSales, boolean previousYear)
	{
		Expression state = new ExpressionBuilder(getEntityClass()).get("receipt").get("state").equal(Receipt.State.SAVED);
		if (provider != null && !provider.isEmpty())
		{
			Expression productGroup = new ExpressionBuilder().get("productGroup").anyOfAllowingNone("productGroupMappings").get("provider").equal(provider);
			state.and(productGroup);
		}
		Expression sales = new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.SALES_RELATED);
		if (withOtherSales)
		{
			sales = sales.or(new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.NON_SALES_RELATED));
		}
		if (withExpenses)
		{
			sales = sales.or(new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.EXPENSES_INVESTMENT));
			sales = sales.or(new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.EXPENSES_MATERIAL));
		}

		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		Expression dates = null;
		if (dateRange[0] != null && dateRange[1] != null)
		{
			dates = new ExpressionBuilder().get("receipt").get("timestamp").between(dateRange[0], dateRange[1]);
		}
		else if (dateRange[0] != null)
		{
			dates = new ExpressionBuilder().get("receipt").get("timestamp").greaterThanEqual(dateRange[0]);
		}
		else if (dateRange[1] != null)
		{
			dates = new ExpressionBuilder().get("receipt").get("timestamp").lessThanEqual(dateRange[1]);
		}
		Expression expression = state.and(sales).and(dates).and(sps);

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		expression = expression.and(deleted);

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
//		reportQuery.addAttribute("salespointId", new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
//		reportQuery.addAttribute("salespointName", new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addAttribute("provider", new ExpressionBuilder().get("provider"));
		reportQuery.addAttribute("productGroupId", new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addAttribute("productGroupName", new ExpressionBuilder().get("productGroup").get("name"));
		reportQuery.addAttribute("ordered", new ExpressionBuilder().get("ordered"));

		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("amount", this.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO), Double.class);

//		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
//		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addOrdering(new ExpressionBuilder().get("provider"));
		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("name"));
		reportQuery.addOrdering(new ExpressionBuilder().get("ordered"));

//		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
//		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addGrouping(new ExpressionBuilder().get("provider"));
		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("name"));
		reportQuery.addGrouping(new ExpressionBuilder().get("ordered"));

		List<ReportQueryResult> results = this.selectReportQueryResults(reportQuery);
		Map<String, Map<Long, ProductGroupEntry>> entries = new HashMap<String, Map<Long, ProductGroupEntry>>();
		for (ReportQueryResult result : results)
		{
			String providerId = (String) result.get("provider");
			Map<Long, ProductGroupEntry> productGroups = entries.get(providerId);
			if (productGroups == null)
			{
				productGroups = new HashMap<Long, ProductGroupEntry>();
				entries.put(providerId, productGroups);
			}
			Long productGroupId = (Long) result.get("productGroupId");
			ProductGroupEntry entry = productGroups.get(productGroupId);
			if (entry == null)
			{
				entry = new ProductGroupEntry();
				productGroups.put(productGroupId, entry);
			}
			Boolean ordered = (Boolean)result.get("ordered");
			entry.setProductGroupId(productGroupId);
			entry.setProductGroupName(result.get("productGroupName").toString());
			entry.setProviderId(providerId);
			double amount = ((Double) result.get("amount")).doubleValue();
			int quantity = ((Integer) result.get("quantity")).intValue();
			if (ordered)
			{
				double existingAmount = entry.getOrderAmount();
				entry.setOrderAmount(Double.valueOf(existingAmount + amount));
				int existingQuantity = entry.getOrderQuantity();
				entry.setOrderQuantity(Integer.valueOf(existingQuantity + quantity));
			}
			else
			{
				double existingAmount = entry.getStockAmount();
				entry.setStockAmount(Double.valueOf(existingAmount + amount));
				int existingQuantity = entry.getStockQuantity();
				entry.setStockQuantity(Integer.valueOf(existingQuantity + quantity));
			}
			double existingAmount = entry.getTotalAmount();
			entry.setTotalAmount(Double.valueOf(existingAmount + amount));
			int existingQuantity = entry.getTotalQuantity();
			entry.setTotalQuantity(Integer.valueOf(existingQuantity + quantity));
		}
		
		return entries;
	}
	
	public Map<Long, Map<String, Map<Long, ProductGroupEntry>>> selectProductGroupStatisticsBySalespoint(Salespoint[] salespoints, Calendar[] dateRange,
			String provider, boolean withExpenses, boolean withOtherSales, boolean previousYear)
	{
		Expression state = new ExpressionBuilder(getEntityClass()).get("receipt").get("state").equal(Receipt.State.SAVED);
		if (provider != null && !provider.isEmpty())
		{
			Expression productGroup = new ExpressionBuilder().get("productGroup").anyOfAllowingNone("productGroupMappings").get("provider").equal(provider);
			state.and(productGroup);
		}
		Expression sales = new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.SALES_RELATED);
		if (withOtherSales)
		{
			sales = sales.or(new ExpressionBuilder().get("productGroup").get("productGroupType").notEqual(ProductGroupType.NON_SALES_RELATED));
		}
		if (withExpenses)
		{
			sales = sales.or(new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.EXPENSES_INVESTMENT));
			sales = sales.or(new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.EXPENSES_MATERIAL));
		}

		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		Expression dates = null;
		if (dateRange[0] != null && dateRange[1] != null)
		{
			dates = new ExpressionBuilder().get("receipt").get("timestamp").between(dateRange[0], dateRange[1]);
		}
		else if (dateRange[0] != null)
		{
			dates = new ExpressionBuilder().get("receipt").get("timestamp").greaterThanEqual(dateRange[0]);
		}
		else if (dateRange[1] != null)
		{
			dates = new ExpressionBuilder().get("receipt").get("timestamp").lessThanEqual(dateRange[1]);
		}
		Expression expression = state.and(sales).and(dates).and(sps);

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		expression = expression.and(deleted);

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("salespointId", new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
		reportQuery.addAttribute("salespointName", new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addAttribute("provider", new ExpressionBuilder().get("provider"));
		reportQuery.addAttribute("provider", new ExpressionBuilder().get("provider"));
		reportQuery.addAttribute("productGroupId", new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addAttribute("productGroupName", new ExpressionBuilder().get("productGroup").get("name"));
		reportQuery.addAttribute("ordered", new ExpressionBuilder().get("ordered"));

		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("amount", this.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO), Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addOrdering(new ExpressionBuilder().get("provider"));
		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("name"));
		reportQuery.addOrdering(new ExpressionBuilder().get("ordered"));

		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addGrouping(new ExpressionBuilder().get("provider"));
		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("name"));
		reportQuery.addGrouping(new ExpressionBuilder().get("ordered"));

		List<ReportQueryResult> results = this.selectReportQueryResults(reportQuery);
		Map<Long, Map<String, Map<Long, ProductGroupEntry>>> entries = new HashMap<Long, Map<String, Map<Long, ProductGroupEntry>>>();
		for (ReportQueryResult result : results)
		{
			Long salespointId = (Long) result.get("salespointId");
			Map<String, Map<Long, ProductGroupEntry>> salespointMap = entries.get(salespointId);
			if (salespointMap == null)
			{
				salespointMap = new HashMap<String, Map<Long, ProductGroupEntry>>();
				entries.put(salespointId, salespointMap);
			}
			String providerId = (String) result.get("provider");
			Map<Long, ProductGroupEntry> productGroups = salespointMap.get(providerId);
			if (productGroups == null)
			{
				productGroups = new HashMap<Long, ProductGroupEntry>();
				salespointMap.put(providerId, productGroups);
			}
			Long productGroupId = (Long) result.get("productGroupId");
			ProductGroupEntry entry = productGroups.get(productGroupId);
			if (entry == null)
			{
				entry = new ProductGroupEntry();
				productGroups.put(productGroupId, entry);
			}
			Boolean ordered = (Boolean)result.get("ordered");
			entry.setSalespointId(salespointId);
			entry.setSalespointName(result.get("salespointName").toString());
			entry.setProductGroupId(productGroupId);
			entry.setProductGroupName(result.get("productGroupName").toString());
			entry.setProviderId(providerId);
			double amount = ((Double) result.get("amount")).doubleValue();
			int quantity = ((Integer) result.get("quantity")).intValue();
			if (ordered)
			{
				double existingAmount = entry.getOrderAmount();
				entry.setOrderAmount(Double.valueOf(existingAmount + amount));
				int existingQuantity = entry.getOrderQuantity();
				entry.setOrderQuantity(Integer.valueOf(existingQuantity + quantity));
			}
			else
			{
				double existingAmount = entry.getStockAmount();
				entry.setStockAmount(Double.valueOf(existingAmount + amount));
				int existingQuantity = entry.getStockQuantity();
				entry.setStockQuantity(Integer.valueOf(existingQuantity + quantity));
			}
			double existingAmount = entry.getTotalAmount();
			entry.setTotalAmount(Double.valueOf(existingAmount + amount));
			int existingQuantity = entry.getTotalQuantity();
			entry.setTotalQuantity(Integer.valueOf(existingQuantity + quantity));
		}
		
		return entries;
	}
	
	private Expression getTaxAmount(final Currency currency, final Receipt.QuotationType quotationType)
	{
		final Expression base = this.getAmount(quotationType, AmountType.NETTO);
		final Expression product = ExpressionMath.multiply(base, this.taxFactor());
		final Expression result = this.round(product, currency);
		return result;
	}

	private List<SettlementTax> getTaxDetails(final Collection<ReportQueryResult> results,
			final Settlement settlement)
	{
		final List<SettlementTax> details = new ArrayList<SettlementTax>();
		for (final ReportQueryResult result : results)
		{
			final Long id = (Long) result.get("currentTax");
			final CurrentTax currentTax = (CurrentTax) this.getConnectionService().find(CurrentTax.class, id);

			final SettlementTax detail = SettlementTax.newInstance(settlement, currentTax);
			final Integer quantity = (Integer) result.get("quantity");
			detail.setQuantity(quantity == null ? 0 : quantity);
			Double amount = (Double) result.get("baseAmount");
			detail.setBaseAmount(amount == null ? 0D : amount);
			amount = (Double) result.get("taxAmount");
			System.out.println(amount);
			detail.setTaxAmount(amount == null ? 0D : amount);
			details.add(detail);
		}
		return details;
	}

	private Expression netto()
	{
		final Expression constant = new ExpressionBuilder().value(1D);
		final Expression discount = ExpressionMath.add(constant, new ExpressionBuilder().get("discount"));
		final Expression netto = ExpressionMath.multiply(this.brutto(), discount);
		return netto;
	}

	private Expression quotation(final Receipt.QuotationType quotationType)
	{
		switch (quotationType)
		{
			case REFERENCE_CURRENCY:
			{
				final Expression fcQuotation = new ExpressionBuilder().get("foreignCurrencyQuotation");
				final Expression rcQuotation = new ExpressionBuilder().get("receipt").get("referenceCurrencyQuotation");
				return ExpressionMath.divide(fcQuotation, rcQuotation);
			}
			case DEFAULT_CURRENCY:
			{
				final Expression fcQuotation = new ExpressionBuilder().get("foreignCurrencyQuotation");
				final Expression dcQuotation = new ExpressionBuilder().get("receipt").get("defaultCurrencyQuotation");
				return ExpressionMath.divide(fcQuotation, dcQuotation);
			}
			case FOREIGN_CURRENCY:
			{
				return new ExpressionBuilder().value(1);
			}
			case DEFAULT_FOREIGN_CURRENCY:
			{
				final Expression fcQuotation = new ExpressionBuilder().get("foreignCurrencyQuotation");
				final Expression rfcQuotation = new ExpressionBuilder().get("receipt").get("foreignCurrencyQuotation");
				return ExpressionMath.divide(fcQuotation, rfcQuotation);
			}
			default:
				throw new RuntimeException("Invalid quotation type");
		}
	}

	private Expression round(final Expression amount, final Currency currency)
	{
		final Expression power = this.decimals(currency);
		final Expression product = ExpressionMath.divide(amount, power);
		final Expression up = new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR));
		final Expression round = ExpressionMath.floor(ExpressionMath.add(product, up));
		final Expression value = ExpressionMath.multiply(round, this.decimals(currency));
		return value;
	}

	private Expression round(final Expression amount, final Receipt.QuotationType quotationType)
	{
		switch (quotationType)
		{
			case REFERENCE_CURRENCY:
			{
				final Expression divisor = new ExpressionBuilder().get("receipt").get("referenceCurrencyRoundFactor");
				final Expression factor = new ExpressionBuilder().get("receipt").get("referenceCurrencyRoundFactor");
				final Expression quotient = ExpressionMath.divide(amount, divisor);
				final Expression round = ExpressionMath.floor(ExpressionMath.add(quotient,
						new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR))));
				final Expression product = ExpressionMath.multiply(round, factor);
				return product;
			}
			case DEFAULT_CURRENCY:
			{
				final Expression divisor = new ExpressionBuilder().get("receipt").get("defaultCurrencyRoundFactor");
				final Expression factor = new ExpressionBuilder().get("receipt").get("defaultCurrencyRoundFactor");
				final Expression quotient = ExpressionMath.divide(amount, divisor);
				final Expression round = ExpressionMath.floor(ExpressionMath.add(quotient,
						new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR))));
				final Expression product = ExpressionMath.multiply(round, factor);
				return product;
			}
			case FOREIGN_CURRENCY:
			{
				final Expression divisor = new ExpressionBuilder().get("foreignCurrencyRoundFactor");
				final Expression factor = new ExpressionBuilder().get("foreignCurrencyRoundFactor");
				final Expression quotient = ExpressionMath.divide(amount, divisor);
				final Expression round = ExpressionMath.floor(ExpressionMath.add(quotient,
						new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR))));
				final Expression product = ExpressionMath.multiply(round, factor);
				return product;
			}
			case DEFAULT_FOREIGN_CURRENCY:
			{
				final Expression divisor = new ExpressionBuilder().get("receipt").get("foreignCurrencyRoundFactor");
				final Expression factor = new ExpressionBuilder().get("receipt").get("foreignCurrencyRoundFactor");
				final Expression quotient = ExpressionMath.divide(amount, divisor);
				final Expression round = ExpressionMath.floor(ExpressionMath.add(quotient,
						new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR))));
				final Expression product = ExpressionMath.multiply(round, factor);
				return product;
			}
			default:
				throw new RuntimeException("Invalid quotation type");
		}
	}

	private List<ReportQueryResult> selectPositionsBySalespointsAndDateRange(Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<ReportQueryResult>();
		}

		Currency currency = salespoints[0].getCommonSettings().getReferenceCurrency();

		Expression state = new ExpressionBuilder(getEntityClass()).get("receipt").get("state").equal(Receipt.State.SAVED);
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		Expression expression = state.and(deleted);
		
		Expression allocation = new ExpressionBuilder().get("productGroup").get("productGroupType").notEqual(ProductGroupType.ALLOCATION);
		Expression withdrawal = new ExpressionBuilder().get("productGroup").get("productGroupType").notEqual(ProductGroupType.WITHDRAWAL);
		Expression internal = allocation.and(withdrawal);
		expression = expression.and(internal);
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		expression = expression.and(sps);

		if (dateRange[0] != null && dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.between(dateRange[0], dateRange[1]));
		}
		else if (dateRange[0] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.greaterThanEqual(dateRange[0]));
		}
		else if (dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.lessThanEqual(dateRange[1]));
		}

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("productGroup", new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addAttribute("defaultCurrency",
				new ExpressionBuilder().get("receipt").get("defaultCurrency").get("id"));
		reportQuery.addAttribute("foreignCurrency", new ExpressionBuilder().get("foreignCurrency").get("id"));

		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("foreignCurrencyAmount",
				this.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO), Double.class);
		reportQuery.addSum("defaultCurrencyAmount",
				this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO), Double.class);
		reportQuery.addSum("taxAmount", this.getTaxAmount(currency, Receipt.QuotationType.DEFAULT_CURRENCY),
				Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("id").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("defaultCurrency").get("id").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("foreignCurrency").get("id").ascending());

		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("defaultCurrency").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("foreignCurrency").get("id"));

		return this.selectReportQueryResults(reportQuery);
	}


	public List<ReportQueryResult> selectPositionsBySalespointsAndDateRangeAndDiscount(Salespoint[] salespoints,
			Calendar[] dateRange, boolean onlyWithDiscount)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<ReportQueryResult>();
		}

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression state = new ExpressionBuilder(getEntityClass()).get("receipt").get("state").equal(Receipt.State.SAVED);
		Expression expression = state.and(deleted);

		if (onlyWithDiscount)
		{
			Expression discount = new ExpressionBuilder().get("discount").notEqual(0D);
			expression = expression.and(discount);
		}
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		expression = expression.and(sps);

		if (dateRange[0] != null && dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.between(dateRange[0], dateRange[1]));
		}
		else if (dateRange[0] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.greaterThanEqual(dateRange[0]));
		}
		else if (dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.lessThanEqual(dateRange[1]));
		}

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("salespointId", new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
		reportQuery.addAttribute("salespointName", new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addAttribute("date", new ExpressionBuilder().get("receipt").get("timestamp"));
//		reportQuery.addAttribute("foreignCurrency", new ExpressionBuilder().get("foreignCurrency").get("id"));

		reportQuery.addSum("brutAmount",
				this.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.BRUTTO), Double.class);
		reportQuery.addSum("netAmount",
				this.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO), Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("timestamp"));

		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("timestamp"));

		return this.selectReportQueryResults(reportQuery);
	}

	public List<Position> selectBySalespointsAndDateRange(Salespoint[] salespoints, Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<Position>();
		}

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression state = new ExpressionBuilder(getEntityClass()).get("receipt").get("state").equal(Receipt.State.SAVED);
		Expression expression = state.and(deleted);
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		expression = expression.and(sps);

		if (dateRange[0] != null && dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.between(dateRange[0], dateRange[1]));
		}
		else if (dateRange[0] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.greaterThanEqual(dateRange[0]));
		}
		else if (dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.lessThanEqual(dateRange[1]));
		}

		return this.select(expression);
	}

	private List<ReportQueryResult> selectInternalsBySalespointsAndDateRange(Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<ReportQueryResult>();
		}

		Currency currency = salespoints[0].getCommonSettings().getReferenceCurrency();

		Expression state = new ExpressionBuilder(getEntityClass()).get("receipt").get("state").equal(Receipt.State.SAVED);
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		Expression expression = state.and(deleted);
		
		Expression allocation = new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.ALLOCATION);
		Expression withdrawal = new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.WITHDRAWAL);
		Expression internal = allocation.or(withdrawal);
		expression = expression.and(internal);
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		expression = expression.and(sps);

		if (dateRange[0] != null && dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.between(dateRange[0], dateRange[1]));
		}
		else if (dateRange[0] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.greaterThanEqual(dateRange[0]));
		}
		else if (dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.lessThanEqual(dateRange[1]));
		}

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("productGroup", new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addAttribute("defaultCurrency",
				new ExpressionBuilder().get("receipt").get("defaultCurrency").get("id"));
		reportQuery.addAttribute("foreignCurrency", new ExpressionBuilder().get("foreignCurrency").get("id"));

		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("foreignCurrencyAmount",
				this.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY, Position.AmountType.NETTO), Double.class);
		reportQuery.addSum("defaultCurrencyAmount",
				this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO), Double.class);
		reportQuery.addSum("taxAmount", this.getTaxAmount(currency, Receipt.QuotationType.DEFAULT_CURRENCY),
				Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("id").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("defaultCurrency").get("id").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("foreignCurrency").get("id").ascending());

		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("defaultCurrency").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("foreignCurrency").get("id"));

		return this.selectReportQueryResults(reportQuery);
	}

	private Collection<Position> selectPayedInvoicesBySalespointsAndDateRange(Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<Position>();
		}

		ProductGroup payedInvoice = salespoints[0].getCommonSettings().getPayedInvoice();

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression state = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
				.equal(Receipt.State.SAVED);
		Expression expression = state.and(deleted);
		
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		expression = expression.and(sps);

		Expression pi = new ExpressionBuilder().get("option").equal(Position.Option.PAYED_INVOICE);
		if (payedInvoice != null)
		{
			pi = pi.or(new ExpressionBuilder().get("productGroup").equal(payedInvoice));
		}
		expression = expression.and(pi);

		if (dateRange[0] != null && dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.between(dateRange[0], dateRange[1]));
		}
		else if (dateRange[0] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.greaterThanEqual(dateRange[0]));
		}
		else if (dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.lessThanEqual(dateRange[1]));
		}

		Collection<Position> positions = super.select(expression);
		return positions;
	}

	private List<ReportQueryResult> selectPositionsBySettlement(final Settlement settlement)
	{
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(deleted);
		
		Expression allocation = new ExpressionBuilder().get("productGroup").get("productGroupType").notEqual(ProductGroupType.ALLOCATION);
		Expression withdrawal = new ExpressionBuilder().get("productGroup").get("productGroupType").notEqual(ProductGroupType.WITHDRAWAL);
		Expression internal = allocation.and(withdrawal);
		expression = expression.and(internal);

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("productGroupType", new ExpressionBuilder().get("productGroup")
				.get("productGroupType"));
		reportQuery.addAttribute("productGroup", new ExpressionBuilder().get("productGroup").get("id"));

		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("taxAmount", this.getTaxAmount(settlement.getSalespoint().getPaymentType().getCurrency(),
				Receipt.QuotationType.DEFAULT_CURRENCY), Double.class);
		reportQuery.addSum("defaultCurrencyAmount",
				this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO), Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("productGroupType").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("id").ascending());

		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("productGroupType"));
		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("id"));

		List<ReportQueryResult> result = super.selectReportQueryResults(reportQuery);
		return result;
	}

	private List<ReportQueryResult> selectInternalsBySettlement(final Settlement settlement)
	{
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(deleted);
		
		Expression allocation = new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.ALLOCATION);
		Expression withdrawal = new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.WITHDRAWAL);
		Expression internal = allocation.or(withdrawal);
		expression = expression.and(internal);

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("productGroupType", new ExpressionBuilder().get("productGroup")
				.get("productGroupType"));
		reportQuery.addAttribute("productGroup", new ExpressionBuilder().get("productGroup").get("id"));

		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("taxAmount", this.getTaxAmount(settlement.getSalespoint().getPaymentType().getCurrency(),
				Receipt.QuotationType.DEFAULT_CURRENCY), Double.class);
		reportQuery.addSum("defaultCurrencyAmount",
				this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO), Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("productGroupType").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("productGroup").get("id").ascending());

		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("productGroupType"));
		reportQuery.addGrouping(new ExpressionBuilder().get("productGroup").get("id"));

		List<ReportQueryResult> result = super.selectReportQueryResults(reportQuery);
		return result;
	}

	private Collection<Position> selectPayedInvoicesBySettlement(final Settlement settlement)
	{
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(deleted);
		
		ProductGroup payedInvoiceGroup = settlement.getSalespoint().getCommonSettings().getPayedInvoice();
		if (payedInvoiceGroup == null)
		{
			expression = expression.and(new ExpressionBuilder().get("option").equal(Option.PAYED_INVOICE));
		}
		else
		{
			expression = expression.and(new ExpressionBuilder().get("productGroup").equal(payedInvoiceGroup));
		}
		return this.select(expression);
	}

//	private List<Position> selectInternalsBySettlement(final Settlement settlement)
//	{
//		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
//		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
//
//		Expression expression = new ExpressionBuilder(this.getEntityClass());
//		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
//		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
//		expression = expression.and(deleted);
//		
//		Expression alloc = new ExpressionBuilder().get("productGroup").get("productGroupType")
//				.equal(ProductGroupType.ALLOCATION);
//		Expression withd = new ExpressionBuilder().get("productGroup").get("productGroupType")
//				.equal(ProductGroupType.WITHDRAWAL);
//
//		expression = expression.and(alloc.or(withd));
//		List<Position> internals = this.select(expression);
//		return internals;
//	}

//	private List<Position> selectInternalsBySalespointsAndDateRange(final Salespoint[] salespoints,
//			Calendar[] dateRange)
//	{
//		if (salespoints == null || salespoints.length == 0)
//		{
//			return new ArrayList<Position>();
//		}
//
//		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
//		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
//
//		Expression state = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
//				.equal(Receipt.State.SAVED);
//		Expression expression = state.and(deleted);
//
//		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
//				.equal(salespoints[0]);
//		for (int i = 1; i < salespoints.length; i++)
//		{
//			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
//					.equal(salespoints[i]));
//		}
//		expression = expression.and(sps);
//
//		if (dateRange[0] != null && dateRange[1] != null)
//		{
//			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
//					.between(dateRange[0], dateRange[1]));
//		}
//		else if (dateRange[0] != null)
//		{
//			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
//					.greaterThanEqual(dateRange[0]));
//		}
//		else if (dateRange[1] != null)
//		{
//			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
//					.lessThanEqual(dateRange[1]));
//		}
//
//		Expression alloc = new ExpressionBuilder().get("productGroup").get("productGroupType")
//				.equal(ProductGroupType.ALLOCATION);
//		Expression withd = new ExpressionBuilder().get("productGroup").get("productGroupType")
//				.equal(ProductGroupType.WITHDRAWAL);
//
//		expression = expression.and(alloc.or(withd));
//
//		List<Position> internals = this.select(expression);
//		return internals;
//	}

	private List<Position> selectRestitutedPositionsBySettlement(final Settlement settlement)
	{
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(deleted);
		
		expression = expression.and(new ExpressionBuilder().get("quantity").lessThan(0));

		Expression restitution = new ExpressionBuilder().get("productGroup").get("productGroupType")
				.equal(ProductGroupType.SALES_RELATED);
		restitution = restitution.or(new ExpressionBuilder().get("productGroup").get("productGroupType")
				.equal(ProductGroupType.NON_SALES_RELATED));
		expression = expression.and(restitution);
		return this.select(expression);
	}

	private Collection<Position> selectRestitutedPositionsBySalespointsAndDateRange(final Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<Position>();
		}

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression state = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state").equal(Receipt.State.SAVED);
		Expression expression = state.and(deleted);
		
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		expression = expression.and(sps);
		expression = expression.and(new ExpressionBuilder().get("quantity").lessThan(0));

		Expression restitution = new ExpressionBuilder().get("productGroup").get("productGroupType")
				.equal(ProductGroupType.SALES_RELATED);
		restitution = restitution.or(new ExpressionBuilder().get("productGroup").get("productGroupType")
				.equal(ProductGroupType.NON_SALES_RELATED));
		expression = expression.and(restitution);

		if (dateRange[0] != null && dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.between(dateRange[0], dateRange[1]));
		}
		else if (dateRange[0] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.greaterThanEqual(dateRange[0]));
		}
		else if (dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.lessThanEqual(dateRange[1]));
		}

		return super.select(expression);
	}

	private List<ReportQueryResult> selectTaxesBySettlement(final Settlement settlement)
	{
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(deleted);

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("currentTax", new ExpressionBuilder().get("currentTax").get("id"));

		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("baseAmount",
				this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO), Double.class);
		reportQuery.addSum("taxAmount", this.getTaxAmount(settlement.getSalespoint().getPaymentType().getCurrency(),
				Receipt.QuotationType.DEFAULT_CURRENCY), Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("currentTax").get("id").ascending());

		reportQuery.addGrouping(new ExpressionBuilder().get("currentTax").get("id"));

		return super.selectReportQueryResults(reportQuery);
	}

	private Collection<ReportQueryResult> selectTaxesBySalespointsAndDateRange(final Salespoint[] salespoints,
			Calendar[] dates)
	{
		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression state = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
				.equal(Receipt.State.SAVED);
		Expression expression = state.and(deleted);

		expression = expression.and(new ExpressionBuilder().get("receipt").get("internal").equal(false));
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		expression = expression.and(sps);

		if (dates[0] != null && dates[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.between(dates[0], dates[1]));
		}
		else if (dates[0] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("receipt").get("timestamp")
					.greaterThanEqual(dates[0]));
		}
		else if (dates[1] != null)
		{
			expression = expression
					.and(new ExpressionBuilder().get("receipt").get("timestamp").lessThanEqual(dates[1]));
		}

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("currentTax", new ExpressionBuilder().get("currentTax").get("id"));
		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("baseAmount",
				this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO), Double.class);
		reportQuery.addSum("taxAmount", this.getTaxAmount(salespoints[0].getCommonSettings().getReferenceCurrency(),
				Receipt.QuotationType.DEFAULT_CURRENCY), Double.class);
		reportQuery.addOrdering(new ExpressionBuilder().get("currentTax").get("id").ascending());
		reportQuery.addGrouping(new ExpressionBuilder().get("currentTax").get("id"));

		return super.selectReportQueryResults(reportQuery);
	}

	public Collection<DayTimeRow> selectDayHourStatisticsRange(final Salespoint[] salespoints,
			Calendar[] dateRange, int[] weekdays, int[] hourRange)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("productGroup").get("productGroupType").equal(ProductGroupType.SALES_RELATED));

		if (salespoints != null && salespoints.length > 0)
		{
			Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[0]);
			for (int i = 1; i < salespoints.length; i++)
			{
				sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
						.equal(salespoints[1]));
			}
			expression.and(sps);
		}

		Expression dateRangeExpression = null;
		if (dateRange[0] != null && dateRange[1] != null)
		{
			dateRangeExpression = new ExpressionBuilder().get("receipt").get("timestamp")
					.between(dateRange[0], dateRange[1]);
		}
		else if (dateRange[0] != null)
		{
			dateRangeExpression = new ExpressionBuilder().get("receipt").get("timestamp")
					.greaterThanEqual(dateRange[0]);
		}
		else if (dateRange[1] != null)
		{
			dateRangeExpression = new ExpressionBuilder().get("receipt").get("timestamp")
					.lessThanEqual(dateRange[1]);
		}
		if (dateRangeExpression != null)
		{
			expression = expression.and(dateRangeExpression);
		}

		if (weekdays.length > 0)
		{
			Expression weekdayExpression = new ExpressionBuilder().get("receipt").get("timestamp").datePart("weekday").equal(weekdays[0]);
			for (int i = 1; i < weekdays.length; i++)
			{
				weekdayExpression = weekdayExpression.or(new ExpressionBuilder().get("receipt").get("timestamp").datePart("weekday").equal(weekdays[i]));
			}
			expression.and(weekdayExpression);
		}

		expression.and(new ExpressionBuilder().get("receipt").get("hour").between(hourRange[0], hourRange[1]));

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.or(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		expression = expression.and(deleted);

		Map<Long, DayTimeRow> rows = new HashMap<Long, DayTimeRow>();

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("name", new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"), String.class);
		reportQuery.addAttribute("hour", new ExpressionBuilder().get("receipt").get("hour"), Integer.class);
//		reportQuery.addSum("quantity", Integer.class);
		reportQuery.addSum("amount", this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY, Position.AmountType.NETTO), Double.class);
		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("receipt").get("hour").ascending());
		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint").get("name"));
		reportQuery.addGrouping(new ExpressionBuilder().get("receipt").get("hour"));
		Collection<ReportQueryResult> results =  super.selectReportQueryResults(reportQuery);
		if (results != null && results.size() > 0)
		{
			for (ReportQueryResult result : results)
			{
				Long id = (Long) result.get("id");
				String name = (String) result.get("name");
				Integer hour = (Integer) result.get("hour");
				Double amount = (Double) result.get("amount");
				DayTimeRow row = rows.get(id);
				if (row == null)
				{
					row = new DayTimeRow(id, name, hour, amount);
					for (int i = hourRange[0]; i <= hourRange[1]; i++)
					{
						String key = "h" + new Integer(i).toString();
						if (row.get(key) == null)
						{
							row.put(key, new Double(0D));
						}
					}
					rows.put(id, row);
				}
				else
				{
					row.add(hour, amount);
				}
			}
		}
		return rows.values();
	}

	private Expression taxFactor()
	{
		final Expression percents = new ExpressionBuilder().get("taxPercents");
		if (this.getCommonSettings().isTaxInclusive())
		{
			final Expression addition = new ExpressionBuilder().get("taxPercents");
			final Expression divisor = ExpressionMath.add(new ExpressionBuilder().value(1), addition);
			final Expression quotient = ExpressionMath.divide(percents, divisor);
			return quotient;
		}
		else
		{
			return percents;
		}
	}

}
