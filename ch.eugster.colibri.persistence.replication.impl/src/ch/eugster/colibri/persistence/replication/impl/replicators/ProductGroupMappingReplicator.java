/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.queries.ProductGroupMappingQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProductGroupMappingReplicator extends AbstractEntityReplicator<ProductGroupMapping>
{
	public ProductGroupMappingReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{

		int i = 0;

		final ProductGroupMappingQuery query = (ProductGroupMappingQuery) this.persistenceService.getServerService()
				.getQuery(ProductGroupMapping.class);
		final Collection<ProductGroupMapping> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final ProductGroupMapping source : sources)
			{
				ProductGroupMapping target = (ProductGroupMapping) this.persistenceService.getCacheService().find(
						ProductGroupMapping.class, source.getId());
				if ((target == null) || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getProductGroup().getId().equals(source.getProductGroup().getId()))
						{
							final ProductGroup productGroup = (ProductGroup) this.persistenceService.getCacheService()
									.find(ProductGroup.class, source.getProductGroup().getId());
							target.setProductGroup(productGroup);
						}
						if (!target.getExternalProductGroup().getId().equals(source.getExternalProductGroup().getId()))
						{
							final ExternalProductGroup externalProductGroup = (ExternalProductGroup) this.persistenceService
									.getCacheService().find(ExternalProductGroup.class,
											source.getExternalProductGroup().getId());
							target.setExternalProductGroup(externalProductGroup);
						}
						target = this.replicate(source, target);
					}
					if (target != null)
					{
						target = (ProductGroupMapping) this.persistenceService.getCacheService().merge(target);
						target.getExternalProductGroup().setProductGroupMapping(target);
						target.getProductGroup().addProductGroupMapping(target);
					}
				}
				if (monitor != null)
				{
					i++;
					monitor.worked(i);
				}
			}
		}
		finally
		{
			if (monitor != null)
			{
				monitor.done();
			}
		}

	}

	@Override
	protected ProductGroupMapping replicate(final ProductGroupMapping source)
	{
		ProductGroupMapping target = null;
		if (source.getProductGroup() != null && source.getExternalProductGroup() != null)
		{
			final ProductGroup productGroup = (ProductGroup) this.persistenceService.getCacheService().find(
					ProductGroup.class, source.getProductGroup().getId());
			final ExternalProductGroup externalProductGroup = (ExternalProductGroup) this.persistenceService
					.getCacheService().find(ExternalProductGroup.class, source.getExternalProductGroup().getId());
			target = this.replicate(source, ProductGroupMapping.newInstance(productGroup, externalProductGroup));
		}
		return target;
	}

	@Override
	protected ProductGroupMapping replicate(final ProductGroupMapping source, ProductGroupMapping target)
	{
		target = super.replicate(source, target);
		return target;
	}
}
