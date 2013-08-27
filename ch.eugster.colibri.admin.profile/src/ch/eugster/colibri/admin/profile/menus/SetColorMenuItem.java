/*
 * Created on 01.04.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.menus;

import java.awt.MenuItem;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.ColorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.progress.UIJob;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SetColorMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	public SetColorMenuItem(final TabEditor editor, final TabEditorButton button, final String label, final String actionCommand)
	{
		super();
		this.setLabel(label);
		this.setActionCommand(actionCommand);
		this.editor = editor;
		this.button = button;
		this.addActionListener(this);
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final UIJob uiJob = new UIJob("menu action")
		{
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor)
			{
				RGB rgb = SetColorMenuItem.this.getRGB();
				final Shell shell = new Shell(this.getDisplay());
				final ColorDialog dialog = new ColorDialog(shell);
				dialog.setRGB(rgb);
				final RGB result = rgb = dialog.open();
				{
					if (result != null)
					{
						SetColorMenuItem.this.setRGB(result);
						SetColorMenuItem.this.button.getKeys()[1].setDeleted(false);
						SetColorMenuItem.this.button.update(SetColorMenuItem.this.editor.getFailOverState());
					}
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	private RGB getRGB()
	{
		RGB rgb = null;
		if (this.editor.getFailOverState())
		{
			if (this.getActionCommand().equals("foreground"))
			{
				final java.awt.Color color = new java.awt.Color(this.button.getKeys()[1].getFailOverFg());
				rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
			}
			else if (this.getActionCommand().equals("background"))
			{
				final java.awt.Color color = new java.awt.Color(this.button.getKeys()[1].getFailOverBg());
				rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
			}
		}
		else
		{
			if (this.getActionCommand().equals("foreground"))
			{
				final java.awt.Color color = new java.awt.Color(this.button.getKeys()[1].getNormalFg());
				rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
			}
			else if (this.getActionCommand().equals("background"))
			{
				final java.awt.Color color = new java.awt.Color(this.button.getKeys()[1].getNormalBg());
				rgb = new RGB(color.getRed(), color.getGreen(), color.getBlue());
			}
		}
		return rgb;
	}

	private void setRGB(final RGB rgb)
	{
		final java.awt.Color color = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
		if (this.editor.getFailOverState())
		{
			if (this.getActionCommand().equals("foreground"))
			{
				this.button.getKeys()[1].setFailOverFg(color.getRGB());
			}
			else if (this.getActionCommand().equals("background"))
			{
				this.button.getKeys()[1].setFailOverBg(color.getRGB());
			}
		}
		else
		{
			if (this.getActionCommand().equals("foreground"))
			{
				this.button.getKeys()[1].setNormalFg(color.getRGB());
			}
			else if (this.getActionCommand().equals("background"))
			{
				this.button.getKeys()[1].setNormalBg(color.getRGB());
			}
		}
	}

}
