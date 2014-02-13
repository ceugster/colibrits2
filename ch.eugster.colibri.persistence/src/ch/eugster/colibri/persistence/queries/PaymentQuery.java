package ch.eugster.colibri.persistence.queries;

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
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementPayment;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;

public class PaymentQuery extends AbstractQuery<Payment>
{
	public Collection<Payment> selectVoucherUpdates(final Salespoint salespoint, String providerId, final int maxRows)
	{
		Expression expression = new ExpressionBuilder(Payment.class).get("receipt").get("settlement").get("salespoint").equal(salespoint);

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		final Expression update = new ExpressionBuilder().get("bookProvider").equal(true);

		Expression saved = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED);
		saved = saved.and(update.and(new ExpressionBuilder().get("providerBooked").equal(false)));

		Expression reversed = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.REVERSED);
		reversed = reversed.and(update.and(new ExpressionBuilder().get("providerBooked").equal(true)));

		final Expression states = expression.and(deleted).and(saved.or(reversed));
		final Collection<Payment> payments = this.select(states, maxRows);
		return payments;
	}

	private List<ReportQueryResult> selectPaymentsBySettlement(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.get("receipt").get("settlement").equal(settlement);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("internal").equal(false));
		
		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addAttribute("paymentType", new ExpressionBuilder().get("paymentType").get("id"));

		/*
		 * Betrag errechnen
		 */
		final Expression roundedFcAmount = this.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY);
		final Expression roundedDcAmount = this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY);
		/*
		 * 
		 */
		reportQuery.addCount("id", Long.class);
		reportQuery.addSum("defaultCurrencyAmount", roundedDcAmount, Double.class);
		reportQuery.addSum("foreignCurrencyAmount", roundedFcAmount, Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("paymentType").get("id").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("back").ascending());

		reportQuery.addGrouping(new ExpressionBuilder().get("paymentType").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("back"));

		return super.selectReportQueryResults(reportQuery);
	}

	public List<ReportQueryResult> selectPaymentsBySalespointsAndDateRange(final Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
				.equal(Receipt.State.SAVED);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));
		expression = expression.and(new ExpressionBuilder().get("receipt").get("internal").equal(false));
		
		Expression sps = new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
				.equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("receipt").get("settlement").get("salespoint")
					.equal(salespoints[1]));
		}
		expression.and(sps);

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
		reportQuery.addAttribute("paymentType", new ExpressionBuilder().get("paymentType").get("id"));

		/*
		 * Betrag errechnen
		 */
		final Expression roundedFcAmount = this.getAmount(Receipt.QuotationType.FOREIGN_CURRENCY);
		final Expression roundedDcAmount = this.getAmount(Receipt.QuotationType.DEFAULT_CURRENCY);

		/*
		 * 
		 */
		reportQuery.addCount("id", Long.class);
		reportQuery.addSum("defaultCurrencyAmount", roundedDcAmount, Double.class);
		reportQuery.addSum("foreignCurrencyAmount", roundedFcAmount, Double.class);

		reportQuery.addOrdering(new ExpressionBuilder().get("paymentType").get("id").ascending());
		reportQuery.addOrdering(new ExpressionBuilder().get("back").ascending());

		reportQuery.addGrouping(new ExpressionBuilder().get("paymentType").get("id"));
		reportQuery.addGrouping(new ExpressionBuilder().get("back"));

		return super.selectReportQueryResults(reportQuery);
	}

	public List<SettlementPayment> selectPayments(final Settlement settlement)
	{
		return this.getPaymentDetails(this.selectPaymentsBySettlement(settlement), settlement);
	}

	public List<SettlementPayment> selectPayments(Salespoint[] salespoints, Calendar[] dateRange)
	{
		List<ReportQueryResult> results = this.selectPaymentsBySalespointsAndDateRange(salespoints, dateRange);
		List<SettlementPayment> payments = this.getPaymentDetails(results, null);
		return payments;
	}

	public double sumCashAndVoucher(final Settlement settlement, final Currency currency)
	{
		final PaymentTypeGroup[] paymentTypeGroups = new PaymentTypeGroup[] { PaymentTypeGroup.CASH,
				PaymentTypeGroup.VOUCHER };
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("receipt").get("state")
				.equal(Receipt.State.SAVED);
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("paymentType").get("currency").equal(currency));
		expression = expression.and(new ExpressionBuilder().get("paymentType").get("paymentTypeGroup")
				.in(paymentTypeGroups));

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addSum("amount", new ExpressionBuilder().get("amount"), Double.class);

		final Query query = JpaHelper.createQuery(reportQuery, this.getConnectionService().getEntityManager());
//		Double result = (Double) query.getSingleResult();
		ReportQueryResult result = (ReportQueryResult) query.getSingleResult();
		Double amount = (Double) result.get("amount");
		return amount == null ? 0D : amount.doubleValue();
	}

	public double getDifferenceSinceLastSettlement(final Stock stock, final Settlement settlement,
			final Currency currency)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("paymentType").equal(
				stock.getPaymentType());
		expression = expression.and(new ExpressionBuilder().get("receipt").get("settlement").equal(settlement));
		if (stock.getLastCashSettlement() != null)
		{
			expression = expression.and(new ExpressionBuilder(this.getEntityClass()).get("receipt").get("settlement")
					.get("settlement").greaterThan(stock.getLastCashSettlement().getSettled()));
		}

		final ReportQuery reportQuery = new ReportQuery(this.getEntityClass(), expression);
		reportQuery.addSum("amount", new ExpressionBuilder().get("amount"), Double.class);

		final Query query = JpaHelper.createQuery(reportQuery, this.getConnectionService().getEntityManager());
		Double result = (Double) query.getSingleResult();
//		ReportQueryResult result = reportQuery.getReportQueryResult(reportQuery);
//		Double amount = (Double) result.get("amount");
		return result == null ? 0D : result.doubleValue();
	}

	@Override
	protected Class<Payment> getEntityClass()
	{
		return Payment.class;
	}

	private Expression getAmount(Receipt.QuotationType quotationType)
	{
		final Expression amount = new ExpressionBuilder().get("amount");
		Expression quotation = quotation(quotationType);
		Expression product = ExpressionMath.multiply(amount, quotation);
		return this.round(product, quotationType);
	}

	private Expression quotation(final Receipt.QuotationType quotationType)
	{
		switch (quotationType)
		{
			case REFERENCE_CURRENCY:
			{
				return ExpressionMath.divide(new ExpressionBuilder().get("foreignCurrencyQuotation"),
						new ExpressionBuilder().get("receipt").get("referenceCurrencyQuotation"));
			}
			case DEFAULT_CURRENCY:
			{
				return ExpressionMath.divide(new ExpressionBuilder().get("foreignCurrencyQuotation"),
						new ExpressionBuilder().get("receipt").get("defaultCurrencyQuotation"));
			}
			case FOREIGN_CURRENCY:
			{
				return new ExpressionBuilder().value(1d);
			}
			case DEFAULT_FOREIGN_CURRENCY:
			{
				return ExpressionMath.divide(new ExpressionBuilder().get("foreignCurrencyQuotation"),
						new ExpressionBuilder().get("receipt").get("foreignCurrencyQuotation"));
			}
			default:
				return new ExpressionBuilder().value(0d);
		}
	}

	private Expression round(Expression amount, final Receipt.QuotationType quotationType)
	{
		switch (quotationType)
		{
			case REFERENCE_CURRENCY:
			{
				Expression referenceCurrencyRoundFactor = new ExpressionBuilder().get("receipt").get(
						"referenceCurrencyRoundFactor");
				Expression division = ExpressionMath.divide(amount, referenceCurrencyRoundFactor);
				Expression floor = ExpressionMath.add(division,
						new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR)));
				Expression left = ExpressionMath.floor(floor);
				Expression result = ExpressionMath.multiply(left,
						new ExpressionBuilder().get("receipt").get("referenceCurrencyRoundFactor"));
				return result;
			}
			case DEFAULT_CURRENCY:
			{
				Expression referenceCurrencyRoundFactor = new ExpressionBuilder().get("receipt").get(
						"defaultCurrencyRoundFactor");
				Expression division = ExpressionMath.divide(amount, referenceCurrencyRoundFactor);
				Expression floor = ExpressionMath.add(division,
						new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR)));
				Expression left = ExpressionMath.floor(floor);
				Expression result = ExpressionMath.multiply(left,
						new ExpressionBuilder().get("receipt").get("defaultCurrencyRoundFactor"));
				return result;
			}
			case FOREIGN_CURRENCY:
			{
				Expression referenceCurrencyRoundFactor = new ExpressionBuilder().get("foreignCurrencyRoundFactor");
				Expression division = ExpressionMath.divide(amount, referenceCurrencyRoundFactor);
				Expression floor = ExpressionMath.add(division,
						new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR)));
				Expression left = ExpressionMath.floor(floor);
				Expression result = ExpressionMath.multiply(left,
						new ExpressionBuilder().get("foreignCurrencyRoundFactor"));
				return result;
			}
			case DEFAULT_FOREIGN_CURRENCY:
			{
				Expression referenceCurrencyRoundFactor = new ExpressionBuilder().get("receipt").get(
						"foreignCurrencyRoundFactor");
				Expression division = ExpressionMath.divide(amount, referenceCurrencyRoundFactor);
				Expression floor = ExpressionMath.add(division,
						new ExpressionBuilder().value((0.5 + AbstractEntity.ROUND_FACTOR)));
				Expression left = ExpressionMath.floor(floor);
				Expression result = ExpressionMath.multiply(left,
						new ExpressionBuilder().get("receipt").get("foreignCurrencyRoundFactor"));
				return result;
			}
			default:
				return new ExpressionBuilder().value(0d);
		}
	}

	private List<SettlementPayment> getPaymentDetails(final List<ReportQueryResult> results,
			final Settlement settlement)
	{
		final Map<Long, SettlementPayment> details = new HashMap<Long, SettlementPayment>();
		for (final ReportQueryResult result : results)
		{
			Long id = (Long) result.get("paymentType");
			PaymentType paymentType = (PaymentType) this.getConnectionService().find(PaymentType.class, id);
			Double amount = (Double) result.get("defaultCurrencyAmount");
			if (paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER))
			{
				if (amount.doubleValue() < 0)
				{
					id = -id;
				}
			}
			SettlementPayment detail = details.get(id);
			if (detail == null)
			{
				detail = SettlementPayment.newInstance(settlement, paymentType);
				details.put(id, detail);
			}
			final Long quantity = (Long) result.get("id");
			detail.setQuantity(detail.getQuantity() + (quantity == null ? 0 : quantity.intValue()));

			detail.setDefaultCurrencyAmount(detail.getDefaultCurrencyAmount() + (amount == null ? 0D : amount));
			amount = (Double) result.get("foreignCurrencyAmount");
			detail.setForeignCurrencyAmount(detail.getForeignCurrencyAmount() + (amount == null ? 0d : amount));
		}
		List<SettlementPayment> payments = new ArrayList<SettlementPayment>();
		for (SettlementPayment detail : details.values())
		{
			payments.add(detail);
		}
		return payments;
	}

	public long countProviderUpdates(Salespoint salespoint, String providerId)
	{
		Expression expression = new ExpressionBuilder(Payment.class).get("receipt").get("settlement").get("salespoint").equal(salespoint);

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression provider = new ExpressionBuilder().get("providerId").equal(providerId);
		Expression update = new ExpressionBuilder().get("bookProvider").equal(true);
		provider = provider.and(update.or(new ExpressionBuilder().get("serverUpdated").equal(false)));

		Expression saved = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED);
		saved = saved.and(provider.and(new ExpressionBuilder().get("providerBooked").equal(false)));

		Expression reversed = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.REVERSED);
		reversed = reversed.and(provider.and(new ExpressionBuilder().get("providerBooked").equal(true)));

		final Expression states = expression.and(deleted).and(saved.or(reversed));
		final long value = this.count(states);
		return value;
	}

	public Collection<Payment> selectProviderUpdates(final Salespoint salespoint, String providerId, final int maxRows)
	{
		Expression expression = new ExpressionBuilder(Payment.class).get("receipt").get("settlement").get("salespoint").equal(salespoint);

		Expression deleted = new ExpressionBuilder().get("deleted").equal(false);
		deleted = deleted.and(new ExpressionBuilder().get("receipt").get("deleted").equal(false));

		Expression provider = new ExpressionBuilder().get("providerId").equal(providerId);
		Expression update = new ExpressionBuilder().get("bookProvider").equal(true);
		provider = provider.and(update.or(new ExpressionBuilder().get("serverUpdated").equal(false)));

		Expression saved = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.SAVED);
		saved = saved.and(provider.and(new ExpressionBuilder().get("providerBooked").equal(false)));

		Expression reversed = new ExpressionBuilder().get("receipt").get("state").equal(Receipt.State.REVERSED);
		reversed = reversed.and(provider.and(new ExpressionBuilder().get("providerBooked").equal(true)));

		final Expression states = expression.and(deleted).and(saved.or(reversed));
		final Collection<Payment> payments = this.select(states, maxRows);
		return payments;
	}

}
