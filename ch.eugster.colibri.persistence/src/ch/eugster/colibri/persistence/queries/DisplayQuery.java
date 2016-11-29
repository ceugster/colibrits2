package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.Salespoint;

public class DisplayQuery extends AbstractQuery<Display>
{
	public Display findByDisplayTypeAndSalespoint(final String displayType, final Salespoint salespoint)
	{
		if (salespoint.getCustomerDisplaySettings() != null)
		{
			Expression builder = new ExpressionBuilder(Display.class).get("displayType").equal(displayType);
			builder = builder.and(new ExpressionBuilder().get("salespoint").equal(salespoint));
			builder = builder.and(new ExpressionBuilder().get("customerDisplaySettings").equal(
					salespoint.getCustomerDisplaySettings().getCustomerDisplaySettings()));
			return this.find(builder);
		}
		return null;
	}

	public Display findTemplate(final String displayType, final CustomerDisplaySettings customerDisplaySettings)
	{
		Expression builder = new ExpressionBuilder(Display.class).get("displayType").equal(displayType);
		builder = builder.and(new ExpressionBuilder().get("customerDisplaySettings").equal(customerDisplaySettings));
		builder = builder.and(new ExpressionBuilder().get("salespoint").isNull());
		return this.find(builder);
	}

	public List<Display> selectAll()
	{
		try
		{
			final Expression expression = new ExpressionBuilder(Display.class);
			final List<Expression> orders = new ArrayList<Expression>();
			orders.add(new ExpressionBuilder().get("salespoint").isNull().ascending());
			final List<Display> displays = this.select(expression, orders, 0);
			return displays;
		}
		catch (Exception e)
		{
			return new ArrayList<Display>();
		}
	}

	public List<Display> selectDisplayChildren()
	{
		try
		{
			final Expression builder = new ExpressionBuilder(Display.class).get("display").notNull();
			final List<Display> displays = this.select(builder);
			return displays;
		}
		catch (Exception e)
		{
			return new ArrayList<Display>();
		}
	}

	public List<Display> selectDisplayParents()
	{
		try
		{
			final Expression builder = new ExpressionBuilder(Display.class).get("display").isNull();
			final List<Display> displays = this.select(builder);
			return displays;
		}
		catch (Exception e)
		{
			return new ArrayList<Display>();
		}
	}

	@Override
	protected Class<Display> getEntityClass()
	{
		return Display.class;
	}
}
