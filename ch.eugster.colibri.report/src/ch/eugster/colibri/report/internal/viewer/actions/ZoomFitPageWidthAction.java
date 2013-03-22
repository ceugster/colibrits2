/*
 * SWTJasperViewer - Free SWT/JFace report viewer for JasperReports.
 * Copyright (C) 2004  Peter Severin (peter_p_s@users.sourceforge.net)
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307, USA.
 */
package ch.eugster.colibri.report.internal.viewer.actions;

import ch.eugster.colibri.report.Activator;
import ch.eugster.colibri.report.internal.viewer.IReportViewer;
import ch.eugster.colibri.report.internal.viewer.ReportViewerEvent;

/**
 * Fit page width zoom action
 * 
 * @author Peter Severin (peter_p_s@users.sourceforge.net)
 */
public class ZoomFitPageWidthAction extends AbstractReportViewerAction
{
	/**
	 * @see AbstractReportViewerAction#AbstractReportViewerAction(IReportViewer)
	 */
	public ZoomFitPageWidthAction(final IReportViewer viewer)
	{
		super(viewer);

		setText(Messages.getString("ZoomFitPageWidthAction.label")); //$NON-NLS-1$
		setToolTipText(Messages.getString("ZoomFitPageWidthAction.tooltip")); //$NON-NLS-1$
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("zoomfitwidth"));
		setDisabledImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("zoomfitwidthd"));
		update();
	}

	/**
	 * @see com.jasperassistant.designer.viewer.actions.AbstractReportViewerAction#calculateEnabled()
	 */
	@Override
	protected boolean calculateEnabled()
	{
		return getReportViewer().canChangeZoom();
	}

	/**
	 * @see com.jasperassistant.designer.viewer.actions.AbstractReportViewerAction#runBusy()
	 */
	@Override
	protected void runBusy()
	{
		getReportViewer().setZoomMode(IReportViewer.ZOOM_MODE_FIT_WIDTH);
		update();
	}

	private void update()
	{
		setChecked(getReportViewer().getZoomMode() == IReportViewer.ZOOM_MODE_FIT_WIDTH);
	}

	/**
	 * @see com.jasperassistant.designer.viewer.actions.AbstractReportViewerAction#viewerStateChanged(ch.eugster.colibri.report.internal.viewer.ReportViewerEvent)
	 */
	@Override
	public void viewerStateChanged(final ReportViewerEvent evt)
	{
		update();
		super.viewerStateChanged(evt);
	}
}
