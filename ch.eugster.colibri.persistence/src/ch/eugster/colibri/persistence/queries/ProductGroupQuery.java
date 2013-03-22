package ch.eugster.colibri.persistence.queries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Position.Option;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;

public class ProductGroupQuery extends AbstractQuery<ProductGroup>
{
	public long countByProductGroupType(final ProductGroupType productGroupType)
	{
		final ExpressionBuilder expressionBuilder = new ExpressionBuilder(ProductGroup.class);
		final Expression expression = expressionBuilder.get("productGroupType").equal(productGroupType);
		return this.count(expression);
	}

	public ProductGroup findByCode(final String code)
	{
		final ExpressionBuilder expressionBuilder = new ExpressionBuilder(ProductGroup.class);
		final Expression expression = expressionBuilder.get("code").equal(code);
		return this.find(expression);
	}

	public boolean isCodeUnique(final String code, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("code", code);
		return this.isUniqueValue(params, id);
	}

	public boolean isNameUnique(final String name, final Long id)
	{
		final Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", name);
		return this.isUniqueValue(params, id);
	}

	public Collection<ProductGroup> selectByProductGroupType(final ProductGroupType productGroupType)
	{
		final ExpressionBuilder expressionBuilder = new ExpressionBuilder(ProductGroup.class);
		final Expression expression = expressionBuilder.get("productGroupType").equal(productGroupType);
		return this.select(expression);
	}

	public Collection<ProductGroup> selectByProductGroupTypeWithoutPayedInvoice(final ProductGroupType productGroupType)
	{
		final ExpressionBuilder criteria = new ExpressionBuilder(ProductGroup.class);
		Expression expression = criteria.get("productGroupType").equal(productGroupType)
				.and(new ExpressionBuilder().get("proposalOption").notEqual(Option.PAYED_INVOICE));
		return this.select(expression);
	}

	public long countWithoutMapping()
	{
		final ExpressionBuilder expressionBuilder = new ExpressionBuilder(ProductGroup.class);
		final Expression isNull = expressionBuilder.get("mappingId").isNull();
		final Expression isEmpty = expressionBuilder.get("mappingId").equal("");
		final Expression deleted = expressionBuilder.get("deleted").equal(false);
		return this.count(deleted.and(isNull.or(isEmpty)));
	}

	@Override
	protected Class<ProductGroup> getEntityClass()
	{
		return ProductGroup.class;
	}
}
