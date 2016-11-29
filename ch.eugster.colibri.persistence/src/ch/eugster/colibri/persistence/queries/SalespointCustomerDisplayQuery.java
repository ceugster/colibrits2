package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;

public class SalespointCustomerDisplayQuery extends AbstractQuery<SalespointCustomerDisplaySettings>
{
	@Override
	public List<SalespointCustomerDisplaySettings> selectAll(final boolean deletedToo)
	{
		final Expression group = new ExpressionBuilder(SalespointCustomerDisplaySettings.class);
		if (!deletedToo)
		{
			group.getBuilder().get("deleted").equal(deletedToo);
		}
		try
		{
			return this.select(group);
		}
		catch (Exception e)
		{
			return new ArrayList<SalespointCustomerDisplaySettings>();
		}
	}

	@Override
	protected Class<SalespointCustomerDisplaySettings> getEntityClass()
	{
		return SalespointCustomerDisplaySettings.class;
	}
}
