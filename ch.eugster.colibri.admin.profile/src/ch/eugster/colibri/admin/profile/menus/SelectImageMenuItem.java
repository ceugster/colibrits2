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

import javax.swing.ImageIcon;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.UIJob;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SelectImageMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	public SelectImageMenuItem(final TabEditor editor, final TabEditorButton button, final String label, final String actionCommand)
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
		final UIJob uiJob = new UIJob("set dirty")
		{
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor)
			{
				final FileDialog dialog = new FileDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
				dialog.setFileName(SelectImageMenuItem.this.button.getKeys()[1].getImageId());
				dialog.setFilterExtensions(new String[] { "*.gif", "*.png", "*.jpg" });
				dialog.setFilterIndex(0);
				dialog.setOverwrite(false);
				dialog.setText("Wählen Sie die gewünschte Datei aus:");
				final String path = dialog.open();
				if (path != null)
				{
					SelectImageMenuItem.this.button.getKeys()[1].setImageId(path);
					final ImageIcon image = new ImageIcon(path);
					SelectImageMenuItem.this.button.setIcon(image);
					SelectImageMenuItem.this.button.getKeys()[1].setDeleted(false);
					SelectImageMenuItem.this.button.update(SelectImageMenuItem.this.editor.getFailOverState());
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

}
