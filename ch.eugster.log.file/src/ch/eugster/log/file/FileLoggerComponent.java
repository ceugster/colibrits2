package ch.eugster.log.file;

import java.io.File;
import java.io.FileFilter;
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

public class FileLoggerComponent implements LogListener
{
	private LogReaderService logReaderService;
	
	private PrintStream out;

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
				// TODO Dateigrösse höher setzen
				if (currentLogfile.exists() && (currentLogfile.length() > 1000000))
				{
					if (historyFolder.exists())
					{
						final Calendar calendar = Calendar.getInstance();
						StringBuilder dateBuilder = new StringBuilder(new DecimalFormat("0000").format(calendar
								.get(Calendar.YEAR)));
						dateBuilder = dateBuilder.append(new DecimalFormat("00").format(calendar.get(Calendar.MONTH + 1)));
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
				File history = new File(logFolder.getAbsolutePath() + File.separator + "history");
				if (history.isDirectory())
				{
					final int days = Activator.getDefault().getDeleteLogsAfterDays();
					if (days != 0)
					{
						File[] oldLogFiles = history.listFiles(new FileFilter() 
						{
							@Override
							public boolean accept(File file) 
							{
								if (file.getName().startsWith("log_") && file.getName().length() >= 12)
								{
									int year = getInt(file.getName().substring(4, 8));
									int month = getInt(file.getName().substring(8, 10));
									int day = getInt(file.getName().substring(10, 12));
									Calendar todayMinusDays = GregorianCalendar.getInstance();
									todayMinusDays.add(Calendar.DATE, -days);
									Calendar fileDate = GregorianCalendar.getInstance();
									fileDate.set(Calendar.DATE, day);
									fileDate.set(Calendar.MONTH, month);
									fileDate.set(Calendar.YEAR, year);
									boolean ret = fileDate.getTimeInMillis() < todayMinusDays.getTimeInMillis();
									return ret;
								}
								return false;
							}
						});
						for (File oldLogFile : oldLogFiles)
						{
							oldLogFile.delete();
						}
					}
				}
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private int getInt(String value)
	{
		try
		{
			return Integer.valueOf(value);
		}
		catch (Exception e)
		{
			return 0;
		}
	}

	protected void deactivate(ComponentContext context)
	{
		this.logReaderService.removeLogListener(this);
		try
		{
			out.close();
			out = null;
		}
		catch(Exception e)
		{
			
		}
	}

	@Override
	public void logged(LogEntry entry)
	{
		if (out != null || Activator.getDefault() != null)
		{
			if (this.logLevel == 0)
			{
				this.logLevel = Activator.getDefault().getCurrentConsoleLogLevel();
			}

			if (entry.getLevel() <= this.logLevel)
			{
				String log = SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance().getTime()) + " " + String.format("[%s] <%s> %s", Activator.getDefault().getLevelAsString(entry.getLevel()), entry.getBundle().getSymbolicName(), entry.getMessage());
				out.println(log);
				Throwable exception = entry.getException();
				if (exception != null)
				{
					exception.printStackTrace();
				}
			}
		}
	}
}
