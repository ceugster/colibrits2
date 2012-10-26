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
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.progress.UIJob;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SetNameMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	public SetNameMenuItem(final TabEditor editor, final TabEditorButton button, final String label, final String actionCommand)
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
				final InputDialog dialog = new InputDialog(
						SetNameMenuItem.this.editor.getSite().getShell(),
						"Beschriftung",
						"Erfassen Sie die gewünschte Beschriftung. Wenn Sie einen mehrzeiligen Text eingeben wollen, dann verwenden Sie als Zeilenschaltung den Wert '<br>' (z.B.: 'Zeile 1<br>Zeile 2')",
						SetNameMenuItem.this.button.extractText(SetNameMenuItem.this.button.getText()), null);
				if (dialog.open() == Window.OK)
				{
					SetNameMenuItem.this.button.getKeys()[1].setLabel(dialog.getValue());
					SetNameMenuItem.this.button.getKeys()[1].setDeleted(false);
					SetNameMenuItem.this.button.update(SetNameMenuItem.this.editor.getFailOverState());
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

}
