package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
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
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Position.AmountType;
import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementInternal;
import ch.eugster.colibri.persistence.model.SettlementPayedInvoice;
import ch.eugster.colibri.persistence.model.SettlementPosition;
import ch.eugster.colibri.persistence.model.SettlementRestitutedPosition;
import ch.eugster.colibri.persistence.model.SettlementTax;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.product.ProductGroupGroup;
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
		final Expression deleted = new ExpressionBuilder(Position.class).get("deleted").equal(false);
		final Expression parked = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.PARKED);
		final Expression value = new ExpressionBuilder().get("searchValue").equal(searchValue);
		return this.select(deleted.and(parked.and(value)));
	}

	public long countProviderUpdates()
	{
		Expression deleted = new ExpressionBuilder(Position.class).get("deleted").equal(false);
		deleted = deleted.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		final Expression update = new ExpressionBuilder().get("bookProvider").equal(true);

		Expression saved = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED);
		saved = saved.and(update.and(new ExpressionBuilder().get("providerBooked").equal(false)));

		Expression reversed = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.REVERSED);
		reversed = reversed.and(update.and(new ExpressionBuilder().get("providerBooked").equal(true)));

		final Expression states = deleted.and(saved.or(reversed));
		final long value = this.count(states);
		return value;
	}

	public Collection<SettlementPosition> selectPositions(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		Collection<ReportQueryResult> results = this.selectPositionsBySalespointsAndDateRange(salespoints, dateRange);
		Collection<SettlementPosition> positions = this.getPositionsDetails(results, null);
		return positions;
	}

	public Collection<SettlementPosition> selectPositions(final Settlement settlement)
	{
		Collection<ReportQueryResult> results = this.selectPositionsBySettlement(settlement);
		Collection<SettlementPosition> positions = this.getPositionsDetails(results, settlement);
		return positions;
	}

	public Collection<SettlementPayedInvoice> selectPayedInvoices(final Settlement settlement)
	{
		return this.getPayedInvoicesDetails(this.selectPayedInvoicesBySettlement(settlement), settlement);
	}

	public Collection<SettlementPayedInvoice> selectPayedInvoices(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		return this.getPayedInvoicesDetails(this.selectPayedInvoicesBySalespointsAndDateRange(salespoints, dateRange),
				null);
	}

	public Collection<SettlementRestitutedPosition> selectRestitutedPositions(final Settlement settlement)
	{
		return this.getRestitutedPositionDetails(this.selectRestitutedPositionsBySettlement(settlement), settlement);
	}

	public Collection<SettlementRestitutedPosition> selectRestitutedPositions(final Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		return this.getRestitutedPositionDetails(
				this.selectRestitutedPositionsBySalespointsAndDateRange(salespoints, dateRange), null);
	}

	public Collection<SettlementInternal> selectInternals(final Settlement settlement)
	{
		return this.getInternalDetails(this.selectInternalsBySettlement(settlement), settlement);
	}

	public Collection<SettlementInternal> selectInternals(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		return this.getInternalDetails(this.selectInternalsBySalespointsAndDateRange(salespoints, dateRange), null);
	}

	public Collection<Position> selectProviderUpdates(final int maxRows)
	{
		Expression deleted = new ExpressionBuilder(Position.class).get("deleted").equal(false);
		deleted = deleted.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		final Expression update = new ExpressionBuilder().get("bookProvider").equal(true);

		Expression saved = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED);
		saved = saved.and(update.and(new ExpressionBuilder().get("providerBooked").equal(false)));

		Expression reversed = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.REVERSED);
		reversed = reversed.and(update.and(new ExpressionBuilder().get("providerBooked").equal(true)));

		final Expression states = deleted.and(saved.or(reversed));
		final Collection<Position> positions = this.select(states, maxRows);
		return positions;
	}

	public Collection<SettlementTax> selectTaxes(final Settlement settlement)
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

	private Collection<SettlementPosition> getPositionsDetails(final Collection<ReportQueryResult> results,
			final Settlement settlement)
	{
		final Collection<SettlementPosition> details = new ArrayList<SettlementPosition>();
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

			System.out.println(productGroup.getId() + ", " + productGroup.getName() + ", "
					+ productGroup.getProductGroupType().toString());

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

	private Collection<SettlementPayedInvoice> getPayedInvoicesDetails(final Collection<Position> positions,
			final Settlement settlement)
	{
		final Collection<SettlementPayedInvoice> payedInvoices = new ArrayList<SettlementPayedInvoice>();
		for (final Position position : positions)
		{
			final SettlementPayedInvoice payedInvoice = SettlementPayedInvoice.newInstance(settlement, position);
			payedInvoices.add(payedInvoice);
		}
		return payedInvoices;
	}

	private Collection<SettlementRestitutedPosition> getRestitutedPositionDetails(final Collection<Position> positions,
			final Settlement settlement)
	{
		final Collection<SettlementRestitutedPosition> details = new ArrayList<SettlementRestitutedPosition>();
		for (final Position position : positions)
		{
			final SettlementRestitutedPosition detail = SettlementRestitutedPosition.newInstance(settlement, position);
			details.add(detail);
		}
		return details;
	}

	private Collection<SettlementInternal> getInternalDetails(final Collection<Position> positions,
			final Settlement settlement)
	{
		final Collection<SettlementInternal> details = new ArrayList<SettlementInternal>();
		for (final Position position : positions)
		{
			final SettlementInternal detail = SettlementInternal.newInstance(settlement, position);
			details.add(detail);
		}
		return details;
	}

	// private Collection<SettlementPosition> getPositionDetails(final
	// Collection<Position> positions,
	// final Settlement settlement)
	// {
	// final Collection<SettlementPosition> details = new
	// ArrayList<SettlementPosition>();
	// for (final Position position : positions)
	// {
	// final SettlementPosition detail =
	// SettlementPosition.newInstance(settlement, position.getProductGroup(),
	// position.getReceipt().getDefaultCurrency(),
	// position.getForeignCurrency());
	// detail.setQuantity(position.getQuantity());
	// double amount =
	// position.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY,
	// Position.AmountType.NETTO);
	// detail.setDefaultCurrencyAmount(amount);
	// detail.setForeignCurrencyAmount(amount);
	// detail.setTaxAmount(position.getTaxAmount(Receipt.QuotationType.DEFAULT_CURRENCY));
	// details.add(detail);
	// }
	// return details;
	// }

	private Expression getTaxAmount(final Currency currency, final Receipt.QuotationType quotationType)
	{
		final Expression base = this.getAmount(quotationType, AmountType.NETTO);
		final Expression product = ExpressionMath.multiply(base, this.taxFactor());
		final Expression result = this.round(product, currency);
		return result;
	}

	private Collection<SettlementTax> getTaxDetails(final Collection<ReportQueryResult> results,
			final Settlement settlement)
	{
		final Collection<SettlementTax> details = new ArrayList<SettlementTax>();
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

	private Collection<ReportQueryResult> selectPositionsBySalespointsAndDateRange(Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<ReportQueryResult>();
		}

		Currency currency = salespoints[0].getCommonSettings().getReferenceCurrency();

		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
				.equal(Receipt.State.SAVED);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
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

		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
				.equal(Receipt.State.SAVED);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
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

	private Collection<ReportQueryResult> selectPositionsBySettlement(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));

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

		Collection<ReportQueryResult> result = super.selectReportQueryResults(reportQuery);
		return result;
	}

	private Collection<Position> selectPayedInvoicesBySettlement(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

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

	private Collection<Position> selectInternalsBySettlement(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression alloc = new ExpressionBuilder().get("productGroup").get("productGroupType")
				.equal(ProductGroupType.ALLOCATION);
		Expression withd = new ExpressionBuilder().get("productGroup").get("productGroupType")
				.equal(ProductGroupType.WITHDRAWAL);

		expression = expression.and(alloc.or(withd));
		Collection<Position> internals = this.select(expression);
		return internals;
	}

	private Collection<Position> selectInternalsBySalespointsAndDateRange(final Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<Position>();
		}

		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
				.equal(Receipt.State.SAVED);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[i]));
		}
		expression = expression.and(sps);
		Expression alloc = new ExpressionBuilder().get("productGroup").get("productGroupType")
				.equal(ProductGroupType.ALLOCATION);
		Expression withd = new ExpressionBuilder().get("productGroup").get("productGroupType")
				.equal(ProductGroupType.WITHDRAWAL);

		expression = expression.and(alloc.or(withd));
		Collection<Position> internals = this.select(expression);
		return internals;
	}

	private Collection<Position> selectRestitutedPositionsBySettlement(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("settlement").equal(settlement);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
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

		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state").equal(Receipt.State.SAVED);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
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

	private Collection<ReportQueryResult> selectTaxesBySettlement(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.get("receipt").get("settlement").equal(settlement);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

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
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
				.equal(Receipt.State.SAVED);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
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
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
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
						row.put("h" + new Integer(i).toString(), new Double(0D));
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
