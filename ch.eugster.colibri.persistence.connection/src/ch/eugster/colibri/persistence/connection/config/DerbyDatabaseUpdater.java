package ch.eugster.colibri.persistence.connection.config;

import java.util.Properties;

public class DerbyDatabaseUpdater extends DatabaseUpdater
{
	
	public DerbyDatabaseUpdater(Properties properties)
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
