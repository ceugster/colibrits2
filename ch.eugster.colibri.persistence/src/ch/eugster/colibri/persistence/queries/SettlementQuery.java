package ch.eugster.colibri.persistence.queries;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;

public class SettlementQuery extends AbstractQuery<Settlement>
{
	public Settlement selectBySalespoint(final Salespoint salespoint)
	{
		final Expression salespointCriteria = new ExpressionBuilder().get("salespoint").equal(salespoint);
		final Expression settledCriteria = new ExpressionBuilder().get("settled").isNull();
		Settlement settlement = this.find(salespointCriteria.and(settledCriteria));
		if (settlement == null)
		{
			settlement = (Settlement) this.getConnectionService().merge(Settlement.newInstance(salespoint));
		}
		return settlement;
	}

	public Collection<Settlement> selectBySalespointAndDateRange(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<Settlement>();
		}
		Expression salespointCriteria = new ExpressionBuilder(Settlement.class).get("salespoint").equal(salespoints[0]);
		for (int i = 1; i > salespoints.length; i++)
		{
			salespointCriteria.or(new ExpressionBuilder().get("salespoint").equal(salespoints[i]));
		}
		final Expression settledCriteria = new ExpressionBuilder().get("settled").between(dateRange[0], dateRange[1]);
		Collection<Settlement> settlements = this.select(salespointCriteria.and(settledCriteria));
		return settlements;
	}

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
				Calendar calendar = GregorianCalendar.getInstance();
				calendar.setTimeInMillis(settledFrom.longValue());
				System.out.println(settledFrom.longValue() + ", "
						+ SimpleDateFormat.getDateTimeInstance().format(calendar.getTime()));
				calendar = GregorianCalendar.getInstance();
				calendar.setTimeInMillis(settledTo.longValue());
				System.out.println(settledTo.longValue() + ", "
						+ SimpleDateFormat.getDateTimeInstance().format(calendar.getTime()));
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
