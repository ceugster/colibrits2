package ch.eugster.colibri.admin.currency;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminCurrencyPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.currency.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
