package ch.eugster.colibri.client.ui.app;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ClientPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.client.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
		layout.addStandaloneView("ch.eugster.colibri.client.view", false, IPageLayout.LEFT, 1F, layout.getEditorArea());
	}

}
