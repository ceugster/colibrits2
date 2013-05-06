package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.SettlementReceipt;
import ch.eugster.colibri.persistence.model.User;

public class ReceiptQuery extends AbstractQuery<Receipt>
{
	public long countSavedBySettlement(Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.SAVED));
		expression = expression.and(new ExpressionBuilder().get("settlement").equal(settlement));
		return this.count(expression);
	}

	public Collection<Receipt> selectSavedBySettlement(Settlement settlement)
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

	public long countRemainingToTransfer()
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("transferred").equal(false));
		// Expression states = new
		// ExpressionBuilder().get("state").equal(Receipt.State.CLOSED);
		Expression states = new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED);
		states = states.or(new ExpressionBuilder().get("state").equal(Receipt.State.SAVED));

		expression = expression.and(states);
		return this.count(expression);
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

	public Receipt findByOtherId(final Long id)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("id").equal(id);
		return find(expression);
	}

	public Collection<Receipt> selectBySalespointAndDate(Salespoint salespoint, Calendar from, Calendar to)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("settlement").get("salespoint")
				.equal(salespoint);
		expression = expression.and(new ExpressionBuilder().get("timestamp").between(from, to));
		final Collection<Receipt> receipts = this.select(expression);
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

	public Collection<Receipt> selectBySalespointAndNumber(Salespoint salespoint, String number)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("settlement").get("salespoint")
				.equal(salespoint);
		expression = expression.and(new ExpressionBuilder().get("number").equal(number));
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		final Collection<Receipt> receipts = this.select(expression);
		return receipts;

	}

	public Collection<Receipt> selectByNumber(Long number)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("number").equal(number);
		final Collection<Receipt> receipts = this.select(expression);
		return receipts;

	}

	public Collection<Receipt> selectBySettlement(final Settlement settlement, Receipt.State[] states)
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
		final Collection<Receipt> receipts = this.select(expression);
		return receipts;
	}

	public Collection<Receipt> selectBySettlementAndUser(final Settlement settlement, final User user,
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
		final Collection<Receipt> receipts = this.select(expression);
		return receipts;
	}

	public Collection<Receipt> selectParked(final Salespoint salespoint)
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("settlement").get("salespoint").equal(salespoint));
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.PARKED));
		return this.select(expression);
	}

	public Collection<Receipt> selectParked(final User user)
	{
		Expression expression = new ExpressionBuilder().get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("user").equal(user));
		expression = expression.and(new ExpressionBuilder().get("state").equal(Receipt.State.PARKED));
		return this.select(expression);
	}
	
	public int removeParked(final Salespoint salespoint)
	{
		int result = 0;
		Collection<Receipt> receipts = selectParked(salespoint);
		for (Receipt receipt : receipts)
		{
			this.getConnectionService().remove(receipt);
			result++;
		}
		return result;
	}

	public Collection<Receipt> selectProviderNotUpdated(final int maxResults)
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
		final Collection<Receipt> receipts = this.select(deleted.and(states), maxResults);
		return receipts;
	}

	public Collection<SettlementReceipt> selectReversed(final Settlement settlement)
	{
		final Collection<Receipt> receipts = this.selectReversedReceiptsBySettlement(settlement);
		return this.getReversedReceipts(settlement, receipts);
	}

	public Collection<SettlementReceipt> selectReversed(final Salespoint[] salespoints, Calendar[] dateRange)
	{
		final Collection<Receipt> receipts = this.selectReversedBySalespointsAndDateRange(salespoints, dateRange);
		return this.getReversedReceipts(null, receipts);
	}

	public Collection<Receipt> selectTransferables()
	{
		return this.selectTransferables(0);
	}

	public Collection<Receipt> selectTransferables(final int maxResults)
	{
		Expression expression = new ExpressionBuilder(Receipt.class).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("transferred").equal(false));
		Expression states = new ExpressionBuilder().get("state").equal(Receipt.State.SAVED);
		states = states.or(new ExpressionBuilder().get("state").equal(Receipt.State.REVERSED));
		expression = expression.and(states);
		return this.select(expression, maxResults);
	}

	@Override
	protected Class<Receipt> getEntityClass()
	{
		return Receipt.class;
	}

	private Collection<SettlementReceipt> getReversedReceipts(Settlement settlement, final Collection<Receipt> receipts)
	{
		final Collection<SettlementReceipt> details = new ArrayList<SettlementReceipt>();
		for (final Receipt receipt : receipts)
		{
			final SettlementReceipt detail = SettlementReceipt.newInstance(settlement, receipt);
			details.add(detail);
		}
		return details;
	}

	private Collection<Receipt> selectReversedReceiptsBySettlement(final Settlement settlement)
	{
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("state").equal(Receipt.State.REVERSED);
		expression = expression.and(new ExpressionBuilder().get("settlement").get("salespoint")
				.equal(settlement.getSalespoint()));
		expression = expression.and(new ExpressionBuilder().get("settlement").get("settled").isNull());
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		Collection<Receipt> receipts = this.select(expression);
		return receipts;
	}

	private Collection<Receipt> selectReversedBySalespointsAndDateRange(final Salespoint[] salespoints,
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

}
