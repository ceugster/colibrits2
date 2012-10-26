package ch.eugster.colibri.admin.common.settings;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminCommonSettingsPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.common.settings.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
