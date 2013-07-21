/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class PaymentTypeReplicator extends AbstractEntityReplicator<PaymentType>
{
	public PaymentTypeReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{

		int i = 0;

		final PaymentTypeQuery query = (PaymentTypeQuery) this.persistenceService.getServerService().getQuery(PaymentType.class);
		final Collection<PaymentType> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final PaymentType source : sources)
			{
				PaymentType target = (PaymentType) this.persistenceService.getCacheService().find(PaymentType.class, source.getId());
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
					target = (PaymentType) this.persistenceService.getCacheService().merge(target);
					target.getCurrency().addPaymentType(target);
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
	protected PaymentType replicate(final PaymentType source)
	{
		final PaymentType target = this.replicate(source, PaymentType.newInstance(source.getPaymentTypeGroup()));
		return target;
	}

	@Override
	protected PaymentType replicate(final PaymentType source, PaymentType target)
	{
		final Currency currency = (Currency) this.persistenceService.getCacheService().find(Currency.class, source.getCurrency().getId());

		target = super.replicate(source, target);
		target.setAccount(source.getAccount());
		target.setChange(source.isChange());
		target.setCode(source.getCode());
		target.setCurrency(currency);
		target.setMappingId(source.getMappingId());
		target.setName(source.getName());
		target.setOpenCashdrawer(source.isOpenCashdrawer());
		target.setPaymentTypeGroup(source.getPaymentTypeGroup());
		target.setUndeletable(source.isUndeletable());
		target.setProductGroup(source.getProductGroup());
		target.setPercentualCharge(source.getPercentualCharge());
		target.setFixCharge(source.getFixCharge());
		target.setChargeType(source.getChargeType());
		target.setUndeletable(source.isUndeletable());
//		target.setValue(source.getValue());
		return target;
	}
}
