package ch.eugster.colibri.persistence.service;

import javax.persistence.spi.PersistenceProvider;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

public interface PersistenceService
{
	PersistenceProvider getPersistenceProvider();

	LogService getLogService();

	CacheService getCacheService();

	String encrypt(String message);

	String decrypt(String encryptedMessage);

	ComponentContext getComponentContext();

	EventAdmin getEventAdmin();

	ServerService getServerService();

	void postEvent(Event event);

	void sendEvent(Event event);
	
	int getTimeout();
}
