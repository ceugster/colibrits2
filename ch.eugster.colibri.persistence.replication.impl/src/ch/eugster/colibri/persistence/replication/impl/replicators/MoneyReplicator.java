/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.queries.MoneyQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class MoneyReplicator extends AbstractEntityReplicator<Money>
{
	public MoneyReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{

		int i = 0;

		final MoneyQuery query = (MoneyQuery) this.persistenceService.getServerService().getQuery(Money.class);
		final Collection<Money> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Money source : sources)
			{
				Money target = (Money) this.persistenceService.getCacheService().find(Money.class, source.getId());
				if ((target == null) || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getPaymentType().getId().equals(source.getPaymentType().getId()))
						{
							final PaymentType paymentType = (PaymentType) this.persistenceService.getCacheService()
									.find(PaymentType.class, source.getPaymentType().getId());
							target.setPaymentType(paymentType);
						}
						target = this.replicate(source, target);
					}
					target = (Money) this.persistenceService.getCacheService().merge(target);
					target.getPaymentType().addMoney(target);
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
	protected Money replicate(final Money source)
	{
		final PaymentType paymentType = (PaymentType) this.persistenceService.getCacheService().find(PaymentType.class, source.getPaymentType().getId());
		final Money target = this.replicate(source, Money.newInstance(paymentType));
		return target;
	}

	@Override
	protected Money replicate(final Money source, Money target)
	{
		target = super.replicate(source, target);
		target.setValue(source.getValue());
		return target;
	}
}
