/*
 * Created on 02.01.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.admin.salespoint.wizards;

import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.salespoint.Activator;
import ch.eugster.colibri.admin.ui.wizards.AbstractEntityWizard;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class StockWizard extends AbstractEntityWizard
{
	private Stock stock;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private StockWizardPage stockWizardPage;

	public StockWizard(final Stock stock)
	{
		super();
		this.stock = stock;

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	@Override
	public void addPages()
	{
		this.stockWizardPage = new StockWizardPage(this);
		this.addPage(this.stockWizardPage);
	}

	@Override
	public boolean canFinish()
	{
		return this.stockWizardPage.isPageComplete();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
		super.dispose();
	}

	public Stock getStock()
	{
		return this.stock;
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

		this.stockWizardPage.update();
		if (this.stock.getId() == null)
		{
			this.stock.getSalespoint().addStock(this.stock);
		}

		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			persistenceService.getServerService().merge(this.stock.getSalespoint());
		}
		return result;
	}
}
