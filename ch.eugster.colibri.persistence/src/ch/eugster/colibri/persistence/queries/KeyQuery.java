package ch.eugster.colibri.persistence.queries;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionBuilder;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.model.payment.PaymentTypeGroup;

public class KeyQuery extends AbstractQuery<Key>
{
	@Override
	protected Class<Key> getEntityClass()
	{
		return Key.class;
	}
	
	public Collection<Key> selectVouchers(Profile profile, Currency currency)
	{
		Collection<Key> vouchers = new ArrayList<Key>();
		Expression expression = new ExpressionBuilder(this.getEntityClass()).get("deleted").equal(false);
		expression = expression.and(new ExpressionBuilder().get("keyType").equal(KeyType.PAYMENT_TYPE));
		Collection<Key> keys = this.select(expression);
		for (Key key : keys)
		{
			PaymentType paymentType = (PaymentType) this.getConnectionService().find(PaymentType.class, key.getParentId());
			if (paymentType != null && paymentType.getPaymentTypeGroup().equals(PaymentTypeGroup.VOUCHER) && paymentType.getCurrency().getId().equals( currency.getId()))
			{
				key.paymentType = paymentType;
				vouchers.add(key);
			}
		}
		return vouchers;
	}
}
