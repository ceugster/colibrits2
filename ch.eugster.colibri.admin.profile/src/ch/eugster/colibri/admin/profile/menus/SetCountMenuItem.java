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
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.progress.UIJob;

import ch.eugster.colibri.admin.profile.editors.tab.TabEditor;
import ch.eugster.colibri.admin.profile.editors.tab.TabEditorButton;

public class SetCountMenuItem extends MenuItem implements ActionListener
{
	private static final long serialVersionUID = 0l;

	private TabEditor editor;

	private TabEditorButton button;

	public SetCountMenuItem(final TabEditor editor, final TabEditorButton button, final String label, final String actionCommand)
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
				final InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), "Anzahl", "Geben Sie die gew�nschte Anzahl ein",
						Integer.toString(SetCountMenuItem.this.button.getKeys()[1].getCount()), new IInputValidator()
						{
							public String isValid(final String value)
							{
								String returnValue = null;
								try
								{
									Integer.parseInt(value);
								}
								catch (final NumberFormatException e)
								{
									returnValue = "Die Eingabe ist ung�ltig.";
								}
								return returnValue;
							}
						});
				if (dialog.open() == Window.OK)
				{
					SetCountMenuItem.this.button.getKeys()[1].setCount(Integer.parseInt(dialog.getValue()));
					SetCountMenuItem.this.button.getKeys()[1].setDeleted(false);
					SetCountMenuItem.this.button.update(SetCountMenuItem.this.editor.getFailOverState());
					SetCountMenuItem.this.editor.propertyChanged(this, IEditorPart.PROP_DIRTY);
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}
}
