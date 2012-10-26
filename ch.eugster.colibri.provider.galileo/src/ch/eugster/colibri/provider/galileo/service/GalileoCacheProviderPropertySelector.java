package ch.eugster.colibri.provider.galileo.service;

import java.util.Map;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
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
		return GalileoConfiguration.Property.values();
	}

	@Override
	public String getProviderId()
	{
		return Activator.PLUGIN_ID;
	}

	@Override
	public Map<String, ProviderProperty> getProviderProperties()
	{
		return this.getProviderProperties(null);
	}

	@Override
	public Map<String, ProviderProperty> getProviderProperties(final Salespoint salespoint)
	{
		final ProviderPropertyQuery service = (ProviderPropertyQuery) this.persistenceService.getCacheService()
				.getQuery(ProviderProperty.class);

		final Map<String, ProviderProperty> providerProperties = service.selectByProviderAsMap(this.getProviderId());

		if (salespoint instanceof Salespoint)
		{
			final Map<String, ProviderProperty> providerSalespointProperties = service
					.selectByProviderAndSalespointAsMap(this.getProviderId(), salespoint);
			if (providerProperties.size() != providerSalespointProperties.size())
			{
				final IProperty[] properties = GalileoConfiguration.Property.values();
				for (final IProperty property : properties)
				{
					final ProviderProperty providerProperty = providerSalespointProperties.get(property.key());
					if (providerProperty != null)
					{
						providerProperties.put(property.key(), providerProperty);
					}
				}
			}
		}
		return providerProperties;
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
