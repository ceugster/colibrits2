package ch.eugster.colibri.persistence.connection.service;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.config.DatabaseUpdater;
import ch.eugster.colibri.persistence.connection.dialog.LoginDialog;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Entity;
import ch.eugster.colibri.persistence.model.IReplicatable;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.queries.AbstractQuery;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.queries.VersionQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.ServerService;

public class ServerServiceImpl extends AbstractConnectionService implements ServerService
{
	private LoginDialog loginDialog;
	
	private Topic databaseCompatibilityErrorTopic = null;
	
	public ServerServiceImpl(final PersistenceService persistenceService)
	{
		super(persistenceService);
		ServerExceptionHandler.setServerService(this);
	}

	@Override
	public AbstractQuery<? extends AbstractEntity> getQuery(final Class<? extends AbstractEntity> adaptable)
	{
		return this.getQuery(this, adaptable);
	}

	protected void login()
	{
		if (User.getLoginUser() == null && loginDialog == null)
		{
			String application = System.getProperty("eclipse.application");
			if (!application.contains("client"))
			{
				try
				{
					Display display = Display.getCurrent();
					if (display == null)
					{
						display = new Display();
					}
					Shell shell = new Shell(display);
					final UserQuery userQuery = (UserQuery) getQuery(User.class);
					loginDialog = new LoginDialog(shell, userQuery);
					loginDialog.open();
				}
				finally
				{
					loginDialog = null;
				}
			}
		}
	}
	
	@Override
	public boolean isLocal()
	{
		return this.getEntityManagerFactory() == this.getPersistenceService().getCacheService()
				.getEntityManagerFactory();
	}

	@Override
	protected IStatus updateDatabase(final Properties properties)
	{
		final Element connection = Activator.getDefault().getCurrentConnectionElement();
		if (Boolean.valueOf(connection
				.getAttributeValue(ConnectionService.KEY_USE_EMBEDDED_DATABASE)).booleanValue())
		{
			return Status.OK_STATUS;
		}
		final DatabaseUpdater databaseUpdater = DatabaseUpdater.newInstance(properties);
		return databaseUpdater.updateDatabase();
	}
	
	protected Properties getProperties()
	{
		final Element connection = Activator.getDefault().getCurrentConnectionElement();
		final Boolean embedded = Boolean.valueOf(connection
				.getAttributeValue(ConnectionService.KEY_USE_EMBEDDED_DATABASE));
		final String driverName = connection.getAttributeValue(PersistenceUnitProperties.JDBC_DRIVER);
		final String url = connection.getAttributeValue(PersistenceUnitProperties.JDBC_URL);
		final String username = connection.getAttributeValue(PersistenceUnitProperties.JDBC_USER);
		final String password = connection.getAttributeValue(PersistenceUnitProperties.JDBC_PASSWORD);
		final String target = connection.getAttributeValue(PersistenceUnitProperties.TARGET_DATABASE);

		final Properties properties = new Properties();
		properties.setProperty("derby.system.home", Activator.getDefault().getDerbyHome().getAbsolutePath());
		properties.setProperty(ConnectionService.KEY_NAME, connection.getText());
		properties.setProperty(ConnectionService.KEY_USE_EMBEDDED_DATABASE, embedded.toString());
		properties.setProperty(ConnectionService.KEY_PERSISTENCE_UNIT, ConnectionService.PERSISTENCE_UNIT_SERVER);
		properties.setProperty(PersistenceUnitProperties.JDBC_DRIVER,
				embedded.booleanValue() ? EmbeddedDriver.class.getName() : driverName);
		properties.setProperty(PersistenceUnitProperties.JDBC_URL,
				embedded.booleanValue() ? "jdbc:derby:" + connection.getText() : url);
		properties.setProperty(PersistenceUnitProperties.JDBC_USER, embedded.booleanValue() ? connection.getText() : username);
		properties.setProperty(PersistenceUnitProperties.JDBC_PASSWORD, embedded.booleanValue() ? connection.getText() : password);
		properties.setProperty(PersistenceUnitProperties.TARGET_DATABASE, target);
		properties.setProperty(PersistenceUnitProperties.CONNECTION_POOL_WAIT, "3000");
		properties.setProperty(PersistenceUnitProperties.QUERY_TIMEOUT, "0");
		properties.setProperty(PersistenceUnitProperties.LOGGING_FILE, Activator.getDefault().getPersistenceLogDir()+ File.separator + "persistence.log");
		properties.setProperty(PersistenceUnitProperties.LOGGING_TIMESTAMP, "true");
		properties.setProperty(PersistenceUnitProperties.LOGGING_LOGGER, "DefaultLogger");
		properties.setProperty(PersistenceUnitProperties.LOGGING_LEVEL, Activator.getDefault().getLogLevel());
		return properties;
	}
	
	private Map<String, Object> getEntityManagerProperties(IStatus status, final Properties properties)
	{
		final Map<String, Object> map = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		final Enumeration<String> keys = (Enumeration<String>) properties.propertyNames();
		while (keys.hasMoreElements())
		{
			final String key = keys.nextElement();
			Object value = properties.getProperty(key);
			if (key.equals(ConnectionService.KEY_USE_EMBEDDED_DATABASE))
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

//		map.put(PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS, ServerExceptionHandler.class.getName());
//		map.put(PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.SEVERE_LABEL);
		map.put(PersistenceUnitProperties.CLASSLOADER, this.getClass().getClassLoader());
		map.put(PersistenceUnitProperties.EXCEPTION_HANDLER_CLASS, "ch.eugster.colibri.persistence.connection.ServerExceptionHandler");

		if (status.getSeverity() == IStatus.OK)
		{
			properties.setProperty(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
			properties.setProperty(PersistenceUnitProperties.DDL_GENERATION_MODE,
					PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION);
		}
		else
		{
			map.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
			map.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_BOTH_GENERATION);
		}

		return map;
	}
	
	@Override
	protected String getTopic()
	{
		return Topic.SCHEDULED_TRANSFER.topic();
	}

	@Override
	protected EntityManagerFactory createEntityManagerFactory(IStatus status, Properties properties)
	{
		EntityManagerFactory factory = null;
		Boolean embedded = Boolean.valueOf(properties.getProperty(ConnectionService.KEY_USE_EMBEDDED_DATABASE));
		if (embedded.booleanValue())
		{
			factory = this.getPersistenceService().getCacheService().getEntityManagerFactory();
		}
		else
		{
			Map<String, Object> map = getEntityManagerProperties(status, properties);
			factory = this.getPersistenceService().getPersistenceProvider()
					.createEntityManagerFactory(ConnectionService.PERSISTENCE_UNIT_SERVER, map);
			log(LogService.LOG_INFO, "EntityManagerFactory for " + ConnectionService.PERSISTENCE_UNIT_SERVER + " created.");
		}
		return factory;
	}

	@Override
	protected void updateReplicationValue(Entity entity) throws Exception 
	{
		if (isConnected())
		{
			if (IReplicatable.class.isInstance(entity))
			{
				VersionQuery query = (VersionQuery) this.getQuery(Version.class);
				Version version = query.findDefault();
				version.setReplicationValue(version.getReplicationValue() + 1);
				query.setDefault((Version)this.merge(version));
			}
		}
	}

	@Override
	public ConnectionType getConnectionType() 
	{
		return ConnectionType.SERVER;
	}

	@Override
	public void setDatabaseCompatibilityErrorTopic(Topic topic) 
	{
		this.databaseCompatibilityErrorTopic = topic;
	}

	@Override
	public Topic getDatabaseCompatibilityErrorTopic() 
	{
		return this.databaseCompatibilityErrorTopic;
	}

}
