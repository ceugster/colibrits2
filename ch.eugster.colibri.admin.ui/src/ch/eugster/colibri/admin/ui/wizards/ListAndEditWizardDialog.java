package ch.eugster.colibri.admin.ui.wizards;

import java.util.Iterator;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import ch.eugster.colibri.admin.ui.Activator;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ListAndEditWizardDialog<T extends AbstractEntity> extends WizardDialog implements ISelectionChangedListener, IDoubleClickListener
{
	public ListAndEditWizardDialog(final Shell shell, final ListAndEditWizard<T> wizard)
	{
		super(shell, wizard);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void buttonPressed(final int buttonId)
	{
		if (this.getCurrentPage() instanceof ItemsWizardPage<?>)
		{
			if (buttonId == IDialogConstants.BACK_ID)
			{
				if (new MessageDialog(this.getShell(), "Eintrag entfernen", null, "Soll der gewählte Listeneintrag entfernt werden?",
						MessageDialog.QUESTION, new String[] { "Ja", "Nein" }, 0).open() == 0)
				{
					final PersistenceService persistenceService = Activator.getDefault().getPersistenceService();
					if (persistenceService != null)
					{
						final ItemsWizardPage<T> itemsPage = ((ListAndEditWizard) this.getWizard()).getItemsWizardPage();
						final StructuredSelection ssel = (StructuredSelection) itemsPage.getSelection();
						final Iterator<T> iterator = ssel.iterator();
						while (iterator.hasNext())
						{
							final T entity = iterator.next();
							try
							{
								persistenceService.getServerService().delete(entity);
							} 
							catch (Exception e) 
							{
								e.printStackTrace();
								IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
								ErrorDialog.openError(this.getShell(), "Fehler", "Die Änderungen konnten nicht gespeichert werden.", status);
							}
						}
					}
				}
			}
			else if (buttonId == IDialogConstants.NEXT_ID)
			{
				this.addNewElement();
			}
			else if (buttonId == IDialogConstants.FINISH_ID)
			{
				this.editElement();
			}
		}
		else if (this.getCurrentPage() instanceof EditWizardPage)
		{
			this.saveElement();
		}
	}

	@Override
	public void cancelPressed()
	{
		if (this.getCurrentPage() instanceof EditWizardPage<?>)
		{
			this.showPage(this.getWizard().getPage("TablePage"));
			this.getButton(IDialogConstants.BACK_ID).setVisible(true);
			this.getButton(IDialogConstants.NEXT_ID).setVisible(true);
			this.getButton(IDialogConstants.FINISH_ID).setText("Bearbeiten");
			this.getButton(IDialogConstants.CANCEL_ID).setText("Schliessen");
		}
		else if (this.getCurrentPage() instanceof ItemsWizardPage<?>)
		{
			this.close();
		}
	}

	@Override
	public void createButtonsForButtonBar(final Composite parent)
	{
		this.createButton(parent, IDialogConstants.BACK_ID, "Entfernen", false);
		this.getButton(IDialogConstants.BACK_ID).setEnabled(false);
		this.createButton(parent, IDialogConstants.NEXT_ID, "Neu", false);
		super.createButtonsForButtonBar(parent);
		this.getButton(IDialogConstants.FINISH_ID).setText("Bearbeiten");
		this.getButton(IDialogConstants.CANCEL_ID).setText("Schliessen");
	}

	public void doubleClick(final DoubleClickEvent event)
	{
		if (this.getCurrentPage() instanceof ItemsWizardPage<?>)
		{
			this.editElement();
		}
	}

	@Override
	public void finishPressed()
	{
		if (this.getCurrentPage() instanceof EditWizardPage<?>)
		{
			this.saveElement();
		}
		else if (this.getCurrentPage() instanceof ItemsWizardPage)
		{
			this.editElement();
		}
	}

	@SuppressWarnings("unchecked")
	public void selectionChanged(final SelectionChangedEvent event)
	{
		final boolean deleteable = ((ListAndEditWizard<T>) this.getWizard()).getItemsWizardPage().canDelete();
		this.getButton(IDialogConstants.BACK_ID).setEnabled(!event.getSelection().isEmpty() && deleteable);
		this.getButton(IDialogConstants.FINISH_ID).setEnabled(!event.getSelection().isEmpty());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addNewElement()
	{
		final ItemsWizardPage<T> itemsPage = ((ListAndEditWizard) this.getWizard()).getItemsWizardPage();
		final EditWizardPage<T> editPage = ((ListAndEditWizard) this.getWizard()).getEditWizardPage(itemsPage.getNewEntity());
		this.showPage(editPage);
		if (!editPage.getControl().isFocusControl())
		{
			editPage.getControl().setFocus();
		}
		itemsPage.setSelection(null);
		this.getButton(IDialogConstants.BACK_ID).setVisible(false);
		this.getButton(IDialogConstants.NEXT_ID).setVisible(false);
		this.getButton(IDialogConstants.FINISH_ID).setText("Speichern");
		this.getButton(IDialogConstants.CANCEL_ID).setText("Abbrechen");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void editElement()
	{
		final ItemsWizardPage<T> itemsPage = ((ListAndEditWizard) this.getWizard()).getItemsWizardPage();

		T entity = null;
		if (itemsPage.getSelection() instanceof IStructuredSelection)
		{
			final StructuredSelection sel = (StructuredSelection) itemsPage.getSelection();
			entity = (T) sel.getFirstElement();
		}
		else
		{
			entity = (T) itemsPage.getSelection();
		}

		final EditWizardPage editPage = ((ListAndEditWizard) this.getWizard()).getEditWizardPage(entity);
		editPage.setEntity(entity);
		this.showPage(editPage);
		if (!editPage.getControl().isFocusControl())
		{
			editPage.getControl().setFocus();
		}
		this.getButton(IDialogConstants.BACK_ID).setVisible(false);
		this.getButton(IDialogConstants.NEXT_ID).setVisible(false);
		this.getButton(IDialogConstants.FINISH_ID).setText("Speichern");
		this.getButton(IDialogConstants.CANCEL_ID).setText("Abbrechen");
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void saveElement()
	{
		final PersistenceService persistenceService = Activator.getDefault().getPersistenceService();
		if (persistenceService != null)
		{
			final ItemsWizardPage<T> itemsPage = ((ListAndEditWizard) this.getWizard()).getItemsWizardPage();
			final EditWizardPage<T> editPage = (EditWizardPage) this.getCurrentPage();
			final T entity = editPage.getEntity();
			try
			{
				persistenceService.getServerService().merge(entity);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
				ErrorDialog.openError(this.getShell(), "Fehler", "Die Änderungen konnten nicht gespeichert werden.", status);
			}
			this.showPage(itemsPage);
			this.getButton(IDialogConstants.BACK_ID).setVisible(true);
			this.getButton(IDialogConstants.NEXT_ID).setVisible(true);
			this.getButton(IDialogConstants.FINISH_ID).setText("Bearbeiten");
			this.getButton(IDialogConstants.CANCEL_ID).setText("Schliessen");
		}
	}
}
