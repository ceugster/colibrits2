package ch.eugster.colibri.persistence.connection.handlers;

import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.sessions.factories.SessionCustomizer;

public class ColibriSessionCustomizer implements SessionCustomizer 
{
	@Override
	public void customize(Session session) throws Exception 
	{
		DatabaseLogin login = (DatabaseLogin) session.getDatasourceLogin();
		login.setConnectionHealthValidatedOnError(false);
	}
}
