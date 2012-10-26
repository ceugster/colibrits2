package ch.eugster.colibri.report.receipt;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class ReportReceiptPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.report.receipt.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(false);
	}

}
