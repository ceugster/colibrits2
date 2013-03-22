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

import java.text.DecimalFormat;

import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.util.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import ch.eugster.colibri.report.internal.viewer.IReportViewer;
import ch.eugster.colibri.report.internal.viewer.IReportViewerListener;
import ch.eugster.colibri.report.internal.viewer.ReportViewerEvent;

/**
 * Zoom contribution item
 * 
 * @author Peter Severin (peter_p_s@users.sourceforge.net)
 */
public class ZoomComboContributionItem extends ContributionItem implements IReportViewerListener, Listener
{

	private static DecimalFormat ZOOM_FORMAT = new DecimalFormat("####%"); //$NON-NLS-1$

	private IReportViewer viewer;

	private Combo combo;

	private ToolItem toolitem;

	private double[] zoomLevels;

	/**
	 * Constructs the action by specifying the report viewer to associate with
	 * the item.
	 * 
	 * @param viewer
	 *            the report viewer
	 */
	public ZoomComboContributionItem(final IReportViewer viewer)
	{
		Assert.isNotNull(viewer);
		this.viewer = viewer;
		this.viewer.addReportViewerListener(this);
		refresh();
	}

	private Control createControl(final Composite parent)
	{
		combo = new Combo(parent, SWT.DROP_DOWN);
		combo.addListener(SWT.Selection, this);
		combo.addListener(SWT.DefaultSelection, this);
		combo.addListener(SWT.FocusIn, this);

		// Initialize width of combo
		combo.setItems(new String[] { "8888%" }); //$NON-NLS-1$
		combo.pack();

		refresh();
		return combo;
	}

	/**
	 * @see org.eclipse.jface.action.ContributionItem#dispose()
	 */
	@Override
	public void dispose()
	{
		viewer.removeReportViewerListener(this);
		combo = null;
		viewer = null;
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public final void fill(final Composite parent)
	{
		createControl(parent);
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.Menu,
	 *      int)
	 */
	@Override
	public final void fill(final Menu parent, final int index)
	{
		Assert.isTrue(false, Messages.getString("ZoomComboContributionItem.cannotAddToMenu")); //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.jface.action.IContributionItem#fill(org.eclipse.swt.widgets.ToolBar,
	 *      int)
	 */
	@Override
	public void fill(final ToolBar parent, final int index)
	{
		toolitem = new ToolItem(parent, SWT.SEPARATOR, index);
		Control control = createControl(parent);
		toolitem.setWidth(combo.getSize().x);
		toolitem.setControl(control);
	}

	private String getZoomAsText()
	{
		return ZOOM_FORMAT.format(viewer.getZoom());
	}

	private String[] getZoomLevelsAsText()
	{
		if (zoomLevels == null)
		{
			return new String[] { "100%" }; //$NON-NLS-1$
		}
		else
		{
			String[] textZoomLevels = new String[zoomLevels.length];
			for (int i = 0; i < textZoomLevels.length; i++)
			{
				textZoomLevels[i] = ZOOM_FORMAT.format(zoomLevels[i]);
			}
			return textZoomLevels;
		}
	}

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	@Override
	public void handleEvent(final Event event)
	{
		switch (event.type)
		{
			case SWT.FocusIn:
				refresh();
				break;
			case SWT.Selection:
			case SWT.DefaultSelection:
				onSelection();
				break;
		}
	}

	private void onSelection()
	{
		if (viewer.hasDocument())
		{
			if (combo.getSelectionIndex() >= 0)
				setZoomAsText(combo.getItem(combo.getSelectionIndex()));
			else
				setZoomAsText(combo.getText());
		}
	}

	private void refresh()
	{
		if (combo == null || combo.isDisposed())
			return;
		try
		{
			if (!viewer.canChangeZoom())
			{
				combo.setEnabled(false);
				combo.setText(""); //$NON-NLS-1$
			}
			else
			{
				combo.removeListener(SWT.Selection, this);
				combo.removeListener(SWT.DefaultSelection, this);
				if (zoomLevels != viewer.getZoomLevels())
				{
					this.zoomLevels = viewer.getZoomLevels();
					combo.setItems(getZoomLevelsAsText());
				}

				String zoom = getZoomAsText();
				int index = combo.indexOf(zoom);
				if (index != -1)
					combo.select(index);
				else
					combo.setText(zoom);
				combo.setEnabled(true);
				combo.addListener(SWT.Selection, this);
				combo.addListener(SWT.DefaultSelection, this);
			}
		}
		catch (SWTException exception)
		{
			if (!SWT.getPlatform().equals("gtk")) //$NON-NLS-1$
				throw exception;
		}
	}

	private void setZoomAsText(String zoomText)
	{
		int percentIndex = zoomText.indexOf('%');
		if (percentIndex != -1)
			zoomText = zoomText.substring(0, percentIndex);
		try
		{
			final float zoom = Float.parseFloat(zoomText) / 100;
			BusyIndicator.showWhile(null, new Runnable()
			{
				@Override
				public void run()
				{
					viewer.setZoom(zoom);
				}
			});
		}
		catch (NumberFormatException e)
		{
			Display.getCurrent().beep();
		}

		combo.removeListener(SWT.Selection, this);
		combo.removeListener(SWT.DefaultSelection, this);
		String zoom = ZOOM_FORMAT.format(viewer.getZoom());
		int index = combo.indexOf(zoom);
		if (index != -1)
			combo.select(index);
		else
			combo.setText(zoom);
		combo.addListener(SWT.Selection, this);
		combo.addListener(SWT.DefaultSelection, this);
	}

	/**
	 * @see ch.eugster.colibri.report.internal.viewer.IReportViewerListener#viewerStateChanged(ch.eugster.colibri.report.internal.viewer.ReportViewerEvent)
	 */
	@Override
	public void viewerStateChanged(final ReportViewerEvent evt)
	{
		refresh();
	}

	private static int getTextWidth(final String string, final Control control)
	{
		GC gc = new GC(control);
		try
		{
			return gc.textExtent(string).x;
		}
		finally
		{
			gc.dispose();
		}
	}

}
