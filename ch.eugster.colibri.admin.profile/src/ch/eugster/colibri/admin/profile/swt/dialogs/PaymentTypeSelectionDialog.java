/*
 * Created on 21.05.2008
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.profile.swt.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import ch.eugster.colibri.persistence.model.PaymentType;

/**
 * @author ceugster
 * 
 *         To change the template for this generated type comment go to
 *         Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PaymentTypeSelectionDialog extends TitleAreaDialog
{
	private Combo combo;

	private PaymentTypeSelectionDialogInput input;

	public PaymentTypeSelectionDialog(final Shell shell, final PaymentTypeSelectionDialogInput input)
	{
		super(shell);
		this.input = input;
	}

	@Override
	protected void createButtonsForButtonBar(final Composite parent)
	{
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, "Abbrechen", false);
	}

	@Override
	protected Control createDialogArea(final Composite parent)
	{
		// setTitle("Auswahl Rückgeld");
		// setMessage("Wählen Sie aus der Liste die gewünschte Zahlungsart.");

		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		// Label label = new Label(composite, SWT.NONE);
		// label.setText("Zahlungsart");
		//		
		// PaymentType[] paymentTypes = PaymentType.selectBacks(false);
		// this.combo = new Combo(composite, SWT.READ_ONLY | SWT.DROP_DOWN);
		// this.combo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// PaymentType type = this.input.getPaymentType();
		// String selection = "";
		// if (type != null) selection = type.code + " " +
		// this.input.getPaymentType().name;
		//		
		// for (PaymentType paymentType : paymentTypes)
		// {
		// String data = paymentType.code + " " + paymentType.name;
		// this.combo.add(data);
		// this.combo.setData(data, paymentType);
		// if (data.equals(selection))
		// this.combo.select(this.combo.getItemCount() - 1);
		// }
		// if (this.combo.getItemCount() > 0 && this.combo.getSelectionIndex()
		// == -1)
		// this.combo.select(0);

		return null;
	}

	@Override
	protected void okPressed()
	{
		final Object object = combo.getData(combo.getItem(combo.getSelectionIndex()));
		input.setPaymentType((PaymentType) object);
		super.okPressed();
	}
}
