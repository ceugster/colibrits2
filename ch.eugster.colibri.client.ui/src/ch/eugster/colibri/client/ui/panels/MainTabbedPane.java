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
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

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

	private State state;

	private ClientView clientView;

	private DisplayService displayService;
	
	private boolean failOver;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;
	
	public MainTabbedPane(final ClientView clientView)
	{
		this.state = State.STARTING;

		this.clientView = clientView;
		this.addChangeListener(this.clientView);

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

		final SalespointCustomerDisplaySettings customerDisplaySettings = this.getSalespoint().getCustomerDisplaySettings();
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

		final Profile profile = this.getSalespoint().getProfile();

		this.setFont(this.getFont().deriveFont(profile.getTabbedPaneFontStyle(), profile.getTabbedPaneFontSize()));
		this.fgSelected = new java.awt.Color(profile.getTabbedPaneFgSelected());
		this.fg = new java.awt.Color(profile.getTabbedPaneFg());
		this.bg = new java.awt.Color(profile.getTabbedPaneBg());

//		final StatusPanel statusPanel = new StatusPanel(this.getSalespoint().getProfile());
//		this.add("Status", statusPanel);

		final LoginPanel loginPanel = new LoginPanel(this.getSalespoint().getProfile());
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
		topics.add(Topic.SCHEDULED.topic());
		topics.add(Topic.PROVIDER_QUERY.topic());
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, this, properties);

		this.state = State.RUNNING;

	}
	
	@Override
	public void handleEvent(Event event) 
	{
		this.failOver = event.getProperty(EventConstants.EXCEPTION) != null;
	}

	public boolean isFailOver()
	{
		return this.failOver;
	}
	
	public void setFailOver(boolean failOver)
	{
		this.failOver = failOver;
	}

	public boolean settlementRequired()
	{
		if (!this.getSalespoint().isForceSettlement())
		{
			return false;
		}
		if (this.getSalespoint().getSettlement() == null)
		{
			updateSettlementTimestamp();
			return false;
		}
		if (this.getSalespoint().getSettlement().getSettled() != null)
		{
			updateSettlementTimestamp();
			return false;
		}
		
		PersistenceService service = persistenceServiceTracker.getService();
		if (service != null)
		{
			return mustSettle(service);
		}
		return false;
	}

	private boolean mustSettle(PersistenceService persistenceService)
	{
		Salespoint salespoint = this.clientView.getSalespoint();
		ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
		if (query.countSavedAndReversedBySettlement(salespoint.getSettlement()) > 0L)
		{
			if (settlementIsBeforeToday(salespoint.getSettlement()))
			{
				return true;
			}
		}
		else
		{
			updateSettlementTimestamp();
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

	private void updateSettlementTimestamp()
	{
		try
		{
			PersistenceService service = persistenceServiceTracker.getService();
			if (getSalespoint().getSettlement() == null || getSalespoint().getSettlement().getSettled() != null)
			{
				getSalespoint().setSettlement(Settlement.newInstance(getSalespoint()));
			}
			else
			{
				getSalespoint().getSettlement().setTimestamp(GregorianCalendar.getInstance(Locale.getDefault()));
			}
			clientView.updateSalespoint((Salespoint) service.getCacheService().merge(getSalespoint()));
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

	public Salespoint getSalespoint()
	{
		return this.clientView.getSalespoint();
	}

	public CommonSettings getSetting()
	{
		return this.clientView.getSalespoint().getCommonSettings();
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
		StringBuilder builder = new StringBuilder(Version.getTitle() + " v" + getVersion() + " :: " + this.getSalespoint().getName());
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
		final String msg = failOver ? "Die Verbindung zu " + providerName + " wurde unterbrochen.\nSie können weiterarbeiten, müssen aber gegebenenfalls vermehrt Eingaben von Hand machen."
				: "Die Verbindung zu " + providerName + " konnte wiederhergestellt werden.";
		final Frame frame = Activator.getDefault().getFrame();
		final Profile profile = this.getCurrentPanel().getProfile();
		MessageDialog.showInformation(frame, profile, providerName, msg, MessageDialog.TYPE_WARN);
	}

//	private void sendEvent(String topics)
//	{
//		ServiceTracker<EventAdmin, EventAdmin> tracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(), EventAdmin.class, null);
//		try
//		{
//			tracker.open();
//			final EventAdmin eventAdmin = (EventAdmin) tracker.getService();
//			if (eventAdmin != null)
//			{
//				eventAdmin.sendEvent(this.getEvent(tracker.getServiceReference(), topics));
//			}
//		}
//		finally
//		{
//			tracker.close();
//		}
//	}
//
//	private Event getEvent(ServiceReference<EventAdmin> reference, final String topics)
//	{
//		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
//		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
//		properties.put(EventConstants.BUNDLE_ID,
//				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
//		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
//		properties.put(EventConstants.SERVICE, reference);
//		properties.put(EventConstants.SERVICE_ID, reference.getProperty("service.id"));
//		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
//		return new Event(topics, properties);
//	}

	public enum State
	{
		STARTING, RUNNING, SHUTDOWN;
	}

}
