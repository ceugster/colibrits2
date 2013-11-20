package ch.eugster.colibri.persistence.transfer.impl.services;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.transfer.impl.Activator;

public abstract class AbstractTransfer {

	protected LogService logService;

	protected PersistenceService persistenceService;
	
	public AbstractTransfer(LogService logService, PersistenceService persistenceService) 
	{
		this.logService = logService;
		this.persistenceService = persistenceService;
	}

	protected void log(int level, String message) 
	{
		if (this.logService != null)
		{
			this.logService.log(level, message);
		}
	}

	protected IStatus getStatus(Exception e)
	{
		if (e == null)
		{
			return new Status(IStatus.OK, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_TRANSFER.topic());
		}
		else
		{
			return new Status(IStatus.ERROR, Activator.getDefault().getBundleContext().getBundle().getSymbolicName(), Topic.SCHEDULED_TRANSFER.topic(), new Exception("Die Verbindung zur Datenbank auf dem Server kann nicht hergestellt werden."));
		}
	}

}