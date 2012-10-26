package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Collection;
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

	public Collection<Display> selectAll()
	{
		final Expression expression = new ExpressionBuilder(Display.class);

		final List<Expression> orders = new ArrayList<Expression>();
		orders.add(new ExpressionBuilder().get("salespoint").isNull().ascending());
		final Collection<Display> displays = this.select(expression, orders, 0);
		return displays;
	}

	public Collection<Display> selectDisplayChildren()
	{
		final Expression builder = new ExpressionBuilder(Display.class).get("display").notNull();
		final Collection<Display> displays = this.select(builder);
		return displays;
	}

	public Collection<Display> selectDisplayParents()
	{
		final Expression builder = new ExpressionBuilder(Display.class).get("display").isNull();
		final Collection<Display> displays = this.select(builder);
		return displays;
	}

	@Override
	protected Class<Display> getEntityClass()
	{
		return Display.class;
	}
}
