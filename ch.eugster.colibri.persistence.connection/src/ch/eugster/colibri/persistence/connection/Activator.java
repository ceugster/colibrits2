package ch.eugster.colibri.persistence.connection;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.spi.PersistenceProvider;

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
import org.osgi.service.event.EventAdmin;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.encryption.service.EncryptionService;
import ch.eugster.colibri.persistence.connection.service.PersistenceServiceImpl;
import ch.eugster.colibri.persistence.connection.wizard.DatabaseWizard;
import ch.eugster.colibri.persistence.connection.wizard.WizardDialog;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin
{
	private static Activator plugin;

	private ServiceTracker<EncryptionService, EncryptionService> encryptionServiceTracker;

	private ServiceTracker<LogService, LogService> logServiceTracker;

	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker;

	private LogService logService;

	private PersistenceProvider persistenceProvider;
	
	private EntityManager cacheEntityManager;

	private Document document;

	private Document oldDocument;
	
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

	public File getFile()
	{
		File cfgFile = null;
		try
		{
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final File root = workspace.getRoot().getRawLocation().toFile();

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

	public File getOldFile()
	{
		File cfgFile = null;
		try
		{
			final File cfgFolder = new File(System.getProperty("user.home") + File.separator
					+ ConnectionService.OJB_MIGRATION_DIR);
			if (cfgFolder.exists())
			{
				cfgFile = new File(cfgFolder.getAbsolutePath()+ File.separator
						+ ConnectionService.OJB_MIGRATION_COLIBRI_XML_FILE);
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
	public void log(int level, final String message)
	{
		if (logService != null)
		{
			logService.log(level, message);
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

	public String getLogLevel()
	{
		return this.document.getRootElement().getChild("current").getAttributeValue("log-level");
	}

	public void printProperties(final Map<String, Object> properties)
	{
		final String persistenceUnit = (String) properties.get(ConnectionService.KEY_PERSISTENCE_UNIT);
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

		this.logServiceTracker = new ServiceTracker<LogService, LogService>(context, LogService.class, null);
		this.logServiceTracker.open();
		this.logService = logServiceTracker.getService();
		
		this.encryptionServiceTracker = new ServiceTracker<EncryptionService, EncryptionService>(context, EncryptionService.class, null);
		this.encryptionServiceTracker.open();

//		this.persistenceProviderTracker = new ServiceTracker<PersistenceProvider, PersistenceProvider>(context, PersistenceProvider.class, null);
//		this.persistenceProviderTracker.open();
		
		this.eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(context, EventAdmin.class, null);
		this.eventAdminTracker.open();
		
		this.setDerbyHome();

		final File file = this.getFile();
		if (!file.exists())
		{
			this.initializeConfiguration(file);
		}
		else
		{
			startPersistenceService();
		}

		this.log(LogService.LOG_INFO, "Bundle " + Activator.getDefault().getBundle().getSymbolicName() + " gestartet.");
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
		this.log(LogService.LOG_INFO, "Bundle " + Activator.getDefault().getBundle().getSymbolicName() + " gestoppt.");

		this.eventAdminTracker.close();
//		this.persistenceProviderTracker.close();
		this.encryptionServiceTracker.close();
		this.logServiceTracker.close();
		
		this.saveDocument(document);

		Activator.plugin = null;
		super.stop(context);
	}
	
	public void startPersistenceService()
	{
		log(LogService.LOG_INFO, "Starte Persistenz Service...");
		PersistenceService persistenceService = new PersistenceServiceImpl();
		log(LogService.LOG_INFO, "Registriere Persistenz Service...");
		getBundle().getBundleContext().registerService(PersistenceService.class, persistenceService, new Hashtable<String, Object>());
		log(LogService.LOG_INFO, "Persistenz Service registriert.");
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
			this.log(LogService.LOG_INFO, "Derby home gesetzt: " + System.getProperty("derby.system.home"));
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
