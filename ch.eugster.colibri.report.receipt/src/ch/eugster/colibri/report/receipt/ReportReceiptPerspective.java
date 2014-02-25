package ch.eugster.colibri.report.receipt;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import ch.eugster.colibri.report.destination.views.DestinationView;
import ch.eugster.colibri.report.receipt.views.PaymentListView;
import ch.eugster.colibri.report.receipt.views.PositionListView;
import ch.eugster.colibri.report.receipt.views.ReceiptFilterView;
import ch.eugster.colibri.report.receipt.views.ReceiptListView;

public class ReportReceiptPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.report.receipt.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
		layout.setEditorAreaVisible(true);
		layout.addView(ReceiptFilterView.ID, IPageLayout.LEFT, .3F, "org.eclipse.ui.editorss");
		layout.addView(DestinationView.ID, IPageLayout.BOTTOM, .8F, ReceiptFilterView.ID);
		layout.addView(ReceiptListView.ID, IPageLayout.RIGHT, .7F, "org.eclipse.ui.editorss");
		layout.addView(PositionListView.ID, IPageLayout.BOTTOM, .5F, ReceiptListView.ID);
		layout.addView(PaymentListView.ID, IPageLayout.BOTTOM, .5F, PositionListView.ID);
		layout.setEditorAreaVisible(false);
	}

}
