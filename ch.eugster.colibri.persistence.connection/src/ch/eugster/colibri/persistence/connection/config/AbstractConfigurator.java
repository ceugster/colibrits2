package ch.eugster.colibri.persistence.connection.config;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceProvider;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.queries.SequenceQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;

public abstract class AbstractConfigurator
{
	private final Shell shell;

	private EntityManager entityManager;

	public AbstractConfigurator(final Shell shell, final Element connection)
	{
		this.shell = shell;
	}

	protected EntityManager getEntityManager()
	{
		if (this.entityManager == null || !entityManager.isOpen())
		{
			ServiceTracker<PersistenceProvider, PersistenceProvider> tracker = new ServiceTracker<PersistenceProvider, PersistenceProvider>(Activator.getDefault().getBundle().getBundleContext(),
					PersistenceProvider.class, null);
			tracker.open();
			PersistenceProvider persistenceProvider = (PersistenceProvider) tracker.getService();
			Map<String, Object> map = null;
			EntityManagerFactory factory = null;
			if (persistenceProvider != null)
			{
				Element current = Activator.getDefault().getCurrentConnectionElement();
				boolean embedded = Boolean.parseBoolean(current
						.getAttributeValue(ConnectionService.KEY_USE_EMBEDDED_DATABASE));
				if (embedded)
				{
					Properties properties = Activator.getDefault().getCacheConnectionProperties(current);
					map = Activator.getDefault().getCacheEntityManagerProperties(properties);
					factory = persistenceProvider.createEntityManagerFactory(ConnectionService.PERSISTENCE_UNIT_LOCAL,
							map);
				}
				else
				{
					Properties properties = Activator.getDefault().getServerConnectionProperties(current);
					map = Activator.getDefault().getServerEntityManagerProperties(properties);
					factory = persistenceProvider.createEntityManagerFactory(ConnectionService.PERSISTENCE_UNIT_SERVER,
							map);
				}
				this.entityManager = factory.createEntityManager();
			}
		}
		return this.entityManager;
	}

	protected Shell getShell()
	{
		return this.shell;
	}

	protected void log(final IStatus status)
	{
		Activator.getDefault().log(status.getMessage());
		Activator.getDefault().getLog().log(status);
	}

	protected void log(final String message)
	{
		Activator.getDefault().log(message);
	}

	protected void releaseEntityManager(final EntityManager entityManager)
	{
		if (entityManager != null)
		{
			if (entityManager.isOpen())
			{
				entityManager.close();
			}
		}
	}

	protected void start()
	{
		final IRunnableWithProgress runnable = new IRunnableWithProgress()
		{
			@Override
			public void run(final IProgressMonitor monitor)
			{
				try
				{
					monitor.beginTask("Die Datenbank wird konfiguriert...", 1);
					AbstractConfigurator.this.start(new SubProgressMonitor(monitor, 1));
				}
				finally
				{
					monitor.done();
				}
			}
		};
		final ProgressMonitorDialog dialog = new ProgressMonitorDialog(this.getShell());
		try
		{
			dialog.run(false, false, runnable);
		}
		catch (final InvocationTargetException e)
		{
		}
		catch (final InterruptedException e)
		{
		}
	}

	protected abstract void start(IProgressMonitor monitor);

	protected void updateSequence(final String key, final Long value)
	{
		final SequenceQuery sequenceQuery = new SequenceQuery(this.getEntityManager());
		sequenceQuery.findAndUpdate(key, value);
	}
}
