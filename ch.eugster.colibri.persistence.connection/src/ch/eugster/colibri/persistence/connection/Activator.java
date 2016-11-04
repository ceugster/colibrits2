package ch.eugster.colibri.persistence.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.encryption.service.EncryptionService;
import ch.eugster.colibri.persistence.connection.service.PersistenceServiceImpl;
import ch.eugster.colibri.persistence.connection.wizard.DatabaseWizard;
import ch.eugster.colibri.persistence.connection.wizard.WizardDialog;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.service.PersistenceService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements EventHandler
{
	public static final String PLUGIN_ID = "ch.eugster.colibri.persistence.connection";

	public static final String KEY_USE_EMBEDDED_DATABASE = "ch.eugster.colibri.persistence.impl.use.embedded.database";

	public static final String KEY_NAME = "ch.eugster.colibri.persistence.server.connection.name";

	public static final String KEY_LOCAL_SCHEMA = "ch.eugster.colibri.persistence.local.schema";

	private static final String CONFIGURATION_DTD_FILE = "database.dtd";

	public static final String KEY_PERSISTENCE_UNIT = "ch.eugster.colibri.persistence.unit";

	public static final String PERSISTENCE_UNIT_SERVER = "ch.eugster.colibri.persistence.server";

	public static final String PERSISTENCE_UNIT_LOCAL = "ch.eugster.colibri.persistence.local";

	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	
	private static Activator plugin;

	private static final String LOCAL_DATABASE_DIRECTORY = "cache";
	
	private static final String BACKUP_DIRECTORY = "backup";
	
	private static final String CONFIGURATION_DIRECTORY = "configuration";

	private static final String CONFIGURATION_XML_FILE = "database.xml";

	private static final String OJB_MIGRATION_COLIBRI_XML_FILE = "colibri.xml";
	
	private static final String OJB_MIGRATION_DIRECTORY = "Migration";
	
	private ServiceTracker<EncryptionService, EncryptionService> encryptionServiceTracker;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker;

	private LogService logService;

	private PersistenceProvider persistenceProvider;
	
	private PersistenceService persistenceService;
	
	private EntityManager cacheEntityManager;

	private Document document;

	private Document oldDocument;
	
	private Context ctx;
	
	private long openTransfers;

	private long openUpdates;

	private Properties properties;
	
	private ServiceRegistration<EventHandler> eventHandlerRegistration;
	
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

	public Document getMigrationDocument()
	{
		if (this.oldDocument == null)
		{
			this.oldDocument = this.loadOldDocument();
		}
		return this.oldDocument;
	}
	
	public String getPersistenceLogDir()
	{

		return this.getWorkspace().getAbsolutePath() + File.separator + "logs";
	}

	public File getFile()
	{
		File configurationFile = null;
		try
		{
			File configurationDirectory = getConfigurationDirectory();
			if (!configurationDirectory.exists())
			{
				configurationDirectory.mkdirs();
			}
			if (configurationDirectory.exists())
			{
				File configurationFileDTD = new File(configurationDirectory.getAbsolutePath() + File.separator
						+ Activator.CONFIGURATION_DTD_FILE);
				if (!configurationFileDTD.exists())
				{
					createDTDFile(configurationFileDTD);
				}
				configurationFile = new File(configurationDirectory.getAbsolutePath() + File.separator
						+ Activator.CONFIGURATION_XML_FILE);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return configurationFile;
	}

	public File getOldFile()
	{
		File cfgFile = null;
		try
		{
			final File cfgFolder = new File(System.getProperty("user.home") + File.separator
					+ Activator.OJB_MIGRATION_DIRECTORY);
			if (cfgFolder.exists())
			{
				cfgFile = new File(cfgFolder.getAbsolutePath()+ File.separator
						+ Activator.OJB_MIGRATION_COLIBRI_XML_FILE);
			}
		}
		catch (final Exception e)
		{
			e.printStackTrace();
		}
		return cfgFile;
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

	public int getLogLevel(int statusSeverity)
	{
		switch(statusSeverity)
		{
		case IStatus.CANCEL:
		{
			return LogService.LOG_WARNING;
		}
		case IStatus.ERROR:
		{
			return LogService.LOG_ERROR;
		}
		case IStatus.INFO:
		{
			return LogService.LOG_INFO;
		}
		case IStatus.OK:
		{
			return LogService.LOG_INFO;
		}
		case IStatus.WARNING:
		{
			return LogService.LOG_WARNING;
		}
		default:
		{
			return LogService.LOG_INFO;
		}
		}
	}

	private void createDTDFile(File dtdFile)
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
	public void log(int level, final String message)
	{
		if (logService != null)
		{
			logService.log(level, message);
		}
	}

	public void saveDocument(final Document document)
	{
		this.document = document;
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

	@Override
	public void handleEvent(Event event) 
	{
		if (event.getTopic().equals(Topic.SCHEDULED_TRANSFER.topic()))
		{
			if (event.getProperty("count") != null)
			{
				this.openTransfers = ((Long) event.getProperty("count"));
			}
		}
		if (event.getTopic().equals(Topic.SCHEDULED_TRANSFER.topic()))
		{
			if (event.getProperty("count") != null)
			{
				this.openUpdates = ((Long) event.getProperty("count"));
			}
		}
	}

	public String getLogLevel()
	{
		return this.document.getRootElement().getChild("current").getAttributeValue("log-level");
	}

	public void printProperties(final Map<String, Object> properties)
	{
		final String persistenceUnit = (String) properties.get(Activator.KEY_PERSISTENCE_UNIT);
		final String driver = properties.get(PersistenceUnitProperties.JDBC_DRIVER).toString();
		final String url = properties.get(PersistenceUnitProperties.JDBC_URL).toString();
		final String username = properties.get(PersistenceUnitProperties.JDBC_USER).toString();
		final String password = properties.get(PersistenceUnitProperties.JDBC_PASSWORD).toString();
		Activator.getDefault().log(LogService.LOG_INFO, 
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
		Hashtable<String, Object> environment = new Hashtable<String, Object>();
		environment.put(Context.INITIAL_CONTEXT_FACTORY, "ch.eugster.colibri.persistence.connection.ColibriContextFactory");
		ctx = new InitialContext(environment);

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();
		this.logService = logServiceTracker.getService();
		
		this.encryptionServiceTracker = new ServiceTracker<EncryptionService, EncryptionService>(context, EncryptionService.class, null);
		this.encryptionServiceTracker.open();

		File file = getStatusFile();
		properties = new Properties();
		if (file.exists())
		{
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
			InputStream is = new FileInputStream(file);
			try
			{
				properties.load(is);
			}
			finally
			{
				is.close();
			}
			String lastBackup = properties.getProperty("last.backup");
			if (lastBackup == null)
			{
				lastBackup = sdf.format(GregorianCalendar.getInstance().getTime());
			}
			Calendar calendar = GregorianCalendar.getInstance();
			calendar.setTime(sdf.parse(lastBackup));
			int days = 8;
			long count = 0;
			String dayString = properties.getProperty("next.backup.after.days");
			try
			{
				days = Integer.parseInt(dayString == null ? "8" : dayString);
			}
			catch (NumberFormatException e)
			{
				days = 8;
			}
			properties.setProperty("next.backup.after.days", Integer.toString(days));
			
			calendar.add(Calendar.DATE, days);
			if (calendar.after(GregorianCalendar.getInstance()))
			{
				String countString = properties.getProperty("count");
				try
				{
					count = Long.parseLong(countString == null ? "0" : countString);
				}
				catch (NumberFormatException e)
				{
					count = 0L;
				}
				if (count == 0L)
				{
					this.backupDerbyHome();
				}
			}
		}

		this.setDerbyHome();
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		final Collection<String> topicNames = new ArrayList<String>();
		topicNames.add(Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		topicNames.add(Topic.SCHEDULED_TRANSFER.topic());
		final String[] topics = topicNames.toArray(new String[topicNames.size()]);
		properties.put(EventConstants.EVENT_TOPIC, topics);
		eventHandlerRegistration = context.registerService(EventHandler.class, this, properties);
		
		this.eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(context, EventAdmin.class, null);
		this.eventAdminTracker.open();
		
		file = this.getFile();
		if (!file.exists())
		{
			this.initializeConfiguration(file);
		}
		else
		{
			startPersistenceService();
		}

		this.log(LogService.LOG_DEBUG, "Bundle " + Activator.getDefault().getBundle().getSymbolicName() + " gestartet.");
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
		this.log(LogService.LOG_DEBUG, "Bundle " + Activator.getDefault().getBundle().getSymbolicName() + " gestoppt.");

		this.eventAdminTracker.close();
		this.encryptionServiceTracker.close();
		this.logServiceTracker.close();
		this.stopPersistenceService();
		this.eventHandlerRegistration.unregister();
		this.saveDocument(document);
		this.saveState();
		ctx.close();

		Activator.plugin = null;
		super.stop(context);
	}
	
	public void startPersistenceService()
	{
		log(LogService.LOG_INFO, "Starte Persistenz Service...");
		persistenceService = new PersistenceServiceImpl();
		log(LogService.LOG_INFO, "Registriere Persistenz Service...");
		getBundle().getBundleContext().registerService(PersistenceService.class, persistenceService, new Hashtable<String, Object>());
		log(LogService.LOG_INFO, "Persistenz Service registriert.");
	}
	
	public void stopPersistenceService()
	{
		persistenceService.close();
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
		EncryptionService service = this.encryptionServiceTracker.getService();
		return service;
	}

	public PersistenceProvider getPersistenceProvider()
	{
		if (persistenceProvider == null)
		{
			persistenceProvider = new org.eclipse.persistence.jpa.PersistenceProvider();
		}
		return persistenceProvider;
	}

	public EventAdmin getEventAdmin()
	{
		EventAdmin admin = this.eventAdminTracker.getService();
		return admin;
	}

	public LogService getLogService()
	{
		return logService;
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

	private Document loadOldDocument()
	{
		final File cfgFile = this.getOldFile();
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
	
	private void saveState() throws IOException
	{
		properties.setProperty("count", NumberFormat.getIntegerInstance().format(openTransfers + openUpdates));
		OutputStream os = new FileOutputStream(getStatusFile());
		try
		{
			properties.store(os, "Der Inhalt diese Datei darf nicht gerändert werden!");
		}
		finally
		{
			os.close();
		}
	}

	private void setDerbyHome()
	{
		final File cacheFolder = this.getLocalDatabaseDirectory();
		if (!cacheFolder.exists())
		{
			cacheFolder.mkdir();
		}
		if (cacheFolder.exists())
		{
			final Properties properties = System.getProperties();
			properties.setProperty("derby.system.home", cacheFolder.getAbsolutePath());
			properties.setProperty("derby.locks.deadlockTrace", "true");
			this.log(LogService.LOG_INFO, "Derby home gesetzt: " + System.getProperty("derby.system.home"));
		}
	}
	
	private void backupDerbyHome() throws IOException
	{
		Calendar calendar = GregorianCalendar.getInstance();
		String backupPath = getBackupDirectory().getAbsolutePath();
		File localDatabaseDirectory = getLocalDatabaseDirectory();
		File targetBackup = new File(backupPath + File.separator + localDatabaseDirectory.getName() + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(calendar.getTime()));
		if (localDatabaseDirectory.exists())
		{
			FileUtils.copyDirectory(localDatabaseDirectory, targetBackup);
			FileUtils.deleteDirectory(localDatabaseDirectory);
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_PATTERN);
			properties.setProperty("last.backup", sdf.format(calendar.getTime()));
			properties.setProperty("backup.directory", targetBackup.getAbsolutePath());
			this.saveState();
		}
	}
	
	private File getWorkspace()
	{
		final IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot().getRawLocation().toFile();
	}

	private File getConfigurationDirectory()
	{
		return new File(getWorkspace().getAbsolutePath() + File.separator + Activator.CONFIGURATION_DIRECTORY);
	}
	
	private File getLocalDatabaseDirectory()
	{
		return new File(getWorkspace().getAbsolutePath() + File.separator + Activator.LOCAL_DATABASE_DIRECTORY);
	}

	private File getBackupDirectory()
	{
		return new File(getWorkspace().getAbsolutePath() + File.separator + Activator.BACKUP_DIRECTORY);
	}
	
	private File getStatusFile()
	{
		return new File(this.getConfigurationDirectory().getAbsolutePath() + File.separator + "status.cfg");
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
