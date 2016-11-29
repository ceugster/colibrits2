package ch.eugster.colibri.persistence.queries;

import java.util.List;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.TaxCodeMapping;

public class TaxCodeMappingQuery extends AbstractQuery<TaxCodeMapping>
{
//	public TaxCodeMapping findTaxCodeMapping(final String code)
//	{
//		return this.find(new ExpressionBuilder(TaxCodeMapping.class).get("code").equal(code));
//	}

	public TaxCodeMapping selectTaxCodeMappingByProviderAndCode(final String provider, final String taxCode)
	{
		Expression expression = new ExpressionBuilder().get("provider").equal(provider);
		expression = expression.and(new ExpressionBuilder().get("code").equal(taxCode));
		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
		try
		{
			List<TaxCodeMapping> mappings = this.select(expression);
			return mappings.size() == 0 ? null : mappings.iterator().next();
		}
		catch (Exception e)
		{
			return null;
		}
	}

	@Override
	protected Class<TaxCodeMapping> getEntityClass()
	{
		return TaxCodeMapping.class;
	}
}
