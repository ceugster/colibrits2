package ch.eugster.colibri.persistence.connection.service;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.eclipse.core.runtime.IStatus;
import org.jdom.Element;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.config.DatabaseUpdater;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Entity;
import ch.eugster.colibri.persistence.queries.AbstractQuery;
import ch.eugster.colibri.persistence.service.CacheService;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class CacheServiceImpl extends AbstractConnectionService implements CacheService
{
	public CacheServiceImpl(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public AbstractQuery<? extends AbstractEntity> getQuery(final Class<? extends AbstractEntity> adaptable)
	{
		return this.getQuery(this, adaptable);
	}

	@Override
	public Properties getProperties()
	{
		final Element connection = Activator.getDefault().getCurrentConnectionElement();
		return Activator.getDefault().getCacheConnectionProperties(connection);
	}

	@Override
	protected IStatus updateDatabase(final Properties properties)
	{
		final DatabaseUpdater databaseUpdater = DatabaseUpdater.newInstance(properties);
		return databaseUpdater.updateDatabase();
	}

	@Override
	protected String getTopic()
	{
		return CacheService.EVENT_TOPIC;
	}

	@Override
	protected EntityManagerFactory createEntityManagerFactory()
	{
		Properties properties = this.getProperties();
		Map<String, Object> map = Activator.getDefault().getCacheEntityManagerProperties(properties);
		EntityManagerFactory factory = this.getPersistenceService().getPersistenceProvider()
				.createEntityManagerFactory(ConnectionService.PERSISTENCE_UNIT_LOCAL, map);
		return factory;
	}

	@Override
	protected void updateReplicationValue(Entity entity) 
	{
		//Do nothing (only ServerService should update the replication value
	}

}
