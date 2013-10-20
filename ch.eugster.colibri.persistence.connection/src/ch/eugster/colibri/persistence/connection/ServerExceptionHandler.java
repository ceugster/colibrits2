package ch.eugster.colibri.persistence.connection;

import org.eclipse.persistence.exceptions.ExceptionHandler;

public class ServerExceptionHandler implements ExceptionHandler 
{
	public ServerExceptionHandler()
	{}

	@Override
	public Object handleException(RuntimeException exception) 
	{
		exception.printStackTrace();
		throw exception;
	}

}
