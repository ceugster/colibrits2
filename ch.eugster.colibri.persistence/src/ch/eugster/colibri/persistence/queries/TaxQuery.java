package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;

public class TaxQuery extends AbstractQuery<Tax>
{
	public List<Tax> selectByTaxTypeAndTaxRate(final TaxType taxType, final TaxRate taxRate)
	{
		final Expression types = new ExpressionBuilder().get("taxType").equal(taxType);
		final Expression rates = new ExpressionBuilder().get("taxRate").equal(taxRate);
		try
		{
			return this.select(types.and(rates));
		}
		catch (Exception e)
		{
			return new ArrayList<Tax>();
		}
	}

	@Override
	protected Class<Tax> getEntityClass()
	{
		return Tax.class;
	}
}
