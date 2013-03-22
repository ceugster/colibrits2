package ch.eugster.colibri.persistence.service;

import java.io.File;

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
	
	AbstractEntity delete(AbstractEntity entity);

	AbstractEntity find(Class<? extends AbstractEntity> clazz, Long id);

	EntityManager getEntityManager();

	AbstractQuery<? extends AbstractEntity> getQuery(final Class<? extends AbstractEntity> adaptable);

	AbstractEntity merge(AbstractEntity entity);

	AbstractEntity merge(AbstractEntity entity, boolean updateTimestamp);

	void remove(AbstractEntity entity);
	
	void persist(AbstractEntity entity);

	AbstractQuery<? extends AbstractEntity> getQuery(ConnectionService connectionService,
			Class<? extends AbstractEntity> clazz);

	AbstractEntity refresh(AbstractEntity entity);
	
	boolean isConnected();
	
	void start();
	
	void stop();
	
	void resetEntityManager(Throwable throwable);

	boolean connect();
	
	int getTimeout();
}
