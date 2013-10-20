package ch.eugster.colibri.client.app;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.osgi.service.log.LogService;

import ch.eugster.colibri.client.Activator;

public class ClientApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor
{
	public static final String TITLE = "ColibriTS II";

	public ClientApplicationWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer)
	{
		super(configurer);
	}

	@Override
	public ActionBarAdvisor createActionBarAdvisor(final IActionBarConfigurer configurer)
	{
		return new ClientApplicationActionBarAdvisor(configurer);
	}
	
	@Override
	public void preWindowOpen()
	{
		Activator.getDefault().log(LogService.LOG_INFO, "Konfiguriere Programmfenster.");
		this.getWindowConfigurer().setShowFastViewBars(false);
		this.getWindowConfigurer().setShowMenuBar(false);
		this.getWindowConfigurer().setShowPerspectiveBar(false);
		this.getWindowConfigurer().setShowProgressIndicator(false);
		this.getWindowConfigurer().setShowCoolBar(false);
		this.getWindowConfigurer().setShowStatusLine(true);
	}

}
