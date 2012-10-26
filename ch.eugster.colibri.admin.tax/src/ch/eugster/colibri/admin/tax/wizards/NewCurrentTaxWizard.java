/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.tax.wizards;

import org.eclipse.jface.wizard.IWizardPage;

import ch.eugster.colibri.admin.ui.wizards.AbstractEntityWizard;
import ch.eugster.colibri.persistence.model.TaxRate;
import ch.eugster.colibri.persistence.model.TaxType;

public class NewCurrentTaxWizard extends AbstractEntityWizard
{
	private final TaxType[] taxTypes;

	private final TaxRate[] taxRates;

	public NewCurrentTaxWizard(final TaxType[] taxTypes, TaxRate[] taxRates)
	{
		super();
		this.taxTypes = taxTypes;
		this.taxRates = taxRates;
	}

	@Override
	public void addPages()
	{
		addPage(new NewCurrentTaxWizardPage("new.current.tax.wizard.page", "Mehrwertsteuersätze aktualisieren",
				this.taxTypes, this.taxRates));
	}

	@Override
	public boolean canFinish()
	{
		for (final IWizardPage page : getPages())
		{
			if (!page.isPageComplete())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public boolean needsPreviousAndNextButtons()
	{
		return false;
	}

	@Override
	public boolean performFinish()
	{
		final boolean result = true;

		for (IWizardPage page : this.getPages())
		{
			if (page instanceof NewCurrentTaxWizardPage)
			{
				NewCurrentTaxWizardPage newTaxPage = (NewCurrentTaxWizardPage) page;
				newTaxPage.store();
			}
		}
		return result;
	}
}
