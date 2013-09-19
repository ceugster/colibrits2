package ch.eugster.colibri.provider.galileo.service;

import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.configuration.ProviderConfiguration;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.service.ProviderPropertySelector;

public class GalileoCacheProviderPropertySelector implements ProviderPropertySelector
{
	private PersistenceService persistenceService;

	private LogService logService;

	@Override
	public ProviderConfiguration getConfiguration()
	{
		return new GalileoConfiguration();
	}

	@Override
	public IProperty[] getProperties()
	{
		return GalileoConfiguration.GalileoProperty.values();
	}

	@Override
	public String getProviderId()
	{
		return Activator.PLUGIN_ID;
	}

	@Override
	public Map<String, IProperty> getProviderProperties()
	{
		Map<String, IProperty> properties = null;
		SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getServerService().getQuery(Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint != null)
		{
			properties = getProviderProperties(salespoint);
		}
		else
		{
			properties = GalileoConfiguration.GalileoProperty.asMap();
			ProviderPropertyQuery providerPropertyQuery = (ProviderPropertyQuery) persistenceService.getServerService().getQuery(ProviderProperty.class);
			Collection<ProviderProperty> providerProperties = providerPropertyQuery.selectByProvider(this.getProviderId());
			for (ProviderProperty providerProperty : providerProperties)
			{
				IProperty property = properties.get(providerProperty.getKey());
				property.setPersistedProperty(providerProperty);
			}
		}
		return properties;
	}

	@Override
	public Map<String, IProperty> getProviderProperties(final Salespoint salespoint)
	{
		Map<String, IProperty> properties = GalileoConfiguration.GalileoProperty.asMap();
		ProviderPropertyQuery providerPropertyQuery = (ProviderPropertyQuery) persistenceService.getServerService().getQuery(ProviderProperty.class);
		Collection<ProviderProperty> providerProperties = providerPropertyQuery.selectByProviderAndSalespoint(this.getProviderId(), salespoint);
		for (ProviderProperty providerProperty : providerProperties)
		{
			IProperty property = properties.get(providerProperty.getKey());
			property.setPersistedProperty(providerProperty);
		}
		return properties;
	}

	public void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	public void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	public void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	public void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	protected void activate(final ComponentContext context)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service GalileoCacheProviderPropertySelector aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext context)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service GalileoCacheProviderPropertySelector deaktiviert.");
		}
	}

}
