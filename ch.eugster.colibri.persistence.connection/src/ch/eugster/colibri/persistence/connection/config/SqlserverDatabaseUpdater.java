package ch.eugster.colibri.persistence.connection.config;

import java.util.Properties;

public class SqlserverDatabaseUpdater extends DatabaseUpdater
{
	public SqlserverDatabaseUpdater(Properties properties)
	{
		super(properties);
	}

	protected String getAddColumnStatement(String tableName, String columnName, String type, String defaultValue, boolean nullAllowed)
	{
		StringBuilder sql = new StringBuilder("ALTER TABLE " + tableName.toUpperCase())
							.append(" ADD " + columnName.toUpperCase())
							.append(type == null ? " VARCHAR(255)" : " " + type)
							.append(defaultValue == null ? "" : " DEFAULT " + defaultValue)
							.append(nullAllowed ? " NULL" : " NOT NULL");
		return sql.toString();
	}

	@Override
	protected String getDoubleTypeName()
	{
		return "DECIMAL(19,6)";
	}
}
