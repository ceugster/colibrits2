package ch.eugster.colibri.persistence.connection.service;

import javax.persistence.spi.PersistenceProvider;

import org.eclipse.core.runtime.Platform;
import org.eclipse.persistence.config.QueryHints;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.service.CacheService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.ServerService;

public class PersistenceServiceImpl implements PersistenceService
{
	private int timeout;
	
	private CacheService cacheService;

	private ServerService serverService;

	public PersistenceServiceImpl()
	{
		activate();
	}
	
	@Override
	public String encrypt(String message)
	{
		return Activator.getDefault().encrypt(message);
	}

	@Override
	public String decrypt(String encryptedMessage)
	{
		return Activator.getDefault().decrypt(encryptedMessage);
	}

	@Override
	public CacheService getCacheService()
	{
		if (this.cacheService == null)
		{
			Activator.getDefault().log(LogService.LOG_DEBUG, "Instantiiere Cache Service.");
			this.cacheService = new CacheServiceImpl(this);
		}
		return this.cacheService;
	}

	@Override
	public LogService getLogService()
	{
		return Activator.getDefault().getLogService();
	}

	@Override
	public EventAdmin getEventAdmin()
	{
		return Activator.getDefault().getEventAdmin();
	}

	@Override
	public ServerService getServerService()
	{
		if (this.serverService == null)
		{
			Activator.getDefault().log(LogService.LOG_DEBUG, "Instantiiere Service für Server Datenbank...");
			this.serverService = new ServerServiceImpl(this);
		}
		return this.serverService;
	}

	@Override
	public void postEvent(final Event event)
	{
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null)
		{
			eventAdmin.postEvent(event);
		}
	}

	@Override
	public void sendEvent(final Event event)
	{
		EventAdmin eventAdmin = getEventAdmin();
		if (eventAdmin != null)
		{
			eventAdmin.sendEvent(event);
		}
	}
	
	public int getTimeout()
	{
		return this.timeout;
	}

//	protected void activate(final ComponentContext componentContext)
//	{
//		this.activate();
//	}

	private void activate()
	{
		Activator.getDefault().log(LogService.LOG_DEBUG, "Aktiviere Service " + this.getClass().getName() + ".");
		String[] args = Platform.getApplicationArgs();
		for (int i = 0; i < args.length; i++)
		{
			if (args[i].equals("-" + QueryHints.JDBC_TIMEOUT) && args.length > i)
			{
				try
				{
					timeout = Integer.valueOf(args[i + 1]);
				}
				catch(NumberFormatException e)
				{
					timeout = 0;
				}
				break;
			}
		}
		Activator.getDefault().log(LogService.LOG_INFO, "Starte Cache Service.");
		this.getCacheService().start();
		Activator.getDefault().log(LogService.LOG_INFO, "Cache Service gestartet.");
		Activator.getDefault().log(LogService.LOG_INFO, "Starte Server Service.");
		this.getServerService().start();
		Activator.getDefault().log(LogService.LOG_INFO, "Server Service gestartet.");
	}
	
//	protected void deactivate(final ComponentContext componentContext)
//	{
//		Activator.getDefault().log(
//				"Service " + componentContext.getProperties().get("component.name") + " deaktiviert.");
//	}

	@Override
	public PersistenceProvider getPersistenceProvider()
	{
		return Activator.getDefault().getPersistenceProvider();
	}

}
