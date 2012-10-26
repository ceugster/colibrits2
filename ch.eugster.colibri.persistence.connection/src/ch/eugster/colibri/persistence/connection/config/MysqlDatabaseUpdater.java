package ch.eugster.colibri.persistence.connection.config;

import java.util.Properties;

public class MysqlDatabaseUpdater extends DatabaseUpdater
{
	public MysqlDatabaseUpdater(Properties properties)
	{
		super(properties);
	}

	protected String getClobTypeName()
	{
		return "LONGTEXT";
	}

	@Override
	protected String getDoubleTypeName()
	{
		return "DECIMAL(19,6)";
	}
}
