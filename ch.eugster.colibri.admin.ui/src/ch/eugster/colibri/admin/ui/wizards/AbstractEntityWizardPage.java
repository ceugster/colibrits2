/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

public abstract class AbstractEntityWizardPage extends WizardPage implements Listener
{
	public AbstractEntityWizardPage(String name, String title)
	{
		super(name);
		this.setTitle(title);
	}
	
	@Override
	public boolean isPageComplete()
	{
		return this.validatePage();
	}
	
	protected abstract boolean validatePage();
	
	/**
	 * refresh fields from entity
	 */
	protected abstract void refresh();
	
	/**
	 * update values in entity
	 */
	public abstract void update();
	
	public void handleEvent(Event event)
	{
		this.setPageComplete(this.validatePage());
	}
	
}
