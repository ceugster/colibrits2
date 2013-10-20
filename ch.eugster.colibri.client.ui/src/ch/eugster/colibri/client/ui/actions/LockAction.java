/*
 * Created on 26.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.Hashtable;

import org.eclipse.core.runtime.Status;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.DisposeListener;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.model.key.KeyType;
import ch.eugster.colibri.persistence.model.print.IPrintable;

public class LockAction extends ConfigurableAction implements DisposeListener
{
	public static final long serialVersionUID = 0l;

	private ServiceTracker<EventAdmin, EventAdmin> eventServiceTracker;
	
	public LockAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);

		this.eventServiceTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventServiceTracker.open();
}

	@Override
	public void actionPerformed(final ActionEvent event)
	{
		if (userPanel.getCurrentState().equals(UserPanel.State.LOCKED))
		{
			if (userPanel.getUser().getPosLogin().equals(userPanel.getValueDisplay().getPosLogin()))
			{
				userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), userPanel.getOldState()));
				EventAdmin eventAdmin = eventServiceTracker.getService();
				if (eventAdmin != null)
				{
					eventAdmin.sendEvent(getEvent("ch/eugster/colibri/client/user/added"));
				}
			}
			else
			{
				final String title = "Ungültiges Passwort";
				final String message = "Das eingegebene Passwort ist ungültig.";
				final int messageType = ch.eugster.colibri.client.ui.dialogs.MessageDialog.TYPE_INFORMATION;
				MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), userPanel.getProfile(), title, message, messageType);
			}
		}
		else
		{
			userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.LOCKED));
			EventAdmin eventAdmin = eventServiceTracker.getService();
			if (eventAdmin != null)
			{
				eventAdmin.sendEvent(getEvent("ch/eugster/colibri/client/salespoint/closed"));
			}
		}
	}

	protected boolean getState(final StateChangeEvent event)
	{
		boolean state = super.getState(event); 
		if (event.getNewState().equals(UserPanel.State.LOCKED))
		{
			state = true;
		}
		return event.getNewState().equals(UserPanel.State.MUST_SETTLE) ? false : state;
	}

	private Event getEvent(final String topics)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.SERVICE, this.eventServiceTracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID,
				this.eventServiceTracker.getServiceReference().getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		return new Event(topics, properties);
	}
	
	@Override
	public void dispose()
	{
		this.eventServiceTracker.close();
	}
}
