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

import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class SalespointReplicator extends AbstractEntityReplicator<Salespoint>
{
	public SalespointReplicator(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public void replicate(final IProgressMonitor monitor, boolean force)
	{
		int i = 0;

		final SalespointQuery query = (SalespointQuery) this.persistenceService.getServerService().getQuery(
				Salespoint.class);
		final Collection<Salespoint> sources = query.selectAll(true);

		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Salespoint source : sources)
			{
				Salespoint target = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class,
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

	// @Override
	// public IStatus replicate(final Salespoint source)
	// {
	// IStatus status = Status.OK_STATUS;
	// final Salespoint target = this.replicate(source,
	// Salespoint.newInstance());
	// if (cacheService.merge(target) == null)
	// {
	// status = new Status(IStatus.ERROR, Activator.PLUGIN_ID,
	// "Speichern des Kassenobjekts fehlgeschlagen.");
	// }
	// return status;
	// }

	@Override
	public Salespoint replicate(final Salespoint source)
	{
		final CommonSettingsQuery query = (CommonSettingsQuery) this.persistenceService.getCacheService().getQuery(
				CommonSettings.class);
		final CommonSettings commonSettings = query.findDefault();
		final Salespoint target = this.replicate(source, Salespoint.newInstance(commonSettings));
		return target;
	}

	public void setPrintoutAndDisplay(final IProgressMonitor monitor)
	{
		int i = 0;

		final SalespointQuery query = (SalespointQuery) this.persistenceService.getServerService().getQuery(
				Salespoint.class);
		final Collection<Salespoint> sources = query.selectAll(true);
		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Salespoint source : sources)
			{
				final Salespoint target = (Salespoint) this.persistenceService.getCacheService().find(Salespoint.class,
						source.getId());
				boolean update = false;

				final Collection<Printout> printouts = source.getPrintouts();
				if (printouts.size() > 0)
				{
					for (final Printout printout : printouts)
					{
						if (target.getPrintout(printout.getPrintoutType()) == null)
						{
							final Printout cachePrintout = (Printout) this.persistenceService.getCacheService().find(
									Printout.class, printout.getId());
							if (cachePrintout != null)
							{
								target.putPrintout(printout);
								update = true;
							}
						}
					}
				}

				final Display sourceDisplay = source.getDisplay();
				if (sourceDisplay != null)
				{
					if (target.getDisplay() == null)
					{
						final Display cacheDisplay = (Display) this.persistenceService.getCacheService().find(
								Display.class, sourceDisplay.getId());
						target.setDisplay(cacheDisplay);
						update = true;
					}
					else if (!target.getDisplay().getId().equals(sourceDisplay.getId()))
					{
						final Display cacheDisplay = (Display) this.persistenceService.getCacheService().find(
								Display.class, sourceDisplay.getId());
						target.setDisplay(cacheDisplay);
						update = true;
					}
				}
				else
				{
					if (target.getDisplay() != null)
					{
						target.setDisplay(null);
						update = true;
					}
				}

				if (update)
				{
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

	public IStatus updatePeriphery(final IProgressMonitor monitor)
	{
		final IStatus status = Status.OK_STATUS;
		int i = 0;

		final SalespointQuery query = (SalespointQuery) this.persistenceService.getServerService().getQuery(
				Salespoint.class);
		final Collection<Salespoint> sources = query.selectAll(true);
		if (monitor != null)
		{
			monitor.beginTask("Die Daten werden abgeglichen...", sources.size());
		}

		try
		{
			for (final Salespoint source : sources)
			{
				final Salespoint salespoint = (Salespoint) this.persistenceService.getCacheService().find(
						Salespoint.class, source.getId());
				/*
				 * CustomerDisplaySettings aktualisieren
				 */
				SalespointCustomerDisplaySettings customerDisplay = null;
				if (source.getCustomerDisplaySettings() != null)
				{
					customerDisplay = (SalespointCustomerDisplaySettings) this.persistenceService.getCacheService()
							.find(SalespointCustomerDisplaySettings.class, source.getCustomerDisplaySettings().getId());

				}
				salespoint.setCustomerDisplaySettings(customerDisplay);

				/*
				 * ReceiptPrinterSettings aktualisieren
				 */
				SalespointReceiptPrinterSettings receiptPrinterSettings = null;
				if (source.getReceiptPrinterSettings() != null)
				{
					receiptPrinterSettings = (SalespointReceiptPrinterSettings) this.persistenceService
							.getCacheService().find(SalespointReceiptPrinterSettings.class,
									source.getReceiptPrinterSettings().getId());

				}
				salespoint.setReceiptPrinterSettings(receiptPrinterSettings);

				this.persistenceService.getCacheService().merge(salespoint);

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
	protected Salespoint replicate(final Salespoint source, Salespoint target)
	{
		target = super.replicate(source, target);
		target.setAllowTestSettlement(source.isAllowTestSettlement());
		target.setCommonSettings((CommonSettings) this.persistenceService.getCacheService().find(CommonSettings.class,
				source.getCommonSettings().getId()));
		target.setCurrentParkedReceiptNumber(source.getCurrentParkedReceiptNumber());
		target.setCurrentReceiptNumber(source.getCurrentReceiptNumber());
		target.setExport(source.isExport());
		target.setExportPath(source.getExportPath());
		target.setForceCashCheck(source.isForceCashCheck());
		target.setForceSettlement(source.isForceSettlement());
		target.setHost(source.getHost());
		target.setLocalProviderProperties(source.isLocalProviderProperties());
		target.setLocation(source.getLocation());
		target.setMapping(source.getMapping());
		target.setName(source.getName());
		target.setPaymentType((PaymentType) this.persistenceService.getCacheService().find(PaymentType.class,
				source.getPaymentType().getId()));
		target.setProfile((Profile) this.persistenceService.getCacheService().find(Profile.class,
				source.getProfile().getId()));
		// target.setProposalOption(source.getProposalOption());
		target.setProposalPrice(source.getProposalPrice());
		target.setProposalQuantity(source.getProposalQuantity());
		if (source.getProposalTax() != null)
		{
			target.setProposalTax((Tax) this.persistenceService.getCacheService().find(Tax.class,
					source.getProposalTax().getId()));
		}
		target.setUseIndividualExport(source.isUseIndividualExport());
		return target;
	}

}
