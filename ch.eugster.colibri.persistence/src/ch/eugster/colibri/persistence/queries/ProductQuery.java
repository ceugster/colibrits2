package ch.eugster.colibri.persistence.queries;

import ch.eugster.colibri.persistence.model.Product;

public class ProductQuery extends AbstractQuery<Product>
{
	@Override
	protected Class<Product> getEntityClass()
	{
		return Product.class;
	}
}
