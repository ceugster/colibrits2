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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JRootPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.awt.SWT_AWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.ui.texteditor.StatusLineContributionItem;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.osgi.service.log.LogService;
import org.osgi.util.tracker.ServiceTracker;

import ch.eugster.colibri.client.ui.Activator;
import ch.eugster.colibri.client.ui.dialogs.MessageDialog;
import ch.eugster.colibri.client.ui.events.ReceiptChangeMediator;
import ch.eugster.colibri.client.ui.events.ShutdownEvent;
import ch.eugster.colibri.client.ui.events.ShutdownListener;
import ch.eugster.colibri.client.ui.panels.MainTabbedPane;
import ch.eugster.colibri.persistence.events.EntityMediator;
import ch.eugster.colibri.persistence.events.Topic;
import ch.eugster.colibri.persistence.model.CommonSettings;
import ch.eugster.colibri.persistence.model.ExternalProductGroup;
import ch.eugster.colibri.persistence.model.Payment;
import ch.eugster.colibri.persistence.model.PaymentType;
import ch.eugster.colibri.persistence.model.Position;
import ch.eugster.colibri.persistence.model.ProductGroup;
import ch.eugster.colibri.persistence.model.ProviderProperty;
import ch.eugster.colibri.persistence.model.Receipt;
import ch.eugster.colibri.persistence.model.Salespoint;
import ch.eugster.colibri.persistence.model.Settlement;
import ch.eugster.colibri.persistence.model.product.Customer;
import ch.eugster.colibri.persistence.queries.CommonSettingsQuery;
import ch.eugster.colibri.persistence.queries.ExternalProductGroupQuery;
import ch.eugster.colibri.persistence.queries.PaymentQuery;
import ch.eugster.colibri.persistence.queries.PaymentTypeQuery;
import ch.eugster.colibri.persistence.queries.PositionQuery;
import ch.eugster.colibri.persistence.queries.ProductGroupQuery;
import ch.eugster.colibri.persistence.queries.ProviderPropertyQuery;
import ch.eugster.colibri.persistence.queries.ReceiptQuery;
import ch.eugster.colibri.persistence.queries.SalespointQuery;
import ch.eugster.colibri.persistence.queries.SettlementQuery;
import ch.eugster.colibri.persistence.service.PersistenceService;
import ch.eugster.colibri.provider.configuration.IProperty;
import ch.eugster.colibri.provider.service.ProviderIdService;
import ch.eugster.colibri.provider.service.ProviderQuery;
import ch.eugster.colibri.scheduler.service.UpdateScheduler;

public class ClientView extends ViewPart implements IWorkbenchListener, PropertyChangeListener, ChangeListener,
		EventHandler
{
	public static final String ID = "ch.eugster.colibri.client.view";

	private static ClientView instance;

	private MainTabbedPane mainTabbedPane;

	private ServiceTracker<PersistenceService, PersistenceService> persistenceServiceTracker;

	private ServiceTracker<ProviderQuery, ProviderQuery> providerQueryTracker;

	private ServiceTracker<EventAdmin, EventAdmin> eventServiceTracker;

	private ServiceRegistration<EventHandler> eventHandlerServiceRegistration;

//	private StatusLineContributionItem customerInformation;

	private StatusLineContributionItem timeInformation;

	private Timer timer;
	
	private StatusLineContributionItem providerInformation;

	private StatusLineContributionItem transferInformation;

	private ReceiptChangeMediator receiptChangeMediator;

	private Salespoint salespoint;
	
	private long lastFailoverMessage = 0l;
	
	private long lastWarnMessage = 0l;
	
	private Long frequency = null;
	
	private final Collection<ShutdownListener> shutdownListeners = new ArrayList<ShutdownListener>();

	public boolean addShutdownListener(final ShutdownListener listener)
	{
		return this.shutdownListeners.add(listener);
	}
	
	public Salespoint getSalespoint()
	{
		return this.salespoint;
	}

	public void updateSalespoint(Salespoint salespoint)
	{
		this.salespoint = salespoint;
	}
	
	@Override
	public void createPartControl(final Composite parent)
	{
		this.getViewSite().getWorkbenchWindow().getShell().addListener (SWT.Resize,  new Listener () 
		{
			@Override
			public void handleEvent(org.eclipse.swt.widgets.Event event) 
			{
				Rectangle rect = ClientView.this.getViewSite().getWorkbenchWindow().getShell().getClientArea ();
				System.out.println(rect);
			}
		});

		StringBuilder errors = new StringBuilder();
		final PersistenceService persistenceService = (PersistenceService) this.persistenceServiceTracker.getService();
		String msg = "Die Verbindung zur lokalen Datenbank kann nicht hergestellt werden.";
		errors = errors.append(persistenceService == null ? msg : "");
		if (persistenceService != null)
		{
			final SalespointQuery salespointQuery = (SalespointQuery) persistenceService.getCacheService().getQuery(
					Salespoint.class);
			salespoint = salespointQuery.getCurrentSalespoint();
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
					errors = errors.append(this.checkProviderTaxMapped());
					errors = errors.append(this.checkExport(salespoint));
					errors = errors.append(this.checkEBooks(salespoint));

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
		if (errors.toString().trim().isEmpty())
		{
			this.createControl(parent);
		}
		else
		{
			this.createErrorControl(parent, errors.toString());
		}
	}

	private String checkExport(Salespoint salespoint) 
	{
		StringBuilder msg = new StringBuilder();
		Bundle[] bundles = Activator.getDefault().getBundle().getBundleContext().getBundles();
		for (Bundle bundle : bundles)
		{
			if (bundle.getSymbolicName().equals("ch.eugster.colibri.export"))
			{
				if (salespoint.isExport())
				{
					if (salespoint.getMapping() == null || salespoint.getMapping().isEmpty())
					{
						msg = msg.append("Es wurde noch keine ExportId für die aktuelle Kasse definiert\n");
					}
					PersistenceService service = persistenceServiceTracker.getService();
					ProductGroupQuery productGroupQuery = (ProductGroupQuery) service.getCacheService().getQuery(ProductGroup.class);
					long count = productGroupQuery.countWithoutMapping();
					if (count > 0L)
					{
						msg = msg.append("Für " + count + " Warengruppe/n sind noch keine ExportId's definiert\n");
					}
					PaymentTypeQuery paymentTypeQuery = (PaymentTypeQuery) service.getCacheService().getQuery(PaymentType.class);
					count = paymentTypeQuery.countWithoutMapping();
					if (count > 0L)
					{
						msg = msg.append("Für " + count + " Zahlungsarten/n sind noch keine ExportId's definiert\n");
					}
				}
			}
		}
		return msg.toString();
	}

	@Override
	public void dispose()
	{
		if (this.timer != null)
		{
			this.timer.cancel();
		}
		this.eventHandlerServiceRegistration.unregister();
		this.providerQueryTracker.close();
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

	private String getMessageFrequency()
	{
		Map<String, IProperty> properties = UpdateScheduler.SchedulerProperty.asMap();
		PersistenceService persistenceService = persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			ProviderPropertyQuery query = (ProviderPropertyQuery) persistenceService.getCacheService().getQuery(ProviderProperty.class);
			Collection<ProviderProperty> providerProperties = query.selectByProvider(UpdateScheduler.SchedulerProperty.SCHEDULER_COUNT.providerId());
			for (ProviderProperty providerProperty : providerProperties)
			{
				IProperty property = properties.get(providerProperty.getKey());
				if (property != null)
				{
					property.setPersistedProperty(providerProperty);
				}
			}
		}
		return properties.get(UpdateScheduler.SchedulerProperty.SCHEDULER_FAILOVER_MESSAGE_FREQUENCY.key()).value();
	}
	
	private void showErrorMessage(Event event)
	{
		if (event.getTopic().equals(Topic.SCHEDULED.topic()) || 
				event.getTopic().equals(Topic.SCHEDULED_PROVIDER_UPDATE.topic()) ||
				event.getTopic().equals(Topic.SCHEDULED_TRANSFER.topic()) ||
				event.getTopic().equals(Topic.PROVIDER_QUERY.topic()))
		{
			Object ex = event.getProperty(EventConstants.EXCEPTION);
			if (ex instanceof Exception)
			{
				Exception e = (Exception) ex;
				Activator.getDefault().log(LogService.LOG_ERROR, "Failover occurred: " + e.getLocalizedMessage());
			}
			if (frequency == null)
			{
				try
				{
					frequency = Long.valueOf(getMessageFrequency()).longValue() * 60000;
				}
				catch(NumberFormatException e)
				{
					frequency = 3600000L;
				}
			}
			Boolean force = (Boolean) event.getProperty("force");
			boolean doForce = force == null ? false : force.booleanValue();
			long now = GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis();
			long diff = now - this.lastFailoverMessage;
			if (doForce || diff > frequency)
			{
				lastFailoverMessage = now;
				final String message = "Die Verbindung zum Server kann zur Zeit nicht hergestellt werden."
						+ "\nTiteleingaben müssen manuell vorgenommen werden." 
						+ "\nAktualisierungen können nicht durchgeführt werden."
						+ "\nStarten Sie den Hauptrechner oder Server neu."
						+ "\n\nFalls das Problem weiterhin besteht, kontaktieren Sie bitte die Hotline von Comelivres AG.";
				if (message != null)
				{
					MessageDialog.showInformation(Activator.getDefault().getFrame(), ClientView.this.mainTabbedPane.getSalespoint()
									.getProfile(), "Verbindungsproblem", message, MessageDialog.TYPE_ERROR);
				}
			}
		}
		else if (event.getTopic().equals(Topic.PRINT_SETTLEMENT.topic()) || event.getTopic().equals(Topic.PRINT_RECEIPT.topic()) || event.getTopic().equals(Topic.PRINT_VOUCHER.topic()))
		{
			final IStatus status = (IStatus) event.getProperty("status");
			final String message = status.getMessage() == null ? "Der Belegdrucker kann nicht angesprochen werden"
					: status.getMessage();
			MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.mainTabbedPane.getSalespoint()
					.getProfile(), "Problem mit Belegdrucker", message, MessageDialog.TYPE_ERROR);
		}
	}

	private void showWarningMessage(Event event)
	{
		if (event.getTopic().equals(Topic.SCHEDULED.topic()) || 
				event.getTopic().equals(Topic.SCHEDULED_PROVIDER_UPDATE.topic()) ||
				event.getTopic().equals(Topic.SCHEDULED_TRANSFER.topic()))
		{
			IStatus status = (IStatus) event.getProperty("status");
			Activator.getDefault().log(LogService.LOG_WARNING, "Failover occurred: " + status.getMessage());

			if (frequency == null)
			{
				try
				{
					frequency = Long.valueOf(getMessageFrequency()).longValue() * 60000;
				}
				catch(NumberFormatException e)
				{
					frequency = 3600000L;
				}
			}
			Boolean force = (Boolean) event.getProperty("force");
			boolean doForce = force == null ? false : force.booleanValue();
			long now = GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis();
			long diff = now - this.lastWarnMessage;
			if (doForce || diff > frequency)
			{
				lastWarnMessage = now;
				final String message = (String) event.getProperty(EventConstants.MESSAGE);
				if (message != null)
				{
					MessageDialog.showInformation(Activator.getDefault().getFrame(), ClientView.this.mainTabbedPane.getSalespoint()
									.getProfile(), "Aktualisierungsproblem", message, MessageDialog.TYPE_WARN);
				}
			}
		}
		else if (event.getTopic().equals(Topic.PRINT_SETTLEMENT.topic()) || event.getTopic().equals(Topic.PRINT_RECEIPT.topic()) || event.getTopic().equals(Topic.PRINT_VOUCHER.topic()))
		{
			final IStatus status = (IStatus) event.getProperty("status");
			final String message = status.getMessage() == null ? "Der Belegdrucker kann nicht angesprochen werden"
					: status.getMessage();
			MessageDialog.showSimpleDialog(Activator.getDefault().getFrame(), this.mainTabbedPane.getSalespoint()
					.getProfile(), "Problem mit Belegdrucker", message, MessageDialog.TYPE_ERROR);
		}
	}

	@Override
	public void handleEvent(final Event event)
	{
		if (event.getTopic().equals(Topic.DATABASE_COMPATIBILITY_ERROR.topic()))
		{
			UIJob job = new UIJob("Datenbankfehler")
			{
				@Override
				public IStatus runInUIThread(IProgressMonitor monitor) 
				{
					org.eclipse.jface.dialogs.MessageDialog.openWarning(ClientView.this.getSite().getShell(), "Datenbankfehler", Topic.DATABASE_COMPATIBILITY_ERROR.error());
					PlatformUI.getWorkbench().close();
					return Status.CANCEL_STATUS;
				}
			};
			job.schedule();
		}
		Object ex = event.getProperty(EventConstants.EXCEPTION);
		if (ex instanceof Exception)
		{
			showErrorMessage(event);
		}
		IStatus status = (IStatus) event.getProperty("status");
		if (status != null && status.getSeverity() == IStatus.WARNING)
		{
			this.showWarningMessage(event);
		}
		this.updateTransferMessage(event);
		this.updateProviderMessage(event);
	}

	@Override
	public void init(final IViewSite site) throws PartInitException
	{
		super.init(site);

		Activator.getDefault().log(LogService.LOG_DEBUG, "Registriere Anwendungs-Instanz.");

		ClientView.instance = this;

		final Collection<String> t = new ArrayList<String>();
		t.add(Topic.SCHEDULED.topic());
		t.add(Topic.SCHEDULED_PROVIDER_UPDATE.topic());
		t.add(Topic.SCHEDULED_TRANSFER.topic());
		t.add(Topic.STORE_RECEIPT.topic());
		t.add(Topic.PRINT_RECEIPT.topic());
		t.add(Topic.PRINT_SETTLEMENT.topic());
		t.add(Topic.PRINT_VOUCHER.topic());
		t.add(Topic.SETTLE_PERFORMED.topic());
		t.add(Topic.PROVIDER_QUERY.topic());
		t.add(Topic.DATABASE_COMPATIBILITY_ERROR.topic());
		final String[] topics = t.toArray(new String[t.size()]);

		final EventHandler eventHandler = this;
		final Dictionary<String, Object> properties = new Hashtable<String, Object>();
		properties.put(EventConstants.EVENT_TOPIC, topics);
		this.eventHandlerServiceRegistration = Activator.getDefault().getBundle().getBundleContext()
				.registerService(EventHandler.class, eventHandler, properties);

		this.persistenceServiceTracker = new ServiceTracker<PersistenceService, PersistenceService>(Activator.getDefault().getBundle().getBundleContext(),
				PersistenceService.class, null);
		this.persistenceServiceTracker.open();

		this.providerQueryTracker = new ServiceTracker<ProviderQuery, ProviderQuery>(Activator.getDefault().getBundle().getBundleContext(),
				ProviderQuery.class, null);
		this.providerQueryTracker.open();

		this.eventServiceTracker = new ServiceTracker<EventAdmin, EventAdmin>(Activator.getDefault().getBundle().getBundleContext(),
				EventAdmin.class, null);
		this.eventServiceTracker.open();

		setWindowMode(site);
	}

	private void setWindowMode(IViewSite site)
	{
		PersistenceService service = this.persistenceServiceTracker.getService();
		if (service != null)
		{
			CommonSettingsQuery query = (CommonSettingsQuery) service.getCacheService().getQuery(CommonSettings.class);
			CommonSettings settings = query.findDefault();
			if (settings != null)
			{
				site.getShell().setMaximized(settings.isMaximizedClientWindow());
			}
		}
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
		if (this.providerInformation != null)
		{
			if (event.getTopic().equals(Topic.SCHEDULED_PROVIDER_UPDATE.topic()) ||
					event.getTopic().equals(Topic.STORE_RECEIPT.topic()) ||
					event.getTopic().equals(Topic.PROVIDER_QUERY.topic()))
			{
				long myCount = 0L;
				if (event.getTopic().equals(Topic.SCHEDULED_PROVIDER_UPDATE.topic()))
				{
					myCount = ((Long) event.getProperty("count")).longValue();
				}
				else
				{
					myCount = this.countProviderUpdates(this.salespoint);
				}
				final long count = myCount;
				final UIJob uiJob = new UIJob("Aktualisiere Meldung...")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						IStatus status = (IStatus) event.getProperty("status");
						final ImageRegistry registry = Activator.getDefault().getImageRegistry();
						if (status.getSeverity() == IStatus.ERROR)
						{
							status = new Status(status.getSeverity(), Activator.getDefault().getBundle().getSymbolicName(),
									"Keine Verbindung!");
							ClientView.this.providerInformation.setImage(null);
							ClientView.this.providerInformation.setText(null);
							ClientView.this.providerInformation.setErrorText(status.getMessage());
							ClientView.this.providerInformation.setErrorImage(registry.get("error"));
						}
						else
						{
							status = new Status(count == 0L ? IStatus.OK : IStatus.WARNING, Activator.getDefault().getBundle().getSymbolicName(),
									"Verbuchen: " + count);
							ClientView.this.providerInformation.setErrorImage(null);
							ClientView.this.providerInformation.setErrorText(null);
							ClientView.this.providerInformation.setText(status.getMessage());
							ClientView.this.providerInformation.setImage(registry.get(Topic.SCHEDULED_PROVIDER_UPDATE.icon(status)));
						}
						return Status.OK_STATUS;
					}
				};
				uiJob.setPriority(Job.DECORATE);
				uiJob.schedule();
			}
		}
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

	private String checkProviderTaxMapped()
	{
		ProviderQuery providerQuery = providerQueryTracker.getService();
		if (providerQuery == null)
		{
			return "";
		}
		else
		{
			return providerQuery.checkTaxCodes(persistenceServiceTracker.getService()).getMessage();
		}
	}

	private String checkEBooks(Salespoint salespoint)
	{
		String msg = "Es wurde noch keine Warengruppe für EBooks definiert\n";
		return salespoint.getCommonSettings().getEBooks() == null ? msg : "";
	}

	private String checkDefaultProductGroup(Salespoint salespoint)
	{
		CommonSettings settings = salespoint.getCommonSettings();
		if (settings.getDefaultProductGroup() == null)
		{
			return "Es wurde noch keine Default-Warengruppe definiert\n";
		}
		else if (settings.getDefaultProductGroup().getProductGroupMappings().size() == 0)
		{
			ServiceReference<ProviderQuery>[] references = providerQueryTracker.getServiceReferences();
			if (references != null && references.length > 0)
			{
				StringBuilder builder = new StringBuilder();
				for (int i = 0; i < references.length; i++)
				{
					ProviderQuery query = providerQueryTracker.getService(references[i]);
					if (query != null)
					{
						builder = builder.append((builder.length() == 0 ? "" : (references.length > 1 && i == references.length - 1 ? " oder " : ", ")) + query.getConfiguration().getName());
					}
				}
				return "Die Default-Warengruppe muss mit einer Warengruppe von " + builder.toString() + " verbunden sein.";
			}
		}
		return "";
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

		long count = this.countTransfers(this.salespoint);
		Image image = Activator.getDefault().getImageRegistry().get(count == 0L ? "ok" : "exclamation");
		this.transferInformation = new StatusLineContributionItem("transfer.information", true, 36);
		this.transferInformation.setText("Zu übertragen: " + count);
		this.transferInformation.setImage(image);
		this.getViewSite().getActionBars().getStatusLineManager().appendToGroup(StatusLineManager.BEGIN_GROUP, this.transferInformation);

		this.providerInformation = new StatusLineContributionItem("provider.information", true, 32);
		this.providerInformation.setErrorText("");
		count = this.countProviderUpdates(this.salespoint);
		image = Activator.getDefault().getImageRegistry().get(count == 0L ? "ok" : "exclamation");
		this.providerInformation.setText("Verbuchen: " + count);
		this.providerInformation.setImage(image);

		this.getViewSite().getActionBars().getStatusLineManager().appendToGroup(StatusLineManager.BEGIN_GROUP, this.providerInformation);

		this.timeInformation = new StatusLineContributionItem("time.information", true, 24);
		this.timeInformation.setText("");
		this.getViewSite().getActionBars().getStatusLineManager().prependToGroup(StatusLineManager.BEGIN_GROUP, this.timeInformation);

		this.sendEvent(Topic.CLIENT_STARTED.topic());
		
		startTimer();
	}
	
	private void startTimer()
	{
		timer = new Timer();
		TimerTask task = new TimerTask() 
		{
			@Override
			public void run() 
			{
				ClientView.this.getSite().getShell().getDisplay().asyncExec(new Runnable() 
				{
					@Override
					public void run() 
					{
						timeInformation.setText(SimpleDateFormat.getDateTimeInstance().format(GregorianCalendar.getInstance(Locale.getDefault()).getTime()));
					}
				});
			}
		};
		timer.scheduleAtFixedRate(task, 0, 1000);
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
					builder = builder.append(customer.getHasAccount() ? " (Kundenkarte: " + customer.getId().toString() + ")" : " (OHNE KUNDENKARTE)");
				}
				String text = builder.toString();
//					ClientView.this.customerInformation.setText(text);
				Shell shell = ClientView.this.getSite().getShell();
				if (shell != null)
				{
					shell.setText(ClientView.this.mainTabbedPane.prepareTitle() + " :: " + text);
				}
				return Status.OK_STATUS;
			}
		};
		uiJob.schedule();
	}

	private void updateTransferMessage(final Event event)
	{
		if (this.transferInformation != null)
		{
			if (event.getTopic().equals(Topic.SCHEDULED_TRANSFER.topic()) ||
					event.getTopic().equals(Topic.STORE_RECEIPT.topic()) ||
							event.getTopic().equals(Topic.SETTLE_PERFORMED.topic()))
			{
				long myCount = 0L;
				if (event.getTopic().equals(Topic.SCHEDULED_TRANSFER.topic()))
				{
					myCount = ((Long) event.getProperty("count")).longValue();
				}
				else
				{
					myCount = this.countTransfers(salespoint);
				}
				final long count = myCount;
				final UIJob uiJob = new UIJob("Aktualisiere Meldung...")
				{
					@Override
					public IStatus runInUIThread(final IProgressMonitor monitor)
					{
						final ImageRegistry registry = Activator.getDefault().getImageRegistry();
						IStatus status = (IStatus) event.getProperty("status");
						if (status.getSeverity() == IStatus.OK)
						{
							if ((status == null) || (status.getSeverity() == IStatus.OK))
							{
								status = new Status(count == 0L ? IStatus.OK : IStatus.WARNING,
										(String) event.getProperty(EventConstants.BUNDLE_SYMBOLICNAME), "Transfer: "
												+ count);
							}
							else
							{
								status = new Status(IStatus.ERROR, status.getPlugin(), "Keine Verbindung");
							}
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
							}
						}
						else
						{
							status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), "Keine Verbindung!");
							ClientView.this.transferInformation.setImage(null);
							ClientView.this.transferInformation.setText(null);
							ClientView.this.transferInformation.setErrorText(status.getMessage());
							ClientView.this.transferInformation.setErrorImage(registry.get(Topic.SCHEDULED_TRANSFER.icon(status)));
						}
						return Status.OK_STATUS;
					}
				};
				uiJob.schedule();
			}
		}
	}

	private long countProviderUpdates(Salespoint salespoint)
	{
		long count = 0L;
		final PersistenceService persistenceService = (PersistenceService) ClientView.this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			ServiceReference<ProviderQuery>[] references = this.providerQueryTracker.getServiceReferences();
			for (ServiceReference<ProviderQuery> reference : references)
			{
				ProviderQuery query = this.providerQueryTracker.getService(reference);
				PositionQuery positionQuery = (PositionQuery) persistenceService.getCacheService().getQuery(Position.class);
				count += positionQuery.countProviderUpdates(salespoint, query.getProviderId(), !persistenceService.getServerService().isLocal());
				PaymentQuery paymentQuery = (PaymentQuery) persistenceService.getCacheService().getQuery(Payment.class);
				count += paymentQuery.countProviderUpdates(salespoint, query.getProviderId());
			}
		}
		return count;
	}
	
	private long countTransfers(Salespoint salespoint)
	{
		long count = 0L;
		final PersistenceService persistenceService = (PersistenceService) ClientView.this.persistenceServiceTracker.getService();
		if (persistenceService != null)
		{
			final ReceiptQuery receiptQuery = (ReceiptQuery) persistenceService.getCacheService().getQuery(Receipt.class);
			count = receiptQuery.countRemainingToTransfer();
			final SettlementQuery settlementQuery = (SettlementQuery) persistenceService.getCacheService().getQuery(Settlement.class);
			count += settlementQuery.countTransferables();
		}
		return count;
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
		properties.put(EventConstants.BUNDLE_SYMBOLICNAME, Activator.getDefault().getBundle().getSymbolicName());
		properties.put(EventConstants.SERVICE, this.eventServiceTracker.getServiceReference());
		properties.put(EventConstants.SERVICE_ID,
				this.eventServiceTracker.getServiceReference().getProperty("service.id"));
		properties.put(EventConstants.TIMESTAMP, Long.valueOf(GregorianCalendar.getInstance(Locale.getDefault()).getTimeInMillis()));
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
