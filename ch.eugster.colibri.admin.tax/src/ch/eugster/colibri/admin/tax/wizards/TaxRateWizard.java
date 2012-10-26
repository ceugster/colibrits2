/*
 * Created on 18.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.wizards;

import org.eclipse.jface.resource.ImageDescriptor;

import ch.eugster.colibri.admin.tax.TaxActivator;
import ch.eugster.colibri.admin.ui.wizards.ListAndEditWizardDialog;
import ch.eugster.colibri.admin.ui.wizards.TableAndEditWizard;
import ch.eugster.colibri.persistence.model.TaxRate;

public class TaxRateWizard extends TableAndEditWizard<TaxRate>
{

	@SuppressWarnings("unchecked")
	@Override
	public void addPages()
	{
		final ImageDescriptor descriptor = TaxActivator.getDefault().getImageRegistry().getDescriptor("defcon_wiz.png");
		tablePage = new TaxRateTableWizardPage("TablePage", "Mehrwertsteuersatzarten", descriptor);
		tablePage.addSelectionChangedListener((ListAndEditWizardDialog<TaxRate>) getContainer());
		tablePage.addDoubleClickListener(((ListAndEditWizardDialog<TaxRate>) getContainer()));
		addPage(tablePage);
		editPage = new TaxRateEditWizardPage("EditPage", "Mehrwertsteuersatzarten", descriptor);
		addPage(editPage);
	}
}
