package ch.eugster.colibri.admin.profile;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminProfilePerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.profile.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
