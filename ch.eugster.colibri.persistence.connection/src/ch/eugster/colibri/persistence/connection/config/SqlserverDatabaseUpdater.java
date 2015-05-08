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
							.append(nullAllowed ? " NULL" : " NOT NULL")
							.append(defaultValue == null ? "" : " DEFAULT " + defaultValue);
		return sql.toString();
	}

	@Override
	protected String getDoubleTypeName()
	{
		return "DECIMAL(19,6)";
	}

	protected String getDatePart(DatePart datePart, String dateField)
	{
		return getFunction(datePart, dateField);
	}

	private String getFunction(DatePart datePart, String dateField) throws IllegalArgumentException
	{
		switch(datePart)
		{
		case SECOND:
		{
			return "DATEPART(ss, " + dateField + ")";
		}
		case MINUTE:
		{
			return "DATEPART(mi, " + dateField + ")";
		}
		case HOUR:
		{
			return "DATEPART(hh, " + dateField + ")";
		}
		case DAY:
		{
			return "DATEPART(dd, " + dateField + ")";
		}
		case MONTH:
		{
			return "DATEPART(mm, " + dateField + ")";
		}
		case YEAR:
		{
			return "DATEPART(yyyy, " + dateField + ")";
		}
		case WEEKDAY:
		{
			return "DATEPART(dw, DATEADD(day, 1, " + dateField + "))";
		}
		default:
		{
			throw new IllegalArgumentException("Invalid Datepart: " + datePart.toString());
		}
		}
	}
}
