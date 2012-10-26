package ch.eugster.colibri.persistence.connection.service;

import org.eclipse.core.runtime.Platform;
import org.eclipse.persistence.config.QueryHints;
import org.eclipse.persistence.jpa.osgi.PersistenceProvider;
import org.osgi.service.component.ComponentContext;
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
	
	private ComponentContext componentContext;

	private PersistenceProvider persistenceProvider;

	private CacheService cacheService;

	private ServerService serverService;

	private LogService logService;

	private EventAdmin eventAdmin;

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
		return this.logService;
	}

	@Override
	public ComponentContext getComponentContext()
	{
		return this.componentContext;
	}

	@Override
	public EventAdmin getEventAdmin()
	{
		return this.eventAdmin;
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
		this.eventAdmin.postEvent(event);
	}

	@Override
	public void sendEvent(final Event event)
	{
		this.eventAdmin.sendEvent(event);
	}
	
	public int getTimeout()
	{
		return this.timeout;
	}

	protected void activate(final ComponentContext componentContext)
	{
		this.componentContext = componentContext;
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
		Activator.getDefault().log("Service " + componentContext.getProperties().get("component.name") + " aktiviert.");
		this.getCacheService().start();
		this.getServerService().start();
	}

	protected void deactivate(final ComponentContext componentContext)
	{
		this.componentContext = null;
		Activator.getDefault().log(
				"Service " + componentContext.getProperties().get("component.name") + " deaktiviert.");
	}

	@Override
	public PersistenceProvider getPersistenceProvider()
	{
		return persistenceProvider;
	}

	protected void setEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = eventAdmin;
	}

	protected void unsetEventAdmin(final EventAdmin eventAdmin)
	{
		this.eventAdmin = null;
	}

	protected void setPersistenceProvider(final PersistenceProvider persistenceProvider)
	{
		this.persistenceProvider = persistenceProvider;
	}

	protected void unsetPersistenceProvider(final PersistenceProvider persistenceProvider)
	{
		this.persistenceProvider = null;
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
