package ch.eugster.colibri.derby.functions;

import java.util.Calendar;
import java.util.GregorianCalendar;

public final class Timestamp 
{
	public static String datePart(Timestamp timestamp, String datepart)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		if (datepart.equals("y"))
		{
			return String.valueOf(calendar.get(Calendar.YEAR));
		}
		if (datepart.equals("M"))
		{
			return String.valueOf(calendar.get(Calendar.MONTH));
		}
		if (datepart.equals("w"))
		{
			return String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
		}
		if (datepart.equals("W"))
		{
			return String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH));
		}
		if (datepart.equals("D"))
		{
			return String.valueOf(calendar.get(Calendar.DAY_OF_YEAR));
		}
		if (datepart.equals("d"))
		{
			return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
		}
		if (datepart.equals("F"))
		{
			return String.valueOf(calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
		}
		if (datepart.equals("E"))
		{
			return String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
		}
		if (datepart.equals("H"))
		{
			return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		}
		if (datepart.equals("m"))
		{
			return String.valueOf(calendar.get(Calendar.MINUTE));
		}
		if (datepart.equals("s"))
		{
			return String.valueOf(calendar.get(Calendar.SECOND));
		}
		if (datepart.equals("S"))
		{
			return String.valueOf(calendar.get(Calendar.MILLISECOND));
		}
		return "";
	}

	public static String year(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.YEAR));
	}


	public static String month(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.MONTH));
	}

	public static String weekOfYear(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.WEEK_OF_YEAR));
	}

	public static String weekOfMonth(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.WEEK_OF_MONTH));
	}

	public static String dayOfYear(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.DAY_OF_YEAR));
	}

	public static String dayOfMonth(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	}

	public static String dayOfWeekInMonth(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.DAY_OF_WEEK_IN_MONTH));
	}

	public static String dayOfWeek(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.DAY_OF_WEEK));
	}

	public static String hourOfDay(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
	}

	public static String minute(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.MINUTE));
	}

	public static String second(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.SECOND));
	}

	public static String millisecond(Timestamp timestamp)
	{
		Calendar calendar = GregorianCalendar.getInstance();
		return String.valueOf(calendar.get(Calendar.MILLISECOND));
	}
}
