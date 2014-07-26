package ch.eugster.colibri.persistence.connection.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.connection.Activator;

public abstract class AbstractInitializer
{
	protected Properties properties;

	public AbstractInitializer(final Properties properties)
	{
		this.properties = properties;
	}

	protected Connection createConnection()
	{
		log(LogService.LOG_DEBUG, "Enter AbstractInitializer.createConnection()");
		try
		{
			final String driverName = this.properties.getProperty(PersistenceUnitProperties.JDBC_DRIVER);
			final String url = this.properties.getProperty(PersistenceUnitProperties.JDBC_URL);
			final String username = this.properties.getProperty(PersistenceUnitProperties.JDBC_USER);
			String password = this.properties.getProperty(PersistenceUnitProperties.JDBC_PASSWORD);
			password = Activator.getDefault().decrypt(password);
			Class.forName(driverName);
			log(LogService.LOG_DEBUG, "Exit AbstractInitializer.createConnection()");
			return DriverManager.getConnection(url, username, password);
		}
		catch (final Exception e)
		{
			log(LogService.LOG_DEBUG, "Exit AbstractInitializer.createConnection()");
			return null;
		}
	}

	protected void log(final IStatus status)
	{
		log(Activator.getDefault().getLogLevel(status.getSeverity()), status.getMessage());
	}

	protected static void log(int level, final String message)
	{
		if (Activator.getDefault() != null)
		{
			Activator.getDefault().log(level, message);
		}
	}

	protected void releaseConnection(final Connection connection)
	{
		Activator.getDefault().log(LogService.LOG_DEBUG, "Enter AbstractInitializer.releaseConnection()");
		if (connection != null)
		{
			try
			{
				connection.close();
			}
			catch (final SQLException e)
			{
				// do nothing
			}
		}
		log(LogService.LOG_DEBUG, "Exit AbstractInitializer.releaseConnection()");
	}

}
