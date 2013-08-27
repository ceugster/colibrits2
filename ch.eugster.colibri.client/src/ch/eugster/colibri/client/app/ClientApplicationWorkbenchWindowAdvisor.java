package ch.eugster.colibri.client.app;

import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

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
		this.getWindowConfigurer().setShowFastViewBars(false);
		this.getWindowConfigurer().setShowMenuBar(false);
		this.getWindowConfigurer().setShowPerspectiveBar(false);
		this.getWindowConfigurer().setShowProgressIndicator(false);
		this.getWindowConfigurer().setShowCoolBar(false);
	}

}
