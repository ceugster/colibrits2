package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.CurrentTaxCodeMapping;

public class CurrentTaxCodeMappingQuery extends AbstractQuery<CurrentTaxCodeMapping>
{
//	public CurrentTaxCodeMapping selectCurrentTaxCodeMappingByProviderAndCode(final String provider, final String taxCode)
//	{
//		Expression expression = new ExpressionBuilder().get("provider").equal(provider);
//		expression = expression.and(new ExpressionBuilder().get("code").equal(taxCode));
//		expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
//		Collection<CurrentTaxCodeMapping> mappings = this.select(expression);
//		return mappings.size() == 0 ? null : mappings.iterator().next();
//	}

	@Override
	protected Class<CurrentTaxCodeMapping> getEntityClass()
	{
		return CurrentTaxCodeMapping.class;
	}
}
