package ch.eugster.colibri.provider.galileo.service;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.galileo.Activator;
import ch.eugster.colibri.provider.galileo.config.GalileoConfiguration;
import ch.eugster.colibri.provider.service.ProviderIdService;

public class GalileoIdService implements ProviderIdService
{
	private LogService logService;

	@Override
	public GalileoConfiguration getConfiguration()
	{
		return Activator.getDefault().getConfiguration();
	}

	@Override
	public IProperty[] getProperties()
	{
		return GalileoConfiguration.GalileoProperty.values();
	}

	@Override
	public String getProviderLabel()
	{
		return "Galileo";
	}

	@Override
	public String getProviderId()
	{
		return Activator.PLUGIN_ID;
	}

	protected void activate(final ComponentContext componentContext)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name")
					+ " aktiviert.");
		}
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		if (this.logService != null)
		{
			this.logService.log(LogService.LOG_INFO, "Service " + componentContext.getProperties().get("component.name")
					+ " deaktiviert.");
		}
	}

	protected void setLogService(final LogService logService)
	{
		this.logService = logService;
	}

	protected void unsetLogService(final LogService logService)
	{
		this.logService = null;
	}

}
