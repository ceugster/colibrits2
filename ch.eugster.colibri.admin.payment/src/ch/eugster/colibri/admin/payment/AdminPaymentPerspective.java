package ch.eugster.colibri.admin.payment;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class AdminPaymentPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.admin.payment.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
	}

}
