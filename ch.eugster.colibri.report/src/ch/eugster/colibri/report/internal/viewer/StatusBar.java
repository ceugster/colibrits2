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
package ch.eugster.colibri.report.internal.viewer;

import java.text.MessageFormat;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Status bar component. Displays the current page and the total number of pages
 * in the viewed document.
 * 
 * @author Peter Severin (peter_p_s@users.sourceforge.net)
 */
public class StatusBar implements IReportViewerAware
{

	/** The viewer */
	private IReportViewer viewer;

	/** Style */
	private int style = SWT.CENTER;

	/** The label used to display status information */
	private Label label;

	/** Viewer model change listener */
	private IReportViewerListener listener = new IReportViewerListener()
	{
		@Override
		public void viewerStateChanged(final ReportViewerEvent evt)
		{
			refresh();
		}
	};

	/**
	 * Default constructor. The control will be created using default style
	 */
	public StatusBar()
	{
	}

	/**
	 * Constructor that allows to specify a different style for the status bar
	 * control. Possible styles are documented in the {@link Label}class.
	 * 
	 * @param style
	 *            style
	 * @see Label
	 */
	public StatusBar(final int style)
	{
		this.style = style;
	}

	/**
	 * Creates the status bar control. If called multiple times, the control
	 * that was created first time is returned.
	 * 
	 * @param parent
	 *            the parent
	 * @return the created control
	 */
	public Control createControl(final Composite parent)
	{
		if (label == null)
		{
			label = new Label(parent, style);
			refresh();
		}

		return label;
	}

	/**
	 * @see ch.eugster.events.report.internal.viewer.IReportViewerAware#getReportViewer()
	 */
	@Override
	public IReportViewer getReportViewer()
	{
		return viewer;
	}

	private void refresh()
	{
		if (label == null || label.isDisposed())
			return;

		if (viewer == null || !viewer.hasDocument())
		{
			label.setText(""); //$NON-NLS-1$
		}
		else
		{
			label.setText(MessageFormat.format("{0} von {1}", new Object[] { //$NON-NLS-1$
					new Integer(viewer.getPageIndex() + 1), new Integer(viewer.getDocument().getPages().size()) }));
		}
	}

	/**
	 * @see ch.eugster.events.report.internal.viewer.IReportViewerAware#setReportViewer(ch.eugster.events.report.internal.viewer.IReportViewer)
	 */
	@Override
	public void setReportViewer(final IReportViewer viewer)
	{
		if (viewer != null)
			viewer.removeReportViewerListener(listener);
		this.viewer = viewer;
		if (viewer != null)
			viewer.addReportViewerListener(listener);
		refresh();
	}
}