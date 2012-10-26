package ch.eugster.colibri.admin.ui.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.IHandler;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.service.PersistenceService;

public abstract class AbstractPersistenceClientHandler extends AbstractHandler implements IHandler
{
	protected ServiceTracker<PersistenceService, PersistenceService> tracker;

	protected PersistenceService persistenceService;
	
	protected abstract BundleContext getBundleContext();
	
	public AbstractPersistenceClientHandler()
	{
		tracker = new ServiceTracker<PersistenceService, PersistenceService>(getBundleContext(), PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(ServiceReference<PersistenceService> reference)
			{
				AbstractPersistenceClientHandler.this.persistenceService = super.addingService(reference);
				setBaseEnabled(true);
				return persistenceService;
			}

			@Override
			public void removedService(ServiceReference<PersistenceService> reference, PersistenceService service)
			{
				AbstractPersistenceClientHandler.this.persistenceService = null;
				setBaseEnabled(false);
			}
		};
		tracker.open();
	}
	
	public void dispose()
	{
		tracker.close();
	}

}
