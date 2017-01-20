/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.product.ProductGroupType;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class TotalSalesAction extends ConfigurableAction
{
	private static final long serialVersionUID = 0l;

	private EventAdmin eventAdmin;
	
	public TotalSalesAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);

		ServiceTracker<EventAdmin, EventAdmin> tracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
		tracker.open();
		try
		{
			this.eventAdmin = tracker.getService();
		}
		finally
		{
			tracker.close();
		}
	}

	private boolean isConnected(PersistenceService service)
	{
		return service != null && (service.getServerService().isLocal() || service.getServerService().isConnected());
	}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		final ServiceTracker<PersistenceService, PersistenceService> serviceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		serviceTracker.open();
		try
		{
			final PersistenceService service = (PersistenceService) serviceTracker.getService();
			if (isConnected(service))
			{
				MessageDialog dialog = null;
				final Frame frame = Activator.getDefault().getFrame();
				final Profile profile = this.userPanel.getLocalSalespoint().getProfile();
				try
				{
					final PositionQuery positionQuery = (PositionQuery) service.getServerService().getQuery(Position.class);
					final double sales = positionQuery.sumCurrent(ProductGroupType.SALES_RELATED, 5);
				
					Map<String, Object> properties = new HashMap<String, Object>();
					properties.put("provider", "transfer");
					properties.put("failover", Boolean.FALSE);
					properties.put("status", Status.OK_STATUS);
					eventAdmin.sendEvent(new Event(Topic.PROVIDER_QUERY.topic(), properties));

					final NumberFormat formatter = NumberFormat.getCurrencyInstance();
					formatter.setCurrency(this.userPanel.getLocalSalespoint().getCommonSettings().getReferenceCurrency().getCurrency());

					dialog = new MessageDialog(frame, profile, "Umsatz", new int[] { MessageDialog.BUTTON_OK }, 0, this.userPanel.getMainTabbedPane().isFailOver());
					dialog.setMessage("Gesamtumsatz:   " + formatter.format(sales));
					dialog.pack();
					dialog.centerInScreen();
					dialog.setVisible(true);
				}
				catch (Exception e)
				{
					Map<String, Object> properties = new HashMap<String, Object>();
					properties.put("provider", "transfer");
					properties.put("failover", Boolean.TRUE);
					properties.put("status", new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), "Umsatz kann nicht abgefragt werden."));
					eventAdmin.sendEvent(new Event(Topic.PROVIDER_QUERY.topic(), properties));
					
					dialog = new MessageDialog(frame, profile, "Umsatz", new int[] { MessageDialog.BUTTON_OK }, 0, true);
					dialog.setMessage("Zur Zeit kann der Umsatz nicht abgefragt werden.\nDie Verbindung zum Datenbankserver ist unterbrochen.");
					dialog.pack();
					dialog.centerInScreen();
					dialog.setVisible(true);
				}
			}
		}
		finally
		{
			serviceTracker.close();
		}
	}

	protected boolean getState(final StateChangeEvent event)
	{
		boolean enabled = super.getState(event);
		if (enabled)
		{
			final ServiceTracker<PersistenceService, PersistenceService> serviceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
					PersistenceService.class, null);
			serviceTracker.open();
			try
			{
				final PersistenceService service = (PersistenceService) serviceTracker.getService();
				enabled = isConnected(service);
			}
			finally
			{
				serviceTracker.close();
			}
		}
		return enabled;
	}

	@Override
	public void handleEvent(Event event) 
	{
		if (event.getTopic().equals(Topic.FAIL_OVER.topic()))
		{
			String provider = (String) event.getProperty("provider");
			if (provider != null && provider.equals("transfer"))
			{
				Boolean result = (Boolean) event.getProperty("failover");
				this.setEnabled(result == null || !result.booleanValue());
			}
		}
	}
}
