package ch.eugster.colibri.persistence.connection.config;

import java.util.Properties;

import ch.eugster.colibri.persistence.connection.config.DatabaseUpdater.DatePart;

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
			return "SECOND(TIME(" + dateField + "))";
		}
		case MINUTE:
		{
			return "MINUTE(TIME(" + dateField + "))";
		}
		case HOUR:
		{
			return "HOUR(TIME(" + dateField + "))";
		}
		case DAY:
		{
			return "DAY(DATE(" + dateField + "))";
		}
		case MONTH:
		{
			return "MONTH(DATE(" + dateField + "))";
		}
		case YEAR:
		{
			return "YEAR(DATE(" + dateField + "))";
		}
		default:
		{
			throw new IllegalArgumentException("Invalid Datepart: " + datePart.toString());
		}
		}
	}
}
