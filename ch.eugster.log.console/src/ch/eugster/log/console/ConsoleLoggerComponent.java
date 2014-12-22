package ch.eugster.log.console;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;

public class ConsoleLoggerComponent implements LogListener
{
	private LogReaderService logReaderService;
	
	private int logLevel;
	
	protected void setLogReaderService(LogReaderService logReaderService)
	{
		this.logReaderService = logReaderService;
	}

	protected void unsetLogReaderService(LogReaderService logReaderService)
	{
		this.logReaderService = null;
	}

	protected void activate(ComponentContext context)
	{
		logReaderService.addLogListener(this);
	}

	protected void deactivate(ComponentContext context)
	{
		this.logReaderService.removeLogListener(this);
	}

	@Override
	public void logged(LogEntry entry)
	{
		if (Activator.getDefault() != null && entry.getBundle() != null)
		{
			String level = Activator.getDefault().getLevelAsString(entry.getLevel());
			String symbolicName = entry.getBundle().getSymbolicName();
			String message = entry.getMessage();
			
			if (this.logLevel == 0)
			{
				this.logLevel = Activator.getDefault().getCurrentLogLevel();
			}

			if (entry.getLevel() <= this.logLevel)
			{
				String log = SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance(Locale.getDefault()).getTime()) + " " + String.format("[%s] <%s> %s", level, symbolicName, message);
				System.out.println(log);
				Throwable exception = entry.getException();
				if (exception != null)
				{
					exception.printStackTrace();
				}
			}
		}
	}
}
