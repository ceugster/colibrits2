package ch.eugster.colibri.persistence.service;

import javax.persistence.EntityManagerFactory;

public interface CacheService extends ConnectionService
{
	public static final String EVENT_TOPIC = "ch/eugster/colibri/persistence/local/database";

	EntityManagerFactory getEntityManagerFactory();
}
