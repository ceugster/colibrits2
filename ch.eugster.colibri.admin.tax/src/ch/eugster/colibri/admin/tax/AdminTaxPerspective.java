package ch.eugster.colibri.admin.tax;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminTaxPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.tax.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
