package ch.eugster.colibri.persistence.queries;

import java.util.Collection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;

public class PaymentTypeQuery extends AbstractQuery<PaymentType>
{
//	public Collection<PaymentType> selectByChange(final boolean isChange)
//	{
//		final ExpressionBuilder builder = new ExpressionBuilder(PaymentType.class);
//		final Expression change = builder.get("changer").equal(isChange);
//		return this.select(change);
//	}

	public PaymentType findByMappingId(final String code)
	{
		final ExpressionBuilder expressionBuilder = new ExpressionBuilder(PaymentType.class);
		final Expression expression = expressionBuilder.get("mappingId").equal(code);
		return this.find(expression);
	}

	public Collection<PaymentType> selectByChange(final boolean isChange)
	{
		Expression expression = new ExpressionBuilder(PaymentType.class).get("changer").equal(isChange);
		expression = expression.and(new ExpressionBuilder().get("paymentTypeGroup").equal(PaymentTypeGroup.CASH));
		return this.select(expression);
	}

	public Collection<PaymentType> selectByGroup(final PaymentTypeGroup paymentTypeGroup)
	{
		final Expression builder = new ExpressionBuilder().get("paymentTypeGroup").equal(paymentTypeGroup);
		return this.select(builder);
	}

	public Collection<PaymentType> selectByPaymentTypeGroupAndCurrency(final PaymentTypeGroup paymentTypeGroup,
			final Currency currency)
	{
		final Expression currencyCriteria = new ExpressionBuilder().get("currency").equal(currency);
		final Expression paymentTypeGroupCriteria = new ExpressionBuilder().get("paymentTypeGroup").equal(
				paymentTypeGroup);
		return this.select(paymentTypeGroupCriteria.and(currencyCriteria));
	}

	public long countWithoutMapping()
	{
		final ExpressionBuilder expressionBuilder = new ExpressionBuilder(PaymentType.class);
		final Expression isNull = expressionBuilder.get("mappingId").isNull();
		final Expression isEmpty = expressionBuilder.get("mappingId").equal("");
		final Expression deleted = expressionBuilder.get("deleted").equal(false);
		return this.count(deleted.and(isNull.or(isEmpty)));
	}

	@Override
	protected Class<PaymentType> getEntityClass()
	{
		return PaymentType.class;
	}
}
