/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import org.eclipse.core.runtime.IStatus;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderQuery;

public class SelectCustomerAction extends ConfigurableAction implements DisposeListener
{
	private static final long serialVersionUID = 0l;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public SelectCustomerAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		userPanel.addDisposeListener(this);
		
		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();
	}

	public ProductGroup getProductGroup()
	{
		ProductGroup productGroup = null;
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			productGroup = (ProductGroup) persistenceService.getCacheService().find(ProductGroup.class, this.key.getParentId());
		}
		return productGroup;
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
//		final UIJob uiJob = new UIJob("send message")
//		{
//			@Override
//			public IStatus runInUIThread(final IProgressMonitor monitor)
//			{
//				IStatus status = Status.CANCEL_STATUS;
				final ServiceTracker<ProviderQuery, ProviderQuery> ProviderQueryTracker = new ServiceTracker<ProviderQuery, ProviderQuery>(Activator.getDefault().getBundle().getBundleContext(),
						ProviderQuery.class, null);
				ProviderQueryTracker.open();
				try
				{
					final ProviderQuery providerQuery = (ProviderQuery) ProviderQueryTracker.getService();
					if (providerQuery != null)
					{
						IStatus status = providerQuery.selectCustomer(userPanel.getPositionWrapper().getPosition(), getProductGroup());
						if (status.getSeverity() == IStatus.OK)
						{
							userPanel.getPositionListPanel().getModel().actionPerformed(event);
						}
//						else if (status.getSeverity() != IStatus.CANCEL)
//						{
//							MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), userPanel.getProfile(), "Lesen der Kundendaten",
//									status.getMessage(), MessageDialog.BUTTON_OK);
//						}
					}
				}
				finally
				{
					ProviderQueryTracker.close();
				}
//				return status;
//			}
//		};
//		uiJob.setUser(true);
//		uiJob.schedule();
	}

	@Override
	public void dispose()
	{
		this.persistenceServiceTracker.close();
	}

}
