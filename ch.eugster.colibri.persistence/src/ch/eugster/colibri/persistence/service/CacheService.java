package ch.eugster.colibri.persistence.service;

import javax.persistence.EntityManagerFactory;

public interface CacheService extends ConnectionService
{
	EntityManagerFactory getEntityManagerFactory();
}
