package ch.eugster.colibri.persistence.queries;

import java.util.List;

import ch.eugster.colibri.persistence.model.ProductGroupMapping;

public class ProductGroupMappingQuery extends AbstractQuery<ProductGroupMapping>
{
	@Override
	protected Class<ProductGroupMapping> getEntityClass()
	{
		return ProductGroupMapping.class;
	}
}
