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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.IStatus;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
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

public class UserSalesAction extends ConfigurableAction
{
	private static final long serialVersionUID = 0l;

	public UserSalesAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);
		registerHandler();
	}

	private void registerHandler()
	{
		final Collection<String> t = new ArrayList<String>();
		t.add(Topic.SCHEDULED_TRANSFER.topic());
		final String[] topics = t.toArray(new String[t.size()]);

		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.handlerRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);
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
				final PositionQuery positionQuery = (PositionQuery) service.getServerService().getQuery(Position.class);
				final double sales = positionQuery.sumCurrent(ProductGroupType.SALES_RELATED, this.userPanel.getUser());

				final Frame frame = Activator.getDefault().getFrame();
				final Profile profile = this.userPanel.getLocalSalespoint().getProfile();
				MessageDialog dialog = null;
				if (isConnected(service))
				{
					final NumberFormat formatter = NumberFormat.getCurrencyInstance();
					formatter.setCurrency(this.userPanel.getLocalSalespoint().getCommonSettings().getReferenceCurrency().getCurrency());

					dialog = new MessageDialog(frame, profile, "Umsatz", new int[] { MessageDialog.BUTTON_OK }, 0, this.userPanel.getMainTabbedPane().isFailOver());
					dialog.setMessage("Benutzerumsatz:   " + formatter.format(sales));
				}
				else
				{
					dialog = new MessageDialog(frame, profile, "Umsatz", new int[] { MessageDialog.BUTTON_OK }, 0, this.userPanel.getMainTabbedPane().isFailOver());
					dialog.setMessage("Zur Zeit kann der Umsatz nicht abgefragt werden.\nDie Verbindung zum Datenbankserver ist unterbrochen.");
				}
				dialog.pack();
				dialog.centerInScreen();
				dialog.setVisible(true);
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
		if (event.getTopic().equals(Topic.SCHEDULED_TRANSFER.topic()))
		{
			Object property = event.getProperty("status");
			if (property instanceof IStatus)
			{
				IStatus status = (IStatus) property;
				if (status.getSeverity() == IStatus.ERROR)
				{
					this.setEnabled(false);
				}
				else
				{
					this.setEnabled(getState(new StateChangeEvent(this.userPanel.getCurrentState(), this.userPanel.getCurrentState())) && status.getSeverity() == IStatus.OK);
				}
			}
		}
	}
}
