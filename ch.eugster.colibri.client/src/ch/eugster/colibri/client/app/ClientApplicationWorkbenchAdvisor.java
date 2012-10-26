package ch.eugster.colibri.client.app;

import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class ClientApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
	private static final String PERSPECTIVE = "ch.eugster.colibri.client.perspective";

	public ClientApplicationWorkbenchAdvisor(final IApplicationContext context)
	{
		super();
	}

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer)
	{
		return new ClientApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId()
	{
		return ClientApplicationWorkbenchAdvisor.PERSPECTIVE;
	}

	@Override
	public void initialize(final IWorkbenchConfigurer configurer)
	{
		configurer.setSaveAndRestore(false);
	}

}
