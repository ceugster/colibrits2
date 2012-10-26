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
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.UIJob;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SetArticleCodeMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	public SetArticleCodeMenuItem(final TabEditor editor, final TabEditorButton button, final String label, final String actionCommand)
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
				final InputDialog dialog = new InputDialog(Display.getCurrent().getActiveShell(), "Artikelcode", "Geben Sie den Artikelcode ein",
						SetArticleCodeMenuItem.this.button.getKeys()[1].getProductCode(), null);
				if (dialog.open() == Window.OK)
				{
					SetArticleCodeMenuItem.this.button.getKeys()[1].setProductCode(dialog.getValue());
					SetArticleCodeMenuItem.this.button.getKeys()[1].setDeleted(false);
					SetArticleCodeMenuItem.this.button.update(SetArticleCodeMenuItem.this.editor.getFailOverState());
					SetArticleCodeMenuItem.this.editor.propertyChanged(this, IEditorPart.PROP_DIRTY);
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}
}
