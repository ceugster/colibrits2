package ch.eugster.colibri.admin.app;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class AdminApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
	private static final String PERSPECTIVE = "ch.eugster.colibri.admin.salespoint.perspective";

	public AdminApplicationWorkbenchAdvisor(final IApplicationContext context)
	{
		super();
	}

	@Override
	public WorkbenchWindowAdvisor createWorkbenchWindowAdvisor(final IWorkbenchWindowConfigurer configurer)
	{
		return new AdminApplicationWorkbenchWindowAdvisor(configurer);
	}

	@Override
	public String getInitialWindowPerspectiveId()
	{
		return Platform.getBundle("ch.eugster.colibri.admin.salespoint") == null ? null : PERSPECTIVE;
	}

	@Override
	public void initialize(final IWorkbenchConfigurer configurer)
	{
		super.initialize(configurer);
		configurer.setSaveAndRestore(true);
	}

}
