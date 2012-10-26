/*
 * Created on 17.02.2009
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ch.eugster.colibri.client.ui.views;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Hashtable;

import javax.swing.JRootPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
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
import ch.eugster.colibri.client.ui.panels.MainTabbedPane;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.service.ProviderIdService;
import ch.eugster.colibri.provider.service.ProviderInterface;

public class ClientView extends ViewPart implements IWorkbenchListener, PropertyChangeListener, ChangeListener,
		EventHandler
{
	public static final String ID = "ch.eugster.colibri.client.view";

	private static ClientView instance;

	private MainTabbedPane mainTabbedPane;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderInterface, ProviderInterface> providerServiceTracker;

	private ServiceTracker<EventAdmin, EventAdmin> eventServiceTracker;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

	private StatusLineContributionItem customerInformation;

	private StatusLineContributionItem providerInformation;

	private StatusLineContributionItem transferInformation;

	private ReceiptChangeMediator receiptChangeMediator;

	private final Collection<ShutdownListener> shutdownListeners = new ArrayList<ShutdownListener>();

	public boolean addShutdownListener(final ShutdownListener listener)
	{
		return this.shutdownListeners.add(listener);
	}

	@Override
	public void createPartControl(final Composite parent)
	{
		StringBuilder errors = new StringBuilder();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		String msg = "Die Verbindung zur lokalen Datenbank konnte nicht hergestellt werden.";
		errors = errors.append(persistenceService == null ? msg : "");
		if (persistenceService != null)
		{
			final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getCacheService().getQuery(
					Salespoint.class);
			Salespoint salespoint = salespointQuery.getCurrentSalespoint();
			msg = "Für diese Arbeitsstation ist noch keine Kasse registriert.";
			errors = errors.append(salespoint == null ? msg : "");
			if (salespoint != null)
			{
				msg = "Für diese Kasse ist noch kein Profil definiert.";
				errors = errors.append(salespoint.getProfile() == null ? msg : "");
				if (salespoint.getProfile() != null)
				{
					errors = errors.append(this.checkReferenceCurrency(salespoint));
					errors = errors.append(this.checkDefaultProductGroup(salespoint));
					errors = errors.append(this.checkPayedInvoice(salespoint));

					final ServiceTracker<ProviderIdService, ProviderIdService> tracker = new ServiceTracker<ProviderIdService, ProviderIdService>(Activator.getDefault().getBundle()
							.getBundleContext(), ProviderIdService.class, null);
					tracker.open();
					final ProviderIdService service = (ProviderIdService) tracker.getService();
					if (service != null)
					{
						final ExternalProductGroupQuery query = (ExternalProductGroupQuery) persistenceService
								.getCacheService().getQuery(ExternalProductGroup.class);
						Collection<ExternalProductGroup> externalProductGroups = query.selectUnmapped(service
								.getProviderId());
						if (externalProductGroups.size() > 0)
						{
							errors = errors.append(errors.length() == 0 ? "" : "\n");
							errors = errors
									.append("- Folgende Warengruppen der Warenbewirtschaftung sind noch nicht gemappt:\n");
							for (ExternalProductGroup externalProductGroup : externalProductGroups)
							{
								errors = errors.append("-- " + externalProductGroup.getCode() + " "
										+ externalProductGroup.getText() + "\n");
							}
							errors = errors.append(errors.length() == 0 ? "" : "\n");
						}
					}
				}
			}
		}
		if (errors.toString().isEmpty())
		{
			this.createControl(parent);
		}
		else
		{
			this.createErrorControl(parent, errors.toString());
		}
	}

	@Override
	public void dispose()
	{
		this.eventHandlerServiceRegistration.unregister();
		this.providerServiceTracker.close();
		this.persistenceServiceTracker.close();
		this.eventServiceTracker.close();

		EntityMediator.removeListener(Salespoint.class, this.mainTabbedPane);
		super.dispose();
	}

	public boolean fireShutdownEvent(final ShutdownEvent e)
	{
		for (final ShutdownListener shutdownListener : this.shutdownListeners)
		{
			if (!shutdownListener.shutdown(e))
			{
				return false;
			}
		}

		return true;
	}

	public ReceiptChangeMediator getReceiptChangeMediator()
	{
		return this.receiptChangeMediator;
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals("ch/eugster/colibri/print/error"))
		{
			final IStatus status = (IStatus) event.getProperty("status");
			final String message = status.getMessage() == null ? "Der Belegdrucker kann nicht angesprochen werden"
					: status.getMessage();
			MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.mainTabbedPane.getSalespoint()
					.getProfile(), "Problem mit Belegdrucker", message, MessageDialog.TYPE_ERROR);
		}
		else if (event.getTopic().equals("ch/eugster/colibri/display/error"))
		{
			final IStatus status = (IStatus) event.getProperty("status");
			final String message = status.getMessage() == null ? "Das Kundendisplay kann nicht angesprochen werden"
					: status.getMessage();
			MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.mainTabbedPane.getSalespoint()
					.getProfile(), "Problem mit Kundendisplay", message, MessageDialog.TYPE_ERROR);
		}
		else
		{
			if (this.transferInformation != null)
			{
				final PersistenceService persistenceService = (PersistenceService) ClientView.this.persistenceServiceTracker
						.getService();
				if (persistenceService != null)
				{
					if (!persistenceService.getServerService().isLocal())
					{
						if (event.getTopic().equals("ch/eugster/colibri/client/store/receipt"))
						{
							this.updateTransferMessage(event);
						}
						else if (event.getTopic().equals("ch/eugster/colibri/persistence/server/database"))
						{
							this.updateTransferMessage(event);
						}
					}
				}
			}
			if (this.providerInformation != null)
			{
				if (this.providerServiceTracker.getService() != null)
				{
					if (event.getTopic().equals("ch/eugster/colibri/client/store/receipt"))
					{
						this.updateProviderMessage(event);
					}
					else if (event.getTopic().equals(ProviderInterface.Topic.ARTICLE_UPDATE.topic()))
					{
						this.updateProviderMessage(event);
					}
					else if (event.getTopic().equals(ProviderInterface.Topic.PROVIDER_TAX_NOT_SPECIFIED.topic()))
					{
						this.updateProviderMessage(event);
					}
					else if (event.getTopic().equals(ProviderInterface.Topic.PROVIDER_FAILOVER.topic()))
					{
						this.updateProviderMessage(event);
					}
				}
			}
		}
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);

		ClientView.instance = this;

		final Collection<String> t = new ArrayList<String>();
		t.add(ProviderInterface.Topic.ARTICLE_UPDATE.topic());
		t.add(ProviderInterface.Topic.PROVIDER_TAX_NOT_SPECIFIED.topic());
		t.add(ProviderInterface.Topic.PROVIDER_FAILOVER.topic());
		t.add("ch/eugster/colibri/client/store/receipt");
		t.add("ch/eugster/colibri/persistence/server/database");
		t.add("ch/eugster/colibri/print/error");
		t.add("ch/eugster/colibri/display/error");
		final String[] topics = t.toArray(new String[t.size()]);

		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.providerServiceTracker = new ServiceTracker<ProviderInterface, ProviderInterface>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderInterface.class, null);
		this.providerServiceTracker.open();

		this.eventServiceTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventServiceTracker.open();
	}

	@Override
	public void postShutdown(final IWorkbench workbench)
	{
	}

	@Override
	public boolean preShutdown(final IWorkbench workbench, final boolean force)
	{
		boolean shutdown = true;
		final ShutdownListener[] shutdownListeners = this.shutdownListeners.toArray(new ShutdownListener[0]);
		for (final ShutdownListener listener : shutdownListeners)
		{
			if (!listener.shutdown(new ShutdownEvent()))
			{
				shutdown = false;
			}
		}

		return shutdown;
	}

	@Override
	public void propertyChange(final PropertyChangeEvent event)
	{
		this.updateCustomerLabel(event.getNewValue());
	}

	public boolean removeShutdownListener(final ShutdownListener listener)
	{
		return this.shutdownListeners.remove(listener);
	}

	@Override
	public void setFocus()
	{
	}

	public void setReceiptChangeMediator(final ReceiptChangeMediator receiptChangeMediator)
	{
		this.receiptChangeMediator = receiptChangeMediator;
	}

	@Override
	public void stateChanged(final ChangeEvent e)
	{
	}

	public void updateProviderMessage(final Event event)
	{
		final UIJob uiJob = new UIJob("Aktualisiere Meldung...")
		{
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor)
			{
				ProviderInterface.Topic topic = (ProviderInterface.Topic) event.getProperty("topic");
				IStatus status = (IStatus) event.getProperty("status");
				if (status.getMessage().equals("ch/eugster/colibri/client/store/receipt"))
				{
					System.out.println();
				}
				final ImageRegistry registry = Activator.getDefault().getImageRegistry();
				if (status.getSeverity() == IStatus.ERROR)
				{
					ClientView.this.providerInformation.setImage(null);
					ClientView.this.providerInformation.setText(null);
					ClientView.this.providerInformation.setErrorText(topic.message());
					ClientView.this.providerInformation.setErrorImage(registry.get("error"));
				}
				else
				{
					final PersistenceService persistenceService = (PersistenceService) ClientView.this.persistenceServiceTracker
								.getService();
					if (persistenceService != null)
					{
						final PositionQuery query = (PositionQuery) persistenceService.getCacheService().getQuery(
								Position.class);
						final long count = query.countProviderUpdates();
						if (topic == null || topic.equals(ProviderInterface.Topic.ARTICLE_UPDATE))
						{
							if ((status == null) || (status.getSeverity() == IStatus.OK))
							{
								if (count > 0)
								{
									status = new Status(count == 0L ? IStatus.OK : IStatus.WARNING, Activator.PLUGIN_ID,
											"Zu verbuchen: " + count);
								}
								if (status.getSeverity() == IStatus.OK)
								{
									ClientView.this.providerInformation.setErrorImage(null);
									ClientView.this.providerInformation.setErrorText(null);
									ClientView.this.providerInformation.setText("Zu verbuchen: 0");
									ClientView.this.providerInformation.setImage(registry.get("ok"));
								}
								else if (status.getSeverity() == IStatus.WARNING)
								{
									ClientView.this.providerInformation.setErrorImage(null);
									ClientView.this.providerInformation.setErrorText(null);
									ClientView.this.providerInformation.setText(status.getMessage());
									ClientView.this.providerInformation.setImage(registry.get("exclamation"));
								}
							}
						}
						else
						{
							ClientView.this.providerInformation.setImage(null);
							ClientView.this.providerInformation.setText(null);
							ClientView.this.providerInformation.setErrorText(status.getMessage());
							ClientView.this.providerInformation.setErrorImage(registry.get("error"));
						}
					}
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	private String checkReferenceCurrency(Salespoint salespoint)
	{
		String msg = "Es wurde noch keine Referenzwährung definiert\n";
		return salespoint.getCommonSettings().getReferenceCurrency() == null ? msg : "";
	}

	private String checkPayedInvoice(Salespoint salespoint)
	{
		String msg = "Es wurde noch keine Warengruppe für das Bezahlen von Rechnungen definiert.\n";
		return salespoint.getCommonSettings().getPayedInvoice() == null ? msg : "";
	}

	private String checkDefaultProductGroup(Salespoint salespoint)
	{
		String msg = "Es wurde noch keine Default-Warengruppe definiert\n";
		return salespoint.getCommonSettings().getDefaultProductGroup() == null ? msg : "";
	}

	private void createControl(final Composite parent)
	{
		final Composite composite = new Composite(parent, SWT.EMBEDDED);
		final Frame frame = SWT_AWT.new_Frame(composite);

		Activator.getDefault().setFrame(frame);

		final Panel panel = this.createRootPanel();
		frame.add(panel);

		this.mainTabbedPane = new MainTabbedPane(this);

		panel.add(this.mainTabbedPane, BorderLayout.CENTER);

		Activator.getDefault().getFrame().add(panel);
		Activator.getDefault().getFrame().pack();
		Activator.getDefault().getFrame().setFocusTraversalKeysEnabled(false);

		// configureFocusPolicy();

		this.addShutdownListener(this.mainTabbedPane);

		this.mainTabbedPane.initFocus();

		PlatformUI.getWorkbench().addWorkbenchListener(this);

		EntityMediator.addListener(Salespoint.class, this.mainTabbedPane);

		this.customerInformation = new StatusLineContributionItem("customer.information", true, 48);
		// this.customerInformation = new
		// StatusLineContributionItem("customer.information", 48);
		this.customerInformation.setText("Kunde: ");
		this.getViewSite().getActionBars().getStatusLineManager().add(this.customerInformation);

		String count = "?";
		Image image = null;

		// this.providerInformation = new
		// StatusLineContributionItem("provider.information", 32);
		this.providerInformation = new StatusLineContributionItem("provider.information", true, 32);
		this.providerInformation.setErrorText("");
		PersistenceService persistenceService = (PersistenceService) ClientView.this.persistenceServiceTracker
				.getService();
		if (persistenceService != null)
		{
			ProviderInterface provider = (ProviderInterface) this.providerServiceTracker.getService();
			if (provider != null)
			{
				final PositionQuery query = (PositionQuery) persistenceService.getCacheService().getQuery(
						Position.class);
				final long counted = query.countProviderUpdates();
				count = Long.valueOf(counted).toString();
				image = Activator.getDefault().getImageRegistry().get(counted == 0L ? "ok" : "exclamation");
				this.providerInformation.setText(provider.getName() + ": " + count);
				this.providerInformation.setImage(image);
			}
		}
		this.getViewSite().getActionBars().getStatusLineManager().add(this.providerInformation);

		this.transferInformation = new StatusLineContributionItem("transfer.information", true, 36);
		this.transferInformation.setErrorText("");
		// this.transferInformation = new
		// StatusLineContributionItem("transfer.information", 36);
		persistenceService = (PersistenceService) ClientView.this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			if (!persistenceService.getServerService().isLocal())
			{
				final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
				final long counted = query.countRemainingToTransfer();
				count = Long.valueOf(counted).toString();
				image = Activator.getDefault().getImageRegistry().get(counted == 0L ? "ok" : "exclamation");
				this.transferInformation.setText("Zu übertragen: " + count);
				this.transferInformation.setImage(image);
			}
		}
		this.getViewSite().getActionBars().getStatusLineManager().add(this.transferInformation);
		this.sendEvent("ch/eugster/colibri/client/started");
	}

	private void createErrorControl(final Composite parent, final String messages)
	{
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setBackground(this.getViewSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		composite.setLayout(new GridLayout());
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;

		Label label = new Label(composite, SWT.None | SWT.CENTER);
		label.setBackground(this.getViewSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setLayoutData(gridData);

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;

		label = new Label(composite, SWT.None | SWT.LEFT);
		label.setBackground(this.getViewSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setLayoutData(gridData);
		label.setText(messages);

		gridData = new GridData();
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;

		Button quit = new Button(composite, SWT.CENTER | SWT.PUSH);
		quit.setLayoutData(gridData);
		quit.setText("Beenden");
		quit.addSelectionListener(new SelectionListener()
		{
			@Override
			public void widgetSelected(SelectionEvent e)
			{
				final UIJob uiJob = new UIJob("Programm wird beendet.")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().close();
						return Status.OK_STATUS;
					}
				};
				uiJob.schedule();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});

		gridData = new GridData(GridData.FILL_BOTH);
		gridData.horizontalAlignment = SWT.CENTER;
		gridData.verticalAlignment = SWT.CENTER;

		label = new Label(composite, SWT.None | SWT.CENTER);
		label.setBackground(this.getViewSite().getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE));
		label.setLayoutData(gridData);
	}

	@SuppressWarnings("serial")
	private Panel createRootPanel()
	{
		final Panel panel = new Panel(new BorderLayout())
		{
			@Override
			public void update(final java.awt.Graphics g)
			{
				this.paint(g);
			}

		};

		final JRootPane root = new JRootPane();
		panel.add(root);

		return panel;
	}

	private void updateCustomerLabel(final Object object)
	{
		if (this.customerInformation != null)
		{
			final UIJob uiJob = new UIJob("set provider message")
			{
				@Override
				public IStatus runInUIThread(final IProgressMonitor monitor)
				{
					StringBuilder builder = new StringBuilder("Kunde: ");
					Customer customer = null;
					if (object instanceof Receipt)
					{
						customer = ((Receipt) object).getCustomer();
					}
					else if (object instanceof Customer)
					{
						customer = (Customer) object;
					}
					else if (object instanceof String)
					{
						builder = builder.append((String) object);
					}
					if (customer != null)
					{
						builder = builder.append(customer.getId().toString());
						builder = builder.append(" ");
						if (!customer.getFullname().isEmpty())
						{
							builder = builder.append(customer.getFullname());
						}
						else
						{
							builder = builder.append("?");
						}
						if (customer.getDiscount() != 0D)
						{
							builder = builder.append(" (Rabatt "
									+ NumberFormat.getPercentInstance().format(customer.getDiscount()) + ")");
						}
						builder = builder.append(", Kontostand: ");
						final NumberFormat formatter = NumberFormat.getNumberInstance();
						formatter.setMaximumFractionDigits(2);
						formatter.setMinimumFractionDigits(2);
						final String amount = formatter.format(customer.getAccount());
						builder = builder.append(amount);
					}

					ClientView.this.customerInformation.setText(builder.toString());
					return Status.OK_STATUS;
				}
			};
			uiJob.schedule();
		}
	}

	private void updateTransferMessage(final Event event)
	{
		final UIJob uiJob = new UIJob("Aktualisiere Meldung...")
		{
			@Override
			public IStatus runInUIThread(final IProgressMonitor monitor)
			{
				final PersistenceService persistenceService = (PersistenceService) ClientView.this.persistenceServiceTracker
						.getService();
				if (persistenceService != null)
				{
					final ReceiptQuery query = (ReceiptQuery) persistenceService.getCacheService().getQuery(
							Receipt.class);
					final long count = query.countRemainingToTransfer();
					IStatus status = (IStatus) event.getProperty("status");
					if ((status == null) || (status.getSeverity() == IStatus.OK))
					{
						status = new Status(count == 0L ? IStatus.OK : IStatus.WARNING,
								(String) event.getProperty(EventConstants.BUNDLE_SYMBOLICNAME), "Zu übertragen: "
										+ count);
					}
					else
					{
						status = new Status(IStatus.ERROR, status.getPlugin(), "Keine Verbindung");
					}
					final ImageRegistry registry = Activator.getDefault().getImageRegistry();
					if (status.getSeverity() == IStatus.OK)
					{
						ClientView.this.transferInformation.setErrorImage(null);
						ClientView.this.transferInformation.setErrorText(null);
						ClientView.this.transferInformation.setText(status.getMessage());
						ClientView.this.transferInformation.setImage(registry.get("ok"));
					}
					else if (status.getSeverity() == IStatus.WARNING)
					{
						ClientView.this.transferInformation.setErrorImage(null);
						ClientView.this.transferInformation.setErrorText(null);
						ClientView.this.transferInformation.setText(status.getMessage());
						ClientView.this.transferInformation.setImage(registry.get("exclamation"));
					}
					else if (status.getSeverity() == IStatus.ERROR)
					{
						ClientView.this.transferInformation.setImage(null);
						ClientView.this.transferInformation.setText(null);
						ClientView.this.transferInformation.setErrorText(status.getMessage());
						ClientView.this.transferInformation.setErrorImage(registry.get("error"));
					}
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	public static ClientView getClientView()
	{
		return ClientView.instance;
	}

	private Event getEvent(final String topics)
	{
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.BUNDLE, Activator.getDefault().getBundle().getBundleContext().getBundle());
		properties.put(EventConstants.BUNDLE_ID,
				Long.valueOf(Activator.getDefault().getBundle().getBundleContext().getBundle().getBundleId()));
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.PLUGIN_ID);
		properties.put(EventConstants.SERVICE, this.eventServiceTracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID,
				this.eventServiceTracker.getServiceReference().getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(Calendar.getInstance().getTimeInMillis()));
		properties.put("status", Status.OK_STATUS);
		return new Event(topics, properties);
	}

	private void sendEvent(String topics)
	{
		final EventAdmin eventAdmin = (EventAdmin) this.eventServiceTracker.getService();
		if (eventAdmin != null)
		{
			eventAdmin.sendEvent(this.getEvent(topics));
		}
	}

}
