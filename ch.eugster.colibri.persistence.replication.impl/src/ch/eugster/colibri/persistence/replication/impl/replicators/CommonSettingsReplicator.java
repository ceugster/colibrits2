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
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CommonSettingsReplicator extends AbstractEntityReplicator<CommonSettings>
{
	public CommonSettingsReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final CommonSettingsQuery query = (CommonSettingsQuery) this.persistenceService.getServerService().getQuery(
				CommonSettings.class);
		final Collection<CommonSettings> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final CommonSettings source : sources)
			{
				CommonSettings target = (CommonSettings) this.persistenceService.getCacheService().find(
						CommonSettings.class, source.getId());
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

	public void update(final IProgressMonitor monitor)
	{
		int i = 0;

		final CommonSettingsQuery query = (CommonSettingsQuery) this.persistenceService.getServerService().getQuery(
				CommonSettings.class);
		final Collection<CommonSettings> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final CommonSettings source : sources)
			{
				if (source.getDefaultProductGroup() != null)
				{
					final ProductGroup productGroup = (ProductGroup) this.persistenceService.getCacheService().find(ProductGroup.class, source.getDefaultProductGroup().getId());
					final CommonSettings target = (CommonSettings) this.persistenceService.getCacheService().find(
							CommonSettings.class, source.getId());
					if ((target.getDefaultProductGroup() == null)
							|| (target.getDefaultProductGroup().getId() != productGroup.getId()))
					{
						target.setDefaultProductGroup(productGroup);
						this.persistenceService.getCacheService().merge(target);
					}
				}
				if (monitor != null)
				{
					i++;
					monitor.worked(i);
				}
				if (source.getPayedInvoice() != null)
				{
					final ProductGroup productGroup = (ProductGroup) this.persistenceService.getCacheService().find(ProductGroup.class, source.getPayedInvoice().getId());
					final CommonSettings target = (CommonSettings) this.persistenceService.getCacheService().find(
							CommonSettings.class, source.getId());
					if ((target.getPayedInvoice() == null)
							|| (target.getPayedInvoice().getId() != productGroup.getId()))
					{
						target.setPayedInvoice(productGroup);
						this.persistenceService.getCacheService().merge(target);
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
	protected CommonSettings replicate(final CommonSettings source)
	{
		final CommonSettings target = this.replicate(source, CommonSettings.newInstance());
		return target;
	}

	@Override
	protected CommonSettings replicate(final CommonSettings source, CommonSettings target)
	{
		final Currency currency = (Currency) this.persistenceService.getCacheService().find(Currency.class,
				source.getReferenceCurrency().getId());
		ProductGroup productGroup = null;
		if (source.getDefaultProductGroup() != null)
		{
			productGroup = (ProductGroup) this.persistenceService.getCacheService().find(ProductGroup.class,
					source.getDefaultProductGroup().getId());
		}
		ProductGroup payedInvoice = null;
		if (source.getPayedInvoice() != null)
		{
			payedInvoice = (ProductGroup) this.persistenceService.getCacheService().find(ProductGroup.class,
					source.getPayedInvoice().getId());
		}

		target = super.replicate(source, target);
		target.setDefaultProductGroup(productGroup);
		target.setPayedInvoice(payedInvoice);
		target.setReferenceCurrency(currency);
		target.setHostnameResolver(source.getHostnameResolver());
		target.setMaxPaymentAmount(source.getMaxPaymentAmount());
		target.setMaxPaymentRange(source.getMaxPaymentRange());
		target.setMaxPriceAmount(source.getMaxPriceAmount());
		target.setMaxPriceRange(source.getMaxPriceRange());
		target.setMaxQuantityAmount(source.getMaxQuantityAmount());
		target.setMaxQuantityRange(source.getMaxQuantityRange());
		target.setProvider(source.getProvider());
		target.setTaxInclusive(source.isTaxInclusive());
		target.setReceiptNumberFormat(source.getReceiptNumberFormat());
		target.setAllowTestSettlement(source.isAllowTestSettlement());
		target.setTransferDelay(source.getTransferDelay());
		target.setTransferRepeatDelay(source.getTransferRepeatDelay());
		target.setTransferReceiptCount(source.getTransferReceiptCount());
		target.setMaximizedClientWindow(source.isMaximizedClientWindow());
		target.setForceSettlement(source.isForceSettlement());
		return target;
	}
}
