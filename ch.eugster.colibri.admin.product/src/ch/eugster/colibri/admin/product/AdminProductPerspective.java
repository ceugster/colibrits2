package ch.eugster.colibri.admin.product;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminProductPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.product.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
