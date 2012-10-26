package ch.eugster.colibri.persistence.queries;

import java.util.Collection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;

public class SalespointCustomerDisplayQuery extends AbstractQuery<SalespointCustomerDisplaySettings>
{
	@Override
	public Collection<SalespointCustomerDisplaySettings> selectAll(final boolean deletedToo)
	{
		final Expression group = new ExpressionBuilder(SalespointCustomerDisplaySettings.class);
		if (!deletedToo)
		{
			group.getBuilder().get("deleted").equal(deletedToo);
		}
		return this.select(group);
	}

	@Override
	protected Class<SalespointCustomerDisplaySettings> getEntityClass()
	{
		return SalespointCustomerDisplaySettings.class;
	}
}
