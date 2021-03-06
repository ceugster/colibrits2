/*
 * Created on 16.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.KeyboardFocusManager;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.ReceiptChangeMediator;
import ch.eugster.colibri.client.ui.events.ShutdownEvent;
import ch.eugster.colibri.client.ui.events.ShutdownListener;
import ch.eugster.colibri.client.ui.panels.login.ILoginListener;
import ch.eugster.colibri.client.ui.panels.login.LoginEvent;
import ch.eugster.colibri.client.ui.panels.login.LoginPanel;
import ch.eugster.colibri.client.ui.panels.user.UserPanel;
import ch.eugster.colibri.client.ui.views.ClientView;
import ch.eugster.colibri.display.service.DisplayService;
import ch.eugster.colibri.persistence.events.EntityListener;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.AbstractEntity;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.Profile;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.SalespointCustomerDisplaySettings;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.User;
import ch.eugster.colibri.persistence.model.Version;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.replication.service.ReplicationService;
import ch.eugster.colibri.persistence.service.ConnectionService;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.persistence.transfer.services.TransferAgent;
import ch.eugster.colibri.provider.service.ProviderUpdater;

public class MainTabbedPane extends JTabbedPane implements ILoginListener, ShutdownListener, ChangeListener,
		EntityListener, EventHandler
{
	public static final long serialVersionUID = 0l;

	private final Color fgSelected;

	private final Color fg;

	private final Color bg;

	private String title;

	private String version;
	
	private ServiceTracker<DisplayService, DisplayService> displayTracker;

	private ServiceTracker<TransferAgent, TransferAgent> transferServiceTracker;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderUpdater, ProviderUpdater> providerUpdaterTracker;

	private ServiceTracker<EventAdmin, EventAdmin> eventAdminTracker;

	private State state;

	private ClientView clientView;

	private DisplayService displayService;
	
	private Map<String, Boolean> failOvers = new HashMap<String, Boolean>();
	
	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;
	
	public MainTabbedPane(final ClientView clientView)
	{
		this.state = State.STARTING;

		this.clientView = clientView;
		this.addChangeListener(this.clientView);

		this.eventAdminTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
		this.eventAdminTracker.open();

		this.transferServiceTracker = new ServiceTracker<TransferAgent, TransferAgent>(Activator.getDefault().getBundle().getBundleContext(),
				TransferAgent.class, null);
		this.transferServiceTracker.open();

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null)
		{
			@Override
			public PersistenceService addingService(final ServiceReference<PersistenceService> reference)
			{
				final PersistenceService service = (PersistenceService) super.addingService(reference);
				return service;
			}
		};
		this.persistenceServiceTracker.open();

		final SalespointCustomerDisplaySettings customerDisplaySettings = this.getLocalSalespoint().getCustomerDisplaySettings();
		if (customerDisplaySettings != null)
		{
			this.displayTracker = new ServiceTracker<DisplayService, DisplayService>(Activator.getDefault().getBundle().getBundleContext(),
					DisplayService.class, null);
			this.displayTracker.open();

			final Object[] services = this.displayTracker.getServices();
			if (services != null)
			{
				for (Object service : services)
				{
					DisplayService displayService = (DisplayService) service;
					if (displayService.getLayoutType(customerDisplaySettings.getComponentName()) != null)
					{
						this.displayService = displayService;
					}
				}
			}
		}
		ServiceTracker<ReplicationService, ReplicationService> tracker = new ServiceTracker<ReplicationService, ReplicationService>(Activator.getDefault().getBundle().getBundleContext(), ReplicationService.class, null);
		tracker.open();
		try
		{
			ReplicationService service = tracker.getService();
			if (service != null)
			{
				if (!service.lastReplicationSucceeded())
				{
					this.failOvers.put("transfer", Boolean.TRUE);
				}
			}
		}
		finally
		{
			tracker.close();
		}

		final Profile profile = this.getLocalSalespoint().getProfile();

		this.setFont(this.getFont().deriveFont(profile.getTabbedPaneFontStyle(), profile.getTabbedPaneFontSize()));
		this.fgSelected = new java.awt.Color(profile.getTabbedPaneFgSelected());
		this.fg = new java.awt.Color(profile.getTabbedPaneFg());
		this.bg = new java.awt.Color(profile.getTabbedPaneBg());

//		final StatusPanel statusPanel = new StatusPanel(this.getLocalSalespoint().getProfile());
//		this.add("Status", statusPanel);

		final LoginPanel loginPanel = new LoginPanel(this.getLocalSalespoint().getProfile(), isFailOver());
		loginPanel.addLoginListener(this);
		this.addChangeListener(this);
		this.add("Anmelden", loginPanel);

		this.providerUpdaterTracker = new ServiceTracker<ProviderUpdater, ProviderUpdater>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderUpdater.class, null)
		{
			@Override
			public void removedService(final ServiceReference<ProviderUpdater> reference, final ProviderUpdater service)
			{
				if (!MainTabbedPane.this.state.equals(MainTabbedPane.State.SHUTDOWN))
				{
					String name = service.getName();
					MainTabbedPane.this.showProviderInterfaceMessage(true, name);
				}
				super.removedService(reference, service);
			}
		};
		this.providerUpdaterTracker.open();
		this.setSelectedComponent(loginPanel);

		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		List<String> topics = new ArrayList<String>();
		topics.add(Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		topics.add(Topic.SCHEDULED_TRANSFER.topic());
		topics.add(Topic.PROVIDER_QUERY.topic());
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, this, properties);

		this.state = State.RUNNING;

	}
	
	public boolean isFailOver()
	{
		return !this.failOvers.isEmpty();
	}
	
	@Override
	public void handleEvent(Event event) 
	{
		Boolean failOverRelevant = (Boolean) event.getProperty("failover");
		if (failOverRelevant != null)
		{
			boolean update = false;
			String provider = (String) event.getProperty("provider");
			Boolean failOver = this.failOvers.get(provider);
			if (failOverRelevant.booleanValue())
			{
				if (failOver == null || !failOver.booleanValue())
				{
					this.failOvers.put((String)event.getProperty("provider"), Boolean.TRUE);
					update = true;
				}
			}
			else
			{
				if (failOver != null && failOver.booleanValue())
				{
					this.failOvers.remove((String)event.getProperty("provider"));
					update = true;
				}
			}
			if (update)
			{
				EventAdmin eventAdmin = eventAdminTracker.getService();
				if (eventAdmin != null)
				{
					Map<String, Object> properties = new HashMap<String, Object>();
					String[] names = event.getPropertyNames();
					for (String name : names)
					{
						properties.put(name, event.getProperty(name));
					}
					String[] keys = this.failOvers.keySet().toArray(new String[0]);
					for (String key : keys)
					{
						properties.put(key, this.failOvers.get(key));
					}
					properties.put("failover-list", this.failOvers);
					Event evt = new Event(Topic.FAIL_OVER.topic(), properties);
					eventAdmin.sendEvent(evt);
				}
			}
		}
	}

//	public boolean isFailOver()
//	{
//		return this.failOvers.size() > 0;
//	}
	
	public boolean settlementRequired()
	{
		Salespoint serverSalespoint = this.getServerSalespoint();
		if (serverSalespoint != null)
		{
			if (!serverSalespoint.isForceSettlement())
			{
				return false;
			}
			PersistenceService service = persistenceServiceTracker.getService();
			if (service != null && service.getServerService().isConnected())
			{
				if (serverSalespoint.getSettlement() == null)
				{
					updateSettlementTimestamp(service.getServerService());
					return false;
				}
				if (serverSalespoint.getSettlement().getSettled() != null)
				{
					updateSettlementTimestamp(service.getServerService());
					return false;
				}
				return mustSettle(service.getServerService());
			}
		}
		return false;
	}

	private boolean mustSettle(ConnectionService service)
	{
		Salespoint salespoint = this.clientView.getServerSalespoint();
		ReceiptQuery query = (ReceiptQuery) service.getQuery(Receipt.class);
		if (query.countSavedAndReversedBySettlement(salespoint.getSettlement()) > 0L)
		{
			if (settlementIsBeforeToday(salespoint.getSettlement()))
			{
				return true;
			}
			else
			{
				updateSettlementTimestamp(service);
			}
		}
		else
		{
			updateSettlementTimestamp(service);
		}
		return false;
	}
	
	public boolean settlementIsBeforeToday(Settlement settlement)
	{
		Calendar today = GregorianCalendar.getInstance(Locale.getDefault());
		today.set(Calendar.HOUR_OF_DAY, 0);
		today.set(Calendar.MINUTE, 0);
		today.set(Calendar.SECOND, 0);
		today.set(Calendar.MILLISECOND, 0);
		return settlement.getTimestamp().before(today);
	}

	private void updateSettlementTimestamp(ConnectionService service)
	{
		try
		{
			if (getServerSalespoint().getSettlement() == null || getServerSalespoint().getSettlement().getSettled() != null)
			{
				getServerSalespoint().setSettlement(Settlement.newInstance(getServerSalespoint()));
			}
			else
			{
				getServerSalespoint().getSettlement().setTimestamp(GregorianCalendar.getInstance(Locale.getDefault()));
			}
			clientView.updateServerSalespoint((Salespoint) service.merge(getServerSalespoint()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public MainPanel getCurrentPanel()
	{
		return (MainPanel) this.getSelectedComponent();
	}

	public Salespoint getLocalSalespoint()
	{
		return this.clientView.getLocalSalespoint();
	}

	public Salespoint getServerSalespoint()
	{
		return this.clientView.getServerSalespoint();
	}

	public CommonSettings getSetting()
	{
		return this.clientView.getLocalSalespoint().getCommonSettings();
	}

	public void initFocus()
	{
		if (this.getSelectedComponent() instanceof MainPanel)
		{
			((MainPanel) this.getSelectedComponent()).initFocus();
		}
	}

	@Override
	public void login(final LoginEvent event)
	{
		this.selectUserPanel(event.getUser());
	}

	@Override
	public void postDelete(final AbstractEntity entity)
	{
	}

	@Override
	public void postLoad(final AbstractEntity entity)
	{
	}

	@Override
	public void postPersist(final AbstractEntity entity)
	{
	}

	@Override
	public void postRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void postUpdate(final AbstractEntity entity)
	{
	}

	@Override
	public void preDelete(final AbstractEntity entity)
	{
	}
	
	private String getVersion()
	{
		if (this.version == null)
		{
			Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
			for (Bundle bundle : bundles)
			{
				if (bundle.getSymbolicName().equals("ch.eugster.colibri.product"))
				{
					this.version = bundle.getHeaders().get("Bundle-Version");
				}
			}
		}
		return this.version;
	}

	public String prepareTitle()
	{
		StringBuilder builder = new StringBuilder(Version.getTitle() + " v" + getVersion() + " :: " + this.getLocalSalespoint().getName());
		if (this.getSelectedComponent() instanceof TitleProvider)
		{
			builder = builder.append(" :: " + ((TitleProvider) this.getSelectedComponent()).getTitle());
		}
		this.title = builder.toString();
		return this.title;
	}

	@Override
	public void prePersist(final AbstractEntity entity)
	{
	}

	@Override
	public void preRemove(final AbstractEntity entity)
	{
	}

	@Override
	public void preUpdate(final AbstractEntity entity)
	{
	}

	public void removeUserPanel(final UserPanel userPanel)
	{
		if (!userPanel.readyToLogout())
		{
			userPanel.getReceiptWrapper().parkReceipt();
		}
		this.remove(userPanel);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(userPanel);
		this.clientView.getReceiptChangeMediator().dispose();
		this.clientView.propertyChange(new PropertyChangeEvent(userPanel.getReceiptWrapper().getReceipt(),
				"receipt", null, null));
		userPanel.dispose();
		UIJob job = new UIJob("")
		{
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) 
			{
				if (clientView.getSite().getWorkbenchWindow().getShell().getMaximized())
				{
					clientView.getSite().getWorkbenchWindow().getShell().setMaximized(false);
					clientView.getSite().getWorkbenchWindow().getShell().setMaximized(true);
				}
				else if (!clientView.getSite().getWorkbenchWindow().getShell().getMinimized())
				{
					clientView.getSite().getWorkbenchWindow().getShell().setMaximized(true);
					clientView.getSite().getWorkbenchWindow().getShell().setMaximized(false);
				}
				return Status.OK_STATUS;
			}
		};
		job.schedule();
	}

	@Override
	public boolean shutdown(final ShutdownEvent event)
	{
		this.state = State.SHUTDOWN;

		this.eventHandlerServiceRegistration.unregister();
		
		final boolean shutdown = true;
		final Component[] children = this.getComponents();
		for (final Component component : children)
		{
			if (component instanceof UserPanel)
			{
				final UserPanel userPanel = (UserPanel) component;
				if (!userPanel.readyToLogout())
				{
					userPanel.getReceiptWrapper().parkReceipt();
				}
				userPanel.dispose();
			}
		}
		if (this.eventAdminTracker != null)
		{
			this.eventAdminTracker.close();
		}
		if (this.persistenceServiceTracker != null)
		{
			this.persistenceServiceTracker.close();
		}
		if (this.providerUpdaterTracker != null)
		{
			this.providerUpdaterTracker.close();
		}
		if (this.transferServiceTracker != null)
		{
			this.transferServiceTracker.close();
		}
		if (this.displayTracker != null)
		{
			this.displayTracker.close();
		}

		return shutdown;
	}

	@Override
	public void stateChanged(final ChangeEvent e)
	{
		for (int i = 0; i < this.getTabCount(); i++)
		{
			if (this.getSelectedIndex() == i)
			{
				this.setForegroundAt(i, this.fgSelected);

				final MainPanel panel = (MainPanel) this.getSelectedComponent();
				if (panel != null)
				{
					if (this.displayService != null)
					{
						if (panel instanceof UserPanel)
						{
							this.displayService.displayWelcomeMessage(0);
						}
						else
						{
							this.displayService.displaySalespointClosedMessage();
						}
					}
					if (panel instanceof UserPanel)
					{
						final UserPanel userPanel = (UserPanel) panel;
						KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(userPanel);
						this.clientView.setReceiptChangeMediator(new ReceiptChangeMediator(userPanel, this.clientView,
								new String[] { "receipt", "customer", "customerCode" }));
						this.clientView.propertyChange(new PropertyChangeEvent(userPanel.getReceiptWrapper()
								.getReceipt(), "receipt", null, userPanel.getReceiptWrapper().getReceipt()));
					}
					this.printTitle();

				}
				panel.requestFocus();
			}
			else
			{
				this.setForegroundAt(i, this.fg);
				this.setBackgroundAt(i, this.bg);
				final MainPanel panel = (MainPanel) this.getComponent(i);
				if (panel instanceof UserPanel)
				{
					final UserPanel userPanel = (UserPanel) panel;
					KeyboardFocusManager.getCurrentKeyboardFocusManager().removeKeyEventDispatcher(userPanel);
					this.clientView.getReceiptChangeMediator().dispose();
					this.clientView.propertyChange(new PropertyChangeEvent(userPanel.getReceiptWrapper().getReceipt(),
							"receipt", null, null));
				}
			}
		}
	}

	private void printTitle()
	{
		this.prepareTitle();

		final UIJob uiJob = new UIJob("print title")
		{
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor)
			{
				final Display display = MainTabbedPane.this.clientView.getViewSite().getShell().getDisplay();
				if ((display.getActiveShell() != null) && display.getActiveShell().isVisible())
				{
					display.getActiveShell().setText(MainTabbedPane.this.title);
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	private void selectUserPanel(final User user)
	{
		String topics = null;;
		final Component[] children = this.getComponents();
		for (final Component component : children)
		{
			if (component instanceof UserPanel)
			{
				final UserPanel userPanel = (UserPanel) component;
				if (userPanel.getUser().equals(user))
				{
					this.setSelectedComponent(component);
					userPanel.initFocus();
					topics = Topic.USER_LOGGED_IN.topic();
				}
			}
		}
		if (topics == null)
		{
			final UserPanel userPanel = new UserPanel(this, user);
			userPanel.initFocus();

			this.add(user.getUsername(), userPanel);
			this.setSelectedComponent(userPanel);
			topics = Topic.USER_LOGGED_IN.topic();
		}
	}

	private void showProviderInterfaceMessage(final boolean failOver, String providerName)
	{
		final String msg = failOver ? "Die Verbindung zu " + providerName + " wurde unterbrochen.\nSie k�nnen weiterarbeiten, m�ssen aber gegebenenfalls vermehrt Eingaben von Hand machen."
				: "Die Verbindung zu " + providerName + " konnte wiederhergestellt werden.";
		final Frame frame = Activator.getDefault().getFrame();
		final Profile profile = this.getCurrentPanel().getProfile();
		MessageDialog.showInformation(frame, profile, providerName, msg, MessageDialog.TYPE_WARN, this.isFailOver());
	}

	public enum State
	{
		STARTING, RUNNING, SHUTDOWN;
	}

}
