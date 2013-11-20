package ch.eugster.colibri.persistence.service;

import javax.persistence.EntityManagerFactory;

public interface ServerService extends ConnectionService
{
	EntityManagerFactory getEntityManagerFactory();
	
	boolean isLocal();
}
