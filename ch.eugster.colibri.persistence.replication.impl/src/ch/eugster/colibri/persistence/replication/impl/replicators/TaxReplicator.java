/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;

import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TaxReplicator extends AbstractEntityReplicator<Tax>
{
	public TaxReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final TaxQuery query = (TaxQuery) this.persistenceService.getServerService().getQuery(Tax.class);
		final Collection<Tax> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Tax source : sources)
			{
				Tax target = (Tax) this.persistenceService.getCacheService().find(Tax.class, source.getId());
				if ((target == null) || force || (target.getUpdate() != source.getVersion()))
				{
					if (target == null)
					{
						target = this.replicate(source);
					}
					else
					{
						if (!target.getTaxRate().getId().equals(source.getTaxRate().getId()))
						{
							final TaxRate taxRate = (TaxRate) this.persistenceService.getCacheService().find(TaxRate.class, source.getTaxRate().getId());
							target.setTaxRate(taxRate);
						}
						if (!target.getTaxType().getId().equals(source.getTaxType().getId()))
						{
							final TaxType taxType = (TaxType) this.persistenceService.getCacheService().find(TaxType.class, source.getTaxType().getId());
							target.setTaxType(taxType);
						}
						target = this.replicate(source, target);
					}
					target = (Tax) this.persistenceService.getCacheService().merge(target);
					target.getTaxRate().addTax(target);
					target.getTaxType().addTax(target);
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

	public void setCurrentTaxes(final IProgressMonitor monitor)
	{
		int i = 0;

		final TaxQuery query = (TaxQuery) this.persistenceService.getServerService().getQuery(Tax.class);
		final Collection<Tax> sources = query.selectAll(true);
		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Tax source : sources)
			{
				final Tax target = (Tax) this.persistenceService.getCacheService().find(Tax.class, source.getId());
				if (target.getCurrentTax() == null)
				{
					target.setCurrentTax((CurrentTax) this.persistenceService.getCacheService().find(CurrentTax.class, source.getCurrentTax().getId()));
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
	protected Tax replicate(final Tax source)
	{
		final TaxRate taxRate = (TaxRate) this.persistenceService.getCacheService().find(TaxRate.class, source.getTaxRate().getId());
		final TaxType taxType = (TaxType) this.persistenceService.getCacheService().find(TaxType.class, source.getTaxType().getId());
		final Tax target = this.replicate(source, Tax.newInstance(taxRate, taxType));
		return target;
	}

	@Override
	protected Tax replicate(final Tax source, Tax target)
	{
		target = super.replicate(source, target);
		target.setAccount(source.getAccount());
		target.setText(source.getText());
		return target;
	}
}
