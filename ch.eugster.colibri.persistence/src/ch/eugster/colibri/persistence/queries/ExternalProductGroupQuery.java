package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.ExternalProductGroup;

public class ExternalProductGroupQuery extends AbstractQuery<ExternalProductGroup>
{
	public long countUnmapped(final String providerId)
	{
		Expression expression = new ExpressionBuilder(ExternalProductGroup.class).get("provider").equal(providerId);
		Expression type = new ExpressionBuilder().isEmpty("productGroupMappings");
		type = type.or(new ExpressionBuilder().allOf("productGroupMappings", new ExpressionBuilder().get("deleted").equal(true)));
		expression = expression.and(type);
		return this.count(expression);
	}
	
	public List<ExternalProductGroup> selectByProvider(final String provider)
	{
		try
		{
			final Expression p = new ExpressionBuilder().get("provider").equal(provider);
			return this.select(p);
		}
		catch (Exception e)
		{
			return new ArrayList<ExternalProductGroup>();
		}
	}

	public ExternalProductGroup selectByProviderAndCode(final String provider, final String code)
	{
		final Expression providerCriteria = new ExpressionBuilder(ExternalProductGroup.class).get("provider").equal(
				provider);
		final Expression codeCriteria = new ExpressionBuilder().get("code").equal(code);
		final Expression deletedCriteria = new ExpressionBuilder().get("deleted").equal(false);
		return this.find(providerCriteria.and(codeCriteria).and(deletedCriteria));
	}

	public List<ExternalProductGroup> selectUnmapped(final String providerId)
	{
		try
		{
			Expression expression = new ExpressionBuilder(ExternalProductGroup.class).get("provider").equal(providerId);
			expression = expression.and(new ExpressionBuilder().get("deleted").equal(false));
			Expression type = new ExpressionBuilder().get("productGroupMapping").isNull();
			type = type.or(new ExpressionBuilder().get("productGroupMapping").get("deleted").equal(true));
			expression = expression.and(type);
			List<ExternalProductGroup> externalProductGroups = this.select(expression);
			return externalProductGroups;
		}
		catch (Exception e)
		{
			return new ArrayList<ExternalProductGroup>();
		}
	}
	
	public List<ExternalProductGroup> selectMapped(final String providerId)
	{
		try
		{
			Expression expression = new ExpressionBuilder(ExternalProductGroup.class).get("provider").equal(providerId);
			Expression type = new ExpressionBuilder().get("productGroupMapping").notNull();
			type = type.and(new ExpressionBuilder().get("productGroupMapping").get("deleted").equal(false));
			expression = expression.and(type);
			List<ExternalProductGroup> externalProductGroups = this.select(expression);
			return externalProductGroups;
		}
		catch (Exception e)
		{
			return new ArrayList<ExternalProductGroup>();
		}
	}
	
	public boolean isCodeUnique(String code, Long id)
	{
		Map<String, Object> codes = new HashMap<String, Object>();
		codes.put("code", code);
		return this.isUniqueValue(codes, id);
	}

	@Override
	protected Class<ExternalProductGroup> getEntityClass()
	{
		return ExternalProductGroup.class;
	}

}
