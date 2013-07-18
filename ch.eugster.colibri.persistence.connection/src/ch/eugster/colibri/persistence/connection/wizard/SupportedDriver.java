package ch.eugster.colibri.persistence.connection.wizard;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.derby.jdbc.ClientDriver;
import org.apache.derby.jdbc.EmbeddedDriver;

import ch.eugster.colibri.persistence.connection.wizard.DatabaseWizardConnectionPage.Property;

public enum SupportedDriver
{
	POSTGRESQL_8X, MSSQLSERVER_2008, MYSQL_51, DERBY_CS, DERBY_EMBEDDED;

	private Pattern pattern;

	private SupportedDriver()
	{
	}

	public boolean checkURL(final String url)
	{
		String pattern = this.getURLPattern();
		if (pattern == null)
		{
			return true;
		}
		this.pattern = Pattern.compile(this.getURLPattern());
		final Matcher matcher = this.pattern.matcher(url);
		boolean match = matcher.matches();
		return match;
	}

	public String getBaseProtocol()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "jdbc:postgresql:";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return "jdbc:sqlserver:";
		}
		else if (this.equals(MYSQL_51))
		{
			return "jdbc:mysql:";
		}
		else if (this.equals(DERBY_CS))
		{
			return "jdbc:derby:";
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "jdbc:derby:";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public boolean hasInstance()
	{
		if (this.equals(MSSQLSERVER_2008))
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	public String getInstanceDelimiter()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return "\\";
		}
		else if (this.equals(MYSQL_51))
		{
			return "";
		}
		else if (this.equals(DERBY_CS))
		{
			return "";
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getDefaultPort()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "5432";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return "1433";
		}
		else if (this.equals(MYSQL_51))
		{
			return "3306";
		}
		else if (this.equals(DERBY_CS))
		{
			return "1527";
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getDefaultURL()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "jdbc:postgresql:colibri";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return "jdbc:sqlserver:colibri";
		}
		else if (this.equals(MYSQL_51))
		{
			return "jdbc:mysql:colibri";
		}
		else if (this.equals(DERBY_CS))
		{
			return "jdbc:derby:colibri";
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "jdbc:derby:colibri";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getDescription()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "Ersetzen Sie <host> durch den Namen oder die IP-Nummer des Hosts (z.B. 'localhost' oder 127.0.0.1), auf dem der Datenbankserver läuft.\nErsetzen Sie <port> durch die Nummer des Ports, über welchen die Datenbank erreichbar ist (optional, Defaultwert: 5432).\nErsetzen Sie <database> durch den Namen der Datenbank, die verwendet werden soll.";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return "Ersetzen Sie <host\\instance> durch den Namen oder die IP-Nummer des Hosts auf dem der Datenbankserver läuft (z.B. 'localhost' oder 127.0.0.1) und den Instanznamen des SQL-Servers, getrennt durch einen Backslash.\nErsetzen Sie <port> durch die Nummer des Ports, über welchen die Datenbank erreichbar ist (optional, wenn der Standardport verwendet wird, Defaultwert: 1433).\nErsetzen Sie [key=value]* durch Schlüssel/Wert-Paare (z.B.\n-- databaseName=<Datenbankname>\n--integratedSecurity=true\n--user=<Benutzername>\n-- password=<Passwort>\nBeachten Sie dazu die Erläuterung von Microsoft";
		}
		else if (this.equals(MYSQL_51))
		{
			return "Ersetzen Sie <host> durch den Namen oder die IP-Nummer des Hosts (z.B. 'localhost' oder 127.0.0.1), auf dem der Datenbankserver läuft.\nErsetzen Sie <port> durch die Nummer des Ports, über welchen die Datenbank erreichbar ist (optional, Defaultwert: 3306).\nErsetzen Sie <database> durch den Namen der Datenbank, die verwendet werden soll.";
		}
		else if (this.equals(DERBY_CS))
		{
			return "Ersetzen Sie <host> durch den Namen oder die IP-Nummer des Hosts, auf dem der Datenbankserver läuft.\nErsetzen Sie <port> durch die Nummer des Ports, über welchen die Datenbank erreichbar ist.\nErsetzen Sie <database> durch den Namen der Datenbank, die verwendet werden soll.\n[] = optionaler Ausdruck";
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "Ersetzen Sie <database> durch den Namen der Datenbank, die verwendet werden soll.\n[] = optionaler Ausdruck";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getDriver()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return org.postgresql.Driver.class.getName();
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return com.microsoft.sqlserver.jdbc.SQLServerDriver.class.getName();
		}
		else if (this.equals(MYSQL_51))
		{
			return com.mysql.jdbc.Driver.class.getName();
		}
		else if (this.equals(DERBY_CS))
		{
			return org.apache.derby.jdbc.ClientDriver.class.getName();
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return EmbeddedDriver.class.getName();
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getExampleURL()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "jdbc:postgresql:[//<host>[:<port>]/]<database>";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return "jdbc:sqlserver:[//<host\\instance[:<port>]]>[;key=value]*";
		}
		else if (this.equals(MYSQL_51))
		{
			return "jdbc:mysql:[//<host>[:<port>]/]<database>";
		}
		else if (this.equals(DERBY_CS))
		{
			return "jdbc:derby:[//<host>[:<port>]/]<database>[;<option>]";
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "jdbc:derby:<database>[;<option>]";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getOjbPlatform()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "PostgreSQL";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return "MsSQLServer";
		}
		else if (this.equals(MYSQL_51))
		{
			return "MySQL";
		}
		else if (this.equals(DERBY_CS))
		{
			return ("Derby");
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "Derby";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getOjbSubprotocol()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "postgresql";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			return "sqlserver";
		}
		else if (this.equals(MYSQL_51))
		{
			return "mysql";
		}
		else if (this.equals(DERBY_CS))
		{
			return ("derby");
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "derby";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getPlatform()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "PostgreSQL " + org.postgresql.Driver.MAJORVERSION + "." + org.postgresql.Driver.MINORVERSION;
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
			final com.microsoft.sqlserver.jdbc.SQLServerDriver driver = new com.microsoft.sqlserver.jdbc.SQLServerDriver();
			return "Microsoft SQL Server " + driver.getMajorVersion() + "." + driver.getMinorVersion();
		}
		else if (this.equals(MYSQL_51))
		{
			com.mysql.jdbc.Driver driver = null;
			try
			{
				driver = new com.mysql.jdbc.Driver();
			}
			catch (final SQLException e)
			{
			}
			return "MySQL " + driver.getMajorVersion() + "." + driver.getMinorVersion();
		}
		else if (this.equals(DERBY_CS))
		{
			final ClientDriver driver = new ClientDriver();
			return ("Derby Client/Server " + driver.getMajorVersion() + "." + driver.getMinorVersion());
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			final EmbeddedDriver driver = new EmbeddedDriver();
			return "Derby Embedded " + driver.getMajorVersion() + "." + driver.getMajorVersion();
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}

	public String getURLPattern()
	{
		if (this.equals(POSTGRESQL_8X))
		{
			return "jdbc:postgresql://((\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}|[A-Za-z][A-Za-z_0-9]+)(:\\d+)?/)?[A-Za-z_0-9]+(;[A-Za-z_0-9]+=[A-Za-z_0-9]+)*";
		}
		else if (this.equals(MSSQLSERVER_2008))
		{
//			return "jdbc:sqlserver://((\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}|[A-Za-z][A-Za-z_0-9]+)(:\\d+)?/)?[A-Za-z_0-9]+(;[A-Za-z_0-9]+=[A-Za-z_0-9]+)*";
			return null;
		}
		else if (this.equals(MYSQL_51))
		{
			return "jdbc:mysql://((\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}|[A-Za-z][A-Za-z_0-9]+)(:\\d+)?/)?[A-Za-z_0-9]+(;[A-Za-z_0-9]+=[A-Za-z_0-9]+)*";
		}
		else if (this.equals(DERBY_CS))
		{
			return "jdbc:derby://((\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}[.]\\d{1,3}|[A-Za-z][A-Za-z_0-9]+)(:\\d+)?/)?[A-Za-z_0-9]+(;[A-Za-z_0-9]+=[A-Za-z_0-9]+)*";
		}
		else if (this.equals(DERBY_EMBEDDED))
		{
			return "jdbc:derby:[A-Za-z_0-9]+(;[A-Za-z_0-9]+=[A-Za-z_0-9]+)*";
		}
		else
		{
			throw new RuntimeException("Invalid driver");
		}
	}
	
	@SuppressWarnings("unchecked")
	public String getUrl(String protocol, String host, String delimiter, String instance, String port, String database, Object parameters)
	{
		List<Property> properties = null;
		if (parameters instanceof List)
		{
			properties = (List<Property>) parameters;
		}
		StringBuilder builder;
		switch(this)
		{
			case MSSQLSERVER_2008:
			{
				builder = new StringBuilder()
				.append(protocol)
				.append("//")
				.append(host == null || host.isEmpty() ? "localhost" : host)
				.append(instance == null || instance.isEmpty() ? "" : delimiter + instance)
				.append(port == null || port.isEmpty() || port.equals(this.getDefaultPort()) ? "" : ":" + port)
				.append(database == null || database.isEmpty() ? "" : ";database=" + database);
				return builder.append(getProperties(properties)).toString();
			}
			default:
			{
				builder = new StringBuilder()
				.append(protocol)
				.append("//")
				.append(host == null || host.isEmpty() ? "localhost" : host)
				.append(instance == null || instance.isEmpty() ? "" : delimiter + instance)
				.append(port == null || port.isEmpty() ? "" : ":" + port)
				.append(database == null || database.isEmpty() ? "" : "/" + database);
				return builder.append(getProperties(properties)).toString();
			}
		}
	}
	
	private String getProperties(List<Property> properties)
	{
		StringBuilder builder = new StringBuilder();
		if (properties instanceof List)
		{
			for (Property property : properties)
			{
				builder = builder.append(";" + property.getKey() + "=" + property.getValue());
			}
		}
		return builder.toString();
	}

	public static SupportedDriver findDriver(final String driverName)
	{
		final SupportedDriver[] drivers = SupportedDriver.values();
		for (final SupportedDriver driver : drivers)
		{
			if (driver.getDriver().equals(driverName))
			{
				return driver;
			}
		}
		return null;
	}
}
