package ch.eugster.colibri.persistence.service;

import javax.persistence.EntityManagerFactory;

public interface ServerService extends ConnectionService
{
	public static final String EVENT_TOPIC = "ch/eugster/colibri/persistence/server/database";

	EntityManagerFactory getEntityManagerFactory();
	
	boolean isLocal();
}
