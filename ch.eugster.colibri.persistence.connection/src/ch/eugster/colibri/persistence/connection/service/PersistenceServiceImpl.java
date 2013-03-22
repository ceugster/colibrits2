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
			this.serverService = new ServerServiceImpl(this);
		}
		return this.serverService;
	}

	@Override
	public void postEvent(final Event event)
	{
		EventAdmin eventAdmin = Activator.getDefault().getEventAdmin();
		if (eventAdmin != null)
		{
			eventAdmin.postEvent(event);
		}
	}

	@Override
	public void sendEvent(final Event event)
	{
		EventAdmin eventAdmin = Activator.getDefault().getEventAdmin();
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
		Activator.getDefault().log("Service " + this.getClass().getName() + " aktiviert.");
		this.getCacheService().start();
		this.getServerService().start();
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
//
//	public void setEventAdmin(final EventAdmin eventAdmin)
//	{
//		this.eventAdmin = eventAdmin;
//	}
//
//	public void unsetEventAdmin(final EventAdmin eventAdmin)
//	{
//		this.eventAdmin = null;
//	}
//
//	public void setPersistenceProvider(final PersistenceProvider persistenceProvider)
//	{
//		this.persistenceProvider = persistenceProvider;
//	}
//
//	public void unsetPersistenceProvider(final PersistenceProvider persistenceProvider)
//	{
//		this.persistenceProvider = null;
//	}
//
//	public void setLogService(final LogService logService)
//	{
//		this.logService = logService;
//	}
//
//	public void unsetLogService(final LogService logService)
//	{
//		this.logService = null;
//	}
}
