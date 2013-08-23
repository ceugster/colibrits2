/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ProductGroupReplicator extends AbstractEntityReplicator<ProductGroup>
{
	public ProductGroupReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final ProductGroupQuery query = (ProductGroupQuery) this.persistenceService.getServerService().getQuery(
				ProductGroup.class);
		final Collection<ProductGroup> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final ProductGroup source : sources)
			{
				ProductGroup target = (ProductGroup) this.persistenceService.getCacheService().find(ProductGroup.class,
						source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						target = this.replicate(source, target);
					}
					this.persistenceService.getCacheService().merge(target);
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
	protected ProductGroup replicate(final ProductGroup source)
	{
		final CommonSettingsQuery commonSettingsQuery = (CommonSettingsQuery) this.persistenceService.getCacheService()
				.getQuery(CommonSettings.class);
		final CommonSettings commonSettings = commonSettingsQuery.findDefault();
		final ProductGroup target = this.replicate(source,
				ProductGroup.newInstance(source.getProductGroupType(), commonSettings));
		return target;
	}

	@Override
	protected ProductGroup replicate(final ProductGroup source, ProductGroup target)
	{
		target = super.replicate(source, target);
		target.setAccount(source.getAccount());
		target.setCode(source.getCode());
		if (source.getDefaultTax() != null)
		{
			target.setDefaultTax((Tax) this.persistenceService.getCacheService().find(Tax.class,
					source.getDefaultTax().getId()));
		}
		if (source.getPaymentType() != null)
		{
			target.setPaymentType((PaymentType) this.persistenceService.getCacheService().find(PaymentType.class,
					source.getPaymentType().getId()));
		}
		target.setProductGroupType(source.getProductGroupType());
		target.setMappingId(source.getMappingId());
		target.setName(source.getName());
		target.setPriceProposal(source.getPriceProposal());
		target.setProposalOption(source.getProposalOption());
		target.setQuantityProposal(source.getQuantityProposal());
		return target;
	}
}
