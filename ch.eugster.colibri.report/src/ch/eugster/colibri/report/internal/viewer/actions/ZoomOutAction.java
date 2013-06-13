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

/**
 * Zoom out action
 * 
 * @author Peter Severin (peter_p_s@users.sourceforge.net)
 */
public class ZoomOutAction extends AbstractReportViewerAction
{
	/**
	 * @see AbstractReportViewerAction#AbstractReportViewerAction(IReportViewer)
	 */
	public ZoomOutAction(final IReportViewer viewer)
	{
		super(viewer);

		setText("Verkleinern"); //$NON-NLS-1$
		setToolTipText("Verkleinern"); //$NON-NLS-1$
		setImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("zoomminus"));
		setDisabledImageDescriptor(Activator.getDefault().getImageRegistry().getDescriptor("zoomminusd"));
	}

	/**
	 * @see com.jasperassistant.designer.viewer.actions.AbstractReportViewerAction#calculateEnabled()
	 */
	@Override
	protected boolean calculateEnabled()
	{
		return getReportViewer().canZoomOut();
	}

	/**
	 * @see com.jasperassistant.designer.viewer.actions.AbstractReportViewerAction#runBusy()
	 */
	@Override
	protected void runBusy()
	{
		getReportViewer().zoomOut();
	}
}
