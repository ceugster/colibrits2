package ch.eugster.colibri.persistence.service;

import javax.persistence.EntityManagerFactory;

import ch.eugster.colibri.persistence.events.Topic;

public interface ServerService extends ConnectionService
{
	EntityManagerFactory getEntityManagerFactory();
	
	void setDatabaseCompatibilityErrorTopic(Topic topic);

	Topic getDatabaseCompatibilityErrorTopic();

	boolean isLocal();

}
