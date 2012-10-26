package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.Stock;

public class StockQuery extends AbstractQuery<Stock>
{
	@Override
	protected Class<Stock> getEntityClass()
	{
		return Stock.class;
	}
}
