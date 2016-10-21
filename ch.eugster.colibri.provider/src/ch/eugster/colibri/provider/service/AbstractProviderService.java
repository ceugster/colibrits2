package ch.eugster.colibri.provider.service;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.Activator;
import ch.eugster.colibri.provider.configuration.IProperty;

public abstract class AbstractProviderService implements ProviderService
{
	protected boolean failOverMode;
	
	protected ComponentContext context;

	protected LogService logService;

	protected EventAdmin eventAdmin;
	
	protected PersistenceService persistenceService;

	protected Map<String, IProperty> properties = new HashMap<String, IProperty>();
	
	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}
	
	protected void setEventAdmin(EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	protected void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

	protected void unsetEventAdmin(EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}
	
	protected void unsetPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = null;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.context = componentContext;
		log(LogService.LOG_DEBUG, "Service " + this.context.getProperties().get("component.name") + " aktiviert.");
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		log(LogService.LOG_DEBUG, "Service " + this.context.getProperties().get("component.name")
					+ " deaktiviert.");
		this.context = null;
	}
	
	protected void log(int severity, String msg)
	{
		if (this.logService != null)
		{
			this.logService.log(severity, msg);
		}
	}
	
	protected IStatus getStatus(Exception e)
	{
		if (e == null)
		{
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		}
		else
		{
			return new Status(IStatus.ERROR, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_PROVIDER_UPDATE.topic(), new Exception("Die Verbindung zu " + this.getName() + " kann nicht hergestellt werden."));
		}
	}

	protected void loadProperties(ConnectionService connectionService, String providerId, Map<String, IProperty> defaultProperties)
	{
		properties = defaultProperties;
		final ProviderPropertyQuery query = (ProviderPropertyQuery) connectionService
				.getQuery(ProviderProperty.class);
		Map<String, ProviderProperty>  providerProperties = query.selectByProviderAsMap(providerId);
		for (final ProviderProperty providerProperty : providerProperties.values())
		{
			IProperty property = properties.get(providerProperty.getKey());
			if (property != null)
			{
				property.setPersistedProperty(providerProperty);
			}
		}
		final SalespointQuery salespointQuery = (SalespointQuery) connectionService.getQuery(
				Salespoint.class);
		Salespoint salespoint = salespointQuery.getCurrentSalespoint();
		if (salespoint != null)
		{
			providerProperties = query.selectByProviderAndSalespointAsMap(providerId, salespoint);
			for (final ProviderProperty providerProperty : providerProperties.values())
			{
				IProperty property = properties.get(providerProperty.getKey());
				if (property != null)
				{
					property.setPersistedProperty(providerProperty);
				}
			}
		}
	}
}
