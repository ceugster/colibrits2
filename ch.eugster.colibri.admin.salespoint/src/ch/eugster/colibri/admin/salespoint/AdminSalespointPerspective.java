package ch.eugster.colibri.admin.salespoint;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminSalespointPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.salespoint.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
