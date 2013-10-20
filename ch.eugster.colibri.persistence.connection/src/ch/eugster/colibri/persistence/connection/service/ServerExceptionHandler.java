package ch.eugster.colibri.persistence.connection.service;

import org.eclipse.persistence.exceptions.ExceptionHandler;

import ch.eugster.colibri.persistence.service.ServerService;

public class ServerExceptionHandler implements ExceptionHandler 
{
	private static ServerService serverService;
	
	public static void setServerService(ServerService service)
	{
		serverService = service;
	}
	
	public ServerExceptionHandler()
	{
		
	}
	
	@Override
	public Object handleException(RuntimeException exception) 
	{
		serverService.resetEntityManager(exception);
		return null;
	}

}
