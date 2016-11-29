package ch.eugster.colibri.persistence.connection.service;

import org.eclipse.persistence.exceptions.ExceptionHandler;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.persistence.connection.Activator;
import ch.eugster.colibri.persistence.service.ServerService;

public class ServerExceptionHandler implements ExceptionHandler 
{
	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker;
	
	private static ServerService serverService;
	
	public static void setServerService(ServerService service)
	{
		serverService = service;
	}
	public ServerExceptionHandler()
	{
		eventAdminTracker = new ServiceTracker<EventAdmin,EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
		eventAdminTracker.open();
	}
	
	@Override
	public Object handleException(RuntimeException exception) 
	{
		serverService.stop();
		return null;
	}

}
