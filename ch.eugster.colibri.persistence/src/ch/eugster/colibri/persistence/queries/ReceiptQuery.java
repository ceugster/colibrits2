package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementReceipt;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class ReceiptQuery extends AbstractQuery<Receipt>
{
	public long countSavedBySettlement(Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		return this.count(expression);
	}

	public long countSavedAndReversedBySettlement(Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		Expression states = new ExpressionBuilder().get("state").equal(Receipt.State.SAVED);
		states = states.or(new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED));
		expression = expression.and(states);
		long count = this.count(expression);
		return count;
	}

	public long countReversedBySettlement(Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		Expression states = new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED);
		expression = expression.and(states);
		return this.count(expression);
	}

	public List<Receipt> selectSavedBySettlement(Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		return this.select(expression);
	}

	public long countParked(final User user)
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("user").equal(user));
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.PARKED));
		return this.count(expression);
	}

	public long countParked(final Salespoint salespoint)
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("settlement").get("salespoint").equal(salespoint));
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.PARKED));
		return this.count(expression);
	}

	public long countBySalespointsAndDateRange(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		long result = 0L;
		Expression sps = null;
		if (salespoints != null && salespoints.length > 0)
		{
			sps = new ExpressionBuilder().get("settlement").get("salespoint").equal(salespoints[0]);
			for (int i = 1; i < salespoints.length; i++)
			{
				sps = sps.or(new ExpressionBuilder().get("settlement").get("salespoint").equal(salespoints[i]));
			}

			Expression dr = new ExpressionBuilder().get("timestamp").between(dateRange[0], dateRange[1]);

			Expression expression = new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false);
			expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.SAVED));
			expression = expression.and(sps);
			expression = expression.and(dr);

			result = this.count(expression);
		}
		return result;
	}

	public long countProviderNotUpdated()
	{
		Expression deleted = new ExpressionBuilder(Receipt.class).get("deleted").equal(false);
		deleted = deleted.and(new ExpressionBuilder().anyOfAllowingNone("positions").get("deleted").equal(false));

		final Expression update = new ExpressionBuilder().anyOfAllowingNone("positions").get("bookProvider")
				.equal(true);

		Expression saved = new ExpressionBuilder().get("state").equal(Receipt.State.SAVED);
		saved = saved.and(update.and(new ExpressionBuilder().anyOfAllowingNone("positions").get("providerBooked")
				.equal(false)));

		Expression reversed = new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED);
		reversed = reversed.and(update.and(new ExpressionBuilder().anyOfAllowingNone("positions").get("providerBooked")
				.equal(true)));

		final Expression states = saved.or(reversed);
		final long value = this.count(deleted.and(states));
		return value;
	}

	private Expression createSelectTransferablesExpression()
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("transferred").equal(false));
		Expression states = new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED);
		states = states.or(new ExpressionBuilder().get("state").equal(Receipt.State.SAVED));
		return expression.and(states);
	}
	
	private Expression createSelectTransferablesFromSettlementExpression(Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("transferred").equal(false));
		Expression states = new ExpressionBuilder().get("state").equal(Receipt.State.SAVED);
		states = states.or(new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED));
		return expression.and(states);
	}
	
	public long countRemainingToTransfer()
	{
		return this.count(createSelectTransferablesExpression());
	}

	// public Receipt findBySalespointAndNumber(final Salespoint salespoint,
	// final Long number)
	// {
	// Expression expression = new
	// ExpressionBuilder(this.getEntityClass()).get("number").equal(number);
	// expression = expression.and(new
	// ExpressionBuilder().get("settlement").get("salespoint").equal(salespoint));
	// expression = expression.and(new
	// ExpressionBuilder().get("deleted").equal(false));
	// final Collection<Receipt> receipts = this.select(expression);
	// if (receipts.isEmpty())
	// {
	// return null;
	// }
	// return receipts.iterator().next();
	// }

	public Receipt findByOtherId(final Long otherId)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("id").equal(otherId);
		return find(expression);
	}

	public Receipt findWithOtherId(final Long otherId)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("otherId").equal(otherId);
		return find(expression);
	}

	public List<Receipt> selectBySalespointAndDate(Salespoint salespoint, Calendar from, Calendar to)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("settlement").get("salespoint")
				.equal(salespoint);
		expression = expression.and(new ExpressionBuilder().get("timestamp").between(from, to));
		final List<Receipt> receipts = this.select(expression);
		return receipts;

	}

	public Receipt findBySalespointAndTimestamp(Salespoint salespoint, Calendar calendar)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("settlement").get("salespoint")
				.equal(salespoint);
		expression = expression.and(new ExpressionBuilder().get("timestamp").equal(calendar));
		final Receipt receipt = this.find(expression);
		return receipt;

	}

	public List<Receipt> selectBySalespointAndNumber(Salespoint salespoint, String number)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("settlement").get("salespoint")
				.equal(salespoint);
		expression = expression.and(new ExpressionBuilder().get("number").equal(number));
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		final List<Receipt> receipts = this.select(expression);
		return receipts;

	}

	public List<Receipt> selectByNumber(Long number)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("number").equal(number);
		final List<Receipt> receipts = this.select(expression);
		return receipts;

	}

	public List<Receipt> selectBySettlement(final Settlement settlement, Receipt.State[] states)
	{
		Expression state = null;
		if (states == null)
		{
			state = new ExpressionBuilder().get("state").equal(Receipt.State.SAVED);
			state = state.or(new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED));
		}
		else
		{
			state = new ExpressionBuilder().get("state").equal(states[0]);
			if (states.length > 1)
			{
				for (int i = 1; i < states.length; i++)
				{
					state = state.or(new ExpressionBuilder().get("state").equal(states[i]));
				}
			}
		}

		Expression expression = new ExpressionBuilder(Receipt.class).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		expression = expression.and(state);
		final List<Receipt> receipts = this.select(expression);
		return receipts;
	}

	public List<Receipt> selectBySettlementAndUser(final Settlement settlement, final User user,
			Receipt.State[] states)
	{
		Expression state = null;
		if (states == null)
		{
			state = new ExpressionBuilder().get("state").equal(Receipt.State.SAVED);
			state = state.or(new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED));
		}
		else
		{
			state = new ExpressionBuilder().get("state").equal(states[0]);
			if (states.length > 1)
			{
				for (int i = 1; i < states.length; i++)
				{
					state = state.or(new ExpressionBuilder().get("state").equal(states[i]));
				}
			}
		}

		Expression expression = new ExpressionBuilder(Receipt.class).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("user").equal(user));
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		expression = expression.and(state);
		final List<Receipt> receipts = this.select(expression);
		return receipts;
	}

	public List<Receipt> selectParked(final Salespoint salespoint)
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("settlement").get("salespoint").equal(salespoint));
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.PARKED));
		return this.select(expression);
	}

	public List<Receipt> selectParked(final User user)
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("user").equal(user));
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.PARKED));
		return this.select(expression);
	}
	
	public List<Receipt> selectParked(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.PARKED));
		return this.select(expression);
	}
	
	public int removeParked(final Salespoint salespoint)
	{
		int result = 0;
		List<Receipt> receipts = selectParked(salespoint);
		for (Receipt receipt : receipts)
		{
			try
			{
				this.getConnectionService().delete(receipt);
				result++;
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	public List<Receipt> selectProviderNotUpdated(final int maxResults)
	{
		Expression deleted = new ExpressionBuilder(Receipt.class).get("deleted").equal(false);
		deleted = deleted.and(new ExpressionBuilder().anyOfAllowingNone("positions").get("deleted").equal(false));

		final Expression update = new ExpressionBuilder().anyOfAllowingNone("positions").get("bookProvider")
				.equal(true);

		Expression saved = new ExpressionBuilder().get("state").equal(Receipt.State.SAVED);
		saved = saved.and(update.and(new ExpressionBuilder().anyOfAllowingNone("positions").get("providerBooked")
				.equal(false)));

		Expression reversed = new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED);
		reversed = reversed.and(update.and(new ExpressionBuilder().anyOfAllowingNone("positions").get("providerBooked")
				.equal(true)));

		final Expression states = saved.or(reversed);
		final List<Receipt> receipts = this.select(deleted.and(states), maxResults);
		return receipts;
	}

	public List<SettlementReceipt> selectReversed(final Settlement settlement)
	{
		final List<Receipt> receipts = this.selectReversedReceiptsBySettlement(settlement);
		return this.getReversedReceipts(settlement, receipts);
	}

	public List<SettlementReceipt> selectReversed(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		final List<Receipt> receipts = this.selectReversedBySalespointsAndDateRange(salespoints, dateRange);
		return this.getReversedReceipts(null, receipts);
	}

	public List<Receipt> selectTransferables()
	{
		return this.selectTransferables(0);
	}

	public List<Receipt> selectTransferables(Settlement settlement)
	{
		return this.selectTransferables(settlement, 0);
	}

	public List<Receipt> selectTransferables(final int maxResults)
	{
		return this.select(createSelectTransferablesExpression(), maxResults);
	}

	public List<Receipt> selectTransferables(final Settlement settlement, final int maxResults)
	{
		return this.select(createSelectTransferablesFromSettlementExpression(settlement), maxResults);
	}

	public long countTransferables(Settlement settlement)
	{
		return this.count(createSelectTransferablesFromSettlementExpression(settlement));
	}

	@Override
	protected Class<Receipt> getEntityClass()
	{
		return Receipt.class;
	}

	private List<SettlementReceipt> getReversedReceipts(Settlement settlement, final List<Receipt> receipts)
	{
		final List<SettlementReceipt> details = new ArrayList<SettlementReceipt>();
		for (final Receipt receipt : receipts)
		{
			final SettlementReceipt detail = SettlementReceipt.newInstance(settlement, receipt);
			details.add(detail);
		}
		return details;
	}

	private List<Receipt> selectReversedReceiptsBySettlement(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("state").equal(Receipt.State.REVERSED);
		expression = expression.and(new ExpressionBuilder().get("settlement").get("salespoint")
				.equal(settlement.getSalespoint()));
		expression = expression.and(new ExpressionBuilder().get("settlement").get("settled").isNull());
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		List<Receipt> receipts = this.select(expression);
		return receipts;
	}

	private List<Receipt> selectReversedBySalespointsAndDateRange(final Salespoint[] salespoints,
			Calendar[] dateRange)
	{
		if (salespoints == null || salespoints.length == 0)
		{
			return new ArrayList<Receipt>();
		}

		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("state").equal(Receipt.State.REVERSED);

		Expression sps = new ExpressionBuilder().get("settlement").get("salespoint").equal(salespoints[0]);
		for (int i = 1; i < salespoints.length; i++)
		{
			sps = sps.or(new ExpressionBuilder().get("settlement").get("salespoint").equal(salespoints[i]));
		}
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		if (dateRange[0] != null && dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("timestamp").between(dateRange[0], dateRange[1]));
		}
		else if (dateRange[0] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("timestamp").greaterThanEqual(dateRange[0]));
		}
		else if (dateRange[1] != null)
		{
			expression = expression.and(new ExpressionBuilder().get("timestamp").lessThanEqual(dateRange[1]));
		}
		return this.select(expression);
	}

	public long countDayHourStatisticsRange(final Long salespointId,
			Calendar[] dateRange, int[] weekdays, int hourRange, boolean nonSalesToo)
	{
		if (weekdays == null || weekdays.length == 0)
		{
			return 0;
		}
		
		Expression expression = new ExpressionBuilder(this.getEntityClass());
		expression = expression.and(new ExpressionBuilder().anyOfAllowingNone("positions").get("productGroup").get("productGroupType").equal(ProductGroupType.SALES_RELATED));
		if (nonSalesToo)
		{
			expression = expression.or(new ExpressionBuilder().anyOf("positions").get("productGroup").get("productGroupType").equal(ProductGroupType.NON_SALES_RELATED));
		}
		
		Expression sps = new ExpressionBuilder().get("settlement").get("salespoint").get("id").equal(salespointId);
		expression.and(sps);

		Expression dateRangeExpression = null;
		if (dateRange[0] != null && dateRange[1] != null)
		{
			dateRangeExpression = new ExpressionBuilder().get("timestamp")
					.between(dateRange[0], dateRange[1]);
		}
		else if (dateRange[0] != null)
		{
			dateRangeExpression = new ExpressionBuilder().get("timestamp")
					.greaterThanEqual(dateRange[0]);
		}
		else if (dateRange[1] != null)
		{
			dateRangeExpression = new ExpressionBuilder().get("timestamp")
					.lessThanEqual(dateRange[1]);
		}
		if (dateRangeExpression != null)
		{
			expression = expression.and(dateRangeExpression);
		}

		if (weekdays.length < 7)
		{
			Expression weekdayExpression = new ExpressionBuilder().get("dayOfWeek").equal(weekdays[0]);
			for (int i = 1; i < weekdays.length; i++)
			{
				weekdayExpression = weekdayExpression.or(new ExpressionBuilder().get("dayOfWeek").equal(weekdays[i]));
			}
			expression = expression.and(weekdayExpression);
		}

		expression = expression.and(new ExpressionBuilder().get("hour").equal(hourRange));
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		return super.count(expression);
	}

}
