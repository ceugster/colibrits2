package ch.eugster.colibri.persistence.service;

import javax.persistence.spi.PersistenceProvider;

import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.events.Topic;

public interface PersistenceService
{
	PersistenceProvider getPersistenceProvider();

	LogService getLogService();

	CacheService getCacheService();

	String encrypt(String message);

	String decrypt(String encryptedMessage);

	EventAdmin getEventAdmin();

	ServerService getServerService();

	void postEvent(Event event);

	void sendEvent(Event event);
	
	void setDatabaseCompatibilityError(Topic topic);

	Topic getDatabaseCompatibilityError();
	
	void close();
}
