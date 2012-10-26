package ch.eugster.colibri.persistence.connection.config;

import java.util.Properties;

public class PostgresqlDatabaseUpdater extends DatabaseUpdater
{
	public PostgresqlDatabaseUpdater(Properties properties)
	{
		super(properties);
	}

	@Override
	protected String getDoubleTypeName()
	{
		return "DECIMAL(19,6)";
	}
}
