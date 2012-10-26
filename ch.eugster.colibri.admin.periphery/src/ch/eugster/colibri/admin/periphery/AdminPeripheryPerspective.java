package ch.eugster.colibri.admin.periphery;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminPeripheryPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.periphery.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
