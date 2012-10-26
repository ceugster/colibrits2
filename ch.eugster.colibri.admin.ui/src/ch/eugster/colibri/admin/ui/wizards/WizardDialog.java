/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.ui.wizards;

import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

public class WizardDialog extends org.eclipse.jface.wizard.WizardDialog
{
	public WizardDialog(final Shell shell, final Wizard wizard)
	{
		super(shell, wizard);
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent)
	{
		super.createButtonsForButtonBar(parent);
		getButton(16).setText("Speichern");
		getButton(Window.CANCEL).setText("Abbrechen");
	}
}
