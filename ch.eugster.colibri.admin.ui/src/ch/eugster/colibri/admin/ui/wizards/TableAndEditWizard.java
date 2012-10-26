package ch.eugster.colibri.admin.ui.wizards;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class TableAndEditWizard<T extends AbstractEntity> extends ListAndEditWizard<T>
{

	protected TableWizardPage<T> tablePage;

	protected EditWizardPage<T> editPage;

	@Override
	public boolean canFinish()
	{
		if (getContainer().getCurrentPage().equals(this.tablePage))
		{
			return !this.tablePage.getViewer().getSelection().isEmpty();
		}
		else if (getContainer().getCurrentPage().equals(this.editPage))
		{
			return this.editPage.isPageComplete();
		}
		else
		{
			return true;
		}
	}

	@Override
	public EditWizardPage<T> getEditWizardPage(final T entity)
	{
		this.editPage.setEntity(entity);
		return this.editPage;
	}

	@Override
	public ItemsWizardPage<T> getItemsWizardPage()
	{
		return this.tablePage;
	}

	@Override
	public boolean performFinish()
	{
		return true;
	}

}
