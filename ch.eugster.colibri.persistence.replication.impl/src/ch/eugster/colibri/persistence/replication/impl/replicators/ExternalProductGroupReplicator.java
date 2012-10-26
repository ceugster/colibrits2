/*
 * Created on 25.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.replication.impl.replicators;

import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ExternalProductGroupReplicator extends AbstractEntityReplicator<ExternalProductGroup>
{
	public ExternalProductGroupReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor)
	{
		int i = 0;

		final ExternalProductGroupQuery query = (ExternalProductGroupQuery) this.persistenceService.getServerService().getQuery(ExternalProductGroup.class);
		final Collection<ExternalProductGroup> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final ExternalProductGroup source : sources)
			{
				ExternalProductGroup target = (ExternalProductGroup) this.persistenceService.getCacheService().find(ExternalProductGroup.class, source.getId());
				if ((target == null) || (target.getUpdate() != source.getVersion()))
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

	public IStatus setProductGroupMapping(final IProgressMonitor monitor)
	{
		final IStatus status = Status.OK_STATUS;
		int i = 0;

		final ExternalProductGroupQuery query = (ExternalProductGroupQuery) this.persistenceService.getServerService().getQuery(ExternalProductGroup.class);
		final Collection<ExternalProductGroup> sources = query.selectAll(true);
		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final ExternalProductGroup source : sources)
			{
				final ExternalProductGroup target = (ExternalProductGroup) this.persistenceService.getCacheService().find(ExternalProductGroup.class, source.getId());
				if (target.getProductGroupMapping() == null)
				{
					final Long id = source.getProductGroupMapping().getId();
					final ProductGroupMapping mapping = (ProductGroupMapping) this.persistenceService.getCacheService().find(ProductGroupMapping.class, id);
					target.setProductGroupMapping(mapping);
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
		return status;
	}

	@Override
	protected ExternalProductGroup replicate(final ExternalProductGroup source)
	{
		final ExternalProductGroup target = this.replicate(source, ExternalProductGroup.newInstance(source.getProvider()));
		return target;
	}

	@Override
	protected ExternalProductGroup replicate(final ExternalProductGroup source, ExternalProductGroup target)
	{
		target = super.replicate(source, target);
		target.setAccount(source.getAccount());
		target.setCode(source.getCode());
		target.setText(source.getText());
		return target;
	}

}
