package ch.eugster.colibri.persistence.replication.impl.service;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.queries.VersionQuery;
import ch.eugster.colibri.persistence.replication.impl.Activator;
import ch.eugster.colibri.persistence.replication.impl.replicators.CommonSettingsReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.ConfigurableReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.CurrencyReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.CurrentTaxCodeMappingReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.CurrentTaxReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.CustomerDisplayReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.DisplayAreaReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.DisplayReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.ExternalProductGroupReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.KeyReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.MoneyReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.PaymentTypeReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.PrintoutAreaReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.PrintoutReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.ProductGroupMappingReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.ProductGroupReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.ProfileReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.ProviderPropertyReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.ReceiptPrinterReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.RolePropertyReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.RoleReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.SalespointCustomerDisplayReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.SalespointReceiptPrinterReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.SalespointReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.StockReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.TabReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.TaxCodeMappingReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.TaxRateReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.TaxReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.TaxTypeReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.UserReplicator;
import ch.eugster.colibri.persistence.replication.impl.replicators.VersionReplicator;
import ch.eugster.colibri.persistence.replication.service.ReplicationService;
import ch.eugster.colibri.persistence.rules.LocalDatabaseRule;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ReplicationServiceImpl implements ReplicationService
{
	private LogService logService;

	private PersistenceService persistenceService;

	private PersistenceService getPersistenceService()
	{
		return this.persistenceService;
	}
	
	public boolean isLocalService()
	{
		return this.getPersistenceService().getServerService().isLocal();
	}
	
	@Override
	public IStatus replicate(final Shell shell, final boolean force)
	{
		IStatus status = Status.OK_STATUS;
		if (!this.persistenceService.getServerService().isLocal())
		{
			if (!this.persistenceService.getServerService().isConnected())
			{
				this.persistenceService.getServerService().connect();
			}
			if (this.persistenceService.getServerService().isConnected())
			{
				if (force || checkReplicationValue())
				{
					final IRunnableWithProgress runnable = new IRunnableWithProgress()
					{
						@Override
						public void run(final IProgressMonitor monitor)
						{
							IJobManager manager = Job.getJobManager();
							try
							{
								manager.beginRule(LocalDatabaseRule.getRule(), monitor);
								monitor.beginTask("Die lokalen Daten werden abgeglichen...", 38);
								new VersionReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);

								new CurrencyReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new PaymentTypeReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new MoneyReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);

								new RoleReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new UserReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new RolePropertyReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);

								new TaxRateReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new TaxTypeReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new TaxReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new CurrentTaxReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new TaxCodeMappingReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new CurrentTaxCodeMappingReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new TaxReplicator(ReplicationServiceImpl.this.persistenceService)
										.setCurrentTaxes(new SubProgressMonitor(monitor, 1));

								new ProfileReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new ConfigurableReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new TabReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new KeyReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new ConfigurableReplicator(ReplicationServiceImpl.this.persistenceService)
										.setDefaultTabs(new SubProgressMonitor(monitor, 1));

								new CommonSettingsReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);

								new ProductGroupReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new ExternalProductGroupReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new ProductGroupMappingReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new ExternalProductGroupReplicator(ReplicationServiceImpl.this.persistenceService)
										.setProductGroupMapping(new SubProgressMonitor(monitor, 1));

								new CommonSettingsReplicator(ReplicationServiceImpl.this.persistenceService)
										.update(new SubProgressMonitor(monitor, 1));

								new SalespointReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new StockReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);

								new CustomerDisplayReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new ReceiptPrinterReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);

								new SalespointCustomerDisplayReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new SalespointReceiptPrinterReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);

								new SalespointReplicator(ReplicationServiceImpl.this.persistenceService)
										.updatePeriphery(new SubProgressMonitor(monitor, 1));

								new PrintoutReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new PrintoutAreaReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);

								new DisplayReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new DisplayAreaReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								new SalespointReplicator(ReplicationServiceImpl.this.persistenceService)
										.setPrintoutAndDisplay(new SubProgressMonitor(monitor, 1));

								new ProviderPropertyReplicator(ReplicationServiceImpl.this.persistenceService)
										.replicate(new SubProgressMonitor(monitor, 1), force);
								
							}
							finally
							{
								ReplicationServiceImpl.this.persistenceService.getCacheService().getEntityManagerFactory()
										.getCache().evictAll();
								monitor.done();
								manager.endRule(LocalDatabaseRule.getRule());
							}
						}
					};
					
					final ProgressMonitorDialog dialog = new ProgressMonitorDialog(shell);
					dialog.setCancelable(false);
					try
					{
						dialog.run(true, false, runnable);
					}
					catch (final InterruptedException e)
					{
						status = new Status(IStatus.CANCEL, Activator.PLUGIN_ID, "Das Abgleichen der Daten wurde abgebrochen.",
								e);
					}
					catch (final Exception e)
					{
						e.printStackTrace();
						status = new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Fehler beim replizieren der Daten.", e);
					}
				}
			}
		}
		return status;
	}

	protected void activate(final ComponentContext componentContext)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name")
					+ " aktiviert");
		}
	}
	protected void deactivate(final ComponentContext componentContext)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name")
					+ " deaktiviert");
		}
	}
	
	private int getReplicationValue(ConnectionService service)
	{
		VersionQuery query = (VersionQuery) service.getQuery(Version.class);
		Version version = query.find(Long.valueOf(1L));
		return version == null ? 0 : version.getReplicationValue();
	}

	private boolean checkReplicationValue()
	{
		int serverValue = getReplicationValue(this.persistenceService.getServerService());
		int cacheValue = getReplicationValue(this.persistenceService.getCacheService());
		return serverValue > cacheValue;
	}
	
	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	protected void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

}
