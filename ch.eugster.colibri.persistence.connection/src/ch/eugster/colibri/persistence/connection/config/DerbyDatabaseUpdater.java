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
	
	protected String getAddColumnStatement(String tableName, String columnName, String type, String defaultValue, boolean nullAllowed)
	{
		StringBuilder sql = new StringBuilder("ALTER TABLE " + tableName.toUpperCase())
							.append(" ADD COLUMN " + columnName.toUpperCase())
							.append(type == null ? " VARCHAR(255)" : " " + type)
							.append(defaultValue == null ? "" : " DEFAULT " + defaultValue)
							.append(nullAllowed ? "" : " NOT NULL");
		return sql.toString();
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
			return "SECOND(" + dateField + ")";
		}
		case MINUTE:
		{
			return "MINUTE(" + dateField + ")";
		}
		case HOUR:
		{
			return "HOUR(" + dateField + ")";
		}
		case DAY:
		{
			return "DAY(" + dateField + ")";
		}
		case MONTH:
		{
			return "MONTH(" + dateField + ")";
		}
		case YEAR:
		{
			return "YEAR(" + dateField + ")";
		}
		case WEEKDAY:
		{
			return "0";
		}
		default:
		{
			throw new IllegalArgumentException("Invalid Datepart: " + datePart.toString());
		}
		}
	}
	
}
