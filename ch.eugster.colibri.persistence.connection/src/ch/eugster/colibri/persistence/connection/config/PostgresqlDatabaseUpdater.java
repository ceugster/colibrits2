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
			return "EXTRACT(SECOND FROM TIMESTAMP " + dateField + ")";
		}
		case MINUTE:
		{
			return "EXTRACT(MINUTE FROM TIMESTAMP " + dateField + ")";
		}
		case HOUR:
		{
			return "EXTRACT(HOUR FROM TIMESTAMP " + dateField + ")";
		}
		case DAY:
		{
			return "EXTRACT(DAY FROM TIMESTAMP " + dateField + ")";
		}
		case MONTH:
		{
			return "EXTRACT(MONTH FROM TIMESTAMP " + dateField + ")";
		}
		case YEAR:
		{
			return "EXTRACT(YEAR FROM TIMESTAMP " + dateField + ")";
		}
		default:
		{
			throw new IllegalArgumentException("Invalid Datepart: " + datePart.toString());
		}
		}
	}
}
