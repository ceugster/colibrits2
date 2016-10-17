package ch.eugster.colibri.persistence.service;

import javax.persistence.EntityManager;

import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.queries.AbstractQuery;

public interface ConnectionService
{
	AbstractEntity delete(AbstractEntity entity) throws Exception;

	AbstractEntity find(Class<? extends AbstractEntity> clazz, Long id);

	EntityManager getEntityManager();

	void clearCache();
	
	AbstractQuery<? extends AbstractEntity> getQuery(final Class<? extends AbstractEntity> adaptable);

	AbstractEntity merge(AbstractEntity entity) throws Exception;

	AbstractEntity merge(AbstractEntity entity, boolean updateTimestamp) throws Exception;

	AbstractEntity merge(AbstractEntity entity, boolean updateTimestamp, boolean updateReplicatable) throws Exception;

	void remove(AbstractEntity entity) throws Exception;
	
	void persist(AbstractEntity entity) throws Exception;

	void persist(AbstractEntity entity, boolean updateTimestamp) throws Exception;

	AbstractQuery<? extends AbstractEntity> getQuery(ConnectionService connectionService,
			Class<? extends AbstractEntity> clazz);

	AbstractEntity refresh(AbstractEntity entity);
	
	boolean isConnected();
	
	void start();
	
	void stop();
	
	void resetEntityManager(Exception exception);

	boolean connect();
	
	int getTimeout();
	
	ConnectionType getConnectionType();
	
	void close();
	
	public enum ConnectionType
	{
		LOCAL, SERVER;
	}
}
