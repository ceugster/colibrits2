/*
 * Created on 27.03.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.actions;

import java.awt.event.ActionEvent;

import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.events.StateChangeEvent;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.persistence.model.Key;
import ch.eugster.colibri.persistence.model.key.FunctionType;
import ch.eugster.colibri.persistence.service.PersistenceService;

public class ShowCoinCounterPanelAction extends ConfigurableAction implements EventHandler
{
	private static final long serialVersionUID = 0L;

	private final ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	public ShowCoinCounterPanelAction(final UserPanel userPanel, final Key key)
	{
		super(userPanel, key);

		persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		persistenceServiceTracker.open();
	}

	@Override
	public void actionPerformed(final ActionEvent e)
	{
		userPanel.fireStateChange(new StateChangeEvent(userPanel.getCurrentState(), UserPanel.State.COIN_COUNTER));
	}

	protected boolean getState(final StateChangeEvent event)
	{
		boolean enabled = super.getState(event);
		if (event.getNewState().equals(UserPanel.State.MUST_SETTLE))
		{
			enabled = userPanel.getUser().getRole().getPropertyValue(FunctionType.FUNCTION_SHOW_COIN_COUNTER_PANEL.key());
		}
		return enabled;
	}

	@Override
	protected void finalize() throws Throwable
	{
		persistenceServiceTracker.close();
		super.finalize();
	}

}
