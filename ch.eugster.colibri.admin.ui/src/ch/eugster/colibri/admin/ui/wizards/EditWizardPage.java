package ch.eugster.colibri.admin.ui.wizards;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import ch.eugster.colibri.persistence.model.AbstractEntity;

public abstract class EditWizardPage<T extends AbstractEntity> extends WizardPage implements ModifyListener, SelectionListener,
		ISelectionChangedListener
{

	protected T entity;

	protected boolean dirty;

	public EditWizardPage(final String name, final String title, final ImageDescriptor image)
	{
		super(name, title, image);
	}

	public void createControl(final Composite parent)
	{
		setControl(this.doCreateControl(parent));
	}

	/**
	 * Create the Controls to manipulate the items data
	 * 
	 * @param parent
	 * @return the composite
	 */
	public abstract Composite doCreateControl(Composite parent);

	public T getEntity()
	{
		if (this.dirty)
		{
			this.getFieldValues();
		}
		return this.entity;
	}

	public void modifyText(final ModifyEvent event)
	{
		this.dirty = true;
		this.checkPage();
	}

	public void selectionChanged(final SelectionChangedEvent event)
	{
		this.dirty = true;
		this.checkPage();
	}

	/**
	 * Set the properties of the concrete Object
	 * 
	 * @param T
	 */
	public void setEntity(final T entity)
	{
		this.entity = entity;
		this.setFieldValues();
	}

	public void widgetDefaultSelected(final SelectionEvent event)
	{
		this.widgetSelected(event);
	}

	public void widgetSelected(final SelectionEvent event)
	{
		this.dirty = true;
		this.checkPage();

	}

	/**
	 * Checks after every event if the data are completed and sets the
	 * errorMessage, if not
	 */
	protected abstract boolean checkInput();

	protected void checkPage()
	{
		if (this.checkInput())
		{
			setErrorMessage(null);
			setPageComplete(true);
		}
		else
		{
			setPageComplete(false);
		}
	}

	/**
	 * Update the properties of the concrete object
	 * 
	 * @param object
	 */
	protected abstract void getFieldValues();

	/**
	 * @return a new and initialized concrete subclass of AbstractObject
	 */
	protected abstract T getNewEntity();

	/**
	 * Set the field values from the concrete object
	 * 
	 * @param object
	 */
	protected abstract void setFieldValues();
}
