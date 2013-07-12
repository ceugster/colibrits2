package ch.eugster.log.file;

import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.log.LogEntry;
import org.osgi.service.log.LogListener;
import org.osgi.service.log.LogReaderService;
import org.osgi.service.log.LogService;

public class FileLoggerComponent implements LogListener
{
	private LogReaderService logReaderService;
	
	private PrintStream out;
	
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
		try
		{
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			File root = workspace.getRoot().getRawLocation().toFile();
	
			final File logFolder = new File(root.getAbsolutePath() + File.separator + "logs");
			if (!logFolder.exists())
			{
				logFolder.mkdirs();
			}
			if (logFolder.exists())
			{
				final File historyFolder = new File(logFolder.getAbsolutePath() + File.separator + "history");
				if (!historyFolder.exists())
				{
					historyFolder.mkdirs();
				}
	
				File currentLogfile = new File(logFolder.getAbsolutePath() + File.separator + "colibri.log");
				// TODO Dateigr�sse h�her setzen
				if (currentLogfile.exists() && (currentLogfile.length() > 1000000))
				{
					if (historyFolder.exists())
					{
						final Calendar calendar = Calendar.getInstance();
						StringBuilder dateBuilder = new StringBuilder(new DecimalFormat("0000").format(calendar
								.get(Calendar.YEAR)));
						dateBuilder = dateBuilder.append(new DecimalFormat("00").format(calendar.get(Calendar.MONTH)));
						dateBuilder = dateBuilder.append(new DecimalFormat("00").format(calendar.get(Calendar.DATE)));
						dateBuilder = dateBuilder.append(new DecimalFormat("00").format(calendar
								.get(Calendar.HOUR_OF_DAY)));
						dateBuilder = dateBuilder.append(new DecimalFormat("00").format(calendar.get(Calendar.MINUTE)));
						dateBuilder = dateBuilder.append(new DecimalFormat("00").format(calendar.get(Calendar.SECOND)));
						dateBuilder = dateBuilder.append(new DecimalFormat("000").format(calendar
								.get(Calendar.MILLISECOND)));
						currentLogfile.renameTo(new File(historyFolder.getAbsolutePath() + File.separator + "log_"
								+ dateBuilder.toString() + ".log"));
					}
				}
	
				currentLogfile = new File(logFolder.getAbsolutePath() + File.separator + "colibri.log");
				if (!currentLogfile.exists())
				{
					currentLogfile.createNewFile();
				}
				if (currentLogfile.exists())
				{
					out = new PrintStream(currentLogfile);
					logReaderService.addLogListener(this);
				}
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void deactivate(ComponentContext context)
	{
		try
		{
			out.close();
		}
		catch(Exception e)
		{
			
		}
		logReaderService.removeLogListener(this);
	}

	@Override
	public void logged(LogEntry entry)
	{
		String log = SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()) + " " + String.format("[%s] <%s> %s", getLevelAsString(entry.getLevel()), entry.getBundle().getSymbolicName(), entry.getMessage());
		out.println(log);
		Throwable exception = entry.getException();
		if (exception != null)
		{
			exception.printStackTrace();
		}
	}

	private String getLevelAsString(int level)
	{
		switch (level)
		{
		case LogService.LOG_DEBUG:
		{
			return "DEBUG";
		}
		case LogService.LOG_INFO:
		{
			return "INFO";
		}
		case LogService.LOG_WARNING:
		{
			return "WARNING";
		}
		case LogService.LOG_ERROR:
		{
			return "ERROR";
		}
		default:
		{
			return "UNKNOWN";
		}
		}
	}
}
