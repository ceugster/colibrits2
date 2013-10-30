/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.currency.wizards;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.currency.Activator;
import ch.eugster.colibri.admin.ui.wizards.AbstractEntityWizard;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ReferenceCurrencyWizard extends AbstractEntityWizard
{
	private CommonSettings settings;

	private ReferenceCurrencyWizardPage referenceCurrencyWizardPage;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ReferenceCurrencyWizard(final CommonSettings settings)
	{
		super();
		this.settings = settings;

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void addPages()
	{
		this.referenceCurrencyWizardPage = new ReferenceCurrencyWizardPage(this);
		this.addPage(this.referenceCurrencyWizardPage);
	}

	@Override
	public boolean canFinish()
	{
		return this.referenceCurrencyWizardPage.isPageComplete();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

	public CommonSettings getSettings()
	{
		return this.settings;
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

		this.referenceCurrencyWizardPage.update();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			try 
			{
				this.settings = (CommonSettings) persistenceService.getServerService().merge(this.settings);
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				IStatus status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), e.getLocalizedMessage(), e);
				ErrorDialog.openError(this.getShell(), "Fehler", this.settings.getReferenceCurrency().getName() + " konnte nicht als Referenzwährung gespeichert werden.", status);
			}
		}
		return result;
	}
}
