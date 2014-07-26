package ch.eugster.colibri.persistence.connection.config;

import java.lang.reflect.InvocationTargetException;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.jdom.Element;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.model.Tax;
import ch.eugster.colibri.persistence.queries.SequenceQuery;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractConfigurator
{
	private Shell shell;

	private EntityManager entityManager;

	public AbstractConfigurator()
	{
	}

	public void setShell(Shell shell)
	{
		this.shell = shell;
	}
	
	public AbstractConfigurator(final Shell shell)
	{
		this.shell = shell;
	}

	protected EntityManager getEntityManager()
	{
		if (this.entityManager == null || !entityManager.isOpen())
		{
			ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
					PersistenceService.class, null);
			tracker.open();
			PersistenceService persistenceService = (PersistenceService) tracker.getService();
			EntityManagerFactory factory = null;
			if (persistenceService!= null)
			{
				Element current = Activator.getDefault().getCurrentConnectionElement();
				boolean embedded = Boolean.parseBoolean(current
						.getAttributeValue(ConnectionService.KEY_USE_EMBEDDED_DATABASE));
				if (embedded)
				{
					factory = persistenceService.getCacheService().getEntityManagerFactory();
				}
				else
				{
					factory = persistenceService.getServerService().getEntityManagerFactory();
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
		if (Activator.getDefault() != null)
		{
			Activator.getDefault().log(Activator.getDefault().getLogLevel(status.getSeverity()), status.getMessage());
		}
	}

	protected static void log(int level, final String message)
	{
		if (Activator.getDefault() != null)
		{
			Activator.getDefault().log(level, message);
		}
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
	
	protected String getTaxText(Tax tax)
	{
		if (tax.getTaxType().getCode().equals("U"))
		{
			return "Umsatzst.";
		}
		else if (tax.getTaxType().getCode().equals("M"))
		{
			return "Vorst. M/D";
		}
		else if (tax.getTaxType().getCode().equals("I"))
		{
			return "Vorst. I/B";
		}
		return "";
	}

}
