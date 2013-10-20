package ch.eugster.colibri.client.app;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;

public class ClientApplicationActionBarAdvisor extends ActionBarAdvisor
{
	public ClientApplicationActionBarAdvisor(final IActionBarConfigurer configurer)
	{
		super(configurer);
	}

	protected void fillStatusLine(IStatusLineManager statusLine) 
	{
    }
}
