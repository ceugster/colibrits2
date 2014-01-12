/*
 * Created on 25.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.provider.galileo.galserve;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.barcode.service.BarcodeVerifier;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration.GalileoProperty;

public abstract class AbstractGalileoServer implements IServer
{
	private ch.eugster.colibri.provider.galileo.galserve.Igdserve galserve;

	protected GalileoConfiguration configuration = new GalileoConfiguration();

	protected String database;

	protected boolean keepConnection;

	protected boolean connect;
	
	protected boolean open = false;
	
	protected boolean wasOpen;
	
	protected ServiceTracker<LogService, LogService> logServiceTracker;

	protected ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;
	
	protected ServiceTracker<BarcodeVerifier, BarcodeVerifier> barcodeVerifierTracker;

	protected IStatus status;

	protected Igdserve getGalserve()
	{
		return this.galserve;
	}
	
	@Override
	public IStatus start()
	{
		this.status = new Status(IStatus.OK, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		this.logServiceTracker = new ServiceTracker<LogService, LogService>(Activator.getDefault().getBundle().getBundleContext(),
				LogService.class, null);
		this.logServiceTracker.open();
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
		this.barcodeVerifierTracker = new ServiceTracker<BarcodeVerifier, BarcodeVerifier>(Activator.getDefault().getBundle().getBundleContext(), BarcodeVerifier.class, null);
		this.barcodeVerifierTracker.open();
		try
		{
			this.galserve = ch.eugster.colibri.provider.galileo.galserve.sql.ClassFactory.creategdserve();
			Object version = this.galserve.galversion();
			if (version.toString().equals("8"))
			{
				this.galserve = ch.eugster.colibri.provider.galileo.galserve.old.ClassFactory.creategdserve();
				version = this.galserve.galversion();
				System.out.println(version);
			}
		}
		catch (Exception e)
		{
			this.status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(),
					"Die Verbindung zu Warenbewirtschaftung kann nicht hergestellt werden.", e);
		}
		return this.status;
	}

	protected void updateProperties()
	{
		Map<String, IProperty> properties = GalileoConfiguration.GalileoProperty.asMap();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getCacheService()
					.getQuery(ProviderProperty.class);
			Map<String, ProviderProperty>  providerProperties = query.selectByProviderAsMap(this.configuration.getProviderId());
			for (final ProviderProperty providerProperty : providerProperties.values())
			{
				IProperty property = properties.get(providerProperty.getKey());
				property.setPersistedProperty(providerProperty);
			}
			final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getCacheService().getQuery(
					Salespoint.class);
			Salespoint salespoint = salespointQuery.getCurrentSalespoint();
			if (salespoint != null)
			{
				providerProperties = query.selectByProviderAndSalespointAsMap(this.configuration.getProviderId(), salespoint);
				for (final ProviderProperty providerProperty : providerProperties.values())
				{
					IProperty property = properties.get(providerProperty.getKey());
					property.setPersistedProperty(providerProperty);
				}
			}
			this.database = properties.get(GalileoProperty.DATABASE_PATH.key()).value();
			this.connect = Boolean.valueOf(properties.get(GalileoProperty.CONNECT.key()).value()).booleanValue();
			this.keepConnection = Boolean.valueOf(properties.get(GalileoProperty.KEEP_CONNECTION.key()).value()).booleanValue();
		}
	}
	
	protected boolean open()
	{
		this.wasOpen = this.open;
		if (!this.open)
		{
			try
			{
				this.open = this.getGalserve().do_NOpen(this.database);
				if (!this.open)
				{
					this.status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), new Exception("Die Verbindung zu " + this.configuration.getName() + " kann nicht hergestellt werden."));
				}
			}
			catch (Exception e)
			{
				e.printStackTrace();
				this.status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), e);
			}
		}
		return this.open;
	}

	@Override
	public void stop()
	{
		if (this.galserve != null)
		{
			this.galserve = null;
		}
		if (this.persistenceServiceTracker != null)
		{
			this.persistenceServiceTracker.close();
		}
		if (this.barcodeVerifierTracker != null)
		{
			this.barcodeVerifierTracker.close();
		}
		if (this.logServiceTracker != null)
		{
			this.logServiceTracker.close();
		}
		this.status = Status.CANCEL_STATUS;
	}

}
