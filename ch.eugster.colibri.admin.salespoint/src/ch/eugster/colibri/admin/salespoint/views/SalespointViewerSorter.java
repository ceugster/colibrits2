package ch.eugster.colibri.admin.salespoint.views;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.admin.salespoint.Activator;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Currency;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Stock;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class SalespointViewerSorter extends ViewerSorter
{
	private String hostname;

	public SalespointViewerSorter()
	{
		final ServiceTracker<PersistenceService, PersistenceService> tracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		tracker.open();

		final PersistenceService persistenceService = (PersistenceService) tracker.getService();
		if (persistenceService != null)
		{
			final CommonSettingsQuery query = (CommonSettingsQuery) persistenceService.getServerService().getQuery(CommonSettings.class);
			final CommonSettings settings = query.findDefault();
			if (settings != null)
			{
				this.hostname = settings.getHostnameResolver().getHostname();
			}
		}
	}

	@Override
	public int compare(final Viewer viewer, final Object object1, final Object object2)
	{
		if ((object1 instanceof Salespoint) && (object2 instanceof Salespoint))
		{
			final Salespoint sp1 = (Salespoint) object1;
			final Salespoint sp2 = (Salespoint) object2;

			if (sp1.getHost().equals(this.hostname))
			{
				return -1;
			}
			else if (sp2.getHost().equals(this.hostname))
			{
				return 1;
			}
			else
			{
				return sp1.getName().compareTo(sp2.getName());
			}
		}
		if ((object1 instanceof Stock) && (object2 instanceof Stock))
		{
			final Stock st1 = (Stock) object1;
			final Stock st2 = (Stock) object2;

			final Currency currency = st1.getSalespoint().getCommonSettings().getReferenceCurrency();
			if (currency != null)
			{
				if (st1.getPaymentType().getCurrency().equals(currency))
				{
					return -1;
				}
				else if (st2.getPaymentType().getCurrency().equals(currency))
				{
					return 1;
				}
			}
		}
		return 0;
	}
}
