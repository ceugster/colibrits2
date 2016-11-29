package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.TaxRate;

public class TaxRateQuery extends AbstractQuery<TaxRate>
{
	public boolean isCodeUnique(final String code, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return super.isUniqueValue(params, id);
	}

	public TaxRate selectByCode(final String code)
	{
		return this.find(new ExpressionBuilder().get("code").equal(code));
	}

	public List<TaxRate> selectExceptCode(String code)
	{
		try
		{
			return this.select(new ExpressionBuilder().get("code").notEqual(code));
		}
		catch (Exception e)
		{
			return new ArrayList<TaxRate>();
		}
	}

	@Override
	protected Class<TaxRate> getEntityClass()
	{
		return TaxRate.class;
	}
}
