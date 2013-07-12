package ch.eugster.colibri.admin.app;

import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.app.IApplicationContext;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.application.IWorkbenchConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchAdvisor;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;

public class AdminApplicationWorkbenchAdvisor extends WorkbenchAdvisor
{
	@Override
	public boolean preShutdown() 
	{
		IEditorReference[] references = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
		for (IEditorReference reference : references)
		{
			IEditorPart editor = reference.getEditor(true);
			if (editor.isDirty())
			{
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().activate(editor);
				return !MessageDialog.openQuestion(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), "Änderungen speichern", "Sie haben Änderungen vorgenommen, die noch nicht gespeichert worden sind. Wollen Sie diese Änderungen speichern?");
			}
		}
		return true;
	}

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
		configurer.setSaveAndRestore(false);
	}

}
