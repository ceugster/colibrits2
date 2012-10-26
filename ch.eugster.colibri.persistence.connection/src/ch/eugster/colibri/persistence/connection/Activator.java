package ch.eugster.colibri.persistence.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;

import org.apache.derby.jdbc.EmbeddedDriver;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.logging.SessionLog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.BundleContext;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.encryption.service.EncryptionService;
import ch.eugster.colibri.persistence.connection.wizard.DatabaseWizard;
import ch.eugster.colibri.persistence.connection.wizard.WizardDialog;
import ch.eugster.colibri.persistence.service.ConnectionService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{

	public static final String PLUGIN_ID = "ch.eugster.colibri.persistence.connection"; //$NON-NLS-1$

	private static Activator plugin;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private ServiceTracker<EncryptionService, EncryptionService> encryptionServiceTracker;

	private EntityManager cacheEntityManager;

	private Document document;

	/**
	 * The constructor
	 */
	public Activator()
	{
	}

	public boolean configurationFileExists()
	{
		return this.getFile().exists();
	}

	public String decrypt(final String message)
	{
		final EncryptionService service = this.getEncryptionService();
		String decryptedMessage = message;
		if (service != null)
		{
			try
			{
				decryptedMessage = this.getEncryptionService().decrypt(message);
			}
			catch (Exception e)
			{
			}
		}
		return decryptedMessage;
	}

	public String encrypt(final String message)
	{
		final EncryptionService service = this.getEncryptionService();
		if (service != null)
		{
			return service.encrypt(message);
		}
		return message;
	}

	public EntityManager getCacheEntityManager()
	{
		return this.cacheEntityManager;
	}

	public Element getCurrentConnectionElement()
	{
		final Document document = this.getDocument();
		if (document == null)
		{
			return null;
		}

		return document.getRootElement().getChild("current").getChild("connection");
	}

	public Document getDocument()
	{
		if (this.document == null)
		{
			this.document = this.loadDocument();
		}
		return this.document;
	}

	public File getFile()
	{
		File cfgFile = null;
		try
		{
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final File root = workspace.getRoot().getRawLocation().toFile();
			System.out.println(root.getAbsolutePath());

			final File cfgFolder = new File(root.getAbsolutePath() + File.separator
					+ ConnectionService.CONFIGURATION_DIR);
			if (!cfgFolder.exists())
			{
				cfgFolder.mkdir();
			}
			if (cfgFolder.exists())
			{
				File dtdFile = new File(cfgFolder.getAbsolutePath() + File.separator
						+ ConnectionService.CONFIGURATION_DTD_FILE);
				if (!dtdFile.exists())
				{
					createDTDFile(dtdFile);
				}
				cfgFile = new File(cfgFolder.getAbsolutePath() + File.separator
						+ ConnectionService.CONFIGURATION_XML_FILE);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return cfgFile;
	}

	public void createDTDFile(File dtdFile)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dtdFile)));
			URL url = this.getBundle().getEntry("/META-INF/database.dtd");
			BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
			String line = reader.readLine();
			while (line != null)
			{
				writer.write(line + "\n");
				line = reader.readLine();
			}
			reader.close();
			writer.close();
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void log(final String message)
	{
		final LogService logService = (LogService) this.logServiceTracker.getService();
		if (logService != null)
		{
			logService.log(LogService.LOG_INFO, message);
		}
	}

	public void saveDocument(final Document document)
	{
		final Format format = Format.getPrettyFormat();
		final XMLOutputter outputter = new XMLOutputter();
		outputter.setFormat(format);
		try
		{
			outputter.output(document, new FileOutputStream(this.getFile()));
		}
		catch (final FileNotFoundException e)
		{
		}
		catch (final IOException e)
		{
		}
	}

	public Properties getCacheConnectionProperties(final Element connection)
	{
		final String embedded = connection.getAttributeValue(ConnectionService.KEY_USE_EMBEDDED_DATABASE);

		final Properties properties = new Properties();
		properties.setProperty("derby.system.home", Activator.getDefault().getDerbyHome().getAbsolutePath());
		properties.setProperty(ConnectionService.KEY_NAME, connection.getText());
		properties.setProperty(ConnectionService.KEY_USE_EMBEDDED_DATABASE, embedded);
		properties.setProperty(ConnectionService.KEY_PERSISTENCE_UNIT, ConnectionService.PERSISTENCE_UNIT_LOCAL);
		properties.setProperty(PersistenceUnitProperties.JDBC_DRIVER, EmbeddedDriver.class.getName());
		properties.setProperty(PersistenceUnitProperties.JDBC_URL, "jdbc:derby:" + connection.getText());
		properties.setProperty(PersistenceUnitProperties.JDBC_USER, connection.getText());
		properties.setProperty(PersistenceUnitProperties.JDBC_PASSWORD, connection.getText());
		properties.setProperty(PersistenceUnitProperties.TARGET_DATABASE, "Derby");
		String level = this.document.getRootElement().getChild("current").getAttributeValue("log-level");
		properties.setProperty(PersistenceUnitProperties.LOGGING_LEVEL, level);
		
		File file = Activator.getDefault().getDerbyHome().getAbsoluteFile();
		FilenameFilter filter = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				if (dir.getAbsolutePath().equals(Activator.getDefault().getDerbyHome().getAbsolutePath()))
				{
					return name.equals(connection.getText());
				}
				return false;
			}
		};
		if (!file.exists() || file.list(filter).length == 0)
		{
			String url = properties.getProperty(PersistenceUnitProperties.JDBC_URL);
			properties.setProperty(PersistenceUnitProperties.JDBC_URL, url.concat(";create=true"));
			properties.setProperty(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.DROP_AND_CREATE);
			properties.setProperty(PersistenceUnitProperties.DDL_GENERATION_MODE,
					PersistenceUnitProperties.DDL_DATABASE_GENERATION);
		}
		else
		{
			properties.setProperty(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.NONE);
			properties.setProperty(PersistenceUnitProperties.DDL_GENERATION_MODE,
					PersistenceUnitProperties.DDL_SQL_SCRIPT_GENERATION);
		}
		return properties;
	}

	public Properties getServerConnectionProperties(final Element connection)
	{
		final Boolean embedded = Boolean.valueOf(connection
				.getAttributeValue(ConnectionService.KEY_USE_EMBEDDED_DATABASE));
		final String driverName = connection.getAttributeValue(PersistenceUnitProperties.JDBC_DRIVER);
		final String url = connection.getAttributeValue(PersistenceUnitProperties.JDBC_URL);
		final String username = connection.getAttributeValue(PersistenceUnitProperties.JDBC_USER);
		final String password = connection.getAttributeValue(PersistenceUnitProperties.JDBC_PASSWORD);

		final Properties properties = new Properties();
		properties.setProperty("derby.system.home", Activator.getDefault().getDerbyHome().getAbsolutePath());
		properties.setProperty(ConnectionService.KEY_NAME, connection.getText());
		properties.setProperty(ConnectionService.KEY_USE_EMBEDDED_DATABASE, embedded.toString());
		properties.setProperty(ConnectionService.KEY_PERSISTENCE_UNIT, ConnectionService.PERSISTENCE_UNIT_SERVER);
		properties.setProperty(PersistenceUnitProperties.JDBC_DRIVER,
				embedded.booleanValue() ? EmbeddedDriver.class.getName() : driverName);
		properties.setProperty(PersistenceUnitProperties.JDBC_URL,
				embedded.booleanValue() ? "jdbc:derby:" + connection.getText() : url);
		properties.setProperty(PersistenceUnitProperties.JDBC_USER, embedded.booleanValue() ? connection.getText() : username);
		properties.setProperty(PersistenceUnitProperties.JDBC_PASSWORD, embedded.booleanValue() ? connection.getText() : password);
		String level = this.document.getRootElement().getChild("current").getAttributeValue("log-level");
		properties.setProperty(PersistenceUnitProperties.LOGGING_LEVEL, level);

		return properties;
	}

	public Map<String, Object> getServerEntityManagerProperties(final Properties properties)
	{
		final Map<String, Object> map = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		final Enumeration<String> keys = (Enumeration<String>) properties.propertyNames();
		while (keys.hasMoreElements())
		{
			final String key = keys.nextElement();
			Object value = properties.getProperty(key);
			if (key.equals(ConnectionService.KEY_USE_EMBEDDED_DATABASE))
			{
				value = value == null ? true : Boolean.valueOf((String) value);
			}
			else if (key.equals(PersistenceUnitProperties.JDBC_PASSWORD))
			{
				value = value == null ? "" : value.toString().isEmpty() ? "" : Activator.getDefault().decrypt(
						value.toString());
			}
			map.put(key, value);
		}

		map.put(PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.SEVERE_LABEL);
		map.put(PersistenceUnitProperties.CLASSLOADER, this.getClass().getClassLoader());
		map.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
		map.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_BOTH_GENERATION);

		return map;
	}

	public Map<String, Object> getCacheEntityManagerProperties(final Properties properties)
	{
		final Map<String, Object> map = new HashMap<String, Object>();

		@SuppressWarnings("unchecked")
		final Enumeration<String> keys = (Enumeration<String>) properties.propertyNames();
		while (keys.hasMoreElements())
		{
			final String key = keys.nextElement();
			Object value = properties.getProperty(key);
			if (key.equals(ConnectionService.KEY_USE_EMBEDDED_DATABASE))
			{
				value = value == null ? true : Boolean.valueOf((String) value);
			}
			else if (key.equals(PersistenceUnitProperties.JDBC_PASSWORD))
			{
				value = value == null ? "" : value.toString().isEmpty() ? "" : Activator.getDefault().decrypt(
						value.toString());
			}
			map.put(key, value);
		}

		map.put(PersistenceUnitProperties.CLASSLOADER, this.getClass().getClassLoader());
		map.put(PersistenceUnitProperties.LOGGING_LEVEL, SessionLog.ALL_LABEL);

		final StringBuilder url = new StringBuilder(properties.getProperty(PersistenceUnitProperties.JDBC_URL));
		final File[] files = Activator.getDefault().getDerbyHome().listFiles(new FilenameFilter()
		{
			@Override
			public boolean accept(final File dir, final String name)
			{
				if (dir.getAbsolutePath().equals(Activator.getDefault().getDerbyHome().getAbsolutePath()))
				{
					return name.equals(properties.getProperty(ConnectionService.KEY_NAME));
				}
				return false;
			}
		});
		if (files.length == 0)
		{
			url.append(";create=true");
			map.put(PersistenceUnitProperties.DDL_GENERATION, PersistenceUnitProperties.CREATE_ONLY);
			map.put(PersistenceUnitProperties.DDL_GENERATION_MODE, PersistenceUnitProperties.DDL_DATABASE_GENERATION);
		}
		map.put(PersistenceUnitProperties.JDBC_URL, url.toString());

		printProperties(map);

		return map;
	}

	protected void printProperties(final Map<String, Object> properties)
	{
		final String persistenceUnit = (String) properties.get(ConnectionService.KEY_PERSISTENCE_UNIT);
		final String driver = properties.get(PersistenceUnitProperties.JDBC_DRIVER).toString();
		final String url = properties.get(PersistenceUnitProperties.JDBC_URL).toString();
		final String username = properties.get(PersistenceUnitProperties.JDBC_USER).toString();
		final String password = properties.get(PersistenceUnitProperties.JDBC_PASSWORD).toString();
		Activator.getDefault().log(
				persistenceUnit + " (Treiber: " + driver + ", URL: " + url + ", Benutzername: " + username
						+ ", Passwort: " + password + ").");
	}

	public void setCacheEntityManager(final EntityManager entityManager)
	{
		this.cacheEntityManager = entityManager;
	}

	@Override
	public void start(final BundleContext context) throws Exception
	{
		super.start(context);
		Activator.plugin = this;

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();

		this.encryptionServiceTracker = new ServiceTracker<EncryptionService, EncryptionService>(context, EncryptionService.class, null);
		this.encryptionServiceTracker.open();

		this.setDerbyHome();

		final File file = this.getFile();
		if (!file.exists())
		{
			this.initializeConfiguration(file);
		}

		this.log("Plugin " + Activator.PLUGIN_ID + " gestartet.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void stop(final BundleContext context) throws Exception
	{
		this.log("Plugin " + Activator.PLUGIN_ID + " gestoppt.");

		this.encryptionServiceTracker.close();

		this.logServiceTracker.close();

		Activator.plugin = null;
		super.stop(context);
	}

	@Override
	protected void initializeImageRegistry(final ImageRegistry imageRegistry)
	{
		super.initializeImageRegistry(imageRegistry);
		imageRegistry.put("DATABASE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/db.gif")));
		imageRegistry.put("ADD", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/add_16.png")));
		imageRegistry.put("REMOVE", ImageDescriptor.createFromURL(this.getBundle().getEntry("/icons/delete_16.png")));
	}

	public File getDerbyHome()
	{
		if (System.getProperty("derby.system.home") == null)
		{
			this.setDerbyHome();
		}
		return new File(System.getProperty("derby.system.home"));
	}

	private EncryptionService getEncryptionService()
	{
		return (EncryptionService) this.encryptionServiceTracker.getService();
	}

	private void initializeConfiguration(final File file)
	{
		Display.getDefault().syncExec(new Runnable()
		{

			@Override
			public void run()
			{
				final Shell shell = new Shell(Display.getDefault());
				final DatabaseWizard wizard = new DatabaseWizard();
				final WizardDialog dialog = new WizardDialog(shell, wizard);
				final int result = dialog.open();
				if (result == 1)
				{
					System.exit(-1);
				}
			}
		});
	}

	private Document loadDocument()
	{
		final File cfgFile = this.getFile();
		if (cfgFile.exists())
		{
			try
			{
				final SAXBuilder builder = new SAXBuilder();
				return builder.build(cfgFile);
			}
			catch (final Exception e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	private void setDerbyHome()
	{
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final File root = workspace.getRoot().getRawLocation().toFile();

		final File cacheFolder = new File(root.getAbsolutePath() + File.separator + "cache");
		if (!cacheFolder.exists())
		{
			cacheFolder.mkdir();
		}
		if (cacheFolder.exists())
		{
			final Properties properties = System.getProperties();
			properties.setProperty("derby.system.home", cacheFolder.getAbsolutePath());
			properties.setProperty("derby.locks.deadlockTrace", "true");
			this.log("Derby home set: " + System.getProperty("derby.system.home"));
		}
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault()
	{
		return Activator.plugin;
	}

	public enum ResultType
	{
		CONNECT_NORMAL, UPDATE_CURRENT_VERSION, NO_CONNECTION, EXIT_PROGRAM;

		private String message;

		private IStatus status;

		public String getMessage()
		{
			return this.message;
		}

		public IStatus getStatus()
		{
			return this.status;
		}

		public void setMessage(final String message)
		{
			this.message = message;
		}

		public void setStatus(final IStatus status)
		{
			this.status = status;
		}
	}
}
