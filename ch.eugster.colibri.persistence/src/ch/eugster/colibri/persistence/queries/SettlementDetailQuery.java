package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementDetailQuery extends AbstractQuery<Settlement>
{
	// public Collection<SettlementDetail> selectDifferences()
	// {
	// Expression expression = new
	// ExpressionBuilder(this.getEntityClass()).get("debit").notEqual(
	// new ExpressionBuilder().get("credit"));
	// expression = expression.and(new
	// ExpressionBuilder().get("part").equal(Part.DIFFERENCE));
	//
	// final Expression settledCriteria = new
	// ExpressionBuilder().get("settled").isNull();
	// Settlement settlement =
	// this.find(salespointCriteria.and(settledCriteria));
	// if (settlement == null)
	// {
	// settlement = (Settlement)
	// this.getConnectionService().merge(Settlement.newInstance(salespoint));
	// }
	// return settlement;
	// }
	//
	// public static Iterator selectDiffRecords(Salespoint[] salespoints, Date
	// from, Date to)
	// {
	// String[] fields = new String[] { "salespointId", "settlement", "code",
	// "longText", "type", "subtype",
	// "cashtype", "amount1" };
	//
	// int[] fieldTypes = new int[] { Types.BIGINT, Types.BIGINT, Types.VARCHAR,
	// Types.VARCHAR, Types.INTEGER,
	// Types.INTEGER, Types.INTEGER, Types.DOUBLE };
	//
	// Criteria criteria = new Criteria();
	//
	// Long fromLong = new Long(from.getTime());
	// Long toLong = new Long(to.getTime());
	//
	//		criteria.addBetween("settlement", fromLong, toLong); //$NON-NLS-1$
	// criteria.addEqualTo("type", new Integer(Settlement.TYPE_CASH_CHECK));
	// criteria.addEqualTo("cashtype", new
	// Integer(Settlement.CASH_CHECK_DIFFERENCE));
	//
	// if (salespoints != null)
	// criteria.addAndCriteria(Salespoint.getSalespointCriteria(salespoints));
	//
	// ReportQueryByCriteria query = new ReportQueryByCriteria(Settlement.class,
	// fields, criteria);
	// query.setJdbcTypes(fieldTypes);
	//
	// query.addOrderByAscending("salespointId");
	// query.addOrderByAscending("code");
	// query.addOrderByAscending("settlement");
	//
	// return
	// Database.getCurrent().getBroker().getReportQueryIteratorByQuery(query);
	// }
	//
	// public static int countDiscountRecords(Salespoint[] salespoints, Date
	// from, Date to, boolean onlyWithDiscounts)
	// {
	// Criteria criteria = new Criteria();
	//
	//		criteria.addBetween("receipt.timestamp", from, to); //$NON-NLS-1$
	// criteria.addNotNull("receipt.settlement");
	// criteria.addEqualTo("receipt.status", new
	// Integer(Receipt.RECEIPT_STATE_SERIALIZED));
	// criteria.addEqualTo("type", new Integer(ProductGroup.TYPE_INCOME));
	// if (onlyWithDiscounts)
	// criteria.addNotEqualTo("discount", new Double(0d));
	//
	// if (salespoints != null)
	// criteria.addAndCriteria(Salespoint.getSalespointCriteria(salespoints));
	//
	// QueryByCriteria query = new QueryByCriteria(Position.class, criteria);
	//
	// query.addOrderByAscending("salespointId");
	// query.addOrderByAscending("receipt.timestamp");
	//
	// query.addGroupBy("salespointId");
	// query.addGroupBy("YEAR(receipt.timestamp)");
	// query.addGroupBy("MONTH(receipt.timestamp)");
	// query.addGroupBy("DAYOFMONTH(receipt.timestamp)");
	//
	// return Database.getCurrent().getBroker().getCount(query);
	// }
	//
	// public static Iterator selectDiscountRecords(Salespoint[] salespoints,
	// Date from, Date to, boolean onlyWithDiscounts)
	// {
	// String[] fields = new String[] { "receipt.salespointId",
	// "YEAR(receipt.timestamp) AS year",
	// "MONTH(receipt.timestamp) AS month",
	// "DAYOFMONTH(receipt.timestamp) AS day", "SUM(amount) AS amount",
	// "SUM(ROUND(quantity * price, 2)) AS fullAmount" };
	//
	// int[] fieldTypes = new int[] { Types.BIGINT, Types.INTEGER,
	// Types.INTEGER, Types.INTEGER, Types.DOUBLE,
	// Types.DOUBLE };
	//
	// Criteria criteria = new Criteria();
	//
	//		criteria.addBetween("receipt.timestamp", from, to); //$NON-NLS-1$
	// criteria.addNotNull("receipt.settlement");
	// criteria.addEqualTo("receipt.status", new
	// Integer(Receipt.RECEIPT_STATE_SERIALIZED));
	// criteria.addEqualTo("type", new Integer(ProductGroup.TYPE_INCOME));
	// if (onlyWithDiscounts)
	// criteria.addNotEqualTo("discount", new Double(0d));
	//
	// if (salespoints != null)
	// criteria.addAndCriteria(Salespoint.getSalespointCriteria(salespoints));
	//
	// ReportQueryByCriteria query = new ReportQueryByCriteria(Position.class,
	// fields, criteria);
	// query.setJdbcTypes(fieldTypes);
	//
	// query.addOrderByAscending("salespointId");
	// query.addOrderByAscending("receipt.timestamp");
	//
	// query.addGroupBy("salespointId");
	// query.addGroupBy("YEAR(receipt.timestamp)");
	// query.addGroupBy("MONTH(receipt.timestamp)");
	// query.addGroupBy("DAYOFMONTH(receipt.timestamp)");
	//
	// return
	// Database.getCurrent().getBroker().getReportQueryIteratorByQuery(query);
	// }

	public Collection<Settlement> selectBySalespointsAndSettled(final Salespoint[] salespoints, final Long settledFrom,
			final Long settledTo)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<Settlement>();
		}
		Expression salespoint = new ExpressionBuilder(Settlement.class).get("salespoint").equal(salespoints[0]);
		for (int i = 1; i > salespoints.length; i++)
		{
			salespoint.or(new ExpressionBuilder().get("salespoint").equal(salespoints[i]));
		}
		Expression settled = null;
		if (settledFrom != null || settledTo != null)
		{
			if (settledFrom == null)
			{
				settled = new ExpressionBuilder().get("settled").lessThanEqual(settledTo.longValue());
			}
			else if (settledTo == null)
			{
				settled = new ExpressionBuilder().get("settled").greaterThanEqual(settledFrom.longValue());
			}
			else
			{
				Calendar calendar = GregorianCalendar.getInstance(Locale.getDefault());
				calendar.setTimeInMillis(settledFrom.longValue());
				calendar = GregorianCalendar.getInstance(Locale.getDefault());
				calendar.setTimeInMillis(settledTo.longValue());
				settled = new ExpressionBuilder().get("settled")
						.between(settledFrom.longValue(), settledTo.longValue());
			}
		}
		else
		{
			settled = new ExpressionBuilder().get("settled").isNull();
		}
		if (settled != null)
		{
			salespoint = salespoint.and(settled);
		}
		Collection<Settlement> settlements = this.select(salespoint);
		return settlements;
	}

	@Override
	protected Class<Settlement> getEntityClass()
	{
		return Settlement.class;
	}
}
