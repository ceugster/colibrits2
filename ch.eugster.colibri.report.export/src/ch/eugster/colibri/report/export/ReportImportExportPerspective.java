package ch.eugster.colibri.report.export;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import ch.eugster.colibri.report.daterange.views.DateView;
import ch.eugster.colibri.report.destination.views.DestinationView;
import ch.eugster.colibri.report.export.views.ImportExportView;
import ch.eugster.colibri.report.salespoint.views.SalespointView;

public class ReportImportExportPerspective implements IPerspectiveFactory
{
	public static final String ID = "ch.eugster.colibri.report.export.perspective";

	@Override
	public void createInitialLayout(final IPageLayout layout)
	{
//		layout.setEditorAreaVisible(true);
//		layout.addView(ImportExportView.ID, IPageLayout.RIGHT, .7F, "org.eclipse.ui.editorss");
//		layout.addView(SalespointView.ID, IPageLayout.LEFT, .3F, "org.eclipse.ui.editorss");
//		layout.addView(DateView.ID, IPageLayout.BOTTOM, .7F, SalespointView.ID);
		layout.setEditorAreaVisible(false);
	}

}
