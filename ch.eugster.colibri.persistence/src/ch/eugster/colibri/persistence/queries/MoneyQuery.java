package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.Money;

public class MoneyQuery extends AbstractQuery<Money>
{
	@Override
	protected Class<Money> getEntityClass()
	{
		return Money.class;
	}
}
