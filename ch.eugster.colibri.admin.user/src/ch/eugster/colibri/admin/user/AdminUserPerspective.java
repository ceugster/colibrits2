package ch.eugster.colibri.admin.user;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminUserPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.user.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
