package ch.eugster.colibri.admin.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class ListAndEditWizard<T extends AbstractEntity> extends Wizard
{
	@Override
	public abstract void addPages();

	@SuppressWarnings("unchecked")
	public IWizardPage getCurrentPage()
	{
		return ((ListAndEditWizardDialog<T>) getContainer()).getCurrentPage();
	}

	public abstract EditWizardPage<T> getEditWizardPage(T entity);

	public abstract ItemsWizardPage<T> getItemsWizardPage();

	@Override
	public boolean needsPreviousAndNextButtons()
	{
		return false;
	}

	@Override
	public boolean performFinish()
	{
		return true;
	}

	@SuppressWarnings("unchecked")
	public void showPage(final IWizardPage page)
	{
		((ListAndEditWizardDialog<T>) getContainer()).showPage(page);
	}

}