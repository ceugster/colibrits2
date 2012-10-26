/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.persistence.connection.wizard;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Button;
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
		Button button = getButton(IDialogConstants.BACK_ID);
		if (button != null)
		{
			button.setText("Zurück");
		}
		button = getButton(IDialogConstants.NEXT_ID);
		if (button != null)
		{
			button.setText("Nächste");
		}
		button = getButton(IDialogConstants.CANCEL_ID);
		if (button != null)
		{
			button.setText("Abbrechen");
		}
		button = getButton(IDialogConstants.FINISH_ID);
		if (button != null)
		{
			button.setText("Ausführen");
		}
	}
}
