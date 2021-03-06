package ch.eugster.colibri.persistence.connection.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.jdom.Element;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.config.DatabaseUpdater;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Entity;
import ch.eugster.colibri.persistence.queries.AbstractQuery;
import ch.eugster.colibri.persistence.service.CacheService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CacheServiceImpl extends AbstractConnectionService implements CacheService
{
//	final String cache = "COLIBRI";

	public CacheServiceImpl(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public AbstractQuery<? extends AbstractEntity> getQuery(final Class<? extends AbstractEntity> adaptable)
	{
		return this.getQuery(this, adaptable);
	}

	@Override
	public Properties getProperties()
	{
		log(LogService.LOG_INFO, "Reading properties for " + Activator.PERSISTENCE_UNIT_LOCAL);
		final Element connection = Activator.getDefault().getCurrentConnectionElement();
		/*
		 * Use ONLY the embedded (local) database, no server database!
		 */
		final String embedded = connection.getAttributeValue(Activator.KEY_USE_EMBEDDED_DATABASE);

		final Properties properties = new Properties();
		properties.setProperty("derby.system.home", Activator.getDefault().getDerbyHome().getAbsolutePath());
		properties.setProperty(Activator.KEY_NAME, connection.getText());
		properties.setProperty(Activator.KEY_USE_EMBEDDED_DATABASE, embedded);
		properties.setProperty(Activator.KEY_PERSISTENCE_UNIT, Activator.PERSISTENCE_UNIT_LOCAL);
		properties.setProperty(PersistenceUnitProperties.JDBC_DRIVER, EmbeddedDriver.class.getName());
		properties.setProperty(PersistenceUnitProperties.JDBC_URL, "jdbc:derby:" + connection.getText());
		properties.setProperty(PersistenceUnitProperties.JDBC_USER, connection.getAttributeValue(PersistenceUnitProperties.JDBC_USER));
		properties.setProperty(PersistenceUnitProperties.JDBC_PASSWORD, "");
		properties.setProperty(PersistenceUnitProperties.TARGET_DATABASE, "Derby");
		properties.setProperty(PersistenceUnitProperties.LOGGING_LEVEL, Activator.getDefault().getLogLevel());
		
		File file = Activator.getDefault().getDerbyHome().getAbsoluteFile();
		FilenameFilter filter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				if (dir.getAbsolutePath().equals(Activator.getDefault().getDerbyHome().getAbsolutePath()))
				{
					return name.toUpperCase().equals(properties.getProperty(Activator.KEY_NAME).toUpperCase());
				}
				return false;
			}
		};
		if (!file.exists() || file.list(filter).length == 0)
		{
			String url = properties.getProperty(PersistenceUnitProperties.JDBC_URL);
			properties.setProperty(PersistenceUnitProperties.JDBC_URL, url.concat(";create=true"));
			properties.setProperty(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
			properties.setProperty(PersistenceUnitProperties.DDL_GENERATION_MODE,
					PersistenceUnitProperties.DDL_DATABASE_GENERATION);
		}
		return properties;
	}

	private Map<String, Object> getEntityManagerProperties(final Properties properties)
	{
		log(LogService.LOG_INFO, "Collecting properties for " + Activator.PERSISTENCE_UNIT_LOCAL);
		final Map<String, Object> map = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		final Enumeration<String> keys = (Enumeration<String>) properties.propertyNames();
		while (keys.hasMoreElements())
		{
			final String key = keys.nextElement();
			Object value = properties.getProperty(key);
			if (key.equals(Activator.KEY_USE_EMBEDDED_DATABASE))
			{
				value = value == null ? true : Boolean.valueOf((String) value);
			}
			else if (key.equals(PersistenceUnitProperties.JDBC_PASSWORD))
			{
				value = value == null ? "" : value.toString().isEmpty() ? "" : Activator.getDefault().decrypt(
						value.toString());
			}
			map.put(key, value);
		}

		map.put(PersistenceUnitProperties.CLASSLOADER, this.getClass().getClassLoader());
//		map.put(PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.ALL_LABEL);

		final StringBuilder url = new StringBuilder(properties.getProperty(PersistenceUnitProperties.JDBC_URL));
		final File[] files = Activator.getDefault().getDerbyHome().listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(final File dir, final String name)
			{
				if (dir.getAbsolutePath().equals(Activator.getDefault().getDerbyHome().getAbsolutePath()))
				{
					return name.toUpperCase().equals(properties.getProperty(Activator.KEY_NAME).toUpperCase());
				}
				return false;
			}
		});
		if (files.length == 0)
		{
			url.append(";create=true");
			map.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
			map.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
		}
		map.put(PersistenceUnitProperties.JDBC_URL, url.toString());

		return map;
	}

	@Override
	protected IStatus updateDatabase(final Properties properties)
	{
		final DatabaseUpdater databaseUpdater = DatabaseUpdater.newInstance(properties);
		return databaseUpdater.updateDatabase(false);
	}

	@Override
	protected String getTopic()
	{
		return Topic.LOCAL_DATABASE.topic();
	}

	@Override
	protected EntityManagerFactory createEntityManagerFactory(IStatus status, Properties properties)
	{
		Map<String, Object> map = getEntityManagerProperties(properties);
		EntityManagerFactory factory = this.getPersistenceService().getPersistenceProvider()
				.createEntityManagerFactory(Activator.PERSISTENCE_UNIT_LOCAL, map);
		log(LogService.LOG_INFO, "EntityManagerFactory for " + Activator.PERSISTENCE_UNIT_LOCAL + " created.");
		return factory;
	}

	@Override
	protected void updateReplicationValue(Entity entity) 
	{
		//Do nothing (only ServerService should update the replication value
	}

	@Override
	public ConnectionType getConnectionType() 
	{
		return ConnectionType.LOCAL;
	}
	
	public void close()
	{
		if (this.getEntityManager() != null)
		{
			this.getEntityManager().clear();
			this.getEntityManager().close();
		}
		if (this.getEntityManagerFactory() != null)
		{
			this.getEntityManagerFactory().close();
		}
	}

}
