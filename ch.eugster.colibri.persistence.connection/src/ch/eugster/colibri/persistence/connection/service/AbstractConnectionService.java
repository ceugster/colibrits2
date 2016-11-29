package ch.eugster.colibri.persistence.connection.service;

import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceException;
import javax.persistence.RollbackException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.exceptions.DatabaseException;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.config.TaxUpdater;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.CommonSettingsProperty;
import ch.eugster.colibri.persistence.model.Configurable;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.CurrentTax;
import ch.eugster.colibri.persistence.model.CurrentTaxCodeMapping;
import ch.eugster.colibri.persistence.model.CustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Display;
import ch.eugster.colibri.persistence.model.DisplayArea;
import ch.eugster.colibri.persistence.model.Entity;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Money;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Printout;
import ch.eugster.colibri.persistence.model.PrintoutArea;
import ch.eugster.colibri.persistence.model.Product;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProductGroupMapping;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.ReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Role;
import ch.eugster.colibri.persistence.model.RoleProperty;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.SalespointReceiptPrinterSettings;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.model.Tab;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.model.TaxCodeMapping;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.queries.AbstractQuery;
import ch.eugster.colibri.persistence.queries.CommonSettingsPropertyQuery;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ConfigurableQuery;
import ch.eugster.colibri.persistence.queries.CurrencyQuery;
import ch.eugster.colibri.persistence.queries.CurrentTaxCodeMappingQuery;
import ch.eugster.colibri.persistence.queries.CurrentTaxQuery;
import ch.eugster.colibri.persistence.queries.CustomerDisplaySettingsQuery;
import ch.eugster.colibri.persistence.queries.DisplayAreaQuery;
import ch.eugster.colibri.persistence.queries.DisplayQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.KeyQuery;
import ch.eugster.colibri.persistence.queries.MoneyQuery;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.PrintoutAreaQuery;
import ch.eugster.colibri.persistence.queries.PrintoutQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupMappingQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ProductQuery;
import ch.eugster.colibri.persistence.queries.ProfileQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.ReceiptPrinterSettingsQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.queries.RolePropertyQuery;
import ch.eugster.colibri.persistence.queries.RoleQuery;
import ch.eugster.colibri.persistence.queries.SalespointCustomerDisplayQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.queries.SalespointReceiptPrinterQuery;
import ch.eugster.colibri.persistence.queries.SettlementQuery;
import ch.eugster.colibri.persistence.queries.StockQuery;
import ch.eugster.colibri.persistence.queries.TabQuery;
import ch.eugster.colibri.persistence.queries.TaxCodeMappingQuery;
import ch.eugster.colibri.persistence.queries.TaxQuery;
import ch.eugster.colibri.persistence.queries.TaxRateQuery;
import ch.eugster.colibri.persistence.queries.TaxTypeQuery;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.queries.VersionQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractConnectionService implements ConnectionService
{
	private int timeout;
	
	private PersistenceService persistenceService;

	private EntityManagerFactory entityManagerFactory;

	private EntityManager entityManager;

	private final Map<Class<? extends AbstractEntity>, AbstractQuery<? extends AbstractEntity>> queryServices = new HashMap<Class<? extends AbstractEntity>, AbstractQuery<? extends AbstractEntity>>();

	protected abstract Properties getProperties();

	protected void log(int level, String message)
	{
		if (Activator.getDefault() != null)
		{
			Activator.getDefault().log(LogService.LOG_DEBUG, "Enter AbstractConnectionService.getEntityManager()");
		}
	}
	
	protected void setTimeout(int timeout)
	{
		this.timeout = timeout;
	}

	public int getTimeout()
	{
		return this.timeout;
	}
	
	public void clearCache()
	{
		if (this.entityManager != null)
		{
			this.entityManager.clear();
		}
	}
	
	@Override
	public AbstractQuery<? extends AbstractEntity> getQuery(final ConnectionService connectionService,
			final Class<? extends AbstractEntity> adaptable)
	{
		AbstractQuery<? extends AbstractEntity> query = this.queryServices.get(adaptable);
		if (query == null)
		{
			if (adaptable.equals(CommonSettings.class))
			{
				query = new CommonSettingsQuery();
			}
			else if (adaptable.equals(CommonSettingsProperty.class))
			{
				query = new CommonSettingsPropertyQuery();
			}
			else if (adaptable.equals(Configurable.class))
			{
				query = new ConfigurableQuery();
			}
			else if (adaptable.equals(Currency.class))
			{
				query = new CurrencyQuery();
			}
			else if (adaptable.equals(CurrentTaxCodeMapping.class))
			{
				query = new CurrentTaxCodeMappingQuery();
			}
			else if (adaptable.equals(CustomerDisplaySettings.class))
			{
				query = new CustomerDisplaySettingsQuery();
			}
			else if (adaptable.equals(TaxCodeMapping.class))
			{
				query = new TaxCodeMappingQuery();
			}
			else if (adaptable.equals(CurrentTax.class))
			{
				query = new CurrentTaxQuery();
			}
			else if (adaptable.equals(Display.class))
			{
				query = new DisplayQuery();
			}
			else if (adaptable.equals(DisplayArea.class))
			{
				query = new DisplayAreaQuery();
			}
			else if (adaptable.equals(ExternalProductGroup.class))
			{
				query = new ExternalProductGroupQuery();
			}
			else if (adaptable.equals(Key.class))
			{
				query = new KeyQuery();
			}
			else if (adaptable.equals(Money.class))
			{
				query = new MoneyQuery();
			}
			else if (adaptable.equals(Payment.class))
			{
				query = new PaymentQuery();
			}
			else if (adaptable.equals(PaymentType.class))
			{
				query = new PaymentTypeQuery();
			}
			else if (adaptable.equals(Position.class))
			{
				query = new PositionQuery();
			}
			else if (adaptable.equals(Printout.class))
			{
				query = new PrintoutQuery();
			}
			else if (adaptable.equals(PrintoutArea.class))
			{
				query = new PrintoutAreaQuery();
			}
			else if (adaptable.equals(Product.class))
			{
				query = new ProductQuery();
			}
			else if (adaptable.equals(ProductGroup.class))
			{
				query = new ProductGroupQuery();
			}
			else if (adaptable.equals(ProductGroupMapping.class))
			{
				query = new ProductGroupMappingQuery();
			}
			else if (adaptable.equals(Profile.class))
			{
				query = new ProfileQuery();
			}
			else if (adaptable.equals(ProviderProperty.class))
			{
				query = new ProviderPropertyQuery();
			}
			else if (adaptable.equals(Receipt.class))
			{
				query = new ReceiptQuery();
			}
			else if (adaptable.equals(ReceiptPrinterSettings.class))
			{
				query = new ReceiptPrinterSettingsQuery();
			}
			else if (adaptable.equals(Role.class))
			{
				query = new RoleQuery();
			}
			else if (adaptable.equals(RoleProperty.class))
			{
				query = new RolePropertyQuery();
			}
			else if (adaptable.equals(Salespoint.class))
			{
				query = new SalespointQuery();
			}
			else if (adaptable.equals(SalespointCustomerDisplaySettings.class))
			{
				query = new SalespointCustomerDisplayQuery();
			}
			else if (adaptable.equals(SalespointReceiptPrinterSettings.class))
			{
				query = new SalespointReceiptPrinterQuery();
			}
			else if (adaptable.equals(Settlement.class))
			{
				query = new SettlementQuery();
			}
			else if (adaptable.equals(Stock.class))
			{
				query = new StockQuery();
			}
			else if (adaptable.equals(Tab.class))
			{
				query = new TabQuery();
			}
			else if (adaptable.equals(Tax.class))
			{
				query = new TaxQuery();
			}
			else if (adaptable.equals(TaxRate.class))
			{
				query = new TaxRateQuery();
			}
			else if (adaptable.equals(TaxType.class))
			{
				query = new TaxTypeQuery();
			}
			else if (adaptable.equals(User.class))
			{
				query = new UserQuery();
			}
			else if (adaptable.equals(Version.class))
			{
				query = new VersionQuery();
			}
			this.queryServices.put(adaptable, query);
		}
		query.setConnectionService(connectionService);
		return query;
	}

	public boolean isConnected()
	{
		if (entityManager != null && !entityManager.isOpen())
		{
			entityManager = null;
		}
		return entityManager != null;
	}
	
	public EntityManagerFactory getEntityManagerFactory()
	{
		if (entityManagerFactory == null || !entityManagerFactory.isOpen())
		{
			final Properties properties = this.getProperties();
			log(LogService.LOG_INFO, "Aktualisiere Datenbankversion...");
			IStatus status = this.updateDatabase(properties);
			if (status.isOK() || status.getSeverity() == IStatus.CANCEL)
			{
				entityManagerFactory = createEntityManagerFactory(status, properties);
			}
			else
			{
				final Dictionary<String, Object> props = new Hashtable<String, Object>();
				props.put(EventConstants.BUNDLE, Activator.getDefault().getBundle());
				props.put(EventConstants.BUNDLE_ID, Long.valueOf(Activator.getDefault().getBundle().getBundleId()));
				props.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
				props.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
				props.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
				props.put("status", status);
				props.put("show.failover.message", Boolean.valueOf(true));
				props.put(EventConstants.EVENT_TOPIC, status.getMessage());
				props.put(EventConstants.BUNDLE_ID, Activator.getDefault().getBundle().getSymbolicName());
				props.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
				if (status.getException() != null)
				{
					props.put(EventConstants.EXCEPTION, status.getException());
					props.put(EventConstants.EXCEPTION_MESSAGE, status.getException().getMessage() == null ? "" : status.getException().getMessage());
				}
				String database = properties.getProperty(PersistenceUnitProperties.TARGET_DATABASE);
				String url = properties.getProperty(PersistenceUnitProperties.JDBC_URL);
				String message = "Die Version der Datenbank " + database + " mit der URL " + url + " ist aktueller als die Version der Anwendung. Um Inkonsistenzen in der Datenbank zu vermeiden, wird die Anwendung beendet. Bitte installieren Sie ein Programm, das kompatibel mit der aktuellen Datenbankstruktur ist. Die Datenbankstruktur hat die Version "
						+ Version.STRUCTURE + ".";
				props.put("message", message);
				props.put("force", Boolean.valueOf(false));
				final Event event = new Event(Topic.DATABASE_COMPATIBILITY_ERROR.topic(), props);
				this.persistenceService.setDatabaseCompatibilityError(Topic.DATABASE_COMPATIBILITY_ERROR);
				this.persistenceService.getEventAdmin().sendEvent(event);
			}
		}
		log(LogService.LOG_DEBUG, "Exit AbstractConnectionService.getEntityManagerFactory()");
		return entityManagerFactory;
	}
	
	public void start()
	{
		this.entityManagerFactory = this.getEntityManagerFactory();
	}
	
	public void stop()
	{
		this.entityManagerFactory.close();
	}

	@Override
	public AbstractEntity refresh(AbstractEntity entity)
	{
		if (entity.getId() == null)
		{
			return entity;
		}
		else
		{
			if (this.getEntityManager() != null)
			{
				if (this.getEntityManager().contains(entity))
				{
					this.getEntityManager().refresh(entity);
					return entity;
				}
				else
				{
					return getEntityManager().find(entity.getClass(), entity.getId());
				}
			}
			else
			{
				return entity;
			}
		}
	}

	protected abstract EntityManagerFactory createEntityManagerFactory(IStatus status, Properties properties);

	protected abstract String getTopic();

	public EntityManager createEntityManager()
	{
		if (persistenceService.getPersistenceProvider() != null)
		{
			if (getEntityManagerFactory() != null)
			{
				connect();
			}
		}
		return entityManager;
	}

	public boolean connect()
	{
		try
		{
			if (getEntityManagerFactory() == null)
			{
				System.out.println();
			}
			else
			{
				log(LogService.LOG_DEBUG, "Kreiere EntityManager für Datenbank " + getEntityManagerFactory().getProperties().get(PersistenceUnitProperties.JDBC_URL) + ".");
				this.entityManager = getEntityManagerFactory().createEntityManager(getEntityManagerFactory().getProperties());
				log(LogService.LOG_DEBUG, "EntityManager kreiert.");
				this.entityManager.setFlushMode(FlushModeType.COMMIT);
				CommonSettingsQuery query = (CommonSettingsQuery) this.getQuery(CommonSettings.class);
				query.findDefault();
			}
		}
		catch (Exception e)
		{
			log(LogService.LOG_DEBUG, "EntityManager konnte nicht kreiert werden.");
			String app = System.getProperty("eclipse.application");
			if (app.equals("ch.eugster.colibri.client.application"))
			{
				resetEntityManager(e);
			}
			else
			{
				System.out.println();
			}
		}
		return this.entityManager != null;
	}
	
	protected Event getEvent(final IStatus status)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		if (Activator.getDefault() != null)
		{
			properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle());
			properties.put(EventConstants.BUNDLE_ID, Long.valueOf(Activator.getDefault().getBundle().getBundleId()));
			properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
		}
		properties.put(EventConstants.SERVICE_OBJECTCLASS, this.getClass().getName());
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
		properties.put("status", status);
		properties.put("show.failover.message", Boolean.valueOf(true));
		properties.put(EventConstants.EVENT_TOPIC, status.getMessage());
		properties.put(EventConstants.BUNDLE_ID, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
		if (status.getException() != null)
		{
			properties.put(EventConstants.EXCEPTION, status.getException());
			properties.put(EventConstants.EXCEPTION_MESSAGE, status.getException().getMessage() == null ? "" : status.getException().getMessage());
		}
		String message = "Die Verbindung zum Datenbankserver kann nicht hergestellt werden.";
		properties.put("message", message);
		properties.put("force", Boolean.valueOf(false));
		final Event event = new Event(status.getMessage(), properties);
		return event;
	}

//	private Event getEvent(Exception e)
//	{
//		final String topic = this.getTopic(e);
//		IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), message);
//		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
//		properties.put("show.failover.message", Boolean.valueOf(true));
//		properties.put(EventConstants.EVENT_TOPIC, topic);
//		properties.put(EventConstants.BUNDLE_ID, Activator.getDefault().getBundle().getSymbolicName());
//		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
//		if (e != null)
//		{
//			properties.put(EventConstants.EXCEPTION, e);
//			properties.put(EventConstants.EXCEPTION_MESSAGE, e.getMessage() == null ? "" : e.getMessage());
//		}
//		properties.put("message", message);
//		properties.put("status", status);
//		properties.put("force", Boolean.valueOf(false));
//		final Event event = new Event(topic, properties);
//		return event;
//	}

	public AbstractConnectionService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	@Override
	public AbstractEntity delete(final AbstractEntity entity) throws Exception
	{
		entity.setDeleted(true);
		return this.merge(entity);
	}

	public void resetEntityManager(Exception exception)
	{
		if (entityManager != null)
		{
			if (exception instanceof DatabaseException || exception instanceof PersistenceException)
			{
				entityManager.clear();
				entityManager.close();
				entityManager = null;
			}
		}
		log(LogService.LOG_ERROR, "Failover caused by " + exception.getClass().getName() + ": " + exception.getLocalizedMessage());
		this.getPersistenceService().sendEvent(this.getEvent(getStatus(exception)));
	}

	private IStatus getStatus(Exception exception)
	{
		return new Status(IStatus.ERROR, Activator.PLUGIN_ID, getTopic(), exception);
	}
	
	@Override
	public AbstractEntity find(final Class<? extends AbstractEntity> clazz, final Long id)
	{
		AbstractEntity entity = null;
		EntityManager entityManager = null;
		try
		{
			entityManager = this.getEntityManager();
			if (entityManager != null)
			{
				entity = entityManager.find(clazz, id);
			}
		}
		catch (Exception e)
		{
			this.resetEntityManager(e);
		}
		finally
		{
		}
		return entity;
	}
	
	@Override
	public AbstractEntity merge(final AbstractEntity entity) throws Exception
	{
		return this.merge(entity, true);
	}

	@Override
	public AbstractEntity merge(final AbstractEntity entity, boolean updateTimestamp) throws Exception
	{
		return this.merge(entity, updateTimestamp, true);
	}

	protected abstract void updateReplicationValue(Entity entity) throws Exception;

	@Override
	public synchronized AbstractEntity merge(AbstractEntity entity, final boolean updateTimestamp, boolean updateReplicatable) throws Exception
	{
		EntityManager entityManager = this.getEntityManager();
		if (entityManager != null)
		{
			EntityTransaction tx = entityManager.getTransaction();
			if (!tx.isActive())
			{
				tx.begin();
				if (updateTimestamp)
				{
					entity.setTimestamp(GregorianCalendar.getInstance(Locale.getDefault()));
				}

				try
				{
					entity = entityManager.merge(entity);
					tx.commit();
					logStored(entity);
				}
				catch (Exception e)
				{
					e.printStackTrace();
					if (tx.isActive())
					{
						tx.rollback();
					}
					if (e instanceof RollbackException)
					{
						RollbackException re = (RollbackException) e;
						if (re.getCause() instanceof DatabaseException)
						{
							DatabaseException de = (DatabaseException) re.getCause();
							if (de.getErrorCode() == 4002)
							{
								this.resetEntityManager(e);
								return entity;
							}
						}
					}
					throw new Exception(e);
				}
				finally
				{
					if (updateReplicatable)
					{
						updateReplicationValue(entity);
					}
				}
			}
		}
		return entity;
	}

	protected void logStored(AbstractEntity entity)
	{
		if (this.persistenceService.getLogService() != null)
		{
			this.persistenceService.getLogService().log(LogService.LOG_INFO,
					"Stored " + this.getProperties().getProperty(PersistenceUnitProperties.JDBC_URL) + ": "
							+ entity.getClass().getName() + " - " + entity.getId() + " - " + entity.getUpdate() + " - "
							+ entity.getVersion());
		}
	}

	@Override
	public void persist(final AbstractEntity entity) throws Exception
	{
		this.persist(entity, true);
	}

	public synchronized void persist(final AbstractEntity entity, final boolean updateTimestamp) throws Exception
	{
		EntityManager entityManager = this.getEntityManager();
		if (entityManager != null)
		{
			EntityTransaction tx = entityManager.getTransaction();
			boolean isActive = tx.isActive();
			if (!isActive)
			{
				tx.begin();
			}
			if (updateTimestamp)
			{
				entity.setTimestamp(GregorianCalendar.getInstance(Locale.getDefault()));
			}

			try
			{
				entityManager.persist(entity);
				if (!isActive)
				{
					tx.commit();
				}
				logStored(entity);
			}
			catch (Exception e)
			{
				resetEntityManager(e);
			}
			finally
			{
				if (!isActive && tx.isActive())
				{
					tx.rollback();
				}
				updateReplicationValue(entity);
			}
		}
	}

	public synchronized void remove(final AbstractEntity entity) throws Exception
	{
		EntityManager entityManager = this.getEntityManager();
		if (entityManager != null)
		{
			EntityTransaction tx = entityManager.getTransaction();
			boolean isActive = tx.isActive();
			if (!isActive)
			{
				tx.begin();
			}
			try
			{
				entityManager.remove(entity);
				if (!isActive)
				{
					tx.commit();
				}
				logStored(entity);
			}
			catch (Exception e)
			{
				resetEntityManager(e);
			}
			finally
			{
				if (!isActive && tx.isActive())
				{
					tx.rollback();
				}
			}
		}
	}

	@Override
	public EntityManager getEntityManager()
	{
		if (entityManager == null || !entityManager.isOpen())
		{
			if (getEntityManagerFactory() != null)
			{
				entityManager = this.createEntityManager();
				if (entityManager != null)
				{
					try
					{
						TaxUpdater.updateTaxes(this);
					}
					catch (Exception e)
					{
					}
				}
			}
		}
		if (entityManager != null)
		{
			login();
		}
		return entityManager;
	}

	protected void login()
	{
	}
	
	protected PersistenceService getPersistenceService()
	{
		return this.persistenceService;
	}

	protected void setPersistenceService(final PersistenceService persistenceService)
	{
		this.persistenceService = persistenceService;
	}

	protected abstract IStatus updateDatabase(final Properties properties);

}
