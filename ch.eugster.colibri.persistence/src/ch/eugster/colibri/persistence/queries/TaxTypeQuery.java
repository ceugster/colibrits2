package ch.eugster.colibri.persistence.queries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class TaxTypeQuery extends AbstractQuery<TaxType>
{
	public boolean isCodeUnique(final String code, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return super.isUniqueValue(params, id);
	}

	public TaxType selectByCode(final String code)
	{
		return this.find(new ExpressionBuilder().get("code").equal(code));
	}

	public Collection<Tax> selectTaxes(final ProductGroupType productGroupType)
	{
		final Long taxTypeId = productGroupType.getTaxTypeId();
		if (taxTypeId == null)
		{
			return new Vector<Tax>();
		}
		else
		{
			final TaxType taxType = this.find(taxTypeId);
			return taxType.getTaxes();
		}
	}

	@Override
	protected Class<TaxType> getEntityClass()
	{
		return TaxType.class;
	}
}
