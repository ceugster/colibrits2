package ch.eugster.colibri.persistence.connection.service;

import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.connection.config.DatabaseUpdater;
import ch.eugster.colibri.persistence.connection.dialog.LoginDialog;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.Entity;
import ch.eugster.colibri.persistence.model.IReplicationRelevant;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.queries.AbstractQuery;
import ch.eugster.colibri.persistence.queries.UserQuery;
import ch.eugster.colibri.persistence.queries.VersionQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.service.ServerService;

public class ServerServiceImpl extends AbstractConnectionService implements ServerService
{
	public ServerServiceImpl(final PersistenceService persistenceService)
	{
		super(persistenceService);
	}

	@Override
	public AbstractQuery<? extends AbstractEntity> getQuery(final Class<? extends AbstractEntity> adaptable)
	{
		return this.getQuery(this, adaptable);
	}

	protected void login()
	{
		if (User.getLoginUser() == null)
		{
			String application = System.getProperty("eclipse.application");
			if (!application.contains("client"))
			{
				Display display = Display.getCurrent();
				if (display == null)
				{
					display = new Display();
				}
				Shell shell = new Shell(display);
				final UserQuery userQuery = (UserQuery) getQuery(User.class);
				LoginDialog dialog = new LoginDialog(shell, userQuery);
				dialog.open();
			}
		}
	}
	
	@Override
	public boolean isLocal()
	{
		return this.getEntityManagerFactory() == this.getPersistenceService().getCacheService()
				.getEntityManagerFactory();
	}

	@Override
	public Properties getProperties()
	{
		final Element connection = Activator.getDefault().getCurrentConnectionElement();
		return Activator.getDefault().getServerConnectionProperties(connection);
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
		return "ch/eugster/colibri/persistence/server/database";
	}

	@Override
	protected EntityManagerFactory createEntityManagerFactory()
	{
		EntityManagerFactory factory = null;
		Properties properties = this.getProperties();
		Boolean embedded = Boolean.valueOf(properties.getProperty(ConnectionService.KEY_USE_EMBEDDED_DATABASE));
		if (embedded.booleanValue())
		{
			factory = this.getPersistenceService().getCacheService().getEntityManagerFactory();
		}
		else
		{
			Map<String, Object> map = Activator.getDefault().getServerEntityManagerProperties(properties);
			factory = this.getPersistenceService().getPersistenceProvider()
					.createEntityManagerFactory(ConnectionService.PERSISTENCE_UNIT_SERVER, map);
		}
		return factory;
	}

	@Override
	protected void updateReplicationValue(Entity entity) 
	{
		if (IReplicationRelevant.class.isInstance(entity))
		{
			VersionQuery query = (VersionQuery) this.getQuery(Version.class);
			Version version = query.findDefault();
			version.setReplicationValue(version.getReplicationValue() + 1);
			query.setDefault((Version)this.merge(version));
		}
	}

}
