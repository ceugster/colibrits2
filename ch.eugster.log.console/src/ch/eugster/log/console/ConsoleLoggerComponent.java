package ch.eugster.log.console;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;

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
		this.logReaderService.removeLogListener(this);
		this.logReaderService = null;
	}

	protected void activate(ComponentContext context)
	{
	}

	protected void deactivate(ComponentContext context)
	{
	}

	@Override
	public void logged(LogEntry entry)
	{
		if (this.logLevel == 0)
		{
			this.logLevel = Activator.getDefault().getCurrentLogLevel();
		}

		if (entry.getLevel() <= this.logLevel)
		{
			String log = SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()) + " " + String.format("[%s] <%s> %s", Activator.getDefault().getLevelAsString(entry.getLevel()), entry.getBundle().getSymbolicName(), entry.getMessage());
			System.out.println(log);
			Throwable exception = entry.getException();
			if (exception != null)
			{
				exception.printStackTrace();
			}
		}
	}
}
