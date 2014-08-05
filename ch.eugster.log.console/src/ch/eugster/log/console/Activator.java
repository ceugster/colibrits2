package ch.eugster.log.console;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;

public class Activator extends AbstractUIPlugin
{
	public static final String KEY_MAX_LOG_LEVEL = "max.log.level.console";

	private static Activator activator;

	private Properties properties;
	
	public static Activator getDefault()
	{
		return activator;
	}

	@Override
	public void start(BundleContext context) throws Exception
	{
		super.start(context);
		Activator.activator = this;
		this.properties = loadProperties();

	}

	@Override
	public void stop(BundleContext context) throws Exception
	{
		Activator.activator = null;
		super.stop(context);
	}

	public Properties getProperties()
	{
		return this.properties;
	}
	
	public int getCurrentLogLevel()
	{
		return this.getLevelAsInt(this.properties.getProperty(KEY_MAX_LOG_LEVEL));
	}
	
	public File getPropertyFile()
	{
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		File root = workspace.getRoot().getRawLocation().toFile();
		return new File(root.getAbsolutePath() + File.separator + "configuration" + File.separator + "logging.ini");
	}
	
	public void storeProperties()
	{
		try 
		{
			properties.store(new FileOutputStream(getPropertyFile()), "Properties for file logger");
		} 
		catch (FileNotFoundException e) 
		{
		} 
		catch (IOException e) 
		{
		}
	}
	
	private Properties loadProperties()
	{
		properties = new Properties();
		properties.setProperty(KEY_MAX_LOG_LEVEL, this.getLevelAsString(LogService.LOG_DEBUG));
		File propertyFile = getPropertyFile();
		if (propertyFile.isFile())
		{
			try 
			{
				properties.load(new InputStreamReader(new FileInputStream(propertyFile)));
			} 
			catch (FileNotFoundException e) 
			{
			} 
			catch (IOException e) 
			{
			}
		}
		return properties;
	}

	public String getLevelAsString(int level)
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

	public int getLevelAsInt(String level)
	{
		if (level.equals("DEBUG"))
		{
			return LogService.LOG_DEBUG;
		}
		if (level.equals("INFO"))
		{
			return LogService.LOG_INFO;
		}
		if (level.equals("WARNING"))
		{
			return LogService.LOG_WARNING;
		}
		if (level.equals("ERROR"))
		{
			return LogService.LOG_ERROR;
		}
		return LogService.LOG_WARNING;
	}
}
