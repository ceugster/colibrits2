package ch.eugster.colibri.persistence.service;

import javax.persistence.EntityManager;

import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.queries.AbstractQuery;

public interface ConnectionService
{
	public static final String KEY_USE_EMBEDDED_DATABASE = "ch.eugster.colibri.persistence.impl.use.embedded.database";

	public static final String KEY_NAME = "ch.eugster.colibri.persistence.server.connection.name";

	public static final String KEY_LOCAL_SCHEMA = "ch.eugster.colibri.persistence.local.schema";

	public static final String CONFIGURATION_DIR = "configuration";

	public static final String CONFIGURATION_XML_FILE = "database.xml";

	public static final String CONFIGURATION_DTD_FILE = "database.dtd";

	public static final String KEY_PERSISTENCE_UNIT = "ch.eugster.colibri.persistence.unit";

	public static final String PERSISTENCE_UNIT_SERVER = "ch.eugster.colibri.persistence.server";

	public static final String PERSISTENCE_UNIT_LOCAL = "ch.eugster.colibri.persistence.local";

	public static final String OJB_MIGRATION_COLIBRI_XML_FILE = "colibri.xml";
	
	public static final String OJB_MIGRATION_DIR = "Migration";
	
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
	
	public enum ConnectionType
	{
		LOCAL, SERVER;
	}
}
